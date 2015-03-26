package mobi.nowtechnologies.server.service.exception;

import mobi.nowtechnologies.common.util.ServerMessage;

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
