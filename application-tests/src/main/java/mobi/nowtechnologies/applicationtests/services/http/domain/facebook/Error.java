package mobi.nowtechnologies.applicationtests.services.http.domain.facebook;

/**
 * @author kots
 * @since 8/21/2014.
 */
public class Error {
    private String displayMessage;
    private String message;
    private String errorCode;

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
