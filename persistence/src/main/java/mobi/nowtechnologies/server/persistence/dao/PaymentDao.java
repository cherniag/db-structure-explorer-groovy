package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.AppConstants;
import mobi.nowtechnologies.server.shared.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@Deprecated
public class PaymentDao extends JpaDaoSupport {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PaymentDao.class.getName());
	
	public static enum TxType {
		PAYMENT(1),
		REFUND(2),
		REPEAT(3),
		AUTHENTICATE(4),
		AUTHORISE(5),
		CANCEL(6),
		RELEASE(7),
		DEFERRED(8);
		
		private int code;
		
		private TxType(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}
	
	private UserRepository userRepository;
	
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	

	public boolean isUserAlreadyPaidSuccessfully(int userID) {
		Long foundedRecordsCount = (Long) getJpaTemplate().find(
				"select count(payment) from " + Payment.class.getSimpleName()
						+ " payment where payment."
						+ Payment.Fields.userUID.toString() + "=?1"
						+ " and payment." + Payment.Fields.status.toString()
						+ "='" + AppConstants.STATUS_OK + "' and (payment."
						+ Payment.Fields.txType.toString() + "="
						+ TxType.PAYMENT.getCode() + " or payment."
						+ Payment.Fields.txType.toString() + "="
						+ TxType.RELEASE.getCode() + ")",userID).get(0);

		LOGGER.info("There are [" + foundedRecordsCount
				+ "] payments with status [" + AppConstants.STATUS_OK
				+ "] and txType=[" + TxType.PAYMENT.getCode() + "] or txType=["
				+ TxType.RELEASE.getCode() + "] for userID=[" + userID + "]");
		return !(Long.valueOf(0L).equals(foundedRecordsCount));
	}


	public PayPalPayment getLastDeferedPayPalPayment(int userID) {
		@SuppressWarnings("unchecked")
		List<PayPalPayment> payPalPayments =  getJpaTemplate().find(
				"select payment from " + PayPalPayment.class.getSimpleName()
						+ " payment where payment."
						+ Payment.Fields.userUID.toString() + "=?1"
						+" and payment."
						+ Payment.Fields.status.toString() + "= '"
						+ AppConstants.STATUS_USER_CONFIRMED + "' order by payment."+Payment.Fields.timestamp.toString()+" desc",userID);
		
		
		if (payPalPayments.isEmpty()) return null;
		return  payPalPayments.get(0);
	}


	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<User> getUsersForPendingPayment() {
		List<User> users = userRepository.getUsersForPendingPayment(Utils.getEpochSeconds());
		
		return users;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<User> getUsersForRetryPayment() {
		return getJpaTemplate().execute(new JpaCallback<List<User>>() {
			@Override
			public List<User> doInJpa(EntityManager em) throws PersistenceException {
				em.clear();
				Query queryObject = em.createNamedQuery(User.NQ_GET_USERS_FOR_RETRY_PAYMENT);
				getJpaTemplate().prepareQuery(queryObject);
				return queryObject.getResultList();
			}
		});
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public PendingPayment savePendingPayment(PendingPayment pendingPayment) {
		return getJpaTemplate().merge(pendingPayment);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<PendingPayment> getPendingPayments() {
		return getJpaTemplate().findByNamedQuery(PendingPayment.NQ_GET_PENDING_PAYMENTS);
	}


	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<PendingPayment> getExpiredPendingPayments() {
		return getJpaTemplate().findByNamedQuery(PendingPayment.NQ_GET_EXPIRED_PENDING_PAYMENTS, System.currentTimeMillis());
	}
	
	@SuppressWarnings("unchecked")
	public List<AbstractPayment> findByUserIdOrderedByTimestampDesc(final int userId, final Integer maxResults) {
		LOGGER.debug("input parameters userId: [{}], [{}]", userId, maxResults);
		
		List<AbstractPayment> abstractPayments = getJpaTemplate().executeFind(new JpaCallback<List>() {
			@Override
			public List<AbstractPayment> doInJpa(EntityManager entityManager) throws javax.persistence.PersistenceException {
				Query query = entityManager.createNamedQuery(SubmittedPayment.NQ_FIND_BY_USER_ID_ORDERED_BY_TIMESTAMP_DESC);
				query.setParameter(1, userId);
				List<AbstractPayment> abstractPayments = query.getResultList();
				if(maxResults != null && abstractPayments.size() > maxResults)
					abstractPayments = abstractPayments.subList(0, maxResults);
				return abstractPayments;
			}
		});
		
		LOGGER.debug("Output parameter abstractPayments=[{}]", abstractPayments);
		return abstractPayments;
	}
}