/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment.request;

import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.AMT;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.BUTTONSOURCE;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.CANCELURL;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.CURRENCYCODE;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.L_BILLINGAGREEMENTDESCRIPTION0;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.L_BILLINGTYPE0;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.METHOD;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.PAYERID;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.PAYMENTACTION;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.PAYMENTREQUEST_0_AMT;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.PAYMENTREQUEST_0_CURRENCYCODE;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.PAYMENTREQUEST_0_PAYMENTACTION;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.PWD;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.REFERENCEID;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.RETURNURL;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.SIGNATURE;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.SOLUTIONTYPE;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.TOKEN;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.USER;
import static mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam.VERSION;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PayPalRequestTest {

    private static final String TOKEN_VAL = "ECJ748178G052312W";
    private static final String PAYERID_VAL = "RT67JJ";
    private static final String COMMUNITY = "mtv1";
    private static final String BILLINGAGREEMENTID = "BA178G052312W";
    private static final String CURRENCY = "GBP";
    private static final BigDecimal AMOUNT = new BigDecimal("12.00");
    private static final String SUCCESS_URL = "http://localhost/success";
    private static final String FAILURE_URL = "http://localhost/fail";

    @Mock
    private CommunityResourceBundleMessageSource communityMessageSource;

    @InjectMocks
    private PayPalRequest payPalRequest;

    private DecimalFormat decimalFormat;

    @Before
    public void setUp() {
        payPalRequest.setAmountFormat("###,###.00");
        decimalFormat = (DecimalFormat) ReflectionTestUtils.getField(payPalRequest, "decimalFormat");
        Map<String, String> credentialMap = new HashMap<>();
        credentialMap.put("paypal.user", "test_username");
        credentialMap.put("paypal.password", "test_pass");
        credentialMap.put("paypal.signature", "test_signature");
        credentialMap.put("paypal.apiVersion", "80.0");
        credentialMap.put("paypal.btnSource", "PP-ECWizard");
        credentialMap.put("paypal.lBillingType0", "MerchantInitiatedBillingSingleAgreement");
        for (Map.Entry<String, String> entry : credentialMap.entrySet()) {
            when(communityMessageSource.getDecryptedMessage(anyString(), eq(entry.getKey()), any(Object[].class), any(Locale.class))).thenReturn(entry.getValue());
        }
    }

    @Test
    public void testCreateBillingAgreementRequest() {
        PayPalRequest request = payPalRequest.createBillingAgreementRequest(TOKEN_VAL, COMMUNITY);
        List<NameValuePair> params = request.build();

        assertTrue(containDefaultParams(params));
        assertTrue(containParam(params, TOKEN, TOKEN_VAL));
        assertTrue(containParam(params, METHOD, "CreateBillingAgreement"));
    }

    @Test
    public void testCreateCheckoutDetailsRequest() {
        PayPalRequest request = payPalRequest.createCheckoutDetailsRequest(TOKEN_VAL, COMMUNITY);
        List<NameValuePair> params = request.build();

        assertTrue(containDefaultParams(params));
        assertTrue(containParam(params, TOKEN, TOKEN_VAL));
        assertTrue(containParam(params, METHOD, "GetExpressCheckoutDetails"));
    }

    @Test
    public void testCreateReferenceTransactionRequest() {
        PayPalRequest request = payPalRequest.createReferenceTransactionRequest(BILLINGAGREEMENTID, CURRENCY, AMOUNT, COMMUNITY);
        List<NameValuePair> params = request.build();

        assertTrue(containDefaultParams(params));
        assertTrue(containParam(params, AMT, decimalFormat.format(AMOUNT)));
        assertTrue(containParam(params, REFERENCEID, BILLINGAGREEMENTID));
        assertTrue(containParam(params, CURRENCYCODE, CURRENCY));
        assertTrue(containParam(params, METHOD, "DoReferenceTransaction"));
        assertTrue(containParam(params, PAYMENTACTION, "Sale"));
    }

    @Test
    public void testCreateDoPaymentRequest() {
        PayPalRequest request = payPalRequest.createDoPaymentRequest(TOKEN_VAL, PAYERID_VAL, CURRENCY, AMOUNT, COMMUNITY);
        List<NameValuePair> params = request.build();

        assertTrue(containDefaultParams(params));
        assertTrue(containParam(params, PAYMENTREQUEST_0_AMT, decimalFormat.format(AMOUNT)));
        assertTrue(containParam(params, TOKEN, TOKEN_VAL));
        assertTrue(containParam(params, PAYERID, PAYERID_VAL));
        assertTrue(containParam(params, PAYMENTREQUEST_0_CURRENCYCODE, CURRENCY));
        assertTrue(containParam(params, METHOD, "DoExpressCheckoutPayment"));
        assertTrue(containParam(params, PAYMENTREQUEST_0_PAYMENTACTION, "Sale"));
    }

    @Test
    public void testCreateBillingAgreementTokenRequest() {
        PayPalRequest request = payPalRequest.createBillingAgreementTokenRequest(SUCCESS_URL, FAILURE_URL, CURRENCY, COMMUNITY, BILLINGAGREEMENTID);
        List<NameValuePair> params = request.build();

        assertTrue(containDefaultParams(params));
        assertTrue(containDefaultTokenParams(params));
        assertTrue(containParam(params, L_BILLINGAGREEMENTDESCRIPTION0, BILLINGAGREEMENTID));
        assertTrue(containParam(params, L_BILLINGTYPE0, "MerchantInitiatedBillingSingleAgreement"));
    }

    @Test
    public void testCreateOnetimePaymentTokenRequest() {
        PayPalRequest request = payPalRequest.createOnetimePaymentTokenRequest(SUCCESS_URL, FAILURE_URL, CURRENCY, COMMUNITY, AMOUNT);
        List<NameValuePair> params = request.build();

        assertTrue(containDefaultParams(params));
        assertTrue(containDefaultTokenParams(params));
        assertTrue(containParam(params, SOLUTIONTYPE, "Sole"));
        assertTrue(containParam(params, AMT, decimalFormat.format(AMOUNT)));
    }


    private boolean containParam(List<NameValuePair> params, PayPalRequestParam paramName, String paramValue) {
        return params.contains(new BasicNameValuePair(paramName.name(), paramValue));
    }

    private boolean containDefaultParams(List<NameValuePair> params) {
        return params.contains(new BasicNameValuePair(USER.name(), "test_username")) &&
               params.contains(new BasicNameValuePair(PWD.name(), "test_pass")) &&
               params.contains(new BasicNameValuePair(VERSION.name(), "80.0")) &&
               params.contains(new BasicNameValuePair(BUTTONSOURCE.name(), "PP-ECWizard")) &&
               params.contains(new BasicNameValuePair(SIGNATURE.name(), "test_signature"));
    }

    private boolean containDefaultTokenParams(List<NameValuePair> params) {
        return params.contains(new BasicNameValuePair(RETURNURL.name(), SUCCESS_URL)) &&
               params.contains(new BasicNameValuePair(CANCELURL.name(), FAILURE_URL)) &&
               params.contains(new BasicNameValuePair(METHOD.name(), "SetExpressCheckout")) &&
               params.contains(new BasicNameValuePair(CURRENCYCODE.name(), CURRENCY)) &&
               params.contains(new BasicNameValuePair(PAYMENTACTION.name(), "Authorization"));
    }

}