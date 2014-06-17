package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.common.util.ServerMessage;
import mobi.nowtechnologies.server.assembler.UserAsm;
import mobi.nowtechnologies.server.builder.PromoRequestBuilder;
import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.dao.*;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.ReactivationUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.data.SubscriberData;
import mobi.nowtechnologies.server.service.data.UserDetailsUpdater;
import mobi.nowtechnologies.server.service.exception.*;
import mobi.nowtechnologies.server.service.social.facebook.FacebookService;
import mobi.nowtechnologies.server.service.o2.O2Service;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderService;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;
import mobi.nowtechnologies.server.service.o2.impl.O2UserDetailsUpdater;
import mobi.nowtechnologies.server.service.payment.MigPaymentService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.shared.AppConstants;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import mobi.nowtechnologies.server.shared.dto.web.AccountDto;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;
import mobi.nowtechnologies.server.shared.dto.web.UserRegDetailsDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.UnsubscribeDto;
import mobi.nowtechnologies.server.shared.enums.*;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import static java.lang.Boolean.TRUE;
import static mobi.nowtechnologies.server.builder.PromoRequestBuilder.PromoRequest;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.shared.Utils.*;
import static mobi.nowtechnologies.server.shared.enums.ActionReason.USER_DOWNGRADED_TARIFF;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.*;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.DIRECT;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.INDIRECT;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static mobi.nowtechnologies.server.shared.enums.Tariff._4G;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.*;
import static mobi.nowtechnologies.server.shared.util.DateUtils.newDate;
import static mobi.nowtechnologies.server.shared.util.EmailValidator.isNotEmail;
import static mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService.AutoOptInTriggerType.*;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.Validate.notNull;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

public class UserService {
    public static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    public static final String MULTIPLE_FREE_TRIAL_STOP_DATE = "multiple.free.trial.stop.date";

    private UserDao userDao;
    private UserGroupRepository userGroupRepository;

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
    private RefundService refundService;
    private UserServiceNotification userServiceNotification;
    private static final Pageable PAGEABLE_FOR_WEEKLY_UPDATE = new PageRequest(0, 1000);

    private O2ProviderService o2ClientService;
    private O2Service o2Service;
    private O2UserDetailsUpdater o2UserDetailsUpdater;

    private UserDetailsUpdater userDetailsUpdater;
    private MobileProviderService mobileProviderService;

    private boolean sendActivationSMS = false;
    private UserNotificationService userNotificationService;

    private TaskService taskService;
    private AutoOptInRuleService autoOptInRuleService;

    private ReactivationUserInfoRepository reactivationUserInfoRepository;
    private DeviceUserDataService deviceUserDataService;

    public void setReactivationUserInfoRepository(ReactivationUserInfoRepository reactivationUserInfoRepository) {
        this.reactivationUserInfoRepository = reactivationUserInfoRepository;
    }

    public void setAutoOptInRuleService(AutoOptInRuleService autoOptInRuleService) {
        this.autoOptInRuleService = autoOptInRuleService;
    }

    private User checkAndMerge(User user, User mobileUser) {
        if (isNotNull(mobileUser) && mobileUser.getId() != user.getId()) {
            user = mergeUser(mobileUser, user);
        }else{
            LOGGER.info("User merge procedure is skipped");
        }
        return user;
    }

    private User updateContractAndProvider(User user, ProviderUserDetails providerUserDetails) {
        LOGGER.info("Attempt to update user contract and provider with [{}]", providerUserDetails);
        if (user.isVFNZCommunityUser()){
            updateProviderForVFNZCommunityUser(user, providerUserDetails);
        }else {
            if (isPromotedDevice(user.getMobile(), user.getUserGroup().getCommunity())) {
                user.setContract(PAYM);
                user.setProvider(O2);
            } else {
                user.setContract(Contract.valueOf(providerUserDetails.contract));
                user.setProvider(ProviderType.valueOfKey(providerUserDetails.operator));
            }
        }
        return user;
    }

    private void updateProviderForVFNZCommunityUser(User user, ProviderUserDetails providerUserDetails) {
        if (isNotNull(providerUserDetails.operator)) {
            user.setProvider(ProviderType.valueOfKey(providerUserDetails.operator));
        }
    }

    private int detectUserAccountWithSameDeviceAndDisableIt(String deviceUID, Community community) {
        UserGroup userGroup = userGroupRepository.findByCommunity(community);
        return userRepository.detectUserAccountWithSameDeviceAndDisableIt(deviceUID, userGroup);
    }

    private User applyInitPromoInternal(PromoRequest promoRequest){
        User user = promoRequest.user;
        boolean updateWithProviderUserDetails = promoRequest.isMajorApiVersionNumberLessThan4 || user.isVFNZCommunityUser();
        ProviderUserDetails providerUserDetails = otacValidationService.validate(promoRequest.otac, user.getMobile(), user.getUserGroup().getCommunity());
        LOGGER.info("[{}], u.contract=[{}], u.mobile=[{}], u.operator=[{}], u.activationStatus=[{}] , updateWithProviderUserDetails=[{}]", providerUserDetails, user.getContract(), user.getMobile(), user.getOperator(), user.getActivationStatus(), updateWithProviderUserDetails);

        user = checkAndMerge(user, promoRequest.mobileUser);
        user = checkAndUpdateWithProviderUserDetails(user, updateWithProviderUserDetails, providerUserDetails);

        user = checkAndApplyPromo(new PromoRequestBuilder(promoRequest).setUser(user).createPromoRequest());

        user = userRepository.save(user.withActivationStatus(ACTIVATED).withUserName(user.getMobile()));
        disableReactivation(promoRequest.disableReactivationForUser, user);
        LOGGER.info("Save user with new activationStatus (should be ACTIVATED) and userName (should be as mobile) [{}]", user);

        LOGGER.debug("Output parameter user=[{}]", user);
        return user;
    }

    private void disableReactivation(boolean disableReactivationForUser, User user) {
        if (disableReactivationForUser){
            reactivationUserInfoRepository.disableReactivationForUser(user);
        }
    }

    private User checkAndApplyPromo(PromoRequest promoRequest) {
        User user = promoRequest.user;
        boolean isApplyingWithoutEnterPhone = promoRequest.isApplyingWithoutEnterPhone;
        if (isNull(promoRequest.mobileUser)) {
            if (isApplyingWithoutEnterPhone || (ENTERED_NUMBER.equals(user.getActivationStatus()) && isNotEmail(user.getUserName()))) {
                user = promotionService.applyPotentialPromo(user);
            }else{
                LOGGER.info("Promo applying procedure is skipped for new user");
            }
        }else if(promoRequest.isSubjectToAutoOptIn){
            user = findAndApplyPromoFromRule(user);
        }else{
            LOGGER.info("Promo applying procedure is skipped for existed user");
        }
        return user;
    }

    private User findAndApplyPromoFromRule(User user) {
        Promotion promotion = promotionService.getPromotionFromRuleForAutoOptIn(user);
        if (isNotNull(promotion)) {
            user = promotionService.applyPromotionByPromoCode(user, promotion.withCouldBeAppliedMultipleTimes(true));
        }else{
            LOGGER.info("Promo applying procedure is skipped because no promotion from rule found");
        }
        return user;
    }

    private User checkAndUpdateWithProviderUserDetails(User user, boolean updateContractAndProvider, ProviderUserDetails providerUserDetails) {
        if(updateContractAndProvider) {
            return updateContractAndProvider(user, providerUserDetails);
        }else{
            LOGGER.info("Update user contract and provider procedure is skipped");
        }
        return user;
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

    public void setUserGroupRepository(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }

    public void setUserNotificationService(UserNotificationService userNotificationService) {
        this.userNotificationService = userNotificationService;
    }

    public void setSendActivationSMS(boolean sendActivationSMS) {
        this.sendActivationSMS = sendActivationSMS;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public Boolean canActivateVideoTrial(User u) {
        if (u.isOnWhiteListedVideoAudioFreeTrial()) {
            return false;
        }
        Date multipleFreeTrialsStopDate = messageSource.readDate(u.getCommunityRewriteUrl(), MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014));

        if(u.is4G() && u.isO2PAYGConsumer() && !u.isVideoFreeTrialHasBeenActivated()) {
            return true;
        }
        if(u.is4G() && u.isO2PAYMConsumer() && INDIRECT.equals(u.getContractChannel()) && !u.isVideoFreeTrialHasBeenActivated()){
            return true;
        }

        boolean beforeMultipleFreeTrialsStopDate = new DateTime().isBefore(multipleFreeTrialsStopDate.getTime());
        if(u.is4G() && u.isO2PAYMConsumer() && !u.isOnVideoAudioFreeTrial()
                && (DIRECT.equals(u.getContractChannel()) || isNull(u.getContractChannel()))
                && !u.has4GVideoAudioSubscription() && beforeMultipleFreeTrialsStopDate){
            return true;
        }
        if(u.is4G() && u.isO2PAYMConsumer()
                && !u.isVideoFreeTrialHasBeenActivated() && !beforeMultipleFreeTrialsStopDate){
            return true;
        }
        return  false;
    }

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
                if (null == currentPaymentDetails && user.getStatus().getI() == UserStatusDao.getEulaUserStatus().getI()) {
                    LOGGER.info("The user [{}] couldn't login in while he has no payment details and he is in status [{}]",
                            new Object[]{user, UserStatus.EULA.name()});
                }
                else {
                    return user;
                }
            } else {
                LOGGER.info("Invalid user token. Expected {} but received {}", localUserToken, user.getToken());
            }
        } else {
            String message = "Could not find user with userName [" + userName + "] and communityName [" + communityName + "] in the database";
            LOGGER.info(message);

            ServerMessage serverMessage = ServerMessage.getMessageOnUnExistUser(userName, communityName);
            throw new UserCredentialsException(serverMessage);
        }

        ServerMessage serverMessage = ServerMessage.getInvalidPassedStoredToken(userName, communityName);
        throw new UserCredentialsException(serverMessage);
    }

    public void checkActivationStatus(User user, ActivationStatus... availableActivationStatuses){
        ActivationStatus activationStatus = user.getActivationStatus();
        if(availableActivationStatuses != null && availableActivationStatuses.length > 0){
            List<ActivationStatus> statusList = Arrays.asList(availableActivationStatuses);
            if(!statusList.contains(activationStatus)){
                LOGGER.error("User activation status ["+activationStatus+"] is invalid. User must have one of activation statuses" + statusList);
                throw new ActivationStatusException(activationStatus, availableActivationStatuses[0]);
            }
        }

        String message = null;
        String messageCode = null;
        if(activationStatus == REGISTERED){
            if(!user.isTempUserName()){
                message = "User activation status [REGISTERED] is invalid. User must have temp userName";
                messageCode = "error.604.activation.status.REGISTERED.invalid.userName";
            } else if(user.hasAllDetails()){
                message = "User activation status [REGISTERED] is invalid. User can't have all details";
                messageCode = "error.604.activation.status.REGISTERED.invalid.userDetails";
            } else if(!user.isLimited()){
                message = "User activation status [REGISTERED] is invalid. User must have limit status";
                messageCode = "error.604.activation.status.REGISTERED.invalid.status";
            } else if(user.hasPhoneNumber()){
                message = "User activation status [REGISTERED] is invalid. User can't have phoneNumber";
                messageCode = "error.604.activation.status.REGISTERED.invalid.phoneNumber";
            }
        } else if(activationStatus == ENTERED_NUMBER) {
            if(!user.isTempUserName()){
                message = "User activation status [ENTERED_NUMBER] is invalid. User must have temp userName";
                messageCode = "error.604.activation.status.ENTERED_NUMBER.invalid.userName";
            } else if(!user.isLimited()){
                message = "User activation status [ENTERED_NUMBER] is invalid. User must have limit status";
                messageCode = "error.604.activation.status.ENTERED_NUMBER.invalid.status";
            } else if(!user.hasPhoneNumber()){
                message = "User activation status [ENTERED_NUMBER] is invalid. User must have phoneNumber";
                messageCode = "error.604.activation.status.ENTERED_NUMBER.invalid.phoneNumber";
            }
        } else if(activationStatus == ACTIVATED){
            if(!user.hasAllDetails()){
                message = "User activation status [ACTIVATED] is invalid. User must have all user details";
                messageCode = "error.604.activation.status.ACTIVATED.invalid.userDetails";
            }
            else
            if(!user.isActivatedUserName()){
                message = "User activation status [ACTIVATED] is invalid. User must have activated userName";
                messageCode = "error.604.activation.status.ACTIVATED.invalid.userName";
            }
        }

        if(message != null){
            LOGGER.error(message);
            throw new ActivationStatusException(message, messageCode);
        }
    }

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

    @Deprecated
    public boolean userExists(String userName, String communityName) {
        return userDao.userExists(userName, communityName);
    }

    public User findByName(String userName) {
        if (userName == null)
            throw new ServiceException("The parameter userName is null");
        return entityService.findByProperty(User.class, User.Fields.userName.toString(), userName);
    }

    public List<User> findByMobile(String mobile) {
        if (mobile == null)
            throw new ServiceException("The parameter mobile is null");
        return userRepository.findByMobile(mobile);
    }

    public User findByFacebookId(String facebookId, String communityName) {
        LOGGER.debug("input parameters facebookId, communityName: [{}], [{}]", facebookId, communityName);
        if (facebookId == null)
            throw new ServiceException("The parameter facebookId is null");
        final User user = userDao.findByFacebookAndCommunity(facebookId, communityName);
        LOGGER.info("Output parameter user=[{}]", user);
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

    @Transactional(propagation = REQUIRED)
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional(propagation = REQUIRED)
    public User mergeUser(User oldUser, User userByDeviceUID) {
        LOGGER.info("Attempt to merge old user [{}] with current user [{}]. The old user deviceUID should be updated with current user deviceUID. Current user should be removed and replaced on old user", oldUser, userByDeviceUID);

        userDeviceDetailsService.removeUserDeviceDetails(userByDeviceUID);

        deviceUserDataService.removeDeviceUserData(oldUser);
        deviceUserDataService.removeDeviceUserData(userByDeviceUID);

        int deletedUsers = userRepository.deleteUser(userByDeviceUID.getId());
        if(deletedUsers>1) throw new ServiceException("Couldn't remove user with id ["+userByDeviceUID.getId()+"]. There are ["+deletedUsers +"] users with id ["+userByDeviceUID.getId()+"]");

        oldUser.setDeviceUID(userByDeviceUID.getDeviceUID());
        oldUser.setDeviceType(userByDeviceUID.getDeviceType());
        oldUser.setDeviceModel(userByDeviceUID.getDeviceModel());
        oldUser.setIpAddress(userByDeviceUID.getIpAddress());

        oldUser = userRepository.save(oldUser);

        accountLogService.logAccountMergeEvent(oldUser, userByDeviceUID);

        LOGGER.info("The current user after merge now is [{}]", oldUser);
        return oldUser;
    }

    @Transactional(propagation = REQUIRED)
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

    @Transactional(propagation = REQUIRED)
    public List<PaymentDetails> unsubscribeUser(String phoneNumber, String operatorName) {
        LOGGER.debug("input parameters phoneNumber, operatorName: [{}], [{}]", phoneNumber, operatorName);

        List<PaymentDetails> paymentDetails = paymentDetailsService.findActivatedPaymentDetails(operatorName, phoneNumber);
        LOGGER.info("Trying to unsubscribe [{}] user(s) having [{}] as mobile number", paymentDetails.size(), phoneNumber);
        final String reason = "STOP sms";
        for (PaymentDetails paymentDetail : paymentDetails) {
            final User owner = paymentDetail.getOwner();
            if(isNotNull(owner) && paymentDetail.equals(owner.getCurrentPaymentDetails())){
                unsubscribeUser(owner, reason);
            }else {
                paymentDetailsService.disablePaymentDetails(paymentDetail, reason);
            }
            LOGGER.info("Phone number [{}] was successfully unsubscribed", phoneNumber);
        }

        LOGGER.debug("Output parameter paymentDetails=[{}]", paymentDetails);
        return paymentDetails;
    }

    @Transactional(propagation = REQUIRED)
    public User unsubscribeUser(int userId, UnsubscribeDto dto) {
        LOGGER.debug("input parameters userId, dto: [{}], [{}]", userId, dto);
        User user = entityService.findById(User.class, userId);
        String reason = dto.getReason();
        if (!StringUtils.hasText(reason)) {
            reason = "Unsubscribed by user manually via web portal";
        }
        user = unsubscribeUser(user, reason);
        LOGGER.info("Output parameter user=[{}]", user);
        return user;
    }


    public boolean isUnsubscribedUser(User user){
        return user != null && user.getCurrentPaymentDetails() != null && !user.getCurrentPaymentDetails().isActivated();
    }


    @Transactional(propagation = REQUIRED)
    public User unsubscribeUser(User user, final String reason) {
        LOGGER.debug("input parameters user, reason: [{}], [{}]", user, reason);
        notNull(user, "The parameter user is null");
        user = paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, reason);
        user = entityService.updateEntity(user);
        taskService.cancelSendChargeNotificationTask(user);
        LOGGER.info("Output parameter user=[{}]", user);
        return user;
    }

    @Transactional(propagation = REQUIRED)
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

    @Transactional(propagation = REQUIRED)
    public User changePassword(Integer userId, String newPassword) {
        LOGGER.debug("input parameters changePassword(Integer userId, String newPassword): [{}], [{}]", new Object[]{userId, newPassword});

        User user = findById(userId);

        String storedToken = Utils.createStoredToken(user.getUserName(), newPassword);

        userRepository.updateFields(storedToken, userId);

        LOGGER.debug("output parameters changePassword(Integer userId, String newPassword): [{}]", new Object[]{user});
        return user;
    }

    @Transactional(propagation = REQUIRED)
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

    @Transactional(propagation = REQUIRED)
    public void processPaymentSubBalanceCommand(User user, int subweeks, SubmittedPayment payment) {
        LOGGER.debug("processPaymentSubBalanceCommand input parameters user, subweeks, payment: [{}]", new Object[] { user, subweeks, payment });
        final String paymentSystem = payment.getPaymentSystem();

        // Update last Successful payment time
        final long epochMillis = getEpochMillis();
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
        }else if (user.isMonthlyPaidUser()) {
            user.setNextSubPayment(Utils.getMonthlyNextSubPayment(oldNextSubPayment));
        }else if (user.isSMSActivatedUser()){
            if (Utils.getEpochSeconds() > oldNextSubPayment){
                user.setNextSubPayment(Utils.getEpochSeconds() + subweeks * WEEK_SECONDS);
            }else{
                user.setNextSubPayment(oldNextSubPayment + subweeks * WEEK_SECONDS);
            }
        } else {
            user.setSubBalance(user.getSubBalance() + subweeks);

            user.setNextSubPayment(Utils.getNewNextSubPayment(oldNextSubPayment));
        }

        if(paymentSystem.equals(PaymentDetails.VF_PSMS_TYPE)){
            taskService.createSendChargeNotificationTask(user);
        }

        entityService.saveEntity(new AccountLog(user.getId(), payment, user.getSubBalance(), CARD_TOP_UP));
        // The main idea is that we do pre-payed service, this means that
        // in case of first payment or after LIMITED status we need to decrease subBalance of user immediately
        if (wasInLimitedStatus || UserStatusDao.getEulaUserStatus().getI() == user.getStatus().getI()) {
            if (!user.isSMSActivatedUser() && !paymentSystem.equals(PaymentDetails.ITUNES_SUBSCRIPTION)) {
                user.setSubBalance(user.getSubBalance() - 1);
                entityService.saveEntity(new AccountLog(user.getId(), payment, user.getSubBalance(), SUBSCRIPTION_CHARGE));
            }
        }

        // Update user status to subscribed
        user.setStatus(UserStatusDao.getSubscribedUserStatus());

        entityService.updateEntity(user);

        LOGGER.info("User {} with balance {}", user.getId(), user.getSubBalance());
    }

    @Transactional(propagation = REQUIRED)
    public User proceessAccountCheckCommandForAuthorizedUser(int userId) {
        LOGGER.debug("input parameters userId: [{}]", new String[]{String.valueOf(userId)});

        User user = userDao.findUserById(userId);

        user = updateLastDeviceLogin(user);

        if (user.getLastDeviceLogin() == 0)
            makeUserActive(user);

        user = prepareUserConversionToAccountCheckDto(user);

        LOGGER.debug("Output parameter user=[{}]", user);
        return user;
    }

    private User prepareUserConversionToAccountCheckDto(User user) {
        Community community = user.getUserGroup().getCommunity();
        boolean isAutoOptInEnabled = messageSource.readBoolean(community.getRewriteUrlParameter(), "auto.opt.in.enabled", true);
        user = findUserTree(user.getId());
        return user.withAutoOptInEnabled(isAutoOptInEnabled);
    }

    @Transactional(readOnly = true)
    public User findUserTree(int userId) {
        LOGGER.debug("input parameters userId: [{}]", userId);
        User user = userRepository.findUserTree(userId);

        if(isNotNull(user)){
            User oldUser = userRepository.findByUserNameAndCommunityAndOtherThanPassedId(user.getMobile(), user.getUserGroup().getCommunity(), user.getId());
            user.withOldUser(oldUser);
        }

        LOGGER.debug("Output parameter user=[{}]", user);
        return user;
    }

    @Deprecated
    @Transactional(propagation = REQUIRED)
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
        if (isNull(promotionCode))
            promotionCode = getDefaultPromoCode(communityName);

        promotionService.applyPromotionByPromoCode(user, promotionCode);

        LOGGER.debug("Output parameter user=[{}]", user);
        promotionService.assignPotentialPromotion(user);
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

    @Transactional(propagation = REQUIRED, readOnly = true)
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

    @Transactional(propagation = REQUIRED)
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

    @Transactional(propagation = REQUIRED)
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

    @Transactional(propagation = REQUIRED)
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

    private User findUserWithUserNameAsPassedDeviceUID(String deviceUID, Community community) {
        LOGGER.debug("input parameters deviceUID, community: [{}], [{}]", deviceUID, community);

        User user = userRepository.findUserWithUserNameAsPassedDeviceUID(deviceUID, community);

        LOGGER.debug("Output parameter user=[{}]", user);
        return user;
    }

    @Transactional(propagation = REQUIRED)
    public User registerUser(UserDeviceRegDetailsDto userDeviceRegDetailsDto, boolean createPotentialPromo, boolean updateUserPendingActivation) {
        LOGGER.info("REGISTER_USER Started [{}]", userDeviceRegDetailsDto);

        final String deviceUID = userDeviceRegDetailsDto.getDeviceUID().toLowerCase();

        Community community = communityService.getCommunityByUrl(userDeviceRegDetailsDto.getCommunityUri());
        User user = findUserWithUserNameAsPassedDeviceUID(deviceUID, community);

        if (isNull(user)) {
            detectUserAccountWithSameDeviceAndDisableIt(deviceUID, community);

            DeviceType deviceType = DeviceTypeDao.getDeviceTypeMapNameAsKeyAndDeviceTypeValue().get(userDeviceRegDetailsDto.getDeviceType());
            if (isNull(deviceType)) deviceType = DeviceTypeDao.getNoneDeviceType();

            user = createUser(userDeviceRegDetailsDto, deviceUID, deviceType, community);
        } else if (isNotNull(user) && updateUserPendingActivation && ActivationStatus.PENDING_ACTIVATION == user.getActivationStatus()) {
            user.setActivationStatus(REGISTERED);
        }

        if (createPotentialPromo && user.getNextSubPayment() == 0) {
            String communityUri = community.getRewriteUrlParameter().toLowerCase();
            String deviceModel = user.getDeviceModel();

            final String promotionCode;

            if (canBePromoted(community, deviceUID, deviceModel)) {
                promotionCode = messageSource.getMessage(communityUri, "promotionCode", null, null);
            } else {
                String blackListModels = messageSource.getMessage(communityUri, "promotion.blackListModels", null, null);
                if (deviceModel != null && blackListModels.contains(deviceModel)) {
                    promotionCode = null;
                } else
                    promotionCode = messageSource.getMessage(communityUri, "defaultPromotionCode", null, null);
            }

            promotionService.setPotentialPromoByPromoCode(user, promotionCode);
        }

        userRepository.save(user);
        LOGGER.info("REGISTER_USER user[{}] changed activation_status to[{}]", user.getUserName(), REGISTERED);
        return user;
    }

    private User createUser(UserDeviceRegDetailsDto userDeviceRegDetailsDto, String deviceUID, DeviceType deviceType, Community community) {
        User user = new User();
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

        return userRepository.save(user);
    }

    // TODO: PERFORMANCE: could be improved by avoiding unneeded queries basing on the condition
    private boolean canBePromoted(Community community, String deviceUID, String deviceModel) {
        boolean existsInPromotedList = deviceService.existsInPromotedList(community, deviceUID);
        boolean promotedDeviceModel = deviceService.isPromotedDeviceModel(community, deviceModel);
        boolean doesNotExistInNotPromotedList = !deviceService.existsInNotPromotedList(community, deviceUID);
        return existsInPromotedList || (promotedDeviceModel && doesNotExistInNotPromotedList);
    }

    @Transactional(propagation = REQUIRED)
    public User updateLastDeviceLogin(User user) {
        LOGGER.debug("input parameters user: [{}]", user);

        user.setLastDeviceLogin(Utils.getEpochSeconds());
        updateUser(user);

        LOGGER.debug("Output parameter user=[{}]", user);
        return user;
    }

    @Transactional(propagation = REQUIRED)
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

    @Transactional(propagation = REQUIRED)
    public User updateUser(UserDto userDto) {
        LOGGER.debug("input parameters userDto: [{}], [{}]", userDto);

        if (userDto == null)
            throw new NullPointerException("The parameter userDto is null");

        final Integer userId = userDto.getId();
        User user = userRepository.findOne(userId);

        if (user == null)
            throw new ServiceException("users.management.edit.page.coudNotFindUser.error", "Couldn't find user with id [" + userId + "]");

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

        if (!userDto.getPaymentEnabled() && isNotNull(currentPaymentDetails)) {
            unsubscribeUser(user, "Unsubscribed by admin");
        }

        LOGGER.info("Output parameter user=[{}]", user);
        return user;

    }

    @Transactional(propagation = REQUIRED)
    public List<User> findActivePsmsUsers(String communityURL, BigDecimal amountOfMoneyToUserNotification, long deltaSuccesfullPaymentSmsSendingTimestampMillis) {
        LOGGER.debug("input parameters communityURL, amountOfMoneyToUserNotification, deltaSuccesfullPaymentSmsSendingTimestampMillis: [{}], [{}], [{}]", new Object[]{
                communityURL, amountOfMoneyToUserNotification, deltaSuccesfullPaymentSmsSendingTimestampMillis});

        if (communityURL == null)
            throw new NullPointerException("The parameter communityURL is null");
        if (amountOfMoneyToUserNotification == null)
            throw new NullPointerException("The parameter amountOfMoneyToUserNotification is null");

        List<User> users = userRepository.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, getEpochMillis(), deltaSuccesfullPaymentSmsSendingTimestampMillis);

        LOGGER.info("Output parameter users=[{}]", users);
        return users;
    }

    @Transactional(propagation = REQUIRED)
    public User resetSmsAccordingToLawAttributes(User user) {
        LOGGER.debug("input parameters user: [{}]", user);

        if (user == null)
            throw new NullPointerException("The parameter user is null");

        user.setAmountOfMoneyToUserNotification(BigDecimal.ZERO);
        user.setLastSuccesfullPaymentSmsSendingTimestampMillis(getEpochMillis());

        final int id = user.getId();
        int updatedRowCount = userRepository.updateFields(user.getAmountOfMoneyToUserNotification(), user.getLastSuccesfullPaymentSmsSendingTimestampMillis(), id);
        if (updatedRowCount != 1)
            throw new ServiceException("Unexpected updated users count [" + updatedRowCount + "] for id [" + id + "]");

        LOGGER.info("Output parameter user=[{}]", user);
        return user;
    }

    @Transactional(propagation = REQUIRED)
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

    @Transactional(propagation = REQUIRED, rollbackFor = { ServiceCheckedException.class, RuntimeException.class })
    public Future<Boolean> makeSuccessfulPaymentFreeSMSRequest(User user) throws ServiceCheckedException {
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
                resetLastSuccessfulPaymentSmsSendingTimestampMillis(user.getId());

            result = new AsyncResult<Boolean>(TRUE);

            LOGGER.debug("Output parameter result=[{}]", result);
            return result;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceCheckedException("", "Couldn't make free sms request on successfully payment", e);
        }
    }

    @Transactional(propagation = REQUIRED)
    public int resetLastSuccessfulPaymentSmsSendingTimestampMillis(int userId) {
        LOGGER.debug("input parameters userId: [{}]", userId);

        int updatedRowCount = userRepository.updateFields(getEpochMillis(), userId);
        if (updatedRowCount != 1)
            throw new ServiceException("Unexpected updated users count [" + updatedRowCount + "] for id [" + userId + "]");

        LOGGER.debug("Output parameter updatedRowCount=[{}]", updatedRowCount);
        return updatedRowCount;
    }

    @Transactional(propagation = REQUIRED)
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

    @Transactional(propagation = REQUIRED)
    public User activatePhoneNumber(User user, String phone) {
        LOGGER.info("activate phone number phone=[{}] userId=[{}] activationStatus=[{}]", phone, user.getId(),
                user.getActivationStatus());

        String phoneNumber = phone != null ? phone : user.getMobile();
        PhoneNumberValidationData result = mobileProviderService.validatePhoneNumber(phoneNumber);

        LOGGER.info("after validating phone number msidn:[{}] phone:[{}] u.mobile:[{}]", result.getPhoneNumber(), phone, user.getMobile());

        user.setMobile(result.getPhoneNumber());
        user.setActivationStatus(ENTERED_NUMBER);
        if(result.getPin() != null){
            user.setPin(result.getPin());
        }
        userRepository.save(user);
        sendActivationPin(user);
        LOGGER.info("PHONE_NUMBER user[{}] changed activation status to [{}]", phoneNumber, ENTERED_NUMBER);
        return user;
    }

    private void sendActivationPin(User user) {
        if (sendActivationSMS){
            try {
                userNotificationService.sendActivationPinSMS(user);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public void populateSubscriberData(final User user) {
        String phoneNumber = user.getMobile();
        Community community = user.getUserGroup().getCommunity();

        if ( isPromotedDevice(phoneNumber, community)) {
            // if the device is promoted, we set the default field
            populateSubscriberData(user, null);
        } else {
            try {
                mobileProviderService.getSubscriberData(phoneNumber, userDetailsUpdater);
            } catch (Exception ex) {
                // intentionally swallowing the exception to enable user to continue with activation
                LOGGER.error("Unable to get subscriber data during activation phone=[{}]", phoneNumber, ex);
            }
        }
    }

    public void populateSubscriberData(User user, SubscriberData subscriberData) {
        LOGGER.debug("Started data population for user[{}] with data [{}]", new Object[]{user, subscriberData});
        userDetailsUpdater.setUserFieldsFromSubscriberData(user, subscriberData);

        userRepository.save(user);

        LOGGER.info("Subscriber data was populated for user[{}] with data [{}]", new Object[]{user, subscriberData});
    }

    @Transactional(readOnly = true)
    public String getRedeemServerO2Url(User user) {
        return o2ClientService.getRedeemServerO2Url(user.getMobile());
    }

    @Transactional(propagation = REQUIRED)
    public User applyInitPromo(User user, String otac, boolean isMajorApiVersionNumberLessThan4, boolean isApplyingWithoutEnterPhone, boolean checkReactivation) {
        LOGGER.info("apply init promo o2 userId = [{}], mobile = [{}], activationStatus = [{}], isMajorApiVersionNumberLessThan4=[{}]", user.getId(), user.getMobile(), user.getActivationStatus(), isMajorApiVersionNumberLessThan4);

        User mobileUser = userRepository.findByUserNameAndCommunityAndOtherThanPassedId(user.getMobile(), user.getUserGroup().getCommunity(), user.getId());

        return applyInitPromo(user, mobileUser, otac, isMajorApiVersionNumberLessThan4, isApplyingWithoutEnterPhone, checkReactivation);

    }

    @Transactional(propagation = REQUIRED)
    public User applyInitPromo(User user, User mobileUser, String otac, boolean isMajorApiVersionNumberLessThan4, boolean isApplyingWithoutEnterPhone, boolean disableReactivationForUser) {
        PromoRequest promoRequest = new PromoRequestBuilder().setUser(user).setMobileUser(mobileUser).setOtac(otac).setIsMajorApiVersionNumberLessThan4(isMajorApiVersionNumberLessThan4).setIsApplyingWithoutEnterPhone(isApplyingWithoutEnterPhone).
                setIsSubjectToAutoOptIn(false).setDisableReactivationForUser(disableReactivationForUser).createPromoRequest();
        user = applyInitPromoInternal(promoRequest);

        user.setHasPromo(user.isPromotionApplied());
        return user;
    }

    @Transactional(propagation = REQUIRED)
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

    @Transactional(propagation = REQUIRED)
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

    @Transactional(propagation = REQUIRED)
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
        int freeTrialStartedTimestampSeconds = (int) (user.getFreeTrialStartedTimestampMillis() / 1000L);
        LOGGER.info("Attempt to apply promotion using user freeTrialStartedTimestampMillis value unix time [{}] as freeTrialStartedTimestampSeconds", freeTrialStartedTimestampSeconds);
        promotionService.applyPotentialPromo(user, user.getUserGroup().getCommunity(), freeTrialStartedTimestampSeconds);
        return user;
    }

    @Transactional(propagation = REQUIRED)
    public User unsubscribeAndSkipFreeTrial(User user, ActionReason actionReason) {
        user = unsubscribeUser(user, actionReason.getDescription());
        user = skipFreeTrial(user);
        return user;
    }

    @Transactional(propagation = REQUIRED)
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

    @Transactional(propagation = REQUIRED)
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

    public boolean isPromotedDevice(String phoneNumber, Community community) {
        boolean isPromoted = false;
        try {
            isPromoted = deviceService.isPromotedDevicePhone(
                    community,
                    phoneNumber,
                    null);
        } catch ( Exception e ) {
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.info("isPromotedDevice('{}')={}", phoneNumber, isPromoted);
        return isPromoted;
    }

    public boolean isVFNZOtacValid(String otac, String phoneNumber, Community community) {
        return userRepository.findByOtacMobileAndCommunity(otac, phoneNumber, community)==0L ? false: true;
    }

    @Transactional(propagation = REQUIRED)
    public User autoOptIn(String communityUri, String userName, String userToken, String timestamp, String deviceUID, String otac, boolean checkReactivation) {
        User user = checkUser(communityUri, userName, userToken, timestamp, deviceUID, false, ENTERED_NUMBER, ACTIVATED);
        return autoOptIn(user, otac, checkReactivation);
    }

    private User  autoOptIn(User user, String otac, boolean checkReactivation) {
        LOGGER.info("Attempt to auto opt in, otac {}", otac);

        User mobileUser = userRepository.findByUserNameAndCommunityAndOtherThanPassedId(user.getMobile(), user.getUserGroup().getCommunity(), user.getId());

        user.withOldUser(mobileUser);
        if(!autoOptInRuleService.isSubjectToAutoOptIn(ALL, user)) {
            throw new ServiceException("user.is.not.subject.to.auto.opt.in", "User isn't subject to Auto Opt In");
        }

        if(isNotBlank(otac)){
            user = applyInitPromoInternal(new PromoRequestBuilder().setUser(user).setMobileUser(mobileUser).setOtac(otac).setIsMajorApiVersionNumberLessThan4(false).setIsApplyingWithoutEnterPhone(false).setIsSubjectToAutoOptIn(true).setDisableReactivationForUser(checkReactivation).createPromoRequest());
        }else{
            User result = promotionService.applyPotentialPromo(user);
            disableReactivation(checkReactivation, result);
            user = result;
        }

        if (!user.isPromotionApplied()){
            throw new ServiceException("could.not.apply.promotion", "Couldn't apply promotion");
        }

        PaymentDetails paymentDetails = paymentDetailsService.createDefaultO2PsmsPaymentDetails(user);
        return paymentDetails.getOwner();
    }

    @Transactional(propagation = REQUIRED)
    public void activateVideoAudioFreeTrialAndAutoOptIn(User user) {
        LOGGER.info("Attempt to activate video audio free trial and subscribe user user with id: [{}]", user.getId());
        boolean subjectToAutoOptIn = autoOptInRuleService.isSubjectToAutoOptIn(EMPTY, user);
        user = promotionService.activateVideoAudioFreeTrial(user);
        if (subjectToAutoOptIn) {
            paymentDetailsService.createDefaultO2PsmsPaymentDetails(user);
        }
    }

    private boolean isValidDeviceUID(String deviceUID){
        return org.springframework.util.StringUtils.hasText(deviceUID) && !deviceUID.equals("0f607264fc6318a92b9e13c65db7cd3c");
    }

    @Transactional(propagation = REQUIRED)
    public User checkUser(String community, String userName, String userToken, String timestamp, String deviceUID, boolean checkReactivation, ActivationStatus... activationStatuses){
        User user;
        if (isValidDeviceUID(deviceUID)) {
            user = checkCredentials(userName, userToken, timestamp, community, deviceUID);
        }else {
            user = checkCredentials(userName, userToken, timestamp, community);
        }
        checkActivationStatus(user, activationStatuses);
        if (checkReactivation){
            checkUserReactivation(user);
        }
        return user;
    }

    private void checkUserReactivation(User user) {
        if (TRUE.equals(reactivationUserInfoRepository.isUserShouldBeReactivated(user)))
             throw new ReactivateUserException();
    }

    public void setDeviceUserDataService(DeviceUserDataService deviceUserDataService) {
        this.deviceUserDataService = deviceUserDataService;
    }
}
