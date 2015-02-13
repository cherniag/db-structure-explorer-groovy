package mobi.nowtechnologies.server.service.pincode;

import mobi.nowtechnologies.server.persistence.domain.User;

/**
 * @author Anton Zemliankin
 */

public interface PinCodeSmsService {

    boolean sendPinCode(User user, String msisdn, String pinCode);

}
