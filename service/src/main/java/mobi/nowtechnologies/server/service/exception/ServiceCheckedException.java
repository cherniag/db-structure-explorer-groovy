package mobi.nowtechnologies.server.service.exception;

/**
 * @author Titov Mykhaylo (titov)
 */
public class ServiceCheckedException extends Exception {

    private static final long serialVersionUID = 929513262767303130L;

    private String errorCodeForMessageLocalization;
    private String defaultMessage;

    public ServiceCheckedException(String code, String defaultMessage) {
        super(defaultMessage);
        this.errorCodeForMessageLocalization = code;
        this.defaultMessage = defaultMessage;
    }

    public ServiceCheckedException(String code, String defaultMessage, Exception e) {
        super(defaultMessage, e);
        this.errorCodeForMessageLocalization = code;
        this.defaultMessage = defaultMessage;
    }

    public String getErrorCodeForMessageLocalization() {
        return errorCodeForMessageLocalization;
    }

    public void setErrorCodeForMessageLocalization(String errorCodeForMessageLocalization) {
        this.errorCodeForMessageLocalization = errorCodeForMessageLocalization;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String toString() {
        return "ServiceCheckedException [defaultMessage=" + defaultMessage + ", errorCodeForMessageLocalization=" + errorCodeForMessageLocalization + ", " + super.toString() + "]";
    }

}
