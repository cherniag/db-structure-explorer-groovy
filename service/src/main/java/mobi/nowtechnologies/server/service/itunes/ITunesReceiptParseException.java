package mobi.nowtechnologies.server.service.itunes;

import com.jayway.jsonpath.InvalidPathException;

/**
 * Author: Gennadii Cherniaiev
 * Date: 1/6/2015
 */
public class ITunesReceiptParseException extends RuntimeException {

    public ITunesReceiptParseException(InvalidPathException e) {
        super(e);
    }
}
