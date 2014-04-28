package mobi.nowtechnologies.server.service.social.core;

import mobi.nowtechnologies.server.service.exception.ServiceException;

/**
 * Created by oar on 2/17/14.
 */
public class OAuth2ForbiddenException extends ServiceException {

    public OAuth2ForbiddenException(String code, String defaultMessage) {
        super(code, defaultMessage, defaultMessage);
    }

}
