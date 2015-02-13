package mobi.nowtechnologies.server.service.exception;

import org.springframework.ws.client.WebServiceFaultException;

/**
 * @author Anton Zemliankin
 */
public class SubscriberServiceException{

    //Msisdn not found in subscriber service
    public static class MsisdnNotFound extends Exception {
        public MsisdnNotFound(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    //Service returned fault message
    public static class ServiceNotAvailable extends Exception {
        public ServiceNotAvailable(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

}
