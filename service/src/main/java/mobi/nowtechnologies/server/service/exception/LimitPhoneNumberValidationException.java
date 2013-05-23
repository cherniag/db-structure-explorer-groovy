package mobi.nowtechnologies.server.service.exception;


public class LimitPhoneNumberValidationException extends InvalidPhoneNumberException{
	private static final long serialVersionUID = -7687345560695115391L;

	public LimitPhoneNumberValidationException() {
		super("603", "Limit phone number validation", "phone.number.limit.validation");
	}
}
