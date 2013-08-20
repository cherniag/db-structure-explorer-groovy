/**
 * 
 */
package mobi.nowtechnologies.server.persistence.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

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

    public ErrorMessage addErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
        return this;
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
