package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import mobi.nowtechnologies.server.persistence.domain.social.SocialInfo;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.social.BaseSocialRepository;
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.social.GooglePlusUserInfoRepository;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static mobi.nowtechnologies.server.shared.enums.ProviderType.EMAIL;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Transactional
public class UserPromoServiceImpl implements UserPromoService {

    private ActivationEmailService activationEmailService;

    private UserService userService;

    @Resource
    private FacebookUserInfoRepository facebookUserInfoRepository;

    @Resource
    private GooglePlusUserInfoRepository googlePlusUserInfoRepository;

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
    public User applyInitPromoByGooglePlus(User userAfterSignUp, GooglePlusUserInfo googleUserInfo, boolean disableReactivationForUser) {
        User userAfterApplyPromo = doApplyPromo(userAfterSignUp, googleUserInfo, googlePlusUserInfoRepository, ProviderType.GOOGLE_PLUS, disableReactivationForUser);
        googlePlusUserInfoRepository.save(googleUserInfo);

        return userAfterApplyPromo;
    }

    @Override
    public User applyInitPromoByFacebook(User userAfterSignUp, FacebookUserInfo facebookProfile, boolean disableReactivationForUser) {
        User userAfterApplyPromo = doApplyPromo(userAfterSignUp, facebookProfile, facebookUserInfoRepository, ProviderType.FACEBOOK, disableReactivationForUser);
        facebookUserInfoRepository.save(facebookProfile);

        return userAfterApplyPromo;
    }
    
    private User doApplyPromo(User userAfterSignUp, SocialInfo socialInfo, BaseSocialRepository baseSocialRepository, ProviderType providerType, boolean disableReactivationForUser) {
        User refreshedSignUpUser = userRepository.findOne(userAfterSignUp.getId());
        User userForMerge = getUserForMerge(refreshedSignUpUser, socialInfo.getEmail());
        User userAfterApplyPromo = userService.applyInitPromo(refreshedSignUpUser, userForMerge, null, false, true, disableReactivationForUser);
        baseSocialRepository.deleteByUser(userAfterApplyPromo);

        socialInfo.setUser(userAfterApplyPromo);
        userAfterApplyPromo.setUserName(socialInfo.getEmail());
        userAfterApplyPromo.setProvider(providerType);

        userRepository.save(userAfterApplyPromo);
        return userAfterApplyPromo;
    }

    private User getUserForMerge(User userAfterSignUp, String email) {
        return userRepository.findOne(email, userAfterSignUp.getCommunityRewriteUrl());
    }


    public void setActivationEmailService(ActivationEmailService activationEmailService) {
        this.activationEmailService = activationEmailService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
