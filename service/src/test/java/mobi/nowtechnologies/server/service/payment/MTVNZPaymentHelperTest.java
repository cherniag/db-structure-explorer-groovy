package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.service.EntityService;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.sms.SMSResponse;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import java.math.BigDecimal;

import static org.jsmpp.bean.SMSCDeliveryReceipt.SUCCESS_FAILURE;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.runners.*;
import org.mockito.stubbing.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MTVNZPaymentHelperTest {
    private static final String USER_DOES_NOT_BELONG_TO_VF = "User does not belong to VF";
    @Mock
    private VFNZSMSGatewayServiceImpl smsGatewayService;
    @Mock
    private CommunityResourceBundleMessageSource messageSource;
    @Mock
    private PendingPaymentRepository pendingPaymentRepository;
    @Mock
    private PaymentDetailsRepository paymentDetailsRepository;
    @Mock
    private PaymentDetailsService paymentDetailsService;
    @Mock
    private UserService userService;
    @Mock
    private PaymentEventNotifier paymentEventNotifier;
    @Mock
    private EntityService entityService;
    @InjectMocks
    private MTVNZPaymentHelper mtvnzPaymentHelper;

    @Mock
    private PendingPayment pendingPayment;
    @Mock
    private PSMSPaymentDetails paymentDetails;
    @Mock
    private PaymentPolicy paymentPolicy;
    @Mock
    private User user;
    @Mock
    private SMSResponse smsResponse;

    // business fields
    private String communityUrl = "mtvnz";
    private Period period = new Period(DurationUnit.WEEKS, 2);
    private long expireMillis = 100L;
    private String phoneNumber = "+64123456789";
    private String shortCode = "1234";
    private BigDecimal amount = BigDecimal.TEN;
    private String smsText = "SMS text message";
    private Object[] args = new Object[]{amount.toString(), period.getDuration()};

    @Before
    public void setUp() throws Exception {
        mtvnzPaymentHelper.setExpireMillis(expireMillis);
        when(user.getCommunityRewriteUrl()).thenReturn(communityUrl);

        when(pendingPayment.getPeriod()).thenReturn(period);
        when(pendingPayment.getPaymentDetails()).thenReturn(paymentDetails);
        when(pendingPayment.getUser()).thenReturn(user);
        when(pendingPayment.getAmount()).thenReturn(amount);

        when(paymentDetails.getPhoneNumber()).thenReturn(phoneNumber);
        when(paymentDetails.getPaymentPolicy()).thenReturn(paymentPolicy);

        when(paymentPolicy.getShortCode()).thenReturn(shortCode);
        when(smsGatewayService.send(phoneNumber, smsText, shortCode, SUCCESS_FAILURE, expireMillis)).thenReturn(smsResponse);
        when(messageSource.getMessage(communityUrl, "sms.mtvnzPsms.payment.text.1234.WEEKS", args, null)).thenReturn(smsText);

        when(entityService.updateEntity(any())).thenAnswer(getAnswer());
    }

    @Test
    public void startPaymentSuccess() throws Exception {
        final boolean smsWasSent = true;
        when(smsResponse.isSuccessful()).thenReturn(smsWasSent);

        mtvnzPaymentHelper.startPayment(pendingPayment);

        verify(messageSource).getMessage(communityUrl, "sms.mtvnzPsms.payment.text.1234.WEEKS", args, null);
        verify(smsGatewayService).send(phoneNumber, smsText, shortCode, SUCCESS_FAILURE, expireMillis);

        verify(pendingPaymentRepository, never()).delete(pendingPayment);
        verify(paymentDetails, never()).completedWithError(anyString());
        verify(userService, never()).unsubscribeUser(eq(user), anyString());
        verify(paymentEventNotifier, never()).onUnsubscribe(user);
    }

    @Test
    public void startPaymentWhenSMSWasNotSent() throws Exception {
        final boolean smsWasSent = false;
        when(smsResponse.isSuccessful()).thenReturn(smsWasSent);

        mtvnzPaymentHelper.startPayment(pendingPayment);

        verify(pendingPaymentRepository, never()).delete(pendingPayment);
        verify(paymentDetails, never()).completedWithError(anyString());
        verify(userService, never()).unsubscribeUser(eq(user), anyString());
        verify(paymentEventNotifier, never()).onUnsubscribe(user);

        verify(messageSource).getMessage(communityUrl, "sms.mtvnzPsms.payment.text.1234.WEEKS", args, null);
        verify(smsGatewayService).send(phoneNumber, smsText, shortCode, SUCCESS_FAILURE, expireMillis);

        verify(entityService, atLeastOnce()).updateEntity(any(SubmittedPayment.class));
        verify(paymentDetails).setLastPaymentStatus(PaymentDetailsStatus.ERROR);
        verify(paymentDetails, never()).incrementMadeAttemptsAccordingToMadeRetries();
        verify(entityService, atLeastOnce()).updateEntity(paymentDetails);
        verify(paymentEventNotifier).onError(paymentDetails);
        verify(paymentDetails).shouldBeUnSubscribed();
        verify(entityService).removeEntity(PendingPayment.class, pendingPayment.getI());
    }

    @Test
    public void finishPaymentForNotVFUser() throws Exception {
        mtvnzPaymentHelper.finishPaymentForNotVFUser(pendingPayment, USER_DOES_NOT_BELONG_TO_VF);

        verify(paymentDetailsRepository).save(paymentDetails);
        verify(paymentDetails).completedWithError(USER_DOES_NOT_BELONG_TO_VF);
        verify(userService).unsubscribeUser(user, USER_DOES_NOT_BELONG_TO_VF);
        verify(pendingPaymentRepository).delete(pendingPayment);
        verify(paymentEventNotifier).onUnsubscribe(user);
    }

    @Test
    public void skipAttemptWithoutRetryIncrement() throws Exception {
        mtvnzPaymentHelper.skipAttemptWithoutRetryIncrement(pendingPayment, "Connection timeout");

        verify(pendingPaymentRepository, never()).delete(pendingPayment);
        verify(paymentDetails, never()).completedWithError(anyString());
        verify(userService, never()).unsubscribeUser(eq(user), anyString());
        verify(paymentEventNotifier, never()).onUnsubscribe(user);

        verify(entityService, atLeastOnce()).updateEntity(any(SubmittedPayment.class));
        verify(paymentDetails).setLastPaymentStatus(PaymentDetailsStatus.ERROR);
        verify(paymentDetails, never()).incrementMadeAttemptsAccordingToMadeRetries();
        verify(entityService, atLeastOnce()).updateEntity(paymentDetails);
        verify(paymentEventNotifier).onError(paymentDetails);
        verify(paymentDetails).shouldBeUnSubscribed();
        verify(entityService).removeEntity(PendingPayment.class, pendingPayment.getI());
    }

    private Answer<?> getAnswer(){
        return new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        };
    }
}