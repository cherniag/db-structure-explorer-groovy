package mobi.nowtechnologies.server.service.social.facebook.impl;

import com.google.common.base.Strings;

import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.UserOperations;
import org.springframework.social.facebook.api.impl.FacebookTemplate;

public class FacebookOperationsAdaptor {

    public FacebookProfile getFacebookProfile(String accessToken, String userId) {
        UserOperations userOperations = new FacebookTemplate(accessToken).userOperations();
        return Strings.isNullOrEmpty(userId) ?
               userOperations.getUserProfile() :
               userOperations.getUserProfile(userId);
    }
}
