package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.facebook.FacebookService;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional
public class UserPromoServiceImpl implements UserPromoService {

    private ActivationEmailService activationEmailService;

    private UserService userService;

    @Resource
    private FacebookService facebookService;

    @Override
    public User applyInitPromoByEmail(User user, Long activationEmailId, String email, String token) {
        user.setMobile(email);
        activationEmailService.activate(activationEmailId, email, token);

        user = userService.applyInitPromo(user, null, false, true);

        user.setUserName(email);
        user.setProvider(ProviderType.EMAIL);

        userService.updateUser(user);

        return user;
    }

    @Override
    public User applyInitPromoByFacebook(User user, FacebookProfile facebookProfile) {
        //MOBILE_IS_SET BECAUSE MERGE IS POSSIBLE
        user.setMobile(facebookProfile.getEmail());
        user = userService.applyInitPromo(user, null, false, true);
        facebookService.saveFacebookInfoForUser(user, facebookProfile);
        return user;
    }

    public void setActivationEmailService(ActivationEmailService activationEmailService) {
        this.activationEmailService = activationEmailService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
