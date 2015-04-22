package mobi.nowtechnologies.server.service.itunes;

/**
 * Author: Gennadii Cherniaiev Date: 4/20/2015
 */
public class ITunesConnectionException extends Exception{

    public ITunesConnectionException(Throwable cause) {
        super(cause);
    }

    public ITunesConnectionException(String message) {
        super(message);
    }
}
