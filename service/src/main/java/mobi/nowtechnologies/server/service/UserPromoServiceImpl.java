package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.repository.FacebookUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
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

    @Resource
    private FacebookUserInfoRepository facebookUserInfoRepository;

    @Resource
    private UserRepository userRepository;

    @Override
    public User applyInitPromoByEmail(User user, Long activationEmailId, String email, String token) {
        activationEmailService.activate(activationEmailId, email, token);

        User existingUser = userRepository.findOne(email, user.getUserGroup().getCommunity().getRewriteUrlParameter());

        user = userService.applyInitPromo(user, existingUser, null, true);

        user.setProvider(ProviderType.EMAIL);
        user.setUserName(email);

        userService.updateUser(user);

        return user;
    }

    @Override
    public User applyInitPromoByFacebook(User userAfterSignUp, FacebookProfile facebookProfile) {
        User userForMerge = getUserForMerge(userAfterSignUp, facebookProfile);
        User userAfterApplyPromo = userService.applyInitPromo(userAfterSignUp, userForMerge, null, true);
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
