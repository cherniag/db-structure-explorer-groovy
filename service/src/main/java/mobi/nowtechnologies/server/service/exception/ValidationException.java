package mobi.nowtechnologies.server.service.exception;

import mobi.nowtechnologies.common.util.ServerMessage;

/**
 * @author Titov Mykhaylo (titov)
 */
public class ValidationException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(ServerMessage message) {
        super(message);
    }

    public static ValidationException getInstance(String errorCodeForMessageLocalization) {
        ValidationException validationException = new ValidationException((String) null);
        validationException.errorCodeForMessageLocalization = errorCodeForMessageLocalization;
        return validationException;
    }
}
