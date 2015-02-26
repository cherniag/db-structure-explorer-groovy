package mobi.nowtechnologies.server.service.pincode;

import mobi.nowtechnologies.server.persistence.domain.PinCode;
import mobi.nowtechnologies.server.persistence.domain.User;

/**
 * @author Anton Zemliankin
 */
public interface PinCodeService {

    PinCode generate(User user, int digits) throws MaxGenerationReachedException;

    boolean attempt(User user, String pinCode) throws MaxAttemptsReachedException;
}
