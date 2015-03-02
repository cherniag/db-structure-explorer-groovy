package mobi.nowtechnologies.server.service.nz;

import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;

/**
 * @author Anton Zemliankin
 */
public class MsisdnNotFoundException extends ServiceCheckedException {

    public MsisdnNotFoundException(String msg, Exception cause) {
        super(null, msg, cause);
    }

    public MsisdnNotFoundException(String msg) {
        super(null, msg);
    }

}