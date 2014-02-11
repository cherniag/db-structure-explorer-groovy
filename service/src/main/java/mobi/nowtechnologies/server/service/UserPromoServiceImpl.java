package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.facebook.FacebookService;
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

        userService.updateUser(user);

        return user;
    }

    @Override
    public User applyInitPromoByFacebook(User user, FacebookProfile facebookProfile) {
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
