package mobi.nowtechnologies.server.service.payment.http;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.server.service.payment.request.SagePayRequest;
import mobi.nowtechnologies.server.service.payment.response.SagePayResponse;
import mobi.nowtechnologies.server.support.http.BasicResponse;
import mobi.nowtechnologies.server.support.http.PostService;

import java.math.BigDecimal;
import java.util.List;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SagePayHttpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagePayHttpService.class);

    private String deferUrl;
    private String releaseUrl;
    private String repeatUrl;

    private SagePayRequest request;
    private PostService httpService;

    public PostService getPostService() {
        return httpService;
    }

    public void setPostService(PostService httpService) {
        this.httpService = httpService;
    }

    protected SagePayResponse sendRequest(String url, List<NameValuePair> nameValuePairs) {
        LOGGER.debug("SagePay http request params {}", nameValuePairs);
        BasicResponse response = getPostService().sendHttpPost(url, nameValuePairs, null);
        LOGGER.info("SagePay http response params {}", response);
        return new SagePayResponse(response);
    }

    public SagePayResponse makeDeferRequest(PaymentDetailsDto paymentDto) {
        SagePayRequest deferRequest = request.createDeferRequest(paymentDto);
        LOGGER.info("SagePay making defer request...");
        return sendRequest(deferUrl, deferRequest.build());
    }

    public SagePayResponse makeReleaseRequest(String currencyISO, String description, String vpsTxId, String vendorTxCode, String securityKey, String txAuthNo, BigDecimal amount) {
        SagePayRequest releaseRequest = request.createReleaseRequest(currencyISO, description, vpsTxId, vendorTxCode, securityKey, txAuthNo, amount);
        LOGGER.info("SagePay making release request...");
        return sendRequest(releaseUrl, releaseRequest.build());
    }

    public SagePayResponse makeRepeatRequest(String currencyISO, String description, String vpsTxId, String vendorTxCode, String securityKey, String txAuthNo, String internalTxId, BigDecimal amount) {
        SagePayRequest repeatRequest = request.createRepeatRequest(currencyISO, description, vpsTxId, vendorTxCode, securityKey, txAuthNo, internalTxId, amount);
        LOGGER.info("SagePay making repeat request...");
        return sendRequest(repeatUrl, repeatRequest.build());
    }

    public SagePayResponse makePaymentRequest(PaymentDetailsDto paymentDto) {
        SagePayRequest paymentRequest = request.createPaymentRequest(paymentDto);
        return sendRequest(deferUrl, paymentRequest.build());
    }

    public void setDeferUrl(String deferUrl) {
        this.deferUrl = deferUrl;
    }

    public void setReleaseUrl(String releaseUrl) {
        this.releaseUrl = releaseUrl;
    }

    public void setRepeatUrl(String repeatUrl) {
        this.repeatUrl = repeatUrl;
    }

    public void setRequest(SagePayRequest request) {
        this.request = request;
    }
}