package mobi.nowtechnologies.server.user.criteria;

/**
 * Author: Gennadii Cherniaiev Date: 4/9/2014
 */
public class MatchException extends RuntimeException {

    public MatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatchException(String message) {
        super(message);
    }

    public MatchException(Throwable cause) {
        super(cause);
    }
}
