/**
 * 
 */
package mobi.nowtechnologies.server.persistence.domain;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@XmlRootElement(name="errorMessage")
public class ErrorMessage {
	private String displayMessage;
	private String message;
	private Integer errorCode;
	private Map<String,String> parammeters;


	public Map<String,String> getParammeters() {
		return parammeters;
	}

	public void setParammeters(Map<String,String> parammeters) {
		this.parammeters = parammeters;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDisplayMessage() {
		return displayMessage;
	}

	public void setDisplayMessage(String displayMessage) {
		this.displayMessage = displayMessage;
	}

	@Override
	public String toString() {
		return "ErrorMessage [displayMessage=" + displayMessage + ", errorCode=" + errorCode + ", message=" + message + ", parammeters=" + parammeters + "]";
	}
}
