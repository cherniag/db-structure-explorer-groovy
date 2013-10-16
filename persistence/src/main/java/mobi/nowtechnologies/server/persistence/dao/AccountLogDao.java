package mobi.nowtechnologies.server.persistence.dao;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.Offer;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.TransactionType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	@SuppressWarnings({ "deprecation", "unchecked" })
	public List<AccountLog> findByUserAndLogType(final int userId, final TransactionType transactionType) {
		LOGGER.debug("input parameters userId, transactionType: [{}], [{}]", userId, transactionType);
		final Object[] values = new Object[] {new Integer(userId), transactionType};
		List<AccountLog> accountLogs = getJpaTemplate().findByNamedQuery(AccountLog.NQ_FIND_BY_USER_AND_LOG_TYPE, values);
		LOGGER.info("Output parameter accountLogs=[{}]", accountLogs);
		return accountLogs;
	}
}
