package mobi.nowtechnologies.server.persistence.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.AbstractPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@Deprecated
public class PaymentDao extends JpaDaoSupport {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PaymentDao.class.getName());
	
	private UserRepository userRepository;
	
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public PendingPayment savePendingPayment(PendingPayment pendingPayment) {
		return getJpaTemplate().merge(pendingPayment);
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
