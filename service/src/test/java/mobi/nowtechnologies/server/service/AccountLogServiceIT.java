package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static junit.framework.Assert.assertNotNull;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.ACCOUNT_MERGE;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * User: Titov Mykhaylo (titov)
 * 04.10.13 16:16
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class AccountLogServiceIT {

    @Resource(name = "service.AccountLogService")
    public AccountLogService accountLogServiceFixture;

    @Resource(name = "userRepository")
    public UserRepository userRepository;

    @Test
    public void shouldLogAccountMergeEvent(){
        //given
        User user = userRepository.save(UserFactory.createUser(ActivationStatus.ACTIVATED));
        User removedUser = new User();

        //when
        AccountLog accountLog = accountLogServiceFixture.logAccountMergeEvent(user, removedUser);

        //then
        assertNotNull(accountLog);
        assertThat(accountLog.getId(), is(notNullValue()));
        assertThat(accountLog.getUserId(), is(user.getId()));
        assertThat(accountLog.getBalanceAfter(), is(user.getSubBalance()));
        assertThat(accountLog.getTransactionType(), is(ACCOUNT_MERGE));
        assertThat(accountLog.getLogTimestamp(), is(notNullValue()));
        assertThat(accountLog.getDescription(), is("Account was merged with " + removedUser.toString()));
    }
}
