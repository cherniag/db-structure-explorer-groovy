package mobi.nowtechnologies.server.web.asm;

import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.dto.payment.PaymentPolicyDto;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.service.itunes.payment.ITunesPaymentService;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import mobi.nowtechnologies.server.web.controller.SubscriptionInfo;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.MONTHS;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;
import org.apache.commons.lang.time.DateUtils;

import org.springframework.context.MessageSource;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionInfoAsmTest {

    final int paypalId = 1;
    final int iosId = 2;
    final int notPaypalId = 3;

    Date now = new Date();
    @Mock
    TimeService timeService;
    @Mock
    MessageSource messageSource;
    @Mock
    ITunesPaymentService iTunesPaymentService;
    @InjectMocks
    SubscriptionInfoAsm subscriptionInfoAsm;
    //
    // Variables
    //
    Locale locale = Locale.CANADA;
    Period period = new Period(WEEKS, 3);
    @Mock
    private PaymentPolicy policy;

    @Before
    public void setUp() throws Exception {
        when(timeService.now()).thenReturn(now);
        when(policy.getPeriod()).thenReturn(period);
    }

    @Test
    public void testCreateSubscriptionInfoForIosAndPremium() throws Exception {
        // given
        PaymentPolicyDto paymentPolicy = createIos();
        when(paymentPolicy.getSubcost()).thenReturn(new BigDecimal("2"));
        when(paymentPolicy.getDuration()).thenReturn(1);
        when(paymentPolicy.getDurationUnit()).thenReturn(DurationUnit.DAYS);

        List<PaymentPolicyDto> policies = Lists.newArrayList(paymentPolicy, createPaypal(), createNotPaypal());

        User user = makePremiumIosUser();
        when(iTunesPaymentService.getCurrentSubscribedPaymentPolicy(user)).thenReturn(policy);
        // when
        SubscriptionInfo subscriptionInfo = subscriptionInfoAsm.createSubscriptionInfo(user, policies);

        // then
        assertEquals(iosId, subscriptionInfo.getPaymentPolicyDTOs().get(0).getId().intValue());
        assertTrue(subscriptionInfo.isIos());
        assertTrue(subscriptionInfo.isPremium());
        assertEquals(3, subscriptionInfo.getCurrentPaymentPolicy().getDuration());
        assertEquals(WEEKS, subscriptionInfo.getCurrentPaymentPolicy().getDurationUnit());
    }

    @Test
    public void testCreateSubscriptionInfoForIosAndNotPremium() throws Exception {
        // given
        PaymentPolicyDto paymentPolicy = createIos();
        when(paymentPolicy.getSubcost()).thenReturn(new BigDecimal("3"));
        when(paymentPolicy.getDuration()).thenReturn(33);
        when(paymentPolicy.getDurationUnit()).thenReturn(DurationUnit.DAYS);

        List<PaymentPolicyDto> policies = Lists.newArrayList(paymentPolicy, createPaypal(), createNotPaypal());

        // when
        User user = makeNotPremiumIosUser();
        SubscriptionInfo subscriptionInfo = subscriptionInfoAsm.createSubscriptionInfo(user, policies);

        // then
        assertEquals(iosId, subscriptionInfo.getPaymentPolicyDTOs().get(0).getId().intValue());
        assertEquals(true, subscriptionInfo.isIos());
        assertNull(subscriptionInfo.getCurrentPaymentPolicy());
        assertEquals(false, subscriptionInfo.isPremium());
    }

    @Test
    public void testCreateSubscriptionInfoForNotIosAndPremium() throws Exception {
        // given
        PaymentPolicyDto paymentPolicy = createPaypal();
        when(paymentPolicy.getSubcost()).thenReturn(new BigDecimal("3"));
        when(paymentPolicy.getDuration()).thenReturn(33);
        when(paymentPolicy.getDurationUnit()).thenReturn(WEEKS);

        List<PaymentPolicyDto> policies = Lists.newArrayList(paymentPolicy, createPaypal(), createNotPaypal());

        // when
        User user = makePremiumAndroidUser();
        SubscriptionInfo subscriptionInfo = subscriptionInfoAsm.createSubscriptionInfo(user, policies);

        // then
        assertEquals(paypalId, subscriptionInfo.getPaymentPolicyDTOs().get(0).getId().intValue());
        assertEquals(false, subscriptionInfo.isIos());
        assertEquals(true, subscriptionInfo.isPremium());
        assertEquals(3, subscriptionInfo.getCurrentPaymentPolicy().getDuration());
        assertEquals(WEEKS, subscriptionInfo.getCurrentPaymentPolicy().getDurationUnit());
    }

    @Test
    public void testCreateSubscriptionInfoForNotIosAndNotPremium() throws Exception {
        // given
        PaymentPolicyDto paymentPolicy = createPaypal();
        when(paymentPolicy.getSubcost()).thenReturn(new BigDecimal("3"));
        when(paymentPolicy.getDuration()).thenReturn(33);
        when(paymentPolicy.getDurationUnit()).thenReturn(DurationUnit.MONTHS);

        List<PaymentPolicyDto> policies = Lists.newArrayList(paymentPolicy, createPaypal(), createNotPaypal());

        // when
        User user = makeNotPremiumAndroidUser();
        SubscriptionInfo subscriptionInfo = subscriptionInfoAsm.createSubscriptionInfo(user, policies);

        // then
        assertEquals(paypalId, subscriptionInfo.getPaymentPolicyDTOs().get(0).getId().intValue());
        assertEquals(false, subscriptionInfo.isIos());
        assertEquals(false, subscriptionInfo.isPremium());
    }

    @Test
    public void testCreateSubscriptionInfoOrder() throws Exception {
        // given
        PaymentPolicyDto paymentPolicy1 = createPaypal();
        when(paymentPolicy1.getSubcost()).thenReturn(new BigDecimal("4"));
        when(paymentPolicy1.getDuration()).thenReturn(1);
        when(paymentPolicy1.getDurationUnit()).thenReturn(DurationUnit.MONTHS);

        PaymentPolicyDto paymentPolicy2 = createPaypal();
        when(paymentPolicy2.getSubcost()).thenReturn(new BigDecimal("1"));
        when(paymentPolicy2.getDuration()).thenReturn(1);
        when(paymentPolicy2.getDurationUnit()).thenReturn(DurationUnit.WEEKS);

        PaymentPolicyDto paymentPolicy3 = createPaypal();
        when(paymentPolicy3.getSubcost()).thenReturn(new BigDecimal("10"));
        when(paymentPolicy3.getDuration()).thenReturn(6);
        when(paymentPolicy3.getDurationUnit()).thenReturn(DurationUnit.MONTHS);

        List<PaymentPolicyDto> policies = Lists.newArrayList(paymentPolicy1, paymentPolicy2, paymentPolicy3);

        // when
        User user = makeNotPremiumAndroidUser();
        SubscriptionInfo subscriptionInfo = subscriptionInfoAsm.createSubscriptionInfo(user, policies);

        // then
        assertEquals(paymentPolicy2, subscriptionInfo.getPaymentPolicyDTOs().get(0));
        assertEquals(paymentPolicy1, subscriptionInfo.getPaymentPolicyDTOs().get(1));
        assertEquals(paymentPolicy3, subscriptionInfo.getPaymentPolicyDTOs().get(2));
    }

    //
    // internals
    //
    User makeNotPremiumAndroidUser() {
        User user = getAndroidUser();

        PaymentDetails details = mock(PaymentDetails.class);
        when(details.isActivated()).thenReturn(false);
        when(user.getCurrentPaymentDetails()).thenReturn(details);

        return user;
    }

    User makePremiumAndroidUser() {
        User user = getAndroidUser();

        PaymentDetails details = mock(PaymentDetails.class);
        when(details.getPaymentPolicy()).thenReturn(policy);
        when(details.isActivated()).thenReturn(true);
        when(user.getCurrentPaymentDetails()).thenReturn(details);
        when(user.hasActivePaymentDetails()).thenReturn(true);
        when(user.isPremium(now)).thenReturn(true);

        return user;
    }

    User makePremiumIosUser() {
        User user = getIosUser();

        when(user.getNextSubPaymentAsDate()).thenReturn(DateUtils.addDays(now, 1));
        when(user.getCurrentPaymentDetails()).thenReturn(null);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(PaymentDetails.ITUNES_SUBSCRIPTION);
        when(user.isSubscribedStatus()).thenReturn(true);
        when(user.isPremium(now)).thenReturn(true);

        return user;
    }

    User makeNotPremiumIosUser() {
        User user = getIosUser();

        when(user.getNextSubPaymentAsDate()).thenReturn(DateUtils.addDays(now, -1));
        when(user.getCurrentPaymentDetails()).thenReturn(null);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(PaymentDetails.ITUNES_SUBSCRIPTION);
        when(user.isSubscribedStatus()).thenReturn(false);

        return user;
    }

    PaymentPolicyDto createIos() {
        PaymentPolicyDto ios = mock(PaymentPolicyDto.class);
        when(ios.getId()).thenReturn(iosId);
        when(ios.getPaymentType()).thenReturn(PaymentDetails.ITUNES_SUBSCRIPTION);
        return ios;
    }

    PaymentPolicyDto createPaypal() {
        PaymentPolicyDto ios = mock(PaymentPolicyDto.class);
        when(ios.getId()).thenReturn(paypalId);
        when(ios.getPaymentType()).thenReturn("PAY_PAL");
        when(ios.getDuration()).thenReturn(1);
        when(ios.getDurationUnit()).thenReturn(WEEKS);
        return ios;
    }

    PaymentPolicyDto createNotPaypal() {
        PaymentPolicyDto ios = mock(PaymentPolicyDto.class);
        when(ios.getId()).thenReturn(notPaypalId);
        when(ios.getPaymentType()).thenReturn("NOT_PAY_PAL");
        when(ios.getDuration()).thenReturn(1);
        when(ios.getDurationUnit()).thenReturn(MONTHS);
        return ios;
    }

    private User getIosUser() {
        DeviceType ios = mock(DeviceType.class);
        when(ios.getName()).thenReturn(DeviceType.IOS);

        User user = mock(User.class);
        when(user.getDeviceType()).thenReturn(ios);
        return user;
    }

    private User getAndroidUser() {
        DeviceType android = mock(DeviceType.class);
        when(android.getName()).thenReturn(DeviceType.ANDROID);

        User user = mock(User.class);
        when(user.getDeviceType()).thenReturn(android);
        return user;
    }
}