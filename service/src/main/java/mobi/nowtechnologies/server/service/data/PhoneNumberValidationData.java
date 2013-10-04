package mobi.nowtechnologies.server.service.data;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 10/2/13
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class PhoneNumberValidationData {
    private String phoneNumber;
    private String pin;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public PhoneNumberValidationData withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;

        return this;
    }

    public PhoneNumberValidationData withPin(String pin) {
        this.pin = pin;

        return this;
    }
}
