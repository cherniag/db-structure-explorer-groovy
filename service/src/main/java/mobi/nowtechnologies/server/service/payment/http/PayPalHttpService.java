package mobi.nowtechnologies.server.service.payment.http;

import mobi.nowtechnologies.server.service.payment.request.PayPalRequest;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;
import mobi.nowtechnologies.server.shared.service.BasicResponse;
import mobi.nowtechnologies.server.shared.service.PostService;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class PayPalHttpService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PayPalHttpService.class);

	private String apiUrl;

	private PayPalRequest request;
    private PostService httpService;

    public void setPostService(PostService httpService) {
        this.httpService = httpService;
    }

    public PostService getPostService() {
        return httpService;
    }

	/**
	 * Method requests SetExpressCheckout command to PayPal
	 * @param request - {@link PayPalRequest}
	 * @return Returns PayPal token for creating a billingAgreement Key
	 */
	protected PayPalResponse makeRequest(PayPalRequest request) {
		List<NameValuePair> params = request.build();
		LOGGER.info("PayPal http service making request with params: {}", params);
		BasicResponse response = httpService.sendHttpPost(apiUrl, params, null);
		LOGGER.info("PayPal http service get response: {}", response);
		return new PayPalResponse(response);
	}
	
	public PayPalResponse makeTokenRequest(String billingAgreementDescription, String successUrl, String failUrl, String currencyCode, String communityRewriteUrlParameter) {
		LOGGER.info("Getting token for billing agreement...");
		return makeRequest(request.createTokenRequest(billingAgreementDescription, successUrl, failUrl, currencyCode, communityRewriteUrlParameter));
	}
	
	public PayPalResponse makeBillingAgreementRequest(String token, String communityRewriteUrlParameter) {
		LOGGER.info("Getting billing agreement...");
		return makeRequest(request.createBillingAgreementRequest(token, communityRewriteUrlParameter));
	}
	
	public PayPalResponse makeReferenceTransactionRequest(String billingAgreementTxId, String currencyCode, BigDecimal amount, String communityRewriteUrlParameter) {
		LOGGER.info("Making reference tx with billing agreement...");
		return makeRequest(request.createReferenceTransactionRequest(billingAgreementTxId, currencyCode, amount, communityRewriteUrlParameter));
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public void setRequest(PayPalRequest request) {
		this.request = request;
	}
}