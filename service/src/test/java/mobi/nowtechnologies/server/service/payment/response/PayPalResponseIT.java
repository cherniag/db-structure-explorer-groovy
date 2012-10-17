package mobi.nowtechnologies.server.service.payment.response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import mobi.nowtechnologies.server.service.payment.http.PayPalHttpService;
import mobi.nowtechnologies.server.service.payment.request.PayPalRequest;
import mobi.nowtechnologies.server.shared.service.PostService;

import org.junit.Before;
import org.junit.Test;

public class PayPalResponseIT {
	
	private PostService httpService;
	PayPalRequest request;
	private PayPalHttpService service;
	
	@Before
	public void before() {
		httpService = new PostService();			
			
		request = new PayPalRequest();
			request.setUser("cn_1313656118_biz_api1.chartsnow.mobi");
			request.setPassword("1313656158");
			request.setSignature("AoTXEljMZhDQEXJFn1kQJo2C6CbIAPP7uCi4Y-85yG98nlcq-IJBt9jQ");
			request.setApiVersion("80.0");
			request.setAmountFormat("###,###.00");
			request.setBtnSource("PP-ECWizard");
			request.setReturnURL("http://localhost:8080/portal/payPalRequest.htm");
			request.setCancelURL("http://localhost:8080/portal/payPalCancel.htm");
		
		service = new PayPalHttpService();
			service.setPostService(httpService);
			service.setApiUrl("https://api-3t.sandbox.paypal.com/nvp");
			service.setRequest(request);
	}
	
	@Test
	public void creatingToken() {
		
		PayPalResponse makeTokenResponse = service.makeTokenRequest("Information for paypal page", null, null, "GBP");
		assertNotNull(makeTokenResponse.getToken());
		assertEquals(true, makeTokenResponse.isSuccessful());
	}
	
	public void makingPayPalPayment() {
		
		
	}
}