package mobi.nowtechnologies.server.service.exception;

import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

/**
 * UserCredentialsException
 * 
 * @author Maksym Chernolevskyi (maksym)
 */
public class ActivationStatusException extends ServiceException {
    private static final long serialVersionUID = -7687345560695115391L;

    public ActivationStatusException(ActivationStatus curActivationStatus,ActivationStatus mustActivationStatus) {
        this("604", "User activation status ["+curActivationStatus+"] is invalid. User must have status ["+mustActivationStatus+"]", "error.604.activation.status."+curActivationStatus+".must."+mustActivationStatus);
    }

    protected ActivationStatusException(String code, String defaultMessage, String message) {
        super(code, defaultMessage, message);
    }
	
}