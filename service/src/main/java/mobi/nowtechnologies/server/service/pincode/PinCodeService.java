package mobi.nowtechnologies.server.service.pincode;

import mobi.nowtechnologies.server.persistence.domain.PinCode;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.PinCodeException;

/**
 * @author Anton Zemliankin
 */
public interface PinCodeService {

    PinCode generate(User user, int digits) throws PinCodeException.MaxPinCodesReached;

    boolean check(User user, String pinCode) throws PinCodeException.NotFound, PinCodeException.MaxAttemptsReached;
}
