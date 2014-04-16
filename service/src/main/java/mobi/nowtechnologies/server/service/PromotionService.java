package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.dao.PromotionDao;
import mobi.nowtechnologies.server.persistence.dao.UserGroupDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.filter.FreeTrialPeriodFilter;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.persistence.repository.UserBannedRepository;
import mobi.nowtechnologies.server.service.configuration.ConfigurationAwareService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ContractChannel;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.user.rules.*;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

import static mobi.nowtechnologies.server.persistence.domain.Community.O2_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.persistence.domain.Promotion.*;
import static mobi.nowtechnologies.server.service.PromotionService.PromotionTriggerType.AUTO_OPT_IN;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.shared.Utils.conCatLowerCase;
import static mobi.nowtechnologies.server.shared.Utils.secondsToMillis;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.*;
import static mobi.nowtechnologies.server.shared.enums.ActionReason.*;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static mobi.nowtechnologies.server.shared.enums.Tariff._4G;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.PROMOTION_BY_PROMO_CODE_APPLIED;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.SUBSCRIPTION_CHARGE;
import static mobi.nowtechnologies.server.service.configuration.Configuration.*;
import static mobi.nowtechnologies.server.user.criteria.CallBackUserDetailsMatcher.UserDetailHolder;
import static mobi.nowtechnologies.server.user.rules.RuleServiceSupport.RuleComparator;
import static org.apache.commons.lang.Validate.notNull;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class PromotionService extends ConfigurationAwareService <PromotionService.PromotionTriggerType,Promotion> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PromotionService.class);

    private static final String PROMO_CODE_FOR_O2_CONSUMER_4G = "promoCode.for.o2.consumer.4g.";
	
	private PromotionDao promotionDao;
	private EntityService entityService;
    private UserService userService;
    private CommunityResourceBundleMessageSource messageSource;
    private PromotionRepository promotionRepository;
    private UserBannedRepository userBannedRepository;
    private DeviceService deviceService;
    private CommunityService communityService;

    public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	public void setPromotionDao(PromotionDao promotionDao) {
		this.promotionDao = promotionDao;
	}

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setPromotionRepository(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public void setUserBannedRepository(UserBannedRepository userBannedRepository) {
        this.userBannedRepository = userBannedRepository;
    }

    public void setDeviceService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    public static enum PromotionTriggerType implements TriggerType {
        AUTO_OPT_IN;
    }

    static class PromoParams {
        public final User user;
        public final Promotion promotion;
        public final int freeTrialStartedTimestampSeconds;

        PromoParams(User user, Promotion promotion, int freeTrialStartedTimestampSeconds) {
            this.user = user;
            this.promotion = promotion;
            this.freeTrialStartedTimestampSeconds = freeTrialStartedTimestampSeconds;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("user", user)
                    .append("promotion", promotion)
                    .append("freeTrialStartedTimestampSeconds", freeTrialStartedTimestampSeconds)
                    .toString();
        }
    }

    public Promotion getActivePromotion(String promotionCode, String communityName) {
		notNull(promotionCode, "The parameter promotionCode is null");
		notNull(communityName, "The parameter communityName is null");
        LOGGER.info("Get active promotion for promo code {}, community {}", promotionCode, communityName);

		Community community = communityService.getCommunityByName(communityName);

		UserGroup userGroup = entityService.findByProperty(UserGroup.class,	UserGroup.Fields.communityId.toString(), community.getId());

        Promotion promotion = promotionRepository.getActivePromoCodePromotion(promotionCode, userGroup, Utils.getEpochSeconds(), ADD_FREE_WEEKS_PROMOTION);
        return promotion;
	}

	@Transactional(propagation= REQUIRED)
	public Promotion getPromotionForUser(final String communityName, User user) {
		LOGGER.debug("input parameters communityName, user: [{}], [{}]", communityName, user);
		
		Community community = CommunityDao.getMapAsNames().get(communityName);
		int userGroupId = UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(community.getId()).getId();
		
		List<Promotion> promotionWithFilters = promotionDao.getPromotionWithFilters(userGroupId);
		List<Promotion> promotions = new LinkedList<Promotion>();
			for (Promotion currentPromotion : promotionWithFilters) {
				List<AbstractFilter> filters = currentPromotion.getFilters();
				boolean filtered=true;
				for (AbstractFilter filter : filters) {
					if(!(filtered = filter.doFilter(user, null))) break;
				}
				if (filtered) {
					promotions.add(currentPromotion);
				}
			}
			
		Promotion resPromotion = null;
		for (Promotion promotion : promotions) {
			List<AbstractFilter> filters = promotion.getFilters();
			for (AbstractFilter abstractFilter : filters) {
				if (abstractFilter instanceof FreeTrialPeriodFilter) {
					resPromotion = promotion;
					break;
				}
			}
		}
			
		
		if (resPromotion==null) resPromotion= (promotions.size()>0)?promotions.get(0):null;
		LOGGER.info("Output parameter resPromotion=[{}]", resPromotion);
		return resPromotion;
	}
	
	@Transactional(propagation= REQUIRED)
	public User applyPromotion(User user) {
		if (null != user.getPotentialPromotion()) {
			user.setPotentialPromotion(null);
			user = entityService.updateEntity(user);
		}
		PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
		if (null!=currentPaymentDetails && null != currentPaymentDetails.getPromotionPaymentPolicy()) {
			currentPaymentDetails.setPromotionPaymentPolicy(null);
			entityService.updateEntity(currentPaymentDetails);
		}
		return user;
	}
	
	@Transactional(propagation= REQUIRED)
	public synchronized Promotion incrementUserNumber(Promotion promotion) {
		if (null != promotion) {
			promotion.setNumUsers(promotion.getNumUsers()+1);
			return entityService.updateEntity(promotion);
		}
		return null;
	}

    @Transactional(propagation = REQUIRED)
    public User applyPotentialPromo(User user){
        if (userService.canActivateVideoTrial(user)) {
            user = skipPrevDataAndApplyPromotionForO24GConsumer(user);
        }else {
            user = applyPotentialPromo(user, user.getUserGroup().getCommunity());
        }
        return user;
    }
    
    @Transactional(propagation = REQUIRED)
    public User activateVideoAudioFreeTrial(User user){
        boolean isPromotionApplied;
        if (userService.canActivateVideoTrial(user)) {
            user = skipPrevDataAndApplyPromotionForO24GConsumer(user);
            isPromotionApplied = user.isPromotionApplied();
        }else{
            throw new ServiceException("user.is.not.eligible.for.this.action", "The user isn't eligible for this action")
                    .addErrorCode(ServiceException.Error.NOT_ELIGIBLE.getCode());
        }
        if (!isPromotionApplied){
            throw new ServiceException("could.not.apply.promotion", "Couldn't apply promotion");
        }
        return user;
    }

    private User skipPrevDataAndApplyPromotionForO24GConsumer(User user){
        if(user.isOnAudioBoughtPeriod()){
            LOGGER.info("User is on audio bought period");
            user = userService.skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        }
        else if (user.isOnFreeTrial()) {
            LOGGER.info("User is on free trial");
            userService.unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        }
        else if (user.hasActivePaymentDetails()){
            LOGGER.info("User has active payment details");
            userService.unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
        }
        return applyPromotionForO24GConsumer(user);
    }

    private User applyPromotionForO24GConsumer(User user){
        Promotion promotion = setVideoAudioPromotionForO24GConsumer(user);
        LOGGER.info("Promotion to apply [{}]", promotion);
        if (isNotNull(promotion)){
            user = applyPromotionByPromoCode(user, promotion);
        }
        return user;
    }

    private Promotion setVideoAudioPromotionForO24GConsumer(User user){
        final Promotion promotion;
        final String messageCodeForPromoCode = getVideoCodeForO24GConsumer(user);
        if(StringUtils.hasText(messageCodeForPromoCode)){
            String promoCode = messageSource.getMessage(user.getCommunityRewriteUrl(), messageCodeForPromoCode,null, null, null);
            promotion = setPotentialPromoByPromoCode(user, promoCode);
        }else{
            promotion = null;
            LOGGER.error("Couldn't find promotion code for [{}]", messageCodeForPromoCode);
        }
        return promotion;
    }
    
    public String getVideoCodeForO24GConsumer(User user) {
        final String messageCodeForPromoCode;
        ContractChannel contractChannel = user.getContractChannel();
        String contract = user.getContract().name();
        if (contractChannel == null){
            LOGGER.warn("The user contract channel is null, DIRECT will be used");
            messageCodeForPromoCode = conCatLowerCase(PROMO_CODE_FOR_O2_CONSUMER_4G, contract, ".", DIRECT.name());
        }else{
            messageCodeForPromoCode = conCatLowerCase(PROMO_CODE_FOR_O2_CONSUMER_4G, contract, ".", contractChannel.name());
        }
        LOGGER.info("Message code for getting promotion code [{}]", messageCodeForPromoCode);
        return messageCodeForPromoCode;
    }

    @Transactional(propagation = REQUIRED)
    public User applyInitialPromotion(User user) {
        LOGGER.debug("input parameters user: [{}]", new Object[]{user});

        if (user == null)
            throw new NullPointerException("The parameter user is null");

        if (UserStatusDao.LIMITED.equals(user.getStatus().getName())) {

            Promotion potentialPromoCodePromotion = user.getPotentialPromoCodePromotion();
            if (potentialPromoCodePromotion != null) {
                applyPromotionByPromoCode(user, potentialPromoCodePromotion);
            }
        }
        LOGGER.debug("Output parameter user=[{}]", user);
        return user;
    }

    @Transactional(propagation = REQUIRED)
    public boolean updatePromotionNumUsers(Promotion promotion) {
        int updatedRowsCount = promotionRepository.updatePromotionNumUsers(promotion);
        if (updatedRowsCount!=1){
            throw new ServiceException("Couldn't update promotion [" + promotion +"] numUsers ");
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

        if (deviceService.isPromotedDevicePhone(community, user.getMobile(), staffCode))
            promotion = setPotentialPromoByPromoCode(user, staffCode);
        else if (deviceService.isPromotedDevicePhone(community, user.getMobile(), storeCode))
            promotion = setPotentialPromoByPromoCode(user, storeCode);
        else if (user.isO2User() || user.isVFNZUser())
            promotion = setPotentialPromoByMessageCode(user, "promotionCode");
        else
            promotion = setPotentialPromoByMessageCode(user, "defaultPromotionCode");

        return applyPromotionByPromoCode(new PromoParams(user, promotion, freeTrialStartedTimestampSeconds));
    }

    @Transactional(propagation = REQUIRED)
    public User applyPromotionByPromoCode(User user, Promotion promotion) {
        int freeTrialStartedTimestampSeconds = Utils.getEpochSeconds();
        LOGGER.info("Attempt to apply promotion using current unix time [{}] as freeTrialStartedTimestampSeconds", freeTrialStartedTimestampSeconds);
        return applyPromotionByPromoCode(new PromoParams(user, promotion, freeTrialStartedTimestampSeconds));
    }

    @Transactional(propagation = REQUIRED)
    public User applyPromotionByPromoCode(PromoParams promoParams) {
        User user = promoParams.user;
        LOGGER.info("Attempt to apply promotion [{}] for user [{}] using [{}] as freeTrialStartedTimestampSeconds", promoParams.promotion, user, promoParams.freeTrialStartedTimestampSeconds);

        if (isUserNotBanned(user)) {
            user = applyPromoForNotBannedUser(promoParams);
        } else {
            skipPotentialPromoCodePromotionApplyingForBannedUser(user);
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
        Community community = user.getUserGroup().getCommunity();
        if (code != null) {
            Promotion potentialPromoCodePromotion = getActivePromotion(code, community.getName());
            user.setPotentialPromoCodePromotion(potentialPromoCodePromotion);
            entityService.updateEntity(user);
            return potentialPromoCodePromotion;
        }
        return null;
    }

    private void logAboutPromoApplying(User user, PromoCode promoCode, int freeWeeks) {
        byte balanceAfter = (byte) (user.getSubBalance() + freeWeeks);
        AccountLog accountLog = new AccountLog(user.getId(), null, balanceAfter, PROMOTION_BY_PROMO_CODE_APPLIED);
        accountLog.setPromoCode(promoCode.getCode());
        entityService.saveEntity(accountLog);
        for (byte i = 1; i <= freeWeeks; i++) {
            entityService.saveEntity(new AccountLog(user.getId(), null, balanceAfter - i, SUBSCRIPTION_CHARGE));
        }
    }

    private User skipPotentialPromoCodePromotionApplyingForBannedUser(User user) {
        LOGGER.warn("The promotion wouldn't be applied because user is banned");
        user.setPotentialPromoCodePromotion(null);
        return entityService.updateEntity(user);
    }

    private User applyPromoForNotBannedUser(PromoParams promoParams) {
        Promotion promotion = promoParams.promotion;
        User user = promoParams.user;
        int freeTrialStartedTimestampSeconds = promoParams.freeTrialStartedTimestampSeconds;

        if (isNull(promotion)) {
            throw new IllegalArgumentException("No promotion found");
        }

        final PromoCode promoCode = promotion.getPromoCode();

        if(couldNotBeApplied(user, promoCode)){
            throw new ServiceException("Couldn't apply promotion for ["+ promoCode.getMediaType() + "] media type. Probably because last applied promotion was on the same media type");
        }

        int freeWeeks = promotion.getFreeWeeks(freeTrialStartedTimestampSeconds);
        int nextSubPayment = promotion.getFreeWeeksEndDate(freeTrialStartedTimestampSeconds);

        user.setLastPromo(promoCode);
        user.setNextSubPayment(nextSubPayment);
        user.setFreeTrialExpiredMillis(secondsToMillis(nextSubPayment));
        user.setPotentialPromoCodePromotion(null);

        if(isVideoAndMusicPromoCode(promoCode)){
            user.setVideoFreeTrialHasBeenActivated(true);
        }

        user.setStatus(UserStatusDao.getSubscribedUserStatus());
        user.setFreeTrialStartedTimestampMillis(secondsToMillis(freeTrialStartedTimestampSeconds));
        user = entityService.updateEntity(user);

        updatePromotionNumUsers(promotion);

        logAboutPromoApplying(user, promoCode, freeWeeks);
        return user.withIsPromotionApplied(true);
    }

    public boolean isUserNotBanned(User user) {
        UserBanned userBanned = getUserBanned(user.getId());
        return isNull(userBanned) || userBanned.isGiveAnyPromotion();
    }

    private UserBanned getUserBanned(Integer userId) {
        return userBannedRepository.findOne(userId);
    }

    private boolean couldNotBeApplied(User user, PromoCode currentPromoCode){
        return !currentPromoCode.getPromotion().isCouldBeAppliedMultipleTimes() && arePromotionMediaTypesTheSame(user.getLastPromo(), currentPromoCode);
    }

    private boolean arePromotionMediaTypesTheSame(PromoCode lastAppliedPromoCode, PromoCode currentPromoCode){
        boolean arePromotionMediaTypesTheSame = isNotNull(lastAppliedPromoCode) && isNotNull(currentPromoCode) && isNotNull(lastAppliedPromoCode.getMediaType())
                && lastAppliedPromoCode.getMediaType().equals(currentPromoCode.getMediaType());
        LOGGER.info("Are found and last applied promotions have the same media type: [{}]", arePromotionMediaTypesTheSame);
        return arePromotionMediaTypesTheSame;
    }

    @Transactional(propagation = REQUIRED)
    public User assignPotentialPromotion(int userId) {
        LOGGER.debug("input parameters userId: [{}]", userId);
        User user = userService.findById(userId);

        user = assignPotentialPromotion(user);

        LOGGER.debug("Output parameter user=[{}]", user);
        return user;
    }

    @Transactional(propagation = REQUIRED)
    public User assignPotentialPromotion(User existingUser) {
        LOGGER.debug("input parameters communityName: [{}]", existingUser);
        if (existingUser.getLastSuccessfulPaymentTimeMillis() == 0) {
            String communityName = communityName(existingUser);
            Promotion promotion = getPromotionForUser(communityName, existingUser);
            existingUser.setPotentialPromotion(promotion);
            existingUser = entityService.updateEntity(existingUser);
            LOGGER.info("Promotion [{}] was attached to user with id [{}]", promotion, existingUser.getId());
        }
        LOGGER.debug("Output parameter existingUser=[{}]", existingUser);
        return existingUser;
    }

    @Transactional(propagation = REQUIRED)
    public void applyPromotionByPromoCode(final User user, final String promotionCode) {
        Validate.notNull(user, "The parameter user is null");
        Validate.notNull(promotionCode, "The parameter promotionCode is null");

        LOGGER.debug("input parameters user, promotionCode, communityName: [{}], [{}]", user, promotionCode);

        Promotion userPromotion = getActivePromotion(promotionCode, communityName(user));
        if (userPromotion == null) {
            LOGGER.info("Promotion code [{}] does not exist", promotionCode);
            throw new ServiceException("Invalid promotion code. Please re-enter the code or leave the field blank");
        }

        User applyPromotionByPromoCodeUser = applyPromotionByPromoCode(user, userPromotion);

        boolean isPromotionApplied = applyPromotionByPromoCodeUser.isPromotionApplied();
        if (isPromotionApplied){
            userService.proceessAccountCheckCommandForAuthorizedUser(user.getId());
        }
    }

    public Promotion getPromotionFromRuleForAutoOptIn(User user) {
        RuleResult<Promotion> ruleResult = getRuleServiceSupport().fireRules(AUTO_OPT_IN, user);
        return ruleResult.getResult();
    }

    private String communityName(User user) {
        UserGroup userGroup = user.getUserGroup();
        Community community = userGroup.getCommunity();
        return community.getName();
    }

    private boolean isVideoAndMusicPromoCode(PromoCode promoCode) {
        return isNotNull(promoCode) && promoCode.forVideoAndAudio();
    }

    public void setCommunityService(CommunityService communityService) {
        this.communityService = communityService;
    }
}
