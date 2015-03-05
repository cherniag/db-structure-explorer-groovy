package mobi.nowtechnologies.server.service.social.facebook;

import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FacebookService {

    private static Logger log = LoggerFactory.getLogger(FacebookService.class);

    @Resource
    FacebookClient facebookClient;

    String userId;

    public FacebookUserInfo getFacebookUserInfo(String accessToken, String inputFacebookId) {
        FacebookUserInfo facebookProfileInfo = facebookClient.getProfileUserInfo(accessToken, userId);
        if (!facebookProfileInfo.getFacebookId().equals(inputFacebookId)) {
            log.warn("inputFacebookId should match id on Facebook!");
            throw FacebookClient.INVALID_FACEBOOK_USER_ID;
        }
        return facebookProfileInfo;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
