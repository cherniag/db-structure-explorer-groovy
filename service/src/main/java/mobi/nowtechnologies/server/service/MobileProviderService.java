package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.data.SubscriberData;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;
import mobi.nowtechnologies.server.shared.Processor;

/**
 * User: Alexsandr_Kolpakov Date: 10/2/13 Time: 10:09 AM
 */
public interface MobileProviderService<T extends SubscriberData> {

    PhoneNumberValidationData validatePhoneNumber(String phoneNumber) throws InvalidPhoneNumberException;

    void getSubscriberData(String phoneNumber, Processor<T> processor);
}
