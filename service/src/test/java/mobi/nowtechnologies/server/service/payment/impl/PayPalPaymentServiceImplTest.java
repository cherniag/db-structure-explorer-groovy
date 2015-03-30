/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.service.EntityService;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.PaymentEventNotifier;
import mobi.nowtechnologies.server.service.payment.PaymentTestUtils;
import mobi.nowtechnologies.server.service.payment.http.PayPalHttpService;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;
import mobi.nowtechnologies.server.shared.service.BasicResponse;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.ERROR;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.SUCCESSFUL;

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
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class PayPalPaymentServiceImplTest{

    private static final String SUCCESS_URL = "http://localhost/success";
    private static final String FAILURE_URL = "http://localhost/fail";
    private static final String REDIRECT_URL = "http://localhost/redirect";
    private static final String TOKEN = "ECJ748178G052312W";
    private static final String BILLINGAGREEMENTID = "BA178G052312W";
    private static final String PAYERID = "RT67JJ";
    private static final String TRANSACTIONID = "TX12PF1112222";
    private static final User DEFAULT_USER = new User().withId(1);

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Mock
    private PayPalHttpService httpService;

    @Mock
    private PaymentDetailsService paymentDetailsService;

    @Mock
    private PaymentDetailsRepository paymentDetailsRepository;

    @Mock
    private EntityService entityService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private PayPalPaymentServiceImpl payPalPaymentService;

    @Mock
    private PaymentEventNotifier paymentEventNotifier;

    @Before
    public void setUp(){
        payPalPaymentService.setRedirectURL(REDIRECT_URL);

        when(paymentDetailsRepository.save(any(PaymentDetails.class))).thenAnswer(new Answer<PaymentDetails>() {
            @Override
            public PaymentDetails answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (PaymentDetails) args[0];
            }
        });

        when(entityService.updateEntity(any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                ReflectionTestUtils.setField(args[0], "i", 1L);
                return args[0];
            }
        });
    }


    @Test
    public void testCreatePaymentDetailsOneTimePayment() throws Exception {
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setPaymentPolicyType(PaymentPolicyType.ONETIME);

        when(httpService.getTokenForOnetimeType(anyString(), anyString(), anyString(), anyString(), any(BigDecimal.class))).thenReturn(getSuccessGetTokenResponse());
        PayPalPaymentDetails paymentDetails = payPalPaymentService.createPaymentDetails(null, SUCCESS_URL, FAILURE_URL, DEFAULT_USER, paymentPolicy);

        assertEquals(REDIRECT_URL.concat("?cmd=_express-checkout&useraction=commit&token=").concat(TOKEN), paymentDetails.getBillingAgreementTxId());
        verify(httpService, times(1)).getTokenForOnetimeType(SUCCESS_URL, FAILURE_URL, null, null, null);
    }


    @Test
    public void testCreatePaymentDetailsRecurrentPayment() throws Exception {
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setPaymentPolicyType(PaymentPolicyType.RECURRENT);

        when(httpService.getTokenForRecurrentType(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(getSuccessGetTokenResponse());
        PayPalPaymentDetails paymentDetails = payPalPaymentService.createPaymentDetails(null, SUCCESS_URL, FAILURE_URL, DEFAULT_USER, paymentPolicy);

        assertEquals(REDIRECT_URL.concat("?cmd=_express-checkout&useraction=commit&token=").concat(TOKEN), paymentDetails.getBillingAgreementTxId());
        verify(httpService, times(1)).getTokenForRecurrentType(SUCCESS_URL, FAILURE_URL, null, null, null);
    }


    @Test
    public void testCreatePaymentDetailsPayPalException() throws Exception {
        when(httpService.getTokenForRecurrentType(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(getFailureGetTokenResponse());

        thrown.expect(ServiceException.class);
        payPalPaymentService.createPaymentDetails(null, SUCCESS_URL, FAILURE_URL, DEFAULT_USER, new PaymentPolicy());
    }


    @Test
    public void testCommitPaymentDetailsOneTimePayment() throws Exception {
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setPaymentPolicyType(PaymentPolicyType.ONETIME);

        when(httpService.getPaymentDetailsInfoForOnetimeType(anyString(), anyString())).thenReturn(getCheckoutDetailsResponse());
        PayPalPaymentDetails paymentDetails = payPalPaymentService.commitPaymentDetails(TOKEN, DEFAULT_USER, paymentPolicy, true);

        assertNull(paymentDetails.getBillingAgreementTxId());
        assertEquals(PAYERID, paymentDetails.getPayerId());
        assertEquals(TOKEN, paymentDetails.getToken());
        verify(httpService, times(1)).getPaymentDetailsInfoForOnetimeType(TOKEN, null);
    }


    @Test
    public void testCommitPaymentDetailsRecurrentPayment() throws Exception {
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setPaymentPolicyType(PaymentPolicyType.RECURRENT);

        when(httpService.getPaymentDetailsInfoForRecurrentType(anyString(), anyString())).thenReturn(getBillingAgreementResponse());
        PayPalPaymentDetails paymentDetails = payPalPaymentService.commitPaymentDetails(TOKEN, DEFAULT_USER, paymentPolicy, true);

        assertEquals(BILLINGAGREEMENTID, paymentDetails.getBillingAgreementTxId());
        assertNull(paymentDetails.getPayerId());
        assertNull(paymentDetails.getToken());
        verify(httpService, times(1)).getPaymentDetailsInfoForRecurrentType(TOKEN, null);
    }


    @Test
    public void testCommitPaymentDetailsRecurrentPaymentPayPalException() throws Exception {
        when(httpService.getPaymentDetailsInfoForRecurrentType(anyString(), anyString())).thenReturn(getFailureGetTokenResponse());

        thrown.expect(ServiceException.class);
        payPalPaymentService.commitPaymentDetails(TOKEN, DEFAULT_USER, new PaymentPolicy(), true);
    }


    @Test
    public void testStartOneTimePayment() throws Exception {
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setPaymentPolicyType(PaymentPolicyType.ONETIME);
        PendingPayment pendingPayment = new PendingPayment();
        PayPalPaymentDetails paymentDetails = (PayPalPaymentDetails)new PayPalPaymentDetails().withPaymentPolicy(paymentPolicy);
        paymentDetails.setPayerId(PAYERID);
        paymentDetails.setToken(TOKEN);
        pendingPayment.setI(1L);
        pendingPayment.setUser(new User().withCurrentPaymentDetails(paymentDetails));
        pendingPayment.setPaymentDetails(paymentDetails);

        when(httpService.makePaymentForOnetimeType(anyString(), anyString(), anyString(), anyString(), any(BigDecimal.class))).thenReturn(getDoPaymentResponse());
        payPalPaymentService.startPayment(pendingPayment);

        assertNull(paymentDetails.getErrorCode());
        assertNull(paymentDetails.getDescriptionError());
        assertEquals(SUCCESSFUL, paymentDetails.getLastPaymentStatus());
        verify(httpService, times(1)).makePaymentForOnetimeType(TOKEN, null, PAYERID, null, null);
    }


    @Test
    public void testStartRecurrentPayment() throws Exception {
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setPaymentPolicyType(PaymentPolicyType.RECURRENT);
        PendingPayment pendingPayment = new PendingPayment();
        PayPalPaymentDetails paymentDetails = (PayPalPaymentDetails)new PayPalPaymentDetails().withPaymentPolicy(paymentPolicy);
        paymentDetails.setBillingAgreementTxId(BILLINGAGREEMENTID);
        pendingPayment.setI(1L);
        pendingPayment.setUser(new User().withCurrentPaymentDetails(paymentDetails));
        pendingPayment.setPaymentDetails(paymentDetails);

        when(httpService.makePaymentForRecurrentType(anyString(), anyString(), any(BigDecimal.class), anyString())).thenReturn(getDoPaymentResponse());
        payPalPaymentService.startPayment(pendingPayment);

        assertNull(paymentDetails.getErrorCode());
        assertNull(paymentDetails.getDescriptionError());
        assertEquals(SUCCESSFUL, paymentDetails.getLastPaymentStatus());
        verify(httpService, times(1)).makePaymentForRecurrentType(BILLINGAGREEMENTID, null, null, null);
    }


    @Test
    public void testStartRecurrentPaymentFail() throws Exception {
        PendingPayment pendingPayment = new PendingPayment();
        PayPalPaymentDetails paymentDetails = (PayPalPaymentDetails)new PayPalPaymentDetails().withPaymentPolicy(new PaymentPolicy()).withOwner(DEFAULT_USER);
        pendingPayment.setI(1L);
        pendingPayment.setUser(new User().withCurrentPaymentDetails(paymentDetails));
        pendingPayment.setPaymentDetails(paymentDetails);

        when(httpService.makePaymentForRecurrentType(anyString(), anyString(), any(BigDecimal.class), anyString())).thenReturn(getFailedDoPaymentResponse());
        payPalPaymentService.startPayment(pendingPayment);

        assertNotNull(paymentDetails.getDescriptionError());
        assertEquals(ERROR, paymentDetails.getLastPaymentStatus());
        verify(httpService, times(1)).makePaymentForRecurrentType(null, null, null, null);
    }


    //todo: remove this test after several weeks from release SRV-648 (It is for backward compatibility)
    @Test
    public void testStartOneTimePaymentForPaymentDetailsCreatedBeforeSRV648() throws Exception {
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setPaymentPolicyType(PaymentPolicyType.ONETIME);
        PendingPayment pendingPayment = new PendingPayment();
        PayPalPaymentDetails paymentDetails = (PayPalPaymentDetails)new PayPalPaymentDetails().withPaymentPolicy(paymentPolicy);
        paymentDetails.setBillingAgreementTxId(BILLINGAGREEMENTID);
        pendingPayment.setI(1L);
        pendingPayment.setUser(new User().withCurrentPaymentDetails(paymentDetails));
        pendingPayment.setPaymentDetails(paymentDetails);

        when(httpService.makePaymentForRecurrentType(anyString(), anyString(), any(BigDecimal.class), anyString())).thenReturn(getDoPaymentResponse());
        payPalPaymentService.startPayment(pendingPayment);

        assertNull(paymentDetails.getErrorCode());
        assertNull(paymentDetails.getDescriptionError());
        assertEquals(SUCCESSFUL, paymentDetails.getLastPaymentStatus());
        verify(httpService, times(1)).makePaymentForRecurrentType(BILLINGAGREEMENTID, null, null, null);
    }



    private PayPalResponse getFailedDoPaymentResponse() {
        BasicResponse basicResponse = PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "PAYMENTINFO_0_TRANSACTIONID=".concat(TRANSACTIONID).concat("&ACK=Fail"));
        return new PayPalResponse(basicResponse);
    }

    private PayPalResponse getDoPaymentResponse() {
        BasicResponse basicResponse = PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK, "PAYMENTINFO_0_TRANSACTIONID=".concat(TRANSACTIONID).concat("&ACK=Success"));
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