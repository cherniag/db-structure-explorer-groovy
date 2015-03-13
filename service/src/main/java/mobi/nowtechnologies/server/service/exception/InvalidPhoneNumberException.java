package mobi.nowtechnologies.server.service.exception;


public class InvalidPhoneNumberException extends ServiceException {

    private static final long serialVersionUID = -7687345560695115391L;

    public InvalidPhoneNumberException(String phoneNumber) {
        super("601", "Invalid phone number format: " + phoneNumber, "phone.number.invalid.format", new Object[] {phoneNumber});
    }

    protected InvalidPhoneNumberException(String code, String defaultMessage, String message) {
        super(code, defaultMessage, message);
    }
}
