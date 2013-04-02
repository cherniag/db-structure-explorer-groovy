package mobi.nowtechnologies.server.service.payment.response;

import mobi.nowtechnologies.server.service.payment.request.SagePayRequest;
import mobi.nowtechnologies.server.shared.service.PostService.Response;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class SagePayResponse extends PaymentSystemResponse {
	private static final Logger LOGGER = LoggerFactory.getLogger(SagePayResponse.class);
	
	private Properties properties;
	
	@Deprecated 
	// TODO replace by isSuccessful
	private boolean sagePaySuccessful = false;
	
	public static final String RESPONSE_MATCHED_VALUE = "MATCHED";
	public static final String RESPONSE_ALL_MATCHED_VALUE = "ALL MATCH";
	
	public static enum MessageResponseParam {
		Success,
		StatusDetail,
		Status, AVSCV2, AddressResult, PostCodeResult, CV2Result
	}
	
	public static enum MessageResponseStatus {
		OK,
		REGISTERED,
		PENDING,
		FAIL,
		USER_CONFIRMED
	}

	public SagePayResponse(Response response) {
		super(response);

		String message = response.getMessage();

		properties = new Properties();
		try {
			properties.load(new StringReader(message));
			
			String status = properties.getProperty(MessageResponseParam.Status.toString());
			if (httpStatus != HttpStatus.SC_OK) {
				descriptionError = message;
			} else if (!MessageResponseStatus.OK.toString().equals(status)) {
				isSuccessful = false;
				String errorMessage = properties.getProperty(MessageResponseParam.StatusDetail.toString());
				descriptionError = StringUtils.hasText(errorMessage)?errorMessage:message;
			} else {
				isSuccessful = true;
				sagePaySuccessful = true;
			}
		} catch (IOException e) {
			LOGGER.error("Unable to parse response from SagePay server {}", message, e);
			descriptionError = "Unable to get response from external payment system. Please try again.";
		}		
	}

	@Deprecated
	public boolean isSagePaySuccessful() {
		return sagePaySuccessful;
	}
	
	public String getVPSTxId() {
		String property = properties.getProperty(SagePayRequest.SageRequestParam.VPSTxId.toString());
		return property==null?"":property;
	}
	
	public String getSecurityKey() {
		return properties.getProperty(SagePayRequest.SageRequestParam.SecurityKey.toString());
	}
	
	public String getTxAuthNo() {
		return properties.getProperty(SagePayRequest.SageRequestParam.TxAuthNo.toString());
	}
	
	public String getStatusDetail() {
		return properties.getProperty(MessageResponseParam.StatusDetail.toString());
	}
	
	public String getAVSCV2() {
		return properties.getProperty(MessageResponseParam.AVSCV2.toString());
	}
	
	public String getAddress() {
		return properties.getProperty(MessageResponseParam.AddressResult.toString());
	}
	
	public String getPostCode() {
		return properties.getProperty(MessageResponseParam.PostCodeResult.toString());
	}
	
	public String getCV2() {
		return properties.getProperty(MessageResponseParam.CV2Result.toString());
	}
}