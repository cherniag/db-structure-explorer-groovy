package mobi.nowtechnologies.server.service.pincode;

import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;

/**
 * @author Anton Zemliankin
 */
public class MaxGenerationReachedException extends ServiceCheckedException {

    public MaxGenerationReachedException(String msg) {
        super(null, msg);
    }

}