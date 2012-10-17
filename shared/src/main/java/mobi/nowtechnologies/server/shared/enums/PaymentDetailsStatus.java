package mobi.nowtechnologies.server.shared.enums;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public enum PaymentDetailsStatus {
	AWAITING,
	PENDING,
	SUCCESSFUL,
	ERROR,
	EXTERNAL_ERROR,
	NONE;
	
	public static PaymentDetailsStatus getErrorStatus(int httpRespponse) {
		return (httpRespponse != HttpServletResponse.SC_OK)?EXTERNAL_ERROR:ERROR;
	}
}