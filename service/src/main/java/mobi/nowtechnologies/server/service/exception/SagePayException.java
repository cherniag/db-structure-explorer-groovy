package mobi.nowtechnologies.server.service.exception;

import mobi.nowtechnologies.server.persistence.domain.Payment;

/**
 * SagePayException
 * 
 * @author Maksym Chernolevskyi (maksym)
 */
public class SagePayException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	private Payment failedPayment;
	
	private int httpStatusCode;
	
	public Payment getFailedPayment() {
		return failedPayment;
	}

	public void setFailedPayment(Payment failedPayment) {
		this.failedPayment = failedPayment;
	}

	public SagePayException(String message) {
		super(message);
	}

	public SagePayException(String message, Throwable e) {
		super(message, e);
	}
	
	public SagePayException(String message, Payment failedPayment) {
		super(message);
		this.failedPayment = failedPayment;
	}
	
	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	
	public int getHttpStatusCode() {
		return httpStatusCode;
	}
	
}
