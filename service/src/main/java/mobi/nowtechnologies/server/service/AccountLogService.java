package mobi.nowtechnologies.server.service;

import java.util.LinkedList;
import java.util.List;

import mobi.nowtechnologies.server.persistence.dao.AccountLogDao;
import mobi.nowtechnologies.server.persistence.dao.PersistenceException;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.AccountLogRepository;
import mobi.nowtechnologies.server.shared.dto.web.PaymentHistoryItemDto;
import mobi.nowtechnologies.server.shared.enums.TransactionType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static mobi.nowtechnologies.server.shared.Utils.*;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.*;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class AccountLogService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountLogService.class);

	private AccountLogDao accountLogDao;
	private AccountLogRepository accountLogRepository;

	public void setAccountLogDao(AccountLogDao accountLogDao) {
		this.accountLogDao = accountLogDao;
	}
	
	public void setAccountLogRepository(AccountLogRepository accountLogRepository) {
		this.accountLogRepository = accountLogRepository;
	}

    @Transactional(propagation=Propagation.REQUIRED)
    public AccountLog logAccountMergeEvent(User user, User removedUser) {
        String description = "Account was merged with " + removedUser.toString();
        return accountLogRepository.save(new AccountLog().withUser(user).withBalanceAfter(user.getSubBalance()).withTransactionType(ACCOUNT_MERGE).withDescription(description).withLogTimestamp(getEpochSeconds()));
    }

	@Transactional(propagation=Propagation.REQUIRED)
	public AccountLog logAccountEvent(int userId, int balanceAfter, Media relatedMedia, SubmittedPayment relatedPayment, TransactionType accountLogType, Offer offer) {
		if (accountLogType == null)
			throw new PersistenceException("The parameter accountLogType is null");
		LOGGER.debug("input parameters userId, balanceAfter, relatedMedia, relatedPaymentUID, accountLogType, offer: [{}], [{}], [{}], [{}], [{}], [{}]", new Object[] { userId,
				balanceAfter, relatedMedia, relatedPayment, accountLogType, offer });

		AccountLog accountLog = accountLogDao.logAccountEvent(userId, balanceAfter, relatedMedia, relatedPayment, accountLogType, offer);

		LOGGER.debug("Output parameter accountLog=[{}]", accountLog);
		return accountLog;
	}
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	public List<Integer> getRelatedMediaUIDsByLogType(final int userId, final TransactionType transactionType) {
		LOGGER.debug("input parameters userId, transactionType: [{}], [{}]", userId, transactionType);
		List<Integer> result = new LinkedList<Integer>();
		List<AccountLog> accountLogs = accountLogDao.findByUserAndLogType(userId, transactionType);
		for (AccountLog log : accountLogs) {
			result.add(log.getRelatedMediaUID());
		}
		LOGGER.info("Output parameter result=[{}]", result);
		return result ;
	}

	@Transactional(readOnly=true)
	public List<AccountLog> findByUserId(Integer userId) {
		LOGGER.debug("input parameters userId: [{}]", userId);
		
		if (userId == null)
			throw new NullPointerException("The parameter userId is null");
		
		List<AccountLog> accountLogs = accountLogRepository.findByUserId(userId);
		
		return accountLogs;
	}

}