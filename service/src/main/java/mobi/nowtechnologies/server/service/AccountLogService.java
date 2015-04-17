package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.PersistenceException;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.AccountLogRepository;
import mobi.nowtechnologies.server.shared.enums.TransactionType;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.ACCOUNT_MERGE;

import javax.annotation.Resource;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Titov Mykhaylo (titov)
 */
public class AccountLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountLogService.class);

    @Resource
    AccountLogRepository accountLogRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public AccountLog logAccountMergeEvent(User user, User removedUser) {
        AccountLog accountLog = new AccountLog(user.getId(), null, user.getSubBalance(), ACCOUNT_MERGE);
        accountLog.setDescription("Account was merged with " + removedUser.toString());
        return accountLogRepository.save(accountLog);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AccountLog logAccountEvent(int userId, int balanceAfter, Media relatedMedia, SubmittedPayment relatedPayment, TransactionType accountLogType) {
        if (accountLogType == null) {
            throw new PersistenceException("The parameter accountLogType is null");
        }
        LOGGER.debug("input parameters userId, balanceAfter, relatedMedia, relatedPaymentUID, accountLogType: [{}], [{}], [{}], [{}], [{}]", userId, balanceAfter, relatedMedia, relatedPayment,
                     accountLogType);

        AccountLog entity = new AccountLog(userId, relatedPayment, balanceAfter, accountLogType, relatedMedia);
        AccountLog accountLog = accountLogRepository.save(entity);

        LOGGER.debug("Output parameter accountLog=[{}]", accountLog);
        return accountLog;
    }

    @Transactional(readOnly = true)
    public List<AccountLog> findByUserId(Integer userId) {
        LOGGER.debug("input parameters userId: [{}]", userId);

        if (userId == null) {
            throw new NullPointerException("The parameter userId is null");
        }

        List<AccountLog> accountLogs = accountLogRepository.findByUserId(userId);

        return accountLogs;
    }

}