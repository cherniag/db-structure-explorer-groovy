package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.device.domain.DeviceTypeFactory;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory;
import mobi.nowtechnologies.server.persistence.domain.PendingPaymentFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.UserStatusFactory;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.security.NowTechTokenBasedRememberMeServices;
import mobi.nowtechnologies.server.service.DevicePromotionsService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.ERROR;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.BUSINESS;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.Future;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * @author Titov Mykhaylo (titov)
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {Utils.class, UserNotificationServiceImpl.class})
public class UserNotificationServiceImplTest {

    private UserService userServiceMock;
    private UserNotificationServiceImpl userNotificationImplSpy;
    private CommunityResourceBundleMessageSource communityResourceBundleMessageSourceMock;
    private RestTemplate restTemplateMock;
    private MigHttpService migHttpServiceMock;
    private NowTechTokenBasedRememberMeServices nowTechTokenBasedRememberMeServicesMock;
    private DevicePromotionsService deviceServiceMock;
    private PaymentDetailsRepository paymentDetailsRepository;
    private String forNWeeks;
    private SmsServiceFacade smsServiceFacadeMock;

    @Test
    public void testUserNotificationImpl_Constructor_Success() throws Exception {
        UserNotificationServiceImpl result = new UserNotificationServiceImpl();
        assertNotNull(result);
    }

    @Test
    public void testNotifyUserAboutSuccesfulPayment_Success() throws Exception {

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        Community community = CommunityFactory.createCommunity();
        UserGroup userGroup = UserGroupFactory.createUserGroup(community);
        user.setUserGroup(userGroup);

        Future<Boolean> futureResult = new AsyncResult<Boolean>(Boolean.TRUE);

        Mockito.when(userServiceMock.makeSuccessfulPaymentFreeSMSRequest(user)).thenReturn(futureResult);

        Future<Boolean> result = userNotificationImplSpy.notifyUserAboutSuccessfulPayment(user);

        assertNotNull(result);
        assertEquals(Boolean.TRUE, result.get());
        assertEquals(false, result.isCancelled());
        assertEquals(true, result.isDone());

        Mockito.verify(userServiceMock).makeSuccessfulPaymentFreeSMSRequest(user);
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void testNotifyUserAboutSuccesfullPayment_UserIsNull_Failure() throws Exception {
        User user = null;

        Future<Boolean> futureResult = new AsyncResult<Boolean>(Boolean.TRUE);

        Mockito.when(userServiceMock.makeSuccessfulPaymentFreeSMSRequest(user)).thenReturn(futureResult);

        userNotificationImplSpy.notifyUserAboutSuccessfulPayment(user);

        Mockito.verify(userServiceMock, times(0)).makeSuccessfulPaymentFreeSMSRequest(user);
    }

    @Test
    public void testSetUserService_UserNotificationThrowsRuntimeException_Success() throws Exception {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        Community community = CommunityFactory.createCommunity();
        UserGroup userGroup = UserGroupFactory.createUserGroup(community);
        user.setUserGroup(userGroup);

        Mockito.when(userServiceMock.makeSuccessfulPaymentFreeSMSRequest(user)).thenThrow(new RuntimeException());

        Future<Boolean> result = userNotificationImplSpy.notifyUserAboutSuccessfulPayment(user);

        assertNotNull(result);
        assertEquals(Boolean.FALSE, result.get());
        assertEquals(false, result.isCancelled());
        assertEquals(true, result.isDone());

        Mockito.verify(userServiceMock).makeSuccessfulPaymentFreeSMSRequest(user);
    }

    @Test
    public void testSetUserService_UserNotificationThrowsServiceCheckedException_Success() throws Exception {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        Community community = CommunityFactory.createCommunity();
        UserGroup userGroup = UserGroupFactory.createUserGroup(community);
        user.setUserGroup(userGroup);

        Mockito.when(userServiceMock.makeSuccessfulPaymentFreeSMSRequest(user)).thenThrow(new ServiceCheckedException(null, null, null));

        Future<Boolean> result = userNotificationImplSpy.notifyUserAboutSuccessfulPayment(user);

        assertNotNull(result);
        assertEquals(Boolean.FALSE, result.get());
        assertEquals(false, result.isCancelled());
        assertEquals(true, result.isDone());

        Mockito.verify(userServiceMock).makeSuccessfulPaymentFreeSMSRequest(user);
    }

    @Test
    public void testSend4GDowngradeSMS_Success() throws Exception {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        user.setDeviceType(androidDeviceType);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.downgrade.not.for.device.type");

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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.downgrade.freetrial.text"), argThat(matcher));
        Future<Boolean> result = userNotificationImplSpy.send4GDowngradeSMS(user, UserNotificationService.DOWNGRADE_FROM_4G_FREETRIAL);

        assertNotNull(result);
        assertEquals(true, result.get());

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.downgrade.subscriber.text"), argThat(matcher));
        result = userNotificationImplSpy.send4GDowngradeSMS(user, UserNotificationService.DOWNGRADE_FROM_4G_SUBSCRIBED);

        assertNotNull(result);
        assertEquals(true, result.get());
    }

    @Test
    public void testSendUnsubscribeAfterSMS_Success() throws Exception {
        int nextSubPayment = 100;

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.after.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendUnsubscribeAfterSMS(user);

        assertNotNull(result);
        assertEquals(true, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
        verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.after.text"), argThat(matcher));
    }

    @Test
    public void testSendUnsubscribeAfterSMS_wasSmsSentSuccessfullyIsFalse_Failure() throws Exception {
        int nextSubPayment = 100;

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
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

        doReturn(false).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.after.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendUnsubscribeAfterSMS(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
        verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.after.text"), argThat(matcher));
    }

    @Test
    public void testSendUnsubscribeAfterSMS_rejectedDevice_Success() throws Exception {
        int nextSubPayment = 100;

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.after.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendUnsubscribeAfterSMS(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.after.text"), argThat(matcher));
    }

    @Test(expected = NullPointerException.class)
    public void testSendUnsubscribeAfterSMS_NoPaymentDetails_Failure() throws Exception {
        int nextSubPayment = 100;

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.after.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendUnsubscribeAfterSMS(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.after.text"), argThat(matcher));
    }

    @Test(expected = NullPointerException.class)
    public void testSendUnsubscribeAfterSMS_UserIsNull_Failure() throws Exception {
        //		String deviceTypeName = "ANDROID";

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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.after.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendUnsubscribeAfterSMS(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.after.text"), argThat(matcher));
    }

    @Test(expected = Exception.class)
    public void testSendUnsubscribeAfterSMS_Failure() throws Exception {
        int nextSubPayment = 100;

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
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

        doThrow(new Exception()).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.after.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendUnsubscribeAfterSMS(user);

        assertNotNull(result);
        assertEquals(true, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type");
        verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.after.text"), argThat(matcher));
    }

    @Test
    public void testSendUnsubscribePotentialSMS_Success() throws Exception {
        int nextSubPayment = 100;

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setNextSubPayment(nextSubPayment);
        user.setDeviceType(androidDeviceType);

        PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
        final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
        paymentDetails.setPaymentPolicy(paymentPolicy);
        user.setCurrentPaymentDetails(paymentDetails);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");

        forNWeeks = "for.n.weeks";
        Mockito.doReturn(forNWeeks)
               .when(communityResourceBundleMessageSourceMock)
               .getMessage(eq(user.getUserGroup().getCommunity().getRewriteUrlParameter()), eq("for.n.weeks"), (String[]) any(), any(Locale.class));

        final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

            @Override
            public boolean matches(Object argument) {
                assertNotNull(argument);
                Object[] args = (Object[]) argument;

                assertEquals(5, args.length);

                String unsUrl = (String) args[0];
                String currencyISO = (String) args[1];
                String subcost = (String) args[2];
                String durtionUnitPart = (String) args[3];
                String shortCode = (String) args[4];

                assertEquals(userNotificationImplSpy.getUnsubscribeUrl(), unsUrl);
                assertEquals(paymentPolicy.getCurrencyISO(), currencyISO);
                assertEquals(paymentPolicy.getSubcost().toString(), subcost);
                assertEquals(forNWeeks, durtionUnitPart);
                assertEquals(paymentPolicy.getShortCode(), shortCode);

                return true;
            }
        };

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.potential.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendSubscriptionChangedSMS(user);

        assertNotNull(result);
        assertEquals(true, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");
        verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.potential.text"), argThat(matcher));
    }

    @Test
    public void testSendUnsubscribePotentialSMS_wasSmsSentSuccessfullyIsFalse_Success() throws Exception {
        int nextSubPayment = 100;

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setNextSubPayment(nextSubPayment);
        user.setDeviceType(androidDeviceType);

        PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
        final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
        paymentDetails.setPaymentPolicy(paymentPolicy);
        user.setCurrentPaymentDetails(paymentDetails);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");

        forNWeeks = "for.n.weeks";
        Mockito.doReturn(forNWeeks)
               .when(communityResourceBundleMessageSourceMock)
               .getMessage(eq(user.getUserGroup().getCommunity().getRewriteUrlParameter()), eq("for.n.weeks"), (String[]) any(), any(Locale.class));

        final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

            @Override
            public boolean matches(Object argument) {
                assertNotNull(argument);
                Object[] args = (Object[]) argument;

                assertEquals(5, args.length);

                String unsUrl = (String) args[0];
                String currencyISO = (String) args[1];
                String subCost = (String) args[2];
                String durtionUnitPart = (String) args[3];
                String shortCode = (String) args[4];

                assertEquals(userNotificationImplSpy.getUnsubscribeUrl(), unsUrl);
                assertEquals(paymentPolicy.getCurrencyISO(), currencyISO);
                assertEquals(paymentPolicy.getSubcost().toString(), subCost);
                assertEquals(forNWeeks, durtionUnitPart);
                assertEquals(paymentPolicy.getShortCode(), shortCode);

                return true;
            }
        };

        doReturn(false).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.potential.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendSubscriptionChangedSMS(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");
        verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.potential.text"), argThat(matcher));
    }

    @Test
    public void testSendUnsubscribePotentialSMS_rejectedDevice_Success() throws Exception {
        int nextSubPayment = 100;

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.potential.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendSubscriptionChangedSMS(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.potential.text"), argThat(matcher));
    }

    @Test(expected = NullPointerException.class)
    public void testSendUnsubscribePotentialSMS_NoPaymentDetails_Failure() throws Exception {
        int nextSubPayment = 100;

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.potential.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendSubscriptionChangedSMS(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.potential.text"), argThat(matcher));
    }

    @Test(expected = NullPointerException.class)
    public void testSendUnsubscribePotentialSMS_UserIsNull_Failure() throws Exception {
        //		String deviceTypeName = "ANDROID";

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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.potential.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendSubscriptionChangedSMS(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.potential.text"), argThat(matcher));
    }

    @Test(expected = Exception.class)
    public void testSendUnsubscribePotentialSMS_Failure() throws Exception {
        int nextSubPayment = 100;

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
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

        doThrow(new Exception()).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.potential.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendSubscriptionChangedSMS(user);

        assertNotNull(result);
        assertEquals(true, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.subscribed.not.for.device.type");
        verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user), eq("sms.unsubscribe.potential.text"), argThat(matcher));
    }

    @Test
    public void testSendSmsOnFreeTrialExpired_Success() throws Exception {
        int nextSubPayment = 100;

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setNextSubPayment(nextSubPayment);
        user.setDeviceType(androidDeviceType);
        user.setStatus(limitedUserStatus);

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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.freeTrialExpired.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendSmsOnFreeTrialExpired(user);

        assertNotNull(result);
        assertEquals(true, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.limited.not.for.device.type");
        verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user), eq("sms.freeTrialExpired.text"), argThat(matcher));
    }

    @Test
    public void testSendSmsOnFreeTrialExpired_Subscribed_Success() throws Exception {
        int nextSubPayment = 100;

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        UserStatus subscribedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setNextSubPayment(nextSubPayment);
        user.setDeviceType(androidDeviceType);
        user.setStatus(subscribedUserStatus);

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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.freeTrialExpired.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendSmsOnFreeTrialExpired(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.limited.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.freeTrialExpired.text"), argThat(matcher));
    }

    @Test
    public void testSendSmsOnFreeTrialExpired_wasSmsSentSuccessfullyIsFalse_Success() throws Exception {
        int nextSubPayment = 100;

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setNextSubPayment(nextSubPayment);
        user.setDeviceType(androidDeviceType);
        user.setStatus(limitedUserStatus);

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

        doReturn(false).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.freeTrialExpired.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendSmsOnFreeTrialExpired(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.limited.not.for.device.type");
        verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user), eq("sms.freeTrialExpired.text"), argThat(matcher));
    }

    @Test
    public void testSendSmsOnFreeTrialExpired_rejectedDevice_Success() throws Exception {
        int nextSubPayment = 100;

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setNextSubPayment(nextSubPayment);
        user.setDeviceType(androidDeviceType);
        user.setStatus(limitedUserStatus);

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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.freeTrialExpired.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendSmsOnFreeTrialExpired(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.limited.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.freeTrialExpired.text"), argThat(matcher));
    }

    @Test()
    public void testSendSmsOnFreeTrialExpired_WithPaymentDetails_Failure() throws Exception {
        int nextSubPayment = 100;

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setNextSubPayment(nextSubPayment);
        user.setDeviceType(androidDeviceType);
        user.setStatus(limitedUserStatus);

        PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

        when(paymentDetailsRepository.findPaymentDetailsByOwner(user)).thenReturn(Collections.singletonList(paymentDetails));

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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.freeTrialExpired.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendSmsOnFreeTrialExpired(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.limited.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.freeTrialExpired.text"), argThat(matcher));
    }

    @Test(expected = NullPointerException.class)
    public void testSendSmsOnFreeTrialExpired_UserIsNull_Failure() throws Exception {
        //		String deviceTypeName = "ANDROID";

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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.freeTrialExpired.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendSmsOnFreeTrialExpired(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.limited.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.freeTrialExpired.text"), argThat(matcher));
    }

    @Test(expected = Exception.class)
    public void testSendSmsOnFreeTrialExpired_Failure() throws Exception {
        int nextSubPayment = 100;

        String deviceTypeName = "ANDROID";
        DeviceType androidDeviceType = DeviceTypeFactory.createDeviceType(deviceTypeName);

        UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setNextSubPayment(nextSubPayment);
        user.setDeviceType(androidDeviceType);
        user.setStatus(limitedUserStatus);

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

        doThrow(new Exception()).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.freeTrialExpired.text"), argThat(matcher));

        Future<Boolean> result = userNotificationImplSpy.sendSmsOnFreeTrialExpired(user);

        assertNotNull(result);
        assertEquals(true, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.limited.not.for.device.type");
        verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user), eq("sms.freeTrialExpired.text"), argThat(matcher));
    }

    @Test
    public void testSendLowBalanceWarning_Success() throws Exception {
        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter("o2");

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setContract(PAYG);
        user.setSegment(CONSUMER);

        PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

        user.setCurrentPaymentDetails(paymentDetails);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(user, "sms.lowBalance.text", null);

        Future<Boolean> result = userNotificationImplSpy.sendLowBalanceWarning(user);

        assertNotNull(result);
        assertEquals(true, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");
        verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(user, "sms.lowBalance.text", null);
    }

    @Test
    public void testSendLowBalanceWarning_PAYM_Success() throws Exception {
        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter("o2");

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setContract(PAYM);
        user.setSegment(CONSUMER);

        PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

        user.setCurrentPaymentDetails(paymentDetails);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(user, "sms.lowBalance.text", null);

        Future<Boolean> result = userNotificationImplSpy.sendLowBalanceWarning(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(user, "sms.lowBalance.text", null);
    }

    @Test
    public void testSendLowBalanceWarning_wasSmsSentSuccessfullyIsFalse_Success() throws Exception {
        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter("o2");

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setContract(PAYG);
        user.setSegment(CONSUMER);

        PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

        user.setCurrentPaymentDetails(paymentDetails);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");

        doReturn(false).when(userNotificationImplSpy).sendSMSWithUrl(user, "sms.lowBalance.text", null);

        Future<Boolean> result = userNotificationImplSpy.sendLowBalanceWarning(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");
        verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(user, "sms.lowBalance.text", null);
    }

    @Test
    public void testSendLowBalanceWarning_rejectedDevice_Success() throws Exception {
        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter("o2");

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setContract(PAYG);
        user.setSegment(CONSUMER);

        PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

        user.setCurrentPaymentDetails(paymentDetails);

        doReturn(true).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(user, "sms.lowBalance.text", null);

        Future<Boolean> result = userNotificationImplSpy.sendLowBalanceWarning(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(user, "sms.lowBalance.text", null);
    }

    @Test(expected = NullPointerException.class)
    public void testSendLowBalanceWarning_NoPaymentDetails_Failure() throws Exception {
        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter("o2");

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setContract(PAYG);
        user.setSegment(CONSUMER);
        user.setCurrentPaymentDetails(null);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(user, "sms.lowBalance.text", null);

        Future<Boolean> result = userNotificationImplSpy.sendLowBalanceWarning(user);

        assertNotNull(result);
        assertEquals(true, result.get());

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(user, "sms.lowBalance.text", null);
    }

    @Test(expected = NullPointerException.class)
    public void testSendLowBalanceWarning_UserIsNull_Failure() throws Exception {
        User user = null;

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(user, "sms.lowBalance.text", null);

        Future<Boolean> result = userNotificationImplSpy.sendLowBalanceWarning(user);

        assertNotNull(result);
        assertEquals(true, result.get());

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(user, "sms.lowBalance.text", null);
    }

    @Test(expected = Exception.class)
    public void testSendLowBalanceWarning_Failure() throws Exception {
        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter("o2");

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setContract(PAYG);
        user.setSegment(CONSUMER);

        PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

        user.setCurrentPaymentDetails(paymentDetails);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");

        doThrow(new Exception()).when(userNotificationImplSpy).sendSMSWithUrl(user, "sms.lowBalance.text", null);

        Future<Boolean> result = userNotificationImplSpy.sendLowBalanceWarning(user);

        assertNotNull(result);
        assertEquals(true, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.lowBalance.not.for.device.type");
        verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(user, "sms.lowBalance.text", null);
    }

    @Test
    public void testSendPaymentFailSMS_0h_Success() throws Exception {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED).withUserGroup(new UserGroup().withCommunity(new Community().withName("")));
        user.setNextSubPayment(Integer.MIN_VALUE);

        final PaymentDetails o2PDPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails().withOwner(user);
        o2PDPaymentDetails.withMadeRetries(0);
        o2PDPaymentDetails.setRetriesOnError(3);
        o2PDPaymentDetails.withMadeAttempts(2);
        o2PDPaymentDetails.withLastPaymentStatus(ERROR);
        o2PDPaymentDetails.withPaymentPolicy(new PaymentPolicy().withShortCode("shortCode"));

        user.setCurrentPaymentDetails(o2PDPaymentDetails);

        PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setTimestamp(user.getNextSubPayment() * 1000L);
        pendingPayment.setPaymentDetails(o2PDPaymentDetails);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.paymentFail.at.2attempt.not.for.device.type");

        final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

            @Override
            public boolean matches(Object argument) {
                assertNotNull(argument);
                Object[] args = (Object[]) argument;

                assertEquals(2, args.length);

                String pUrl = (String) args[0];
                String shortCode = (String) args[1];

                assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);
                assertEquals(o2PDPaymentDetails.getPaymentPolicy().getShortCode(), shortCode);

                return true;
            }
        };

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.paymentFail.at.2attempt.text"), argThat(matcher));
        doReturn(o2PDPaymentDetails).when(paymentDetailsRepository).save(o2PDPaymentDetails);
        mockStatic(Utils.class);
        when(Utils.getEpochMillis()).thenReturn(Long.MIN_VALUE);

        Future<Boolean> result = userNotificationImplSpy.sendPaymentFailSMS(o2PDPaymentDetails);

        assertNotNull(result);
        assertEquals(true, result.get());
        assertThat(o2PDPaymentDetails.getLastFailedPaymentNotificationMillis(), is(Long.MIN_VALUE));

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.paymentFail.at.2attempt.not.for.device.type");
        verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user), eq("sms.paymentFail.at.2attempt.text"), argThat(matcher));
        verify(paymentDetailsRepository, times(1)).save(o2PDPaymentDetails);
    }

    @Test
    public void testSendPaymentFailSMS_24h_Success() throws Exception {
        int madeRetries = 0;
        int retriesOnError = madeRetries;

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED).withUserGroup(new UserGroup().withCommunity(new Community().withName("")));
        user.setNextSubPayment(Integer.MAX_VALUE);

        final PaymentDetails o2PDPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails().withOwner(user);
        o2PDPaymentDetails.withMadeRetries(madeRetries);
        o2PDPaymentDetails.setRetriesOnError(retriesOnError);
        o2PDPaymentDetails.withMadeAttempts(1);
        o2PDPaymentDetails.withLastPaymentStatus(ERROR);
        o2PDPaymentDetails.withPaymentPolicy(new PaymentPolicy().withShortCode("shortCode"));

        user.setCurrentPaymentDetails(o2PDPaymentDetails);

        PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setTimestamp((user.getNextSubPayment() - 1) * 1000L);
        pendingPayment.setPaymentDetails(o2PDPaymentDetails);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.paymentFail.at.1attempt.not.for.device.type");

        final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

            @Override
            public boolean matches(Object argument) {
                assertNotNull(argument);
                Object[] args = (Object[]) argument;

                assertEquals(2, args.length);

                String pUrl = (String) args[0];
                String shortCode = (String) args[1];

                assertEquals(userNotificationImplSpy.getPaymentsUrl(), pUrl);
                assertEquals(o2PDPaymentDetails.getPaymentPolicy().getShortCode(), shortCode);

                return true;
            }
        };

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.paymentFail.at.1attempt.text"), argThat(matcher));
        doReturn(o2PDPaymentDetails).when(paymentDetailsRepository).save(o2PDPaymentDetails);
        mockStatic(Utils.class);
        when(Utils.getEpochMillis()).thenReturn(Long.MIN_VALUE);

        Future<Boolean> result = userNotificationImplSpy.sendPaymentFailSMS(o2PDPaymentDetails);

        assertNotNull(result);
        assertEquals(true, result.get());
        assertThat(o2PDPaymentDetails.getLastFailedPaymentNotificationMillis(), is(Long.MIN_VALUE));

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.paymentFail.at.1attempt.not.for.device.type");
        verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user), eq("sms.paymentFail.at.1attempt.text"), argThat(matcher));
        verify(paymentDetailsRepository, times(1)).save(o2PDPaymentDetails);
    }

    @Test
    public void testSendPaymentFailSMS_rejectedDevice_Success() throws Exception {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED).withUserGroup(new UserGroup().withCommunity(new Community().withName("")));
        user.setNextSubPayment(Integer.MIN_VALUE);

        PaymentDetails o2PDPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails().withOwner(user);
        o2PDPaymentDetails.withMadeRetries(0);
        o2PDPaymentDetails.setRetriesOnError(3);
        o2PDPaymentDetails.withMadeAttempts(2);
        o2PDPaymentDetails.withLastPaymentStatus(ERROR);

        user.setCurrentPaymentDetails(o2PDPaymentDetails);

        PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setTimestamp(user.getNextSubPayment() * 1000L);
        pendingPayment.setPaymentDetails(o2PDPaymentDetails);

        doReturn(true).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.paymentFail.at.2attempt.not.for.device.type");

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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.paymentFail.at.2attempt.text"), argThat(matcher));
        doReturn(o2PDPaymentDetails).when(paymentDetailsRepository).save(o2PDPaymentDetails);
        mockStatic(Utils.class);
        when(Utils.getEpochMillis()).thenReturn(Long.MIN_VALUE);

        Future<Boolean> result = userNotificationImplSpy.sendPaymentFailSMS(o2PDPaymentDetails);

        assertNotNull(result);
        assertEquals(false, result.get());
        assertThat(o2PDPaymentDetails.getLastFailedPaymentNotificationMillis(), is(nullValue()));

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.paymentFail.at.2attempt.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.paymentFail.at.2attempt.text"), argThat(matcher));
        verify(paymentDetailsRepository, times(0)).save(o2PDPaymentDetails);
    }

    @Test
    public void testSendPaymentFailSMS_MadeRetriesAndRetriesOnErrorAreNotEqual_Success() throws Exception {
        int madeRetries = Integer.MAX_VALUE;
        int retriesOnError = madeRetries + 1;

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED).withUserGroup(new UserGroup().withCommunity(new Community().withName("")));
        user.setNextSubPayment(Integer.MIN_VALUE);

        PaymentDetails o2PDPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails().withOwner(user);
        o2PDPaymentDetails.withMadeRetries(madeRetries);
        o2PDPaymentDetails.setRetriesOnError(retriesOnError);

        user.setCurrentPaymentDetails(o2PDPaymentDetails);

        PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setTimestamp(user.getNextSubPayment() * 1000L);
        pendingPayment.setPaymentDetails(o2PDPaymentDetails);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.paymentFail.at.2attempt.not.for.device.type");

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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.paymentFail.at.2attempt.text"), argThat(matcher));
        doReturn(o2PDPaymentDetails).when(paymentDetailsRepository).save(o2PDPaymentDetails);
        mockStatic(Utils.class);
        when(Utils.getEpochMillis()).thenReturn(Long.MIN_VALUE);

        Future<Boolean> result = userNotificationImplSpy.sendPaymentFailSMS(o2PDPaymentDetails);

        assertNotNull(result);
        assertEquals(false, result.get());
        assertThat(o2PDPaymentDetails.getLastFailedPaymentNotificationMillis(), is(nullValue()));

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.paymentFail.at.2attempt.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.paymentFail.at.2attempt.text"), argThat(matcher));
        verify(paymentDetailsRepository, times(0)).save(o2PDPaymentDetails);
    }

    @Test(expected = Exception.class)
    public void testSendPaymentFailSMS_Exception_Failure() throws Exception {
        int madeRetries = Integer.MAX_VALUE;
        int retriesOnError = madeRetries;

        PaymentDetails o2PDPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
        o2PDPaymentDetails.withMadeRetries(madeRetries);
        o2PDPaymentDetails.setRetriesOnError(retriesOnError);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setCurrentPaymentDetails(o2PDPaymentDetails);
        user.setNextSubPayment(Integer.MIN_VALUE);

        PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setTimestamp(user.getNextSubPayment() * 1000L);
        pendingPayment.setPaymentDetails(o2PDPaymentDetails);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.paymentFail.at.2attempt.not.for.device.type");

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

        doThrow(new Exception()).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.paymentFail.at.2attempt.text"), argThat(matcher));
        doReturn(o2PDPaymentDetails).when(paymentDetailsRepository).save(o2PDPaymentDetails);
        mockStatic(Utils.class);
        when(Utils.getEpochMillis()).thenReturn(Long.MIN_VALUE);
        assertThat(o2PDPaymentDetails.getLastFailedPaymentNotificationMillis(), is(nullValue()));

        Future<Boolean> result = userNotificationImplSpy.sendPaymentFailSMS(o2PDPaymentDetails);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.paymentFail.at.2attempt.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.paymentFail.at.2attempt.text"), argThat(matcher));
        verify(paymentDetailsRepository, times(0)).save(o2PDPaymentDetails);
    }

    @Test(expected = NullPointerException.class)
    public void testSendPaymentFailSMS_PaymentDetailsIsNull_Failure() throws Exception {
        PaymentDetails o2PDPaymentDetails = null;

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setCurrentPaymentDetails(o2PDPaymentDetails);
        user.setNextSubPayment(Integer.MIN_VALUE);

        PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(o2PDPaymentDetails);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.paymentFail.at.2attempt.not.for.device.type");

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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.paymentFail.at.2attempt.text"), argThat(matcher));
        mockStatic(Utils.class);
        when(Utils.getEpochMillis()).thenReturn(Long.MIN_VALUE);
        assertThat(o2PDPaymentDetails.getLastFailedPaymentNotificationMillis(), is(nullValue()));

        Future<Boolean> result = userNotificationImplSpy.sendPaymentFailSMS(o2PDPaymentDetails);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.paymentFail.at.2attempt.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.paymentFail.at.2attempt.text"), argThat(matcher));
        verify(paymentDetailsRepository, times(0)).save(o2PDPaymentDetails);
    }

    @Test
    public void testSendPaymentFailSMS_UserIsNull_Failure() throws Exception {
        int madeRetries = Integer.MAX_VALUE;
        int retriesOnError = madeRetries;

        PaymentDetails o2PDPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
        o2PDPaymentDetails.withMadeRetries(madeRetries);
        o2PDPaymentDetails.setRetriesOnError(retriesOnError);

        User user = null;

        PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(o2PDPaymentDetails);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.paymentFail.at.2attempt.not.for.device.type");

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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.paymentFail.at.2attempt.text"), argThat(matcher));
        doReturn(o2PDPaymentDetails).when(paymentDetailsRepository).save(o2PDPaymentDetails);
        mockStatic(Utils.class);
        when(Utils.getEpochMillis()).thenReturn(Long.MIN_VALUE);

        Future<Boolean> result = userNotificationImplSpy.sendPaymentFailSMS(o2PDPaymentDetails);

        assertNotNull(result);
        assertEquals(false, result.get());
        assertThat(o2PDPaymentDetails.getLastFailedPaymentNotificationMillis(), is(nullValue()));

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.paymentFail.at.2attempt.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.paymentFail.at.2attempt.text"), argThat(matcher));
        verify(paymentDetailsRepository, times(0)).save(o2PDPaymentDetails);
    }

    @Test
    public void testSendPaymentFailSMS_PendingPaymentIsNull_Failure() throws Exception {
        PendingPayment pendingPayment = null;

        doReturn(false).when(userNotificationImplSpy).rejectDevice(any(User.class), eq("sms.notification.paymentFail.at.2attempt.not.for.device.type"));

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

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(any(User.class), eq("sms.paymentFail.at.2attempt.text"), argThat(matcher));
        mockStatic(Utils.class);
        when(Utils.getEpochMillis()).thenReturn(Long.MIN_VALUE);

        Future<Boolean> result = userNotificationImplSpy.sendPaymentFailSMS(any(PaymentDetails.class));

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(0)).rejectDevice(any(User.class), eq("sms.notification.paymentFail.at.2attempt.not.for.device.type"));
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(any(User.class), eq("sms.paymentFail.at.2attempt.text"), argThat(matcher));
        verify(paymentDetailsRepository, times(0)).save(any(PaymentDetails.class));
    }

    @Test
    public void testSendActivationPinSMS_Success() throws Exception {
        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setPin("0000");

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.activation.pin.not.for.device.type");
        final ArgumentMatcher<String[]> matcher = new ArgumentMatcher<String[]>() {

            @Override
            public boolean matches(Object argument) {
                assertNotNull(argument);
                Object[] args = (Object[]) argument;

                assertEquals(2, args.length);

                String pUrl = (String) args[0];
                String pin = (String) args[1];

                assertEquals(null, pUrl);
                assertEquals(user.getPin(), pin);

                return true;
            }
        };

        doReturn(true).when(userNotificationImplSpy).sendSMSWithUrl(eq(user), eq("sms.activation.pin.text.for.nowtop40"), argThat(matcher));
        Future<Boolean> result = userNotificationImplSpy.sendActivationPinSMS(user);

        assertNotNull(result);
        assertEquals(true, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.activation.pin.not.for.device.type");
        verify(userNotificationImplSpy, times(1)).sendSMSWithUrl(eq(user), eq("sms.activation.pin.text.for.nowtop40"), argThat(matcher));
    }

    @Test
    public void testSendActivationPinSMS_RejectDevice_Success() throws Exception {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        doReturn(true).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.activation.pin.not.for.device.type");

        Future<Boolean> result = userNotificationImplSpy.sendActivationPinSMS(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.activation.pin.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.activation.pin.text"), any(String[].class));
    }

    @Test(expected = NullPointerException.class)
    public void testSendActivationPinSMS_NullUser_Failure() throws Exception {
        User user = null;

        userNotificationImplSpy.sendActivationPinSMS(user);
    }

    @Test
    public void testSendActivationPinSMS_NotHasAllDetails_Success() throws Exception {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setProvider(null);
        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.activation.pin.not.for.device.type");

        Future<Boolean> result = userNotificationImplSpy.sendActivationPinSMS(user);

        assertNotNull(result);
        assertEquals(false, result.get());

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.activation.pin.not.for.device.type");
        verify(userNotificationImplSpy, times(0)).sendSMSWithUrl(eq(user), eq("sms.activation.pin.text"), any(String[].class));
    }

    @Test
    public void testSendSMSWithUrl_MsgArgsDoesNotContainBaseUrl_Success() throws UnsupportedEncodingException {

        String msgCode = "msgCode";
        final String baseUrl = null;
        String[] msgArgs = {baseUrl};


        final String rewriteUrlParameter = "o2";
        final String rememberMeTokenCookieName = "rememberMeTokenCookieName";

        userNotificationImplSpy.setAvailableCommunities(new String[] {rewriteUrlParameter});
        userNotificationImplSpy.setRememberMeTokenCookieName(rememberMeTokenCookieName);

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(BUSINESS);
        user.setContract(null);
        user.setDeviceType(deviceType);

        String message = "message";
        String title = "title";
        final String rememberMeToken = "rememberMeToken";
        String url = "url";

        String tinyUrlService = "tinyUrlService";
        userNotificationImplSpy.setTinyUrlService(tinyUrlService);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.not.for.device.type");
        doReturn(rememberMeToken).when(nowTechTokenBasedRememberMeServicesMock).getRememberMeToken(user.getUserName(), user.getToken());

        ResponseEntity responseEntiytMock = mock(ResponseEntity.class);
        doReturn(url).when(responseEntiytMock).getBody();

        final ArgumentMatcher<MultiValueMap<String, Object>> matcher = new ArgumentMatcher<MultiValueMap<String, Object>>() {

            @Override
            public boolean matches(Object argument) {
                MultiValueMap<String, Object> request = (MultiValueMap<String, Object>) argument;

                assertNotNull(request);
                String expectedUrl = baseUrl + "?community=" + rewriteUrlParameter + "&" + rememberMeTokenCookieName + "=" + rememberMeToken;
                LinkedList<String> expectedLinkedList = new LinkedList<String>();
                expectedLinkedList.add(expectedUrl);

                assertEquals(expectedLinkedList, request.get("url"));

                return true;
            }
        };

        doReturn(responseEntiytMock).when(restTemplateMock).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));

        doReturn(message).when(userNotificationImplSpy).getMessage(user, msgCode, msgArgs);
        doReturn(title).when(communityResourceBundleMessageSourceMock).getMessage(rewriteUrlParameter, "sms.title", null, null);

        doReturn(migHttpServiceMock).when(smsServiceFacadeMock).getSMSProvider(anyString());

        MigResponse migResponse = MigResponse.successfulMigResponse();
        doReturn(migResponse).when(migHttpServiceMock).send(user.getMobile(), message, title);

        boolean wasSmsSentSuccessfully = userNotificationImplSpy.sendSMSWithUrl(user, msgCode, msgArgs);

        assertTrue(wasSmsSentSuccessfully);

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.not.for.device.type");
        verify(nowTechTokenBasedRememberMeServicesMock, times(0)).getRememberMeToken(user.getUserName(), user.getToken());
        verify(responseEntiytMock, times(0)).getBody();
        verify(restTemplateMock, times(0)).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));
        verify(userNotificationImplSpy, times(1)).getMessage(user, msgCode, msgArgs);
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(rewriteUrlParameter, "sms.title", null, null);
        verify(migHttpServiceMock, times(1)).send(user.getMobile(), message, title);
    }

    @Test
    public void testSendSMSWithUrl_MsgArgsIsNull_Success() throws UnsupportedEncodingException {

        String msgCode = "msgCode";
        final String baseUrl = null;
        String[] msgArgs = null;


        final String rewriteUrlParameter = "o2";
        final String rememberMeTokenCookieName = "rememberMeTokenCookieName";

        userNotificationImplSpy.setAvailableCommunities(new String[] {rewriteUrlParameter});
        userNotificationImplSpy.setRememberMeTokenCookieName(rememberMeTokenCookieName);

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(BUSINESS);
        user.setContract(null);
        user.setDeviceType(deviceType);

        String message = "message";
        String title = "title";
        final String rememberMeToken = "rememberMeToken";
        String url = "url";

        String tinyUrlService = "tinyUrlService";
        userNotificationImplSpy.setTinyUrlService(tinyUrlService);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.not.for.device.type");
        doReturn(rememberMeToken).when(nowTechTokenBasedRememberMeServicesMock).getRememberMeToken(user.getUserName(), user.getToken());

        ResponseEntity responseEntiytMock = mock(ResponseEntity.class);
        doReturn(url).when(responseEntiytMock).getBody();

        final ArgumentMatcher<MultiValueMap<String, Object>> matcher = new ArgumentMatcher<MultiValueMap<String, Object>>() {

            @Override
            public boolean matches(Object argument) {
                MultiValueMap<String, Object> request = (MultiValueMap<String, Object>) argument;

                assertNotNull(request);
                String expectedUrl = baseUrl + "?community=" + rewriteUrlParameter + "&" + rememberMeTokenCookieName + "=" + rememberMeToken;
                LinkedList<String> expectedLinkedList = new LinkedList<String>();
                expectedLinkedList.add(expectedUrl);

                assertEquals(expectedLinkedList, request.get("url"));

                return true;
            }
        };

        doReturn(responseEntiytMock).when(restTemplateMock).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));

        doReturn(message).when(userNotificationImplSpy).getMessage(user, msgCode, msgArgs);
        doReturn(title).when(communityResourceBundleMessageSourceMock).getMessage(rewriteUrlParameter, "sms.title", null, null);

        doReturn(migHttpServiceMock).when(smsServiceFacadeMock).getSMSProvider(anyString());

        MigResponse migResponse = MigResponse.successfulMigResponse();
        doReturn(migResponse).when(migHttpServiceMock).send(user.getMobile(), message, title);

        boolean wasSmsSentSuccessfully = userNotificationImplSpy.sendSMSWithUrl(user, msgCode, msgArgs);

        assertTrue(wasSmsSentSuccessfully);

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.not.for.device.type");
        verify(nowTechTokenBasedRememberMeServicesMock, times(0)).getRememberMeToken(user.getUserName(), user.getToken());
        verify(responseEntiytMock, times(0)).getBody();
        verify(restTemplateMock, times(0)).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));
        verify(userNotificationImplSpy, times(1)).getMessage(user, msgCode, msgArgs);
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(rewriteUrlParameter, "sms.title", null, null);
        verify(migHttpServiceMock, times(1)).send(user.getMobile(), message, title);
    }

    @Test
    public void testSendSMSWithUrl_MsgArgsContainBaseUrl_Success() throws UnsupportedEncodingException {

        String msgCode = "msgCode";
        final String baseUrl = "baseUrl";
        String[] msgArgs = {baseUrl};


        final String rewriteUrlParameter = "o2";
        final String rememberMeTokenCookieName = "rememberMeTokenCookieName";

        userNotificationImplSpy.setAvailableCommunities(new String[] {rewriteUrlParameter});
        userNotificationImplSpy.setRememberMeTokenCookieName(rememberMeTokenCookieName);

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(BUSINESS);
        user.setContract(null);
        user.setDeviceType(deviceType);

        String message = "message";
        String title = "title";
        final String rememberMeToken = "rememberMeToken";
        String url = "url";

        String tinyUrlService = "tinyUrlService";
        userNotificationImplSpy.setTinyUrlService(tinyUrlService);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.not.for.device.type");
        doReturn(rememberMeToken).when(nowTechTokenBasedRememberMeServicesMock).getRememberMeToken(user.getUserName(), user.getToken());

        ResponseEntity responseEntiytMock = mock(ResponseEntity.class);
        doReturn(url).when(responseEntiytMock).getBody();

        final ArgumentMatcher<MultiValueMap<String, Object>> matcher = new ArgumentMatcher<MultiValueMap<String, Object>>() {

            @Override
            public boolean matches(Object argument) {
                MultiValueMap<String, Object> request = (MultiValueMap<String, Object>) argument;

                assertNotNull(request);
                String expectedUrl = baseUrl + "?community=" + rewriteUrlParameter + "&" + rememberMeTokenCookieName + "=" + rememberMeToken;
                LinkedList<String> expectedLinkedList = new LinkedList<String>();
                expectedLinkedList.add(expectedUrl);

                assertEquals(expectedLinkedList, request.get("url"));

                return true;
            }
        };

        doReturn(responseEntiytMock).when(restTemplateMock).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));

        doReturn(message).when(userNotificationImplSpy).getMessage(user, msgCode, msgArgs);
        doReturn(title).when(communityResourceBundleMessageSourceMock).getMessage(rewriteUrlParameter, "sms.title", null, null);

        doReturn(migHttpServiceMock).when(smsServiceFacadeMock).getSMSProvider(anyString());

        MigResponse migResponse = MigResponse.successfulMigResponse();
        doReturn(migResponse).when(migHttpServiceMock).send(user.getMobile(), message, title);

        boolean wasSmsSentSuccessfully = userNotificationImplSpy.sendSMSWithUrl(user, msgCode, msgArgs);

        assertTrue(wasSmsSentSuccessfully);

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.not.for.device.type");
        verify(nowTechTokenBasedRememberMeServicesMock, times(1)).getRememberMeToken(user.getUserName(), user.getToken());
        verify(responseEntiytMock, times(1)).getBody();
        verify(restTemplateMock, times(1)).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));
        verify(userNotificationImplSpy, times(1)).getMessage(user, msgCode, msgArgs);
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(rewriteUrlParameter, "sms.title", null, null);
        verify(migHttpServiceMock, times(1)).send(user.getMobile(), message, title);
    }

    @Test(expected = NullPointerException.class)
    public void testSendSMSWithUrl_msgCodeIsNull_Failure() throws UnsupportedEncodingException {

        String msgCode = null;
        final String baseUrl = "baseUrl";
        String[] msgArgs = {baseUrl};


        final String rewriteUrlParameter = "o2";
        final String rememberMeTokenCookieName = "rememberMeTokenCookieName";

        userNotificationImplSpy.setAvailableCommunities(new String[] {rewriteUrlParameter});
        userNotificationImplSpy.setRememberMeTokenCookieName(rememberMeTokenCookieName);

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(BUSINESS);
        user.setContract(null);
        user.setDeviceType(deviceType);

        String message = "message";
        String title = "title";
        final String rememberMeToken = "rememberMeToken";
        String url = "url";

        String tinyUrlService = "tinyUrlService";
        userNotificationImplSpy.setTinyUrlService(tinyUrlService);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.not.for.device.type");
        doReturn(rememberMeToken).when(nowTechTokenBasedRememberMeServicesMock).getRememberMeToken(user.getUserName(), user.getToken());

        ResponseEntity responseEntiytMock = mock(ResponseEntity.class);
        doReturn(url).when(responseEntiytMock).getBody();

        final ArgumentMatcher<MultiValueMap<String, Object>> matcher = new ArgumentMatcher<MultiValueMap<String, Object>>() {

            @Override
            public boolean matches(Object argument) {
                MultiValueMap<String, Object> request = (MultiValueMap<String, Object>) argument;

                assertNotNull(request);
                String expectedUrl = baseUrl + "?community=" + rewriteUrlParameter + "&" + rememberMeTokenCookieName + "=" + rememberMeToken;
                LinkedList<String> expectedLinkedList = new LinkedList<String>();
                expectedLinkedList.add(expectedUrl);

                assertEquals(expectedLinkedList, request.get("url"));

                return true;
            }
        };

        doReturn(responseEntiytMock).when(restTemplateMock).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));

        doReturn(message).when(userNotificationImplSpy).getMessage(user, msgCode, msgArgs);
        doReturn(title).when(communityResourceBundleMessageSourceMock).getMessage(rewriteUrlParameter, "sms.title", null, null);

        MigResponse migResponse = MigResponse.successfulMigResponse();
        doReturn(migResponse).when(migHttpServiceMock).send(user.getMobile(), message, title);

        boolean wasSmsSentSuccessfully = userNotificationImplSpy.sendSMSWithUrl(user, msgCode, msgArgs);

        assertTrue(wasSmsSentSuccessfully);

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.not.for.device.type");
        verify(nowTechTokenBasedRememberMeServicesMock, times(0)).getRememberMeToken(user.getUserName(), user.getToken());
        verify(responseEntiytMock, times(0)).getBody();
        verify(restTemplateMock, times(0)).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));
        verify(userNotificationImplSpy, times(0)).getMessage(user, msgCode, msgArgs);
        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(rewriteUrlParameter, "sms.title", null, null);
        verify(migHttpServiceMock, times(0)).send(user.getMobile(), message, title);
    }

    @Test
    public void testSendSMSWithUrl_rejectedtDevice_Success() throws UnsupportedEncodingException {

        String msgCode = "msgCode";
        final String baseUrl = "baseUrl";
        String[] msgArgs = {baseUrl};


        final String rewriteUrlParameter = "o2";
        final String rememberMeTokenCookieName = "rememberMeTokenCookieName";

        userNotificationImplSpy.setAvailableCommunities(new String[] {});
        userNotificationImplSpy.setRememberMeTokenCookieName(rememberMeTokenCookieName);

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(BUSINESS);
        user.setContract(null);
        user.setDeviceType(deviceType);

        String message = "message";
        String title = "title";
        final String rememberMeToken = "rememberMeToken";
        String url = "url";

        String tinyUrlService = "tinyUrlService";
        userNotificationImplSpy.setTinyUrlService(tinyUrlService);

        doReturn(true).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.not.for.device.type");
        doReturn(rememberMeToken).when(nowTechTokenBasedRememberMeServicesMock).getRememberMeToken(user.getUserName(), user.getToken());

        ResponseEntity responseEntiytMock = mock(ResponseEntity.class);
        doReturn(url).when(responseEntiytMock).getBody();

        final ArgumentMatcher<MultiValueMap<String, Object>> matcher = new ArgumentMatcher<MultiValueMap<String, Object>>() {

            @Override
            public boolean matches(Object argument) {
                MultiValueMap<String, Object> request = (MultiValueMap<String, Object>) argument;

                assertNotNull(request);
                String expectedUrl = baseUrl + "?community=" + rewriteUrlParameter + "&" + rememberMeTokenCookieName + "=" + rememberMeToken;
                LinkedList<String> expectedLinkedList = new LinkedList<String>();
                expectedLinkedList.add(expectedUrl);

                assertEquals(expectedLinkedList, request.get("url"));

                return true;
            }
        };

        doReturn(responseEntiytMock).when(restTemplateMock).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));

        doReturn(message).when(userNotificationImplSpy).getMessage(user, msgCode, msgArgs);
        doReturn(title).when(communityResourceBundleMessageSourceMock).getMessage(rewriteUrlParameter, "sms.title", null, null);

        MigResponse migResponse = MigResponse.successfulMigResponse();
        doReturn(migResponse).when(migHttpServiceMock).send(user.getMobile(), message, title);

        boolean wasSmsSentSuccessfully = userNotificationImplSpy.sendSMSWithUrl(user, msgCode, msgArgs);

        assertFalse(wasSmsSentSuccessfully);

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.not.for.device.type");
        verify(nowTechTokenBasedRememberMeServicesMock, times(0)).getRememberMeToken(user.getUserName(), user.getToken());
        verify(responseEntiytMock, times(0)).getBody();
        verify(restTemplateMock, times(0)).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));
        verify(userNotificationImplSpy, times(0)).getMessage(user, msgCode, msgArgs);
        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(rewriteUrlParameter, "sms.title", null, null);
        verify(migHttpServiceMock, times(0)).send(user.getMobile(), message, title);
    }

    @Test
    public void testSendSMSWithUrl_promotedPhoneNumber_Success() throws UnsupportedEncodingException {

        String msgCode = "msgCode";
        final String baseUrl = "baseUrl";
        String[] msgArgs = {baseUrl};


        final String rewriteUrlParameter = "o2";
        final String rememberMeTokenCookieName = "rememberMeTokenCookieName";

        userNotificationImplSpy.setAvailableCommunities(new String[] {rewriteUrlParameter});
        userNotificationImplSpy.setRememberMeTokenCookieName(rememberMeTokenCookieName);

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(BUSINESS);
        user.setContract(null);
        user.setDeviceType(deviceType);

        String message = "message";
        String title = "title";
        final String rememberMeToken = "rememberMeToken";
        String url = "url";

        String tinyUrlService = "tinyUrlService";
        userNotificationImplSpy.setTinyUrlService(tinyUrlService);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.not.for.device.type");
        doReturn(true).when(deviceServiceMock).isPromotedDevicePhone(o2Community, user.getMobile(), null);
        doReturn(rememberMeToken).when(nowTechTokenBasedRememberMeServicesMock).getRememberMeToken(user.getUserName(), user.getToken());

        ResponseEntity responseEntiytMock = mock(ResponseEntity.class);
        doReturn(url).when(responseEntiytMock).getBody();

        final ArgumentMatcher<MultiValueMap<String, Object>> matcher = new ArgumentMatcher<MultiValueMap<String, Object>>() {

            @Override
            public boolean matches(Object argument) {
                MultiValueMap<String, Object> request = (MultiValueMap<String, Object>) argument;

                assertNotNull(request);
                String expectedUrl = baseUrl + "?community=" + rewriteUrlParameter + "&" + rememberMeTokenCookieName + "=" + rememberMeToken;
                LinkedList<String> expectedLinkedList = new LinkedList<String>();
                expectedLinkedList.add(expectedUrl);

                assertEquals(expectedLinkedList, request.get("url"));

                return true;
            }
        };

        doReturn(responseEntiytMock).when(restTemplateMock).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));

        doReturn(message).when(userNotificationImplSpy).getMessage(user, msgCode, msgArgs);
        doReturn(title).when(communityResourceBundleMessageSourceMock).getMessage(rewriteUrlParameter, "sms.title", null, null);

        MigResponse migResponse = MigResponse.successfulMigResponse();
        doReturn(migResponse).when(migHttpServiceMock).send(user.getMobile(), message, title);

        boolean wasSmsSentSuccessfully = userNotificationImplSpy.sendSMSWithUrl(user, msgCode, msgArgs);

        assertFalse(wasSmsSentSuccessfully);

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.not.for.device.type");
        verify(deviceServiceMock, times(1)).isPromotedDevicePhone(o2Community, user.getMobile(), null);
        verify(nowTechTokenBasedRememberMeServicesMock, times(0)).getRememberMeToken(user.getUserName(), user.getToken());
        verify(responseEntiytMock, times(0)).getBody();
        verify(restTemplateMock, times(0)).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));
        verify(userNotificationImplSpy, times(0)).getMessage(user, msgCode, msgArgs);
        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(rewriteUrlParameter, "sms.title", null, null);
        verify(migHttpServiceMock, times(0)).send(user.getMobile(), message, title);
    }

    @Test
    public void testSendSMSWithUrl_AvailableCommunitiesDoesNotContainSuchCommunity_Success() throws UnsupportedEncodingException {

        String msgCode = "msgCode";
        final String baseUrl = "baseUrl";
        String[] msgArgs = {baseUrl};


        final String rewriteUrlParameter = "o2";
        final String rememberMeTokenCookieName = "rememberMeTokenCookieName";

        userNotificationImplSpy.setAvailableCommunities(new String[] {});
        userNotificationImplSpy.setRememberMeTokenCookieName(rememberMeTokenCookieName);

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(BUSINESS);
        user.setContract(null);
        user.setDeviceType(deviceType);

        String message = "message";
        String title = "title";
        final String rememberMeToken = "rememberMeToken";
        String url = "url";

        String tinyUrlService = "tinyUrlService";
        userNotificationImplSpy.setTinyUrlService(tinyUrlService);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.not.for.device.type");
        doReturn(rememberMeToken).when(nowTechTokenBasedRememberMeServicesMock).getRememberMeToken(user.getUserName(), user.getToken());

        ResponseEntity responseEntiytMock = mock(ResponseEntity.class);
        doReturn(url).when(responseEntiytMock).getBody();

        final ArgumentMatcher<MultiValueMap<String, Object>> matcher = new ArgumentMatcher<MultiValueMap<String, Object>>() {

            @Override
            public boolean matches(Object argument) {
                MultiValueMap<String, Object> request = (MultiValueMap<String, Object>) argument;

                assertNotNull(request);
                String expectedUrl = baseUrl + "?community=" + rewriteUrlParameter + "&" + rememberMeTokenCookieName + "=" + rememberMeToken;
                LinkedList<String> expectedLinkedList = new LinkedList<String>();
                expectedLinkedList.add(expectedUrl);

                assertEquals(expectedLinkedList, request.get("url"));

                return true;
            }
        };

        doReturn(responseEntiytMock).when(restTemplateMock).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));

        doReturn(message).when(userNotificationImplSpy).getMessage(user, msgCode, msgArgs);
        doReturn(title).when(communityResourceBundleMessageSourceMock).getMessage(rewriteUrlParameter, "sms.title", null, null);

        MigResponse migResponse = MigResponse.successfulMigResponse();
        doReturn(migResponse).when(migHttpServiceMock).send(user.getMobile(), message, title);

        boolean wasSmsSentSuccessfully = userNotificationImplSpy.sendSMSWithUrl(user, msgCode, msgArgs);

        assertFalse(wasSmsSentSuccessfully);

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.not.for.device.type");
        verify(nowTechTokenBasedRememberMeServicesMock, times(0)).getRememberMeToken(user.getUserName(), user.getToken());
        verify(responseEntiytMock, times(0)).getBody();
        verify(restTemplateMock, times(0)).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));
        verify(userNotificationImplSpy, times(0)).getMessage(user, msgCode, msgArgs);
        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(rewriteUrlParameter, "sms.title", null, null);
        verify(migHttpServiceMock, times(0)).send(user.getMobile(), message, title);
    }

    @Test
    public void testSendSMSWithUrl_MessageIsNull_Success() throws UnsupportedEncodingException {

        String msgCode = "msgCode";
        final String baseUrl = "baseUrl";
        String[] msgArgs = {baseUrl};


        final String rewriteUrlParameter = "o2";
        final String rememberMeTokenCookieName = "rememberMeTokenCookieName";

        userNotificationImplSpy.setAvailableCommunities(new String[] {rewriteUrlParameter});
        userNotificationImplSpy.setRememberMeTokenCookieName(rememberMeTokenCookieName);

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(BUSINESS);
        user.setContract(null);
        user.setDeviceType(deviceType);

        String message = null;
        String title = "title";
        final String rememberMeToken = "rememberMeToken";
        String url = "url";

        String tinyUrlService = "tinyUrlService";
        userNotificationImplSpy.setTinyUrlService(tinyUrlService);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.not.for.device.type");
        doReturn(rememberMeToken).when(nowTechTokenBasedRememberMeServicesMock).getRememberMeToken(user.getUserName(), user.getToken());

        ResponseEntity responseEntiytMock = mock(ResponseEntity.class);
        doReturn(url).when(responseEntiytMock).getBody();

        final ArgumentMatcher<MultiValueMap<String, Object>> matcher = new ArgumentMatcher<MultiValueMap<String, Object>>() {

            @Override
            public boolean matches(Object argument) {
                MultiValueMap<String, Object> request = (MultiValueMap<String, Object>) argument;

                assertNotNull(request);
                String expectedUrl = baseUrl + "?community=" + rewriteUrlParameter + "&" + rememberMeTokenCookieName + "=" + rememberMeToken;
                LinkedList<String> expectedLinkedList = new LinkedList<String>();
                expectedLinkedList.add(expectedUrl);

                assertEquals(expectedLinkedList, request.get("url"));

                return true;
            }
        };

        doReturn(responseEntiytMock).when(restTemplateMock).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));

        doReturn(message).when(userNotificationImplSpy).getMessage(user, msgCode, msgArgs);
        doReturn(title).when(communityResourceBundleMessageSourceMock).getMessage(rewriteUrlParameter, "sms.title", null, null);

        MigResponse migResponse = MigResponse.successfulMigResponse();
        doReturn(migResponse).when(migHttpServiceMock).send(user.getMobile(), message, title);

        boolean wasSmsSentSuccessfully = userNotificationImplSpy.sendSMSWithUrl(user, msgCode, msgArgs);

        assertFalse(wasSmsSentSuccessfully);

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.not.for.device.type");
        verify(nowTechTokenBasedRememberMeServicesMock, times(1)).getRememberMeToken(user.getUserName(), user.getToken());
        verify(responseEntiytMock, times(1)).getBody();
        verify(restTemplateMock, times(1)).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));
        verify(userNotificationImplSpy, times(1)).getMessage(user, msgCode, msgArgs);
        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(rewriteUrlParameter, "sms.title", null, null);
        verify(migHttpServiceMock, times(0)).send(user.getMobile(), message, title);
    }

    @Test
    public void testSendSMSWithUrl_MessageIsBlank_Success() throws UnsupportedEncodingException {

        String msgCode = "msgCode";
        final String baseUrl = "baseUrl";
        String[] msgArgs = {baseUrl};


        final String rewriteUrlParameter = "o2";
        final String rememberMeTokenCookieName = "rememberMeTokenCookieName";

        userNotificationImplSpy.setAvailableCommunities(new String[] {rewriteUrlParameter});
        userNotificationImplSpy.setRememberMeTokenCookieName(rememberMeTokenCookieName);

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(BUSINESS);
        user.setContract(null);
        user.setDeviceType(deviceType);

        String message = "    ";
        String title = "title";
        final String rememberMeToken = "rememberMeToken";
        String url = "url";

        String tinyUrlService = "tinyUrlService";
        userNotificationImplSpy.setTinyUrlService(tinyUrlService);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.not.for.device.type");
        doReturn(rememberMeToken).when(nowTechTokenBasedRememberMeServicesMock).getRememberMeToken(user.getUserName(), user.getToken());

        ResponseEntity responseEntiytMock = mock(ResponseEntity.class);
        doReturn(url).when(responseEntiytMock).getBody();

        final ArgumentMatcher<MultiValueMap<String, Object>> matcher = new ArgumentMatcher<MultiValueMap<String, Object>>() {

            @Override
            public boolean matches(Object argument) {
                MultiValueMap<String, Object> request = (MultiValueMap<String, Object>) argument;

                assertNotNull(request);
                String expectedUrl = baseUrl + "?community=" + rewriteUrlParameter + "&" + rememberMeTokenCookieName + "=" + rememberMeToken;
                LinkedList<String> expectedLinkedList = new LinkedList<String>();
                expectedLinkedList.add(expectedUrl);

                assertEquals(expectedLinkedList, request.get("url"));

                return true;
            }
        };

        doReturn(responseEntiytMock).when(restTemplateMock).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));

        doReturn(message).when(userNotificationImplSpy).getMessage(user, msgCode, msgArgs);
        doReturn(title).when(communityResourceBundleMessageSourceMock).getMessage(rewriteUrlParameter, "sms.title", null, null);

        MigResponse migResponse = MigResponse.successfulMigResponse();
        doReturn(migResponse).when(migHttpServiceMock).send(user.getMobile(), message, title);

        boolean wasSmsSentSuccessfully = userNotificationImplSpy.sendSMSWithUrl(user, msgCode, msgArgs);

        assertFalse(wasSmsSentSuccessfully);

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.not.for.device.type");
        verify(nowTechTokenBasedRememberMeServicesMock, times(1)).getRememberMeToken(user.getUserName(), user.getToken());
        verify(responseEntiytMock, times(1)).getBody();
        verify(restTemplateMock, times(1)).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));
        verify(userNotificationImplSpy, times(1)).getMessage(user, msgCode, msgArgs);
        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(rewriteUrlParameter, "sms.title", null, null);
        verify(migHttpServiceMock, times(0)).send(user.getMobile(), message, title);
    }

    @Test
    public void testSendSMSWithUrl_MigResponseIsFalse_Success() throws UnsupportedEncodingException {

        String msgCode = "msgCode";
        final String baseUrl = "baseUrl";
        String[] msgArgs = {baseUrl};


        final String rewriteUrlParameter = "o2";
        final String rememberMeTokenCookieName = "rememberMeTokenCookieName";

        userNotificationImplSpy.setAvailableCommunities(new String[] {rewriteUrlParameter});
        userNotificationImplSpy.setRememberMeTokenCookieName(rememberMeTokenCookieName);

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(BUSINESS);
        user.setContract(null);
        user.setDeviceType(deviceType);

        String message = "message";
        String title = "title";
        final String rememberMeToken = "rememberMeToken";
        String url = "url";

        String tinyUrlService = "tinyUrlService";
        userNotificationImplSpy.setTinyUrlService(tinyUrlService);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.not.for.device.type");
        doReturn(rememberMeToken).when(nowTechTokenBasedRememberMeServicesMock).getRememberMeToken(user.getUserName(), user.getToken());

        ResponseEntity responseEntiytMock = mock(ResponseEntity.class);
        doReturn(url).when(responseEntiytMock).getBody();

        final ArgumentMatcher<MultiValueMap<String, Object>> matcher = new ArgumentMatcher<MultiValueMap<String, Object>>() {

            @Override
            public boolean matches(Object argument) {
                MultiValueMap<String, Object> request = (MultiValueMap<String, Object>) argument;

                assertNotNull(request);
                String expectedUrl = baseUrl + "?community=" + rewriteUrlParameter + "&" + rememberMeTokenCookieName + "=" + rememberMeToken;
                LinkedList<String> expectedLinkedList = new LinkedList<String>();
                expectedLinkedList.add(expectedUrl);

                assertEquals(expectedLinkedList, request.get("url"));

                return true;
            }
        };

        doReturn(responseEntiytMock).when(restTemplateMock).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));

        doReturn(message).when(userNotificationImplSpy).getMessage(user, msgCode, msgArgs);
        doReturn(title).when(communityResourceBundleMessageSourceMock).getMessage(rewriteUrlParameter, "sms.title", null, null);

        doReturn(migHttpServiceMock).when(smsServiceFacadeMock).getSMSProvider(anyString());

        MigResponse migResponse = MigResponse.failMigResponse("error");
        doReturn(migResponse).when(migHttpServiceMock).send(user.getMobile(), message, title);

        boolean wasSmsSentSuccessfully = userNotificationImplSpy.sendSMSWithUrl(user, msgCode, msgArgs);

        assertFalse(wasSmsSentSuccessfully);

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.not.for.device.type");
        verify(nowTechTokenBasedRememberMeServicesMock, times(1)).getRememberMeToken(user.getUserName(), user.getToken());
        verify(responseEntiytMock, times(1)).getBody();
        verify(restTemplateMock, times(1)).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));
        verify(userNotificationImplSpy, times(1)).getMessage(user, msgCode, msgArgs);
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(rewriteUrlParameter, "sms.title", null, null);
        verify(migHttpServiceMock, times(1)).send(user.getMobile(), message, title);
    }

    @Test
    public void testSendSMSWithUrl_restTemplateThrowsException_Success() throws UnsupportedEncodingException {

        String msgCode = "msgCode";
        final String baseUrl = "baseUrl";
        String[] msgArgs = {baseUrl};


        final String rewriteUrlParameter = "o2";
        final String rememberMeTokenCookieName = "rememberMeTokenCookieName";

        userNotificationImplSpy.setAvailableCommunities(new String[] {rewriteUrlParameter});
        userNotificationImplSpy.setRememberMeTokenCookieName(rememberMeTokenCookieName);

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(BUSINESS);
        user.setContract(null);
        user.setDeviceType(deviceType);

        String message = "message";
        String title = "title";
        final String rememberMeToken = "rememberMeToken";
        String url = "url";

        String tinyUrlService = "tinyUrlService";
        userNotificationImplSpy.setTinyUrlService(tinyUrlService);

        doReturn(false).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.not.for.device.type");
        doReturn(rememberMeToken).when(nowTechTokenBasedRememberMeServicesMock).getRememberMeToken(user.getUserName(), user.getToken());

        ResponseEntity responseEntiytMock = mock(ResponseEntity.class);
        doReturn(url).when(responseEntiytMock).getBody();

        final ArgumentMatcher<MultiValueMap<String, Object>> matcher = new ArgumentMatcher<MultiValueMap<String, Object>>() {

            @Override
            public boolean matches(Object argument) {
                MultiValueMap<String, Object> request = (MultiValueMap<String, Object>) argument;

                assertNotNull(request);
                String expectedUrl = baseUrl + "?community=" + rewriteUrlParameter + "&" + rememberMeTokenCookieName + "=" + rememberMeToken;
                LinkedList<String> expectedLinkedList = new LinkedList<String>();
                expectedLinkedList.add(expectedUrl);

                assertEquals(expectedLinkedList, request.get("url"));

                return true;
            }
        };

        doThrow(new IllegalArgumentException()).when(restTemplateMock).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));

        doReturn(message).when(userNotificationImplSpy).getMessage(user, msgCode, msgArgs);
        doReturn(title).when(communityResourceBundleMessageSourceMock).getMessage(rewriteUrlParameter, "sms.title", null, null);

        doReturn(migHttpServiceMock).when(smsServiceFacadeMock).getSMSProvider(anyString());

        MigResponse migResponse = MigResponse.successfulMigResponse();
        doReturn(migResponse).when(migHttpServiceMock).send(user.getMobile(), message, title);

        boolean wasSmsSentSuccessfully = userNotificationImplSpy.sendSMSWithUrl(user, msgCode, msgArgs);

        assertTrue(wasSmsSentSuccessfully);

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.not.for.device.type");
        verify(nowTechTokenBasedRememberMeServicesMock, times(1)).getRememberMeToken(user.getUserName(), user.getToken());
        verify(responseEntiytMock, times(0)).getBody();
        verify(restTemplateMock, times(1)).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));
        verify(userNotificationImplSpy, times(1)).getMessage(user, msgCode, msgArgs);
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(rewriteUrlParameter, "sms.title", null, null);
        verify(migHttpServiceMock, times(1)).send(user.getMobile(), message, title);
    }

    @Test()
    public void testSendSMSWithUrl_communityUrlIsNull_Success() throws UnsupportedEncodingException {

        String msgCode = "msgCode";
        final String baseUrl = "baseUrl";
        String[] msgArgs = {baseUrl};


        final String rewriteUrlParameter = "o2";
        final String rememberMeTokenCookieName = "rememberMeTokenCookieName";

        userNotificationImplSpy.setAvailableCommunities(new String[] {});
        userNotificationImplSpy.setRememberMeTokenCookieName(rememberMeTokenCookieName);

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(null);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(BUSINESS);
        user.setContract(null);
        user.setDeviceType(deviceType);

        String message = "message";
        String title = "title";
        final String rememberMeToken = "rememberMeToken";
        String url = "url";

        String tinyUrlService = "tinyUrlService";
        userNotificationImplSpy.setTinyUrlService(tinyUrlService);

        doReturn(true).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.not.for.device.type");
        doReturn(rememberMeToken).when(nowTechTokenBasedRememberMeServicesMock).getRememberMeToken(user.getUserName(), user.getToken());

        ResponseEntity responseEntiytMock = mock(ResponseEntity.class);
        doReturn(url).when(responseEntiytMock).getBody();

        final ArgumentMatcher<MultiValueMap<String, Object>> matcher = new ArgumentMatcher<MultiValueMap<String, Object>>() {

            @Override
            public boolean matches(Object argument) {
                MultiValueMap<String, Object> request = (MultiValueMap<String, Object>) argument;

                assertNotNull(request);
                String expectedUrl = baseUrl + "?community=" + rewriteUrlParameter + "&" + rememberMeTokenCookieName + "=" + rememberMeToken;
                LinkedList<String> expectedLinkedList = new LinkedList<String>();
                expectedLinkedList.add(expectedUrl);

                assertEquals(expectedLinkedList, request.get("url"));

                return true;
            }
        };

        doReturn(responseEntiytMock).when(restTemplateMock).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));

        doReturn(message).when(userNotificationImplSpy).getMessage(user, msgCode, msgArgs);
        doReturn(title).when(communityResourceBundleMessageSourceMock).getMessage(rewriteUrlParameter, "sms.title", null, null);

        MigResponse migResponse = MigResponse.successfulMigResponse();
        doReturn(migResponse).when(migHttpServiceMock).send(user.getMobile(), message, title);

        boolean wasSmsSentSuccessfully = userNotificationImplSpy.sendSMSWithUrl(user, msgCode, msgArgs);

        assertFalse(wasSmsSentSuccessfully);

        verify(userNotificationImplSpy, times(0)).rejectDevice(user, "sms.notification.not.for.device.type");
        verify(nowTechTokenBasedRememberMeServicesMock, times(0)).getRememberMeToken(user.getUserName(), user.getToken());
        verify(responseEntiytMock, times(0)).getBody();
        verify(restTemplateMock, times(0)).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));
        verify(userNotificationImplSpy, times(0)).getMessage(user, msgCode, msgArgs);
        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(rewriteUrlParameter, "sms.title", null, null);
        verify(migHttpServiceMock, times(0)).send(user.getMobile(), message, title);
    }

    @Test(expected = NullPointerException.class)
    public void testSendSMSWithUrl_userIsNull_Failure() throws UnsupportedEncodingException {

        String msgCode = "msgCode";
        final String baseUrl = "baseUrl";
        String[] msgArgs = {baseUrl};


        final String rewriteUrlParameter = "o2";
        final String rememberMeTokenCookieName = "rememberMeTokenCookieName";

        userNotificationImplSpy.setAvailableCommunities(new String[] {});
        userNotificationImplSpy.setRememberMeTokenCookieName(rememberMeTokenCookieName);

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        User user = null;

        String message = "message";
        String title = "title";
        final String rememberMeToken = "rememberMeToken";
        String url = "url";

        String tinyUrlService = "tinyUrlService";
        userNotificationImplSpy.setTinyUrlService(tinyUrlService);

        doReturn(true).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.not.for.device.type");
        doReturn(rememberMeToken).when(nowTechTokenBasedRememberMeServicesMock).getRememberMeToken(any(String.class), any(String.class));

        ResponseEntity responseEntityMock = mock(ResponseEntity.class);
        doReturn(url).when(responseEntityMock).getBody();

        final ArgumentMatcher<MultiValueMap<String, Object>> matcher = new ArgumentMatcher<MultiValueMap<String, Object>>() {

            @Override
            public boolean matches(Object argument) {
                MultiValueMap<String, Object> request = (MultiValueMap<String, Object>) argument;

                assertNotNull(request);
                String expectedUrl = baseUrl + "?community=" + rewriteUrlParameter + "&" + rememberMeTokenCookieName + "=" + rememberMeToken;
                LinkedList<String> expectedLinkedList = new LinkedList<String>();
                expectedLinkedList.add(expectedUrl);

                assertEquals(expectedLinkedList, request.get("url"));

                return true;
            }
        };

        doReturn(responseEntityMock).when(restTemplateMock).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));

        doReturn(message).when(userNotificationImplSpy).getMessage(user, msgCode, msgArgs);
        doReturn(title).when(communityResourceBundleMessageSourceMock).getMessage(rewriteUrlParameter, "sms.title", null, null);

        MigResponse migResponse = MigResponse.successfulMigResponse();
        doReturn(migResponse).when(migHttpServiceMock).send(any(String.class), eq(message), eq(title));

        boolean wasSmsSentSuccessfully = userNotificationImplSpy.sendSMSWithUrl(user, msgCode, msgArgs);

        assertFalse(wasSmsSentSuccessfully);

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.not.for.device.type");
        verify(nowTechTokenBasedRememberMeServicesMock, times(0)).getRememberMeToken(any(String.class), any(String.class));
        verify(responseEntityMock, times(0)).getBody();
        verify(restTemplateMock, times(0)).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));
        verify(userNotificationImplSpy, times(0)).getMessage(user, msgCode, msgArgs);
        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(rewriteUrlParameter, "sms.title", null, null);
        verify(migHttpServiceMock, times(0)).send(any(String.class), eq(message), eq(title));
    }

    @Test(expected = NullPointerException.class)
    public void testSendSMSWithUrl_userGroupIsNull_Failure() throws UnsupportedEncodingException {

        String msgCode = "msgCode";
        final String baseUrl = "baseUrl";
        String[] msgArgs = {baseUrl};


        final String rewriteUrlParameter = "o2";
        final String rememberMeTokenCookieName = "rememberMeTokenCookieName";

        userNotificationImplSpy.setAvailableCommunities(new String[] {});
        userNotificationImplSpy.setRememberMeTokenCookieName(rememberMeTokenCookieName);

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(null);
        user.setProvider(null);
        user.setSegment(BUSINESS);
        user.setContract(null);
        user.setDeviceType(deviceType);

        String message = "message";
        String title = "title";
        final String rememberMeToken = "rememberMeToken";
        String url = "url";

        String tinyUrlService = "tinyUrlService";
        userNotificationImplSpy.setTinyUrlService(tinyUrlService);

        doReturn(true).when(userNotificationImplSpy).rejectDevice(user, "sms.notification.not.for.device.type");
        doReturn(rememberMeToken).when(nowTechTokenBasedRememberMeServicesMock).getRememberMeToken(user.getUserName(), user.getToken());

        ResponseEntity responseEntiytMock = mock(ResponseEntity.class);
        doReturn(url).when(responseEntiytMock).getBody();

        final ArgumentMatcher<MultiValueMap<String, Object>> matcher = new ArgumentMatcher<MultiValueMap<String, Object>>() {

            @Override
            public boolean matches(Object argument) {
                MultiValueMap<String, Object> request = (MultiValueMap<String, Object>) argument;

                assertNotNull(request);
                String expectedUrl = baseUrl + "?community=" + rewriteUrlParameter + "&" + rememberMeTokenCookieName + "=" + rememberMeToken;
                LinkedList<String> expectedLinkedList = new LinkedList<String>();
                expectedLinkedList.add(expectedUrl);

                assertEquals(expectedLinkedList, request.get("url"));

                return true;
            }
        };

        doReturn(responseEntiytMock).when(restTemplateMock).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));

        doReturn(message).when(userNotificationImplSpy).getMessage(user, msgCode, msgArgs);
        doReturn(title).when(communityResourceBundleMessageSourceMock).getMessage(rewriteUrlParameter, "sms.title", null, null);

        MigResponse migResponse = MigResponse.successfulMigResponse();
        doReturn(migResponse).when(migHttpServiceMock).send(user.getMobile(), message, title);

        boolean wasSmsSentSuccessfully = userNotificationImplSpy.sendSMSWithUrl(user, msgCode, msgArgs);

        assertFalse(wasSmsSentSuccessfully);

        verify(userNotificationImplSpy, times(1)).rejectDevice(user, "sms.notification.not.for.device.type");
        verify(nowTechTokenBasedRememberMeServicesMock, times(0)).getRememberMeToken(user.getUserName(), user.getToken());
        verify(responseEntiytMock, times(0)).getBody();
        verify(restTemplateMock, times(0)).postForEntity(eq(tinyUrlService), argThat(matcher), eq(String.class));
        verify(userNotificationImplSpy, times(0)).getMessage(user, msgCode, msgArgs);
        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(rewriteUrlParameter, "sms.title", null, null);
        verify(migHttpServiceMock, times(0)).send(user.getMobile(), message, title);
    }

    @Before
    public void setUp() throws Exception {
        userNotificationImplSpy = spy(new UserNotificationServiceImpl());

        userServiceMock = Mockito.mock(UserService.class);
        communityResourceBundleMessageSourceMock = mock(CommunityResourceBundleMessageSource.class);
        nowTechTokenBasedRememberMeServicesMock = mock(NowTechTokenBasedRememberMeServices.class);
        restTemplateMock = mock(RestTemplate.class);
        migHttpServiceMock = mock(MigHttpService.class);
        deviceServiceMock = mock(DevicePromotionsService.class);
        paymentDetailsRepository = mock(PaymentDetailsRepository.class);
        smsServiceFacadeMock = mock(SmsServiceFacade.class);

        userNotificationImplSpy.setUserService(userServiceMock);
        userNotificationImplSpy.setPaymentsUrl("paymentsUrl");
        userNotificationImplSpy.setUnsubscribeUrl("unsubscribeUrl");
        userNotificationImplSpy.setMessageSource(communityResourceBundleMessageSourceMock);
        userNotificationImplSpy.setRestTemplate(restTemplateMock);
        userNotificationImplSpy.setRememberMeServices(nowTechTokenBasedRememberMeServicesMock);
        userNotificationImplSpy.setDeviceService(deviceServiceMock);
        userNotificationImplSpy.setPaymentDetailsRepository(paymentDetailsRepository);
        userNotificationImplSpy.setSmsServiceFacade(smsServiceFacadeMock);
    }
}