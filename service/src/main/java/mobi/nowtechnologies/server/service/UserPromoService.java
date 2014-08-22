package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;

public interface UserPromoService {

    User applyInitPromoByEmail(User user, Long activationEmailId, String email, String token);


    User applyInitPromoByFacebook(User user, FacebookUserInfo facebookProfile, boolean disableReactivationForUser);
    
    User applyInitPromoByGooglePlus(User user, GooglePlusUserInfo googleUserInfo, boolean disableReactivationForUser);
    
}
