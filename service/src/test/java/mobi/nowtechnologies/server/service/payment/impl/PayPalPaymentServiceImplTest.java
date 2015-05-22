/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.PayPalPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory;
import mobi.nowtechnologies.server.persistence.domain.PendingPaymentFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.SubmittedPaymentRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.PaymentEventNotifier;
import mobi.nowtechnologies.server.service.payment.PaymentTestUtils;
import mobi.nowtechnologies.server.service.payment.http.PayPalHttpService;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.support.http.BasicResponse;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.NONE;

import javax.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import org.springframework.context.ApplicationEventPublisher;

import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.runners.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class PayPalPaymentServiceImplTest {

    private static final String SUCCESS_URL = "http://localhost/success";
    private static final String FAILURE_URL = "http://localhost/fail";
    private static final String REDIRECT_URL = "http://localhost/redirect";
    private static final String TOKEN = "ECJ748178G052312W";
    private static final String BILLINGAGREEMENTID = "BA178G052312W";
    private static final String PAYERID = "RT67JJ";
    private static final String TRANSACTIONID = "TX12PF1112222";
    private static final User DEFAULT_USER = new User().withId(1);
    private static final String CURRENCYISO = "GBP";
    private static final BigDecimal AMOUNT = BigDecimal.ONE;
    private static final String COMMUNITY = "mtv1";
    private static final int RETRIES_ON_ERROR = 5;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Mock
    private PayPalHttpService httpService;

    @Mock
    private PaymentDetailsService paymentDetailsService;

    @Mock
    private PaymentDetailsRepository paymentDetailsRepository;

    @Mock
    private PendingPaymentRepository pendingPaymentRepository;

    @Mock
    private SubmittedPaymentRepository submittedPaymentRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private PayPalPaymentServiceImpl payPalPaymentService;

    @Mock
    private PaymentEventNotifier paymentEventNotifier;

    @Captor
    private ArgumentCaptor<PendingPayment> pendingPaymentCaptor;
    @Captor
    private ArgumentCaptor<SubmittedPayment> submittedPaymentCaptor;
    @Captor
    private ArgumentCaptor<PaymentDetails> paymentDetailsCaptor;

    @Captor
    private ArgumentCaptor<PaymentEvent> paymentEventsCaptor;

    @Before
    public void setUp() {
        payPalPaymentService.setRedirectURL(REDIRECT_URL);
        payPalPaymentService.setRetriesOnError(RETRIES_ON_ERROR);

        when(submittedPaymentRepository.save(any(SubmittedPayment.class))).thenAnswer(new Answer<SubmittedPayment>() {
            @Override
            public SubmittedPayment answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (SubmittedPayment) args[0];
            }
        });

        when(paymentDetailsRepository.save(any(PaymentDetails.class))).thenAnswer(new Answer<PaymentDetails>() {
            @Override
            public PaymentDetails answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (PaymentDetails) args[0];
            }
        });
    }


    @Test
    public void testCreatePaymentDetailsOneTimePayment() throws Exception {
        //
        // Given
        //
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy(PaymentPolicyType.ONETIME, CURRENCYISO, AMOUNT, COMMUNITY);
        when(httpService.getTokenForOnetimeType(eq(SUCCESS_URL), eq(FAILURE_URL), eq(COMMUNITY), eq(CURRENCYISO), eq(AMOUNT))).thenReturn(getSuccessGetTokenResponse());

        //
        // When
        //
        PayPalPaymentDetails paymentDetails = payPalPaymentService.createPaymentDetails(null, SUCCESS_URL, FAILURE_URL, DEFAULT_USER, paymentPolicy);

        //
        // Then
        //
        assertEquals(REDIRECT_URL.concat("?cmd=_express-checkout&useraction=commit&token=").concat(TOKEN), paymentDetails.getBillingAgreementTxId());
        assertEquals(NONE, paymentDetails.getLastPaymentStatus());
        assertEquals(DEFAULT_USER, paymentDetails.getOwner());
        assertEquals(RETRIES_ON_ERROR, paymentDetails.getRetriesOnError());
        assertEquals(0, paymentDetails.getMadeAttempts());
        assertEquals(0, paymentDetails.getMadeRetries());
        assertEquals(paymentPolicy, paymentDetails.getPaymentPolicy());
        assertFalse(paymentDetails.isActivated());
    }


    @Test
    public void testCreatePaymentDetailsRecurrentPayment() throws Exception {
        //
        // Given
        //
        final String billingAgreementDescription = "Billing description";
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy(PaymentPolicyType.RECURRENT, CURRENCYISO, null, COMMUNITY);
        when(httpService.getTokenForRecurrentType(eq(SUCCESS_URL), eq(FAILURE_URL), eq(CURRENCYISO), eq(COMMUNITY), eq(billingAgreementDescription))).thenReturn(getSuccessGetTokenResponse());

        //
        // When
        //
        PayPalPaymentDetails paymentDetails = payPalPaymentService.createPaymentDetails(billingAgreementDescription, SUCCESS_URL, FAILURE_URL, DEFAULT_USER, paymentPolicy);

        //
        // Then
        //
        assertEquals(REDIRECT_URL.concat("?cmd=_express-checkout&useraction=commit&token=").concat(TOKEN), paymentDetails.getBillingAgreementTxId());
        assertEquals(NONE, paymentDetails.getLastPaymentStatus());
        assertEquals(DEFAULT_USER, paymentDetails.getOwner());
        assertEquals(RETRIES_ON_ERROR, paymentDetails.getRetriesOnError());
        assertEquals(0, paymentDetails.getMadeAttempts());
        assertEquals(0, paymentDetails.getMadeRetries());
        assertEquals(paymentPolicy, paymentDetails.getPaymentPolicy());
        assertFalse(paymentDetails.isActivated());
    }


    @Test
    public void testCreatePaymentDetailsPayPalException() throws Exception {
        //
        // Given
        //
        when(httpService.getTokenForRecurrentType(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(getFailureGetTokenResponse());

        //
        // Then
        //
        thrown.expect(ServiceException.class);

        //
        // When
        //
        payPalPaymentService.createPaymentDetails(null, SUCCESS_URL, FAILURE_URL, DEFAULT_USER, new PaymentPolicy());
    }


    @Test
    public void testCommitPaymentDetailsOneTimePayment() throws Exception {
        //
        // Given
        //
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy(PaymentPolicyType.ONETIME, COMMUNITY);
        when(httpService.getPaymentDetailsInfoForOnetimeType(eq(TOKEN), eq(COMMUNITY))).thenReturn(getCheckoutDetailsResponse());

        //
        // When
        //
        PayPalPaymentDetails paymentDetails = payPalPaymentService.commitPaymentDetails(TOKEN, DEFAULT_USER, paymentPolicy, true);

        //
        // Then
        //
        assertNull(paymentDetails.getBillingAgreementTxId());
        assertEquals(PAYERID, paymentDetails.getPayerId());
        assertEquals(TOKEN, paymentDetails.getToken());
        assertEquals(paymentPolicy, paymentDetails.getPaymentPolicy());
        assertTrue(paymentDetails.isActivated());
        assertEquals(NONE, paymentDetails.getLastPaymentStatus());
        assertEquals(DEFAULT_USER, paymentDetails.getOwner());
        assertEquals(RETRIES_ON_ERROR, paymentDetails.getRetriesOnError());
        assertEquals(0, paymentDetails.getMadeAttempts());
        assertEquals(0, paymentDetails.getMadeRetries());
        verify(paymentDetailsRepository, times(1)).save(paymentDetails);
    }


    @Test
    public void testCommitPaymentDetailsRecurrentPayment() throws Exception {
        //
        // Given
        //
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy(PaymentPolicyType.RECURRENT, COMMUNITY);
        when(httpService.getPaymentDetailsInfoForRecurrentType(eq(TOKEN), eq(COMMUNITY))).thenReturn(getBillingAgreementResponse());

        //
        // When
        //
        PayPalPaymentDetails paymentDetails = payPalPaymentService.commitPaymentDetails(TOKEN, DEFAULT_USER, paymentPolicy, true);

        //
        // Then
        //
        assertEquals(BILLINGAGREEMENTID, paymentDetails.getBillingAgreementTxId());
        assertNull(paymentDetails.getPayerId());
        assertNull(paymentDetails.getToken());
        assertEquals(paymentPolicy, paymentDetails.getPaymentPolicy());
        assertTrue(paymentDetails.isActivated());
        assertEquals(NONE, paymentDetails.getLastPaymentStatus());
        assertEquals(DEFAULT_USER, paymentDetails.getOwner());
        assertEquals(RETRIES_ON_ERROR, paymentDetails.getRetriesOnError());
        assertEquals(0, paymentDetails.getMadeAttempts());
        assertEquals(0, paymentDetails.getMadeRetries());
        verify(paymentDetailsRepository, times(1)).save(paymentDetails);
    }


    @Test
    public void testCommitPaymentDetailsRecurrentPaymentPayPalException() throws Exception {
        //
        // Given
        //
        when(httpService.getPaymentDetailsInfoForRecurrentType(anyString(), anyString())).thenReturn(getFailureGetTokenResponse());

        //
        // Then
        //
        thrown.expect(ServiceException.class);

        //
        // When
        //
        payPalPaymentService.commitPaymentDetails(TOKEN, DEFAULT_USER, new PaymentPolicy(), true);
    }


    @Test
    public void testStartOneTimePayment() throws Exception {
        //
        // Given
        //
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy(PaymentPolicyType.ONETIME);
        PayPalPaymentDetails paymentDetails = PayPalPaymentDetailsFactory.createPayPalPaymentDetails(paymentPolicy, PAYERID, TOKEN, null);
        User user = createUser(paymentPolicy, paymentDetails, COMMUNITY);
        PendingPayment pendingPayment = PendingPaymentFactory.create(paymentDetails, user, AMOUNT, CURRENCYISO);

        final String message = "PAYMENTINFO_0_TRANSACTIONID=".concat(TRANSACTIONID).concat("&ACK=Success");

        when(httpService.makePaymentForOnetimeType(eq(TOKEN), eq(COMMUNITY), eq(PAYERID), eq(CURRENCYISO), eq(AMOUNT))).thenReturn(getDoPaymentResponse(message));

        //
        // When
        //
        payPalPaymentService.startPayment(pendingPayment);

        //
        // Then
        //
        verify(pendingPayment, times(1)).setExternalTxId(TRANSACTIONID);
        verify(paymentDetails, times(1)).setDescriptionError(null);
        verify(paymentDetails, times(1)).setErrorCode(null);
        verify(paymentDetails, times(1)).incrementMadeAttemptsAccordingToMadeRetries();
        verify(paymentDetails, times(1)).setLastPaymentStatus(PaymentDetailsStatus.SUCCESSFUL);
        verify(applicationEventPublisher).publishEvent(paymentEventsCaptor.capture());
        verify(pendingPaymentRepository).delete(anyLong());

        verify(pendingPaymentRepository, times(1)).save(pendingPaymentCaptor.capture());
        verify(submittedPaymentRepository, times(1)).save(submittedPaymentCaptor.capture());
        verify(paymentDetailsRepository, times(1)).save(paymentDetailsCaptor.capture());

        assertSame(pendingPaymentCaptor.getValue(), pendingPayment);
        assertSame(paymentDetailsCaptor.getValue(), paymentDetails);
        assertSame(submittedPaymentCaptor.getValue().getPaymentDetails(), paymentDetails);

        SubmittedPayment spArgument = submittedPaymentCaptor.getValue();
        assertEquals(PaymentDetailsStatus.SUCCESSFUL, spArgument.getStatus());
        assertEquals(CURRENCYISO, spArgument.getCurrencyISO());

        assertSame(paymentEventsCaptor.getValue().getPayment(), spArgument);
    }

    @Test
    public void testStartRecurrentPayment() throws Exception {
        //
        // Given
        //
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy(PaymentPolicyType.RECURRENT);
        PayPalPaymentDetails paymentDetails = PayPalPaymentDetailsFactory.createPayPalPaymentDetails(paymentPolicy, null, null, BILLINGAGREEMENTID);
        User user = createUser(paymentPolicy, paymentDetails, COMMUNITY);
        PendingPayment pendingPayment = PendingPaymentFactory.create(paymentDetails, user, AMOUNT, CURRENCYISO);

        final String message = "TRANSACTIONID=".concat(TRANSACTIONID).concat("&ACK=Success");
        when(httpService.makePaymentForRecurrentType(eq(BILLINGAGREEMENTID), eq(CURRENCYISO), eq(AMOUNT), eq(COMMUNITY))).thenReturn(getDoPaymentResponse(message));

        //
        // When
        //
        payPalPaymentService.startPayment(pendingPayment);

        //
        // Then
        //
        verify(pendingPayment, times(1)).setExternalTxId(TRANSACTIONID);
        verify(paymentDetails, times(1)).setDescriptionError(null);
        verify(paymentDetails, times(1)).setErrorCode(null);
        verify(paymentDetails, times(1)).incrementMadeAttemptsAccordingToMadeRetries();
        verify(paymentDetails, times(1)).setLastPaymentStatus(PaymentDetailsStatus.SUCCESSFUL);
        verify(applicationEventPublisher).publishEvent(paymentEventsCaptor.capture());
        verify(pendingPaymentRepository).delete(anyLong());

        verify(pendingPaymentRepository, times(1)).save(pendingPaymentCaptor.capture());
        verify(submittedPaymentRepository, times(1)).save(submittedPaymentCaptor.capture());
        verify(paymentDetailsRepository, times(1)).save(paymentDetailsCaptor.capture());

        assertSame(pendingPaymentCaptor.getValue(), pendingPayment);
        assertSame(paymentDetailsCaptor.getValue(), paymentDetails);
        assertSame(submittedPaymentCaptor.getValue().getPaymentDetails(), paymentDetails);

        SubmittedPayment spArgument = submittedPaymentCaptor.getValue();
        assertEquals(PaymentDetailsStatus.SUCCESSFUL, spArgument.getStatus());
        assertEquals(CURRENCYISO, spArgument.getCurrencyISO());

        assertSame(paymentEventsCaptor.getValue().getPayment(), spArgument);
    }


    @Test
    public void testStartRecurrentPaymentFail500() throws Exception {
        //
        // Given
        //
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy(PaymentPolicyType.RECURRENT);
        PayPalPaymentDetails paymentDetails = PayPalPaymentDetailsFactory.createPayPalPaymentDetails(paymentPolicy, null, null, BILLINGAGREEMENTID);
        User user = createUser(paymentPolicy, paymentDetails, COMMUNITY);
        PendingPayment pendingPayment = PendingPaymentFactory.create(paymentDetails, user, AMOUNT, CURRENCYISO);

        final String message = "Internal server error";
        when(httpService.makePaymentForRecurrentType(eq(BILLINGAGREEMENTID), eq(CURRENCYISO), eq(AMOUNT), eq(COMMUNITY))).thenReturn(getFailedDoPaymentResponse(message));

        //
        // When
        //
        payPalPaymentService.startPayment(pendingPayment);

        //
        // Then
        //
        verify(pendingPayment, times(1)).setExternalTxId(null);
        verify(paymentDetails, times(1)).setDescriptionError("Unexpected http status code [500] so the madeRetries won't be incremented");
        verify(paymentDetails, times(1)).setErrorCode(null);
        verify(paymentDetails, times(1)).setLastPaymentStatus(PaymentDetailsStatus.ERROR);
        verify(pendingPaymentRepository).delete(anyLong());

        verify(pendingPaymentRepository, times(1)).save(pendingPaymentCaptor.capture());
        verify(submittedPaymentRepository, times(1)).save(submittedPaymentCaptor.capture());
        verify(paymentDetailsRepository, times(1)).save(paymentDetailsCaptor.capture());

        assertSame(pendingPaymentCaptor.getValue(), pendingPayment);
        assertSame(paymentDetailsCaptor.getValue(), paymentDetails);
        assertSame(submittedPaymentCaptor.getValue().getPaymentDetails(), paymentDetails);

        SubmittedPayment spArgument = submittedPaymentCaptor.getValue();
        assertEquals(PaymentDetailsStatus.ERROR, spArgument.getStatus());
        assertEquals(CURRENCYISO, spArgument.getCurrencyISO());
    }


    @Test
    public void testStartRecurrentPaymentFail200() throws Exception {
        //
        // Given
        //
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy(PaymentPolicyType.RECURRENT);
        PayPalPaymentDetails paymentDetails = PayPalPaymentDetailsFactory.createPayPalPaymentDetails(paymentPolicy, null, null, BILLINGAGREEMENTID);
        User user = createUser(paymentPolicy, paymentDetails, COMMUNITY);
        PendingPayment pendingPayment = PendingPaymentFactory.create(paymentDetails, user, AMOUNT, CURRENCYISO);

        final String message = "TRANSACTIONID=".concat(TRANSACTIONID).concat("&L_ERRORCODE_PREFIX=SomeErrorCode").concat("&ACK=Fail");
        when(httpService.makePaymentForRecurrentType(eq(BILLINGAGREEMENTID), eq(CURRENCYISO), eq(AMOUNT), eq(COMMUNITY))).thenReturn(getDoPaymentResponse(message));

        //
        // When
        //
        payPalPaymentService.startPayment(pendingPayment);

        //
        // Then
        //
        verify(pendingPayment, times(1)).setExternalTxId(TRANSACTIONID);
        verify(paymentDetails, times(1)).setDescriptionError("[SomeErrorCode]");
        verify(paymentDetails, times(1)).setErrorCode(null);
        verify(paymentDetails, times(1)).setLastPaymentStatus(PaymentDetailsStatus.ERROR);
        verify(paymentDetails, times(1)).incrementMadeAttemptsAccordingToMadeRetries();
        verify(pendingPaymentRepository).delete(anyLong());

        verify(pendingPaymentRepository, times(1)).save(pendingPaymentCaptor.capture());
        verify(submittedPaymentRepository, times(1)).save(submittedPaymentCaptor.capture());
        verify(paymentDetailsRepository, times(1)).save(paymentDetailsCaptor.capture());

        assertSame(pendingPaymentCaptor.getValue(), pendingPayment);
        assertSame(paymentDetailsCaptor.getValue(), paymentDetails);
        assertSame(submittedPaymentCaptor.getValue().getPaymentDetails(), paymentDetails);

        SubmittedPayment spArgument = submittedPaymentCaptor.getValue();
        assertEquals(PaymentDetailsStatus.ERROR, spArgument.getStatus());
        assertEquals(CURRENCYISO, spArgument.getCurrencyISO());
    }


    //todo: remove this test after several weeks from release SRV-648 (It is for backward compatibility)
    @Test
    public void testStartOneTimePaymentForPaymentDetailsCreatedBeforeSRV648() throws Exception {
        //
        // Given
        //
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy(PaymentPolicyType.ONETIME);
        PayPalPaymentDetails paymentDetails = PayPalPaymentDetailsFactory.createPayPalPaymentDetails(paymentPolicy, null, null, BILLINGAGREEMENTID);
        User user = createUser(paymentPolicy, paymentDetails, COMMUNITY);
        PendingPayment pendingPayment = PendingPaymentFactory.create(paymentDetails, user, AMOUNT, CURRENCYISO);

        final String message = "TRANSACTIONID=".concat(TRANSACTIONID).concat("&ACK=Success");
        when(httpService.makePaymentForRecurrentType(eq(BILLINGAGREEMENTID), eq(CURRENCYISO), eq(AMOUNT), eq(COMMUNITY))).thenReturn(getDoPaymentResponse(message));

        //
        // When
        //
        payPalPaymentService.startPayment(pendingPayment);

        //
        // Then
        //
        verify(pendingPayment, times(1)).setExternalTxId(TRANSACTIONID);
        verify(paymentDetails, times(1)).setDescriptionError(null);
        verify(paymentDetails, times(1)).setErrorCode(null);
        verify(paymentDetails, times(1)).incrementMadeAttemptsAccordingToMadeRetries();
        verify(paymentDetails, times(1)).setLastPaymentStatus(PaymentDetailsStatus.SUCCESSFUL);
        verify(applicationEventPublisher).publishEvent(paymentEventsCaptor.capture());
        verify(pendingPaymentRepository).delete(anyLong());

        verify(pendingPaymentRepository, times(1)).save(pendingPaymentCaptor.capture());
        verify(submittedPaymentRepository, times(1)).save(submittedPaymentCaptor.capture());
        verify(paymentDetailsRepository, times(1)).save(paymentDetailsCaptor.capture());

        assertSame(pendingPaymentCaptor.getValue(), pendingPayment);
        assertSame(paymentDetailsCaptor.getValue(), paymentDetails);
        assertSame(submittedPaymentCaptor.getValue().getPaymentDetails(), paymentDetails);

        SubmittedPayment spArgument = submittedPaymentCaptor.getValue();
        assertEquals(PaymentDetailsStatus.SUCCESSFUL, spArgument.getStatus());
        assertEquals(CURRENCYISO, spArgument.getCurrencyISO());

        assertSame(paymentEventsCaptor.getValue().getPayment(), spArgument);
    }


    private User createUser(PaymentPolicy paymentPolicy, PayPalPaymentDetails paymentDetails, String communityUrl) {
        User user = mock(User.class);
        Community community = mock(Community.class);
        when(user.getCurrentPaymentDetails()).thenReturn(paymentDetails);
        when(paymentPolicy.getCommunity()).thenReturn(community);
        when(community.getRewriteUrlParameter()).thenReturn(communityUrl);
        return user;
    }

    private PayPalResponse getFailedDoPaymentResponse(String message) {
        BasicResponse basicResponse = PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        return new PayPalResponse(basicResponse);
    }

    private PayPalResponse getDoPaymentResponse(String message) {
        BasicResponse basicResponse = PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK, message);
        return new PayPalResponse(basicResponse);
    }

    private PayPalResponse getFailureGetTokenResponse() throws UnsupportedEncodingException {
        BasicResponse basicResponse = PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK, "TOKEN=".concat(TOKEN).concat("&ACK=Fail"));
        return new PayPalResponse(basicResponse);
    }

    private PayPalResponse getSuccessGetTokenResponse() throws UnsupportedEncodingException {
        BasicResponse basicResponse = PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK, "TOKEN=".concat(TOKEN).concat("&ACK=Success"));
        return new PayPalResponse(basicResponse);
    }

    private PayPalResponse getCheckoutDetailsResponse() throws UnsupportedEncodingException {
        BasicResponse basicResponse = PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK, "TOKEN=".concat(TOKEN).concat("&PAYERID=").concat(PAYERID).concat("&ACK=Success"));
        return new PayPalResponse(basicResponse);
    }

    private PayPalResponse getBillingAgreementResponse() throws UnsupportedEncodingException {
        BasicResponse basicResponse = PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK, "BILLINGAGREEMENTID=".concat(BILLINGAGREEMENTID).concat("&ACK=Success"));
        return new PayPalResponse(basicResponse);
    }
}