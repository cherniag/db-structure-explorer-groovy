package mobi.nowtechnologies.server.service.payment.response;

import mobi.nowtechnologies.server.service.payment.http.PayPalHttpService;
import mobi.nowtechnologies.server.service.payment.request.PayPalRequest;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.support.http.PostService;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@Ignore
public class PayPalResponseIT {

    PayPalRequest request;
    private PostService httpService;
    private PayPalHttpService service;
    private CommunityResourceBundleMessageSource messageSource = mock(CommunityResourceBundleMessageSource.class);

    @Before
    public void before() {
        initMocks();
        initService();
    }

    private void initService() {
        httpService = new PostService();
        request = new PayPalRequest();
        request.setCommunityResourceBundleMessageSource(messageSource);
        request.setAmountFormat("###,###.00");
        service = new PayPalHttpService();
        service.setPostService(httpService);
        service.setApiUrl("https://api-3t.sandbox.paypal.com/nvp");
        service.setRequest(request);
    }

    @Test
    public void creatingToken() {
        PayPalResponse makeTokenResponse = service.getTokenForRecurrentType(null, null, "GBP", null, "Information for paypal page");
        assertNotNull(makeTokenResponse.getToken());
        assertEquals(true, makeTokenResponse.isSuccessful());
    }

    private void initMocks() {
        Map<String, String> credentialMap = new HashMap<String, String>();
        credentialMap.put("paypal.user", "cn_1313656118_biz_api1.chartsnow.mobi");
        credentialMap.put("paypal.password", "1313656158");
        credentialMap.put("paypal.signature", "AoTXEljMZhDQEXJFn1kQJo2C6CbIAPP7uCi4Y-85yG98nlcq-IJBt9jQ");
        credentialMap.put("paypal.apiVersion", "80.0");
        credentialMap.put("paypal.btnSource", "PP-ECWizard");
        credentialMap.put("paypal.returnURL", "http://localhost:8080/portal/payPalRequest.htm");
        credentialMap.put("paypal.cancelURL", "http://localhost:8080/portal/payPalCancel.htm");
        credentialMap.put("paypal.lBillingType0", "MerchantInitiatedBillingSingleAgreement");
        for (Map.Entry<String, String> entry : credentialMap.entrySet()) {
            when(messageSource.getMessage(anyString(), eq(entry.getKey()), any(Object[].class), any(Locale.class))).thenReturn(entry.getValue());
        }
    }

}