package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.dao.PromotionDao;
import mobi.nowtechnologies.server.persistence.dao.UserGroupDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.filter.FreeTrialPeriodFilter;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ContractChannel;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

import static mobi.nowtechnologies.server.persistence.domain.Promotion.*;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.shared.Utils.concatLowerCase;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.*;
import static org.apache.commons.lang.Validate.notNull;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class PromotionService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PromotionService.class);

    private static final String PROMO_CODE_FOR_O2_CONSUMER_4G = "promoCode.for.o2.consumer.4g.";
	
	private PromotionDao promotionDao;
	private EntityService entityService;
    private UserService userService;
    private CommunityResourceBundleMessageSource messageSource;
    private PromotionRepository promotionRepository;

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

	public Promotion getActivePromotion(String promotionCode, String communityName) {
		notNull(promotionCode, "The parameter promotionCode is null");
		notNull(communityName, "The parameter communityName is null");

		Community community = CommunityDao.getMapAsNames().get(communityName);

		UserGroup userGroup = entityService.findByProperty(UserGroup.class,
				UserGroup.Fields.communityId.toString(), community.getId());

        Promotion promotion = promotionRepository.getActivePromoCodePromotion(promotionCode, userGroup, Utils.getEpochSeconds(), ADD_FREE_WEEKS_PROMOTION);
        return promotion;
	}
	
	public List<PromoCode> getPromoCodes(final String communityName) {
		Community community = CommunityDao.getMapAsNames().get(communityName);
		return promotionDao.getActivePromoCodePromotion(UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(community.getId()).getI());
	}
	
	/***
	 * Method returns the first promotion linked with user
	 * @param communityName
	 * @param user
	 * @return the first available promotion, other wise it returns null
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public Promotion getPromotionForUser(final String communityName, User user) {
		LOGGER.debug("input parameters communityName, user: [{}], [{}]", communityName, user);
		
		Community community = CommunityDao.getMapAsNames().get(communityName);
		byte userGroupId = UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(community.getId()).getI();
		
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
	
	@Transactional(propagation=Propagation.REQUIRED)
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
	
	@Transactional(propagation=Propagation.REQUIRED)
	public synchronized Promotion incrementUserNumber(Promotion promotion) {
		if (null != promotion) {
			promotion.setNumUsers(promotion.getNumUsers()+1);
			return entityService.updateEntity(promotion);
		}
		return null;
	}

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean applyO2PotentialPromoOf4ApiVersion(User user, boolean isO2User){
        boolean isPromotionApplied;
        if (user.is4G() && (user.isO2PAYGConsumer() || user.isO2PAYMConsumer()) && (user.isO2Indirect() || user.isO2Direct()|| isNull(user.getContractChannel()))) {
            isPromotionApplied = applyPromotionForO24GConsumer(user);
        }else {
            isPromotionApplied = userService.applyO2PotentialPromo(isO2User, user, user.getUserGroup().getCommunity());
        }
        return isPromotionApplied;
    }

    private boolean applyPromotionForO24GConsumer(User user){
        boolean isPromotionApplied = false;
        Promotion promotion = setVideoAudioPromotionForO24GConsumer(user);
        if (promotion != null){
            isPromotionApplied = userService.applyPromotionByPromoCode(user, promotion);
        }
        return isPromotionApplied;
    }

    private Promotion setVideoAudioPromotionForO24GConsumer(User user){
        final Promotion promotion;
        final String messageCodeForPromoCode = getVideoCodeForO24GConsumer(user);
        if(StringUtils.hasText(messageCodeForPromoCode)){
            String promoCode = messageSource.getMessage(messageCodeForPromoCode, null);
            promotion = userService.setPotentialPromo(user, promoCode);
        }else{
            promotion = null;
            LOGGER.error("Couldn't find promotion code [{}]", messageCodeForPromoCode);
        }
        return promotion;
    }

    public String getVideoCodeForO24GConsumer(User user) {
        final String messageCodeForPromoCode;
        ContractChannel contractChannel = user.getContractChannel();
        String contract = user.getContract().name();
        if (contractChannel == null){
            messageCodeForPromoCode = concatLowerCase(PROMO_CODE_FOR_O2_CONSUMER_4G, contract, ".", DIRECT.name());
            LOGGER.info("The user contract channel is null, so the message code for getting promo code will be default [{}]", messageCodeForPromoCode);
        }else{
            messageCodeForPromoCode = concatLowerCase(PROMO_CODE_FOR_O2_CONSUMER_4G, contract, ".", contractChannel.name());
        }
        return messageCodeForPromoCode;
    }
}