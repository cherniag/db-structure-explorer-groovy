package mobi.nowtechnologies.server.service.pincode;

import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;

/**
 * @author Anton Zemliankin
 */
public class MaxAttemptsReachedException extends ServiceCheckedException {

    public MaxAttemptsReachedException(String msg) {
        super(null, msg);
    }

}
