package mobi.nowtechnologies.server.persistence.dao;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.Operator;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.PromotionPaymentPolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class PaymentDetailsDao extends JpaDaoSupport {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PaymentDetailsDao.class.getName());
			
	private EntityDao entityDao;
	
	public void setEntityDao(EntityDao entityDao) {
		this.entityDao = entityDao;
	}

	@SuppressWarnings("unchecked")
	public PaymentDetails findPaymentDetails(String paymentType, int userId) {
		if (paymentType == null)
			throw new PersistenceException("The parameter paymentType is null");

		LOGGER.debug("input parameters paymentType, userId: [{}], [{}]",
				new Object[] { paymentType,userId });

		PaymentDetails paymentDetails = null;
		List<PaymentDetails> paymentDetailsList = getJpaTemplate()
				.findByNamedQuery(
						"PaymentDetails.getPaymentDetailsByPaymentType",
						new Object[] { paymentType, userId});

		if (paymentDetailsList.size() > 1)
			throw new PersistenceException(
					"There are more than one paymentDetails with paymentType = ["
							+ paymentType + "] ");
		else if (paymentDetailsList.size() == 1)
			paymentDetails = paymentDetailsList.get(0);

		LOGGER.debug("Output parameter paymentDetails=[{}]", paymentDetails);
		return paymentDetails;
	}
	
	@Transactional(readOnly=true)
	@SuppressWarnings("unchecked")
	public List<MigPaymentDetails> findMigPaymentDetails(String migPhoneNumber) throws DataAccessException {
		return getJpaTemplate().findByNamedQuery(MigPaymentDetails.NQ_GET_PAYMENT_DETAILS_BY_PHONENUMBER, migPhoneNumber);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public PaymentDetails update(PaymentDetails paymentDetails) throws DataAccessException {
		return getJpaTemplate().merge(paymentDetails);
	}
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	public PromotionPaymentPolicy getPromotionPaymentPolicy(Promotion promotion, PaymentPolicy paymentPolicy) {
		List<PromotionPaymentPolicy> promotionPayments = getJpaTemplate().findByNamedQuery(PromotionPaymentPolicy.NQ_GET_PROMOTION_PAYMENT_WITH_PAYMENT_POLICY, promotion, paymentPolicy);
		return (null!=promotionPayments && promotionPayments.size()>0)?promotionPayments.get(0):null;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	public List<Operator> getAvailableOperators(Community community, String paymentType) {
		List<Operator> result = getJpaTemplate().findByNamedQuery(PaymentPolicy.GET_OPERATORS_LIST, (int)community.getId(), paymentType);
		return (null!=result && result.size()>0)?result:null;
	}

	public List<PaymentDetails> find(int userId, PaymentDetailsType paymentDetailsType) {
		LOGGER.debug("input parameters userId, paymentDetailsType: [{}], [{}]", userId, paymentDetailsType);
		
		List<PaymentDetails> paymentDetails = getJpaTemplate().findByNamedQuery(PaymentDetails.FIND_BY_USER_ID_AND_PAYMENT_DETAILS_TYPE, userId, paymentDetailsType);
		
		LOGGER.debug("Output parameter [{}]", paymentDetails);
		return paymentDetails;
	}

	public PaymentDetails find(Long paymentDetailsId) {
		LOGGER.debug("input parameters paymentDetailsId: [{}]", paymentDetailsId);
		
		PaymentDetails paymentDetails = entityDao.findById(PaymentDetails.class, paymentDetailsId);
		LOGGER.debug("Output parameter [{}]", paymentDetails);
		return paymentDetails;
	}
}
