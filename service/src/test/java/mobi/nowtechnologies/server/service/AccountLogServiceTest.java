package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.AccountLogRepository;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.ACCOUNT_MERGE;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.runners.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;

import static org.hamcrest.CoreMatchers.is;


/**
 * User: Titov Mykhaylo (titov) 04.10.13 15:40
 */

@RunWith(MockitoJUnitRunner.class)
public class AccountLogServiceTest {

    AccountLogService accountLogServiceFixture;

    @Mock
    AccountLogRepository accountLogRepositoryMock;

    @Before
    public void setUp() {
        accountLogServiceFixture = new AccountLogService();
        accountLogServiceFixture.accountLogRepository = accountLogRepositoryMock;
    }

    @Test
    public void shouldLogAccountMergeEvent() throws Exception {
        //given
        User user = new User();
        User removedUser = new User();

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        }).when(accountLogRepositoryMock).save(any(AccountLog.class));

        int now = DateTimeUtils.getEpochSeconds();

        //when
        AccountLog accountLog = accountLogServiceFixture.logAccountMergeEvent(user, removedUser);

        //then
        assertNotNull(accountLog);
        assertThat(accountLog.getUserId(), is(user.getId()));
        assertThat(accountLog.getBalanceAfter(), is(user.getSubBalance()));
        assertThat(accountLog.getTransactionType(), is(ACCOUNT_MERGE));
        assertTrue(accountLog.getLogTimestamp() >= now);
        assertThat(accountLog.getDescription(), is("Account was merged with " + removedUser.toString()));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotLogAccountMergeEventWhenUserIsNull() throws Exception {
        //given
        User user = null;
        User removedUser = new User();

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
