package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import mobi.nowtechnologies.server.persistence.domain.social.SocialInfo;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.social.BaseSocialRepository;
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.social.GooglePlusUserInfoRepository;
import mobi.nowtechnologies.server.service.social.googleplus.GooglePlusService;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;

@Transactional
public class UserPromoServiceImpl implements UserPromoService {

    private ActivationEmailService activationEmailService;

    private UserService userService;

    @Resource
    private GooglePlusService googlePlusService;

    @Resource
    private FacebookUserInfoRepository facebookUserInfoRepository;
    @Resource
    private GooglePlusUserInfoRepository googlePlusUserInfoRepository;

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
    public User applyInitPromoByGooglePlus(User userAfterSignUp, GooglePlusUserInfo googleUserInfo) {
        User userForMerge = getUserForMerge(userAfterSignUp, googleUserInfo.getEmail());
        User userAfterApplyPromo = userService.applyInitPromo(userAfterSignUp, userForMerge, null, false, true);
        googlePlusUserInfoRepository.deleteForUser(userAfterApplyPromo);

        googleUserInfo.setUser(userAfterApplyPromo);
        userAfterApplyPromo.setUserName(googleUserInfo.getEmail());
        userAfterApplyPromo.setProvider(ProviderType.GOOGLE_PLUS);

        userRepository.save(userAfterApplyPromo);
        googlePlusUserInfoRepository.save(googleUserInfo);


        return userAfterApplyPromo;
    }

    @Override
    public User applyInitPromoByFacebook(User userAfterSignUp, FacebookUserInfo userInfo) {
        User userForMerge = getUserForMerge(userAfterSignUp, userInfo.getEmail());
        User userAfterApplyPromo = userService.applyInitPromo(userAfterSignUp, userForMerge, null, false, true);
        facebookUserInfoRepository.deleteForUser(userAfterApplyPromo);

        userInfo.setUser(userAfterApplyPromo);
        userAfterApplyPromo.setUserName(userInfo.getEmail());
        userAfterApplyPromo.setProvider(ProviderType.FACEBOOK);

        userRepository.save(userAfterApplyPromo);
        facebookUserInfoRepository.save(userInfo);

        return userAfterApplyPromo;
    }

    private User getUserForMerge(User userAfterSignUp, String email) {
        String url = userAfterSignUp.getCommunityRewriteUrl();
        User userByEmail = userRepository.findOne(email, url);
        if (userByEmail != null) {
            return userByEmail;
        }
        for (BaseSocialRepository currentSocialRepository : socialRepositories){
            SocialInfo socialInfo = currentSocialRepository.findByEmail(email);
            if (socialInfo != null) {
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
