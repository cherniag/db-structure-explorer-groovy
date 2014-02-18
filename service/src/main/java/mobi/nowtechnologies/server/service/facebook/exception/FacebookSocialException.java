package mobi.nowtechnologies.server.service.facebook.exception;

import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.facebook.FacebookConstants;

/**
 * Created by oar on 2/18/14.
 */
public class FacebookSocialException extends ServiceException {

    public FacebookSocialException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public String getErrorCodeForMessageLocalization() {
        return FacebookConstants.FACEBOOK_INVALID_TOKEN_ERROR_CODE;
    }

}
