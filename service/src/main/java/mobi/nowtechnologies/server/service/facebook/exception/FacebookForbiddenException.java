package mobi.nowtechnologies.server.service.facebook.exception;

import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.facebook.FacebookConstants;

/**
 * Created by oar on 2/17/14.
 */
public class FacebookForbiddenException extends ServiceException {

    public FacebookForbiddenException(String message) {
        super(message);
    }

    public String getErrorCodeForMessageLocalization() {
        return FacebookConstants.FACEBOOK_INVALID_USER_ID;
    }
}
