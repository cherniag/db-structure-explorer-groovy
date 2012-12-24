package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType;
import mobi.nowtechnologies.common.util.ServerMessage;
import mobi.nowtechnologies.server.assembler.UserAsm;
import mobi.nowtechnologies.server.persistence.dao.*;
import mobi.nowtechnologies.server.persistence.dao.PaymentDao.TxType;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.FacebookService.UserCredentions;
import mobi.nowtechnologies.server.service.exception.*;
import mobi.nowtechnologies.server.service.payment.MigPaymentService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.service.util.PaymentDetailsValidator;
import mobi.nowtechnologies.server.service.util.UserRegInfoValidator;
import mobi.nowtechnologies.server.shared.AppConstants;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.UserDetailsDto;
import mobi.nowtechnologies.server.shared.dto.UserFacebookDetailsDto;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import mobi.nowtechnologies.server.shared.dto.web.AccountDto;
import mobi.nowtechnologies.server.shared.dto.web.ContentOfferDto;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;
import mobi.nowtechnologies.server.shared.dto.web.UserRegDetailsDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.UnsubscribeDto;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.TransactionType;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.util.PhoneNumberValidator;
import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static mobi.nowtechnologies.server.shared.AppConstants.CURRENCY_GBP;
import static mobi.nowtechnologies.server.shared.Utils.getBigRandomInt;
import static org.apache.commons.lang.Validate.notNull;

/**
 * UserService
 * 
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 * @author Maksym Chernolevskyi (maksym)
 */
public class UserService {
	private static final String PAYD_CC_ERROR = "payd.cc.error";
	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserService.class);

	@Deprecated
	public static class AmountCurrencyWeeks {
		BigDecimal amount;
		String currency;
		byte weeks;

		public AmountCurrencyWeeks(BigDecimal amount, String currency, byte weeks) {
			this.amount = amount;
			this.currency = currency;
			this.weeks = weeks;
		}

		public byte getWeeks() {
			return weeks;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public String getCurrency() {
			return currency;
		}

		@Override
		public String toString() {
			return "AmountCurrencyWeeks [amount=" + amount + ", currency="
					+ currency + ", weeks=" + weeks + "]";
		}

	}

	private UserDao userDao;
	private EntityService entityService;
	private CountryAppVersionService countryAppVersionService;
	private DeviceTypeService deviceTypeService;
	private CountryService countryService;
	private SagePayService sagePayService;
	// private MigService migService;
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
	//private NowTechTokenBasedRememberMeServices ipTokenBasedRememberMeServices;
	private FacebookService facebookService;
	private DeviceService deviceService;
	private OfferService offerService;
	private DrmService drmService;
	private AccountLogService accountLogService;
	private UserRepository userRepository;
	private O2ClientService o2ClientService;
	
	public void setO2ClientService(O2ClientService o2ClientService) {
		this.o2ClientService = o2ClientService;
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

	public void setSagePayService(SagePayService sagePayService) {
		this.sagePayService = sagePayService;
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

	@Deprecated
	public User checkCredentials(String userName, String userToken, String timestamp, String communityName) {
		notNull(userName, "The parameter userName is null");
		notNull(userToken, "The parameter userToken is null");
		notNull(timestamp, "The parameter timestamp is null");
		User user = findByNameAndCommunity(userName, communityName);

		if (user != null) {
			String localUserToken = Utils.createTimestampToken(user.getToken(), timestamp);
			String deviceUserToken = Utils.createTimestampToken(user.getTempToken(), timestamp);
			if (localUserToken.equalsIgnoreCase(userToken) || deviceUserToken.equalsIgnoreCase(userToken)) {
				PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
				if (null == currentPaymentDetails && user.getStatus().getI() == UserStatusDao.getEulaUserStatus().getI())
					LOGGER.info("The user [{}] coudn't login in while he has no payment details and he is in status [{}]",
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
		LOGGER.debug("input parameters userName, userToken, timestamp, communityName, deviceUID: [{}], [{}], [{}], [{}], [{}]", new Object[]{userName, userToken, timestamp, communityName,
				deviceUID});
		User user = checkCredentials(userName, userToken, timestamp, communityName);
		final String foundDeviceUID = user.getDeviceUID();
		if (foundDeviceUID != null && !deviceUID.equalsIgnoreCase(foundDeviceUID)) {//return user info only if foundDeviceUID is null or deviceUID and foundDeviceUID are equals
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

	@Deprecated
	public boolean userCanLogin(String userName, String storedToken,
			String communityName) {
		if (userName == null)
			throw new ServiceException("The parameter userName is null");
		if (storedToken == null)
			throw new ServiceException("The parameter storedToken is null");
		if (communityName == null)
			throw new NullPointerException(
					"The parameter communityName is null");
		boolean userExists = userExists(userName, communityName);
		if (!userExists) {
			LOGGER.info(
					"Login failed. Couldn't find user '{}' for community '{}' in the database",
					userName, communityName);
			return false;
		}

		boolean areTheStoredTokenTheSame = isUserExist(userName, storedToken, communityName);
		if (areTheStoredTokenTheSame)
			return true;
		else {
			LOGGER.info("Login failed. The stored token in the database for user '{}' in community {} is other than was passed '{}'",
					new String[] { userName, communityName, storedToken });
			return false;
		}
	}

	@Deprecated
	public boolean isUserExist(String userName, String storedToken, String communityName) {
		return userDao.userExists(userName,
                storedToken, communityName);
	}

	@Deprecated
	public boolean userExists(String userName, String communityName) {
		return userDao.userExists(userName, communityName);
	}

	@Deprecated
	public boolean facebookUserExists(String facebookId, String communityName) {
		return userDao.facebookserExists(facebookId, communityName);
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
		AccountCheckDTO accountCheck = proceessAccountCheckCommandForAuthorizedUser(userId, null, null);
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

	private void validateUserName(String userName, String communityName) {
		if (userName == null)
			throw new ServiceException(
					"The parameter userName is null");
		if (communityName == null)
			throw new ServiceException(
					"The parameter communityName is null");
		boolean userExists = userExists(userName, communityName);
		if (userExists) {
			LOGGER.error("The user [{}] is already present in community [{}]",
					userName, communityName);
			throw new ServiceException(MessageFormat.format(
					"The user [{0}] is already present", userName));
		}
	}

	public User findByNameAndCommunity(String userName, String communityName) {
		LOGGER.debug("input parameters userName, communityName: [{}], [{}]", userName, communityName);
		User user = userDao.findByNameAndCommunity(userName, communityName);
		LOGGER.debug("Output parameter user=[{}]", user);
		return user;
	}

	// private boolean userGroupIsInCommuntiy(byte userGroup, String
	// communityName) {
	// return communityName.equals(userDao
	// .getCommunityNameByUserGroup(userGroup));
	// }

	@Transactional(propagation = Propagation.REQUIRED)
	public synchronized AccountCheckDTO applyPromotionByPromoCode(User user, Promotion promotion) {
		LOGGER.debug("input parameters user, promotion: [{}], [{}], [{}]", new Object[] { user, promotion });
		if (promotion != null) {

			int nextSubPayment = Utils.getEpochSeconds()+ promotion.getFreeWeeks() * Utils.WEEK_SECONDS;
			user.setNextSubPayment(nextSubPayment);
			user.setFreeTrialExpiredMillis(new Long(nextSubPayment*1000L));

			final PromoCode promoCode = promotion.getPromoCode();
			user.setPotentialPromoCodePromotion(null);

			user.setStatus(UserStatusDao.getSubscribedUserStatus());
			user.setFreeTrialStartedTimestampMillis(Utils.getEpochMillis());
			user = entityService.updateEntity(user);

			promotion.setNumUsers(promotion.getNumUsers() + 1);
			promotion = entityService.updateEntity(promotion);
			AccountLog accountLog = new AccountLog(user.getId(), null, (byte) (user.getSubBalance() + promotion.getFreeWeeks()),
					TransactionType.PROMOTION_BY_PROMO_CODE_APPLIED);
			accountLog.setPromoCode(promoCode.getCode());
			entityService.saveEntity(accountLog);
			for (byte i = 1; i <= promotion.getFreeWeeks(); i++) {
				entityService.saveEntity(new AccountLog(user.getId(), null, (byte) (user.getSubBalance() + promotion.getFreeWeeks() - i),
						TransactionType.SUBSCRIPTION_CHARGE));
			}
			return proceessAccountCheckCommandForAuthorizedUser(user.getId(), null, null);
		}
		throw new IllegalArgumentException("No promotion found");
	}

	private void setPaymentStatusAccoringToPaymentType(User user) {
		if (user == null)
			throw new ServiceException("The parameter user is null");

		String paymentType = user.getPaymentType();
		if (paymentType.equals(UserRegInfoServer.PaymentType.PREMIUM_USER))
			user.setPaymentStatus(PaymentStatusDao.getPIN_PENDING().getId());
		else if (paymentType.equals(UserRegInfoServer.PaymentType.CREDIT_CARD) ||
				paymentType.equals(UserRegInfoServer.PaymentType.FREEMIUM) ||
				paymentType.equals(UserRegInfoServer.PaymentType.PAY_PAL) ||
				paymentType.equals(UserRegInfoServer.PaymentType.UNKNOWN))
			user.setPaymentStatus(PaymentStatusDao.getNULL().getId());
		else
			throw new ServiceException("Unknown payment type: [" + paymentType
					+ "]");
	}

	/*
	 * private void sendPinToUser(String communityName, String paymentType, int operator, String mobile, User user, String pin) { if (communityName == null) throw new
	 * ServiceException("The parameter communityName is null"); if (paymentType == null) throw new ServiceException("The parameter paymentType is null"); if (mobile == null) throw new
	 * ServiceException("The parameter mobile is null"); if (user == null) throw new ServiceException("The parameter user is null"); if (pin == null) throw new
	 * ServiceException("The parameter pin is null");
	 * 
	 * LOGGER .debug( "input parameters communityName, paymentType, operator, mobile, user, pin: [{}],[{}],[{}],[{}],[{}],[{}]" , new Object[] { communityName, paymentType, operator, mobile, user, pin
	 * });
	 * 
	 * Community community = CommunityDao.getMapAsNames().get(communityName);
	 * 
	 * PaymentPolicy paymentPolicy = paymentPolicyService.getPaymentPolicy(operator, paymentType, community.getId());
	 * 
	 * Locale locale = null;
	 * 
	 * String message = messageSource.getMessage(AppConstants.SMS_FREE_MSG, new Object[] { community.getDisplayName(), paymentPolicy.getSubcost(), paymentPolicy.getSubweeks(),
	 * paymentPolicy.getShortCode(), pin }, locale);
	 * 
	 * migService.sendFreeSms("" + getBigRandomInt(), operator, mobile, message); user.setPaymentStatus(PaymentStatus.PIN_PENDING_CODE); user.setPin(pin); }
	 */
	public void updateMobile(User user, String mobile, Integer operator, String communityName) {
		if (communityName == null)
			throw new NullPointerException(
					"The parameter communityName is null");
		PhoneNumberValidator.validate(mobile);

		if (!Operator.getMapAsIds().containsKey(operator))
			throw new ServiceException("Uknown operator parameter value: ["
					+ operator + "]");

		Community community = CommunityDao.getMapAsNames().get(communityName);
		PaymentPolicy paymentPolicy = paymentPolicyService.getPaymentPolicy(operator, PaymentType.PREMIUM_USER, community.getId());

		String migPhone = convertPhoneNumberFromGreatBritainToInternationalFormat(mobile);
		migPaymentService.createPaymentDetails(getMigPhoneNumber(operator, migPhone), user, community, paymentPolicy);
		/*
		 * if (mobile!=null&&mobile.startsWith(MigService._0044)) user .setMobile(MigService .convertPhoneNumberFromInternationalToGreatBritainFormat(mobile)); else user.setMobile(mobile);
		 * user.setOperator(operator); user.setPaymentStatus(PaymentStatusDao.getPIN_PENDING().getId()); updateUser(user);
		 * 
		 * sendPinToUser(communityName, user.getPaymentType(), user.getOperator(), user.getMobile(), user, user.getPin());
		 */
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePaymentDetails(User user, UserRegInfo userRegInfo) {

		if (userRegInfo == null)
			throw new ServiceException("The parameter userRegInfo is null");
		if (user == null)
			throw new ServiceException("The parameter user is null");

		Locale locale = null;

		PaymentDetailsValidator.validate(userRegInfo);

		Community community = CommunityDao.getMapAsNames().get(userRegInfo.getCommunityName());
		user = findById(user.getId());
		createPaymentDetails(userRegInfo, user, community);
		/*
		 * String paymentType = userRegInfo.getPaymentType(); if (paymentType.equals(UserRegInfoServer.PaymentType.PAY_PAL)) throw new ServiceException(
		 * "Coudn't update payment details with current payment type. It can't be [" + UserRegInfoServer.PaymentType.PAY_PAL + "]");
		 * 
		 * int paymentStatus = user.getPaymentStatus(); boolean isUserInPaymentProcessing = isUserInPaymentProcessing(user); if (isUserInPaymentProcessing){
		 * 
		 * ServiceException serviceException = new ServiceException("User has incorrect status [" + PaymentStatusDao.getMapIdAsKey().get(paymentStatus) + "] to update payment details");
		 * 
		 * String localizedMessage = messageSource.getMessage(PAYD_CC_ERROR, null, locale);
		 * 
		 * serviceException.setLocalizedMessage(localizedMessage);
		 * 
		 * throw serviceException; } String token = null; Payment payment = null; if (paymentType.equals(UserRegInfoServer.PaymentType.CREDIT_CARD)){ try { payment = validateCreditCard(user.getId(),
		 * userRegInfo); token = payment.getExternalTxCode(); } catch (SagePayException e) { LOGGER.error(e.getMessage(), e); user.setPaymentEnabled(false); updateUser(user); throw e; } }
		 * 
		 * user.setPaymentType(paymentType);
		 * 
		 * if (!paymentType.equals(UserRegInfoServer.PaymentType.FREEMIUM)) user.setPaymentEnabled(true);
		 * 
		 * String phoneNumber = userRegInfo.getPhoneNumber(); if (phoneNumber == null) userRegInfo.setPhoneNumber("");
		 * 
		 * user.setPin(""); String pin = "" + Utils.generateRandomPIN();
		 * 
		 * if (paymentType.equals(UserRegInfoServer.PaymentType.PREMIUM_USER)) { user.setOperator(userRegInfo.getOperator()); user.setMobile(userRegInfo.getPhoneNumber());
		 * 
		 * sendPinToUser(userRegInfo.getCommunityName(), userRegInfo.getPaymentType(), userRegInfo.getOperator(), phoneNumber, user, pin);
		 * 
		 * user.setPaymentStatus(PaymentStatusDao.getPIN_PENDING().getId()); } else if (paymentType .equals(UserRegInfoServer.PaymentType.CREDIT_CARD) && paymentStatus !=
		 * PaymentStatusDao.getNULL().getId() && user.getStatus() != UserStatus.EULA.getCode() && user.getSubBalance() == 0) user.setPaymentStatus (PaymentStatusDao.getAWAITING_PAYMENT().getId());
		 * else{ user.setPaymentStatus(PaymentStatusDao.getNULL().getId()); }
		 * 
		 * saveOrUpdateUserWithPaymentDetails(user, token, userRegInfo);
		 * 
		 * if (paymentType.equals(UserRegInfoServer.PaymentType.CREDIT_CARD)) saveInitialPayment(payment, user.getId());
		 */
	}

	public boolean isUserInPaymentProcessing(User user) {
		if (user == null)
			throw new NullPointerException("The parameter user is null");
		int paymentStatus = user.getPaymentStatus();
		return (paymentStatus == PaymentStatusDao.getAWAITING_PAYMENT().getId()
				|| paymentStatus == PaymentStatusDao.getAWAITING_PAY_PAL()
						.getId()
				|| paymentStatus == PaymentStatusDao.getAWAITING_PSMS().getId() || paymentStatus == PaymentStatusDao
				.getAWAITING_PAY_PAL().getId());
	}

	public void saveInitialPayment(Payment payment, int userId) {
		payment.setUserUID(userId);
		entityService.saveEntity(payment);
	}

	public Payment validateCreditCard(int userId, UserRegInfo userRegInfo) {
		if (userRegInfo == null)
			throw new ServiceException("userRegInfo is null");

		LOGGER.debug("input parameters userId, userRegInfo: [{}], [{}]",
				new Object[] { userId, userRegInfo });

		byte communityId = CommunityDao.getMapAsNames().get(userRegInfo.getCommunityName()).getId();
		String paymentType = userRegInfo.getPaymentType();

		PaymentPolicy paymentPolicy = paymentPolicyService.getPaymentPolicy(0, paymentType, communityId);

		BigDecimal amount = paymentPolicy.getSubcost();
		try {
			Payment pendingPayment = paymentService.createPendingPayment(userId, userRegInfo.getEmail(), userRegInfo.getCommunityName(), 0,
					UserRegInfoServer.PaymentType.CREDIT_CARD);
			String vendorTxCode = "DFRD" + getBigRandomInt();
			pendingPayment.setTxType(TxType.DEFERRED.getCode());
			pendingPayment.setInternalTxCode(vendorTxCode);
			pendingPayment.setSubweeks(paymentPolicy.getSubweeks());
			// entityService.saveEntity(pendingPayment);
			Payment payment = sagePayService.makeDeferredPayment(userRegInfo, amount, paymentPolicy.getSubweeks(),
					CURRENCY_GBP, "Authenticate user card", vendorTxCode);
			// entityService.removeEntity(Payment.class, pendingPayment.getI());

			LOGGER.debug("Output parameter payment=[{}]", payment);
			return payment;
		} catch (SagePayException e) {
			LOGGER.error(e.getMessage(), e);
			Payment payment = e.getFailedPayment();
			payment.setUserUID(userId);
			entityService.saveEntity(payment);
			throw e;
		}
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

	public User updateUser(User user) {
		return entityService.updateEntity(user);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User mergeUser(User user, User userByDeviceUID) {
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
		Promotion promotion = userDao.getActivePromotion(user.getUserGroupId());
		LOGGER.info("promotion [{}]", promotion);
		if (promotion != null) {
			user.setSubBalance((byte) (user.getSubBalance() + promotion.getFreeWeeks()));
			entityService.updateEntity(user);
			promotion.setNumUsers(promotion.getNumUsers() + 1);
			entityService.updateEntity(promotion);
			entityService.saveEntity(
					new AccountLog(user.getId(), null, user.getSubBalance(),
							TransactionType.PROMOTION));
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void unsubscribeUser(String phoneNumber, String operatorMigName) {
		List<MigPaymentDetails> migPaymentDetails = paymentDetailsService.findMigPaymentDetails(operatorMigName, phoneNumber);
		LOGGER.info("Trying to unsubscribe {} user(s) having {} as mobile number", migPaymentDetails.size(), phoneNumber);
		for (MigPaymentDetails migPaymentDetail : migPaymentDetails) {
			migPaymentDetail.setActivated(false);
			migPaymentDetail.setDisableTimestampMillis(System.currentTimeMillis());
			entityService.updateEntity(migPaymentDetail);
			LOGGER.info("Mig phone number {} was successfuly unsubscribed", migPaymentDetail.getMigPhoneNumber());
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User unsubscribeUser(int userId, UnsubscribeDto dto) {
		LOGGER.debug("input parameters userId, dto: [{}], [{}]", userId, dto);
		User user = entityService.findById(User.class, userId);
		final String reason = dto.getReason();
		user = unsubscribeUser(user, reason);
		LOGGER.info("Output parameter user=[{}]", user);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User unsubscribeUser(User user, final String reason) {
		LOGGER.debug("input parameters user, reason: [{}], [{}]", user, reason);
		if (user == null)
			throw new NullPointerException("The parameter user is null");
		
		user.setPaymentEnabled(false);
		
		user = paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, reason);
		
		user = entityService.updateEntity(user);
		LOGGER.info("Output parameter user=[{}]", user);
		return user;
	}

	protected List<User> findUser(String mobile, String operatorMigName) {
		Map<String, Object> fieldNameValueMap = new HashMap<String, Object>();
		fieldNameValueMap.put(User.Fields.mobile.toString(), mobile);
		// fieldNameValueMap.put(User.Fields.operator.toString(),
		// Operator.getMapAsMigNames().get(operatorMigName).getName());
		fieldNameValueMap.put(User.Fields.paymentType.toString(), "PSMS");
		fieldNameValueMap.put(User.Fields.paymentEnabled.toString(), true);
		return entityService.findListByProperties(User.class, fieldNameValueMap);
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

	public List<PaymentPolicy> getPaymentPoliciesForPasseadOperatorOrOperatorIs0(
			String communityName, String paymentType, final int operator) {
		if (communityName == null)
			throw new ServiceException("The parameter communityName is null");
		if (paymentType == null)
			throw new ServiceException("The parameter paymentType is null");

		return userDao.getPaymentPoliciesForPasseadOperatorOrOperatorIs0(
				communityName, paymentType, operator);
	}

	public List<Integer> getOperatorsAccordingToPaymentPoliciesForPremiumUser(
			String communityName, String paymentType) {
		if (communityName == null)
			throw new ServiceException("The parameter communityName is null");
		if (paymentType == null)
			throw new ServiceException("The parameter paymentType is null");

		return userDao.getOperatorsAccordingToPaymentPolicies(communityName, paymentType);
	}

	public AmountCurrencyWeeks getUpdateAmountCurrencyWeeks(User user) {
		if (user == null)
			throw new ServiceException("The parameter user is null");
		LOGGER.debug("input parameters user: [{}]", new Object[] { user });

		String communityName = userDao.getCommunityNameByUserGroup(user.getUserGroupId());
		Community community = CommunityDao.getMapAsNames().get(communityName);

		PaymentPolicy paymentPolicy = paymentPolicyService.getPaymentPolicy(user.getOperator(), user.getPaymentType(), community.getId());
		LOGGER.info("paymentPolicy {}", paymentPolicy);
		AmountCurrencyWeeks amountCurrencyWeeks = new AmountCurrencyWeeks(paymentPolicy.getSubcost(),
				CURRENCY_GBP, paymentPolicy.getSubweeks());

		LOGGER.debug("Output parameter amountCurrencyWeeks=[{}]",
				amountCurrencyWeeks);
		return amountCurrencyWeeks;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void makeUserActive(User user) {
		if (user == null)
			throw new ServiceException("The parameter user is null");
		user.setLastDeviceLogin(Utils.getEpochSeconds());
		updateUser(user);
	}

	/**
	 * Deprecated due to new portal implementation Use sendSMSWithOTALink(String phone, int userId)
	 * 
	 * @param user
	 */
	@Deprecated
	public void sendSMSWithOTALink(User user) {
		if (user == null)
			throw new ServiceException("The parameter user is null");

		String[] args = { migHttpService.getOtaUrl() + "&CODE=" + user.getCode() };
		String migPhone = convertPhoneNumberFromGreatBritainToInternationalFormat(user.getMobile());
		migHttpService.makeFreeSMSRequest(getMigPhoneNumber(user.getOperator(), migPhone),
                messageSource.getMessage(user.getUserGroup().getCommunity().getRewriteUrlParameter(), "sms.otalink.text", args, null));
	}

	public User findById(int id) {
		return entityService.findById(User.class, id);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePaymentTypeToPayPal(int userId) {
		User user = findById(userId);
		int paymentStatus = user.getPaymentStatus();
		boolean isUserInPaymentProcessing = isUserInPaymentProcessing(user);
		if (isUserInPaymentProcessing)
			throw new ServiceException("User has incorrect status ["
					+ PaymentStatusDao.getMapIdAsKey().get(paymentStatus)
					+ "] to update payment details");
		user.setPaymentType(UserRegInfo.PaymentType.PAY_PAL);
		user.setPaymentEnabled(true);

		updateUser(user);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User changePassword(Integer userId, String newPassword) {
		LOGGER.debug("input parameters changePassword(Integer userId, String newPassword): [{}], [{}]", new Object[] { userId, newPassword });
				
		User user = findById(userId);
		
		String storedToken = Utils.createStoredToken(user.getUserName(), newPassword);

		userRepository.updateFields(storedToken, userId);
		
		LOGGER.debug("output parameters changePassword(Integer userId, String newPassword): [{}]", new Object[] { user });
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
		// user.setSubBalance((byte)0);
		// user.setFreeBalance((byte)0);
		user.setToken(userRegInfo.getStoredToken());
		user.setDeviceType(DeviceTypeDao.getDeviceTypeMapIdAsKeyAndDeviceTypeValue().get(deviceTypeId));
		user.setDeviceString(userRegInfo.getDeviceString());
		user.setDevice("");
		byte communityId = CommunityDao.getCommunityId(communityName);
		user.setUserGroup(UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(communityId));
		// user.setUserType(userType); TODO is it right?
		// user.setLastDeviceLogin(lastDeviceLogin);
		// user.setLastWebLogin(lastWebLogin);
		// user.setNextSubPayment(nextSubPayment);
		// user.setLastPaymentTx(lastPaymentTx);
		user.setAddress1(userRegInfo.getAddress());
		user.setAddress2(userRegInfo.getAddress());// @TODO is it OK?
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

		user.setPaymentEnabled(false);

		user.setOperator(userRegInfo.getOperator());
		// TODO operator should not be a primitive type
		if (0 == user.getOperator()) {
			Entry<Integer, Operator> entry = OperatorDao.getMapAsIds().entrySet().iterator().next();
			user.setOperator(entry.getKey());
		}

		LOGGER.debug("Output parameter user=[{}]", user);
		return user;

	}

	private void updateUserOTALink(User user, String code) {
		if (user == null)
			throw new NullPointerException("The parameter user is null");
		if (code == null)
			throw new NullPointerException("The parameter code is null");

		LOGGER.debug("input parameters user: [{}], code: [{}] ", new Object[] { user, code });

		user.setCode(code);
	}

	@Deprecated
	private boolean deleteUserInPinPendingPaymentStatus(User user) {
		if (user == null)
			throw new ServiceException("The parameter user is null");

		LOGGER.debug("input parameters user: [{}]", new Object[] { user });

		boolean deleted = false;
		if (PaymentStatusDao.getPIN_PENDING().getId() == user
				.getPaymentStatus()) {
			LOGGER
					.info(
							"Removing user [{}] in PIN_PENDING status during registration",
							user.getId());
			entityService.removeEntity(User.class, user.getId());
			deleted = true;
		}

		LOGGER.debug("Output parameter user=[{}]", user);
		return deleted;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	private User continueRegistration(UserRegInfo userRegInfo) {
		if (userRegInfo == null)
			throw new ServiceException("The parameter userRegInfo is null");

		LOGGER.debug("input parameters userRegInfo: [{}]",
				new Object[] { userRegInfo });

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

	/*
	 * @Deprecated private void continueRegistrationAccordingToPaymentType(final User user, final UserRegInfo userRegInfo, final String communityName) { if (user == null) throw new ServiceException(
	 * "The parameter user is null"); if (userRegInfo == null) throw new ServiceException( "The parameter userRegInfo is null"); if (communityName == null) throw new ServiceException(
	 * "The parameter communityName is null"); LOGGER.debug( "input parameters user, promotionCode, communityName: [{}], [{}], [{}]", new Object[] { user, userRegInfo, communityName });
	 * 
	 * Payment payment = null;
	 * 
	 * String paymentType = userRegInfo.getPaymentType(); PaymentDetailsValidator.validate(userRegInfo);
	 * 
	 * String token = null; if (paymentType.equals(UserRegInfoServer.PaymentType.CREDIT_CARD)) { payment = validateCreditCard(0, userRegInfo); token = payment.getExternalTxCode();
	 * user.setPaymentEnabled(true); } user.setPaymentType(paymentType);
	 * 
	 * setPaymentStatusAccoringToPaymentType(user);
	 * 
	 * user.setStatus(UserStatus.EULA.code); String pin = String.valueOf(Utils.generateRandomPIN());
	 * 
	 * if (paymentType.equals(UserRegInfoServer.PaymentType.PREMIUM_USER)) { sendPinToUser(userRegInfo.getCommunityName(), userRegInfo .getPaymentType(), userRegInfo.getOperator(),
	 * userRegInfo.getPhoneNumber(), user, pin); }
	 * 
	 * if (!paymentType.equals(UserRegInfoServer.PaymentType.UNKNOWN)) saveOrUpdateUserWithPaymentDetails(user, token, userRegInfo); else entityService.saveEntity(user); updateUserOTALink(user,
	 * Utils.getOTACode(user.getId(), user.getUserName()));
	 * 
	 * // if (paymentType.equals(UserRegInfoServer.PaymentType.CREDIT_CARD)) // saveInitialPayment(payment, user.getId()); }
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	private void applyPromotionByPromoCode(final User user, final String promotionCode) {
		if (user == null)
			throw new ServiceException(
					"The parameter user is null");
		if (promotionCode == null)
			throw new ServiceException(
					"The parameter promotionCode is null");
		LOGGER.debug(
				"input parameters user, promotionCode, communityName: [{}], [{}]",
				new Object[] { user, promotionCode });

		Promotion userPromotion = promotionService.getActivePromotion(promotionCode, user.getUserGroup().getCommunity().getName());
		if (userPromotion == null) {
			LOGGER.info("Promotion code [{}] does not exist", promotionCode);
			throw new ServiceException(
					"Invalid promotion code. Please re-enter the code or leave the field blank");
		}

		applyPromotionByPromoCode(user, userPromotion);
	}

	// TODO remove this method
	@Deprecated
	void saveOrUpdateUserWithPaymentDetails(final User user,
			final String token, final UserRegInfo userRegInfo) {
		/*
		 * if (user == null) throw new ServiceException("The parameter user is null"); if (userRegInfo == null) throw new ServiceException("The parameter userRegInfo is null");
		 * 
		 * LOGGER.debug( "input parameters user, token, userRegInfo: [{}], [{}], [{}]", new Object[] { user, token, userRegInfo }); String paymentType = userRegInfo.getPaymentType();
		 * 
		 * byte communtyId = CommunityDao.getMapAsNames().get( userRegInfo.getCommunityName()).getId();
		 * 
		 * final boolean isUserAlreadyExsist = (user.getId() != 0);
		 * 
		 * int operatorId = userRegInfo.getOperator(); PaymentPolicy paymentPolicy = paymentPolicyService.getPaymentPolicy( operatorId, paymentType, communtyId);
		 * 
		 * final Operator operator = Operator.getMapAsIds().get(operatorId); String phoneNumber = userRegInfo.getPhoneNumber();
		 * 
		 * PaymentDetails paymentDetails; if (!isUserAlreadyExsist) { paymentDetails = PaymentDetailsService.getPaymentDetails( paymentType, token, operator, phoneNumber);
		 * entityService.saveEntity(user); } else { paymentDetails = getPaymentDetailsForUserUpdate(user, token, paymentType, operator, phoneNumber); entityService.updateEntity(user); }
		 * 
		 * paymentDetails.setPaymentPolicy(paymentPolicy);
		 * 
		 * if (paymentDetails.getI() != null) entityService.updateEntity(paymentDetails); else entityService.saveEntity(paymentDetails);
		 * 
		 * user.setCurrentPaymentDetails(paymentDetails); entityService.updateEntity(user);
		 */
	}

	// PaymentDetails getPaymentDetailsForUserUpdate(final User user,
	// final String token, String paymentType, final Operator operator,
	// String phoneNumber) {
	// if (user == null)
	// throw new ServiceException("The parameter user is null");
	// if (paymentType == null)
	// throw new ServiceException("The parameter paymentType is null");
	//
	// LOGGER
	// .debug(
	// "input parameters user, token, paymentType, operator, phoneNumber: [{}], [{}], [{}], [{}], [{}]",
	// new Object[] { user, token, paymentType, operator,
	// phoneNumber });
	//
	// PaymentDetails paymentDetails = paymentDetailsService
	// .findPaymentDetails(paymentType, user.getId());
	// if (paymentDetails == null)
	// paymentDetails = PaymentDetailsService.getPaymentDetails(
	// paymentType, token, operator, phoneNumber);
	// else
	// paymentDetails = PaymentDetailsService.populatePaymentDetails(
	// paymentDetails, paymentType, token, operator, phoneNumber);
	//
	// LOGGER.debug("Output parameter paymentDetails=[{}]", paymentDetails);
	// return paymentDetails;
	// }

	// TODO remove this method
	@Deprecated
	PaymentDetails getPaymentDetailsForUserUpdate(final User user,
			final String token, String paymentType, final Operator operator,
			String phoneNumber) {
		if (user == null)
			throw new ServiceException("The parameter user is null");
		if (paymentType == null)
			throw new ServiceException("The parameter paymentType is null");

		LOGGER
				.debug(
                        "input parameters user, token, paymentType, operator, phoneNumber: [{}], [{}], [{}], [{}], [{}]",
                        new Object[]{user, token, paymentType, operator,
                                phoneNumber});

		PaymentDetails paymentDetails = user.getCurrentPaymentDetails();

		if (paymentDetails == null)
			paymentDetails = PaymentDetailsService.getPaymentDetails(
					paymentType, token, operator, phoneNumber);
		else {
			entityService.removeEntity(PaymentDetails.class, paymentDetails.getI());
			paymentDetails = PaymentDetailsService.getPaymentDetails(
					paymentType, token, operator, phoneNumber);
		}
		LOGGER.debug("Output parameter paymentDetails=[{}]", paymentDetails);
		return paymentDetails;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void processPaymentSubBalanceCommand(User user, int subweeks, SubmittedPayment payment) {
		LOGGER.debug("processPaymentSubBalanceCommand input parameters user, subweeks, payment: [{}]", new Object[] { user, subweeks, payment });
		// Update last Successful payment time
		user.setLastSuccessfulPaymentTimeMillis(System.currentTimeMillis());
		// Update user balance
		user.setSubBalance(user.getSubBalance() + subweeks);

		entityService.saveEntity(new AccountLog(user.getId(), payment, user.getSubBalance(), TransactionType.CARD_TOP_UP));
		// The main idea is that we do pre-payed service, this means that
		// in case of first payment or after LIMITED status we need to decrease subBalance of user immediately
		if (UserStatusDao.getLimitedUserStatus().getI() == user.getStatus().getI() || UserStatusDao.getEulaUserStatus().getI() == user.getStatus().getI()) {
			user.setSubBalance(user.getSubBalance() - 1);
			entityService.saveEntity(new AccountLog(user.getId(), payment, user.getSubBalance(), TransactionType.SUBSCRIPTION_CHARGE));
		}

		// Update next sub payment time
		user.setNextSubPayment(Utils.getNewNextSubPayment(user.getNextSubPayment()));

		// Update user status to subscribed
		user.setStatus(UserStatusDao.getSubscribedUserStatus());

		entityService.updateEntity(user);

		LOGGER.info("User {} with balance {}", user.getId(), user.getSubBalance());
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
	public AccountCheckDTO proceessAccountCheckCommandForAuthorizedUser(int userId, String pushNotificationToken, String deviceType) {
		LOGGER.debug("input parameters userId, pushToken,  deviceType: [{}], [{}], [{}]", new String[] { String.valueOf(userId), pushNotificationToken, deviceType });

		User user = userDao.findUserById(userId);
		
		user = assignPotentialPromotion(user);
		user = updateLastDeviceLogin(user);

		if (user.getLastDeviceLogin() == 0)
			makeUserActive(user);

		if (deviceType != null && pushNotificationToken != null)
			userDeviceDetailsService.mergeUserDeviceDetails(user, pushNotificationToken, deviceType);

		AccountCheckDTO accountCheckDTO = user.toAccountCheckDTO(null);

		Community community = user.getUserGroup().getCommunity();
		accountCheckDTO.setPromotedDevice(deviceService.existsInPromotedList(community, user.getDeviceUID()));
		// NextSubPayment stores date of next payment -1 week
		accountCheckDTO.setPromotedWeeks(Weeks.weeksBetween(new DateTime(), new DateTime(user.getNextSubPayment() * 1000L)).getWeeks() + 1);
		
		List<Integer> relatedMediaUIDsByLogTypeList = accountLogService.getRelatedMediaUIDsByLogType(userId, TransactionType.OFFER_PURCHASE);

		accountCheckDTO.setHasOffers(false);
		if (relatedMediaUIDsByLogTypeList.isEmpty()){
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
			String communityName = existingUser.getUserGroup().getCommunity().getName();
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

	// New User service method goes right after this comment. All code above is
	// deprecated

	@Transactional(propagation = Propagation.REQUIRED)
	public User registerUser(UserRegDetailsDto userRegDetailsDto) {
		LOGGER.debug("input parameters userRegDetailsDto: [{}]", userRegDetailsDto);

		final String userName = userRegDetailsDto.getEmail().toLowerCase();

		DeviceType deviceType = DeviceTypeDao.getDeviceTypeMapNameAsKeyAndDeviceTypeValue().get(userRegDetailsDto.getDeviceType());
		if (deviceType == null)
			deviceType = DeviceTypeDao.getNoneDeviceType();

		final String deviceString = userRegDetailsDto.getDeviceString();

		User user = new User();
		user.setUserName(userName);
		user.setToken(Utils.createStoredToken(userName, userRegDetailsDto.getPassword()));

		user.setDeviceType(deviceType);
		if (deviceString != null)
			user.setDeviceString(deviceString);

		Community community = communityService.getCommunityByName(userRegDetailsDto.getCommunityName());
		user.setUserGroup(UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(community.getId()));
		user.setCountry(countryService.findIdByFullName("Great Britain"));
		user.setIpAddress(userRegDetailsDto.getIpAddress());
		user.setCanContact(userRegDetailsDto.isNewsDeliveringConfirmed());
		user.setPaymentEnabled(false);
		Entry<Integer, Operator> entry = OperatorDao.getMapAsIds().entrySet().iterator().next();
		user.setOperator(entry.getKey());
		user.setStatus(UserStatusDao.getEulaUserStatus());
		user.setFacebookId(userRegDetailsDto.getFacebookId());

		String communityName = community.getName();

		String promotionCode = userRegDetailsDto.getPromotionCode();
		if (promotionCode == null)
			promotionCode = getDefaultPromoCode(communityName);

		entityService.saveEntity(user);

		applyPromotionByPromoCode(user, promotionCode);

		LOGGER.debug("Output parameter user=[{}]", user);
		assignPotentialPromotion(user);
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
	public User getUser(String email, String communityUrl) {
		LOGGER.debug("input parameters email, communityUrl: [{}], [{}]", email, communityUrl);
		User user = userDao.findOne(email, communityUrl);
		LOGGER.debug("Output parameter user=[{}]", user);
		return user;
	}

	public void setCommunityService(CommunityService communityService) {
		this.communityService = communityService;
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

	/**
	 * @param email
	 * @param communityRedirectURL
	 * @return true if user exist, and false if not
	 */
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

	/**
	 * Sends an OTA link to users number and return true if sms was accepted by MIG or false otherwise
	 * 
	 * @param phone
	 *            - the phone number entered on the page
	 * @param userId
	 *            - id of the current logged user
	 * @return
	 */
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
	
		if(user == null){
			user = findByNameAndCommunity(userCredentions.getEmail(), passedCommunityName);			
		}
		
		if (user != null && userByDeviceUID != null && user.getId()!=userByDeviceUID.getId()) {
			user.setFacebookId(userCredentions.getId());
			mergeUser(user, userByDeviceUID);
		} else {
			user = userByDeviceUID;
			user.setUserName(userCredentions.getEmail() != null ? userCredentions.getEmail() : userCredentions.getId());
			user.setFacebookId(userCredentions.getId());
			user.setFirstUserLoginMillis(System.currentTimeMillis());
			
			updateUser(user);
		}

		AccountCheckDTO accountCheckDTO = proceessAccountCheckCommandForAuthorizedUser(user.getId(), null, null);
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

	@Transactional(propagation = Propagation.REQUIRED)
	public AccountCheckDTO updateUserDetails(UserDetailsDto userDetailsDto) {
		LOGGER.debug("input parameters userDetailsDto: [{}]", userDetailsDto);

		String deviceUID = userDetailsDto.getDeviceId();
		String password = userDetailsDto.getNewPassword();
		String storedToken = userDetailsDto.getStoredToken();
		final String email = userDetailsDto.getEmail();
		final String passedCommunityName = userDetailsDto.getCommunityName();

		Community community = communityService.getCommunityByName(passedCommunityName);

		User userByDeviceUID = checkUserDetailsBeforeUpdate(deviceUID, storedToken, community);

		User user = findByNameAndCommunity(email, passedCommunityName);
		String newStoredToken = Utils.createStoredToken(email, password);					
				
		if (user != null && userByDeviceUID != null && user.getId()!=userByDeviceUID.getId() && user.getToken().equals(newStoredToken)) {
			mergeUser(user, userByDeviceUID);
		} else if(user == null) {
			user = userByDeviceUID;
			user.setUserName(email);
			user.setToken(newStoredToken);
			user.setFirstUserLoginMillis(System.currentTimeMillis());

			updateUser(user);
		} else if(user.getId()!=userByDeviceUID.getId()){
			ServerMessage serverMessage = ServerMessage.getMessageOnUserExsist(email, passedCommunityName);
			throw new ServiceException(serverMessage);
		}

		AccountCheckDTO accountCheckDTO = proceessAccountCheckCommandForAuthorizedUser(user.getId(), null, null);
		LOGGER.debug("Output parameter accountCheckDTO=[{}]", accountCheckDTO);
		return accountCheckDTO;
	}

	public User findByDeviceUIDAndCommunityRedirectURL(String deviceUID, String communityRedirectUrl) {
		LOGGER.debug("input parameters deviceUID, communityRedirectUrl: [{}], [{}]", deviceUID, communityRedirectUrl);
		User user = userDao.findByDeviceUIDAndCommunityRedirectUrl(deviceUID, communityRedirectUrl);
		LOGGER.debug("Output parameter user=[{}]", user);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkStoredToken(String deviceUID, String communityRedirectUrl, String storedToken) {
		LOGGER.debug("input parameters deviceUID, communityRedirectUrl, storedToken [{}], [{}], [{}]", new Object[] { deviceUID, communityRedirectUrl,
				storedToken });

		Community community = communityService.getCommunityByUrl(communityRedirectUrl);
		UserGroup userGroup = UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(community.getId());

		boolean isValid = userDao.checkStoredToken(deviceUID, userGroup.getI(), storedToken);

		LOGGER.debug("Output parameter isValid=[{}]", isValid);
		return isValid;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AccountCheckDTO registerUser(UserDeviceRegDetailsDto userDeviceRegDetailsDto, boolean createPotentialPromo) {
		LOGGER.debug("input parameters userDeviceRegDetailsDto: [{}]", userDeviceRegDetailsDto);

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

		AccountCheckDTO accountCheckDTO = proceessAccountCheckCommandForAuthorizedUser(user.getId(), null, null);
		accountCheckDTO.setActivation(user.getActivationStatus());
		LOGGER.debug("Output parameter accountCheckDTO=[{}]", accountCheckDTO);
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
        user.setPaymentEnabled(false);
        Entry<Integer, Operator> entry = OperatorDao.getMapAsIds().entrySet().iterator().next();
        user.setOperator(entry.getKey());
        user.setStatus(UserStatusDao.getLimitedUserStatus());
        user.setDeviceUID(deviceUID);
        user.setDeviceModel(userDeviceRegDetailsDto.getDeviceModel() != null ? userDeviceRegDetailsDto.getDeviceModel() : deviceType.getName());

        user.setFirstDeviceLoginMillis(System.currentTimeMillis());
        user.setActivationStatus(ActivationStatus.REGISTERED);

        entityService.saveEntity(user);
        return user;
    }

    private boolean canBrPromoted(Community community, String deviceUID, String deviceModel) {
        boolean existsInPromotedList = deviceService.existsInPromotedList(community, deviceUID);
        boolean promotedDeviceModel = deviceService.isPromotedDeviceModel(community, deviceModel);
        boolean doesNotExistInNotPromotedList = !deviceService.existsInNotPromotedList(community, deviceUID);
        return existsInPromotedList || (promotedDeviceModel && doesNotExistInNotPromotedList);
    }

    public Promotion setPotentialPromo(String communityName, User user, String promotionCode) {
        Community community = communityService.getCommunityByName(communityName);
        String communityUri = community.getRewriteUrlParameter().toLowerCase();
        String code = messageSource.getMessage(communityUri, promotionCode, null, null);
        return  setPotentialPromo(community, user, code);
    }

    private Promotion setPotentialPromo(Community community, User user, String code) {
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
		AccountCheckDTO accountCheckDTO = proceessAccountCheckCommandForAuthorizedUser(user.getId(), null, null);
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

	@Transactional(readOnly=true)
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
		
		if (user==null)
			throw new ServiceException("users.management.edit.page.coudNotFindUser.error", "Coudn't find user with id ["+userId+"]");

		final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();

		if (currentPaymentDetails!=null&&!currentPaymentDetails.isActivated() && userDto.getPaymentEnabled()){
			throw new ServiceException("users.management.edit.page.paymentEnabledCannotBeChangedOnTrue.error", "The user payment enabled cannot be changed on true, only false");
		}
		
		Date originalNextSubPayment = Utils.getDateFromInt(user.getNextSubPayment());
		final int originalSubBalance = user.getSubBalance();

		if(userDto.getNextSubPayment().before(originalNextSubPayment)){
			throw new ServiceException("users.management.edit.page.nextSubPaymentCannotBeRedused.error", "The user nextSubPayment cannot be reduced, only extended");
		}
		
		if(userDto.getNextSubPayment().after(originalNextSubPayment)){
			if (user.isOnFreeTrial()) {
				accountLogService.logAccountEvent(userId, originalSubBalance, null, null, TransactionType.TRIAL_TOPUP, null);
			}
			else{ 
				accountLogService.logAccountEvent(userId, originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
			}
		}

		final int balanceAfter = userDto.getSubBalance();
		if (originalSubBalance != balanceAfter){
			accountLogService.logAccountEvent(userId, balanceAfter, null, null, TransactionType.SUPPORT_TOPUP, null);
		}

		user = UserAsm.fromUserDto(userDto, user);

		mobi.nowtechnologies.server.persistence.domain.UserStatus userStatus = UserStatusDao.getUserStatusMapUserStatusAsKey().get(userDto.getUserStatus());

		user.setStatus(userStatus);

		user = updateUser(user);
		
		if(!userDto.getPaymentEnabled()&& currentPaymentDetails!=null){
			unsubscribeUser(user, "Unsubscribed by admin");
		}

		LOGGER.info("Output parameter user=[{}]", user);
		return user;

	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public List<User> findActivePsmsUsers(String communityURL, BigDecimal amountOfMoneyToUserNotification, long deltaSuccesfullPaymentSmsSendingTimestampMillis){
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
		int updatedRowCount=userRepository.updateFields(user.getAmountOfMoneyToUserNotification(), user.getLastSuccesfullPaymentSmsSendingTimestampMillis(), id);
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
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={ServiceCheckedException.class, RuntimeException.class})
	public Future<Boolean> makeSuccesfullPaymentFreeSMSRequest(User user) throws ServiceCheckedException {
		try{
			LOGGER.debug("input parameters user: [{}]", user);
			
			Community community = user.getUserGroup().getCommunity();
			PaymentDetails currentActivePaymentDetails = user.getCurrentPaymentDetails();
			PaymentPolicy paymentPolicy = currentActivePaymentDetails.getPaymentPolicy();

			final String upperCaseCommunityName = community.getRewriteUrlParameter().toUpperCase();
			final String message = messageSource.getMessage(upperCaseCommunityName, "sms.succesfullPayment.text", new Object[] { community.getDisplayName(),
					paymentPolicy.getSubcost(), paymentPolicy.getSubweeks(), paymentPolicy.getShortCode() }, null);

			MigResponse migResponse = migHttpService.makeFreeSMSRequest(((MigPaymentDetails) currentActivePaymentDetails).getMigPhoneNumber(), message);

			if (migResponse.isSuccessful()) {
				LOGGER
						.info(
								"The request for freeSms sent to MIG about user {} succesfully. The nextSubPayment, status, paymentStatus and subBalance was {}, {}, {}, {} respectively",
								new Object[] { user, user.getNextSubPayment(), user.getStatus(), user.getPaymentStatus(), user.getSubBalance() });
			} else
				throw new Exception(migResponse.getDescriptionError());
			
			if (user.getLastSuccesfullPaymentSmsSendingTimestampMillis() == 0)
				resetLastSuccesfullPaymentSmsSendingTimestampMillis(user.getId());

			Future<Boolean> result = new AsyncResult<Boolean>(Boolean.TRUE);

			LOGGER.debug("Output parameter result=[{}]", result);
			return result;
		}catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new ServiceCheckedException("", "Coudn't make free sms request on successfull payment", e);
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public int resetLastSuccesfullPaymentSmsSendingTimestampMillis(int userId){
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
		int updatedRowCount=userRepository.updateFields(user.getAmountOfMoneyToUserNotification(), user.getLastSuccesfullPaymentSmsSendingTimestampMillis(), id);
		if (updatedRowCount != 1)
			throw new ServiceException("Unexpected updated users count [" + updatedRowCount + "] for id [" + id + "]");

		LOGGER.info("Output parameter user=[{}]", user);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User activatePhoneNumber(User user, String phone) {
		
		String msisdn = o2ClientService.validatePhoneNumber(phone != null ? phone : user.getMobile());
		
		user.setMobile(msisdn);
		user.setActivationStatus(ActivationStatus.ENTERED_NUMBER);
		
		userRepository.save(user);
		
		return user;
	}
}
