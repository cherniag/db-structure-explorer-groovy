package mobi.nowtechnologies.server.service.payment.http;

import mobi.nowtechnologies.server.service.payment.request.PayPalRequest;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;
import mobi.nowtechnologies.server.support.http.BasicResponse;
import mobi.nowtechnologies.server.support.http.PostService;

import java.math.BigDecimal;
import java.util.List;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayPalHttpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayPalHttpService.class);

    private String apiUrl;

    private PayPalRequest request;
    private PostService httpService;

    public void setPostService(PostService httpService) {
        this.httpService = httpService;
    }

    //
    // Recurrent payment
    //
    public PayPalResponse getTokenForRecurrentType(String successUrl, String failUrl, String currencyCode, String communityRewriteUrl, String billingAgreementDescription) {
        LOGGER.info("Getting token for billing agreement...");
        return makeRequest(request.createBillingAgreementTokenRequest(successUrl, failUrl, currencyCode, communityRewriteUrl, billingAgreementDescription));
    }

    public PayPalResponse getPaymentDetailsInfoForRecurrentType(String token, String communityRewriteUrl) {
        LOGGER.info("Getting billing agreement...");
        return makeRequest(request.createBillingAgreementRequest(token, communityRewriteUrl));
    }

    public PayPalResponse makePaymentForRecurrentType(String billingAgreementTxId, String currencyCode, BigDecimal amount, String communityRewriteUrl) {
        LOGGER.info("Making reference tx with billing agreement...");
        return makeRequest(request.createReferenceTransactionRequest(billingAgreementTxId, currencyCode, amount, communityRewriteUrl));
    }

    //
    // Onetime payment (Pay with credit Card available link) type
    //
    public PayPalResponse getTokenForOnetimeType(String successUrl, String failUrl, String communityRewriteUrl, String currencyCode, BigDecimal amount) {
        LOGGER.info("Getting token for onetime payment...");
        return makeRequest(request.createOnetimePaymentTokenRequest(successUrl, failUrl, currencyCode, communityRewriteUrl, amount));
    }

    public PayPalResponse getPaymentDetailsInfoForOnetimeType(String token, String communityRewriteUrl) {
        LOGGER.info("Getting checkout details...");
        return makeRequest(request.createCheckoutDetailsRequest(token, communityRewriteUrl));
    }

    public PayPalResponse makePaymentForOnetimeType(String token, String communityRewriteUrl, String payerId, String currencyCode, BigDecimal amount) {
        LOGGER.info("Making do payment request...");
        return makeRequest(request.createDoPaymentRequest(token, payerId, currencyCode, amount, communityRewriteUrl));
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void setRequest(PayPalRequest request) {
        this.request = request;
    }

    /**
     * Method requests SetExpressCheckout command to PayPal
     *
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
}