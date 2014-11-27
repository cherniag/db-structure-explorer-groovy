package mobi.nowtechnologies.server.web.asm;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import mobi.nowtechnologies.server.web.controller.SubscriptionInfo;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SubscriptionInfoAsmTest {
    final int paypalId = 1;
    final int iosId = 2;
    final int notPaypalId = 3;

    Date now = new Date();
    @Captor
    ArgumentCaptor<Object[]> argumentsCaptor;
    @Mock
    TimeService timeService;
    @Mock
    MessageSource messageSource;
    @InjectMocks
    SubscriptionInfoAsm subscriptionInfoAsm;

    //
    // Variables
    //
    Locale locale = Locale.CANADA;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(timeService.now()).thenReturn(now);
    }

    @Test
    public void testCreateSubscriptionInfoForIosAndPremium() throws Exception {
        // given
        PaymentPolicyDto paymentPolicy = createIos();
        when(paymentPolicy.getSubcost()).thenReturn(new BigDecimal("2"));
        when(paymentPolicy.getDuration()).thenReturn(1);
        when(paymentPolicy.getDurationUnit()).thenReturn(DurationUnit.DAYS);

        List<PaymentPolicyDto> policies = Lists.newArrayList(paymentPolicy, createPaypal(), createNotPaypal());
        when(messageSource.getMessage(eq("payment.per.day"), argumentsCaptor.capture(), eq(""), eq(locale))).thenReturn("message");

        // when
        User user = makePremiumIosUser();
        SubscriptionInfo subscriptionInfo = subscriptionInfoAsm.createSubscriptionInfo(locale, user, policies);

        // then
        assertEquals(iosId, subscriptionInfo.getPaymentPolicyDto().getId().intValue());
        assertEquals(true, subscriptionInfo.isIos());
        assertEquals(true, subscriptionInfo.isPremium());
        assertEquals(1, argumentsCaptor.getValue().length);
        assertEquals(new BigDecimal("2"), argumentsCaptor.getValue()[0]);
    }

    @Test
    public void testCreateSubscriptionInfoForIosAndNotPremium() throws Exception {
        // given
        PaymentPolicyDto paymentPolicy = createIos();
        when(paymentPolicy.getSubcost()).thenReturn(new BigDecimal("3"));
        when(paymentPolicy.getDuration()).thenReturn(33);
        when(paymentPolicy.getDurationUnit()).thenReturn(DurationUnit.DAYS);

        List<PaymentPolicyDto> policies = Lists.newArrayList(paymentPolicy, createPaypal(), createNotPaypal());
        when(messageSource.getMessage(eq("payment.for.n.days"), argumentsCaptor.capture(), eq(""), eq(locale))).thenReturn("message");

        // when
        User user = makeNotPremiumIosUser();
        SubscriptionInfo subscriptionInfo = subscriptionInfoAsm.createSubscriptionInfo(locale, user, policies);

        // then
        assertEquals(iosId, subscriptionInfo.getPaymentPolicyDto().getId().intValue());
        assertEquals(true, subscriptionInfo.isIos());
        assertEquals(false, subscriptionInfo.isPremium());
        assertEquals(2, argumentsCaptor.getValue().length);
        assertEquals(new BigDecimal("3"), argumentsCaptor.getValue()[0]);
        assertEquals(33, argumentsCaptor.getValue()[1]);
    }

    @Test
    public void testCreateSubscriptionInfoForNotIosAndPremium() throws Exception {
        // given
        PaymentPolicyDto paymentPolicy = createPaypal();
        when(paymentPolicy.getSubcost()).thenReturn(new BigDecimal("3"));
        when(paymentPolicy.getDuration()).thenReturn(33);
        when(paymentPolicy.getDurationUnit()).thenReturn(DurationUnit.WEEKS);

        List<PaymentPolicyDto> policies = Lists.newArrayList(paymentPolicy, createPaypal(), createNotPaypal());
        when(messageSource.getMessage(eq("payment.for.n.weeks"), argumentsCaptor.capture(), eq(""), eq(locale))).thenReturn("message");

        // when
        User user = makePremiumAndroidUser();
        SubscriptionInfo subscriptionInfo = subscriptionInfoAsm.createSubscriptionInfo(locale, user, policies);

        // then
        assertEquals(paypalId, subscriptionInfo.getPaymentPolicyDto().getId().intValue());
        assertEquals(false, subscriptionInfo.isIos());
        assertEquals(true, subscriptionInfo.isPremium());
        assertEquals(2, argumentsCaptor.getValue().length);
        assertEquals(new BigDecimal("3"), argumentsCaptor.getValue()[0]);
        assertEquals(33, argumentsCaptor.getValue()[1]);
    }

    @Test
    public void testCreateSubscriptionInfoForNotIosAndNotPremium() throws Exception {
        // given
        PaymentPolicyDto paymentPolicy = createPaypal();
        when(paymentPolicy.getSubcost()).thenReturn(new BigDecimal("3"));
        when(paymentPolicy.getDuration()).thenReturn(33);
        when(paymentPolicy.getDurationUnit()).thenReturn(DurationUnit.MONTHS);

        List<PaymentPolicyDto> policies = Lists.newArrayList(paymentPolicy, createPaypal(), createNotPaypal());
        when(messageSource.getMessage(eq("payment.for.n.months"), argumentsCaptor.capture(), eq(""), eq(locale))).thenReturn("message");

        // when
        User user = makeNotPremiumAndroidUser();
        SubscriptionInfo subscriptionInfo = subscriptionInfoAsm.createSubscriptionInfo(locale, user, policies);

        // then
        assertEquals(paypalId, subscriptionInfo.getPaymentPolicyDto().getId().intValue());
        assertEquals(false, subscriptionInfo.isIos());
        assertEquals(false, subscriptionInfo.isPremium());
        assertEquals(2, argumentsCaptor.getValue().length);
        assertEquals(new BigDecimal("3"), argumentsCaptor.getValue()[0]);
        assertEquals(33, argumentsCaptor.getValue()[1]);
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
        when(details.isActivated()).thenReturn(true);
        when(user.getCurrentPaymentDetails()).thenReturn(details);

        return user;
    }

    User makePremiumIosUser() {
        User user = getIosUser();

        when(user.getNextSubPaymentAsDate()).thenReturn(DateUtils.addDays(now, 1));
        when(user.getCurrentPaymentDetails()).thenReturn(null);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(PaymentDetails.ITUNES_SUBSCRIPTION);

        return user;
    }

    User makeNotPremiumIosUser() {
        User user = getIosUser();

        when(user.getNextSubPaymentAsDate()).thenReturn(DateUtils.addDays(now, -1));
        when(user.getCurrentPaymentDetails()).thenReturn(null);
        when(user.getLastSubscribedPaymentSystem()).thenReturn(PaymentDetails.ITUNES_SUBSCRIPTION);

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
        return ios;
    }

    PaymentPolicyDto createNotPaypal() {
        PaymentPolicyDto ios = mock(PaymentPolicyDto.class);
        when(ios.getId()).thenReturn(notPaypalId);
        when(ios.getPaymentType()).thenReturn("NOT_PAY_PAL");
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