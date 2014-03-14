package mobi.nowtechnologies.server.service.facebook.exception;

import mobi.nowtechnologies.server.service.exception.ServiceException;

/**
 * Created by oar on 2/17/14.
 */
public class FacebookForbiddenException extends ServiceException {

    public FacebookForbiddenException(String code, String defaultMessage) {
        super(code, defaultMessage, defaultMessage);
    }

}
