package mobi.nowtechnologies.server.service.exception;

/**
 * @author Titov Mykhaylo (titov)
 */
public class ValidationException extends ServiceException {
    public ValidationException(String message) {
        super(message);
    }

    public static ValidationException getInstance(String errorCodeForMessageLocalization) {
        ValidationException validationException = new ValidationException((String) null);
        validationException.errorCodeForMessageLocalization = errorCodeForMessageLocalization;
        return validationException;
    }
}
