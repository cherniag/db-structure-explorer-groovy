package mobi.nowtechnologies.server.service.exception;

import org.springframework.ws.client.WebServiceFaultException;

/**
 * @author Anton Zemliankin
 */
public class ServiceNotAvailableException extends Exception {

    public ServiceNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
