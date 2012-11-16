package mobi.nowtechnologies.server.persistence.dao;

import java.util.ArrayList;
import java.util.List;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.support.JpaDaoSupport;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class PaymentPolicyDao extends JpaDaoSupport {
	private static final Logger LOGGER = 
		LoggerFactory.getLogger(PaymentPolicyDao.class);
	
	@SuppressWarnings("unchecked")
	public List<PaymentPolicy> getPaymentPoliciesGroupdeByPaymentType(
			String communityName) {
		if (communityName == null)
			throw new PersistenceException(
					"The parameter communityName is null");
		LOGGER.debug("input parameters communityName: [{}]",
				new Object[] { communityName });
		
		Community community = CommunityDao.getMapAsNames().get(communityName);
		if (community==null) return new ArrayList<PaymentPolicy>();
		Integer communityId = Integer.valueOf(community.getId());
		
		List<PaymentPolicy> paymentPolicies = getJpaTemplate().findByNamedQuery("PaymentPolicy.getPaymentPoliciesForCommunityGroupedByPaymentType",  new Object[] {communityId});
		
		LOGGER.debug("Output parameter paymentPolicies=[{}]", paymentPolicies);
		return paymentPolicies;
	}

//	@SuppressWarnings("unchecked")
//	public PaymentPolicy getPaymentPolicy(int operatorId,
//			PaymentSystem paymentSystem, int communityId) {
//		if (paymentSystem == null)
//			throw new PersistenceException("The parameter paymentSystem is null");
//		Object[] argArray = new Object[] { operatorId, paymentSystem,
//				communityId };
//
//		LOGGER
//				.debug(
//						"Input parameters: operatorId = [{}], paymentSystem = [{}], communityId = [{}] ",
//						argArray);
//
//		List<PaymentPolicy> paymentPolicies;
//		Object[] queryArgArray;
//		if (paymentSystem.equals(PaymentSystem.Mig)) {
//			queryArgArray = new Object[] { operatorId, paymentSystem, communityId};
//
//			paymentPolicies = getJpaTemplate().findByNamedQuery(
//					"PaymentPolicy.getPaymentPolicyForMigPaymentType", queryArgArray);
//		} else {
//			queryArgArray = new Object[] { paymentSystem, communityId };
//			paymentPolicies = getJpaTemplate().findByNamedQuery(
//					"PaymentPolicy.getPaymentPolicyForNotMigPaymentType",
//					queryArgArray);
//		}
//
//		PaymentPolicy paymentPolicy = null;
//		if (paymentPolicies.size() > 1)
//			throw new PersistenceException(
//					"There are more than one paymentPolicies in the table for operatorId = ["
//							+ operatorId + "], paymentSystem = ["
//							+ paymentSystem + "], communityId = ["
//							+ communityId + "]");
//		else if (paymentPolicies.size() == 1)
//			paymentPolicy = paymentPolicies.get(0);
//
//		LOGGER.debug("Output parameter paymentPolicy=[{}]", paymentPolicy);
//		return paymentPolicy;
//	}
	
	@SuppressWarnings("unchecked")
	public PaymentPolicy getPaymentPolicy(int operatorId,
			String paymentType, int communityId) {
		if (paymentType == null)
			throw new PersistenceException("The parameter paymentType is null");
		Object[] argArray = new Object[] { operatorId, paymentType,
				communityId };

		LOGGER
				.debug(
						"Input parameters: operatorId = [{}], paymentType = [{}], communityId = [{}] ",
						argArray);

		List<PaymentPolicy> paymentPolicies;
		Object[] queryArgArray;
		if (paymentType.equals(UserRegInfo.PaymentType.PREMIUM_USER)) {
			queryArgArray = new Object[] { operatorId, paymentType, communityId};

			paymentPolicies = getJpaTemplate().findByNamedQuery(
					"PaymentPolicy.getPaymentPolicyForMigPaymentType", queryArgArray);
		} else {
			queryArgArray = new Object[] { paymentType, communityId };
			paymentPolicies = getJpaTemplate().findByNamedQuery(
					"PaymentPolicy.getPaymentPolicyForNotMigPaymentType",
					queryArgArray);
		}

		PaymentPolicy paymentPolicy = null;
		if (paymentPolicies.size() > 1)
			throw new PersistenceException(
					"There are more than one paymentPolicies in the table for operatorId = ["
							+ operatorId + "], paymentType = ["
							+ paymentType + "], communityId = ["
							+ communityId + "]");
		else if (paymentPolicies.size() == 1)
			paymentPolicy = paymentPolicies.get(0);

		LOGGER.debug("Output parameter paymentPolicy=[{}]", paymentPolicy);
		return paymentPolicy;
	}
	
	
	public PaymentPolicy getPaymentPolicy(byte communityId, String paymentType) throws PersistenceException {
		List list = getJpaTemplate().findByNamedQuery("PaymentPolicy.getPaymentPoliciesByCommunityAndPaymentType", new Object[] { paymentType, communityId});
		if (list!=null && list.size()>0)
			return (PaymentPolicy) list.get(0);
		return null;
	}

	public List<PaymentPolicy> getPaymentPolicies(String communityURL, boolean availableInStore) {
		LOGGER.debug("input parameters communityURL, availableInStore: [{}], [{}]", communityURL, availableInStore);
		Community community = CommunityDao.getMapAsUrls().get(communityURL.toUpperCase());

		List<PaymentPolicy> offerPaymentPolicies = getJpaTemplate().findByNamedQuery(PaymentPolicy.GET_BY_COMMUNITY_AND_AVAILABLE_IN_STORE, new Object[] { community, availableInStore});
		LOGGER.debug("Output parameter [{}]", offerPaymentPolicies);
		return offerPaymentPolicies;
	}

}