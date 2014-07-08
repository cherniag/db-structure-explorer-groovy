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

    @Override
    @Transactional(propagation = REQUIRED)
    public OperationResult applyInitPromoByEmail(User user, Long activationEmailId, String email, String token) {
        activationEmailService.activate(activationEmailId, email, token);

        User existingUser = userRepository.findOne(email, user.getCommunityRewriteUrl());
        OperationResult mergeResult = userService.applyInitPromo(user, existingUser, null, false, true, false);
        user = mergeResult.getResultOfOperation();

        user.setProvider(EMAIL);
        user.setUserName(email);

        userService.updateUser(user);

        return new OperationResult(mergeResult.isMergeDone(), user);
    }

    @Override
    public OperationResult applyInitPromoByGooglePlus(User userAfterSignUp, GooglePlusUserInfo googleUserInfo, boolean disableReactivationForUser) {
        OperationResult userAfterApplyPromo = doApplyPromo(userAfterSignUp, googleUserInfo, googlePlusUserInfoRepository, ProviderType.GOOGLE_PLUS, disableReactivationForUser);
        googlePlusUserInfoRepository.save(googleUserInfo);

        return userAfterApplyPromo;
    }

    @Override
    public OperationResult applyInitPromoByFacebook(User userAfterSignUp, FacebookUserInfo facebookProfile, boolean disableReactivationForUser) {
        OperationResult mergeResult = doApplyPromo(userAfterSignUp, facebookProfile, facebookUserInfoRepository, ProviderType.FACEBOOK, disableReactivationForUser);
        facebookUserInfoRepository.save(facebookProfile);

        return mergeResult;
    }
    
    private OperationResult doApplyPromo(User userAfterSignUp, SocialInfo socialInfo, BaseSocialRepository baseSocialRepository, ProviderType providerType, boolean disableReactivationForUser) {
        User refreshedSignUpUser = userRepository.findOne(userAfterSignUp.getId());
        User userForMerge = getUserForMerge(refreshedSignUpUser, socialInfo.getEmail());
        OperationResult mergeResult = userService.applyInitPromo(refreshedSignUpUser, userForMerge, null, false, true, disableReactivationForUser);
        User userAfterApplyPromo = mergeResult.getResultOfOperation();
        baseSocialRepository.deleteByUser(userAfterApplyPromo);

        socialInfo.setUser(userAfterApplyPromo);
        userAfterApplyPromo.setUserName(socialInfo.getEmail());
        userAfterApplyPromo.setProvider(providerType);

        userRepository.save(userAfterApplyPromo);
        return new OperationResult(mergeResult.isMergeDone(), userAfterApplyPromo);
    }

    private User getUserForMerge(User userAfterSignUp, String email) {
        return userRepository.findOne(email, userAfterSignUp.getCommunityRewriteUrl());
    }


}
