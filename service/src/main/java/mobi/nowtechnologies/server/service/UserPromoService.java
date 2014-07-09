package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;

public interface UserPromoService {

    MergeResult applyInitPromoByEmail(User user, Long activationEmailId, String email, String token);


    MergeResult applyInitPromoByFacebook(User user, FacebookUserInfo facebookProfile, boolean disableReactivationForUser);

    MergeResult applyInitPromoByGooglePlus(User user, GooglePlusUserInfo googleUserInfo, boolean disableReactivationForUser);
    
}
