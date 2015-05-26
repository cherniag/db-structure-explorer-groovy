package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.common.util.ServerMessage;
import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.assembler.UserAsm;
import mobi.nowtechnologies.server.builder.PromoRequestBuilder;
import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.device.domain.DeviceTypeCache;
import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Operator;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.payment.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.OperatorRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.persistence.repository.ReactivationUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.UserStatusRepository;
import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.data.SubscriberData;
import mobi.nowtechnologies.server.service.data.UserDetailsUpdater;
import mobi.nowtechnologies.server.service.exception.ReactivateUserException;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;
import mobi.nowtechnologies.server.service.o2.impl.O2UserDetailsUpdater;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.UnsubscribeDto;
import mobi.nowtechnologies.server.shared.enums.ActionReason;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService;
import static mobi.nowtechnologies.common.util.DateTimeUtils.newDate;
import static mobi.nowtechnologies.server.builder.PromoRequestBuilder.PromoRequest;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.ITUNES_SUBSCRIPTION;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.VF_PSMS_TYPE;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.shared.Utils.getEpochMillis;
import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;
import static mobi.nowtechnologies.server.shared.enums.ActionReason.USER_DOWNGRADED_TARIFF;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ENTERED_NUMBER;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.PENDING_ACTIVATION;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.REGISTERED;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.DIRECT;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.INDIRECT;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static mobi.nowtechnologies.server.shared.enums.Tariff._4G;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.BOUGHT_PERIOD_SKIPPING;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.CARD_TOP_UP;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.PROMOTION;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.SUBSCRIPTION_CHARGE;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.SUPPORT_TOPUP;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.TRIAL_SKIPPING;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.TRIAL_TOPUP;
import static mobi.nowtechnologies.server.shared.util.EmailValidator.isNotEmail;
import static mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService.AutoOptInTriggerType.ALL;
import static mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService.AutoOptInTriggerType.EMPTY;

import javax.annotation.Resource;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import static java.lang.Boolean.TRUE;
import static java.lang.Math.max;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.Validate.notNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

public class UserService {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    public static final String MULTIPLE_FREE_TRIAL_STOP_DATE = "multiple.free.trial.stop.date";
    private static final Pageable PAGEABLE_FOR_WEEKLY_UPDATE = new PageRequest(0, 1000);
    @Resource
    UserGroupRepository userGroupRepository;
    @Resource
    PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    ReactivationUserInfoRepository reactivationUserInfoRepository;
    @Resource
    OperatorRepository operatorRepository;
    @Resource
    PromotionRepository promotionRepository;
    @Resource
    UserStatusRepository userStatusRepository;
    private boolean sendActivationSMS = false;
    private O2UserDetailsUpdater o2UserDetailsUpdater;
    private UserDetailsUpdater userDetailsUpdater;
    private UserServiceNotification userServiceNotification;
    private CommunityResourceBundleMessageSource messageSource;
    private CountryAppVersionService countryAppVersionService;
    private CountryService countryService;
    private PromotionService promotionService;
    private PaymentDetailsService paymentDetailsService;
    private MigHttpService migHttpService;
    private CountryByIpService countryByIpService;
    private CommunityService communityService;
    private DevicePromotionsService deviceService;
    private AccountLogService accountLogService;
    private OtacValidationService otacValidationService;
    private RefundService refundService;
    private MobileProviderService mobileProviderService;
    private UserNotificationService userNotificationService;
    private TaskService taskService;
    private AutoOptInRuleService autoOptInRuleService;
    private DeviceUserDataService deviceUserDataService;
    private AppsFlyerDataService appsFlyerDataService;
    private UrbanAirshipTokenService urbanAirshipTokenService;
    private UserActivationStatusService userActivationStatusService;
    private TimeService timeService;

    private MergeResult checkAndMerge(User user, User mobileUser) {
        boolean mergeIsDone = false;
        if (isNotNull(mobileUser) && mobileUser.getId() != user.getId()) {
            mergeIsDone = true;
            user = mergeUser(mobileUser, user);
        } else {
            LOGGER.info("User merge procedure is skipped");
        }
        return new MergeResult(mergeIsDone, user);
    }

    private User updateContractAndProvider(User user, ProviderUserDetails providerUserDetails) {
        LOGGER.info("Attempt to update user contract and provider with [{}]", providerUserDetails);
        if (user.isVFNZCommunityUser()) {
            if (isNotNull(providerUserDetails.operator)) {
                user.setProvider(ProviderType.valueOfKey(providerUserDetails.operator));
            }
        } else {
            if (deviceService.isPromotedDevicePhone(user.getUserGroup().getCommunity(), user.getMobile(), null)) {
                user.setContract(PAYM);
                user.setProvider(O2);
            } else {
                user.setContract(Contract.valueOf(providerUserDetails.contract));
                user.setProvider(ProviderType.valueOfKey(providerUserDetails.operator));
            }
        }
        return user;
    }

    private int detectUserAccountWithSameDeviceAndDisableIt(String deviceUID, Community community) {
        UserGroup userGroup = userGroupRepository.findByCommunity(community);
        return userRepository.updateUserAccountWithSameDeviceAndDisableIt(deviceUID, userGroup);
    }

    private MergeResult applyInitPromoInternal(PromoRequest promoRequest) {
        User user = promoRequest.user;
        boolean updateWithProviderUserDetails = promoRequest.isMajorApiVersionNumberLessThan4 || user.isVFNZCommunityUser();
        ProviderUserDetails providerUserDetails = otacValidationService.validate(promoRequest.otac, user.getMobile(), user.getUserGroup().getCommunity());
        LOGGER.info("[{}], u.contract=[{}], u.mobile=[{}], u.operator=[{}], u.activationStatus=[{}] , updateWithProviderUserDetails=[{}]",
                    providerUserDetails,
                    user.getContract(),
                    user.getMobile(),
                    user.getOperator(),
                    user.getActivationStatus(),
                    updateWithProviderUserDetails);

        MergeResult mergeResult = checkAndMerge(user, promoRequest.mobileUser);
        user = mergeResult.getResultOfOperation();
        user = checkAndUpdateWithProviderUserDetails(user, updateWithProviderUserDetails, providerUserDetails);

        user = checkAndApplyPromo(new PromoRequestBuilder(promoRequest).setUser(user).createPromoRequest());

        user = userRepository.save(user.withActivationStatus(ACTIVATED).withUserName(user.getMobile()));
        disableReactivation(promoRequest.disableReactivationForUser, user);
        LOGGER.info("Save user with new activationStatus (should be ACTIVATED) and userName (should be as mobile) [{}]", user);

        LOGGER.debug("Output parameter user=[{}]", user);
        return new MergeResult(mergeResult.isMergeDone(), user);
    }

    private void disableReactivation(boolean disableReactivationForUser, User user) {
        if (disableReactivationForUser) {
            reactivationUserInfoRepository.disableReactivationForUser(user);
        }
    }

    private User checkAndApplyPromo(PromoRequest promoRequest) {
        User user = promoRequest.user;
        boolean isApplyingWithoutEnterPhone = promoRequest.isApplyingWithoutEnterPhone;
        if (isNull(promoRequest.mobileUser)) {
            if (isApplyingWithoutEnterPhone || (ENTERED_NUMBER.equals(user.getActivationStatus()) && isNotEmail(user.getUserName()))) {
                user = promotionService.applyPotentialPromo(user);
            } else {
                LOGGER.info("Promo applying procedure is skipped for new user");
            }
        } else if (promoRequest.isSubjectToAutoOptIn) {
            user = findAndApplyPromoFromRule(user);
        } else {
            LOGGER.info("Promo applying procedure is skipped for existed user");
        }
        return user;
    }

    private User findAndApplyPromoFromRule(User user) {
        Promotion promotion = promotionService.getPromotionFromRuleForAutoOptIn(user);
        if (isNotNull(promotion)) {
            user = promotionService.applyPromotionByPromoCode(user, promotion.withCouldBeAppliedMultipleTimes(true));
        } else {
            LOGGER.info("Promo applying procedure is skipped because no promotion from rule found");
        }
        return user;
    }

    private User checkAndUpdateWithProviderUserDetails(User user, boolean updateContractAndProvider, ProviderUserDetails providerUserDetails) {
        if (updateContractAndProvider) {
            return updateContractAndProvider(user, providerUserDetails);
        } else {
            LOGGER.info("Update user contract and provider procedure is skipped");
        }
        return user;
    }

    public Boolean canActivateVideoTrial(User u) {
        if (u.isOnWhiteListedVideoAudioFreeTrial()) {
            return false;
        }
        Date multipleFreeTrialsStopDate = messageSource.readDate(u.getCommunityRewriteUrl(), MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014));

        if (u.is4G() && u.isO2PAYGConsumer() && !u.isVideoFreeTrialHasBeenActivated()) {
            return true;
        }
        if (u.is4G() && u.isO2PAYMConsumer() && INDIRECT.equals(u.getContractChannel()) && !u.isVideoFreeTrialHasBeenActivated()) {
            return true;
        }

        boolean beforeMultipleFreeTrialsStopDate = new DateTime().isBefore(multipleFreeTrialsStopDate.getTime());
        if (u.is4G() && u.isO2PAYMConsumer() && !u.isOnVideoAudioFreeTrial() && (DIRECT.equals(u.getContractChannel()) || isNull(u.getContractChannel())) && !u.has4GVideoAudioSubscription() &&
            beforeMultipleFreeTrialsStopDate) {
            return true;
        }
        if (u.is4G() && u.isO2PAYMConsumer() && !u.isVideoFreeTrialHasBeenActivated() && !beforeMultipleFreeTrialsStopDate) {
            return true;
        }
        return false;
    }

    public User checkCredentials(String userName, String userToken, String timestamp, String communityName) {
        notNull(userName, "The parameter userName is null");
        notNull(userToken, "The parameter userToken is null");
        notNull(timestamp, "The parameter timestamp is null");
        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityName);

        if (user != null) {

            final String mobile = user.getMobile();
            final int id = user.getId();
            LogUtils.putSpecificMDC(userName, mobile, id);
            LogUtils.put3rdParyRequestProfileSpecificMDC(userName, mobile, id);

            String localUserToken = Utils.createTimestampToken(user.getToken(), timestamp);
            String deviceUserToken = Utils.createTimestampToken(user.getTempToken(), timestamp);
            if (localUserToken.equalsIgnoreCase(userToken) || deviceUserToken.equalsIgnoreCase(userToken)) {
                PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
                if (null == currentPaymentDetails && UserStatus.EULA.name().equals(user.getStatus().getName())) {
                    LOGGER.info("The user [{}] couldn't login in while he has no payment details and he is in status [{}]", new Object[] {user, UserStatus.EULA.name()});
                } else {
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

    public User checkCredentials(String userName, String userToken, String timestamp, String communityName, String deviceUID) {
        LOGGER.debug("input parameters userName, userToken, timestamp, communityName, deviceUID: [{}], [{}], [{}], [{}], [{}]", userName, userToken, timestamp, communityName, deviceUID);
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

    private String findCountryCodeByIp(String ipAddress) {
        LOGGER.debug("input parameters ipAddress: [{}]", ipAddress);

        String countryName;
        if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
            countryName = "GB";
        } else {
            countryName = countryByIpService.findCountryCodeByIp(ipAddress);
        }

        LOGGER.debug("Output parameter countryName=[{}]", countryName);
        return countryName;
    }

    @Transactional(propagation = REQUIRED)
    public User mergeUser(User oldUser, User tempUser) {
        LOGGER.info(
            "Attempt to merge old user [{}] with current user [{}]. The old user deviceUID should be updated with current user deviceUID. Current user should be removed and replaced on old user",
            oldUser,
            tempUser);

        urbanAirshipTokenService.mergeToken(tempUser, oldUser);

        deviceUserDataService.removeDeviceUserData(oldUser);
        deviceUserDataService.removeDeviceUserData(tempUser);

        appsFlyerDataService.mergeAppsFlyerData(tempUser, oldUser);

        int deletedUsers = userRepository.deleteUser(tempUser.getId());
        if (deletedUsers > 1) {
            throw new ServiceException("Couldn't remove user with id [" + tempUser.getId() + "]. There are [" + deletedUsers + "] users with id [" + tempUser.getId() + "]");
        }

        oldUser.setDeviceUID(tempUser.getDeviceUID());
        oldUser.setDeviceType(tempUser.getDeviceType());
        oldUser.setDeviceModel(tempUser.getDeviceModel());
        oldUser.setIpAddress(tempUser.getIpAddress());
        oldUser.setUuid(tempUser.getUuid());

        oldUser = userRepository.save(oldUser);

        accountLogService.logAccountMergeEvent(oldUser, tempUser);

        LOGGER.info("The current user after merge now is [{}]", oldUser);
        return oldUser;
    }

    @Transactional(propagation = REQUIRED)
    public synchronized void applyPromotion(User user) {
        Promotion promotion = promotionRepository.findActivePromotion(user.getUserGroup(), Promotion.ADD_SUBBALANCE_PROMOTION, DateTimeUtils.getEpochSeconds());
        LOGGER.info("promotion [{}]", promotion);
        if (promotion != null) {
            userRepository.save(user);
            promotion.setNumUsers(promotion.getNumUsers() + 1);
            promotionRepository.save(promotion);
            accountLogService.logAccountEvent(user.getId(), user.getSubBalance(), null, null, PROMOTION);
        }
    }

    @Transactional(propagation = REQUIRED)
    public List<PaymentDetails> unsubscribeUser(String phoneNumber, String operatorName) {
        LOGGER.debug("input parameters phoneNumber, operatorName: [{}], [{}]", phoneNumber, operatorName);

        List<PaymentDetails> paymentDetails = paymentDetailsRepository.findActivatedPaymentDetails(operatorName, phoneNumber);
        LOGGER.info("Trying to unsubscribe [{}] user(s) having [{}] as mobile number", paymentDetails.size(), phoneNumber);
        final String reason = "STOP sms";
        for (PaymentDetails paymentDetail : paymentDetails) {
            final User owner = paymentDetail.getOwner();
            if (isNotNull(owner) && paymentDetail.equals(owner.getCurrentPaymentDetails())) {
                unsubscribeUser(owner, reason);
            } else {
                paymentDetail.disable(reason, new Date());
                paymentDetailsRepository.save(paymentDetail);
                LOGGER.info("Payment details [{}] was successfully disabled", paymentDetail.getI());
            }
            LOGGER.info("Phone number [{}] was successfully unsubscribed", phoneNumber);
        }

        LOGGER.debug("Output parameter paymentDetails=[{}]", paymentDetails);
        return paymentDetails;
    }

    @Transactional(propagation = REQUIRED)
    public User unsubscribeUser(int userId, UnsubscribeDto dto) {
        LOGGER.debug("input parameters userId, dto: [{}], [{}]", userId, dto);
        User user = userRepository.findOne(userId);
        String reason = dto.getReason();
        if (!StringUtils.hasText(reason)) {
            reason = "Unsubscribed by user manually via web portal";
        }
        user = unsubscribeUser(user, reason);
        LOGGER.info("Output parameter user=[{}]", user);
        return user;
    }

    @Transactional(propagation = REQUIRED)
    public User unsubscribeUser(User user, final String reason) {
        LOGGER.info("Unsubscribe user {} reason : {}", user.shortInfo(), reason);
        user = paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, reason);
        user = userRepository.save(user);
        taskService.cancelSendChargeNotificationTask(user);
        LOGGER.info("Output parameter user=[{}]", user);
        return user;
    }

    @Transactional(propagation = REQUIRED)
    public User populateAmountOfMoneyToUserNotification(User user, SubmittedPayment payment) {
        LOGGER.info("input parameters user, payment: [{}], [{}]", user.getId(), payment.getI());

        BigDecimal newAmountOfMoneyToUserNotification = user.getAmountOfMoneyToUserNotification().add(payment.getAmount());
        user.setAmountOfMoneyToUserNotification(newAmountOfMoneyToUserNotification);

        user = updateUser(user);
        LOGGER.info("Output parameter user=[{}]", user);
        return user;
    }


    @Transactional(readOnly = true)
    public User getUserWithSelectedCharts(Integer userId) {
        User user = userRepository.findOne(userId);
        user.getSelectedCharts().size();

        return user;
    }

    public boolean isCommunitySupportByIp(String email, String community, String remoteAddr) {
        String countryCode = findCountryCodeByIp(remoteAddr);
        return countryAppVersionService.isAppVersionLinkedWithCountry("CNBETA", countryCode);
    }

    @Transactional(propagation = REQUIRED)
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public String getMigPhoneNumber(int operator, String mobile) {
        return operatorRepository.findOne(operator).getMigName() + "." + mobile;
    }

    public String convertPhoneNumberFromGreatBritainToInternationalFormat(String mobile) {
        if (!mobile.startsWith("0044")) {
            return mobile.replaceFirst("0", "0044");
        }
        return mobile;
    }

    @Transactional(propagation = REQUIRED)
    public User changePassword(Integer userId, String newPassword) {
        LOGGER.debug("input parameters changePassword(Integer userId, String newPassword): [{}], [{}]", new Object[] {userId, newPassword});

        User user = userRepository.findOne(userId);

        String storedToken = Utils.createStoredToken(user.getUserName(), newPassword);

        userRepository.updateFields(storedToken, userId);

        LOGGER.debug("output parameters changePassword(Integer userId, String newPassword): [{}]", new Object[] {user});
        return user;
    }

    @Transactional(propagation = REQUIRED)
    public void processPaymentSubBalanceCommand(User user, SubmittedPayment payment) {
        LOGGER.info("Processing sub balance command for user {} old next sub payment {} with payment {}", user.getId(), user.getNextSubPayment(), payment.getI());

        final String paymentSystem = payment.getPaymentSystem();

        user.setLastSuccessfulPaymentTimeMillis(getEpochMillis());
        user.setLastSubscribedPaymentSystem(paymentSystem);
        user.setLastSuccessfulPaymentDetails(payment.getPaymentDetails());

        Period period = payment.getPeriod();
        if (paymentSystem.equals(ITUNES_SUBSCRIPTION)) {
            if (user.isOnFreeTrial()) {
                skipFreeTrial(user);
            }
            user.setNextSubPayment(payment.getNextSubPayment());
            user.setAppStoreOriginalTransactionId(payment.getAppStoreOriginalTransactionId());
            user.setBase64EncodedAppStoreReceipt(payment.getBase64EncodedAppStoreReceipt());
        } else {
            int subscriptionStartTimeSeconds = max(getEpochSeconds(), user.getNextSubPayment());
            user.setNextSubPayment(period.toNextSubPaymentSeconds(subscriptionStartTimeSeconds));
        }

        if (paymentSystem.equals(VF_PSMS_TYPE)) {
            taskService.createSendChargeNotificationTask(user);
        }

        LOGGER.info("before save account log entity");
        accountLogService.logAccountEvent(user.getId(), user.getSubBalance(), null, payment, CARD_TOP_UP);
        LOGGER.info("after save account log entity");

        LOGGER.info("before update user entity {}", user.getId());
        user.setStatus(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name()));
        userRepository.save(user);
        LOGGER.info("after update user entity {}", user.getId());

        LOGGER.info("Finish processing sub balance command for user {}", user.shortInfo());
    }

    @Transactional(propagation = REQUIRED)
    public User processAccountCheckCommandForAuthorizedUser(int userId) {
        LOGGER.info("input parameters userId: [{}]", userId);

        User user = userRepository.findOne(userId);

        user.setLastDeviceLogin(getEpochSeconds());
        updateUser(user);

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

        if (isNotNull(user)) {
            User oldUser = userRepository.findByUserNameAndCommunityAndOtherThanPassedId(user.getMobile(), user.getUserGroup().getCommunity(), user.getId());
            user.withOldUser(oldUser);
        }

        LOGGER.debug("Output parameter user=[{}]", user);
        return user;
    }

    public void saveAccountDetails(int userId, String newPassword, String phoneNumber) {
        LOGGER.debug("input parameters: [{}], [{}]", newPassword, phoneNumber);

        User user = userRepository.findOne(userId);

        String localStoredToken = Utils.createStoredToken(user.getUserName(), newPassword);

        user.setToken(localStoredToken);
        user.setMobile(phoneNumber);

        userRepository.save(user);
    }

    @Transactional(propagation = REQUIRED)
    public User registerUser(UserDeviceRegDetailsDto userDeviceRegDetailsDto, boolean createPotentialPromo, boolean updateUserPendingActivation) {
        LOGGER.info("REGISTER_USER Started [{}]", userDeviceRegDetailsDto);

        final String deviceUID = userDeviceRegDetailsDto.getDeviceUID().toLowerCase();

        Community community = communityService.getCommunityByUrl(userDeviceRegDetailsDto.getCommunityUri());
        User user = userRepository.findUserWithUserNameAsPassedDeviceUID(deviceUID, community);

        if (user == null) {
            detectUserAccountWithSameDeviceAndDisableIt(deviceUID, community);
            user = createUser(userDeviceRegDetailsDto, deviceUID, community);
            LOGGER.info("REGISTER_USER created user ", user.getUserName());
        } else if (updateUserPendingActivation && PENDING_ACTIVATION == user.getActivationStatus()) {
            user.setActivationStatus(REGISTERED);
        }

        user.setUuid(Utils.getRandomUUID());

        user = userRepository.saveAndFlush(user);

        if (createPotentialPromo && user.getNextSubPayment() == 0) {
            assignPotentialPromo(user, community);
        }

        LOGGER.info("REGISTER_USER user[{}] changed activation_status to[{}]", user.getUserName(), REGISTERED);
        return user;
    }

    private void assignPotentialPromo(User user, Community community) {
        String communityUri = community.getRewriteUrlParameter().toLowerCase();
        String deviceModel = user.getDeviceModel();

        final String promotionCode;

        if (canBePromoted(community, user.getDeviceUID(), deviceModel)) {
            promotionCode = messageSource.getMessage(communityUri, "promotionCode", null, null);
        } else {
            String blackListModels = messageSource.getMessage(communityUri, "promotion.blackListModels", null, null);
            if (deviceModel != null && blackListModels.contains(deviceModel)) {
                promotionCode = null;
            } else {
                promotionCode = messageSource.getMessage(communityUri, "defaultPromotionCode", null, null);
            }
        }

        promotionService.setPotentialPromoByPromoCode(user, promotionCode);
    }

    private User createUser(UserDeviceRegDetailsDto userDeviceRegDetailsDto, String deviceUID, Community community) {
        User user = new User();
        user.setUserName(deviceUID);
        user.setToken(Utils.createStoredToken(deviceUID, Utils.getRandomString(20)));

        DeviceType deviceType = getDeviceType(userDeviceRegDetailsDto.getDeviceType());
        user.setDeviceType(deviceType);
        user.setUserGroup(getUserGroup(community));
        user.setCountry(countryService.findIdByName("GB").getI());
        user.setIpAddress(userDeviceRegDetailsDto.getIpAddress());
        user.setOperator(getOperator());
        user.setStatus(userStatusRepository.findByName(UserStatusType.LIMITED.name()));
        user.setDeviceUID(deviceUID);
        user.setDeviceModel(userDeviceRegDetailsDto.getDeviceModel() != null ? userDeviceRegDetailsDto.getDeviceModel() : deviceType.getName());

        user.setFirstDeviceLoginMillis(System.currentTimeMillis());
        user.setActivationStatus(REGISTERED);

        return user;
    }

    // TODO: PERFORMANCE: could be improved by avoiding unneeded queries basing on the condition
    private boolean canBePromoted(Community community, String deviceUID, String deviceModel) {
        boolean existsInPromotedList = deviceService.existsInPromotedList(community, deviceUID);
        boolean promotedDeviceModel = deviceService.isPromotedDeviceModel(community, deviceModel);
        boolean doesNotExistInNotPromotedList = !deviceService.existsInNotPromotedList(community, deviceUID);
        return existsInPromotedList || (promotedDeviceModel && doesNotExistInNotPromotedList);
    }

    @Transactional(propagation = REQUIRED)
    public User updateLastWebLogin(int userId) {
        LOGGER.info("Attempt to update user last web login time");

        User user = userRepository.findOne(userId);
        user.setLastWebLogin(timeService.nowSeconds());

        return user;
    }

    @Transactional(readOnly = true)
    public Collection<User> findUsers(String searchWords, String communityURL) {
        LOGGER.debug("input parameters searchWords, communityURL: [{}], [{}]", searchWords, communityURL);

        if (searchWords == null) {
            throw new NullPointerException("The parameter searchWords is null");
        }
        if (communityURL == null) {
            throw new NullPointerException("The parameter communityURL is null");
        }

        Collection<User> users = userRepository.findUser(communityURL, "%" + searchWords + "%");

        LOGGER.info("Output parameter users=[{}]", users);
        return users;
    }

    @Transactional(propagation = REQUIRED)
    public User updateUser(UserDto userDto) {
        LOGGER.debug("input parameters userDto: [{}], [{}]", userDto);

        if (userDto == null) {
            throw new NullPointerException("The parameter userDto is null");
        }

        final Integer userId = userDto.getId();
        User user = userRepository.findOne(userId);

        if (user == null) {
            throw new ServiceException("users.management.edit.page.coudNotFindUser.error", "Couldn't find user with id [" + userId + "]");
        }

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
                accountLogService.logAccountEvent(userId, originalSubBalance, null, null, TRIAL_TOPUP);
            } else {
                accountLogService.logAccountEvent(userId, originalSubBalance, null, null, SUBSCRIPTION_CHARGE);
            }
        }

        final int balanceAfter = userDto.getSubBalance();
        if (originalSubBalance != balanceAfter) {
            accountLogService.logAccountEvent(userId, balanceAfter, null, null, SUPPORT_TOPUP);
        }

        user = UserAsm.fromUserDto(userDto, user);

        mobi.nowtechnologies.server.persistence.domain.UserStatus userStatus = userStatusRepository.findByName(userDto.getUserStatus().name());

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
        LOGGER.debug("input parameters communityURL, amountOfMoneyToUserNotification, deltaSuccesfullPaymentSmsSendingTimestampMillis: [{}], [{}], [{}]",
                     new Object[] {communityURL, amountOfMoneyToUserNotification, deltaSuccesfullPaymentSmsSendingTimestampMillis});

        if (communityURL == null) {
            throw new NullPointerException("The parameter communityURL is null");
        }
        if (amountOfMoneyToUserNotification == null) {
            throw new NullPointerException("The parameter amountOfMoneyToUserNotification is null");
        }

        List<User> users = userRepository.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, getEpochMillis(), deltaSuccesfullPaymentSmsSendingTimestampMillis);

        LOGGER.info("Output parameter users=[{}]", users);
        return users;
    }

    @Transactional(propagation = REQUIRED)
    public User resetSmsAccordingToLawAttributes(User user) {
        LOGGER.debug("input parameters user: [{}]", user);

        if (user == null) {
            throw new NullPointerException("The parameter user is null");
        }

        user.setAmountOfMoneyToUserNotification(BigDecimal.ZERO);
        user.setLastSuccesfullPaymentSmsSendingTimestampMillis(getEpochMillis());

        final int id = user.getId();
        int updatedRowCount = userRepository.updateFields(user.getAmountOfMoneyToUserNotification(), user.getLastSuccesfullPaymentSmsSendingTimestampMillis(), id);
        if (updatedRowCount != 1) {
            throw new ServiceException("Unexpected updated users count [" + updatedRowCount + "] for id [" + id + "]");
        }

        LOGGER.info("Output parameter user=[{}]", user);
        return user;
    }

    @Transactional(propagation = REQUIRED, rollbackFor = {ServiceCheckedException.class, RuntimeException.class})
    public Future<Boolean> makeSuccessfulPaymentFreeSMSRequest(User user) throws ServiceCheckedException {
        try {
            LOGGER.debug("input parameters user: [{}]", user);

            Future<Boolean> result = new AsyncResult<Boolean>(Boolean.FALSE);

            Community community = user.getUserGroup().getCommunity();
            PaymentDetails currentActivePaymentDetails = user.getCurrentPaymentDetails();
            PaymentPolicy paymentPolicy = currentActivePaymentDetails.getPaymentPolicy();

            final String upperCaseCommunityName = community.getRewriteUrlParameter().toUpperCase();
            String smsMessage = "sms.succesfullPayment.text";
            if (user.has4GVideoAudioSubscription()) {
                smsMessage = new StringBuilder().append(smsMessage).append(".video").toString();
            }
            Period period = paymentPolicy.getPeriod();
            final String message = messageSource.getMessage(upperCaseCommunityName,
                                                            smsMessage,
                                                            new Object[] {community.getDisplayName(),
                                                                          paymentPolicy.getSubcost(),
                                                                          period.getDuration(),
                                                                          period.getDurationUnit(),
                                                                          paymentPolicy.getShortCode()},
                                                            null);

            if (message == null || message.isEmpty()) {
                LOGGER.error("The message for video users is missing in services.properties!!! Key should be [{}]. User without message [{}]", smsMessage, user.getId());
                return result;
            }

            MigResponse migResponse = migHttpService.makeFreeSMSRequest(((MigPaymentDetails) currentActivePaymentDetails).getMigPhoneNumber(), message);

            if (migResponse.isSuccessful()) {
                LOGGER.info("The request for freeSms sent to MIG about user {} successfully. The nextSubPayment, status, paymentStatus and subBalance was {}, {}, {}, {} respectively",
                            new Object[] {user, user.getNextSubPayment(), user.getStatus(), user.getPaymentStatus(), user.getSubBalance()});
            } else {
                throw new Exception(migResponse.getDescriptionError());
            }

            if (user.getLastSuccesfullPaymentSmsSendingTimestampMillis() == 0) {
                userRepository.updateFields(getEpochMillis(), user.getId());
            }

            result = new AsyncResult<Boolean>(TRUE);

            LOGGER.debug("Output parameter result=[{}]", result);
            return result;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceCheckedException("", "Couldn't make free sms request on successfully payment", e);
        }
    }

    @Transactional(propagation = REQUIRED)
    public User setToZeroSmsAccordingToLawAttributes(User user) {
        LOGGER.debug("input parameters user: [{}]", user);

        if (user == null) {
            throw new NullPointerException("The parameter user is null");
        }

        user.setAmountOfMoneyToUserNotification(BigDecimal.ZERO);
        user.setLastSuccesfullPaymentSmsSendingTimestampMillis(0);

        final int id = user.getId();
        int updatedRowCount = userRepository.updateFields(user.getAmountOfMoneyToUserNotification(), user.getLastSuccesfullPaymentSmsSendingTimestampMillis(), id);
        if (updatedRowCount != 1) {
            throw new ServiceException("Unexpected updated users count [" + updatedRowCount + "] for id [" + id + "]");
        }

        LOGGER.info("Output parameter user=[{}]", user);
        return user;
    }

    @Transactional(propagation = REQUIRED)
    public User activatePhoneNumber(User user, String phone) {
        LOGGER.info("activate phone number phone=[{}] userId=[{}] activationStatus=[{}]", phone, user.getId(), user.getActivationStatus());

        String phoneNumber = phone != null ? phone : user.getMobile();
        PhoneNumberValidationData result = mobileProviderService.validatePhoneNumber(phoneNumber);

        LOGGER.info("after validating phone number msidn:[{}] phone:[{}] u.mobile:[{}]", result.getPhoneNumber(), phone, user.getMobile());

        user.setMobile(result.getPhoneNumber());
        user.setActivationStatus(ENTERED_NUMBER);
        if (result.getPin() != null) {
            user.setPin(result.getPin());
        }
        userRepository.save(user);
        sendActivationPin(user);
        LOGGER.info("PHONE_NUMBER user[{}] changed activation status to [{}]", phoneNumber, ENTERED_NUMBER);
        return user;
    }

    private void sendActivationPin(User user) {
        if (sendActivationSMS) {
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

        if (deviceService.isPromotedDevicePhone(community, phoneNumber, null)) {
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
        LOGGER.debug("Started data population for user[{}] with data [{}]", new Object[] {user, subscriberData});
        userDetailsUpdater.setUserFieldsFromSubscriberData(user, subscriberData);

        userRepository.save(user);

        LOGGER.info("Subscriber data was populated for user[{}] with data [{}]", new Object[] {user, subscriberData});
    }

    @Transactional(propagation = REQUIRED)
    public MergeResult applyInitPromo(User user, String otac, boolean isMajorApiVersionNumberLessThan4, boolean isApplyingWithoutEnterPhone, boolean checkReactivation) {
        LOGGER.info("apply init promo o2 userId = [{}], mobile = [{}], activationStatus = [{}], isMajorApiVersionNumberLessThan4=[{}]",
                    user.getId(),
                    user.getMobile(),
                    user.getActivationStatus(),
                    isMajorApiVersionNumberLessThan4);

        User mobileUser = userRepository.findByUserNameAndCommunityAndOtherThanPassedId(user.getMobile(), user.getUserGroup().getCommunity(), user.getId());

        return applyInitPromo(user, mobileUser, otac, isMajorApiVersionNumberLessThan4, isApplyingWithoutEnterPhone, checkReactivation);
    }

    @Transactional(propagation = REQUIRED)
    public MergeResult applyInitPromo(User user, User mobileUser, String otac, boolean isMajorApiVersionNumberLessThan4, boolean isApplyingWithoutEnterPhone, boolean disableReactivationForUser) {
        PromoRequest promoRequest = new PromoRequestBuilder().setUser(user)
                                                             .setMobileUser(mobileUser)
                                                             .setOtac(otac)
                                                             .setIsMajorApiVersionNumberLessThan4(isMajorApiVersionNumberLessThan4)
                                                             .setIsApplyingWithoutEnterPhone(isApplyingWithoutEnterPhone)
                                                             .
                                                                 setIsSubjectToAutoOptIn(false)
                                                             .setDisableReactivationForUser(disableReactivationForUser)
                                                             .createPromoRequest();
        MergeResult mergeResult = applyInitPromoInternal(promoRequest);
        user = mergeResult.getResultOfOperation();
        user.setHasPromo(user.isPromotionApplied());
        return new MergeResult(mergeResult.isMergeDone(), user);
    }

    @Transactional(propagation = REQUIRED)
    public void saveWeeklyPayment(User user) throws Exception {
        if (user == null) {
            throw new ServiceException("The parameter user is null");
        }

        user.setStatus(userStatusRepository.findByName(UserStatusType.LIMITED.name()));
        userRepository.save(user);
        LOGGER.info("So the user subscription status was changed on LIMITED for user with id [{}]", user.getId());
    }

    @Transactional(readOnly = true)
    public List<User> findUsersForItunesInAppSubscription(User user, int nextSubPayment, String appStoreOriginalTransactionId) {
        LOGGER.debug("input parameters user, nextSubPayment, appStoreOriginalTransactionId: [{}], [{}], [{}]", new Object[] {user, nextSubPayment, appStoreOriginalTransactionId});

        if (user == null) {
            throw new NullPointerException("The parameter user is null");
        }
        if (appStoreOriginalTransactionId == null) {
            throw new NullPointerException("The parameter appStoreOriginalTransactionId is null");
        }

        List<User> users = userRepository.findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId);
        users.add(user);

        LOGGER.debug("Output parameter users=[{}]", users);
        return users;
    }

    @Transactional(readOnly = true)
    public Page<User> getUsersForPendingPayment(int maxCount) {
        int epochSeconds = getEpochSeconds();
        return userRepository.findUsersForPendingPayment(epochSeconds, new PageRequest(0, maxCount, Sort.Direction.ASC, "nextSubPayment"));
    }

    @Transactional(readOnly = true)
    public List<User> getListOfUsersForWeeklyUpdate() {
        List<User> users = userRepository.findListOfUsersForWeeklyUpdate(getEpochSeconds(), PAGEABLE_FOR_WEEKLY_UPDATE);
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

    @Transactional(propagation = REQUIRED)
    public User downgradeUserTariff(User userWithOldTariff, Tariff newTariff) {

        Tariff oldTariff = userWithOldTariff.getTariff();
        if (_4G.equals(oldTariff) && _3G.equals(newTariff)) {
            if (userWithOldTariff.isOn4GVideoAudioBoughtPeriod()) {
                LOGGER.info("Attempt to unsubscribe user and skip Video Audio bought period (old nextSubPayment = [{}]) because of tariff downgraded from [{}] Video Audio Subscription to [{}] ",
                            userWithOldTariff.getNextSubPayment(),
                            oldTariff,
                            newTariff);
                userWithOldTariff = skipBoughtPeriodAndUnsubscribe(userWithOldTariff, USER_DOWNGRADED_TARIFF);

                userServiceNotification.sendSmsFor4GDowngradeForSubscribed(userWithOldTariff);
            } else if (userWithOldTariff.isOnVideoAudioFreeTrial()) {
                LOGGER.info("Attempt to unsubscribe user, skip Free Trial and apply O2 Potential Promo because of tariff downgraded from [{}] Free Trial Video Audio to [{}]", oldTariff, newTariff);
                userWithOldTariff = downgradeUserOn4GFreeTrialVideoAudioSubscription(userWithOldTariff);

                userServiceNotification.sendSmsFor4GDowngradeForFreeTrial(userWithOldTariff);
            } else if (userWithOldTariff.has4GVideoAudioSubscription()) {
                LOGGER.info("Attempt to unsubscribe user subscribed to Video Audio because of tariff downgraded from [{}] Video Audio with old nextSubPayment [{}] to [{}]",
                            oldTariff,
                            userWithOldTariff.getNextSubPayment(),
                            newTariff);
                userWithOldTariff = unsubscribeUser(userWithOldTariff, USER_DOWNGRADED_TARIFF.getDescription());

                userServiceNotification.sendSmsFor4GDowngradeForSubscribed(userWithOldTariff);
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
        int epochSeconds = getEpochSeconds();
        final int nextSubPayment = userWithOldTariffOnOldBoughtPeriod.getNextSubPayment();

        LOGGER.info("Attempt to skip nextSubPayment [{}] by assigning current time [{}]", nextSubPayment, epochSeconds);

        refundService.logSkippedBoughtPeriod(userWithOldTariffOnOldBoughtPeriod, actionReason);

        userWithOldTariffOnOldBoughtPeriod.setNextSubPayment(epochSeconds);

        accountLogService.logAccountEvent(userWithOldTariffOnOldBoughtPeriod.getId(), userWithOldTariffOnOldBoughtPeriod.getSubBalance(), null, null, BOUGHT_PERIOD_SKIPPING);
        return userWithOldTariffOnOldBoughtPeriod;
    }

    private User skipFreeTrial(User user) {
        int currentTimeSeconds = getEpochSeconds();
        long currentTimeMillis = currentTimeSeconds * 1000L;

        LOGGER.info("Attempt of skipping free trial. The nextSubPayment [{}] and freeTrialExpiredMillis [{}] will be changed to [{}] and [{}] corresponding",
                    user.getNextSubPayment(),
                    user.getFreeTrialExpiredMillis(),
                    currentTimeSeconds,
                    currentTimeMillis);

        user.setNextSubPayment(currentTimeSeconds);
        user.setFreeTrialExpiredMillis(currentTimeMillis);

        accountLogService.logAccountEvent(user.getId(), user.getSubBalance(), null, null, TRIAL_SKIPPING);

        return user;
    }

    @Transactional(propagation = REQUIRED)
    public User o2SubscriberDataChanged(User user, O2SubscriberData o2SubscriberData) {
        Tariff newTariff = o2SubscriberData.isTariff4G() ? _4G : _3G;
        if (!newTariff.equals(user.getTariff())) {
            if (user.isOnWhiteListedVideoAudioFreeTrial()) {
                LOGGER.info("User will not be downgraded because of he on white listed Video Audio Free Trial");
            } else {
                LOGGER.info("tariff changed [{}] to [{}]", user.getTariff(), newTariff);
                user = downgradeUserTariff(user, newTariff);
            }
        }
        o2UserDetailsUpdater.setUserFieldsFromSubscriberData(user, o2SubscriberData);
        return userRepository.save(user);
    }

    @Transactional(propagation = REQUIRED)
    public MergeResult autoOptIn(String communityUri, String userName, String userToken, String timestamp, String deviceUID, String otac, boolean checkReactivation) {
        User user = checkUser(communityUri, userName, userToken, timestamp, deviceUID, false, ENTERED_NUMBER, ACTIVATED);
        return autoOptIn(user, otac, checkReactivation);
    }

    private MergeResult autoOptIn(User user, String otac, boolean checkReactivation) {
        LOGGER.info("Attempt to auto opt in, otac {}", otac);

        User mobileUser = userRepository.findByUserNameAndCommunityAndOtherThanPassedId(user.getMobile(), user.getUserGroup().getCommunity(), user.getId());
        MergeResult resultObject = null;

        user.withOldUser(mobileUser);
        if (!autoOptInRuleService.isSubjectToAutoOptIn(ALL, user)) {
            throw new ServiceException("user.is.not.subject.to.auto.opt.in", "User isn't subject to Auto Opt In");
        }

        if (isNotBlank(otac)) {
            resultObject = applyInitPromoInternal(new PromoRequestBuilder().setUser(user)
                                                                           .setMobileUser(mobileUser)
                                                                           .setOtac(otac)
                                                                           .setIsMajorApiVersionNumberLessThan4(false)
                                                                           .setIsApplyingWithoutEnterPhone(false)
                                                                           .setIsSubjectToAutoOptIn(true)
                                                                           .setDisableReactivationForUser(checkReactivation)
                                                                           .createPromoRequest());
            user = resultObject.getResultOfOperation();
        } else {
            User result = promotionService.applyPotentialPromo(user);
            disableReactivation(checkReactivation, result);
            user = result;
        }

        if (!user.isPromotionApplied()) {
            throw new ServiceException("could.not.apply.promotion", "Couldn't apply promotion");
        }

        PaymentDetails paymentDetails = paymentDetailsService.createDefaultO2PsmsPaymentDetails(user);
        user = paymentDetails.getOwner();
        if (resultObject != null) {
            return new MergeResult(resultObject.isMergeDone(), user);
        }
        return new MergeResult(false, user);
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

    private boolean isValidDeviceUID(String deviceUID) {
        return org.springframework.util.StringUtils.hasText(deviceUID) && !deviceUID.equals("0f607264fc6318a92b9e13c65db7cd3c");
    }

    @Transactional(propagation = REQUIRED)
    public User checkUser(String community, String userName, String userToken, String timestamp, String deviceUID, boolean checkReactivation, ActivationStatus... activationStatuses) {
        User user = authenticate(community, userName, userToken, timestamp, deviceUID);
        user = authorize(user, checkReactivation, activationStatuses);
        return user;
    }

    @Transactional
    public User authorize(User user, boolean checkReactivation, ActivationStatus... activationStatuses) {
        userActivationStatusService.checkActivationStatus(user, activationStatuses);
        if (checkReactivation) {
            checkUserReactivation(user);
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User authenticate(String community, String userName, String userToken, String timestamp, String deviceUID) {
        return isValidDeviceUID(deviceUID) ? checkCredentials(userName, userToken, timestamp, community, deviceUID) : checkCredentials(userName, userToken, timestamp, community);
    }

    @Transactional
    public void updateIdfaToken(User user, String idfa) {
        if (idfa != null) {
            userRepository.updateTokenDetails(user.getId(), idfa);
        }
    }

    private void checkUserReactivation(User user) {
        if (TRUE.equals(reactivationUserInfoRepository.isUserShouldBeReactivated(user))) {
            throw new ReactivateUserException();
        }
    }

    public void setUserDetailsUpdater(UserDetailsUpdater userDetailsUpdater) {
        this.userDetailsUpdater = userDetailsUpdater;
    }

    public void setMobileProviderService(MobileProviderService mobileProviderService) {
        this.mobileProviderService = mobileProviderService;
    }

    public void setOtacValidationService(OtacValidationService otacValidationService) {
        this.otacValidationService = otacValidationService;
    }

    public void setDeviceService(DevicePromotionsService deviceService) {
        this.deviceService = deviceService;
    }

    public void setUrbanAirshipTokenService(UrbanAirshipTokenService urbanAirshipTokenService) {
        this.urbanAirshipTokenService = urbanAirshipTokenService;
    }

    public void setPaymentDetailsService(PaymentDetailsService paymentDetailsService) {
        this.paymentDetailsService = paymentDetailsService;
    }

    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    public void setCountryAppVersionService(CountryAppVersionService countryAppVersionService) {
        this.countryAppVersionService = countryAppVersionService;
    }

    public void setPromotionService(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setMigHttpService(MigHttpService migHttpService) {
        this.migHttpService = migHttpService;
    }

    public void setCountryByIpService(CountryByIpService countryByIpService) {
        this.countryByIpService = countryByIpService;
    }

    public void setAccountLogService(AccountLogService accountLogService) {
        this.accountLogService = accountLogService;
    }

    public void setRefundService(RefundService refundService) {
        this.refundService = refundService;
    }

    public void setUserServiceNotification(UserServiceNotification userServiceNotification) {
        this.userServiceNotification = userServiceNotification;
    }

    public void setCommunityService(CommunityService communityService) {
        this.communityService = communityService;
    }

    public void setO2UserDetailsUpdater(O2UserDetailsUpdater o2UserDetailsUpdater) {
        this.o2UserDetailsUpdater = o2UserDetailsUpdater;
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

    public void setAutoOptInRuleService(AutoOptInRuleService autoOptInRuleService) {
        this.autoOptInRuleService = autoOptInRuleService;
    }

    public void setDeviceUserDataService(DeviceUserDataService deviceUserDataService) {
        this.deviceUserDataService = deviceUserDataService;
    }

    public void setAppsFlyerDataService(AppsFlyerDataService appsFlyerDataService) {
        this.appsFlyerDataService = appsFlyerDataService;
    }

    public void setUserActivationStatusService(UserActivationStatusService userActivationStatusService) {
        this.userActivationStatusService = userActivationStatusService;
    }

    protected Integer getOperator() {
        Operator operator = operatorRepository.findFirst();
        if (operator != null) {
            return operator.getId();
        }
        throw new ServiceException("Couldn't find any operators in db");
    }

    protected UserGroup getUserGroup(Community community) {
        return userGroupRepository.findByCommunity(community);
    }

    protected DeviceType getDeviceType(String device) {
        DeviceType deviceType = DeviceTypeCache.getDeviceTypeMapNameAsKeyAndDeviceTypeValue().get(device);
        if (deviceType == null) {
            return DeviceTypeCache.getNoneDeviceType();
        }
        return deviceType;
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }
}
