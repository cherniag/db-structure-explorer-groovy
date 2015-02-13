package mobi.nowtechnologies.server.service.exception;

/**
 * @author Anton Zemliankin
 */

public class PinCodeException {

    //Pin code not found or has been expired
    public static class NotFound extends Exception {
        public NotFound(String msg) {
            super(msg);
        }
    }

    //User has reached max attempts
    public static class MaxAttemptsReached extends Exception {
        public MaxAttemptsReached(String msg) {
            super(msg);
        }
    }

    //User has reached max pin codes per period
    public static class MaxPinCodesReached extends Exception {
        public MaxPinCodesReached(String msg) {
            super(msg);
        }
    }

}
