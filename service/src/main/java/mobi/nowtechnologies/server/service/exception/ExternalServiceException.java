package mobi.nowtechnologies.server.service.exception;


/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class ExternalServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	
	public ExternalServiceException(String message) {
		super(message);
	}

	public ExternalServiceException(String message, Throwable e) {
		super(message, e);
	}

	public ExternalServiceException(String code, String defaultMessage) {
		super(code, defaultMessage);
	}

    public ExternalServiceException(String message, Throwable e) {
        super(message, e);
    }

	@Override
	public String toString() {
		return "ExternalServiceException [toString()=" + super.toString() + "]";
	}

}
