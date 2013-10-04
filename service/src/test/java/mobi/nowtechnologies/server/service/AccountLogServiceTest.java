package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.AccountLogRepository;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertNotNull;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;

/**
 * User: Titov Mykhaylo (titov)
 * 04.10.13 15:40
 */
@PrepareForTest(Utils.class)
@RunWith(PowerMockRunner.class)
public class AccountLogServiceTest {

    AccountLogService accountLogServiceFixture;

    @Mock
    AccountLogRepository accountLogRepositoryMock;

    @Before
    public void setUp(){
        accountLogServiceFixture = new AccountLogService();
        accountLogServiceFixture.setAccountLogRepository(accountLogRepositoryMock);
    }

    @Test
    public void shouldLogAccountMergeEvent() throws Exception {
        //given
        User user = new User();
        User removedUser = new User();

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.getEpochSeconds()).thenReturn(Integer.MAX_VALUE);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        }).when(accountLogRepositoryMock).save(any(AccountLog.class));

        //when
        AccountLog accountLog = accountLogServiceFixture.logAccountMergeEvent(user, removedUser);

        //then
        assertNotNull(accountLog);
        assertThat(accountLog.getUserId(), is(user.getId()));
        assertThat(accountLog.getBalanceAfter(), is(user.getSubBalance()));
        assertThat(accountLog.getTransactionType(), is(ACCOUNT_MERGE));
        assertThat(accountLog.getLogTimestamp(), is(Integer.MAX_VALUE));
        assertThat(accountLog.getDescription(), is("Account was merged with " + removedUser.toString()));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotLogAccountMergeEventWhenUserIsNull() throws Exception {
        //given
        User user = new User();
        User removedUser = new User();

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.getEpochSeconds()).thenReturn(Integer.MAX_VALUE);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        }).when(accountLogRepositoryMock).save(any(AccountLog.class));

        //when
        accountLogServiceFixture.logAccountMergeEvent(user, removedUser);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotLogAccountMergeEventWhenRemovedUserIsNull() throws Exception {
        //given
        User user = new User();
        User removedUser = null;

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.getEpochSeconds()).thenReturn(Integer.MAX_VALUE);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        }).when(accountLogRepositoryMock).save(any(AccountLog.class));

        //when
        accountLogServiceFixture.logAccountMergeEvent(user, removedUser);
    }
}
