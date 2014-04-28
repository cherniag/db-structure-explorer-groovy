package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.google.api.userinfo.GoogleUserInfo;

public interface UserPromoService {

    User applyInitPromoByEmail(User user, Long activationEmailId, String email, String token);


    User applyInitPromoByFacebook(User user, FacebookProfile facebookProfile);

    User applyInitPromoByGooglePlus(User user, GoogleUserInfo googleUserInfo);
}
