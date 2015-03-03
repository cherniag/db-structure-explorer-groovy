package mobi.nowtechnologies.server.service.exception;


public class LimitPhoneNumberValidationException extends InvalidPhoneNumberException {

    private static final long serialVersionUID = -7687345560695115391L;

    private final String phoneNumber;

    private final String url;


    public LimitPhoneNumberValidationException(String phoneNumber, String url) {
        super("603", "Limit phone number validation", "phone.number.limit.validation");
        this.phoneNumber = phoneNumber;
        this.url = url;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUrl() {
        return url;
    }


}
