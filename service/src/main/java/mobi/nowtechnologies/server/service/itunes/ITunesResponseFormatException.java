package mobi.nowtechnologies.server.service.itunes;

/**
 * Author: Gennadii Cherniaiev Date: 4/20/2015
 */
public class ITunesResponseFormatException extends Exception {

    public ITunesResponseFormatException(String message) {
        super(message);
    }

    public ITunesResponseFormatException(Throwable e) {
        super(e);
    }
}
