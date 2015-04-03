package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfoRepository;
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
    private SocialNetworkInfoRepository socialNetworkInfoRepository;

    @Resource
    private UserRepository userRepository;
    @Resource
    private ReferralService referralService;

    public MergeResult applyInitPromoByEmail(User user, Long activationEmailId, String email, String token) {
        activationEmailService.activate(activationEmailId, email, token);

        User existingUser = userRepository.findByUserNameAndCommunityUrl(email, user.getCommunityRewriteUrl());
        MergeResult mergeResult = userService.applyInitPromo(user, existingUser, null, false, true, false);
        user = mergeResult.getResultOfOperation();

        user.setProvider(EMAIL);
        user.setUserName(email);

        userService.updateUser(user);
        referralService.acknowledge(user, email);

        return new MergeResult(mergeResult.isMergeDone(), user);
    }

    public MergeResult applyInitPromoByGooglePlus(User userAfterSignUp, SocialNetworkInfo googleUserInfo, boolean disableReactivationForUser) {
        MergeResult userAfterApplyPromo = doApplyPromo(userAfterSignUp, googleUserInfo, socialNetworkInfoRepository, ProviderType.GOOGLE_PLUS, disableReactivationForUser);
        socialNetworkInfoRepository.save(googleUserInfo);

        return userAfterApplyPromo;
    }

    public MergeResult applyInitPromoByFacebook(User userAfterSignUp, SocialNetworkInfo facebookProfile, boolean disableReactivationForUser) {
        MergeResult mergeResult = doApplyPromo(userAfterSignUp, facebookProfile, socialNetworkInfoRepository, ProviderType.FACEBOOK, disableReactivationForUser);
        socialNetworkInfoRepository.save(facebookProfile);

        return mergeResult;
    }

    private MergeResult doApplyPromo(User userAfterSignUp, SocialNetworkInfo socialNetworkInfo, SocialNetworkInfoRepository socialNetworkInfoRepository, ProviderType providerType,
                                     boolean disableReactivationForUser) {
        User refreshedSignUpUser = userRepository.findOne(userAfterSignUp.getId());
        User userForMerge = getUserForMerge(refreshedSignUpUser, socialNetworkInfo.getEmail());
        MergeResult mergeResult = userService.applyInitPromo(refreshedSignUpUser, userForMerge, null, false, true, disableReactivationForUser);
        User userAfterApplyPromo = mergeResult.getResultOfOperation();
        socialNetworkInfoRepository.deleteByUserId(userAfterApplyPromo.getId());

        socialNetworkInfo.setUserId(userAfterApplyPromo.getId());
        userAfterApplyPromo.setUserName(socialNetworkInfo.getEmail());
        userAfterApplyPromo.setProvider(providerType);

        userRepository.save(userAfterApplyPromo);

        referralService.acknowledge(userAfterApplyPromo, socialNetworkInfo);

        return new MergeResult(mergeResult.isMergeDone(), userAfterApplyPromo);
    }

    private User getUserForMerge(User userAfterSignUp, String email) {
        return userRepository.findByUserNameAndCommunityUrl(email, userAfterSignUp.getCommunityRewriteUrl());
    }
}
