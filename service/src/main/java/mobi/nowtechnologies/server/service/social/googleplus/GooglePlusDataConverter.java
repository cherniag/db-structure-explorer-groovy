package mobi.nowtechnologies.server.service.social.googleplus;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import org.springframework.social.google.api.userinfo.GoogleUserInfo;

/**
 * Created by oar on 4/28/2014.
 */
public class GooglePlusDataConverter {

    public GooglePlusUserInfo convertForUser(User user, GoogleUserInfo profile) {
        GooglePlusUserInfo result = new GooglePlusUserInfo();
        result.setEmail(profile.getEmail());
        result.setGooglePlusId(profile.getId());
        result.setFirstName(profile.getFirstName());
        result.setSurname(profile.getLastName());
        result.setPicture(profile.getProfilePictureUrl());
        result.setUser(user);
        return result;
    }
}
