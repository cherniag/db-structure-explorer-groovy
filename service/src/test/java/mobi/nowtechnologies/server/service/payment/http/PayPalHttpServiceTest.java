package mobi.nowtechnologies.server.service.payment.http;

import mobi.nowtechnologies.server.service.payment.PaymentTestUtils;
import mobi.nowtechnologies.server.service.payment.request.PayPalRequest;
import mobi.nowtechnologies.server.service.payment.request.PayPalRequestParam;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;
import mobi.nowtechnologies.server.support.http.BasicResponse;
import mobi.nowtechnologies.server.support.http.PostService;

import javax.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.http.NameValuePair;

import org.junit.*;
import org.mockito.*;

// TODO write fail/fail_with_warning and successful_with_warning requests
// TODO validation of the request params
public class PayPalHttpServiceTest {

    private PayPalHttpService paypalHttpService;
    private PostService httpService;
    private String billiningAgreement = "B%2d7WT01760CS939974T";

    @Before
    public void stratup() {
        paypalHttpService = new PayPalHttpService();
        httpService = Mockito.mock(PostService.class);
        paypalHttpService.setPostService(httpService);
    }

    @Test
    public void makeTokenRequest_Successful() {
        PayPalRequest request = new PayPalRequest().addParam(PayPalRequestParam.L_BILLINGAGREEMENTDESCRIPTION0, "Information for paypal page")
                                                   .addParam(PayPalRequestParam.L_BILLINGTYPE0, "MerchantInitiatedBillingSingleAgreement")
                                                   .addParam(PayPalRequestParam.PAYMENTACTION, "Authorization")
                                                   .addParam(PayPalRequestParam.CURRENCYCODE, "GBP")
                                                   .addParam(PayPalRequestParam.RETURNURL, "http://10.20.30.18:8080/portal/payPalRequest.htm")
                                                   .addParam(PayPalRequestParam.CANCELURL, "http://10.20.30.18:8080/portal/payPalCancel.htm");

        Mockito.when(httpService.sendHttpPost(Mockito.anyString(), Mockito.anyListOf(NameValuePair.class), (String) Mockito.eq(null))).thenReturn(getSuccessfulPayPalResponse());

        PayPalResponse response = paypalHttpService.makeRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(true, response.isSuccessful());
        Assert.assertEquals("", response.getDescriptionError());
        Assert.assertEquals(PayPalResponse.ACK_SUCCESS, response.getAck());
        Assert.assertNotNull(response.getToken());
    }

    @Test
    public void makeBillingAgreementRequest_Successful() throws UnsupportedEncodingException {
        PayPalRequest request = new PayPalRequest().addParam(PayPalRequestParam.TOKEN, "EC-2JB29018X62164600")
                                                   .addParam(PayPalRequestParam.METHOD, "CreateBillingAgreement")
                                                   .addParam(PayPalRequestParam.VERSION, "80.0")
                                                   .addParam(PayPalRequestParam.USER, "cn_1313656118_biz_api1.chartsnow.mobi")
                                                   .addParam(PayPalRequestParam.PWD, "1313656158")
                                                   .addParam(PayPalRequestParam.BUTTONSOURCE, "PP-ECWizard")
                                                   .addParam(PayPalRequestParam.SIGNATURE, "AoTXEljMZhDQEXJFn1kQJo2C6CbIAPP7uCi4Y-85yG98nlcq-IJBt9jQ");

        Mockito.when(httpService.sendHttpPost(Mockito.anyString(), Mockito.anyListOf(NameValuePair.class), (String) Mockito.eq(null))).thenReturn(getSuccessfulBillingAgreementResponse());

        PayPalResponse response = paypalHttpService.makeRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(true, response.isSuccessful());
        Assert.assertEquals("", response.getDescriptionError());
        Assert.assertEquals(PayPalResponse.ACK_SUCCESS, response.getAck());
        Assert.assertEquals(URLDecoder.decode(billiningAgreement, "UTF-8"), response.getBillingAgreement());
    }

    @Test
    public void makeReferenceTransactionRequest_Succesful() {
        PayPalRequest request = new PayPalRequest();

        Mockito.when(httpService.sendHttpPost(Mockito.anyString(), Mockito.anyListOf(NameValuePair.class), (String) Mockito.eq(null))).thenReturn(getSuccessfulReferenceTransactionResponse());

        PayPalResponse response = paypalHttpService.makeRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(true, response.isSuccessful());
        Assert.assertEquals("", response.getDescriptionError());
        Assert.assertEquals(PayPalResponse.ACK_SUCCESS, response.getAck());
    }

    @Test
    public void makeTokenRequest_Fail() {
        PayPalRequest request = new PayPalRequest();

        Mockito.when(httpService.sendHttpPost(Mockito.anyString(), Mockito.anyListOf(NameValuePair.class), (String) Mockito.eq(null))).thenReturn(getFailResponse());

        PayPalResponse response = paypalHttpService.makeRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(false, response.isSuccessful());
        Assert.assertNotNull(response.getDescriptionError());
    }

    private BasicResponse getSuccessfulPayPalResponse() {
        return PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK,
                                                    "TOKEN=EC%2d5YJ748178G052312W&TIMESTAMP=2011%2d12%2d23T19%3a40%3a07Z&CORRELATIONID=80d5883fa4b48&ACK=Success&VERSION=80%2e0&BUILD=2271164");
    }

    private BasicResponse getSuccessfulBillingAgreementResponse() {
        return PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK, "BILLINGAGREEMENTID=" + billiningAgreement +
                                                                               "&TIMESTAMP=2011%2d12%2d23T19%3a58%3a42Z&CORRELATIONID=d5f539505d03a&ACK=Success&VERSION=80%2e0&BUILD=2271164");
    }

    private BasicResponse getSuccessfulReferenceTransactionResponse() {
        return PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK, "BILLINGAGREEMENTID=" + billiningAgreement +
                                                                               "&TIMESTAMP=2011%2d12%2d23T19%3a58%3a42Z&CORRELATIONID=d5f539505d03a&ACK=Success&VERSION=80%2e0&BUILD=2271164");
    }

    private BasicResponse getFailResponse() {
        return PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK,
                                                    "TIMESTAMP=2011%2d12%2d26T14%3a08%3a40Z&CORRELATIONID=ca2c7bf39327f&ACK=Failure&VERSION=0%2e000000&BUILD=2271164&L_ERRORCODE0=10002" +
                                                    "&L_SHORTMESSAGE0=Authentication%2fAuthorization%20Failed&L_LONGMESSAGE0=You%20do%20not%20have%20permissions%20to%20make%20this%20API" +
                                                    "%20call" +
                                                    "&L_SEVERITYCODE0=Error");
    }
}