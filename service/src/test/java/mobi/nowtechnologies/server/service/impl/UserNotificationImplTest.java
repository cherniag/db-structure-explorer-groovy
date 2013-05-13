package mobi.nowtechnologies.server.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.concurrent.Future;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.DeviceTypeFactory;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.aop.SMSNotification;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.shared.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * The class <code>UserNotificationImplTest</code> contains tests for the class
 * <code>{@link UserNotificationServiceImpl}</code>.
 * 
 * @generatedBy CodePro at 04.09.12 13:21
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { Utils.class })
public class UserNotificationImplTest {
	private UserService mockUserService;
	private UserNotificationServiceImpl fixtureUserNotificationImpl;
	private SMSNotification smsNotification;

	/**
	 * Run the UserNotificationImpl() constructor test.
	 * 
	 * @generatedBy CodePro at 04.09.12 13:21
	 */
	@Test
	public void testUserNotificationImpl_1()
			throws Exception {
		UserNotificationServiceImpl result = new UserNotificationServiceImpl();
		assertNotNull(result);
	}

	/**
	 * Run the Future<Boolean> notifyUserAboutSuccesfullPayment(User) method
	 * test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 04.09.12 13:21
	 */
	@Test
	public void testNotifyUserAboutSuccesfullPayment_Success()
			throws Exception {

		User user = UserFactory.createUser();
		Community community = CommunityFactory.createCommunity();
		UserGroup userGroup = UserGroupFactory.createUserGroup(community);
		user.setUserGroup(userGroup);

		Future<Boolean> futureResult = new AsyncResult<Boolean>(Boolean.TRUE);

		Mockito.when(mockUserService.makeSuccesfullPaymentFreeSMSRequest(user)).thenReturn(futureResult);

		Future<Boolean> result = fixtureUserNotificationImpl.notifyUserAboutSuccesfullPayment(user);

		assertNotNull(result);
		assertEquals(Boolean.TRUE, result.get());
		assertEquals(false, result.isCancelled());
		assertEquals(true, result.isDone());

		Mockito.verify(mockUserService).makeSuccesfullPaymentFreeSMSRequest(user);
	}

	/**
	 * Run the Future<Boolean> notifyUserAboutSuccesfullPayment(User) method
	 * test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 04.09.12 13:21
	 */
	@Test(expected = java.lang.NullPointerException.class)
	public void testNotifyUserAboutSuccesfullPayment_UserIsNull_Failure()
			throws Exception {
		User user = null;

		Future<Boolean> futureResult = new AsyncResult<Boolean>(Boolean.TRUE);

		Mockito.when(mockUserService.makeSuccesfullPaymentFreeSMSRequest(user)).thenReturn(futureResult);

		fixtureUserNotificationImpl.notifyUserAboutSuccesfullPayment(user);

		Mockito.verify(mockUserService, times(0)).makeSuccesfullPaymentFreeSMSRequest(user);
	}

	/**
	 * Run the void setUserService(UserService) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 04.09.12 13:21
	 */
	@Test
	public void testSetUserService_UserNotificationThrowsRuntimeException_Success()
			throws Exception {
		User user = UserFactory.createUser();
		Community community = CommunityFactory.createCommunity();
		UserGroup userGroup = UserGroupFactory.createUserGroup(community);
		user.setUserGroup(userGroup);

		Mockito.when(mockUserService.makeSuccesfullPaymentFreeSMSRequest(user)).thenThrow(new RuntimeException());

		Future<Boolean> result = fixtureUserNotificationImpl.notifyUserAboutSuccesfullPayment(user);

		assertNotNull(result);
		assertEquals(Boolean.FALSE, result.get());
		assertEquals(false, result.isCancelled());
		assertEquals(true, result.isDone());

		Mockito.verify(mockUserService).makeSuccesfullPaymentFreeSMSRequest(user);
	}

	/**
	 * Run the void setUserService(UserService) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 04.09.12 13:21
	 */
	@Test
	public void testSetUserService_UserNotificationThrowsServiceCheckedException_Success()
			throws Exception {
		User user = UserFactory.createUser();
		Community community = CommunityFactory.createCommunity();
		UserGroup userGroup = UserGroupFactory.createUserGroup(community);
		user.setUserGroup(userGroup);

		Mockito.when(mockUserService.makeSuccesfullPaymentFreeSMSRequest(user)).thenThrow(new ServiceCheckedException(null, null, null));

		Future<Boolean> result = fixtureUserNotificationImpl.notifyUserAboutSuccesfullPayment(user);

		assertNotNull(result);
		assertEquals(Boolean.FALSE, result.get());
		assertEquals(false, result.isCancelled());
		assertEquals(true, result.isDone());

		Mockito.verify(mockUserService).makeSuccesfullPaymentFreeSMSRequest(user);
	}

	@Test
	public void testSendUnsubscribeAfterSMS_Success() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		user.setCurrentPaymentDetails(paymentDetails);

		final String paymentUrl = "";
		Long epochMillis = 0L;

		mockStatic(Utils.class);
		when(Utils.getEpochMillis()).thenReturn(epochMillis);

		when(smsNotification.rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type")).thenReturn(false);

		when(smsNotification.getPaymentsUrl()).thenReturn(paymentUrl);

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {
		
			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;
				
				assertEquals(2, args.length);
				
				String pUrl = (String) args[0];
				String days = (String) args[1];
				
				assertEquals(paymentUrl, pUrl);
				assertEquals("0", days);
				
				return true;
			}
		};
		
		doNothing().when(smsNotification).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text." + deviceTypeName), argThat(matcher));

		Future<Boolean> result = fixtureUserNotificationImpl.sendUnsubscribeAfterSMS(user);

		assertNotNull(result);
		assertEquals(true, result.get());

		verify(smsNotification, times(1)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
		verify(smsNotification, times(1)).getPaymentsUrl();
		verify(smsNotification, times(1)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text." + deviceTypeName), argThat(matcher));
	}
	
	@Test
	public void testSendUnsubscribeAfterSMS_rejectedDevice_Success() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		user.setCurrentPaymentDetails(paymentDetails);

		final String paymentUrl = "";
		Long epochMillis = 0L;

		mockStatic(Utils.class);
		when(Utils.getEpochMillis()).thenReturn(epochMillis);

		when(smsNotification.rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type")).thenReturn(true);

		when(smsNotification.getPaymentsUrl()).thenReturn(paymentUrl);

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {
		
			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;
				
				assertEquals(2, args.length);
				
				String pUrl = (String) args[0];
				String days = (String) args[1];
				
				assertEquals(paymentUrl, pUrl);
				assertEquals("0", days);
				
				return true;
			}
		};
		
		doNothing().when(smsNotification).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text." + deviceTypeName), argThat(matcher));

		Future<Boolean> result = fixtureUserNotificationImpl.sendUnsubscribeAfterSMS(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(smsNotification, times(1)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
		verify(smsNotification, times(0)).getPaymentsUrl();
		verify(smsNotification, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text." + deviceTypeName), argThat(matcher));
	}
	
	@Test
	public void testSendUnsubscribeAfterSMS_NoPaymentDetails_Success() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);

		user.setCurrentPaymentDetails(null);

		final String paymentUrl = "";
		Long epochMillis = 0L;

		mockStatic(Utils.class);
		when(Utils.getEpochMillis()).thenReturn(epochMillis);

		when(smsNotification.rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type")).thenReturn(false);

		when(smsNotification.getPaymentsUrl()).thenReturn(paymentUrl);

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {
		
			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;
				
				assertEquals(2, args.length);
				
				String pUrl = (String) args[0];
				String days = (String) args[1];
				
				assertEquals(paymentUrl, pUrl);
				assertEquals("0", days);
				
				return true;
			}
		};
		
		doNothing().when(smsNotification).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text." + deviceTypeName), argThat(matcher));

		Future<Boolean> result = fixtureUserNotificationImpl.sendUnsubscribeAfterSMS(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(smsNotification, times(0)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
		verify(smsNotification, times(0)).getPaymentsUrl();
		verify(smsNotification, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text." + deviceTypeName), argThat(matcher));
	}
	
	@Test
	public void testSendUnsubscribeAfterSMS_UserIsNull_Success() throws Exception {
		String deviceTypeName = "ANDROID";
		
		User user = null;

		final String paymentUrl = "";
		Long epochMillis = 0L;

		mockStatic(Utils.class);
		when(Utils.getEpochMillis()).thenReturn(epochMillis);

		when(smsNotification.rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type")).thenReturn(false);

		when(smsNotification.getPaymentsUrl()).thenReturn(paymentUrl);

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {
		
			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;
				
				assertEquals(2, args.length);
				
				String pUrl = (String) args[0];
				String days = (String) args[1];
				
				assertEquals(paymentUrl, pUrl);
				assertEquals("0", days);
				
				return true;
			}
		};
		
		doNothing().when(smsNotification).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text." + deviceTypeName), argThat(matcher));

		Future<Boolean> result = fixtureUserNotificationImpl.sendUnsubscribeAfterSMS(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(smsNotification, times(0)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
		verify(smsNotification, times(0)).getPaymentsUrl();
		verify(smsNotification, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text." + deviceTypeName), argThat(matcher));
	}
	
	@Test(expected=Exception.class)
	public void testSendUnsubscribeAfterSMS_Failure() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		user.setCurrentPaymentDetails(paymentDetails);

		final String paymentUrl = "";
		Long epochMillis = 0L;

		mockStatic(Utils.class);
		when(Utils.getEpochMillis()).thenReturn(epochMillis);

		when(smsNotification.rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type")).thenReturn(false);

		when(smsNotification.getPaymentsUrl()).thenReturn(paymentUrl);

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {
		
			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;
				
				assertEquals(2, args.length);
				
				String pUrl = (String) args[0];
				String days = (String) args[1];
				
				assertEquals(paymentUrl, pUrl);
				assertEquals("0", days);
				
				return true;
			}
		};
		
		doThrow(new Exception()).when(smsNotification).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text." + deviceTypeName), argThat(matcher));

		Future<Boolean> result = fixtureUserNotificationImpl.sendUnsubscribeAfterSMS(user);

		assertNotNull(result);
		assertEquals(true, result.get());

		verify(smsNotification, times(1)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
		verify(smsNotification, times(1)).getPaymentsUrl();
		verify(smsNotification, times(1)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text." + deviceTypeName), argThat(matcher));
	}

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 * 
	 * @generatedBy CodePro at 04.09.12 13:21
	 */
	@Before
	public void setUp()
			throws Exception {
		fixtureUserNotificationImpl = new UserNotificationServiceImpl();

		mockUserService = Mockito.mock(UserService.class);
		smsNotification = mock(SMSNotification.class);

		fixtureUserNotificationImpl.setUserService(mockUserService);
		fixtureUserNotificationImpl.setSmsNotification(smsNotification);

	}
}