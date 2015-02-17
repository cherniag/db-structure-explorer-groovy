package mobi.nowtechnologies.server.service.exception;

/**
 * @author Anton Zemliankin
 */
public class SubscriberServiceException{

    //Msisdn not found in subscriber service
    public static class MSISDNNotFound extends Exception {
        public MSISDNNotFound(String msg, Throwable cause) {
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
