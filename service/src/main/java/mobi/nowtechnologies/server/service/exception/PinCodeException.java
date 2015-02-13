package mobi.nowtechnologies.server.service.exception;

/**
 * @author Anton Zemliankin
 */

public class PinCodeException {

    //Pin code not found or has been expired
    public static class NotFound extends ServiceCheckedException {
        private static final long serialVersionUID = 269513262767376431L;

        public NotFound(String msg) {
            super("", msg);
        }
    }

    //User has reached max attempts
    public static class MaxAttemptsReached extends ServiceCheckedException {
        private static final long serialVersionUID = 911513288967303130L;

        public MaxAttemptsReached(String msg) {
            super("", msg);
        }
    }

    //User has reached max pin codes per period
    public static class MaxPinCodesReached extends ServiceCheckedException {
        private static final long serialVersionUID = 529513262767303269L;

        public MaxPinCodesReached(String msg) {
            super("", msg);
        }
    }

}
