package mobi.nowtechnologies.server.service.payment.http;

import java.util.List;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mobi.nowtechnologies.server.service.payment.request.MigRequest;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.shared.service.PostService.Response;

public class MigHttpService extends PaymentHttpService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MigHttpService.class);
	
	private String freeSMSURL;
	private String premiumSMSURL;
	private String otaUrl;
	
	private MigRequest request;
	
	private Integer timeToLiveMin;
	
	public MigResponse makeFreeSMSRequest(String numbers, String message) {
		List<NameValuePair> nameValuePairs = request.createFreeSMSRequest(numbers, message).build();
		LOGGER.info("Mig request for free sms {}", nameValuePairs);
		Response response = httpService.sendHttpPost(freeSMSURL, nameValuePairs, null);
		LOGGER.info("Mig response for free sms {}", response);
		return new MigResponse(response);
	}
	
	public MigResponse makePremiumSMSRequest(String messageId, String oadc, String numbers, String message ) {
		List<NameValuePair> nameValuePairs = request.createPremiumSMSRequest(messageId, oadc, numbers, message, timeToLiveMin.toString()).build();
		LOGGER.info("Mig request for premium sms {}", nameValuePairs);
		Response response = httpService.sendHttpPost(premiumSMSURL, nameValuePairs, null);
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

	public void setOtaUrl(String otaUrl) {
		this.otaUrl = otaUrl;
	}

	public String getOtaUrl() {
		return otaUrl;
	}
}