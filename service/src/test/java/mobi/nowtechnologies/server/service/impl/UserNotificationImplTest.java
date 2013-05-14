package mobi.nowtechnologies.server.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.Future;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.DeviceTypeFactory;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.PendingPaymentFactory;
import mobi.nowtechnologies.server.persistence.domain.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.UserStatusFactory;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.aop.SMSNotification;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalMatchers;
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
@PrepareForTest(value = { Utils.class, UserNotificationServiceImpl.class })
public class UserNotificationImplTest {
	private UserService userServiceMock;
	private UserNotificationServiceImpl userNotificationImplSpy;
	private SMSNotification smsNotificationMock;
	private CommunityResourceBundleMessageSource communityResourceBundleMessageSourceMock;

	/**
	 * Run the UserNotificationImpl() constructor test.
	 * 
	 * @generatedBy CodePro at 04.09.12 13:21
	 */
	@Test
	public void testUserNotificationImpl_Constructor_Success()
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

		Mockito.when(userServiceMock.makeSuccesfullPaymentFreeSMSRequest(user)).thenReturn(futureResult);

		Future<Boolean> result = userNotificationImplSpy.notifyUserAboutSuccesfullPayment(user);

		assertNotNull(result);
		assertEquals(Boolean.TRUE, result.get());
		assertEquals(false, result.isCancelled());
		assertEquals(true, result.isDone());

		Mockito.verify(userServiceMock).makeSuccesfullPaymentFreeSMSRequest(user);
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

		Mockito.when(userServiceMock.makeSuccesfullPaymentFreeSMSRequest(user)).thenReturn(futureResult);

		userNotificationImplSpy.notifyUserAboutSuccesfullPayment(user);

		Mockito.verify(userServiceMock, times(0)).makeSuccesfullPaymentFreeSMSRequest(user);
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

		Mockito.when(userServiceMock.makeSuccesfullPaymentFreeSMSRequest(user)).thenThrow(new RuntimeException());

		Future<Boolean> result = userNotificationImplSpy.notifyUserAboutSuccesfullPayment(user);

		assertNotNull(result);
		assertEquals(Boolean.FALSE, result.get());
		assertEquals(false, result.isCancelled());
		assertEquals(true, result.isDone());

		Mockito.verify(userServiceMock).makeSuccesfullPaymentFreeSMSRequest(user);
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

		Mockito.when(userServiceMock.makeSuccesfullPaymentFreeSMSRequest(user)).thenThrow(new ServiceCheckedException(null, null, null));

		Future<Boolean> result = userNotificationImplSpy.notifyUserAboutSuccesfullPayment(user);

		assertNotNull(result);
		assertEquals(Boolean.FALSE, result.get());
		assertEquals(false, result.isCancelled());
		assertEquals(true, result.isDone());

		Mockito.verify(userServiceMock).makeSuccesfullPaymentFreeSMSRequest(user);
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

		Long epochMillis = 0L;

		mockStatic(Utils.class);
		when(Utils.getEpochMillis()).thenReturn(epochMillis);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(2, args.length);

				String pUrl = (String) args[0];
				String days = (String) args[1];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);
				assertEquals("0", days);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendUnsubscribeAfterSMS(user);

		assertNotNull(result);
		assertEquals(true, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
		verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text"), argThat(matcher));
	}

	@Test
	public void testSendUnsubscribeAfterSMS_wasSmsSentSuccessfullyIsFalse_Failure() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		user.setCurrentPaymentDetails(paymentDetails);

		Long epochMillis = 0L;

		mockStatic(Utils.class);
		when(Utils.getEpochMillis()).thenReturn(epochMillis);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(2, args.length);

				String pUrl = (String) args[0];
				String days = (String) args[1];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);
				assertEquals("0", days);

				return true;
			}
		};

		doReturn(false).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendUnsubscribeAfterSMS(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
		verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text"), argThat(matcher));
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

		Long epochMillis = 0L;

		mockStatic(Utils.class);
		when(Utils.getEpochMillis()).thenReturn(epochMillis);

		doReturn(true).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(2, args.length);

				String pUrl = (String) args[0];
				String days = (String) args[1];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);
				assertEquals("0", days);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendUnsubscribeAfterSMS(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text"), argThat(matcher));
	}

	@Test(expected = NullPointerException.class)
	public void testSendUnsubscribeAfterSMS_NoPaymentDetails_Failure() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);

		user.setCurrentPaymentDetails(null);

		Long epochMillis = 0L;

		mockStatic(Utils.class);
		when(Utils.getEpochMillis()).thenReturn(epochMillis);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(2, args.length);

				String pUrl = (String) args[0];
				String days = (String) args[1];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);
				assertEquals("0", days);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendUnsubscribeAfterSMS(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text"), argThat(matcher));
	}

	@Test(expected = NullPointerException.class)
	public void testSendUnsubscribeAfterSMS_UserIsNull_Failure() throws Exception {
		String deviceTypeName = "ANDROID";

		User user = null;

		Long epochMillis = 0L;

		mockStatic(Utils.class);
		when(Utils.getEpochMillis()).thenReturn(epochMillis);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(2, args.length);

				String pUrl = (String) args[0];
				String days = (String) args[1];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);
				assertEquals("0", days);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendUnsubscribeAfterSMS(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text"), argThat(matcher));
	}

	@Test(expected = Exception.class)
	public void testSendUnsubscribeAfterSMS_Failure() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		user.setCurrentPaymentDetails(paymentDetails);

		Long epochMillis = 0L;

		mockStatic(Utils.class);
		when(Utils.getEpochMillis()).thenReturn(epochMillis);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(2, args.length);

				String pUrl = (String) args[0];
				String days = (String) args[1];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);
				assertEquals("0", days);

				return true;
			}
		};

		doThrow(new Exception()).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendUnsubscribeAfterSMS(user);

		assertNotNull(result);
		assertEquals(true, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
		verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.after.text"), argThat(matcher));
	}

	@Test
	public void testSendUnsubscribePotentialSMS_Success() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		user.setCurrentPaymentDetails(paymentDetails);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String unsUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getUnsubscribeUrl(), unsUrl);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.potential.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendUnsubscribePotentialSMS(user);

		assertNotNull(result);
		assertEquals(true, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");
		verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.potential.text"), argThat(matcher));
	}

	@Test
	public void testSendUnsubscribePotentialSMS_wasSmsSentSuccessfullyIsFalse_Success() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		user.setCurrentPaymentDetails(paymentDetails);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String unsUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getUnsubscribeUrl(), unsUrl);

				return true;
			}
		};

		doReturn(false).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.potential.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendUnsubscribePotentialSMS(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");
		verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.potential.text"), argThat(matcher));
	}

	@Test
	public void testSendUnsubscribePotentialSMS_rejectedDevice_Success() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		user.setCurrentPaymentDetails(paymentDetails);

		doReturn(true).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String unsUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getUnsubscribeUrl(), unsUrl);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.potential.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendUnsubscribePotentialSMS(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.potential.text"), argThat(matcher));
	}

	@Test(expected = NullPointerException.class)
	public void testSendUnsubscribePotentialSMS_NoPaymentDetails_Failure() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);

		user.setCurrentPaymentDetails(null);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String unsUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getUnsubscribeUrl(), unsUrl);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.potential.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendUnsubscribePotentialSMS(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.potential.text"), argThat(matcher));
	}

	@Test(expected = NullPointerException.class)
	public void testSendUnsubscribePotentialSMS_UserIsNull_Failure() throws Exception {
		String deviceTypeName = "ANDROID";

		User user = null;

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String unsUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getUnsubscribeUrl(), unsUrl);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.potential.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendUnsubscribePotentialSMS(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.potential.text"), argThat(matcher));
	}

	@Test(expected = Exception.class)
	public void testSendUnsubscribePotentialSMS_Failure() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		user.setCurrentPaymentDetails(paymentDetails);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String unsUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getUnsubscribeUrl(), unsUrl);

				return true;
			}
		};

		doThrow(new Exception()).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.potential.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendUnsubscribePotentialSMS(user);

		assertNotNull(result);
		assertEquals(true, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");
		verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user),
				eq("sms.unsubscribe.potential.text"), argThat(matcher));
	}

	@Test
	public void testSendSmsOnFreeTrialExpired_Success() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);
		user.setStatus(limitedUserStatus);

		user.setPaymentDetailsList(Collections.<PaymentDetails> emptyList());

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.limited.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String pUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.freeTrialExpired.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendSmsOnFreeTrialExpired(user);

		assertNotNull(result);
		assertEquals(true, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.limited.not.for.device.type");
		verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user),
				eq("sms.freeTrialExpired.text"), argThat(matcher));
	}

	@Test
	public void testSendSmsOnFreeTrialExpired_Subscribed_Success() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		UserStatus subscribedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);
		user.setStatus(subscribedUserStatus);

		user.setPaymentDetailsList(Collections.<PaymentDetails> emptyList());

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.limited.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String pUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.freeTrialExpired.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendSmsOnFreeTrialExpired(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.limited.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.freeTrialExpired.text"), argThat(matcher));
	}

	@Test
	public void testSendSmsOnFreeTrialExpired_wasSmsSentSuccessfullyIsFalse_Success() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);
		user.setStatus(limitedUserStatus);

		user.setPaymentDetailsList(Collections.<PaymentDetails> emptyList());

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.limited.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String pUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);

				return true;
			}
		};

		doReturn(false).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.freeTrialExpired.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendSmsOnFreeTrialExpired(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.limited.not.for.device.type");
		verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user),
				eq("sms.freeTrialExpired.text"), argThat(matcher));
	}

	@Test
	public void testSendSmsOnFreeTrialExpired_rejectedDevice_Success() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);
		user.setStatus(limitedUserStatus);

		user.setPaymentDetailsList(Collections.<PaymentDetails> emptyList());

		doReturn(true).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.limited.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String pUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.freeTrialExpired.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendSmsOnFreeTrialExpired(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.limited.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.freeTrialExpired.text"), argThat(matcher));
	}

	@Test()
	public void testSendSmsOnFreeTrialExpired_WithPaymentDetails_Failure() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);
		user.setStatus(limitedUserStatus);

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		user.setPaymentDetailsList(Collections.<PaymentDetails> singletonList(paymentDetails));

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.limited.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String pUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.freeTrialExpired.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendSmsOnFreeTrialExpired(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.limited.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.freeTrialExpired.text"), argThat(matcher));
	}

	@Test(expected = NullPointerException.class)
	public void testSendSmsOnFreeTrialExpired_UserIsNull_Failure() throws Exception {
		String deviceTypeName = "ANDROID";

		User user = null;

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.limited.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String pUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.freeTrialExpired.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendSmsOnFreeTrialExpired(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.limited.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.freeTrialExpired.text"), argThat(matcher));
	}

	@Test(expected = Exception.class)
	public void testSendSmsOnFreeTrialExpired_Failure() throws Exception {
		int nextSubPayment = 100;

		String deviceTypeName = "ANDROID";
		DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

		UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);

		User user = UserFactory.createUser();
		user.setNextSubPayment(nextSubPayment);
		user.setDeviceType(androidDeviceType);
		user.setStatus(limitedUserStatus);

		user.setPaymentDetailsList(Collections.<PaymentDetails> emptyList());

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.limited.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String pUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);

				return true;
			}
		};

		doThrow(new Exception()).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.freeTrialExpired.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendSmsOnFreeTrialExpired(user);

		assertNotNull(result);
		assertEquals(true, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.limited.not.for.device.type");
		verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user),
				eq("sms.freeTrialExpired.text"), argThat(matcher));
	}

	@Test
	public void testSendLowBalanceWarning_Success() throws Exception {
		Community o2Community = CommunityFactory.createCommunity();
		o2Community.setRewriteUrlParameter("o2");

		UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

		User user = UserFactory.createUser();
		user.setUserGroup(o2UserGroup);
		user.setContract(Contract.PAYG);
		user.setSegment(SegmentType.CONSUMER);

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		user.setCurrentPaymentDetails(paymentDetails);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(user,
				"sms.lowBalance.text", null);

		Future<Boolean> result = userNotificationImplSpy.sendLowBalanceWarning(user);

		assertNotNull(result);
		assertEquals(true, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");
		verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(user,
				"sms.lowBalance.text", null);
	}

	@Test
	public void testSendLowBalanceWarning_PAYM_Success() throws Exception {
		Community o2Community = CommunityFactory.createCommunity();
		o2Community.setRewriteUrlParameter("o2");

		UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

		User user = UserFactory.createUser();
		user.setUserGroup(o2UserGroup);
		user.setContract(Contract.PAYM);
		user.setSegment(SegmentType.CONSUMER);

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		user.setCurrentPaymentDetails(paymentDetails);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(user,
				"sms.lowBalance.text", null);

		Future<Boolean> result = userNotificationImplSpy.sendLowBalanceWarning(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(user,
				"sms.lowBalance.text", null);
	}

	@Test
	public void testSendLowBalanceWarning_wasSmsSentSuccessfullyIsFalse_Success() throws Exception {
		Community o2Community = CommunityFactory.createCommunity();
		o2Community.setRewriteUrlParameter("o2");

		UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

		User user = UserFactory.createUser();
		user.setUserGroup(o2UserGroup);
		user.setContract(Contract.PAYG);
		user.setSegment(SegmentType.CONSUMER);

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		user.setCurrentPaymentDetails(paymentDetails);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");

		doReturn(false).when(userNotificationImplSpy).sendSMSWithUrl(user,
				"sms.lowBalance.text", null);

		Future<Boolean> result = userNotificationImplSpy.sendLowBalanceWarning(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");
		verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(user,
				"sms.lowBalance.text", null);
	}

	@Test
	public void testSendLowBalanceWarning_rejectedDevice_Success() throws Exception {
		Community o2Community = CommunityFactory.createCommunity();
		o2Community.setRewriteUrlParameter("o2");

		UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

		User user = UserFactory.createUser();
		user.setUserGroup(o2UserGroup);
		user.setContract(Contract.PAYG);
		user.setSegment(SegmentType.CONSUMER);

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		user.setCurrentPaymentDetails(paymentDetails);

		doReturn(true).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(user,
				"sms.lowBalance.text", null);

		Future<Boolean> result = userNotificationImplSpy.sendLowBalanceWarning(user);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(user,
				"sms.lowBalance.text", null);
	}

	@Test(expected = NullPointerException.class)
	public void testSendLowBalanceWarning_NoPaymentDetails_Failure() throws Exception {
		Community o2Community = CommunityFactory.createCommunity();
		o2Community.setRewriteUrlParameter("o2");

		UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

		User user = UserFactory.createUser();
		user.setUserGroup(o2UserGroup);
		user.setContract(Contract.PAYG);
		user.setSegment(SegmentType.CONSUMER);
		user.setCurrentPaymentDetails(null);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(user,
				"sms.lowBalance.text", null);

		Future<Boolean> result = userNotificationImplSpy.sendLowBalanceWarning(user);

		assertNotNull(result);
		assertEquals(true, result.get());

		verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(user,
				"sms.lowBalance.text", null);
	}

	@Test(expected = NullPointerException.class)
	public void testSendLowBalanceWarning_UserIsNull_Failure() throws Exception {
		User user = null;

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(user,
				"sms.lowBalance.text", null);

		Future<Boolean> result = userNotificationImplSpy.sendLowBalanceWarning(user);

		assertNotNull(result);
		assertEquals(true, result.get());

		verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(user,
				"sms.lowBalance.text", null);
	}

	@Test(expected = Exception.class)
	public void testSendLowBalanceWarning_Failure() throws Exception {
		Community o2Community = CommunityFactory.createCommunity();
		o2Community.setRewriteUrlParameter("o2");

		UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

		User user = UserFactory.createUser();
		user.setUserGroup(o2UserGroup);
		user.setContract(Contract.PAYG);
		user.setSegment(SegmentType.CONSUMER);

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		user.setCurrentPaymentDetails(paymentDetails);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");

		doThrow(new Exception()).when(userNotificationImplSpy).sendSMSWithUrl(user,
				"sms.lowBalance.text", null);

		Future<Boolean> result = userNotificationImplSpy.sendLowBalanceWarning(user);

		assertNotNull(result);
		assertEquals(true, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");
		verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(user,
				"sms.lowBalance.text", null);
	}
	
	@Test
	public void testSendPaymentFailSMS_0h_Success() throws Exception {
		int madeRetries = Integer.MAX_VALUE;
		int retriesOnError = madeRetries;

		PaymentDetails o2PDPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		o2PDPaymentDetails.setMadeRetries(madeRetries);
		o2PDPaymentDetails.setRetriesOnError(retriesOnError);

		User user = UserFactory.createUser();
		user.setCurrentPaymentDetails(o2PDPaymentDetails);
		user.setNextSubPayment(Integer.MIN_VALUE);

		PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
		pendingPayment.setUser(user);
		pendingPayment.setTimestamp(user.getNextSubPayment()*1000L);
		pendingPayment.setPaymentDetails(o2PDPaymentDetails);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.paymentFail.at.0h.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String pUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.paymentFail.at.0h.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendPaymentFailSMS(pendingPayment);

		assertNotNull(result);
		assertEquals(true, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.paymentFail.at.0h.not.for.device.type");
		verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user),
				eq("sms.paymentFail.at.0h.text"), argThat(matcher));
	}

	@Test
	public void testSendPaymentFailSMS_24h_Success() throws Exception {
		int madeRetries = Integer.MAX_VALUE;
		int retriesOnError = madeRetries;

		PaymentDetails o2PDPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		o2PDPaymentDetails.setMadeRetries(madeRetries);
		o2PDPaymentDetails.setRetriesOnError(retriesOnError);

		User user = UserFactory.createUser();
		user.setCurrentPaymentDetails(o2PDPaymentDetails);
		user.setNextSubPayment(Integer.MAX_VALUE);

		PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
		pendingPayment.setUser(user);
		pendingPayment.setTimestamp((user.getNextSubPayment()- 1)*1000L);
		pendingPayment.setPaymentDetails(o2PDPaymentDetails);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.paymentFail.at.24h.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String pUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.paymentFail.at.24h.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendPaymentFailSMS(pendingPayment);

		assertNotNull(result);
		assertEquals(true, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.paymentFail.at.24h.not.for.device.type");
		verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user),
				eq("sms.paymentFail.at.24h.text"), argThat(matcher));
	}
	
	@Test
	public void testSendPaymentFailSMS_rejectedDevice_Success() throws Exception {
		int madeRetries = Integer.MAX_VALUE;
		int retriesOnError = madeRetries;

		PaymentDetails o2PDPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		o2PDPaymentDetails.setMadeRetries(madeRetries);
		o2PDPaymentDetails.setRetriesOnError(retriesOnError);

		User user = UserFactory.createUser();
		user.setCurrentPaymentDetails(o2PDPaymentDetails);
		user.setNextSubPayment(Integer.MIN_VALUE);

		PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
		pendingPayment.setUser(user);
		pendingPayment.setTimestamp(user.getNextSubPayment()*1000L);
		pendingPayment.setPaymentDetails(o2PDPaymentDetails);

		doReturn(true).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.paymentFail.at.0h.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String pUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.paymentFail.at.0h.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendPaymentFailSMS(pendingPayment);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.paymentFail.at.0h.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.paymentFail.at.0h.text"), argThat(matcher));
	}
	
	@Test
	public void testSendPaymentFailSMS_MadeRetriesAndRetriesOnErrorAreNotEqual_Success() throws Exception {
		int madeRetries = Integer.MAX_VALUE;
		int retriesOnError = madeRetries + 1;

		PaymentDetails o2PDPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		o2PDPaymentDetails.setMadeRetries(madeRetries);
		o2PDPaymentDetails.setRetriesOnError(retriesOnError);

		User user = UserFactory.createUser();
		user.setCurrentPaymentDetails(o2PDPaymentDetails);
		user.setNextSubPayment(Integer.MIN_VALUE);

		PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
		pendingPayment.setUser(user);
		pendingPayment.setTimestamp(user.getNextSubPayment()*1000L);
		pendingPayment.setPaymentDetails(o2PDPaymentDetails);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.paymentFail.at.0h.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String pUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.paymentFail.at.0h.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendPaymentFailSMS(pendingPayment);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.paymentFail.at.0h.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.paymentFail.at.0h.text"), argThat(matcher));
	}
	
	@Test(expected=Exception.class)
	public void testSendPaymentFailSMS_Exception_Failure() throws Exception {
		int madeRetries = Integer.MAX_VALUE;
		int retriesOnError = madeRetries;

		PaymentDetails o2PDPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		o2PDPaymentDetails.setMadeRetries(madeRetries);
		o2PDPaymentDetails.setRetriesOnError(retriesOnError);

		User user = UserFactory.createUser();
		user.setCurrentPaymentDetails(o2PDPaymentDetails);
		user.setNextSubPayment(Integer.MIN_VALUE);

		PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
		pendingPayment.setUser(user);
		pendingPayment.setTimestamp(user.getNextSubPayment()*1000L);
		pendingPayment.setPaymentDetails(o2PDPaymentDetails);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.paymentFail.at.0h.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String pUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);

				return true;
			}
		};

		doThrow(new Exception()).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.paymentFail.at.0h.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendPaymentFailSMS(pendingPayment);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.paymentFail.at.0h.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.paymentFail.at.0h.text"), argThat(matcher));
	}
	
	@Test(expected=NullPointerException.class)
	public void testSendPaymentFailSMS_PaymentDetailsIsNull_Failure() throws Exception {
		PaymentDetails o2PDPaymentDetails = null;

		User user = UserFactory.createUser();
		user.setCurrentPaymentDetails(o2PDPaymentDetails);
		user.setNextSubPayment(Integer.MIN_VALUE);

		PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
		pendingPayment.setUser(user);
		pendingPayment.setPaymentDetails(o2PDPaymentDetails);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.paymentFail.at.0h.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String pUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.paymentFail.at.0h.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendPaymentFailSMS(pendingPayment);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.paymentFail.at.0h.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.paymentFail.at.0h.text"), argThat(matcher));
	}
	
	@Test(expected=NullPointerException.class)
	public void testSendPaymentFailSMS_UserIsNull_Failure() throws Exception {
		int madeRetries = Integer.MAX_VALUE;
		int retriesOnError = madeRetries;

		PaymentDetails o2PDPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		o2PDPaymentDetails.setMadeRetries(madeRetries);
		o2PDPaymentDetails.setRetriesOnError(retriesOnError);

		User user = null;

		PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
		pendingPayment.setUser(user);
		pendingPayment.setPaymentDetails(o2PDPaymentDetails);

		doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.paymentFail.at.0h.not.for.device.type");

		final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

			@Override
			public boolean matches(Object argument) {
				assertNotNull(argument);
				Object[] args = (Object[]) argument;

				assertEquals(1, args.length);

				String pUrl = (String) args[0];

				assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);

				return true;
			}
		};

		doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user),
				eq("sms.paymentFail.at.0h.text"), argThat(matcher));

		Future<Boolean> result = userNotificationImplSpy.sendPaymentFailSMS(pendingPayment);

		assertNotNull(result);
		assertEquals(false, result.get());

		verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.paymentFail.at.0h.not.for.device.type");
		verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user),
				eq("sms.paymentFail.at.0h.text"), argThat(matcher));
	}
	
	@Test(expected=NullPointerException.class)
	public void testSendPaymentFailSMS_PendingPaymentIsNull_Failure() throws Exception {
		PendingPayment pendingPayment = null;

		Future<Boolean> result = userNotificationImplSpy.sendPaymentFailSMS(pendingPayment);

		assertNotNull(result);
		assertEquals(false, result.get());
	}
	
	@Test
	public void testGetMessageCode_ProviderSegmentContractDeviceTypeAreNull_Success()
			throws Exception {
		final String rewriteUrlParameter = "o2";
		
		Community o2Community = CommunityFactory.createCommunity();
		o2Community.setRewriteUrlParameter(rewriteUrlParameter);
		
		UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);
		
		User user = UserFactory.createUser();
		user.setUserGroup(o2UserGroup);
		user.setProvider(null);
		user.setSegment(null);
		user.setContract(null);
		user.setDeviceType(null);
		
		String msgCodeBase = "msgCodeBase";
		
		final String expectedMsgCode = msgCodeBase;
		String expectedMsg = "";
		
		when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(null);
		when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

		String result = userNotificationImplSpy.getMessage(user, o2Community, msgCodeBase, new String[0]);

		assertNotNull(result);
		assertEquals(expectedMsg, result);

		verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
		verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
	}

	@Test
	public void testGetMessageCode_ProviderIsNotNullSegmentContractDeviceTypeAreNull_Success()
			throws Exception {
		final String rewriteUrlParameter = "o2";
		
		Community o2Community = CommunityFactory.createCommunity();
		o2Community.setRewriteUrlParameter(rewriteUrlParameter);
		
		UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);
		
		User user = UserFactory.createUser();
		user.setUserGroup(o2UserGroup);
		user.setProvider("provider");
		user.setSegment(null);
		user.setContract(null);
		user.setDeviceType(null);
		
		String msgCodeBase = "msgCodeBase";
		
		String expectedMsg = "expectedMsg";
		final String expectedMsgCode = msgCodeBase + ".for." + user.getProvider();
		
		when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(null);
		when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

		String result = userNotificationImplSpy.getMessage(user, o2Community, msgCodeBase, new String[0]);

		assertNotNull(result);
		assertEquals(expectedMsg, result);

		verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
		verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
	}

	@Test
	public void testGetMessageCode_ContractIsNotNullProvicerSegmentDeviceTypeAreNull_Success()
			throws Exception {
		final String rewriteUrlParameter = "o2";
		
		Community o2Community = CommunityFactory.createCommunity();
		o2Community.setRewriteUrlParameter(rewriteUrlParameter);
		
		UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);
		
		User user = UserFactory.createUser();
		user.setUserGroup(o2UserGroup);
		user.setProvider(null);
		user.setSegment(null);
		user.setContract(Contract.PAYG);
		user.setDeviceType(null);
		
		String msgCodeBase = "msgCodeBase";
		
		String expectedMsg = "expectedMsg";
		final String expectedMsgCode = msgCodeBase  + ".for." + user.getContract();
		
		when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(null);
		when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

		String result = userNotificationImplSpy.getMessage(user, o2Community, msgCodeBase, new String[0]);

		assertNotNull(result);
		assertEquals(expectedMsg, result);

		verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
		verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
	}
	
	@Test
	public void testGetMessageCode_SegmentIsNotNullContractProvicerDeviceTypeAreNull_Success()
			throws Exception {
		final String rewriteUrlParameter = "o2";
		
		Community o2Community = CommunityFactory.createCommunity();
		o2Community.setRewriteUrlParameter(rewriteUrlParameter);
		
		UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);
		
		User user = UserFactory.createUser();
		user.setUserGroup(o2UserGroup);
		user.setProvider(null);
		user.setSegment(SegmentType.BUSINESS);
		user.setContract(null);
		user.setDeviceType(null);
		
		String msgCodeBase = "msgCodeBase";
		
		String expectedMsg = "expectedMsg";
		final String expectedMsgCode = msgCodeBase  + ".for." + user.getSegment();
		
		when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(null);
		when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

		String result = userNotificationImplSpy.getMessage(user, o2Community, msgCodeBase, new String[0]);

		assertNotNull(result);
		assertEquals(expectedMsg, result);

		verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
		verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
	}
	
	@Test
	public void testGetMessageCode_DeviceTypeIsNotNullSegmentContractProvicerAreNull_Success()
			throws Exception {
		final String rewriteUrlParameter = "o2";
		
		Community o2Community = CommunityFactory.createCommunity();
		o2Community.setRewriteUrlParameter(rewriteUrlParameter);
		
		UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);
		
		DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");
		
		User user = UserFactory.createUser();
		user.setUserGroup(o2UserGroup);
		user.setProvider(null);
		user.setSegment(null);
		user.setContract(null);
		user.setDeviceType(deviceType);
		
		String msgCodeBase = "msgCodeBase";
		
		String expectedMsg = "expectedMsg";
		final String expectedMsgCode = msgCodeBase + ".for."+ deviceType.getName();
		
		when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(null);
		when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

		String result = userNotificationImplSpy.getMessage(user, o2Community, msgCodeBase, new String[0]);

		assertNotNull(result);
		assertEquals(expectedMsg, result);

		verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
		verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
	}
	
	@Test
	public void testGetMessageCode_DeviceTypeSegmentContractProvicerAreNotNull_Success()
			throws Exception {
		final String rewriteUrlParameter = "o2";
		
		Community o2Community = CommunityFactory.createCommunity();
		o2Community.setRewriteUrlParameter(rewriteUrlParameter);
		
		UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);
		
		DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");
		
		User user = UserFactory.createUser();
		user.setUserGroup(o2UserGroup);
		user.setProvider("not-o2");
		user.setSegment(SegmentType.BUSINESS);
		user.setContract(Contract.PAYG);
		user.setDeviceType(deviceType);
		
		String msgCodeBase = "msgCodeBase";
		
		String expectedMsg = "expectedMsg";
		final String expectedMsgCode = msgCodeBase + ".for." + user.getProvider() + "." + user.getSegment() + "." + user.getContract() + "." + deviceType.getName();
		
		when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(null);
		when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

		String result = userNotificationImplSpy.getMessage(user, o2Community, msgCodeBase, new String[0]);

		assertNotNull(result);
		assertEquals(expectedMsg, result);

		verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
		verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
	}
	
	@Test
	public void testGetMessageCode_ProvicerIsNullDeviceTypeSegmentContractAreNotNull_Success()
			throws Exception {
		final String rewriteUrlParameter = "o2";
		
		Community o2Community = CommunityFactory.createCommunity();
		o2Community.setRewriteUrlParameter(rewriteUrlParameter);
		
		UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);
		
		DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");
		
		User user = UserFactory.createUser();
		user.setUserGroup(o2UserGroup);
		user.setProvider(null);
		user.setSegment(SegmentType.BUSINESS);
		user.setContract(Contract.PAYG);
		user.setDeviceType(deviceType);
		
		String msgCodeBase = "msgCodeBase";
		
		String expectedMsg = "expectedMsg";
		final String expectedMsgCode = msgCodeBase + ".for." + user.getSegment() + "." + user.getContract() + "." + deviceType.getName();
		
		when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(null);
		when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

		String result = userNotificationImplSpy.getMessage(user, o2Community, msgCodeBase, new String[0]);

		assertNotNull(result);
		assertEquals(expectedMsg, result);

		verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
		verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
	}
	
	@Test
	public void testGetMessageCode_ProvicerContractAreNullDeviceTypeSegmentAreNotNull_Success()
			throws Exception {
		final String rewriteUrlParameter = "o2";
		
		Community o2Community = CommunityFactory.createCommunity();
		o2Community.setRewriteUrlParameter(rewriteUrlParameter);
		
		UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);
		
		DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");
		
		User user = UserFactory.createUser();
		user.setUserGroup(o2UserGroup);
		user.setProvider(null);
		user.setSegment(SegmentType.BUSINESS);
		user.setContract(null);
		user.setDeviceType(deviceType);
		
		String msgCodeBase = "msgCodeBase";
		
		String expectedMsg = "expectedMsg";
		final String expectedMsgCode = msgCodeBase + ".for." + user.getSegment() + "." + deviceType.getName();
		
		when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(null);
		when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

		String result = userNotificationImplSpy.getMessage(user, o2Community, msgCodeBase, new String[0]);

		assertNotNull(result);
		assertEquals(expectedMsg, result);

		verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
		verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
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
		userNotificationImplSpy = spy(new UserNotificationServiceImpl());

		userServiceMock = Mockito.mock(UserService.class);
		smsNotificationMock = mock(SMSNotification.class);
		communityResourceBundleMessageSourceMock = mock(CommunityResourceBundleMessageSource.class);

		userNotificationImplSpy.setUserService(userServiceMock);
		userNotificationImplSpy.setPaymentsUrl("paymentsUrl");
		userNotificationImplSpy.setUnsubscribeUrl("unsubscribeUrl");
		userNotificationImplSpy.setMessageSource(communityResourceBundleMessageSourceMock);

	}
}