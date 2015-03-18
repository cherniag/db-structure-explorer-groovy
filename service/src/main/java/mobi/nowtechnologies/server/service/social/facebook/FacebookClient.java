package mobi.nowtechnologies.server.service.social.facebook;

import mobi.nowtechnologies.server.persistence.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.service.social.core.OAuth2ForbiddenException;
import mobi.nowtechnologies.server.service.social.facebook.impl.FacebookProfileImage;

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
