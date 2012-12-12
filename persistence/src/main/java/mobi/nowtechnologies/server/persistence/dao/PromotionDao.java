package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.shared.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class PromotionDao extends JpaDaoSupport {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PromotionDao.class);

	public boolean isPromoCodeActivePromotionExsist(byte userGroupId) {
		String startDate = Promotion.Fields.startDate.toString();
		String endDate = Promotion.Fields.endDate.toString();
		String numUsers = Promotion.Fields.numUsers.toString();
		String maxUsers = Promotion.Fields.maxUsers.toString();
		String isActive = Promotion.Fields.isActive.toString();
		String userGroup = Promotion.Fields.userGroup.toString();
		String type = Promotion.Fields.type.toString();

		Long count = (Long) getJpaTemplate().find(
				"select count(promotion) from " + Promotion.class.getName()
						+ " promotion where promotion." + startDate
						+ "<?1 and promotion." + endDate
						+ ">?1 and (promotion." + numUsers + "<" + maxUsers
						+ " or promotion." + maxUsers + "=0) and promotion."
						+ isActive + "=true and promotion." + userGroup
						+ "=?2 and " + type + "='" + Promotion.ADD_FREE_WEEKS_PROMOTION+"'",
						Utils.getEpochSeconds(), userGroupId).get(0);

		return Long.valueOf(1L).equals(count);
	}

	public Promotion getActivePromoCodePromotion(final String promotionCode, final byte userGroupId) {
		Promotion outPromo = null;
		if (promotionCode == null)
			throw new PersistenceException(
					"The parameter promotionCode is null");
		
		
		final String promotionId = PromoCode.Fields.promotionId.toString();
		String code = PromoCode.Fields.code.toString();
		
		List<?> promotionIds = getJpaTemplate().executeFind(new JpaCallback<List>() {
			@Override
			public List doInJpa(EntityManager em)
					throws javax.persistence.PersistenceException {
				// TODO Auto-generated method stub
				return em.createNativeQuery(
				"select " + promotionId + " from tb_promoCode promoCode "
						+ " inner join "
						+ " tb_promotions promotions "
						+ " on promotions.i = promoCode.promotionId"
						+ " and promotions.userGroup = " + userGroupId
						+ " where promoCode.code='" + promotionCode + "'").getResultList();
			}
		});
				
				
		if(promotionIds.isEmpty()) return null;
		int size = promotionIds.size();
		if (size>1) throw new PersistenceException("The promotion code ["+promotionCode+"] references on ["+size+"] promotions");
		
		Byte returnedPromotionId = (Byte) promotionIds.get(0);
		
		if (returnedPromotionId==null) return null;
		
		String startDate = Promotion.Fields.startDate.toString();
		String endDate = Promotion.Fields.endDate.toString();
		String numUsers = Promotion.Fields.numUsers.toString();
		String maxUsers = Promotion.Fields.maxUsers.toString();
		String isActive = Promotion.Fields.isActive.toString();
		String userGroup = Promotion.Fields.userGroup.toString();
		String type = Promotion.Fields.type.toString();
		String i = Promotion.Fields.i.toString();

		
		List<?> promoList = getJpaTemplate().find(
				"select promotion from " + Promotion.class.getSimpleName()
				+ " promotion where promotion." + startDate
				+ "<?1 and promotion." + endDate
				+ ">?1 and (promotion." + numUsers + "<" + maxUsers
				+ " or promotion." + maxUsers + "=0) and promotion."
				+ isActive + "=true and promotion." + userGroup
				+ "=?2 and " + i + "=?3 and " + type + "='"
				+ Promotion.ADD_FREE_WEEKS_PROMOTION + "'", Utils.getEpochSeconds(),
		userGroupId, returnedPromotionId.byteValue());
		
		if (promoList.size() == 1)
			outPromo = (Promotion)promoList.get(0);
		else if (promoList.size() > 1)
			throw new PersistenceException( "More than one promotion was find for [" + promotionCode + "] promotion code" );
			
		return outPromo;
	}
	
	@SuppressWarnings("unchecked")
	 public List<PromoCode> getActivePromoCodePromotion(final byte userGroupId) {
		  List<PromoCode> promotions = getJpaTemplate().find(
				  "select promoCode from  PromoCode promoCode join promoCode.promotion p where p.showPromotion=true and (p.numUsers < p.maxUsers or p.maxUsers=0) and p.isActive=true and p.userGroup=?1 and p.type=?2 and p.startDate<?3 and p.endDate>?3",
				  userGroupId, Promotion.ADD_FREE_WEEKS_PROMOTION, Utils.getEpochSeconds());
		  return promotions;
	 }
	
	public Promotion getActiveNoPromoCodePromotion(final byte userGroupId) {
		Promotion outPromo = null;
		
		String startDate = Promotion.Fields.startDate.toString();
		String endDate = Promotion.Fields.endDate.toString();
		String numUsers = Promotion.Fields.numUsers.toString();
		String maxUsers = Promotion.Fields.maxUsers.toString();
		String isActive = Promotion.Fields.isActive.toString();
		String userGroup = Promotion.Fields.userGroup.toString();
		String type = Promotion.Fields.type.toString();
		
		List<?> promoList = getJpaTemplate().find(
				"select promotion from " + Promotion.class.getSimpleName()
				+ " promotion where promotion." + startDate
				+ "<?1 and promotion." + endDate
				+ ">?1 and (promotion." + numUsers + "<" + maxUsers
				+ " or promotion." + maxUsers + "=0) and promotion."
				+ isActive + "=true and promotion." + userGroup
				+ "=?2 and " + type + "='"
				+ Promotion.ADD_SUBBALANCE_PROMOTION + "'", Utils.getEpochSeconds(),
		userGroupId);
		
		if (promoList.size() == 1)
			outPromo = (Promotion)promoList.get(0);
		else if (promoList.size() > 1)
			throw new PersistenceException( "More than one promotion was find for [" + userGroupId + "] user group" );
		return outPromo;
	 }
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	public Promotion getActivePromotionByType(byte userGroup, String type) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	public List<Promotion> getPromotionWithFilters(final byte userGroupId) {
		LOGGER.debug("input parameters userGroupId: [{}]", userGroupId);
		int currentTime = Utils.getEpochSeconds();
		final List<Promotion> promotionWithFilters = getJpaTemplate().findByNamedQuery(Promotion.NQ_GET_PROMOTION_WITH_FILTER, currentTime, userGroupId);
		LOGGER.info("Output parameter promotionWithFilters=[{}]", promotionWithFilters);
		return promotionWithFilters;
	}
}