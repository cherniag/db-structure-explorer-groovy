package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserPromoServiceImpl implements UserPromoService {

    private ActivationEmailService activationEmailService;

    private UserService userService;

    @Override
    public User applyInitPromoByEmail(User user, Long activationEmailId, String email) {
        user.setMobile(email);
        activationEmailService.activate(activationEmailId, email);

        user = userService.applyInitPromo(user, null, false, true);

        user.setUserName(email);

        userService.updateUser(user);

        return user;
    }

    public void setActivationEmailService(ActivationEmailService activationEmailService) {
        this.activationEmailService = activationEmailService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
