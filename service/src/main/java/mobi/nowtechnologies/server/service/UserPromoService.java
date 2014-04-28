package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import org.springframework.social.google.api.userinfo.GoogleUserInfo;

public interface UserPromoService {

    User applyInitPromoByEmail(User user, Long activationEmailId, String email, String token);


    User applyInitPromoByFacebook(User user, FacebookUserInfo facebookProfile);

    User applyInitPromoByGooglePlus(User user, GoogleUserInfo googleUserInfo);
}
