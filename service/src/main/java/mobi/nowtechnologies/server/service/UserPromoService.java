package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import org.springframework.social.facebook.api.FacebookProfile;

public interface UserPromoService {

    User applyInitPromoByEmail(User user, Long activationEmailId, String email, String token);


    User applyInitPromoByFacebook(User user, FacebookProfile facebookProfile, boolean checkReactivation);
}
