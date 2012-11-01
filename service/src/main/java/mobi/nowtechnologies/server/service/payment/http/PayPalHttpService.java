package mobi.nowtechnologies.server.service.payment.http;

import java.math.BigDecimal;
import java.util.List;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mobi.nowtechnologies.server.service.payment.request.PayPalRequest;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;
import mobi.nowtechnologies.server.shared.service.PostService.Response;

public class PayPalHttpService extends PaymentHttpService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PayPalHttpService.class);
	
	private String apiUrl;
	
	private PayPalRequest request;
	/**
	 * Method requests SetExpressCheckout command to PayPal
	 * @param request - {@link PayPalRequest}
	 * @return Returns PayPal token for creating a billingAgreement Key
	 */
	protected PayPalResponse makeRequest(PayPalRequest request) {
		List<NameValuePair> params = request.build();
		LOGGER.info("PayPal http service making request with params: {}", params);
		Response response = httpService.sendHttpPost(apiUrl, params, null);
		LOGGER.info("PayPal http service get response: {}", response);
		return new PayPalResponse(response);
	}
	
	public PayPalResponse makeTokenRequest(String billingAgreementDescription, String successUrl, String failUrl, String currencyCode) {
		LOGGER.info("Getting token for billing agreement...");
		return makeRequest(request.createTokenRequest(billingAgreementDescription, successUrl, failUrl, currencyCode));
	}
	
	public PayPalResponse makeBillingAgreementRequest(String token) {
		LOGGER.info("Getting billing agreement...");
		return makeRequest(request.createBillingAgreementRequest(token));
	}
	
	public PayPalResponse makeReferenceTransactionRequest(String billingAgeementTxId, String currencyCode, BigDecimal amount) {
		LOGGER.info("Making reference tx with billing agreement...");
		return makeRequest(request.createReferenceTransactionRequest(billingAgeementTxId, currencyCode, amount));
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public void setRequest(PayPalRequest request) {
		this.request = request;
	}
}