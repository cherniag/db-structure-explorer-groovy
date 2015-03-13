package mobi.nowtechnologies.server.service.itunes;

import com.jayway.jsonpath.InvalidPathException;

/**
 * Author: Gennadii Cherniaiev Date: 1/6/2015
 */
public class ITunesResponseParserException extends RuntimeException {

    public ITunesResponseParserException(InvalidPathException e) {
        super(e);
    }
}
