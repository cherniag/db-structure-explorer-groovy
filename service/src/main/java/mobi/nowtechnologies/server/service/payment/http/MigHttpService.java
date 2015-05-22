package mobi.nowtechnologies.server.service.payment.http;

import mobi.nowtechnologies.server.service.payment.request.MigRequest;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.service.sms.SMSGatewayService;
import mobi.nowtechnologies.server.support.http.BasicResponse;
import mobi.nowtechnologies.server.support.http.PostService;

import java.util.List;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigHttpService implements SMSGatewayService<MigResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MigHttpService.class);

    private String freeSMSURL;
    private String premiumSMSURL;
    private String otaUrl;

    private MigRequest request;

    private Integer timeToLiveMin;

    private PostService httpService;

    public PostService getPostService() {
        return httpService;
    }

    public void setPostService(PostService httpService) {
        this.httpService = httpService;
    }

    public MigResponse makeFreeSMSRequest(String numbers, String message) {
        List<NameValuePair> nameValuePairs = request.createFreeSMSRequest(numbers, message).build();
        return makeMigRequest(nameValuePairs, freeSMSURL);
    }

    @Override
    public MigResponse send(String numbers, String message, String title) {
        return makeFreeSMSRequest(numbers, message, title);
    }

    public MigResponse makeFreeSMSRequest(String numbers, String message, String title) {
        List<NameValuePair> nameValuePairs = request.createFreeSMSRequest(numbers, message, title).build();
        return makeMigRequest(nameValuePairs, freeSMSURL);
    }

    private MigResponse makeMigRequest(List<NameValuePair> nameValuePairs, String url) {
        LOGGER.info("Mig request for free sms {}", nameValuePairs);
        BasicResponse response = httpService.sendHttpPost(url, nameValuePairs, null);
        LOGGER.info("Mig response for free sms {}", response);
        return new MigResponse(response);
    }


    public MigResponse makePremiumSMSRequest(String messageId, String oadc, String numbers, String message) {
        List<NameValuePair> nameValuePairs = request.createPremiumSMSRequest(messageId, oadc, numbers, message, timeToLiveMin.toString()).build();
        LOGGER.info("Mig request for premium sms {}", nameValuePairs);
        BasicResponse response = httpService.sendHttpPost(premiumSMSURL, nameValuePairs, null);
        LOGGER.info("Mig response for premium sms {}", response);
        return new MigResponse(response);
    }

    public void setFreeSMSURL(String freeSMSURL) {
        this.freeSMSURL = freeSMSURL;
    }

    public void setPremiumSMSURL(String premiumSMSURL) {
        this.premiumSMSURL = premiumSMSURL;
    }

    public void setRequest(MigRequest request) {
        this.request = request;
    }

    public Integer getTimeToLiveMin() {
        return timeToLiveMin;
    }

    public void setTimeToLiveMin(Integer timeToLiveMin) {
        this.timeToLiveMin = timeToLiveMin;
    }

    public String getOtaUrl() {
        return otaUrl;
    }

    public void setOtaUrl(String otaUrl) {
        this.otaUrl = otaUrl;
    }
}