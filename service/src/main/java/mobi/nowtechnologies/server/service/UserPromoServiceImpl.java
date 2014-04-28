package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.social.GooglePlusUserInfoRepository;
import mobi.nowtechnologies.server.service.social.facebook.FacebookService;
import mobi.nowtechnologies.server.service.social.googleplus.GooglePlusService;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.google.api.userinfo.GoogleUserInfo;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional
public class UserPromoServiceImpl implements UserPromoService {

    private ActivationEmailService activationEmailService;

    private UserService userService;

    @Resource
    private FacebookService facebookService;

    @Resource
    private GooglePlusService googlePlusService;

    @Resource
    private GooglePlusUserInfoRepository googlePlusUserInfoRepository;


    @Resource
    private FacebookUserInfoRepository facebookUserInfoRepository;

    @Resource
    private UserRepository userRepository;

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
        User result = getUserLoggedByFacebook(email);
        if (result == null){
            result = getUserLoggedByGooglePlus(email);
        }
        return result;
    }

    private User getUserLoggedByGooglePlus(String email) {
        GooglePlusUserInfo googlePlusUserInfo = googlePlusUserInfoRepository.findByEmail(email);
        return googlePlusUserInfo == null ? null : googlePlusUserInfo.getUser();
    }

    private User getUserLoggedByFacebook(String email) {
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
