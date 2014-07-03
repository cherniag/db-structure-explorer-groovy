package mobi.nowtechnologies.server.service.social.facebook;

import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;

public interface FacebookServiceInterface {
    FacebookUserInfo getAndValidateFacebookProfile(String facebookAccessToken, String inputFacebookId);
}
