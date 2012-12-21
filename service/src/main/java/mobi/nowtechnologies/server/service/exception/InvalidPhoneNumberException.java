package mobi.nowtechnologies.server.service.exception;

import org.apache.commons.collections.set.CompositeSet.SetMutator;

public class InvalidPhoneNumberException extends ServiceException{
	private static final long serialVersionUID = -7687345560695115391L;

	public InvalidPhoneNumberException() {
		super("601", "Invalid phone number format", "phone.number.invalid.format");
	}
}
