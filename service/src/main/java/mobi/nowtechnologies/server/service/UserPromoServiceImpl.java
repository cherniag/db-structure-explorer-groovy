package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.repository.FacebookUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.facebook.FacebookService;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static mobi.nowtechnologies.server.shared.enums.ProviderType.EMAIL;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Transactional
public class UserPromoServiceImpl implements UserPromoService {

    private ActivationEmailService activationEmailService;

    private UserService userService;

    @Resource
    private FacebookService facebookService;

    @Resource
    private FacebookUserInfoRepository facebookUserInfoRepository;

    @Resource
    private UserRepository userRepository;

    @Override
    @Transactional(propagation = REQUIRED)
    public User applyInitPromoByEmail(User user, Long activationEmailId, String email, String token) {
        activationEmailService.activate(activationEmailId, email, token);

        User existingUser = userRepository.findOne(email, user.getUserGroup().getCommunity().getRewriteUrlParameter());

        user = userService.applyInitPromo(user, existingUser, null, false, true, false);

        user.setProvider(EMAIL);
        user.setUserName(email);

        userService.updateUser(user);

        return user;
    }

    @Override
    public User applyInitPromoByFacebook(User userAfterSignUp, FacebookProfile facebookProfile) {
        User userForMerge = getUserForMerge(userAfterSignUp, facebookProfile);
        User userAfterApplyPromo = userService.applyInitPromo(userAfterSignUp, userForMerge, null, false, true, false);
        facebookService.saveFacebookInfoForUser(userAfterApplyPromo, facebookProfile);
        return userAfterApplyPromo;
    }

    private User getUserForMerge(User userAfterSignUp, FacebookProfile facebookProfile) {
        String url = userAfterSignUp.getUserGroup().getCommunity().getRewriteUrlParameter();
        String email = facebookProfile.getEmail();
        User userByEmail = userRepository.findOne(email, url);
        if (userByEmail != null) {
            return userByEmail;
        }
        FacebookUserInfo facebookInfo = facebookUserInfoRepository.findByEmail(email);
        return facebookInfo == null ? null : facebookInfo.getUser();
    }

    public void setActivationEmailService(ActivationEmailService activationEmailService) {
        this.activationEmailService = activationEmailService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
