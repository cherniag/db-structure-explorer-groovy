package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;

public interface UserPromoService {

    User applyInitPromoByEmail(User user, Long activationEmailId, String email, String token);


    User applyInitPromoByFacebook(User user, FacebookUserInfo facebookProfile);

    User applyInitPromoByGooglePlus(User user, GooglePlusUserInfo googleUserInfo);
}
