package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;

/**
 * User: Alexsandr_Kolpakov Date: 10/2/13 Time: 10:09 AM
 */
public interface MobileProviderService {

    PhoneNumberValidationData validatePhoneNumber(String phoneNumber) throws InvalidPhoneNumberException;

    void getSubscriberData(String phoneNumber);
}
