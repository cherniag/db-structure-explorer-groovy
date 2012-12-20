package mobi.nowtechnologies.server.service.exception;

public class InvalidPhoneNumberException extends ServiceException{
	private static final long serialVersionUID = -7687345560695115391L;

	public InvalidPhoneNumberException() {
		super("Invalid phone number.");
	}
}
