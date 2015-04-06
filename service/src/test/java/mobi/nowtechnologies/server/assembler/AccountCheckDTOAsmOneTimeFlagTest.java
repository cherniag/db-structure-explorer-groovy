package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.AutoOptInExemptPhoneNumber;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.DrmPolicy;
import mobi.nowtechnologies.server.persistence.domain.DrmType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.AutoOptInExemptPhoneNumberRepository;
import mobi.nowtechnologies.server.service.itunes.payment.ITunesPaymentService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService;

import org.junit.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class AccountCheckDTOAsmOneTimeFlagTest {

    @Mock
    private AutoOptInExemptPhoneNumberRepository autoOptInExemptPhoneNumberRepository;
    @Mock
    private UserDetailsDtoAsm userDetailsDtoAsm;
    @Mock
    private AutoOptInRuleService autoOptInRuleService;
    @Mock
    private ITunesPaymentService iTunesPaymentService;
    @InjectMocks
    private AccountCheckDTOAsm accountCheckDTOAsm;

    @Mock
    private AutoOptInExemptPhoneNumber autoOptInExemptPhoneNumber;
    @Mock
    private User user;
    @Mock
    private UserGroup userGroup;
    @Mock
    private Chart chart;
    @Mock
    private DrmPolicy drmPolicy;
    @Mock
    private DrmType drmType;
    @Mock
    private UserStatus userStatus;
    @Mock
    private DeviceType deviceType;
    @Mock
    private PaymentDetails paymentDetails;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(user.getUserGroup()).thenReturn(userGroup);
        when(user.getStatus()).thenReturn(userStatus);
        when(user.getDeviceType()).thenReturn(deviceType);
        when(userGroup.getChart()).thenReturn(chart);
        when(userGroup.getDrmPolicy()).thenReturn(drmPolicy);
        when(drmPolicy.getDrmType()).thenReturn(drmType);
    }

    @Test
    public void iosFreeTrial() throws Exception {
        when(deviceType.getName()).thenReturn(DeviceType.IOS);
        when(user.isSubscribedStatus()).thenReturn(true);
        when(user.isOnFreeTrial()).thenReturn(true);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(null);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(true);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, false, false, false, true);

        assertNull(accountCheckDTO.oneTimePayment);
    }

    @Test
    public void androidFreeTrialWithoutPaymentDetails() throws Exception {
        when(deviceType.getName()).thenReturn(DeviceType.ANDROID);
        when(user.isSubscribedStatus()).thenReturn(true);
        when(user.isOnFreeTrial()).thenReturn(true);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(null);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(true);
        when(user.getCurrentPaymentDetails()).thenReturn(null);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, false, false, false, true);

        assertNull(accountCheckDTO.oneTimePayment);
    }

    @Test
    public void androidFreeTrialActivatedPaymentDetails() throws Exception {
        when(deviceType.getName()).thenReturn(DeviceType.ANDROID);
        when(user.isSubscribedStatus()).thenReturn(true);
        when(user.isOnFreeTrial()).thenReturn(true);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(null);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(true);
        when(user.getCurrentPaymentDetails()).thenReturn(paymentDetails);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, false, false, false, true);

        assertNull(accountCheckDTO.oneTimePayment);
    }

    @Test
    public void iosSubscribedOnOneTime() throws Exception {
        when(deviceType.getName()).thenReturn(DeviceType.IOS);
        when(user.isSubscribedStatus()).thenReturn(true);
        when(user.isOnFreeTrial()).thenReturn(false);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(PaymentDetails.ITUNES_SUBSCRIPTION);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(true);
        when(iTunesPaymentService.hasOneTimeSubscription(user)).thenReturn(true);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, false, false, false, true);

        assertTrue(accountCheckDTO.oneTimePayment);
    }

    @Test
    public void iosSubscribedOnRecurrent() throws Exception {
        when(deviceType.getName()).thenReturn(DeviceType.IOS);
        when(user.isSubscribedStatus()).thenReturn(true);
        when(user.isOnFreeTrial()).thenReturn(false);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(PaymentDetails.ITUNES_SUBSCRIPTION);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(true);
        when(iTunesPaymentService.hasOneTimeSubscription(user)).thenReturn(false);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, false, false, false, true);

        assertFalse(accountCheckDTO.oneTimePayment);
    }

    @Test
    public void androidSubscribedOnOneTimeActivatedPaymentDetails() throws Exception {
        when(deviceType.getName()).thenReturn(DeviceType.ANDROID);
        when(user.isSubscribedStatus()).thenReturn(true);
        when(user.isOnFreeTrial()).thenReturn(false);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(PaymentDetails.PAYPAL_TYPE);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(true);
        when(user.getCurrentPaymentDetails()).thenReturn(paymentDetails);
        when(user.hasOneTimeSubscription()).thenReturn(true);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, false, false, false, true);

        assertTrue(accountCheckDTO.oneTimePayment);
    }

    @Test
    public void androidSubscribedOnRecurrentActivatedPaymentDetails() throws Exception {
        when(deviceType.getName()).thenReturn(DeviceType.ANDROID);
        when(user.isSubscribedStatus()).thenReturn(true);
        when(user.isOnFreeTrial()).thenReturn(false);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(PaymentDetails.PAYPAL_TYPE);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(true);
        when(user.getCurrentPaymentDetails()).thenReturn(paymentDetails);
        when(user.hasOneTimeSubscription()).thenReturn(false);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, false, false, false, true);

        assertFalse(accountCheckDTO.oneTimePayment);
    }

    @Test
    public void androidSubscribedOnOneTimeDeactivatedPaymentDetails() throws Exception {
        when(deviceType.getName()).thenReturn(DeviceType.ANDROID);
        when(user.isSubscribedStatus()).thenReturn(true);
        when(user.isOnFreeTrial()).thenReturn(false);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(PaymentDetails.PAYPAL_TYPE);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(true);
        when(user.getCurrentPaymentDetails()).thenReturn(paymentDetails);
        when(user.hasOneTimeSubscription()).thenReturn(true);
        when(user.hasActivePaymentDetails()).thenReturn(false);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, false, false, false, true);

        assertTrue(accountCheckDTO.oneTimePayment);
    }

    @Test
    public void androidSubscribedOnRecurrentDeactivatedPaymentDetails() throws Exception {
        when(deviceType.getName()).thenReturn(DeviceType.ANDROID);
        when(user.isSubscribedStatus()).thenReturn(true);
        when(user.isOnFreeTrial()).thenReturn(false);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(PaymentDetails.PAYPAL_TYPE);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(true);
        when(user.getCurrentPaymentDetails()).thenReturn(paymentDetails);
        when(user.hasOneTimeSubscription()).thenReturn(true);
        when(user.hasActivePaymentDetails()).thenReturn(false);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, false, false, false, true);

        assertTrue(accountCheckDTO.oneTimePayment);
    }

    @Test
    public void iosExpiredOneTime() throws Exception {
        when(deviceType.getName()).thenReturn(DeviceType.IOS);
        when(user.isSubscribedStatus()).thenReturn(false);
        when(user.isOnFreeTrial()).thenReturn(false);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(PaymentDetails.ITUNES_SUBSCRIPTION);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(false);
        when(iTunesPaymentService.hasOneTimeSubscription(user)).thenReturn(true);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, false, false, false, true);

        assertNull(accountCheckDTO.oneTimePayment);
    }

    @Test
    public void iosExpiredRecurrent() throws Exception {
        when(deviceType.getName()).thenReturn(DeviceType.IOS);
        when(user.isSubscribedStatus()).thenReturn(false);
        when(user.isOnFreeTrial()).thenReturn(false);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(PaymentDetails.ITUNES_SUBSCRIPTION);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(false);
        when(iTunesPaymentService.hasOneTimeSubscription(user)).thenReturn(false);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, false, false, false, true);

        assertNull(accountCheckDTO.oneTimePayment);
    }

    @Test
    public void androidExpiredOneTime() throws Exception {
        when(deviceType.getName()).thenReturn(DeviceType.ANDROID);
        when(user.isSubscribedStatus()).thenReturn(false);
        when(user.isOnFreeTrial()).thenReturn(false);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(PaymentDetails.PAYPAL_TYPE);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(false);
        when(user.getCurrentPaymentDetails()).thenReturn(paymentDetails);
        when(user.hasOneTimeSubscription()).thenReturn(true);
        when(user.hasActivePaymentDetails()).thenReturn(false);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, false, false, false, true);

        assertNull(accountCheckDTO.oneTimePayment);
    }

    @Test
    public void androidExpiredRecurrent() throws Exception {
        when(deviceType.getName()).thenReturn(DeviceType.ANDROID);
        when(user.isSubscribedStatus()).thenReturn(false);
        when(user.isOnFreeTrial()).thenReturn(false);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(PaymentDetails.PAYPAL_TYPE);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(false);
        when(user.getCurrentPaymentDetails()).thenReturn(paymentDetails);
        when(user.hasOneTimeSubscription()).thenReturn(false);
        when(user.hasActivePaymentDetails()).thenReturn(false);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, false, false, false, true);

        assertNull(accountCheckDTO.oneTimePayment);
    }

}
