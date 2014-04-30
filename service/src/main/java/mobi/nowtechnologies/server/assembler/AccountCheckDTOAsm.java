package mobi.nowtechnologies.server.assembler;


import com.google.common.base.Joiner;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentStatus;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import mobi.nowtechnologies.server.persistence.repository.AutoOptInExemptPhoneNumberRepository;
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.social.GooglePlusUserInfoRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.OAuthProvider;
import mobi.nowtechnologies.server.shared.dto.social.FacebookUserDetailsDto;
import mobi.nowtechnologies.server.shared.dto.social.GooglePlusUserDetailsDto;
import mobi.nowtechnologies.server.shared.dto.social.UserDetailsDto;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;

import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.*;
import static mobi.nowtechnologies.server.shared.CollectionUtils.isEmpty;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService.AutoOptInTriggerType.ALL;

public class AccountCheckDTOAsm {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountCheckDTOAsm.class);

    private AutoOptInExemptPhoneNumberRepository autoOptInExemptPhoneNumberRepository;

    @Resource
    private FacebookUserInfoRepository facebookUserInfoRepository;

    @Resource
    private GooglePlusUserInfoRepository googlePlusUserInfoRepository;


    private AutoOptInRuleService autoOptInRuleService;

    public void setAutoOptInExemptPhoneNumberRepository(AutoOptInExemptPhoneNumberRepository autoOptInExemptPhoneNumberRepository) {
        this.autoOptInExemptPhoneNumberRepository = autoOptInExemptPhoneNumberRepository;
    }

    public AccountCheckDTO toAccountCheckDTO(User user, String rememberMeToken, List<String> appStoreProductIds, boolean canActivateVideoTrial, boolean withUserDetails) {
        LOGGER.debug("user=[{}]", user);
        String lastSubscribedPaymentSystem = user.getLastSubscribedPaymentSystem();
        UserStatus status = user.getStatus();
        int nextSubPayment = user.getNextSubPayment();
        int subBalance = user.getSubBalance();
        String userName = user.getUserName();
        Promotion potentialPromotion = user.getPotentialPromotion();

        UserGroup userGroup = user.getUserGroup();
        Chart chart = userGroup.getChart();
        News news = userGroup.getNews();
        DrmPolicy drmPolicy = userGroup.getDrmPolicy();
        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();

        boolean paymentEnabled = ((null != currentPaymentDetails && currentPaymentDetails.isActivated() && (currentPaymentDetails.getLastPaymentStatus().equals(PaymentDetailsStatus.NONE) || currentPaymentDetails
                .getLastPaymentStatus().equals(PaymentDetailsStatus.SUCCESSFUL))) || (lastSubscribedPaymentSystem != null
                && lastSubscribedPaymentSystem.equals(ITUNES_SUBSCRIPTION) && status != null
                && status.getName().equals(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED.name())));
        String oldPaymentType = UserAsm.getPaymentType(currentPaymentDetails, lastSubscribedPaymentSystem, status);
        String oldPaymentStatus = getOldPaymentStatus(currentPaymentDetails);

        AccountCheckDTO accountCheckDTO = new AccountCheckDTO();
        accountCheckDTO.chartTimestamp = chart.getTimestamp();
        accountCheckDTO.chartItems = chart.getNumTracks();
        setNewsItemsAndTimestamp(news, accountCheckDTO);

        accountCheckDTO.timeOfMovingToLimitedStatusSeconds = Utils.getTimeOfMovingToLimitedStatus(nextSubPayment, subBalance);
        if (null != currentPaymentDetails)
            accountCheckDTO.lastPaymentStatus = currentPaymentDetails.getLastPaymentStatus();

        accountCheckDTO.drmType = drmPolicy.getDrmType().getName();
        accountCheckDTO.drmValue = drmPolicy.getDrmValue();
        accountCheckDTO.status = status.getName();
        accountCheckDTO.displayName = user.getDisplayName();
        accountCheckDTO.subBalance = (byte) subBalance;
        accountCheckDTO.deviceType = user.getDeviceType().getName();
        accountCheckDTO.deviceUID = user.getDeviceUID();
        accountCheckDTO.paymentType = oldPaymentType;
        accountCheckDTO.paymentEnabled = paymentEnabled;
        accountCheckDTO.phoneNumber = user.getMobile();
        accountCheckDTO.operator = user.getOperator();
        accountCheckDTO.paymentStatus = oldPaymentStatus;
        accountCheckDTO.userName = userName;
        accountCheckDTO.userToken = user.getToken();
        accountCheckDTO.rememberMeToken = rememberMeToken;
        accountCheckDTO.freeTrial = user.isOnFreeTrial();
        accountCheckDTO.provider = isNotNull(user.getProvider()) ? user.getProvider().getKey() : null;
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

        accountCheckDTO.oAuthProvider = (StringUtils.hasText(user.getFacebookId()) ? OAuthProvider.FACEBOOK : OAuthProvider.NONE);
        accountCheckDTO.nextSubPaymentSeconds = nextSubPayment;

        if (potentialPromotion != null)
            accountCheckDTO.promotionLabel = potentialPromotion.getLabel();
        accountCheckDTO.hasPotentialPromoCodePromotion = (user.getPotentialPromoCodePromotion() != null);

        ActivationStatus activationStatus = user.getActivationStatus();
        accountCheckDTO.activation = activationStatus;
        accountCheckDTO.fullyRegistred = ACTIVATED.equals(activationStatus);
        accountCheckDTO.subjectToAutoOptIn = calcSubjectToAutoOptIn(user);
        accountCheckDTO.user = user;

        if (!isEmpty(appStoreProductIds)) {
            accountCheckDTO.appStoreProductId = Joiner.on(",").skipNulls().join(appStoreProductIds);
        }
        if (withUserDetails) {
            accountCheckDTO.setUserDetails(buildUserDetails(user));
        }
        LOGGER.debug("Output parameter accountCheckDTO=[{}]", accountCheckDTO);
        return accountCheckDTO;
    }

    private UserDetailsDto buildUserDetails(User user) {
        if (ProviderType.FACEBOOK.equals(user.getProvider())) {
            FacebookUserInfo facebookUserInfo = facebookUserInfoRepository.findByUser(user);
            if (facebookUserInfo != null) {
                return convertFacebookInfoToDetails(facebookUserInfo);
            }
        }

        if (ProviderType.GOOGLE_PLUS.equals(user.getProvider())) {
            GooglePlusUserInfo googlePlusUserInfo = googlePlusUserInfoRepository.findByUser(user);
            if (googlePlusUserInfo != null) {
                return convertGooglePlusInfoToDetails(googlePlusUserInfo);
            }
        }
        return null;
    }

    private UserDetailsDto convertGooglePlusInfoToDetails(GooglePlusUserInfo googlePlusUserInfo) {
        GooglePlusUserDetailsDto result = new GooglePlusUserDetailsDto();
        result.setGooglePlusId(googlePlusUserInfo.getGooglePlusId());
        result.setEmail(googlePlusUserInfo.getEmail());
        result.setSurname(googlePlusUserInfo.getSurname());
        result.setFirstName(googlePlusUserInfo.getFirstName());
        result.setPictureUrl(googlePlusUserInfo.getPicture());
        return result;
    }

    private FacebookUserDetailsDto convertFacebookInfoToDetails(FacebookUserInfo details) {
        FacebookUserDetailsDto result = new FacebookUserDetailsDto();
        result.setUserName(details.getUserName());
        result.setFirstName(details.getFirstName());
        result.setSurname(details.getSurname());
        result.setEmail(details.getEmail());
        result.setProfileUrl(details.getProfileUrl());
        result.setFacebookId(details.getFacebookId());
        result.setLocation(details.getCity());
        result.setGender(details.getGender());
        if (details.getBirthday() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            result.setBirthDay(dateFormat.format(details.getBirthday()));
        }
        return result;
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

    private static String getOldPaymentStatus(PaymentDetails paymentDetails) {
        if (null == paymentDetails)
            return PaymentStatus.NULL;
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

    private static void setNewsItemsAndTimestamp(News news, AccountCheckDTO accountCheckDTO) {
        if (news == null)
            return;
        accountCheckDTO.newsTimestamp = news.getTimestamp();
        accountCheckDTO.newsItems = news.getNumEntries();
    }

    public void setAutoOptInRuleService(AutoOptInRuleService autoOptInRuleService) {
        this.autoOptInRuleService = autoOptInRuleService;
    }
}
