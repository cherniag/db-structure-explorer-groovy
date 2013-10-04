package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 10/2/13
 * Time: 10:09 AM
 * To change this template use File | Settings | File Templates.
 */
public interface MobileProviderService {
    PhoneNumberValidationData validatePhoneNumber(String phoneNumber) throws InvalidPhoneNumberException;

    mobi.nowtechnologies.server.service.data.SubsriberData getSubscriberData(String phoneNumber);
}
