package mobi.nowtechnologies.server.assembler;


import mobi.nowtechnologies.server.persistence.domain.AutoOptInExemptPhoneNumber;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.DrmPolicy;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentStatus;
import mobi.nowtechnologies.server.persistence.repository.AutoOptInExemptPhoneNumberRepository;
import mobi.nowtechnologies.server.service.itunes.payment.ITunesPaymentService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.persistence.social.SocialNetworkType;
import mobi.nowtechnologies.server.shared.dto.social.UserDetailsDto;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.ITUNES_SUBSCRIPTION;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.MIG_SMS_TYPE;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.PAYPAL_TYPE;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.SAGEPAY_CREDITCARD_TYPE;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService.AutoOptInTriggerType.ALL;

import java.util.List;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import org.springframework.util.StringUtils;

public class AccountCheckDTOAsm {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountCheckDTOAsm.class);

    private AutoOptInExemptPhoneNumberRepository autoOptInExemptPhoneNumberRepository;

    private UserDetailsDtoAsm userDetailsDtoAsm;

    private AutoOptInRuleService autoOptInRuleService;

    private ITunesPaymentService iTunesPaymentService;

    private static String getOldPaymentStatus(PaymentDetails paymentDetails) {
        if (null == paymentDetails) {
            return PaymentStatus.NULL;
        }
        if (SAGEPAY_CREDITCARD_TYPE.equals(paymentDetails.getPaymentType())) {
            switch (paymentDetails.getLastPaymentStatus()) {
                case AWAITING:
                    return PaymentStatus.AWAITING_PAYMENT;
                case SUCCESSFUL:
                    return PaymentStatus.OK;
                case ERROR:
                case EXTERNAL_ERROR:
                    return PaymentStatus.OK;
                case NONE:
                    return PaymentStatus.NULL;
            }
        } else if (PAYPAL_TYPE.equals(paymentDetails.getPaymentType())) {
            switch (paymentDetails.getLastPaymentStatus()) {
                case AWAITING:
                    return PaymentStatus.AWAITING_PAY_PAL;
                case SUCCESSFUL:
                    return PaymentStatus.OK;
                case ERROR:
                case EXTERNAL_ERROR:
                    return PaymentStatus.PAY_PAL_ERROR;
                case NONE:
                    return PaymentStatus.NULL;
            }
        } else if (MIG_SMS_TYPE.equals(paymentDetails.getPaymentType())) {
            switch (paymentDetails.getLastPaymentStatus()) {
                case AWAITING:
                    return PaymentStatus.AWAITING_PSMS;
                case SUCCESSFUL:
                    return PaymentStatus.OK;
                case ERROR:
                case EXTERNAL_ERROR:
                    return PaymentStatus.PSMS_ERROR;
            }
            if (paymentDetails.getLastPaymentStatus().equals(PaymentDetailsStatus.NONE) && !paymentDetails.isActivated()) {
                return PaymentStatus.PIN_PENDING;
            } else if (paymentDetails.getLastPaymentStatus().equals(PaymentDetailsStatus.NONE) && paymentDetails.isActivated()) {
                return PaymentStatus.NULL;
            }
        }
        return null;
    }

    public AccountCheckDTO toAccountCheckDTO(User user, String rememberMeToken, List<String> appStoreProductIds, boolean canActivateVideoTrial, boolean withUserDetails, Boolean firstActivation,
                                             boolean withUuid, boolean withOneTimePayment) {
        LOGGER.debug("user=[{}], appStoreProductIds=[{}], canActivateVideoTrial={}, withUserDetails={}, firstActivation={}", user, appStoreProductIds, canActivateVideoTrial, withUserDetails,
                     firstActivation);
        String lastSubscribedPaymentSystem = user.getLastSubscribedPaymentSystem();
        UserStatus status = user.getStatus();
        int nextSubPayment = user.getNextSubPayment();
        int subBalance = user.getSubBalance();
        String userName = user.getUserName();
        Promotion potentialPromotion = user.getPotentialPromotion();

        UserGroup userGroup = user.getUserGroup();
        Chart chart = userGroup.getChart();
        DrmPolicy drmPolicy = userGroup.getDrmPolicy();
        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();


        boolean hasOtherPaymentDetails = currentPaymentDetails != null && currentPaymentDetails.isActivated() &&
                                         (currentPaymentDetails.getLastPaymentStatus() == PaymentDetailsStatus.NONE || currentPaymentDetails.getLastPaymentStatus() == PaymentDetailsStatus.SUCCESSFUL);
        boolean hasITunesSubscription = ITUNES_SUBSCRIPTION.equals(lastSubscribedPaymentSystem) && user.isSubscribedStatus();
        boolean hasPaidByPaymentDetails = !user.isOnFreeTrial() && user.isSubscribedStatus() && user.getCurrentPaymentDetails() != null && user.isNextSubPaymentInTheFuture();

        String oldPaymentType = UserAsm.getPaymentType(currentPaymentDetails, lastSubscribedPaymentSystem);
        String oldPaymentStatus = getOldPaymentStatus(currentPaymentDetails);

        AccountCheckDTO accountCheckDTO = new AccountCheckDTO();
        accountCheckDTO.chartTimestamp = chart.getTimestamp();
        accountCheckDTO.chartItems = chart.getNumTracks();

        accountCheckDTO.timeOfMovingToLimitedStatusSeconds = Utils.getTimeOfMovingToLimitedStatus(nextSubPayment, subBalance);
        if (null != currentPaymentDetails) {
            accountCheckDTO.lastPaymentStatus = currentPaymentDetails.getLastPaymentStatus();
        }

        accountCheckDTO.drmType = drmPolicy.getDrmType().getName();
        accountCheckDTO.drmValue = drmPolicy.getDrmValue();
        accountCheckDTO.status = status.getName();
        accountCheckDTO.displayName = user.getDisplayName();
        accountCheckDTO.subBalance = (byte) subBalance;
        accountCheckDTO.deviceType = user.getDeviceType().getName();
        accountCheckDTO.deviceUID = user.getDeviceUID();
        accountCheckDTO.paymentType = oldPaymentType;
        accountCheckDTO.paymentEnabled = hasOtherPaymentDetails || hasITunesSubscription;
        accountCheckDTO.phoneNumber = user.getMobile();
        accountCheckDTO.operator = user.getOperator();
        accountCheckDTO.paymentStatus = oldPaymentStatus;
        accountCheckDTO.userName = userName;
        accountCheckDTO.userToken = user.getToken();
        accountCheckDTO.rememberMeToken = rememberMeToken;
        accountCheckDTO.freeTrial = user.isOnFreeTrial();
        accountCheckDTO.provider = isNotNull(user.getProvider()) ?
                                   user.getProvider().getKey() :
                                   null;
        accountCheckDTO.contract = user.getContract();
        accountCheckDTO.segment = user.getSegment();
        accountCheckDTO.tariff = user.getTariff();
        accountCheckDTO.lastSubscribedPaymentSystem = lastSubscribedPaymentSystem;

        accountCheckDTO.canGetVideo = user.isO2CommunityUser();
        accountCheckDTO.canPlayVideo = user.canPlayVideo();
        accountCheckDTO.canActivateVideoTrial = canActivateVideoTrial;
        accountCheckDTO.hasAllDetails = user.hasAllDetails();
        accountCheckDTO.showFreeTrial = true;
        accountCheckDTO.subscriptionChanged = user.getSubscriptionDirection();
        accountCheckDTO.eligibleForVideo = user.isEligibleForVideo();

        accountCheckDTO.oAuthProvider = (StringUtils.hasText(user.getFacebookId()) ?
                                         SocialNetworkType.FACEBOOK.name() :
                                         SocialNetworkType.NONE.name());
        accountCheckDTO.nextSubPaymentSeconds = nextSubPayment;

        if (potentialPromotion != null) {
            accountCheckDTO.promotionLabel = potentialPromotion.getLabel();
        }
        accountCheckDTO.hasPotentialPromoCodePromotion = (user.getPotentialPromoCodePromotion() != null);

        ActivationStatus activationStatus = user.getActivationStatus();
        accountCheckDTO.activation = activationStatus;
        accountCheckDTO.fullyRegistred = ACTIVATED.equals(activationStatus);
        accountCheckDTO.subjectToAutoOptIn = calcSubjectToAutoOptIn(user);
        accountCheckDTO.user = user;
        accountCheckDTO.firstActivation = firstActivation;

        if (isNotEmpty(appStoreProductIds)) {
            accountCheckDTO.appStoreProductId = Joiner.on(",").skipNulls().join(appStoreProductIds);
        }
        if (withUserDetails) {
            UserDetailsDto userDetailsDto = userDetailsDtoAsm.toUserDetailsDto(user);
            accountCheckDTO.setUserDetails(userDetailsDto);
        }
        if (withUuid) {
            accountCheckDTO.uuid = user.getUuid();
        }
        if (withOneTimePayment) {
            if (hasPaidByPaymentDetails) {
                accountCheckDTO.oneTimePayment = user.hasOneTimeSubscription();
            } else if (hasITunesSubscription) {
                accountCheckDTO.oneTimePayment = iTunesPaymentService.hasOneTimeSubscription(user);
            }
        }
        LOGGER.debug("Output parameter accountCheckDTO=[{}]", accountCheckDTO);
        return accountCheckDTO;
    }

    private boolean calcSubjectToAutoOptIn(User user) {
        LOGGER.info("Checking for availability in database for mobile phone: " + user.getMobile());

        AutoOptInExemptPhoneNumber byUserName = autoOptInExemptPhoneNumberRepository.findOne(user.getMobile());

        //TODO: move to rule this check
        if (byUserName != null) {
            LOGGER.info("Found in database auto-opt-in record for mobile: " + user.getMobile());
            return false;
        }
        LOGGER.info("Not found in database auto-opt-in record for mobile: " + user.getMobile());
        return autoOptInRuleService.isSubjectToAutoOptIn(ALL, user);

    }

    public void setAutoOptInRuleService(AutoOptInRuleService autoOptInRuleService) {
        this.autoOptInRuleService = autoOptInRuleService;
    }

    public void setUserDetailsDtoAsm(UserDetailsDtoAsm userDetailsDtoAsm) {
        this.userDetailsDtoAsm = userDetailsDtoAsm;
    }

    public void setiTunesPaymentService(ITunesPaymentService iTunesPaymentService) {
        this.iTunesPaymentService = iTunesPaymentService;
    }

    public void setAutoOptInExemptPhoneNumberRepository(AutoOptInExemptPhoneNumberRepository autoOptInExemptPhoneNumberRepository) {
        this.autoOptInExemptPhoneNumberRepository = autoOptInExemptPhoneNumberRepository;
    }
}
