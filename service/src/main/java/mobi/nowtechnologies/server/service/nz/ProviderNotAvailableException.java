package mobi.nowtechnologies.server.service.nz;

import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;

/**
 * @author Anton Zemliankin
 */
public class ProviderNotAvailableException extends ServiceCheckedException {

    public ProviderNotAvailableException(String msg, Exception cause) {
        super(null, msg, cause);
    }

    public ProviderNotAvailableException(String msg) {
        super(null, msg);
    }

}
