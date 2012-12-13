package mobi.nowtechnologies.server.persistence.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.Offer;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.TransactionType;

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
public class AccountLogDao extends JpaDaoSupport {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountLogDao.class);

	private EntityDao entityDao;

	public void setEntityDao(EntityDao entityDao) {
		this.entityDao = entityDao;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public AccountLog logAccountEvent(int userId, int balanceAfter, Media relatedMedia, SubmittedPayment submittedPayment, TransactionType accountLogType, Offer offer) {
		if (accountLogType == null)
			throw new PersistenceException("The parameter accountLogType is null");
		LOGGER.debug("input parameters userId, balanceAfter, relatedMedia, submittedPayment, accountLogType, offer: [{}], [{}], [{}], [{}], [{}], [{}]", new Object[] { userId,
				balanceAfter, relatedMedia, submittedPayment, accountLogType, offer });
		AccountLog accountLog = new AccountLog();
		accountLog.setUserId(userId);
		accountLog.setTransactionType(accountLogType);
		accountLog.setBalanceAfter(balanceAfter);
		accountLog.setMedia(relatedMedia);
		accountLog.setSubmittedPayment(submittedPayment);
		accountLog.setLogTimestamp(Utils.getEpochSeconds());
		accountLog.setOffer(offer);

		entityDao.saveEntity(accountLog);

		LOGGER.info("Output parameter accountLog=[{}]", accountLog);
		return accountLog;
	}

	@SuppressWarnings("unchecked")
	public List<AccountLog> findByUserIdOrderedByLogIdDesc(final int userId, final Integer maxResults) {
		LOGGER.debug("input parameters userId: [{}], [{}]", userId, maxResults);
		
		List<AccountLog> accountLogs = getJpaTemplate().executeFind(new JpaCallback<List>() {
			@Override
			public List<AccountLog> doInJpa(EntityManager entityManager) throws javax.persistence.PersistenceException {
				Query query = entityManager.createNamedQuery(AccountLog.NQ_FIND_BY_USER_ID_ORDERED_BY_LOG_ID_DESC);
				query.setParameter(1, userId);
				query.setFirstResult(0);
				if (maxResults!=null) query.setMaxResults(maxResults);
				List<AccountLog> accountLogs = query.getResultList();
				return accountLogs;
			}
		});
		
		LOGGER.debug("Output parameter accountLogs=[{}]", accountLogs);
		return accountLogs;
	}

	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public List<AccountLog> findByUserAndLogType(final int userId, final TransactionType transactionType) {
		LOGGER.debug("input parameters userId, transactionType: [{}], [{}]", userId, transactionType);
		final Object[] values = new Object[] {new Integer(userId), transactionType};
		List<AccountLog> accountLogs = getJpaTemplate().findByNamedQuery(AccountLog.NQ_FIND_BY_USER_AND_LOG_TYPE, values);
		LOGGER.info("Output parameter accountLogs=[{}]", accountLogs);
		return accountLogs;
	}
}
