package mobi.nowtechnologies.server.service.payment.response;

import org.springframework.util.StringUtils;

import mobi.nowtechnologies.server.shared.service.PostService.Response;

public abstract class PaymentSystemResponse {
	
	protected boolean isSuccessful;
	protected String descriptionError;
	protected int httpStatus;
	protected String message;
	protected String errorCode;
	
	public PaymentSystemResponse(Response response) {
		httpStatus = response.getStatusCode();
		if (StringUtils.hasLength(response.getMessage()) && response.getMessage().length()>255)
			message = response.getMessage().substring(0, 254);
		else
			message = response.getMessage();
		descriptionError="";
	}

	public boolean isSuccessful() {
		return isSuccessful;
	}
	
	public String getDescriptionError() {
		return descriptionError;
	}
	
	public int getHttpStatus() {
		return httpStatus;
	}

	public String getMessage() {
		return message;
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public String toString() {
		return "httpStatus=" + httpStatus + ", errorCode=" + errorCode + ", descriptionError=" + descriptionError + ", isSuccessful=" + isSuccessful + ", message=" + message;
	}
}