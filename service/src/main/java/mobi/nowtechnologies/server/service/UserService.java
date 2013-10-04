package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType;
import mobi.nowtechnologies.common.util.ServerMessage;
import mobi.nowtechnologies.server.assembler.UserAsm;
import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.dao.*;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.UserBannedRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.FacebookService.UserCredentions;
import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.data.UserDetailsUpdater;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.service.exception.ValidationException;
import mobi.nowtechnologies.server.service.o2.O2Service;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderService;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;
import mobi.nowtechnologies.server.service.o2.impl.O2UserDetailsUpdater;
import mobi.nowtechnologies.server.service.payment.MigPaymentService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.service.util.PaymentDetailsValidator;
import mobi.nowtechnologies.server.service.util.UserRegInfoValidator;
import mobi.nowtechnologies.server.shared.AppConstants;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.UserFacebookDetailsDto;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import mobi.nowtechnologies.server.shared.dto.web.AccountDto;
import mobi.nowtechnologies.server.shared.dto.web.ContentOfferDto;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;
import mobi.nowtechnologies.server.shared.dto.web.UserRegDetailsDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.UnsubscribeDto;
import mobi.nowtechnologies.server.shared.enums.*;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.util.PhoneNumberValidator;
import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;
import static mobi.nowtechnologies.server.assembler.UserAsm.toAccountCheckDTO;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.shared.enums.ActionReason.USER_DOWNGRADED_TARIFF;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ENTERED_NUMBER;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.REGISTERED;
import static mobi.nowtechnologies.server.shared.enums.Contract.*;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.DIRECT;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.INDIRECT;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static mobi.nowtechnologies.server.shared.enums.Tariff._4G;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.*;
import static mobi.nowtechnologies.server.shared.util.DateUtils.newDate;
import static mobi.nowtechnologies.server.shared.util.EmailValidator.*;
import static org.apache.commons.lang.Validate.notNull;

public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    public static final String MULTIPLE_FREE_TRIAL_STOP_DATE = "multiple.free.trial.stop.date";

	private UserDao userDao;

    private EntityService entityService;
    private CountryAppVersionService countryAppVersionService;
    private DeviceTypeService deviceTypeService;
    private CountryService countryService;
	private PaymentService paymentService;

    private PromotionService promotionService;
    private CommunityResourceBundleMessageSource messageSource;
    private PaymentPolicyService paymentPolicyService;
    private PaymentDetailsService paymentDetailsService;
    private MigPaymentService migPaymentService;
    private MigHttpService migHttpService;
    private CountryByIpService countryByIpService;
    private UserDeviceDetailsService userDeviceDetailsService;
    private CommunityService communityService;
    private MailService mailService;
	private FacebookService facebookService;

    private DeviceService deviceService;
    private OfferService offerService;
    private DrmService drmService;
    private AccountLogService accountLogService;
    private UserRepository userRepository;
    private OtacValidationService otacValidationService;
    private ITunesService iTunesService;
    private UserBannedRepository userBannedRepository;
    private RefundService refundService;
    private UserServiceNotification userServiceNotification;
    private static final Pageable PAGEABLE_FOR_WEEKLY_UPDATE = new PageRequest(0, 1000);

    private O2ProviderService o2ClientService;
    private O2Service o2Service;
    private O2UserDetailsUpdater o2UserDetailsUpdater;

    private UserDetailsUpdater userDetailsUpdater;
    private MobileProviderService mobileProviderService;

    private User checkAndMerge(User user, User mobileUser) {
        if (mobileUser.getId() != user.getId()) {
            user = mergeUser(mobileUser, user);
        }
        return user;
    }

    private User updateContractAndProvider(User user, ProviderUserDetails providerUserDetails) {
        user.setContract(Contract.valueOf(providerUserDetails.contract));
        user.setProvider(providerUserDetails.operator);
        return user;
    }

    private boolean applyInitPromo(User user, User mobileUser, String otac, boolean updateContractAndProvider){
        ProviderUserDetails providerUserDetails = otacValidationService.validate(otac, user.getMobile(), user.getUserGroup().getCommunity());

        LOGGER.info("[{}], u.contract=[{}], u.mobile=[{}], u.operator=[{}]", providerUserDetails,
                user.getContract(), user.getMobile(),
                user.getOperator());

        boolean hasPromo = false;
        if (isNotNull(mobileUser)) {
            user = checkAndMerge(user, mobileUser);
        } else if (ENTERED_NUMBER.equals(user.getActivationStatus())  && !isEmail(user.getUserName())) {
            hasPromo = promotionService.applyPotentialPromo(user, o2ClientService.isO2User(providerUserDetails));
        }

        if(updateContractAndProvider || user.isVFNZCommunityUser()) updateContractAndProvider(user, providerUserDetails);

        user.setActivationStatus(ACTIVATED);
        user.setUserName(user.getMobile());
        userRepository.save(user);

        return hasPromo;
    }

    public void setUserDetailsUpdater(UserDetailsUpdater userDetailsUpdater) {
        this.userDetailsUpdater = userDetailsUpdater;
    }

    public void setMobileProviderService(MobileProviderService mobileProviderService) {
        this.mobileProviderService = mobileProviderService;
    }

    public void setO2ClientService(O2ProviderService o2ClientService) {
		this.o2ClientService = o2ClientService;
    }

    public void setOtacValidationService(OtacValidationService otacValidationService) {
		this.otacValidationService = otacValidationService;
	}

	public void setO2Service(O2Service o2Service) {
    	this.o2Service = o2Service;
    }

    public void setUserBannedRepository(UserBannedRepository userBannedRepository) {
        this.userBannedRepository = userBannedRepository;
    }

    public void setDrmService(DrmService drmService) {
		this.drmService = drmService;
	}

	public void setDeviceService(DeviceService deviceService) {
		this.deviceService = deviceService;
	}

	public void setOfferService(OfferService offerService) {
		this.offerService = offerService;
	}

	public void setUserDeviceDetailsService(UserDeviceDetailsService userDeviceDetailsService) {
		this.userDeviceDetailsService = userDeviceDetailsService;
	}

	public void setPaymentDetailsService(PaymentDetailsService paymentDetailsService) {
		this.paymentDetailsService = paymentDetailsService;
	}

	public void setPaymentService(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	public void setDeviceTypeService(DeviceTypeService aDeviceTypeService) {
		this.deviceTypeService = aDeviceTypeService;
	}

	public void setCountryAppVersionService(
			CountryAppVersionService countryAppVersionService) {
		this.countryAppVersionService = countryAppVersionService;
	}

	public void setPromotionService(PromotionService promotionService) {
		this.promotionService = promotionService;
	}

	public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setPaymentPolicyService(
			PaymentPolicyService paymentPolicyService) {
		this.paymentPolicyService = paymentPolicyService;
	}

	public void setMigPaymentService(MigPaymentService migPaymentService) {
		this.migPaymentService = migPaymentService;
	}

	public void setMigHttpService(MigHttpService migHttpService) {
		this.migHttpService = migHttpService;
	}

	public void setCountryByIpService(CountryByIpService countryByIpService) {
		this.countryByIpService = countryByIpService;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	public void setFacebookService(FacebookService facebookService) {
		this.facebookService = facebookService;
	}

	public void setAccountLogService(AccountLogService accountLogService) {
		this.accountLogService = accountLogService;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void setiTunesService(ITunesService iTunesService) {
		this.iTunesService = iTunesService;
	}

    public void setRefundService(RefundService refundService) {
        this.refundService = refundService;
    }

    public void setUserServiceNotification(
			UserServiceNotification userServiceNotification) {
		this.userServiceNotification = userServiceNotification;
	}

    public void setCommunityService(CommunityService communityService) {
        this.communityService = communityService;
    }

    public void setO2UserDetailsUpdater(O2UserDetailsUpdater o2UserDetailsUpdater) {
        this.o2UserDetailsUpdater = o2UserDetailsUpdater;
    }

    public Boolean canActivateVideoTrial(User u) {
        if (u.isOnWhiteListedVideoAudioFreeTrial()) return false;
        String rewriteUrlParameter = u.getUserGroup().getCommunity().getRewriteUrlParameter();
        Date multipleFreeTrialsStopDate = messageSource.readDate(rewriteUrlParameter, MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014));

        if(u.is4G() && u.isO2PAYGConsumer() && !u.isVideoFreeTrialHasBeenActivated()) return true;
        if(u.is4G() && u.isO2PAYMConsumer() && INDIRECT.equals(u.getContractChannel()) && !u.isVideoFreeTrialHasBeenActivated()) return true;

        boolean beforeMultipleFreeTrialsStopDate = new DateTime().isBefore(multipleFreeTrialsStopDate.getTime());
        if(u.is4G() && u.isO2PAYMConsumer() && !u.isOnVideoAudioFreeTrial() && (DIRECT.equals(u.getContractChannel()) || isNull(u.getContractChannel())) && !u.has4GVideoAudioSubscription() && beforeMultipleFreeTrialsStopDate) return true;
        if(u.is4G() && u.isO2PAYMConsumer() && !u.isVideoFreeTrialHasBeenActivated() && !beforeMultipleFreeTrialsStopDate) return true;
        return  false;
    }

    @Deprecated
	public User checkCredentials(String userName, String userToken, String timestamp, String communityName) {
		notNull(userName, "The parameter userName is null");
		notNull(userToken, "The parameter userToken is null");
		notNull(timestamp, "The parameter timestamp is null");
		User user = findByNameAndCommunity(userName, communityName);

		if (user != null) {
			final String mobile = user.getMobile();
			final int id = user.getId();
			LogUtils.putSpecificMDC(userName, mobile, id);
			LogUtils.put3rdParyRequestProfileSpecificMDC(userName, mobile, id);

			String localUserToken = Utils.createTimestampToken(user.getToken(), timestamp);
			String deviceUserToken = Utils.createTimestampToken(user.getTempToken(), timestamp);
			if (localUserToken.equalsIgnoreCase(userToken) || deviceUserToken.equalsIgnoreCase(userToken)) {
				PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
				if (null == currentPaymentDetails && user.getStatus().getI() == UserStatusDao.getEulaUserStatus().getI())
					LOGGER.info("The user [{}] couldn't login in while he has no payment details and he is in status [{}]",
							new Object[] { user, UserStatus.EULA.name() });
				else
					return user;
			} else
				LOGGER.info("Invalid user token. Expected {} but received {}", localUserToken, user.getToken());
		} else {
			String message = "Could not find user with userName [" + userName + "] and communityName [" + communityName + "] in the database";
			LOGGER.info(message);

			ServerMessage serverMessage = ServerMessage.getMessageOnUnExistUser(userName, communityName);
			throw new UserCredentialsException(serverMessage);
		}

		ServerMessage serverMessage = ServerMessage.getInvalidPassedStoredToken(userName, communityName);
		throw new UserCredentialsException(serverMessage);
	}

	@Deprecated
	public User checkCredentials(String userName, String userToken, String timestamp, String communityName, String deviceUID) {
		LOGGER.debug("input parameters userName, userToken, timestamp, communityName, deviceUID: [{}], [{}], [{}], [{}], [{}]", new Object[] { userName, userToken, timestamp, communityName,
				deviceUID });
		User user = checkCredentials(userName, userToken, timestamp, communityName);
		final String foundDeviceUID = user.getDeviceUID();
		if (deviceUID != null && foundDeviceUID != null && !deviceUID.equalsIgnoreCase(foundDeviceUID)) {//return user info only if foundDeviceUID is null or deviceUID and foundDeviceUID are equals
			Community community = communityService.getCommunityByName(communityName);
			final String communityURL;
			if (community != null) {
				communityURL = community.getRewriteUrlParameter();
			} else {
				communityURL = "unknown community for communityName [" + communityName + "]";
			}
			ServerMessage serverMessage = ServerMessage.getInvalidPassedStoredTokenForDeviceUID(deviceUID, communityURL);
			throw new UserCredentialsException(serverMessage);
		}
		LOGGER.info("Output parameter user=[{}]", user);
		return user;
	}

    public UserBanned getUserBanned(Integer userId) {
         return userBannedRepository.findOne(userId);
    }

	@Deprecated
	public boolean userExists(String userName, String communityName) {
		return userDao.userExists(userName, communityName);
	}

	public User findByName(String userName) {
		if (userName == null)
			throw new ServiceException("The parameter userName is null");
		return entityService.findByProperty(User.class, User.Fields.userName.toString(), userName);
	}

	public User findByFacebookId(String facebookId, String communityName) {
		LOGGER.debug("input parameters facebookId, communityName: [{}], [{}]", facebookId, communityName);
		if (facebookId == null)
			throw new ServiceException("The parameter facebookId is null");
		final User user = userDao.findByFacebookAndCommunity(facebookId, communityName);
		LOGGER.info("Output parameter user=[{}]", user);
		return user;
	}

	public Object[] processSetPasswordCommand(int userId, String token, String communityName) {
		if (null == token)
			throw new ServiceException("The parameter token is null");
		if (communityName == null)
			throw new ServiceException("The parameter communityName is null");

		SetPassword setPassword = new SetPassword();
		try {
			User user = entityService.findById(User.class, userId);
			user.setToken(token);
			entityService.updateEntity(user);
			setPassword.setStatus(SetPassword.Status.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			setPassword.setStatus(SetPassword.Status.FAIL);
		}
		AccountCheckDTO accountCheck = proceessAccountCheckCommandForAuthorizedUser(userId, null, null, null);
		return new Object[] { accountCheck, setPassword };
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User registerUserWhitoutPersonalInfo(UserRegInfo userRegInfo) {
		if (userRegInfo == null)
			throw new ServiceException("The parameter userRegInfo is null");

		String countryFullName = userRegInfo.getCountryFullName();

		if (userRegInfo.getTitle() == null)
			userRegInfo.setTitle("");
		if (userRegInfo.getFirstName() == null)
			userRegInfo.setFirstName("");
		if (userRegInfo.getLastName() == null)
			userRegInfo.setLastName("");
		if (countryFullName == null || countryFullName.isEmpty())
			userRegInfo.setCountryFullName("Great Britain");
		if (userRegInfo.getCity() == null)
			userRegInfo.setCity("");
		if (userRegInfo.getAddress() == null)
			userRegInfo.setAddress("");
		if (userRegInfo.getPostCode() == null)
			userRegInfo.setPostCode("");
		if (userRegInfo.getNewsByEmail() == null)
			userRegInfo.setNewsByEmail(Boolean.FALSE);
		if (userRegInfo.getPhoneNumber() == null) {
			userRegInfo.setPhoneNumber("");
		} else {
			userRegInfo.setPhoneNumber(convertPhoneNumberFromInternationalToGreatBritainFormat(userRegInfo.getPhoneNumber()));
		}

		UserRegInfoValidator.validateWhitoutPersonalInfo(userRegInfo);

		final String ipAddress = userRegInfo.getIpAddress();
		userRegInfo.setCountryCodeByIpAddress(findCountryCodeByIp(ipAddress));

		validateCountry(userRegInfo.getAppVersion(), userRegInfo.getCountryCodeByIpAddress());

		User user = continueRegistration(userRegInfo);
		LOGGER.debug("Output parameter user=[{}]", user);
		assignPotentialPromotion(user);
		return user;
	}

	private String findCountryCodeByIp(String ipAddress) {
		LOGGER.debug("input parameters ipAddress: [{}]", ipAddress);

		String countryName;
		if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress))
			countryName = "GB";
		else
			countryName = countryByIpService.findCountryCodeByIp(ipAddress);

		LOGGER.debug("Output parameter countryName=[{}]", countryName);
		return countryName;
	}

	public String getMigPhoneNumber(int operator, String mobile) {
		return Operator.getMapAsIds().get(operator).getMigName() + "." + mobile;
	}

	public String convertPhoneNumberFromGreatBritainToInternationalFormat(String mobile) {
		if (mobile == null)
			throw new ServiceException("The parameter mobile is null");
		if (!mobile.startsWith("0044"))
			return mobile.replaceFirst("0", "0044");
		return mobile;
	}

	// TODO remove this method and it's usage. This is only for GB partners only
	// for Now Top 40
	public static String convertPhoneNumberFromInternationalToGreatBritainFormat(String mobile) {
		if (mobile == null)
			throw new ServiceException("The parameter mobile is null");
		return mobile.replaceFirst("0044", "0");
	}

	@Transactional(readOnly=true)
	public User getUserWithSelectedCharts(Integer userId){
		User user = userRepository.findOne(userId);
		user.getSelectedCharts().size();

		return user;
	}

	public User findByNameAndCommunity(String userName, String communityName) {
		LOGGER.debug("input parameters userName, communityName: [{}], [{}]", userName, communityName);
		User user = userDao.findByNameAndCommunity(userName, communityName);
		LOGGER.debug("Output parameter user=[{}]", user);
		return user;
	}

	public boolean applyO2PotentialPromo(ProviderUserDetails providerUserDetails, User user, Community community) {

        boolean isO2User = o2ClientService.isO2User(providerUserDetails);

        return applyPotentialPromo(isO2User, user, community);
	}

    public boolean applyPotentialPromo(boolean isO2User, User user, Community community) {
        int freeTrialStartedTimestampSeconds = Utils.getEpochSeconds();
        return applyPotentialPromo(isO2User, user, community, freeTrialStartedTimestampSeconds);
    }

    public boolean applyPotentialPromo(boolean isO2User, User user, Community community, int freeTrialStartedTimestampSeconds) {
        Promotion promotion;

        String staffCode = messageSource.getMessage(community.getRewriteUrlParameter(), "o2.staff.promotionCode", null, null);
        String storeCode = messageSource.getMessage(community.getRewriteUrlParameter(), "o2.store.promotionCode", null, null);

        if (deviceService.isPromotedDevicePhone(community, user.getMobile(), staffCode))
            promotion = setPotentialPromo(community, user, staffCode);
        else if (deviceService.isPromotedDevicePhone(community, user.getMobile(), storeCode))
            promotion = setPotentialPromo(community, user, storeCode);
        else if (isO2User || user.isVFNZUser())
            promotion = setPotentialPromo(community.getName(), user, "promotionCode");
        else
            promotion = setPotentialPromo(community.getName(), user, "defaultPromotionCode");

        return applyPromotionByPromoCode(user, promotion, freeTrialStartedTimestampSeconds);
    }

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean applyPromotionByPromoCode(User user, Promotion promotion) {
        int freeTrialStartedTimestampSeconds = Utils.getEpochSeconds();
        return applyPromotionByPromoCode(user, promotion, freeTrialStartedTimestampSeconds);
	}

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean applyPromotionByPromoCode(User user, Promotion promotion, int freeTrialStartedTimestampSeconds) {
        LOGGER.debug("input parameters user, promotion, freeTrialStartedTimestampSeconds: [{}], [{}], [{}]", new Object[]{user, promotion, freeTrialStartedTimestampSeconds});

        LOGGER.info("Attempt to apply promotion [{}]", promotion);

        boolean isPromotionApplied = false;
        if (promotion == null) {
            throw new IllegalArgumentException("No promotion found");
        }

        UserBanned userBanned = getUserBanned(user.getId());
        if (userBanned == null || userBanned.isGiveAnyPromotion()) {
            int freeWeeks = promotion.getFreeWeeks() == 0 ? (promotion.getEndDate() - freeTrialStartedTimestampSeconds) / (7 * 24 * 60 * 60) : promotion.getFreeWeeks();
            int nextSubPayment = promotion.getFreeWeeks() == 0 ? promotion.getEndDate() : freeTrialStartedTimestampSeconds + freeWeeks * Utils.WEEK_SECONDS;

            final PromoCode promoCode = promotion.getPromoCode();
            user.setLastPromo(promoCode);
            user.setNextSubPayment(nextSubPayment);
            user.setFreeTrialExpiredMillis(new Long(nextSubPayment * 1000L));
            user.setPotentialPromoCodePromotion(null);

            if(isVideoAndMusicPromoCode(promoCode)){
                user.setVideoFreeTrialHasBeenActivated(true);
            }

            user.setStatus(UserStatusDao.getSubscribedUserStatus());
            user.setFreeTrialStartedTimestampMillis(freeTrialStartedTimestampSeconds * 1000L);
            user = entityService.updateEntity(user);

            promotionService.updatePromotionNumUsers(promotion);

            AccountLog accountLog = new AccountLog(user.getId(), null, (byte) (user.getSubBalance() + freeWeeks),
                    PROMOTION_BY_PROMO_CODE_APPLIED);
            accountLog.setPromoCode(promoCode.getCode());
            entityService.saveEntity(accountLog);
            for (byte i = 1; i <= freeWeeks; i++) {
                entityService.saveEntity(new AccountLog(user.getId(), null, (byte) (user.getSubBalance() + freeWeeks - i),
                        SUBSCRIPTION_CHARGE));
            }
            isPromotionApplied = true;
        } else {
            user.setPotentialPromoCodePromotion(null);
            user = entityService.updateEntity(user);
            LOGGER.info("The promotion [{}] wasn't applied because of user is banned", promotion);
        }

        return isPromotionApplied;
    }

    private boolean isVideoAndMusicPromoCode(PromoCode promoCode) {
        return isNotNull(promoCode) && promoCode.forVideoAndMusic();
    }

	public void updateMobile(User user, String mobile, Integer operator, String communityName) {
		if (communityName == null)
			throw new NullPointerException(
					"The parameter communityName is null");
		PhoneNumberValidator.validate(mobile);

		if (!Operator.getMapAsIds().containsKey(operator))
			throw new ServiceException("Unknown operator parameter value: ["
					+ operator + "]");

		Community community = CommunityDao.getMapAsNames().get(communityName);
		PaymentPolicy paymentPolicy = paymentPolicyService.getPaymentPolicy(operator, PaymentType.PREMIUM_USER, community.getId());

		String migPhone = convertPhoneNumberFromGreatBritainToInternationalFormat(mobile);
		migPaymentService.createPaymentDetails(getMigPhoneNumber(operator, migPhone), user, community, paymentPolicy);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePaymentDetails(User user, UserRegInfo userRegInfo) {

		if (userRegInfo == null)
			throw new ServiceException("The parameter userRegInfo is null");
		if (user == null)
			throw new ServiceException("The parameter user is null");


		PaymentDetailsValidator.validate(userRegInfo);

		Community community = CommunityDao.getMapAsNames().get(userRegInfo.getCommunityName());
		user = findById(user.getId());
		createPaymentDetails(userRegInfo, user, community);
	}

	private void validateCountry(String appVersion, String countryCode) {
		if (appVersion == null)
			throw new ServiceException("The parameter appVersion is null");
		if (countryCode == null)
			throw new ServiceException("The parameter countryCode is null");

		boolean isValid = countryAppVersionService.isAppVersionLinkedWithCountry(appVersion, countryCode);
		if (!isValid)
			throw ValidationException.getInstance("registerUser.command.error.unsupportedCountry");
	}

	// TODO Inspect usage of this method and remove it in release 3.5
	@Deprecated
	public void validateUserPin(User user, String pin, String communityName) {
		if (pin == null)
			throw new ServiceException("The parameter pin is null");
		if (StringUtils.hasText(user.getPin())) {
			LOGGER.info(MessageFormat.format("Received from user [{0}] pin [{1}]", user.getId(), pin));

			// For REGISTER_USER command via mobile
			if (pin.equals(user.getPin())) {
				paymentDetailsService.commitMigPaymentDetails(pin, user.getId());
			} else {
				LOGGER.info(MessageFormat.format("Incorrect pin for user [{0}]", user.getId()));
				throw new ServiceException("Incorrect pin");
			}
		} else {
			throw new ServiceException("User has no pin to verify");
		}
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public User updateUser(User user) {
		return userRepository.save(user);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User mergeUser(User user, User userByDeviceUID) {
		userByDeviceUID = userRepository.findOne(userByDeviceUID.getId());
		LOGGER.debug("input parameters user, userByDeviceUID: [{}], [{}]", user, userByDeviceUID);
		userDeviceDetailsService.removeUserDeviceDetails(userByDeviceUID);

		drmService.moveDrms(userByDeviceUID, user);

		entityService.removeEntity(userByDeviceUID);

		user.setDeviceUID(userByDeviceUID.getDeviceUID());
		user.setDeviceString(userByDeviceUID.getDeviceString());
		user.setDeviceModel(userByDeviceUID.getDeviceModel());
		user.setDevice(userByDeviceUID.getDevice());
		user.setDeviceType(userByDeviceUID.getDeviceType());
		user.setIpAddress(userByDeviceUID.getIpAddress());
		user.setTempToken(userByDeviceUID.getToken());
		entityService.updateEntity(user);

		LOGGER.info("Output parameter user=[{}]", user);
		return user;
	}

	public String getCommunityNameByUserGroup(byte userGroup) {
		return userDao.getCommunityNameByUserGroup(userGroup);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public synchronized void applyPromotion(User user) {
		Promotion promotion = userDao.getActivePromotion(user.getUserGroup());
		LOGGER.info("promotion [{}]", promotion);
		if (promotion != null) {
			user.setSubBalance((byte) (user.getSubBalance() + promotion.getFreeWeeks()));
			entityService.updateEntity(user);
			promotion.setNumUsers(promotion.getNumUsers() + 1);
			entityService.updateEntity(promotion);
			entityService.saveEntity(
					new AccountLog(user.getId(), null, user.getSubBalance(),
							PROMOTION));
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PaymentDetails> unsubscribeUser(String phoneNumber, String operatorName) {
		LOGGER.debug("input parameters phoneNumber, operatorName: [{}], [{}]", phoneNumber, operatorName);

		List<PaymentDetails> paymentDetails = paymentDetailsService.findActivatedPaymentDetails(operatorName, phoneNumber);
		LOGGER.info("Trying to unsubscribe [{}] user(s) having [{}] as mobile number", paymentDetails.size(), phoneNumber);
		final String reason = "STOP sms";
		for (PaymentDetails paymentDetail : paymentDetails) {
			final User owner = paymentDetail.getOwner();
			if(owner!=null && paymentDetail.equals(owner.getCurrentPaymentDetails())){
				unsubscribeUser(owner, reason);
			}
			paymentDetail.setActivated(false);
			paymentDetail.setDescriptionError(reason);
			paymentDetail.setDisableTimestampMillis(System.currentTimeMillis());
			entityService.updateEntity(paymentDetail);
			LOGGER.info("Phone number [{}] was successfully unsubscribed", phoneNumber);
		}

		LOGGER.debug("Output parameter paymentDetails=[{}]", paymentDetails);
		return paymentDetails;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User unsubscribeUser(int userId, UnsubscribeDto dto) {
		LOGGER.debug("input parameters userId, dto: [{}], [{}]", userId, dto);
		User user = entityService.findById(User.class, userId);
		String reason = dto.getReason();
		if (reason == null || reason.isEmpty()) {
			reason = "Unsubscribed by user manually via web portal";
		}
		user = unsubscribeUser(user, reason);
		LOGGER.info("Output parameter user=[{}]", user);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User unsubscribeUser(User user, final String reason) {
		LOGGER.debug("input parameters user, reason: [{}], [{}]", user, reason);
		notNull(user, "The parameter user is null");

		user = paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, reason);

		user = entityService.updateEntity(user);
		LOGGER.info("Output parameter user=[{}]", user);
		return user;
	}

	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public List<PaymentPolicy> getPaymentPolicies(
			String communityName) {
		if (communityName == null)
			throw new ServiceException("The parameter communityName is null");
		Byte communityId = Community.getMapAsNames().get(communityName).getId();
		List<PaymentPolicy> paymentPolicies = entityService.findListByProperty(
                PaymentPolicy.class, PaymentPolicy.Fields.communityId.name(),
                communityId);
		for (PaymentPolicy paymentPolicy : paymentPolicies) {
			Operator operator = paymentPolicy.getOperator();
			if (null != operator)
				paymentPolicy.setOperatorName(operator.getName());
		}
		return paymentPolicies;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void makeUserActive(User user) {
		if (user == null)
			throw new ServiceException("The parameter user is null");
		user.setLastDeviceLogin(Utils.getEpochSeconds());
		updateUser(user);
	}

    @Transactional(readOnly = true)
	public User findById(int id) {
		return entityService.findById(User.class, id);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User changePassword(Integer userId, String newPassword) {
		LOGGER.debug("input parameters changePassword(Integer userId, String newPassword): [{}], [{}]", new Object[]{userId, newPassword});

		User user = findById(userId);

		String storedToken = Utils.createStoredToken(user.getUserName(), newPassword);

		userRepository.updateFields(storedToken, userId);

		LOGGER.debug("output parameters changePassword(Integer userId, String newPassword): [{}]", new Object[]{user});
		return user;
	}

	private User getUser(UserRegInfo userRegInfo) {
		if (userRegInfo == null)
			throw new ServiceException("The parameter userRegInfo is null");

		LOGGER.debug("input parameters userRegInfo: [{}]",
				new Object[] { userRegInfo });

		final String userName = userRegInfo.getEmail().toLowerCase();
		final String communityName = userRegInfo.getCommunityName();

		String deviceType = userRegInfo.getDeviceType();
		byte deviceTypeId;
		if (StringUtils.hasText(deviceType)) {
			deviceTypeId = deviceTypeService.findIdByName(deviceType);
		} else {
			deviceTypeId = DeviceTypeDao.getNoneDeviceType().getI();
		}

		User user = new User();
		user.setDisplayName(userRegInfo.getDisplayName());
		user.setTitle(userRegInfo.getTitle());
		user.setFirstName(userRegInfo.getFirstName());
		user.setLastName(userRegInfo.getLastName());
		user.setUserName(userName);
		user.setToken(userRegInfo.getStoredToken());
		user.setDeviceType(DeviceTypeDao.getDeviceTypeMapIdAsKeyAndDeviceTypeValue().get(deviceTypeId));
		user.setDeviceString(userRegInfo.getDeviceString());
		user.setDevice("");
		byte communityId = CommunityDao.getCommunityId(communityName);
		user.setUserGroup(UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(communityId));
		user.setAddress1(userRegInfo.getAddress());
		user.setAddress2(userRegInfo.getAddress());
		user.setCity(userRegInfo.getCity());
		user.setPostcode(userRegInfo.getPostCode());
		user.setCountry(countryService.findIdByFullName(userRegInfo
				.getCountryFullName()));

		String phoneNumber = userRegInfo.getPhoneNumber();
		if (phoneNumber == null || phoneNumber.equalsIgnoreCase("null"))
			userRegInfo.setPhoneNumber("");

		user.setPin("");
		user.setMobile(userRegInfo.getPhoneNumber());
		user.setCode("");
		user.setSessionID("");
		user.setIpAddress(userRegInfo.getIpAddress());
		user.setTempToken("");
		user.setCanContact(userRegInfo.getNewsByEmail());

		user.setOperator(userRegInfo.getOperator());

		if (0 == user.getOperator()) {
			Entry<Integer, Operator> entry = OperatorDao.getMapAsIds().entrySet().iterator().next();
			user.setOperator(entry.getKey());
		}

		LOGGER.debug("Output parameter user=[{}]", user);
		return user;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	private User continueRegistration(UserRegInfo userRegInfo) {
		if (userRegInfo == null)
			throw new ServiceException("The parameter userRegInfo is null");

		LOGGER.debug("input parameters userRegInfo: [{}]",
                new Object[]{userRegInfo});

		final String userName = userRegInfo.getEmail().toLowerCase();
		final String communityName = userRegInfo.getCommunityName();
		User user = findByNameAndCommunity(userName, communityName);

		if (user != null) {
			throw new ServiceException("User with user name [" + userName + "] and community name [" + communityName + "] already registered");
		}

		user = getUser(userRegInfo);
		String promotionCode = userRegInfo.getPromotionCode();

		if (promotionCode == null || promotionCode.isEmpty()) {
			Community community = CommunityDao.getMapAsNames().get(communityName);
			user.setStatus(UserStatusDao.getEulaUserStatus());

			entityService.saveEntity(user);

			createPaymentDetails(userRegInfo, user, community);
		} else {
			user.setStatus(UserStatusDao.getSubscribedUserStatus());

			entityService.saveEntity(user);

			applyPromotionByPromoCode(user, promotionCode);
		}

		user.setCode(Utils.getOTACode(user.getId(), user.getUserName()));

		LOGGER.debug("Output parameter user=[{}]", user);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PaymentDetails createPaymentDetails(UserRegInfo userRegInfo, User user, Community community) {
		PaymentDetailsDto dto = UserRegInfo.getPaymentDetailsDto(userRegInfo);

		if (userRegInfo.getPaymentType().equals(UserRegInfoServer.PaymentType.PREMIUM_USER)) {
			String migPhone = convertPhoneNumberFromGreatBritainToInternationalFormat(dto.getPhoneNumber());
			dto.setPhoneNumber(getMigPhoneNumber(dto.getOperator(), migPhone));
		}

		PaymentDetails createPaymentDetails = paymentDetailsService.createPaymentDetails(dto, user, community);
		if (null == createPaymentDetails) {
			entityService.updateEntity(user);
		}
		return createPaymentDetails;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	private void applyPromotionByPromoCode(final User user, final String promotionCode) {
		Validate.notNull(user, "The parameter user is null");
		Validate.notNull(promotionCode, "The parameter promotionCode is null");

		LOGGER.debug("input parameters user, promotionCode, communityName: [{}], [{}]", user, promotionCode);

		Promotion userPromotion = promotionService.getActivePromotion(promotionCode, communityName(user));
		if (userPromotion == null) {
			LOGGER.info("Promotion code [{}] does not exist", promotionCode);
			throw new ServiceException("Invalid promotion code. Please re-enter the code or leave the field blank");
		}

		boolean isPromotionApplied = applyPromotionByPromoCode(user, userPromotion);
        if (isPromotionApplied){
            proceessAccountCheckCommandForAuthorizedUser(user.getId(), null, null, null);
        }

	}

	private String communityName(User user) {
		UserGroup userGroup = user.getUserGroup();
		Community community = userGroup.getCommunity();
		return community.getName();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void processPaymentSubBalanceCommand(User user, int subweeks, SubmittedPayment payment) {
		LOGGER.debug("processPaymentSubBalanceCommand input parameters user, subweeks, payment: [{}]", new Object[] { user, subweeks, payment });
		final String paymentSystem = payment.getPaymentSystem();

		// Update last Successful payment time
		final long epochMillis = Utils.getEpochMillis();
		user.setLastSuccessfulPaymentTimeMillis(epochMillis);
		user.setLastSubscribedPaymentSystem(paymentSystem);
        user.setLastSuccessfulPaymentDetails(payment.getPaymentDetails());

		final String base64EncodedAppStoreReceipt = payment.getBase64EncodedAppStoreReceipt();

		boolean wasInLimitedStatus = UserStatusDao.LIMITED.equals(user.getStatus().getName());

		final int oldNextSubPayment = user.getNextSubPayment();
		if (paymentSystem.equals(PaymentDetails.ITUNES_SUBSCRIPTION)){
            if (user.isOnFreeTrial()) {
                skipFreeTrial(user);
            }
			user.setNextSubPayment(payment.getNextSubPayment());
			user.setAppStoreOriginalTransactionId(payment.getAppStoreOriginalTransactionId());
			user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
		}else if (user.isO2CommunityUser() && user.isnonO2User()) {
			user.setNextSubPayment(Utils.getMonthlyNextSubPayment(oldNextSubPayment));
		}else if (user.isO2CommunityUser() && !user.isnonO2User()){
			if (Utils.getEpochSeconds() > oldNextSubPayment){
				user.setNextSubPayment(Utils.getEpochSeconds() + subweeks * Utils.WEEK_SECONDS);
			}else{
				user.setNextSubPayment(oldNextSubPayment + subweeks * Utils.WEEK_SECONDS);
			}
		} else {
			user.setSubBalance(user.getSubBalance() + subweeks);

			user.setNextSubPayment(Utils.getNewNextSubPayment(oldNextSubPayment));
		}

		entityService.saveEntity(new AccountLog(user.getId(), payment, user.getSubBalance(), CARD_TOP_UP));
		// The main idea is that we do pre-payed service, this means that
		// in case of first payment or after LIMITED status we need to decrease subBalance of user immediately
		if (wasInLimitedStatus || UserStatusDao.getEulaUserStatus().getI() == user.getStatus().getI()) {
			if (!user.isO2CommunityUser() && !paymentSystem.equals(PaymentDetails.ITUNES_SUBSCRIPTION)) {
				user.setSubBalance(user.getSubBalance() - 1);
				entityService.saveEntity(new AccountLog(user.getId(), payment, user.getSubBalance(), SUBSCRIPTION_CHARGE));
			}
		}

		// Update user status to subscribed
		user.setStatus(UserStatusDao.getSubscribedUserStatus());

		entityService.updateEntity(user);

		LOGGER.info("User {} with balance {}", user.getId(), user.getSubBalance());
	}

	public boolean isnonO2User(User user) {
		Community community = user.getUserGroup().getCommunity();
		String communityUrl = checkNotNull(community.getRewriteUrlParameter());

		boolean isnonO2User = false;
		if ("o2".equalsIgnoreCase(communityUrl) && (!"o2".equals(user.getProvider()))) {
			isnonO2User = true;
		}

		return isnonO2User;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User updateUserBalance(User user, byte intSubBalance) {
		if (user == null)
			throw new ServiceException("The parameter user is null");
		LOGGER.debug("input parameters user, intSubBalance: [{}]", new Object[] { user, intSubBalance });

		user.setSubBalance(intSubBalance);

		user = updateUser(user);
		LOGGER.debug("Output parameter user=[{}]", user);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AccountCheckDTO proceessAccountCheckCommandForAuthorizedUser(int userId, String pushNotificationToken, String deviceType, String transactionReceipt) {
		LOGGER.debug("input parameters userId, pushToken,  deviceType, transactionReceipt: [{}], [{}], [{}], [{}]", new String[]{String.valueOf(userId), pushNotificationToken, deviceType, transactionReceipt});

		try {
			iTunesService.processInAppSubscription(userId, transactionReceipt);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		User user = userDao.findUserById(userId);

		user = assignPotentialPromotion(user);
		user = updateLastDeviceLogin(user);

		if (user.getLastDeviceLogin() == 0)
			makeUserActive(user);

		if (deviceType != null && pushNotificationToken != null)
			userDeviceDetailsService.mergeUserDeviceDetails(user, pushNotificationToken, deviceType);

		Community community = user.getUserGroup().getCommunity();

		List<String> appStoreProductIds = paymentPolicyService.findAppStoreProductIdsByCommunityAndAppStoreProductIdIsNotNull(community);

		AccountCheckDTO accountCheckDTO = toAccountCheckDTO(user, null, appStoreProductIds, canActivateVideoTrial(user));

		accountCheckDTO.setPromotedDevice(deviceService.existsInPromotedList(community, user.getDeviceUID()));

		accountCheckDTO.setPromotedWeeks((int) Math.floor((user.getNextSubPayment() * 1000L - System.currentTimeMillis()) / 1000 / 60 / 60 / 24 / 7) + 1);

		List<Integer> relatedMediaUIDsByLogTypeList = accountLogService.getRelatedMediaUIDsByLogType(userId, OFFER_PURCHASE);

		accountCheckDTO.setHasOffers(false);
		if (relatedMediaUIDsByLogTypeList.isEmpty()) {
			List<ContentOfferDto> contentOfferDtos = offerService.getContentOfferDtos(user.getId());
			if (contentOfferDtos != null && contentOfferDtos.size() > 0)
				accountCheckDTO.setHasOffers(true);
		}

		LOGGER.debug("Output parameter accountCheckDTO=[{}]", accountCheckDTO);
		return accountCheckDTO;
	}

	// TODO Review this method
	public User findUserTree(int userId) {
		LOGGER.debug("input parameters userId: [{}]", userId);
		User user = userDao.findUserTree(userId);

		LOGGER.debug("Output parameter user=[{}]", user);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User loginUser(User user, String communityName, Errors errors) {
		User existingUser = findByNameAndCommunity(user.getUserName(), communityName);
		if (null != existingUser) {
			if (!existingUser.getToken().equals(user.getToken())) {
				errors.rejectValue("token", "login.form.error");
			} else {
				existingUser = assignPotentialPromotion(existingUser);
			}
		} else {
			errors.rejectValue("token", "user.service.error.no.user.found");
		}
		return existingUser;
	}

	public User assignPotentialPromotion(User existingUser) {
		LOGGER.debug("input parameters communityName: [{}]", existingUser);
		if (existingUser.getLastSuccessfulPaymentTimeMillis() == 0) {
			String communityName = communityName(existingUser);
			Promotion promotion = promotionService.getPromotionForUser(communityName, existingUser);
			existingUser.setPotentialPromotion(promotion);
			existingUser = entityService.updateEntity(existingUser);
			LOGGER.info("Promotion [{}] was attached to user with id [{}]", promotion, existingUser.getId());
		}
		LOGGER.debug("Output parameter existingUser=[{}]", existingUser);
		return existingUser;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User setPotentialPromotion(User user, Promotion promotion) {
		user.setPotentialPromotion(promotion);
		return updateUser(user);
	}

    @Deprecated
	@Transactional(propagation = Propagation.REQUIRED)
	public User registerUser(UserRegDetailsDto userRegDetailsDto) {
		LOGGER.debug("input parameters userRegDetailsDto: [{}]", userRegDetailsDto);

		final String userName = userRegDetailsDto.getEmail().toLowerCase();

		DeviceType deviceType = DeviceTypeDao.getDeviceTypeMapNameAsKeyAndDeviceTypeValue().get(userRegDetailsDto.getDeviceType());
		if (deviceType == null)
			deviceType = DeviceTypeDao.getNoneDeviceType();

		final String deviceString = userRegDetailsDto.getDeviceString();

		Community community = communityService.getCommunityByName(userRegDetailsDto.getCommunityName());

		User user = newUser(userRegDetailsDto, userName, deviceType, deviceString, community);
		entityService.saveEntity(user);

		String communityName = community.getName();

		String promotionCode = userRegDetailsDto.getPromotionCode();
		if (promotionCode == null)
			promotionCode = getDefaultPromoCode(communityName);

		applyPromotionByPromoCode(user, promotionCode);

		LOGGER.debug("Output parameter user=[{}]", user);
		assignPotentialPromotion(user);
		return user;
	}

	private User newUser(UserRegDetailsDto userRegDetailsDto, String userName, DeviceType deviceType, String deviceString, Community community) {
		User user = new User();
		user.setUserName(userName);
		user.setToken(Utils.createStoredToken(userName, userRegDetailsDto.getPassword()));

		user.setDeviceType(deviceType);
		if (deviceString != null)
			user.setDeviceString(deviceString);

		user.setUserGroup(UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(community.getId()));
		user.setCountry(countryService.findIdByFullName("Great Britain"));
		user.setIpAddress(userRegDetailsDto.getIpAddress());
		user.setCanContact(userRegDetailsDto.isNewsDeliveringConfirmed());
		Entry<Integer, Operator> entry = OperatorDao.getMapAsIds().entrySet().iterator().next();
		user.setOperator(entry.getKey());
		user.setStatus(UserStatusDao.getEulaUserStatus());
		user.setFacebookId(userRegDetailsDto.getFacebookId());
		return user;
	}

	public String getDefaultPromoCode(String communityName) {
		LOGGER.debug("input parameters communityName: [{}], [{}]", communityName);
		Community community = CommunityDao.getMapAsNames().get(communityName);

		String promotionCode = messageSource.getMessage(community.getRewriteUrlParameter(), "defaultPromotionCode", null, null);
		LOGGER.info("Output parameter [{}]", promotionCode);
		return promotionCode;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public User getUser(String userName, String communityUrl) {
		LOGGER.debug("input parameters email, communityUrl: [{}], [{}]", userName, communityUrl);
		User user = userRepository.findOne(userName, communityUrl);
		LOGGER.debug("Output parameter user=[{}]", user);
		return user;
	}

	public boolean isCommunitySupportByIp(String email, String community, String remoteAddr) {
		String countryCode = findCountryCodeByIp(remoteAddr);
		return countryAppVersionService.isAppVersionLinkedWithCountry("CNBETA", countryCode);
	}

	public boolean checkPromotionCode(String code, String community) {
		Promotion promotion = promotionService.getActivePromotion(code, community);
		return (null != promotion) ? true : false;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User registerUser(FacebookProfile facebookProfile, String communityName, String ipAddress) {
		LOGGER.debug("input parameters facebookProfile: [{}]", facebookProfile);

		String userName;
		String email = facebookProfile.getEmail();
		final String facebookId = facebookProfile.getId();
		if (email == null)
			userName = facebookId;
		else
			userName = email.toLowerCase();

		String tmpPassword = Utils.getRandomString(AppConstants.TMP_PASSWORD_LENGTH);

		UserRegDetailsDto userRegDetailsDto = new UserRegDetailsDto();
		userRegDetailsDto.setEmail(userName);
		userRegDetailsDto.setCommunityName(communityName);
		userRegDetailsDto.setIpAddress(ipAddress);
		userRegDetailsDto.setPassword(tmpPassword);
		userRegDetailsDto.setFacebookId(facebookId);

		User user = registerUser(userRegDetailsDto);
		LOGGER.debug("Output parameter user=[{}]", user);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean restoreUserPassword(String email, String communityRedirectURL) {
		LOGGER.debug("input parameters email, communityRedirectURL: [{}], [{}]", email, communityRedirectURL);

		Community community = communityService.getCommunityByUrl(communityRedirectURL.toUpperCase());

		String tmpPassword = Utils.getRandomString(AppConstants.TMP_PASSWORD_LENGTH);
		String localStoredToken = Utils.createStoredToken(email, tmpPassword);

		User user = findByNameAndCommunity(email, community.getName());

		boolean isUserExist = false;
		if (user != null) {
			user.setToken(localStoredToken);

			updateUser(user);

			String communityUri = communityRedirectURL.toLowerCase();
			String from = messageSource.getMessage(communityUri, email, null, null);
			String[] to = { messageSource.getMessage(communityUri, "mail.rest.password.address", null, null) };
			String subject = messageSource.getMessage(communityUri, "mail.rest.password.subject", null, null);
			String body = messageSource.getMessage(communityUri, "mail.rest.password.body", null, null);
			String portalUrl = messageSource.getMessage(communityUri, "mail.portal.url", null, null);

			Map<String, String> modelMap = new HashMap<String, String>();
			modelMap.put("displayName", user.getDisplayName());
			modelMap.put("portalUrl", portalUrl);
			modelMap.put("email", email);
			modelMap.put("password", tmpPassword);

			mailService.sendMail(from, to, subject, body, modelMap);

			isUserExist = true;
		}
		LOGGER.debug("Output parameter isUserExist=[{}]", isUserExist);
		return isUserExist;
	}

	public AccountDto getAccountDetails(int userId) {
		LOGGER.debug("input parameters userId: [{}]", userId);

		User user = findById(userId);

		AccountDto accountDto = user.toAccountDto();

		LOGGER.debug("Output parameter accountDto=[{}]", accountDto);
		return accountDto;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean sendSMSWithOTALink(String phone, int userId) {
		User user = findById(userId);
		String code = Utils.getOTACode(user.getId(), user.getUserName());
		String[] args = { migHttpService.getOtaUrl() + "&CODE=" + code };
		String migPhone = convertPhoneNumberFromGreatBritainToInternationalFormat(phone);

		user.setCode(code);
		updateUser(user);
		MigResponse response = migHttpService.makeFreeSMSRequest(getMigPhoneNumber(user.getOperator(), migPhone),
                messageSource.getMessage(user.getUserGroup().getCommunity().getRewriteUrlParameter().toLowerCase(), "sms.otalink.text", args, null));
		LOGGER.info("OTA link has been sent to user {}", userId);
		if (200 == response.getHttpStatus())
			return true;
		return false;
	}

	public AccountDto saveAccountDetails(AccountDto accountDto, int userId) {
		LOGGER.debug("input parameters accountDto: [{}]", accountDto);

		User user = findById(userId);

		String localStoredToken = Utils.createStoredToken(user.getUserName(), accountDto.getNewPassword());

		user.setToken(localStoredToken);
		user.setMobile(accountDto.getPhoneNumber());

		updateUser(user);

		accountDto = user.toAccountDto();

		LOGGER.debug("Output parameter accountDto=[{}]", accountDto);
		return accountDto;
	}

	public void contactWithUser(String from, String name, String subject) throws ServiceException {

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User assignPotentialPromotion(int userId) {
		LOGGER.debug("input parameters userId: [{}]", userId);
		User user = findById(userId);

		user = assignPotentialPromotion(user);

		LOGGER.debug("Output parameter user=[{}]", user);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AccountCheckDTO updateUserFacebookDetails(UserFacebookDetailsDto userFacebookDetailsDto) {
		LOGGER.debug("input parameters userFacebookDetailsDto: [{}]", userFacebookDetailsDto);

		final String deviceUID = userFacebookDetailsDto.getDeviceUID();
		final String storedToken = userFacebookDetailsDto.getStoredToken();
		final String passedCommunityName = userFacebookDetailsDto.getCommunityName();
		Community community = communityService.getCommunityByName(passedCommunityName);

		UserCredentions userCredentions = facebookService.getUserCredentions(passedCommunityName, userFacebookDetailsDto.getFacebookToken());

		User userByDeviceUID = checkUserDetailsBeforeUpdate(deviceUID, storedToken, community);

		User user = findByFacebookId(userCredentions.getId(), passedCommunityName);

		if (user == null) {
			user = findByNameAndCommunity(userCredentions.getEmail(), passedCommunityName);
		}

		if (user != null && userByDeviceUID != null && user.getId() != userByDeviceUID.getId()) {
			user.setFacebookId(userCredentions.getId());
			mergeUser(user, userByDeviceUID);
		} else {
			user = userByDeviceUID;
			user.setUserName(userCredentions.getEmail() != null ? userCredentions.getEmail() : userCredentions.getId());
			user.setFacebookId(userCredentions.getId());
			user.setFirstUserLoginMillis(Utils.getEpochMillis());

			updateUser(user);
		}

		AccountCheckDTO accountCheckDTO = proceessAccountCheckCommandForAuthorizedUser(user.getId(), null, null, null);
		LOGGER.debug("Output parameter accountCheckDTO=[{}]", accountCheckDTO);
		return accountCheckDTO;
	}

	private User checkUserDetailsBeforeUpdate(final String deviceUID, final String storedToken, final Community community) {
		LOGGER.debug("input parameters deviceUID, storedToken, community: [{}], [{}], [{}]", new Object[] { deviceUID, storedToken, community });
		final String communityRedirectUrl = community.getRewriteUrlParameter();

		User user = findByDeviceUIDAndCommunityRedirectURL(deviceUID, communityRedirectUrl);
		if (user == null || !user.getToken().equals(storedToken)) {
			ServerMessage serverMessage = ServerMessage.getInvalidPassedStoredTokenForDeviceUID(deviceUID, communityRedirectUrl);
			throw new UserCredentialsException(serverMessage);
		}

		LOGGER.debug("Output parameter result=[{}]", user);
		return user;
	}

	public User findByDeviceUIDAndCommunityRedirectURL(String deviceUID, String communityRedirectUrl) {
		LOGGER.debug("input parameters deviceUID, communityRedirectUrl: [{}], [{}]", deviceUID, communityRedirectUrl);
		User user = userDao.findByDeviceUIDAndCommunityRedirectUrl(deviceUID, communityRedirectUrl);
		LOGGER.debug("Output parameter user=[{}]", user);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AccountCheckDTO registerUser(UserDeviceRegDetailsDto userDeviceRegDetailsDto, boolean createPotentialPromo) {
		LOGGER.info("REGISTER_USER Started [{}]", userDeviceRegDetailsDto);

		final String deviceUID = userDeviceRegDetailsDto.getDeviceUID().toLowerCase();

		DeviceType deviceType = DeviceTypeDao.getDeviceTypeMapNameAsKeyAndDeviceTypeValue().get(userDeviceRegDetailsDto.getDeviceType());
		if (deviceType == null)
			deviceType = DeviceTypeDao.getNoneDeviceType();

		Community community = communityService.getCommunityByName(userDeviceRegDetailsDto.getCommunityName());
		User user = findByDeviceUIDAndCommunityRedirectURL(deviceUID, community.getRewriteUrlParameter());

		if (null == user) {
			user = createUser(userDeviceRegDetailsDto, deviceUID, deviceType, community);
		}

		if (createPotentialPromo && user.getNextSubPayment() == 0) {
			String communityUri = community.getRewriteUrlParameter().toLowerCase();
			String deviceModel = user.getDeviceModel();

			final String promotionCode;

			if (canBrPromoted(community, deviceUID, deviceModel)) {
				promotionCode = messageSource.getMessage(communityUri, "promotionCode", null, null);
			} else {
				String blackListModels = messageSource.getMessage(communityUri, "promotion.blackListModels", null, null);
				if (deviceModel != null && blackListModels.contains(deviceModel)) {
					promotionCode = null;
				} else
					promotionCode = messageSource.getMessage(communityUri, "defaultPromotionCode", null, null);
			}

			setPotentialPromoCodePromotion(community, user, promotionCode);

		}

		user.setActivationStatus(REGISTERED);
		userRepository.save(user);
        LOGGER.info("REGISTER_USER user[{}] changed activation_status to[{}]", user.getUserName(), REGISTERED);
		AccountCheckDTO accountCheckDTO = proceessAccountCheckCommandForAuthorizedUser(user.getId(), null, null, null);
		LOGGER.debug("REGISTER_USER Output parameter [{}]", accountCheckDTO);
		return accountCheckDTO;
	}

	private User createUser(UserDeviceRegDetailsDto userDeviceRegDetailsDto, String deviceUID, DeviceType deviceType, Community community) {
		User user;
		user = new User();
		user.setUserName(deviceUID);
		user.setToken(Utils.createStoredToken(deviceUID, Utils.getRandomString(20)));

		user.setDeviceType(deviceType);
		user.setUserGroup(UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(community.getId()));
		user.setCountry(countryService.findIdByFullName("Great Britain"));
		user.setIpAddress(userDeviceRegDetailsDto.getIpAddress());
		Entry<Integer, Operator> entry = OperatorDao.getMapAsIds().entrySet().iterator().next();
		user.setOperator(entry.getKey());
		user.setStatus(UserStatusDao.getLimitedUserStatus());
		user.setDeviceUID(deviceUID);
		user.setDeviceModel(userDeviceRegDetailsDto.getDeviceModel() != null ? userDeviceRegDetailsDto.getDeviceModel() : deviceType.getName());

		user.setFirstDeviceLoginMillis(System.currentTimeMillis());
		user.setActivationStatus(REGISTERED);

		entityService.saveEntity(user);
		return user;
	}

	private boolean canBrPromoted(Community community, String deviceUID, String deviceModel) {
		boolean existsInPromotedList = deviceService.existsInPromotedList(community, deviceUID);
		boolean promotedDeviceModel = deviceService.isPromotedDeviceModel(community, deviceModel);
		boolean doesNotExistInNotPromotedList = !deviceService.existsInNotPromotedList(community, deviceUID);
		return existsInPromotedList || (promotedDeviceModel && doesNotExistInNotPromotedList);
	}

    public Promotion setPotentialPromo(User user, String promotionCode) {
        Community community = user.getUserGroup()
                .getCommunity();
        return setPotentialPromo(community, user, promotionCode);
    }

	public Promotion setPotentialPromo(String communityName, User user, String promotionCode) {
		Community community = communityService.getCommunityByName(communityName);
		String communityUri = community.getRewriteUrlParameter().toLowerCase();
		String code = messageSource.getMessage(communityUri, promotionCode, null, null);
		return setPotentialPromo(community, user, code);
	}

	protected Promotion setPotentialPromo(Community community, User user, String code) {
		if (code != null) {
			Promotion potentialPromoCodePromotion = promotionService.getActivePromotion(code, community.getName());
			user.setPotentialPromoCodePromotion(potentialPromoCodePromotion);
			entityService.updateEntity(user);
			return potentialPromoCodePromotion;
		}
		return null;
	}

	public void setPotentialPromoCodePromotion(Community community, User user, String promotionCode) {
		if (promotionCode != null) {
			Promotion potentialPromoCodePromotion = promotionService.getActivePromotion(promotionCode, community.getName());
			user.setPotentialPromoCodePromotion(potentialPromoCodePromotion);
			entityService.updateEntity(user);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AccountCheckDTO applyInitialPromotion(User user) {
		LOGGER.debug("input parameters user: [{}]", new Object[] { user });

		if (user == null)
			throw new NullPointerException("The parameter user is null");

		if (UserStatusDao.LIMITED.equals(user.getStatus().getName())) {

			Promotion potentialPromoCodePromotion = user.getPotentialPromoCodePromotion();
			if (potentialPromoCodePromotion != null) {
				applyPromotionByPromoCode(user, potentialPromoCodePromotion);
			}
		}
		AccountCheckDTO accountCheckDTO = proceessAccountCheckCommandForAuthorizedUser(user.getId(), null, null, null);
		LOGGER.debug("Output parameter accountCheckDTO=[{}]", accountCheckDTO);
		return accountCheckDTO;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User updateLastDeviceLogin(User user) {
		LOGGER.debug("input parameters user: [{}]", user);

		user.setLastDeviceLogin(Utils.getEpochSeconds());
		updateUser(user);

		LOGGER.debug("Output parameter user=[{}]", user);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User updateLastWebLogin(User user) {
		LOGGER.debug("input parameters user: [{}]", user);

		user.setLastWebLogin(Utils.getEpochSeconds());
		updateUser(user);

		LOGGER.debug("Output parameter user=[{}]", user);
		return user;
	}

	@Transactional(readOnly = true)
	public Collection<User> findUsers(String searchWords, String communityURL) {
		LOGGER.debug("input parameters searchWords, communityURL: [{}], [{}]", searchWords, communityURL);

		if (searchWords == null)
			throw new NullPointerException("The parameter searchWords is null");
		if (communityURL == null)
			throw new NullPointerException("The parameter communityURL is null");

		Collection<User> users = userRepository.findUser(communityURL, "%" + searchWords + "%");

		LOGGER.info("Output parameter users=[{}]", users);
		return users;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User updateUser(UserDto userDto) {
		LOGGER.debug("input parameters userDto: [{}], [{}]", userDto);

		if (userDto == null)
			throw new NullPointerException("The parameter userDto is null");

		final Integer userId = userDto.getId();
		User user = userRepository.findOne(userId);

		if (user == null)
			throw new ServiceException("users.management.edit.page.coudNotFindUser.error", "Coudln't find user with id [" + userId + "]");

		final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();

		if (currentPaymentDetails != null && !currentPaymentDetails.isActivated() && userDto.getPaymentEnabled()) {
			throw new ServiceException("users.management.edit.page.paymentEnabledCannotBeChangedOnTrue.error", "The user payment enabled cannot be changed on true, only false");
		}

		Date originalNextSubPayment = Utils.getDateFromInt(user.getNextSubPayment());
		final int originalSubBalance = user.getSubBalance();

		if (userDto.getNextSubPayment().before(originalNextSubPayment)) {
			throw new ServiceException("users.management.edit.page.nextSubPaymentCannotBeRedused.error", "The user nextSubPayment cannot be reduced, only extended");
		}

		if (userDto.getNextSubPayment().after(originalNextSubPayment)) {
			if (user.isOnFreeTrial()) {
				accountLogService.logAccountEvent(userId, originalSubBalance, null, null, TRIAL_TOPUP, null);
			}
			else{
				accountLogService.logAccountEvent(userId, originalSubBalance, null, null, SUBSCRIPTION_CHARGE, null);
			}
		}

		final int balanceAfter = userDto.getSubBalance();
		if (originalSubBalance != balanceAfter) {
			accountLogService.logAccountEvent(userId, balanceAfter, null, null, SUPPORT_TOPUP, null);
		}

		user = UserAsm.fromUserDto(userDto, user);

		mobi.nowtechnologies.server.persistence.domain.UserStatus userStatus = UserStatusDao.getUserStatusMapUserStatusAsKey().get(userDto.getUserStatus());

		user.setStatus(userStatus);

		user = updateUser(user);

		if (!userDto.getPaymentEnabled() && currentPaymentDetails != null) {
			unsubscribeUser(user, "Unsubscribed by admin");
		}

		LOGGER.info("Output parameter user=[{}]", user);
		return user;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<User> findActivePsmsUsers(String communityURL, BigDecimal amountOfMoneyToUserNotification, long deltaSuccesfullPaymentSmsSendingTimestampMillis) {
		LOGGER.debug("input parameters communityURL, amountOfMoneyToUserNotification, deltaSuccesfullPaymentSmsSendingTimestampMillis: [{}], [{}], [{}]", new Object[]{
                communityURL, amountOfMoneyToUserNotification, deltaSuccesfullPaymentSmsSendingTimestampMillis});

		if (communityURL == null)
			throw new NullPointerException("The parameter communityURL is null");
		if (amountOfMoneyToUserNotification == null)
			throw new NullPointerException("The parameter amountOfMoneyToUserNotification is null");

		List<User> users = userRepository.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, Utils.getEpochMillis(), deltaSuccesfullPaymentSmsSendingTimestampMillis);

		LOGGER.info("Output parameter users=[{}]", users);
		return users;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User resetSmsAccordingToLawAttributes(User user) {
		LOGGER.debug("input parameters user: [{}]", user);

		if (user == null)
			throw new NullPointerException("The parameter user is null");

		user.setAmountOfMoneyToUserNotification(BigDecimal.ZERO);
		user.setLastSuccesfullPaymentSmsSendingTimestampMillis(Utils.getEpochMillis());

		final int id = user.getId();
		int updatedRowCount = userRepository.updateFields(user.getAmountOfMoneyToUserNotification(), user.getLastSuccesfullPaymentSmsSendingTimestampMillis(), id);
		if (updatedRowCount != 1)
			throw new ServiceException("Unexpected updated users count [" + updatedRowCount + "] for id [" + id + "]");

		LOGGER.info("Output parameter user=[{}]", user);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User populateAmountOfMoneyToUserNotification(User user, SubmittedPayment payment) {
		LOGGER.debug("input parameters user, payment: [{}], [{}]", user, payment);

		if (user == null)
			throw new NullPointerException("The parameter user is null");

		if (payment == null)
			throw new NullPointerException("The parameter payment is null");

		BigDecimal newAmountOfMoneyToUserNotification = user.getAmountOfMoneyToUserNotification().add(
				payment.getAmount());
		user.setAmountOfMoneyToUserNotification(newAmountOfMoneyToUserNotification);

		user = updateUser(user);
		LOGGER.info("Output parameter user=[{}]", user);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { ServiceCheckedException.class, RuntimeException.class })
	public Future<Boolean> makeSuccesfullPaymentFreeSMSRequest(User user) throws ServiceCheckedException {
		try {
			LOGGER.debug("input parameters user: [{}]", user);

			Future<Boolean> result = new AsyncResult<Boolean>(Boolean.FALSE);

			Community community = user.getUserGroup().getCommunity();
			PaymentDetails currentActivePaymentDetails = user.getCurrentPaymentDetails();
			PaymentPolicy paymentPolicy = currentActivePaymentDetails.getPaymentPolicy();

			final String upperCaseCommunityName = community.getRewriteUrlParameter().toUpperCase();
			String smsMessage = "sms.succesfullPayment.text";
			if ( user.has4GVideoAudioSubscription() ) {
				smsMessage = new StringBuilder().append(smsMessage).append(".video").toString();
			}
			final String message = messageSource.getMessage(upperCaseCommunityName, smsMessage, new Object[] { community.getDisplayName(),
					paymentPolicy.getSubcost(), paymentPolicy.getSubweeks(), paymentPolicy.getShortCode() }, null);

			if ( message == null || message.isEmpty() ) {
				LOGGER.error("The message for video users is missing in services.properties!!! Key should be [{}]. User without message [{}]", smsMessage, user.getId());
				return result;
			}

			MigResponse migResponse = migHttpService.makeFreeSMSRequest(((MigPaymentDetails) currentActivePaymentDetails).getMigPhoneNumber(), message);

			if (migResponse.isSuccessful()) {
				LOGGER
						.info(
                                "The request for freeSms sent to MIG about user {} successfully. The nextSubPayment, status, paymentStatus and subBalance was {}, {}, {}, {} respectively",
                                new Object[]{user, user.getNextSubPayment(), user.getStatus(), user.getPaymentStatus(), user.getSubBalance()});
			} else
				throw new Exception(migResponse.getDescriptionError());

			if (user.getLastSuccesfullPaymentSmsSendingTimestampMillis() == 0)
				resetLastSuccesfullPaymentSmsSendingTimestampMillis(user.getId());

			result = new AsyncResult<Boolean>(Boolean.TRUE);

			LOGGER.debug("Output parameter result=[{}]", result);
			return result;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new ServiceCheckedException("", "Coudn't make free sms request on successfull payment", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int resetLastSuccesfullPaymentSmsSendingTimestampMillis(int userId) {
		LOGGER.debug("input parameters userId: [{}]", userId);

		int updatedRowCount = userRepository.updateFields(Utils.getEpochMillis(), userId);
		if (updatedRowCount != 1)
			throw new ServiceException("Unexpected updated users count [" + updatedRowCount + "] for id [" + userId + "]");

		LOGGER.debug("Output parameter updatedRowCount=[{}]", updatedRowCount);
		return updatedRowCount;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User setToZeroSmsAccordingToLawAttributes(User user) {
		LOGGER.debug("input parameters user: [{}]", user);

		if (user == null)
			throw new NullPointerException("The parameter user is null");

		user.setAmountOfMoneyToUserNotification(BigDecimal.ZERO);
		user.setLastSuccesfullPaymentSmsSendingTimestampMillis(0);

		final int id = user.getId();
		int updatedRowCount = userRepository.updateFields(user.getAmountOfMoneyToUserNotification(), user.getLastSuccesfullPaymentSmsSendingTimestampMillis(), id);
		if (updatedRowCount != 1)
			throw new ServiceException("Unexpected updated users count [" + updatedRowCount + "] for id [" + id + "]");

		LOGGER.info("Output parameter user=[{}]", user);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User activatePhoneNumber(User user, String phone, boolean populateSubscriberData) {
		LOGGER.info("activate phone number phone=[{}] userId=[{}] activationStatus=[{}] populateO2SubscriberData=[{}]", phone, user.getId(),
                user.getActivationStatus(), populateSubscriberData);

        String phoneNumber = phone != null ? phone : user.getMobile();
        PhoneNumberValidationData phoneNumberValidationData = o2ClientService.validatePhoneNumber(phoneNumber);

        String msisdn = phoneNumberValidationData.getPhoneNumber();
        LOGGER.info("after validating phone number msidn:[{}] phone:[{}] u.mobile:[{}]", msisdn, phone,
                user.getMobile());
        if(populateSubscriberData){
            if (isPromotedDevice(msisdn)){
				// if the device is promoted, we set the default fields
				O2SubscriberData o2SubscriberData = new O2SubscriberData();
				o2SubscriberData.setBusinessOrConsumerSegment(false);
				o2SubscriberData.setContractPostPayOrPrePay(true);
				o2SubscriberData.setDirectOrIndirect4GChannel(true);
				o2SubscriberData.setProviderO2(true);
				o2SubscriberData.setTariff4G(false);
				
				new O2UserDetailsUpdater().setUserFieldsFromSubscriberData(user, o2SubscriberData);
			} else {
				populateO2subscriberData(user, msisdn);
			}
        }
        
		user.setMobile(msisdn);
		user.setActivationStatus(ENTERED_NUMBER);
        if(phoneNumberValidationData.getPin() != null)
            user.setPin(phoneNumberValidationData.getPin());

		userRepository.save(user);
        LOGGER.info("PHONE_NUMBER user[{}] changed activation status to [{}]", phoneNumber, ENTERED_NUMBER);
		return user;
	}

	private void populateO2subscriberData(User user, String phoneNumber) {
		try {
			O2SubscriberData o2SubscriberData = o2Service.getSubscriberData(phoneNumber);
			new O2UserDetailsUpdater().setUserFieldsFromSubscriberData(user, o2SubscriberData);
		} catch (Exception ex) {
			// intentionally swallowing the exception to enable user to continue with activation
			LOGGER.error("Unable to get subscriber data during activation phone={{}}", phoneNumber, ex);
		}
	}

	@Transactional(readOnly = true)
	public String getRedeemServerO2Url(User user) {
		return o2ClientService.getRedeemServerO2Url(user.getMobile());
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AccountCheckDTO applyInitPromoAndAccCheck(User user, User mobileUser, String otac, boolean updateContractAndProvider) {
        LOGGER.info("apply init promo for user with id [{}] and with user mobile [{}] in [{}] activationStatus. updateContractAndProvider=[{}]", user.getId(),
                user.getMobile(), user.getActivationStatus(), updateContractAndProvider);

        boolean hasPromo = applyInitPromo(user, mobileUser, otac, updateContractAndProvider);

        AccountCheckDTO dto = proceessAccountCheckCommandForAuthorizedUser(user.getId(), null, user.getDeviceTypeIdString(), null);
        dto.setFullyRegistred(true);
        dto.setHasPotentialPromoCodePromotion(hasPromo);
        return dto;
    }

    @Transactional(propagation = Propagation.REQUIRED)
	public void saveWeeklyPayment(User user) throws Exception {
		if (user == null)
			throw new ServiceException("The parameter user is null");

		final int subBalance = user.getSubBalance();
		if (subBalance <= 0) {
			user.setStatus(UserStatusDao.getLimitedUserStatus());
			userRepository.save(user);
			LOGGER.info("Unable to decrease balance [{}] for user with id [{}]. So the user subscription status was changed on LIMITED", subBalance, user.getId());
		} else {

			user.setSubBalance((byte) (subBalance - 1));
			user.setNextSubPayment(Utils.getNewNextSubPayment(user.getNextSubPayment()));
			user.setStatus(UserStatusDao.getSubscribedUserStatus());
			userRepository.save(user);

			accountLogService.logAccountEvent(user.getId(), user.getSubBalance(), null, null, SUBSCRIPTION_CHARGE, null);

			LOGGER.info("weekly updated user id [{}], status OK, next payment [{}], subBalance [{}]",
					new Object[] { user.getId(), Utils.getDateFromInt(user.getNextSubPayment()), user.getSubBalance() });
		}
	}

	@Transactional(readOnly = true)
	public List<User> findUsersForItunesInAppSubscription(User user, int nextSubPayment, String appStoreOriginalTransactionId) {
		LOGGER.debug("input parameters user, nextSubPayment, appStoreOriginalTransactionId: [{}], [{}], [{}]", new Object[]{user, nextSubPayment, appStoreOriginalTransactionId});

		if (user == null)
			throw new NullPointerException("The parameter user is null");
		if (appStoreOriginalTransactionId == null)
			throw new NullPointerException("The parameter appStoreOriginalTransactionId is null");

		List<User> users = userRepository.findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId);
		users.add(user);

		LOGGER.debug("Output parameter users=[{}]", users);
		return users;
	}

	@Transactional(readOnly = true)
	public List<User> getUsersForPendingPayment() {
		List<User> users = userRepository.getUsersForPendingPayment(Utils.getEpochSeconds());
		return users;
	}

	@Transactional(readOnly = true)
	public List<User> getListOfUsersForWeeklyUpdate() {
		List<User> users = userRepository.getListOfUsersForWeeklyUpdate(Utils.getEpochSeconds(), PAGEABLE_FOR_WEEKLY_UPDATE);
		LOGGER.debug("Output parameter users=[{}]", users);
		return users;
	}

	@Transactional(readOnly = true)
	public List<User> findBefore48hExpireUsers(int epochSeconds, Pageable pageable) {
		return userRepository.findBefore48hExpireUsers(epochSeconds, pageable);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateLastBefore48SmsMillis(long lastBefore48SmsMillis, int userId) {
		userRepository.updateLastBefore48SmsMillis(lastBefore48SmsMillis, userId);
	}

    @Transactional
    public User updateTokenDetails(User user, String idfa) {
        int result = userRepository.updateTokenDetails(user.getId(), idfa);

        if(result > 0){
            user.setIdfa(idfa);
        }

        return user;
    }

	@Transactional(readOnly = true)
	public List<User> getUsersForRetryPayment() {

		List<User> usersForRetryPayment = userRepository.getUsersForRetryPayment(Utils.getEpochSeconds());

		LOGGER.debug("Output parameter usersForRetryPayment=[{}]", usersForRetryPayment);
		return usersForRetryPayment;
	}

    @Transactional(propagation = Propagation.REQUIRED)
    public User downgradeUserTariff(User userWithOldTariff, Tariff newTariff) {

        Tariff oldTariff = userWithOldTariff.getTariff();
        if (_4G.equals(oldTariff) && _3G.equals(newTariff)) {
            if (userWithOldTariff.isOn4GVideoAudioBoughtPeriod()) {
                LOGGER.info("Attempt to unsubscribe user and skip Video Audio bought period (old nextSubPayment = [{}]) because of tariff downgraded from [{}] Video Audio Subscription to [{}] ", userWithOldTariff.getNextSubPayment(), oldTariff, newTariff);
                userWithOldTariff = skipBoughtPeriodAndUnsubscribe(userWithOldTariff, USER_DOWNGRADED_TARIFF);
                
                userServiceNotification.sendSmsFor4GDowngradeForSubscribed( userWithOldTariff );
            } else if (userWithOldTariff.isOnVideoAudioFreeTrial()) {
                LOGGER.info("Attempt to unsubscribe user, skip Free Trial and apply O2 Potential Promo because of tariff downgraded from [{}] Free Trial Video Audio to [{}]", oldTariff, newTariff);
                userWithOldTariff = downgradeUserOn4GFreeTrialVideoAudioSubscription(userWithOldTariff);
                
                userServiceNotification.sendSmsFor4GDowngradeForFreeTrial( userWithOldTariff );
            } else if(userWithOldTariff.has4GVideoAudioSubscription()){
                LOGGER.info("Attempt to unsubscribe user subscribed to Video Audio because of tariff downgraded from [{}] Video Audio with old nextSubPayment [{}] to [{}]", oldTariff, userWithOldTariff.getNextSubPayment(), newTariff);
                userWithOldTariff = unsubscribeUser(userWithOldTariff, USER_DOWNGRADED_TARIFF.getDescription());
                
                userServiceNotification.sendSmsFor4GDowngradeForSubscribed( userWithOldTariff );
            }
        } else {
            LOGGER.info("The payment details leaves as is because of old user tariff [{}] isn't 4G or new user tariff [{}] isn't 3G", oldTariff, newTariff);
        }
        return userWithOldTariff;
    }
    
    private User downgradeUserOn4GFreeTrialVideoAudioSubscription(User user) {
        user = unsubscribeAndSkipFreeTrial(user, USER_DOWNGRADED_TARIFF);
        applyPotentialPromo(user.isO2User(), user, user.getUserGroup().getCommunity(), (int) (user.getFreeTrialStartedTimestampMillis() / 1000L));
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User unsubscribeAndSkipFreeTrial(User user, ActionReason actionReason) {
        user = unsubscribeUser(user, actionReason.getDescription());
        user = skipFreeTrial(user);
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User skipBoughtPeriodAndUnsubscribe(User userWithOldTariffOnOldBoughtPeriod, ActionReason actionReason) {
        userWithOldTariffOnOldBoughtPeriod = unsubscribeUser(userWithOldTariffOnOldBoughtPeriod, actionReason.getDescription());
        userWithOldTariffOnOldBoughtPeriod = skipBoughtPeriod(userWithOldTariffOnOldBoughtPeriod, actionReason);
        return userWithOldTariffOnOldBoughtPeriod;
    }

    private User skipBoughtPeriod(User userWithOldTariffOnOldBoughtPeriod, ActionReason actionReason) {
        int epochSeconds = Utils.getEpochSeconds();
        final int nextSubPayment = userWithOldTariffOnOldBoughtPeriod.getNextSubPayment();

        LOGGER.info("Attempt to skip nextSubPayment [{}] by assigning current time [{}]", nextSubPayment, epochSeconds);

        refundService.logSkippedBoughtPeriod(userWithOldTariffOnOldBoughtPeriod, actionReason);

        userWithOldTariffOnOldBoughtPeriod.setNextSubPayment(epochSeconds);

        accountLogService.logAccountEvent(userWithOldTariffOnOldBoughtPeriod.getId(), userWithOldTariffOnOldBoughtPeriod.getSubBalance(), null, null, BOUGHT_PERIOD_SKIPPING, null);
        return userWithOldTariffOnOldBoughtPeriod;
    }

    private User skipFreeTrial(User user){
        int currentTimeSeconds = Utils.getEpochSeconds();
        long currentTimeMillis = currentTimeSeconds * 1000L;

        LOGGER.info("Attempt of skipping free trial. The nextSubPayment [{}] and freeTrialExpiredMillis [{}] will be changed to [{}] and [{}] corresponding", user.getNextSubPayment(), user.getFreeTrialExpiredMillis(), currentTimeSeconds, currentTimeMillis);

        user.setNextSubPayment(currentTimeSeconds);
        user.setFreeTrialExpiredMillis(currentTimeMillis);

        accountLogService.logAccountEvent(user.getId(), user.getSubBalance(), null, null, TRIAL_SKIPPING, null);

        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User o2SubscriberDataChanged(User user, O2SubscriberData o2SubscriberData) {
        Tariff newTariff = o2SubscriberData.isTariff4G() ? _4G : _3G;
        if (!newTariff.equals(user.getTariff())) {
            if (user.isOnWhiteListedVideoAudioFreeTrial()) LOGGER.info("User will not be downgraded because of he on white listed Video Audio Free Trial");
            else {
                LOGGER.info("tariff changed [{}] to [{}]", user.getTariff(), newTariff);
                user = downgradeUserTariff(user, newTariff);
            }
        }
        o2UserDetailsUpdater.setUserFieldsFromSubscriberData(user, o2SubscriberData);
        return userRepository.save(user);
    }
    
    public boolean isPromotedDevice(String phoneNumber) {
		boolean isPromoted = false;
		try {
			isPromoted = deviceService.isPromotedDevicePhone(
					communityService.getCommunityByName("o2"),
					phoneNumber,
					null);
		} catch ( Exception e ) {
			LOGGER.error("", e);
		}
		LOGGER.info("isPromotedDevice('{}')={}", phoneNumber, isPromoted);
		
		return isPromoted;
	}

    public boolean isVFNZOtacValid(String otac, String phoneNumber, Community community) {
        return userRepository.findByOtacMobileAndCommunity(otac, phoneNumber, community)==0L ? false: true;
    }
}
