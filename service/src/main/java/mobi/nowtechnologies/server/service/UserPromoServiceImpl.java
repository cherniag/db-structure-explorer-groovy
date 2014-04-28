package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.SocialInfo;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.social.BaseSocialRepository;
import mobi.nowtechnologies.server.service.social.facebook.FacebookService;
import mobi.nowtechnologies.server.service.social.googleplus.GooglePlusService;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.google.api.userinfo.GoogleUserInfo;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;

@Transactional
public class UserPromoServiceImpl implements UserPromoService {

    private ActivationEmailService activationEmailService;

    private UserService userService;

    @Resource
    private FacebookService facebookService;

    @Resource
    private GooglePlusService googlePlusService;


    @Resource
    private UserRepository userRepository;


    @Resource
    private Collection<BaseSocialRepository> socialRepositories;

    @Override
    public User applyInitPromoByEmail(User user, Long activationEmailId, String email, String token) {
        activationEmailService.activate(activationEmailId, email, token);

        User existingUser = userRepository.findOne(email, user.getUserGroup().getCommunity().getRewriteUrlParameter());

        user = userService.applyInitPromo(user, existingUser, null, false, true);

        user.setProvider(ProviderType.EMAIL);
        user.setUserName(email);

        userService.updateUser(user);

        return user;
    }


    @Override
    public User applyInitPromoByGooglePlus(User userAfterSignUp, GoogleUserInfo googleUserInfo) {
        User userForMerge = getUserForMerge(userAfterSignUp, googleUserInfo.getEmail());
        User userAfterApplyPromo = userService.applyInitPromo(userAfterSignUp, userForMerge, null, false, true);
        googlePlusService.saveGooglePlusInfoForUser(userAfterApplyPromo, googleUserInfo);
        return userAfterApplyPromo;
    }

    @Override
    public User applyInitPromoByFacebook(User userAfterSignUp, FacebookProfile facebookProfile) {
        User userForMerge = getUserForMerge(userAfterSignUp, facebookProfile.getEmail());
        User userAfterApplyPromo = userService.applyInitPromo(userAfterSignUp, userForMerge, null, false, true);
        facebookService.saveFacebookInfoForUser(userAfterApplyPromo, facebookProfile);
        return userAfterApplyPromo;
    }

    private User getUserForMerge(User userAfterSignUp, String email) {
        String url = userAfterSignUp.getUserGroup().getCommunity().getRewriteUrlParameter();
        User userByEmail = userRepository.findOne(email, url);
        if (userByEmail != null) {
            return userByEmail;
        }
        for (BaseSocialRepository currentSocialRepository: socialRepositories){
            SocialInfo socialInfo = currentSocialRepository.findByEmail(email);
            if (socialInfo != null){
                return socialInfo.getUser();
            }
        }
        return null;
    }


    public void setActivationEmailService(ActivationEmailService activationEmailService) {
        this.activationEmailService = activationEmailService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
