package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import mobi.nowtechnologies.server.service.merge.OperationResult;

public interface UserPromoService {

    OperationResult applyInitPromoByEmail(User user, Long activationEmailId, String email, String token);


    OperationResult applyInitPromoByFacebook(User user, FacebookUserInfo facebookProfile, boolean disableReactivationForUser);

    OperationResult applyInitPromoByGooglePlus(User user, GooglePlusUserInfo googleUserInfo, boolean disableReactivationForUser);
    
}
