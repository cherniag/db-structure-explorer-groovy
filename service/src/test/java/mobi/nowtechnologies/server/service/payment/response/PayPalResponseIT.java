package mobi.nowtechnologies.server.service.payment.response;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import mobi.nowtechnologies.server.service.payment.http.PayPalHttpService;
import mobi.nowtechnologies.server.service.payment.request.PayPalRequest;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.service.PostService;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Locale;

public class PayPalResponseIT {
	
	private PostService httpService;
	PayPalRequest request;
	private PayPalHttpService service;
    private CommunityResourceBundleMessageSource messageSource = mock(CommunityResourceBundleMessageSource.class);

	@Before
	public void before() {
        initMocks();
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
		PayPalResponse makeTokenResponse = service.makeTokenRequest("Information for paypal page", null, null, "GBP", null);
		assertNotNull(makeTokenResponse.getToken());
		assertEquals(true, makeTokenResponse.isSuccessful());
	}

    private void initMocks() {
        when(messageSource.getMessage(anyString(),eq("paypal.user"), any(Object[].class),any(Locale.class))).thenReturn("cn_1313656118_biz_api1.chartsnow.mobi");
        when(messageSource.getMessage(anyString(),eq("paypal.password"), any(Object[].class),any(Locale.class))).thenReturn("1313656158");
        when(messageSource.getMessage(anyString(),eq("paypal.signature"), any(Object[].class),any(Locale.class))).thenReturn("AoTXEljMZhDQEXJFn1kQJo2C6CbIAPP7uCi4Y-85yG98nlcq-IJBt9jQ");
        when(messageSource.getMessage(anyString(),eq("paypal.apiVersion"), any(Object[].class),any(Locale.class))).thenReturn("80.0");
        when(messageSource.getMessage(anyString(),eq("paypal.btnSource"), any(Object[].class),any(Locale.class))).thenReturn("PP-ECWizard");
        when(messageSource.getMessage(anyString(),eq("paypal.returnURL"), any(Object[].class),any(Locale.class))).thenReturn("http://localhost:8080/portal/payPalRequest.htm");
        when(messageSource.getMessage(anyString(),eq("paypal.cancelURL"), any(Object[].class),any(Locale.class))).thenReturn("http://localhost:8080/portal/payPalCancel.htm");
        when(messageSource.getMessage(anyString(),eq("paypal.lBillingType0"), any(Object[].class),any(Locale.class))).thenReturn("MerchantInitiatedBillingSingleAgreement");
    }

}