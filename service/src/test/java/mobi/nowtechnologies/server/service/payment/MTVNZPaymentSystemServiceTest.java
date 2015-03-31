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
import mobi.nowtechnologies.server.service.nz.MsisdnNotFoundException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import mobi.nowtechnologies.server.service.nz.ProviderNotAvailableException;
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
public class MTVNZPaymentSystemServiceTest {
    private static final String USER_DOES_NOT_BELONG_TO_VF = "User does not belong to VF";
    private static final String VF_DOES_NOT_KNOW_THIS_USER = "VF doesn't know this user";
    @Mock
    private NZSubscriberInfoService nzSubscriberInfoService;
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
    private MTVNZPaymentSystemService mtvnzPaymentSystemService;

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
    private final String normalizedPhoneNumber = phoneNumber.replaceFirst("\\+", "");

    @Before
    public void setUp() throws Exception {
        mtvnzPaymentSystemService.setExpireMillis(expireMillis);
        when(user.getCommunityRewriteUrl()).thenReturn(communityUrl);

        when(pendingPayment.getPeriod()).thenReturn(period);
        when(pendingPayment.getPaymentDetails()).thenReturn(paymentDetails);
        when(pendingPayment.getUser()).thenReturn(user);
        when(pendingPayment.getAmount()).thenReturn(amount);

        when(paymentDetails.getPhoneNumber()).thenReturn(phoneNumber);
        when(paymentDetails.getPaymentPolicy()).thenReturn(paymentPolicy);

        when(paymentPolicy.getShortCode()).thenReturn(shortCode);
        when(smsGatewayService.send(normalizedPhoneNumber, smsText, shortCode, SUCCESS_FAILURE, expireMillis)).thenReturn(smsResponse);
        when(messageSource.getMessage(communityUrl, "sms.mtvnzPsms.payment.text.1234.WEEKS", args, null)).thenReturn(smsText);

        when(entityService.updateEntity(any())).thenAnswer(getAnswer());
    }

    @Test
    public void startMTVNZPaymentSuccess() throws Exception {
        final boolean userIsVFSubscriber = true;
        final boolean smsWasSent = true;
        when(nzSubscriberInfoService.belongs(normalizedPhoneNumber)).thenReturn(userIsVFSubscriber);
        when(smsResponse.isSuccessful()).thenReturn(smsWasSent);

        mtvnzPaymentSystemService.startPayment(pendingPayment);

        verify(nzSubscriberInfoService).belongs(normalizedPhoneNumber);
        verifySMSWasSentToGateway();
        verifyUserWasNotUnSubscribedBecauseOfProvider();
    }

    @Test
    public void startMTVNZPaymentWhenUserIsNotSubscriber() throws Exception {
        final boolean userIsVFSubscriber = false;
        when(nzSubscriberInfoService.belongs(normalizedPhoneNumber)).thenReturn(userIsVFSubscriber);

        mtvnzPaymentSystemService.startPayment(pendingPayment);

        verify(nzSubscriberInfoService).belongs(normalizedPhoneNumber);
        verifyUserWasUnSubscribedBecauseOfProvider();
        verifySMSWasNotSentToGateway();
    }

    @Test
    public void startMTVNZPaymentWhenNZSubscriberServiceNotAvailable() throws Exception {
        when(nzSubscriberInfoService.belongs(normalizedPhoneNumber)).thenThrow(new ProviderNotAvailableException("cause", new Exception()));

        mtvnzPaymentSystemService.startPayment(pendingPayment);

        verify(nzSubscriberInfoService).belongs(normalizedPhoneNumber);
        verifyUserWasNotUnSubscribedBecauseOfProvider();
        verifySMSWasNotSentToGateway();
        verifyErrorResponseWithoutIncrementWasCommitted();
    }

    @Test
    public void should() throws Exception {
        //given
        when(nzSubscriberInfoService.belongs(normalizedPhoneNumber)).thenThrow(new MsisdnNotFoundException("cause", new Exception()));

        //when
        mtvnzPaymentSystemService.startPayment(pendingPayment);

        //then
        verify(nzSubscriberInfoService).belongs(normalizedPhoneNumber);
        verifyUserWasUnSubscribedBecauseOfUnknownMSISDN();
        verifySMSWasNotSentToGateway();
    }

    @Test
    public void startMTVNZPaymentWhenSMSWasNotSent() throws Exception {
        final boolean userIsVFSubscriber = true;
        final boolean smsWasSent = false;
        when(nzSubscriberInfoService.belongs(normalizedPhoneNumber)).thenReturn(userIsVFSubscriber);
        when(smsResponse.isSuccessful()).thenReturn(smsWasSent);

        mtvnzPaymentSystemService.startPayment(pendingPayment);

        verify(nzSubscriberInfoService).belongs(normalizedPhoneNumber);
        verifyUserWasNotUnSubscribedBecauseOfProvider();
        verifySMSWasSentToGateway();
        verifyErrorResponseWithoutIncrementWasCommitted();
    }

    private void verifySMSWasSentToGateway() {
        verify(messageSource).getMessage(communityUrl, "sms.mtvnzPsms.payment.text.1234.WEEKS", args, null);
        verify(smsGatewayService).send(normalizedPhoneNumber, smsText, shortCode, SUCCESS_FAILURE, expireMillis);
    }

    private void verifyUserWasUnSubscribedBecauseOfProvider() {
        verify(paymentDetailsRepository).save(paymentDetails);
        verify(paymentDetails).completedWithError(USER_DOES_NOT_BELONG_TO_VF);
        verify(userService).unsubscribeUser(user, USER_DOES_NOT_BELONG_TO_VF);
        verify(pendingPaymentRepository).delete(pendingPayment);
        verify(paymentEventNotifier).onUnsubscribe(user);
    }

    private void verifyErrorResponseWithoutIncrementWasCommitted() {
        verify(entityService, atLeastOnce()).updateEntity(any(SubmittedPayment.class));
        verify(paymentDetails).setLastPaymentStatus(PaymentDetailsStatus.ERROR);
        verify(paymentDetails, never()).incrementMadeAttemptsAccordingToMadeRetries();
        verify(entityService, atLeastOnce()).updateEntity(paymentDetails);
        verify(paymentEventNotifier).onError(paymentDetails);
        verify(paymentDetails).shouldBeUnSubscribed();
        verify(entityService).removeEntity(PendingPayment.class, pendingPayment.getI());
    }

    private void verifySMSWasNotSentToGateway() {
        verify(messageSource, never()).getMessage(communityUrl, "sms.mtvnzPsms.payment.text.1234.WEEKS", args, null);
        verify(smsGatewayService, never()).send(normalizedPhoneNumber, smsText, shortCode, SUCCESS_FAILURE, expireMillis);
    }

    private void verifyUserWasNotUnSubscribedBecauseOfProvider() {
        verify(pendingPaymentRepository, never()).delete(pendingPayment);
        verify(paymentDetails, never()).completedWithError(USER_DOES_NOT_BELONG_TO_VF);
        verify(userService, never()).unsubscribeUser(user, USER_DOES_NOT_BELONG_TO_VF);
        verify(paymentEventNotifier, never()).onUnsubscribe(user);
    }

    private void verifyUserWasUnSubscribedBecauseOfUnknownMSISDN() {
        verify(pendingPaymentRepository).delete(pendingPayment);
        verify(paymentDetails).completedWithError(VF_DOES_NOT_KNOW_THIS_USER);
        verify(paymentDetailsRepository).save(paymentDetails);
        verify(userService).unsubscribeUser(user, VF_DOES_NOT_KNOW_THIS_USER);
        verify(paymentEventNotifier).onUnsubscribe(user);
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