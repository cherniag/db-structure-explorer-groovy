package mobi.nowtechnologies.server.service.aop;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.Future;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.persistence.domain.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.UserService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/shared.xml", "/META-INF/dao-test.xml", "/META-INF/service-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class SMSNotificationIT2 {
	
	@Rule
	public PowerMockRule rule = new PowerMockRule();
	
	@Resource(name="service.SpyUserService")
	private UserService userServiceSpy;
	
	@Resource(name="smsNotificationAspect")
	private SMSNotification smsNotificationAspectFixture;
	
	UserNotificationService userNotificationServiceMock;
	
	@Before
	public void setUp() {
		userNotificationServiceMock = mock(UserNotificationService.class);
		
		smsNotificationAspectFixture.setUserNotificationService(userNotificationServiceMock);
		smsNotificationAspectFixture.setUserService(userServiceSpy);
	}
	
	@Test
	public void testUpdateLastBefore48SmsMillis_Success()
			throws Exception {	
		
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
