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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

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
        User userAfterApplyPromo = doApplyPromo(userAfterSignUp, googleUserInfo, googlePlusUserInfoRepository, ProviderType.GOOGLE_PLUS);
        googlePlusUserInfoRepository.save(googleUserInfo);

        return userAfterApplyPromo;
    }

    @Override
    public User applyInitPromoByFacebook(User userAfterSignUp, FacebookUserInfo userInfo) {
        User userAfterApplyPromo = doApplyPromo(userAfterSignUp, userInfo, facebookUserInfoRepository, ProviderType.FACEBOOK);
        facebookUserInfoRepository.save(userInfo);

        return userAfterApplyPromo;
    }

    private User doApplyPromo(User userAfterSignUp, SocialInfo socialInfo, BaseSocialRepository baseSocialRepository, ProviderType googlePlus) {
        User refreshedSignUpUser = userRepository.findOne(userAfterSignUp.getId());
        User userForMerge = getUserForMerge(baseSocialRepository, refreshedSignUpUser, socialInfo.getEmail());
        User userAfterApplyPromo = userService.applyInitPromo(refreshedSignUpUser, userForMerge, null, false, true);
        baseSocialRepository.deleteByUser(userAfterApplyPromo);

        socialInfo.setUser(userAfterApplyPromo);
        userAfterApplyPromo.setUserName(socialInfo.getEmail());
        userAfterApplyPromo.setProvider(googlePlus);

        userRepository.save(userAfterApplyPromo);
        return userAfterApplyPromo;
    }

    private User getUserForMerge(BaseSocialRepository baseSocialRepository, User userAfterSignUp, String email) {
        String url = userAfterSignUp.getCommunityRewriteUrl();
        User userByEmail = userRepository.findOne(email, url);
        if (userByEmail != null) {
            return userByEmail;
        }

        User user = tryToFindUserByEmail(email, Arrays.asList(baseSocialRepository));
        if(user != null) {
            return user;
        }

        Collection<BaseSocialRepository> filtered = new HashSet<BaseSocialRepository>(socialRepositories);
        filtered.remove(baseSocialRepository);
        return tryToFindUserByEmail(email, filtered);
    }

    private User tryToFindUserByEmail(String email, Collection<BaseSocialRepository> socialRepositories) {
        for (BaseSocialRepository currentSocialRepository : socialRepositories) {
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
