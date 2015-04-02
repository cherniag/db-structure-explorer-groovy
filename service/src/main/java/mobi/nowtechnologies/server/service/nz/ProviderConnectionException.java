package mobi.nowtechnologies.server.service.nz;

/**
 * Author: Gennadii Cherniaiev Date: 3/31/2015
 */
public class ProviderConnectionException extends ProviderNotAvailableException {

    public ProviderConnectionException(String msg, Exception cause) {
        super(msg, cause);
    }

    public ProviderConnectionException(String msg) {
        super(msg);
    }
}
