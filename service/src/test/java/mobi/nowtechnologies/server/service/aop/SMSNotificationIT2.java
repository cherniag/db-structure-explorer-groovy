package mobi.nowtechnologies.server.service.aop;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.payment.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.UserService;

import javax.annotation.Resource;

import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.powermock.modules.junit4.rule.PowerMockRule;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/dao-test.xml", "/META-INF/service-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class SMSNotificationIT2 {

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    UserNotificationService userNotificationServiceMock;
    @Resource(name = "service.SpyUserService")
    private UserService userServiceSpy;
    @Resource(name = "smsNotificationAspect")
    private SMSNotification smsNotificationAspectFixture;

    @Before
    public void setUp() {
        userNotificationServiceMock = mock(UserNotificationService.class);

        smsNotificationAspectFixture.setUserNotificationService(userNotificationServiceMock);
        smsNotificationAspectFixture.setUserService(userServiceSpy);
    }

    @Test
    public void testUpdateLastBefore48SmsMillis_Success() throws Exception {

        long lastBefore48SmsMillis = Long.MAX_VALUE;
        int userId = Integer.MAX_VALUE;

        User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), null);
        user.setId(userId);

        doReturn(user).when(userServiceSpy).findById(eq(userId));
        doNothing().when(userServiceSpy).updateLastBefore48SmsMillis(lastBefore48SmsMillis, userId);

        Future<Boolean> result = new AsyncResult<Boolean>(Boolean.TRUE);

        doReturn(result).when(userNotificationServiceMock).sendLowBalanceWarning(eq(user));

        userServiceSpy.updateLastBefore48SmsMillis(lastBefore48SmsMillis, userId);

        //verify(userServiceSpy, times(1)).updateLastBefore48SmsMillis(lastBefore48SmsMillis, userId);
        //verify(userServiceSpy, times(1)).findById(eq(userId));
        verify(userNotificationServiceMock, times(2)).sendLowBalanceWarning(eq(user));

    }

}
