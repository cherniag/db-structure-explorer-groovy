package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.service.ITunesPaymentDetailsService;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.itunes.impl.ITunesResult;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import org.springframework.context.ApplicationEventPublisher;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 * Author: Gennadii Cherniaiev Date: 4/16/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class ITunesPaymentSystemServiceHelperTest {
    @Mock
    ITunesPaymentDetailsService iTunesPaymentDetailsService;
    @Mock
    PaymentPolicyService paymentPolicyService;
    @Mock
    PaymentDetailsRepository paymentDetailsRepository;
    @Mock
    TimeService timeService;
    @Mock
    ApplicationEventPublisher applicationEventPublisher;
    @Mock
    SubmittedPaymentService submittedPaymentService;
    @Mock
    PendingPaymentRepository pendingPaymentRepository;
    @Mock
    UserService userService;
    @InjectMocks
    ITunesPaymentSystemServiceHelper helper;

    @Mock
    User user;
    @Mock
    ITunesPaymentDetails paymentDetails;
    @Mock
    PaymentPolicy paymentPolicy;
    @Mock
    PendingPayment pendingPayment;
    @Mock
    ITunesResult iTunesResult;
    @Mock
    Community community;
    @Captor
    ArgumentCaptor<SubmittedPayment> submittedPaymentArgumentCaptor;
    @Captor
    ArgumentCaptor<PaymentEvent> paymentEventArgumentCaptor;

    private Date now = new Date();
    private Date expire = DateUtils.addDays(new Date(), 2);
    private String actualProductId = "com.mq.actual";
    private String originalTransactionId = "0000000000";
    private BigDecimal amount = BigDecimal.TEN;
    private String currency = "USD";
    private String actualReceipt = "ACTUAL_RECEIPT";

    @Before
    public void setUp() throws Exception {
        when(pendingPayment.getUser()).thenReturn(user);
        when(user.getCurrentPaymentDetails()).thenReturn(paymentDetails);
        when(user.getCommunity()).thenReturn(community);
        when(paymentDetails.getPaymentPolicy()).thenReturn(paymentPolicy);
        when(paymentDetails.getAppStroreReceipt()).thenReturn(actualReceipt);
        when(iTunesResult.getExpireTime()).thenReturn(expire.getTime());
        when(iTunesResult.getProductId()).thenReturn(actualProductId);
        when(iTunesResult.getOriginalTransactionId()).thenReturn(originalTransactionId);
        when(paymentPolicy.getCurrencyISO()).thenReturn(currency);
        when(paymentPolicy.getSubcost()).thenReturn(amount);
        when(paymentPolicy.getAppStoreProductId()).thenReturn(actualProductId);
        when(timeService.now()).thenReturn(now);
    }

    @Test
    public void confirmPaymentWithTheSameProductIdAsStoredPaymentPolicy() throws Exception {
        when(paymentPolicyService.findByCommunityAndAppStoreProductId(community, actualProductId)).thenReturn(paymentPolicy);

        helper.confirmPayment(pendingPayment, iTunesResult);

        verify(paymentPolicyService, never()).findByCommunityAndAppStoreProductId(community, actualProductId);
        verify(paymentDetails, never()).setPaymentPolicy(any(PaymentPolicy.class));
        verify(paymentDetails).completeSuccessful();
        verify(paymentDetailsRepository).save(paymentDetails);
        verify(submittedPaymentService).save(submittedPaymentArgumentCaptor.capture());
        verify(applicationEventPublisher).publishEvent(paymentEventArgumentCaptor.capture());
        verify(pendingPaymentRepository).delete(pendingPayment);

        SubmittedPayment submittedPayment = submittedPaymentArgumentCaptor.getValue();
        assertEquals(originalTransactionId, submittedPayment.getAppStoreOriginalTransactionId());
        assertEquals(PaymentDetailsStatus.SUCCESSFUL, submittedPayment.getStatus());
        assertEquals(actualReceipt, submittedPayment.getBase64EncodedAppStoreReceipt());
        assertEquals(originalTransactionId, submittedPayment.getExternalTxId());
        assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, submittedPayment.getPaymentSystem());
        assertEquals(amount, submittedPayment.getAmount());
        assertEquals(currency, submittedPayment.getCurrencyISO());
        assertEquals(now.getTime(), submittedPayment.getTimestamp());
        assertEquals(expire.getTime() / 1000, submittedPayment.getNextSubPayment());
        assertNull(submittedPayment.getDescriptionError());

        PaymentEvent paymentEvent = paymentEventArgumentCaptor.getValue();
        assertEquals(paymentEvent.getPayment(), submittedPayment);
    }

    @Test
    public void confirmOnetimePaymentWithTheSameProductIdAsStoredPaymentPolicy() throws Exception {
        when(paymentPolicyService.findByCommunityAndAppStoreProductId(community, actualProductId)).thenReturn(paymentPolicy);
        when(iTunesResult.getExpireTime()).thenReturn(null);
        when(iTunesResult.getPurchaseTime()).thenReturn(now.getTime());
        when(paymentPolicy.getPeriod()).thenReturn(new Period(DurationUnit.DAYS, 1));

        helper.confirmPayment(pendingPayment, iTunesResult);

        verify(paymentPolicyService).findByCommunityAndAppStoreProductId(community, actualProductId);
        verify(paymentDetails, never()).setPaymentPolicy(any(PaymentPolicy.class));
        verify(paymentDetails).completeSuccessful();
        verify(paymentDetailsRepository).save(paymentDetails);
        verify(submittedPaymentService).save(submittedPaymentArgumentCaptor.capture());
        verify(applicationEventPublisher).publishEvent(paymentEventArgumentCaptor.capture());
        verify(pendingPaymentRepository).delete(pendingPayment);

        SubmittedPayment submittedPayment = submittedPaymentArgumentCaptor.getValue();
        assertEquals(originalTransactionId, submittedPayment.getAppStoreOriginalTransactionId());
        assertEquals(PaymentDetailsStatus.SUCCESSFUL, submittedPayment.getStatus());
        assertEquals(actualReceipt, submittedPayment.getBase64EncodedAppStoreReceipt());
        assertEquals(originalTransactionId, submittedPayment.getExternalTxId());
        assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, submittedPayment.getPaymentSystem());
        assertEquals(amount, submittedPayment.getAmount());
        assertEquals(currency, submittedPayment.getCurrencyISO());
        assertEquals(DateUtils.addDays(now, 1).getTime() / 1000, submittedPayment.getNextSubPayment());
        assertEquals(now.getTime(), submittedPayment.getTimestamp());
        assertNull(submittedPayment.getDescriptionError());

        PaymentEvent paymentEvent = paymentEventArgumentCaptor.getValue();
        assertEquals(paymentEvent.getPayment(), submittedPayment);
    }

    @Test
    public void confirmPaymentWithNotTheSameProductIdAsStoredPaymentPolicy() throws Exception {
        final String storedProductId = "com.mq.stored";

        PaymentPolicy actualPaymentPolicy = mock(PaymentPolicy.class);

        when(paymentPolicy.getAppStoreProductId()).thenReturn(storedProductId);
        when(paymentPolicyService.findByCommunityAndAppStoreProductId(community, actualProductId)).thenReturn(actualPaymentPolicy);

        helper.confirmPayment(pendingPayment, iTunesResult);

        verify(paymentDetails, never()).setPaymentPolicy(any(PaymentPolicy.class));
        verify(iTunesPaymentDetailsService).createNewPaymentDetails(user, actualProductId, actualReceipt);
        verify(paymentDetails).completeSuccessful();
        verify(paymentDetailsRepository).save(paymentDetails);
        verify(submittedPaymentService).save(submittedPaymentArgumentCaptor.capture());
        verify(applicationEventPublisher).publishEvent(paymentEventArgumentCaptor.capture());
        verify(pendingPaymentRepository).delete(pendingPayment);

        SubmittedPayment submittedPayment = submittedPaymentArgumentCaptor.getValue();
        assertEquals(originalTransactionId, submittedPayment.getAppStoreOriginalTransactionId());
        assertEquals(PaymentDetailsStatus.SUCCESSFUL, submittedPayment.getStatus());
        assertEquals(actualReceipt, submittedPayment.getBase64EncodedAppStoreReceipt());
        assertEquals(originalTransactionId, submittedPayment.getExternalTxId());
        assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, submittedPayment.getPaymentSystem());
        assertEquals(amount, submittedPayment.getAmount());
        assertEquals(currency, submittedPayment.getCurrencyISO());
        assertEquals(now.getTime(), submittedPayment.getTimestamp());
        assertNull(submittedPayment.getDescriptionError());

        PaymentEvent paymentEvent = paymentEventArgumentCaptor.getValue();
        assertEquals(paymentEvent.getPayment(), submittedPayment);
    }


    @Test
    public void stopSubscription() throws Exception {
        final int status = 21006;
        final String descriptionError = "Not valid receipt, status " + status;
        when(iTunesResult.getResult()).thenReturn(status);

        helper.stopSubscription(pendingPayment, descriptionError);

        verify(paymentDetails).completedWithError(descriptionError);
        paymentDetailsRepository.save(paymentDetails);
        userService.unsubscribeUser(user, descriptionError);
        pendingPaymentRepository.delete(pendingPayment);
    }

    @Test
    public void failAttemptForStoredPaymentPolicyAndNotLastAttempt() throws Exception {
        final String descriptionError = "Error";
        final String appStoreTransactionId = "111111111";

        when(paymentDetails.getPaymentPolicy()).thenReturn(paymentPolicy);
        when(paymentDetails.shouldBeUnSubscribed()).thenReturn(false);
        when(user.getAppStoreOriginalTransactionId()).thenReturn(appStoreTransactionId);

        helper.failAttempt(pendingPayment, descriptionError);

        verify(paymentDetails).completedWithError(descriptionError);
        verify(paymentDetailsRepository).save(paymentDetails);
        verify(submittedPaymentService).save(submittedPaymentArgumentCaptor.capture());
        verify(userService, never()).unsubscribeUser(user, descriptionError);
        verify(pendingPaymentRepository).delete(pendingPayment);

        SubmittedPayment submittedPayment = submittedPaymentArgumentCaptor.getValue();
        assertEquals(appStoreTransactionId, submittedPayment.getAppStoreOriginalTransactionId());
        assertEquals(PaymentDetailsStatus.ERROR, submittedPayment.getStatus());
        assertEquals(actualReceipt, submittedPayment.getBase64EncodedAppStoreReceipt());
        assertEquals(appStoreTransactionId, submittedPayment.getExternalTxId());
        assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, submittedPayment.getPaymentSystem());
        assertEquals(amount, submittedPayment.getAmount());
        assertEquals(currency, submittedPayment.getCurrencyISO());
        assertEquals(now.getTime(), submittedPayment.getTimestamp());
        assertNull(submittedPayment.getDescriptionError());
    }

    @Test
    public void failAttemptForStoredPaymentPolicyAndLastAttempt() throws Exception {
        final String descriptionError = "Error";
        final String appStoreTransactionId = "111111111";

        when(paymentDetails.getPaymentPolicy()).thenReturn(paymentPolicy);
        when(paymentDetails.shouldBeUnSubscribed()).thenReturn(true);
        when(user.getAppStoreOriginalTransactionId()).thenReturn(appStoreTransactionId);

        helper.failAttempt(pendingPayment, descriptionError);

        verify(paymentDetails).completedWithError(descriptionError);
        verify(paymentDetailsRepository).save(paymentDetails);
        verify(submittedPaymentService).save(submittedPaymentArgumentCaptor.capture());
        verify(userService).unsubscribeUser(user, descriptionError);
        verify(pendingPaymentRepository).delete(pendingPayment);

        SubmittedPayment submittedPayment = submittedPaymentArgumentCaptor.getValue();
        assertEquals(appStoreTransactionId, submittedPayment.getAppStoreOriginalTransactionId());
        assertEquals(PaymentDetailsStatus.ERROR, submittedPayment.getStatus());
        assertEquals(actualReceipt, submittedPayment.getBase64EncodedAppStoreReceipt());
        assertEquals(appStoreTransactionId, submittedPayment.getExternalTxId());
        assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, submittedPayment.getPaymentSystem());
        assertEquals(amount, submittedPayment.getAmount());
        assertEquals(currency, submittedPayment.getCurrencyISO());
        assertEquals(now.getTime(), submittedPayment.getTimestamp());
        assertNull(submittedPayment.getDescriptionError());
    }
}