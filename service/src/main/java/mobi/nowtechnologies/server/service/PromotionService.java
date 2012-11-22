package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.dao.PromotionDao;
import mobi.nowtechnologies.server.persistence.dao.UserGroupDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.filter.FreeTrialPeriodFilter;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class PromotionService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PromotionService.class);
	
	private PromotionDao promotionDao;
	private EntityService entityService;

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	public void setPromotionDao(PromotionDao promotionDao) {
		this.promotionDao = promotionDao;
	}

	public boolean isPromoCodeActivePromotionExsist(
			String communityName) {
		if (communityName == null)
			throw new ServiceException("The parameter communityName is null");

		Community community = CommunityDao.getMapAsNames().get(communityName);

		UserGroup userGroup = entityService.findByProperty(UserGroup.class,
				UserGroup.Fields.communityId.toString(), community.getId());

		return promotionDao.isPromoCodeActivePromotionExsist(userGroup
				.getI());
	}

	public Promotion getActivePromotion(String promotionCode,
			String communityName) {
		if (promotionCode == null)
			throw new ServiceException("The parameter promotionCode is null");
		if (communityName == null)
			throw new ServiceException("The parameter communityName is null");

		Community community = CommunityDao.getMapAsNames().get(communityName);

		UserGroup userGroup = entityService.findByProperty(UserGroup.class,
				UserGroup.Fields.communityId.toString(), community.getId());

		return promotionDao.getActivePromoCodePromotion(promotionCode, userGroup.getI());
	}
	
	public List<PromoCode> getPromoCodes(final String communityName) {
		Community community = CommunityDao.getMapAsNames().get(communityName);
		return promotionDao.getActivePromoCodePromotion(UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(community.getId()).getI());
	}

	public Promotion getNoPromoCodePromotion(final String communityName) {
		Community community = CommunityDao.getMapAsNames().get(communityName);
		return promotionDao.getActiveNoPromoCodePromotion(UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(community.getId()).getI());
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
	
	/**
	 * 
	 * @param user
	 */
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
	public Promotion incrementUserNumber(Promotion promotion) {
		if (null != promotion) {
			promotion.setNumUsers(promotion.getNumUsers()+1);
			return entityService.updateEntity(promotion);
		}
		return null;
	}
}