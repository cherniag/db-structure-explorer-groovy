package mobi.nowtechnologies.server.service.social.facebook;

import mobi.nowtechnologies.server.persistence.social.SocialNetworkInfo;
import mobi.nowtechnologies.server.service.social.OAuth2ForbiddenException;

/**
 * Created by zam on 2/13/2015.
 */
public interface FacebookClient {

    String DATE_FORMAT = "MM/dd/yyyy";

    OAuth2ForbiddenException INVALID_FACEBOOK_TOKEN_EXCEPTION = OAuth2ForbiddenException.invalidFacebookToken();
    OAuth2ForbiddenException INVALID_FACEBOOK_USER_ID = OAuth2ForbiddenException.invalidFacebookUserId();

    SocialNetworkInfo getProfileUserInfo(String accessToken, String userId);

    FacebookProfileImage getProfileImage(String accessToken, String userId);
}
