package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.ACCOUNT_MERGE;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;


// @author: Titov Mykhaylo (titov) 04.10.13 16:16
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class AccountLogServiceIT {

    @Resource(name = "service.AccountLogService")
    public AccountLogService accountLogServiceFixture;

    @Resource(name = "userRepository")
    public UserRepository userRepository;

    @Test
    public void shouldLogAccountMergeEvent() {
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
