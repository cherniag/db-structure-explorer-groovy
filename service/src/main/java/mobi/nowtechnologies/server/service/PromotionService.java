package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.builder.PromoParamsBuilder;
import mobi.nowtechnologies.server.event.service.EventLoggerService;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilter;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserBanned;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.filter.FreeTrialPeriodFilter;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.persistence.repository.UserBannedRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.UserStatusRepository;
import mobi.nowtechnologies.server.service.configuration.ConfigurationAwareService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ContractChannel;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.user.rules.RuleResult;
import mobi.nowtechnologies.server.user.rules.TriggerType;
import static mobi.nowtechnologies.server.builder.PromoParamsBuilder.PromoParams;
import static mobi.nowtechnologies.server.persistence.domain.PromoCode.PROMO_CODE_FOR_FREE_TRIAL_BEFORE_SUBSCRIBE;
import static mobi.nowtechnologies.server.persistence.domain.Promotion.ADD_FREE_WEEKS_PROMOTION;
import static mobi.nowtechnologies.server.service.PromotionService.PromotionTriggerType.AUTO_OPT_IN;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.shared.Utils.secondsToMillis;
import static mobi.nowtechnologies.server.shared.enums.ActionReason.VIDEO_AUDIO_FREE_TRIAL_ACTIVATION;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.DIRECT;

import javax.annotation.Resource;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.lang.Validate.notNull;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

/**
 * @author Titov Mykhaylo (titov)
 */
public class PromotionService extends ConfigurationAwareService<PromotionService.PromotionTriggerType, Promotion> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionService.class);

    private static final String PROMO_CODE_FOR_O2_CONSUMER_4G = "promoCode.for.o2.consumer.4g";
    @Resource
    PromotionRepository promotionRepository;

    @Resource
    UserBannedRepository userBannedRepository;

    @Resource
    UserGroupRepository userGroupRepository;

    @Resource
    UserRepository userRepository;

    @Resource
    UserStatusRepository userStatusRepository;

    @Resource
    PaymentDetailsRepository paymentDetailsRepository;

    private CommunityResourceBundleMessageSource messageSource;
    private UserService userService;
    private DevicePromotionsService deviceService;
    private EventLoggerService eventLoggerService;

    public void applyPromoToLimitedUser(User user) {
        Preconditions.checkArgument(user.isLimited());

        Promotion twoWeeksTrial = getActivePromotion(user.getUserGroup(), PROMO_CODE_FOR_FREE_TRIAL_BEFORE_SUBSCRIBE);
        if(twoWeeksTrial != null) {
            long now = System.currentTimeMillis();
            int dbSecs = (int) (now / 1000); // in db we keep time in seconds not milliseconds
            if (twoWeeksTrial.getStartDate() < dbSecs && dbSecs < twoWeeksTrial.getEndDate()) {
                applyPromotionByPromoCode(user, twoWeeksTrial);
            }
        }
    }

    public Promotion getActivePromotion(UserGroup userGroup, String promotionCode) {
        notNull(promotionCode, "The parameter promotionCode is null");

        LOGGER.info("Get active promotion for promo code {}, community {}", promotionCode, userGroup.getCommunity().getRewriteUrlParameter());

        return promotionRepository.findActivePromoCodePromotion(promotionCode, userGroup, Utils.getEpochSeconds(), ADD_FREE_WEEKS_PROMOTION);
    }

    @Transactional(propagation = REQUIRED)
    public User applyPromotion(User user) {
        if (null != user.getPotentialPromotion()) {
            user.setPotentialPromotion(null);
            user = userRepository.save(user);
        }
        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
        if (null != currentPaymentDetails && null != currentPaymentDetails.getPromotionPaymentPolicy()) {
            currentPaymentDetails.setPromotionPaymentPolicy(null);
            paymentDetailsRepository.save(currentPaymentDetails);
        }
        return user;
    }

    @Transactional(propagation = REQUIRED)
    public synchronized Promotion incrementUserNumber(Promotion promotion) {
        if (null != promotion) {
            promotion.setNumUsers(promotion.getNumUsers() + 1);
            return promotionRepository.save(promotion);
        }
        return null;
    }

    @Transactional(propagation = REQUIRED)
    public User applyPotentialPromo(User user) {
        if (userService.canActivateVideoTrial(user)) {
            user = skipPrevDataAndApplyPromotionForO24GConsumer(user);
        } else {
            user = applyPotentialPromo(user, user.getUserGroup().getCommunity());
        }
        return user;
    }

    @Transactional(propagation = REQUIRED)
    public User activateVideoAudioFreeTrial(User user) {
        boolean isPromotionApplied;
        if (userService.canActivateVideoTrial(user)) {
            user = skipPrevDataAndApplyPromotionForO24GConsumer(user);
            isPromotionApplied = user.isPromotionApplied();
        } else {
            throw new ServiceException("user.is.not.eligible.for.this.action", "The user isn't eligible for this action").addErrorCode(ServiceException.Error.NOT_ELIGIBLE.getCode());
        }
        if (!isPromotionApplied) {
            throw new ServiceException("could.not.apply.promotion", "Couldn't apply promotion");
        }
        return user;
    }

    private User skipPrevDataAndApplyPromotionForO24GConsumer(User user) {
        if (user.isOnAudioBoughtPeriod()) {
            LOGGER.info("User is on audio bought period");
            user = userService.skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        } else if (user.isOnFreeTrial()) {
            LOGGER.info("User is on free trial");
            userService.unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        } else if (user.hasActivePaymentDetails()) {
            LOGGER.info("User has active payment details");
            userService.unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
        }
        return applyPromotionForO24GConsumer(user);
    }

    private User applyPromotionForO24GConsumer(User user) {
        Promotion promotion = setVideoAudioPromotionForO24GConsumer(user);
        LOGGER.info("Promotion to apply [{}]", promotion);
        if (isNotNull(promotion)) {
            user = applyPromotionByPromoCode(user, promotion);
        }
        return user;
    }

    private Promotion setVideoAudioPromotionForO24GConsumer(User user) {
        final Promotion promotion;
        final String messageCodeForPromoCode = getVideoCodeForO24GConsumer(user);
        if (StringUtils.hasText(messageCodeForPromoCode)) {
            String promoCode = messageSource.getMessage(user.getCommunityRewriteUrl(), messageCodeForPromoCode, null, null, null);
            promotion = setPotentialPromoByPromoCode(user, promoCode);
        } else {
            promotion = null;
            LOGGER.error("Couldn't find promotion code for [{}]", messageCodeForPromoCode);
        }
        return promotion;
    }

    public String getVideoCodeForO24GConsumer(User user) {
        final String messageCodeForPromoCode;
        ContractChannel contractChannel = user.getContractChannel();
        String contract = user.getContract().name();
        if (contractChannel == null) {
            LOGGER.warn("The user contract channel is null, DIRECT will be used");
            messageCodeForPromoCode = PROMO_CODE_FOR_O2_CONSUMER_4G + "." + contract + "." + DIRECT.name();
        } else {
            messageCodeForPromoCode = PROMO_CODE_FOR_O2_CONSUMER_4G + "." + contract + "." + contractChannel.name();
        }
        LOGGER.info("Message code for getting promotion code [{}]", messageCodeForPromoCode);
        return messageCodeForPromoCode.toLowerCase();
    }

    @Transactional(propagation = REQUIRED)
    public boolean updatePromotionNumUsers(Promotion promotion) {
        int updatedRowsCount = promotionRepository.updatePromotionNumUsers(promotion);
        if (updatedRowsCount != 1) {
            throw new ServiceException("Couldn't update promotion [" + promotion + "] numUsers ");
        }
        return true;
    }

    public User applyPotentialPromo(User user, Community community) {
        int freeTrialStartedTimestampSeconds = Utils.getEpochSeconds();
        LOGGER.info("Attempt to apply promotion using current unix time [{}] as freeTrialStartedTimestampSeconds", freeTrialStartedTimestampSeconds);
        return applyPotentialPromo(user, community, freeTrialStartedTimestampSeconds);
    }

    @Transactional(propagation = REQUIRED)
    public User applyPotentialPromo(User user, Community community, int freeTrialStartedTimestampSeconds) {
        LOGGER.info("Applying potential promotion for user id {}, freeTrialStartedTimestampSeconds {}", user.getId(), freeTrialStartedTimestampSeconds);
        Promotion promotion;

        String staffCode = messageSource.getMessage(community.getRewriteUrlParameter(), "o2.staff.promotionCode", null, null);
        String storeCode = messageSource.getMessage(community.getRewriteUrlParameter(), "o2.store.promotionCode", null, null);

        if (deviceService.isPromotedDevicePhone(community, user.getMobile(), staffCode)) {
            promotion = setPotentialPromoByPromoCode(user, staffCode);
        } else if (deviceService.isPromotedDevicePhone(community, user.getMobile(), storeCode)) {
            promotion = setPotentialPromoByPromoCode(user, storeCode);
        } else if (user.isO2User() || user.isVFNZUser()) {
            promotion = setPotentialPromoByMessageCode(user, "promotionCode");
        } else {
            promotion = setPotentialPromoByMessageCode(user, "defaultPromotionCode");
        }

        return applyPromotionByPromoCode(new PromoParamsBuilder().setUser(user).setPromotion(promotion).setFreeTrialStartedTimestampSeconds(freeTrialStartedTimestampSeconds).createPromoParams());
    }

    @Transactional(propagation = REQUIRED)
    public User applyPromotionByPromoCode(User user, Promotion promotion) {
        int freeTrialStartedTimestampSeconds = Utils.getEpochSeconds();
        LOGGER.info("Attempt to apply promotion using current unix time [{}] as freeTrialStartedTimestampSeconds", freeTrialStartedTimestampSeconds);
        return applyPromotionByPromoCode(new PromoParamsBuilder().setUser(user).setPromotion(promotion).setFreeTrialStartedTimestampSeconds(freeTrialStartedTimestampSeconds).createPromoParams());
    }

    @Transactional(propagation = REQUIRED)
    public User applyPromotionByPromoCode(PromoParams promoParams) {
        User user = promoParams.user;
        LOGGER.info("Attempt to apply promotion [{}] for user [{}] using [{}] as freeTrialStartedTimestampSeconds", promoParams.promotion, user, promoParams.freeTrialStartedTimestampSeconds);

        UserBanned userBanned = userBannedRepository.findOne(user.getId());

        if (isNull(userBanned) || userBanned.isGiveAnyPromotion()) {
            user = applyPromoForNotBannedUser(promoParams);
        } else {
            LOGGER.warn("The promotion wouldn't be applied because user is banned");
            user.setPotentialPromoCodePromotion(null);
            userRepository.save(user);
        }

        return user;
    }

    @Transactional(propagation = REQUIRED)
    public Promotion setPotentialPromoByMessageCode(User user, String messageCode) {
        Community community = user.getUserGroup().getCommunity();
        String communityUri = community.getRewriteUrlParameter().toLowerCase();
        String promoCode = messageSource.getMessage(communityUri, messageCode, null, null);
        return setPotentialPromoByPromoCode(user, promoCode);
    }

    @Transactional(propagation = REQUIRED)
    protected Promotion setPotentialPromoByPromoCode(User user, String code) {
        LOGGER.info("Setting potential promotion for user id {} by promo code {}", user.getId(), code);
        if (code != null) {
            Promotion potentialPromoCodePromotion = getActivePromotion(user.getUserGroup(), code);
            user.setPotentialPromoCodePromotion(potentialPromoCodePromotion);
            userRepository.save(user);
            return potentialPromoCodePromotion;
        }
        return null;
    }

    private User applyPromoForNotBannedUser(PromoParams promoParams) {
        Promotion promotion = promoParams.promotion;
        User user = promoParams.user;
        int freeTrialStartSeconds = promoParams.freeTrialStartedTimestampSeconds;

        if (isNull(promotion)) {
            throw new IllegalArgumentException("No promotion found");
        }

        if (couldNotBeApplied(user, promotion)) {
            throw new ServiceException(
                "Couldn't apply promotion for [" + promotion + "]. Probably because last applied promotion was on the same media type and this promo couldn't be applied multiple times");
        }

        final PromoCode promoCode = promotion.getPromoCode();
        int freeTrialEndSeconds = promotion.getEndSeconds(freeTrialStartSeconds);

        user.setLastPromo(promoCode);
        user.setNextSubPayment(freeTrialEndSeconds);
        user.setFreeTrialExpiredMillis(secondsToMillis(freeTrialEndSeconds));
        user.setPotentialPromoCodePromotion(null);

        if (isVideoAndMusicPromoCode(promoCode)) {
            user.setVideoFreeTrialHasBeenActivated(true);
        }

        user.setStatus(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name()));
        user.setFreeTrialStartedTimestampMillis(DateTimeUtils.secondsToMillis(freeTrialStartSeconds));
        user = userRepository.save(user);

        updatePromotionNumUsers(promotion);

        eventLoggerService.logPromotionByPromoCodeApplied(user.getId(), user.getUuid(), promotion.getI(), freeTrialStartSeconds * 1000L, freeTrialEndSeconds * 1000L);

        return user.withIsPromotionApplied(true);
    }

    private boolean couldNotBeApplied(User user, Promotion promotion) {
        return !promotion.isCouldBeAppliedMultipleTimes() && arePromotionMediaTypesTheSame(user.getLastPromo(), promotion.getPromoCode());
    }

    private boolean arePromotionMediaTypesTheSame(PromoCode lastAppliedPromoCode, PromoCode currentPromoCode) {
        boolean arePromotionMediaTypesTheSame = isNotNull(lastAppliedPromoCode) && isNotNull(currentPromoCode) && isNotNull(lastAppliedPromoCode.getMediaType()) &&
                                                lastAppliedPromoCode.getMediaType().equals(currentPromoCode.getMediaType());
        LOGGER.info("Are found and last applied promotions have the same media type: [{}]", arePromotionMediaTypesTheSame);
        return arePromotionMediaTypesTheSame;
    }

    public Promotion getPromotionFromRuleForAutoOptIn(User user) {
        RuleResult<PromotionProvider.PromotionProxy> ruleResult = getRuleServiceSupport().fireRules(AUTO_OPT_IN, user);
        return ruleResult.getResult().getPromotion();
    }

    private boolean isVideoAndMusicPromoCode(PromoCode promoCode) {
        return isNotNull(promoCode) && promoCode.forVideoAndAudio();
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    public void setEventLoggerService(EventLoggerService eventLoggerService) {
        this.eventLoggerService = eventLoggerService;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setDeviceService(DevicePromotionsService deviceService) {
        this.deviceService = deviceService;
    }

    public static enum PromotionTriggerType implements TriggerType {
        AUTO_OPT_IN;
    }
}
