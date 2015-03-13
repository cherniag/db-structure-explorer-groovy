package mobi.nowtechnologies.server.service.social.core;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import mobi.nowtechnologies.server.persistence.domain.social.SocialInfo;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.social.BaseSocialRepository;
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.social.GooglePlusUserInfoRepository;
import mobi.nowtechnologies.server.service.ActivationEmailService;
import mobi.nowtechnologies.server.service.MergeResult;
import mobi.nowtechnologies.server.service.ReferralService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.EMAIL;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserPromoService {

    @Resource
    private ActivationEmailService activationEmailService;

    @Resource(name = "service.UserService")
    private UserService userService;

    @Resource
    private FacebookUserInfoRepository facebookUserInfoRepository;

    @Resource
    private GooglePlusUserInfoRepository googlePlusUserInfoRepository;

    @Resource
    private UserRepository userRepository;
    @Resource
    private ReferralService referralService;

    public MergeResult applyInitPromoByEmail(User user, Long activationEmailId, String email, String token) {
        activationEmailService.activate(activationEmailId, email, token);

        User existingUser = userRepository.findOne(email, user.getCommunityRewriteUrl());
        MergeResult mergeResult = userService.applyInitPromo(user, existingUser, null, false, true, false);
        user = mergeResult.getResultOfOperation();

        user.setProvider(EMAIL);
        user.setUserName(email);

        userService.updateUser(user);
        referralService.acknowledge(user, email);

        return new MergeResult(mergeResult.isMergeDone(), user);
    }

    public MergeResult applyInitPromoByGooglePlus(User userAfterSignUp, GooglePlusUserInfo googleUserInfo, boolean disableReactivationForUser) {
        MergeResult userAfterApplyPromo = doApplyPromo(userAfterSignUp, googleUserInfo, googlePlusUserInfoRepository, ProviderType.GOOGLE_PLUS, disableReactivationForUser);
        googlePlusUserInfoRepository.save(googleUserInfo);

        return userAfterApplyPromo;
    }

    public MergeResult applyInitPromoByFacebook(User userAfterSignUp, FacebookUserInfo facebookProfile, boolean disableReactivationForUser) {
        MergeResult mergeResult = doApplyPromo(userAfterSignUp, facebookProfile, facebookUserInfoRepository, ProviderType.FACEBOOK, disableReactivationForUser);
        facebookUserInfoRepository.save(facebookProfile);

        return mergeResult;
    }

    private MergeResult doApplyPromo(User userAfterSignUp, SocialInfo socialInfo, BaseSocialRepository baseSocialRepository, ProviderType providerType, boolean disableReactivationForUser) {
        User refreshedSignUpUser = userRepository.findOne(userAfterSignUp.getId());
        User userForMerge = getUserForMerge(refreshedSignUpUser, socialInfo.getEmail());
        MergeResult mergeResult = userService.applyInitPromo(refreshedSignUpUser, userForMerge, null, false, true, disableReactivationForUser);
        User userAfterApplyPromo = mergeResult.getResultOfOperation();
        baseSocialRepository.deleteByUser(userAfterApplyPromo);

        socialInfo.setUser(userAfterApplyPromo);
        userAfterApplyPromo.setUserName(socialInfo.getEmail());
        userAfterApplyPromo.setProvider(providerType);

        userRepository.save(userAfterApplyPromo);

        referralService.acknowledge(userAfterApplyPromo, socialInfo);

        return new MergeResult(mergeResult.isMergeDone(), userAfterApplyPromo);
    }

    private User getUserForMerge(User userAfterSignUp, String email) {
        return userRepository.findOne(email, userAfterSignUp.getCommunityRewriteUrl());
    }
}
