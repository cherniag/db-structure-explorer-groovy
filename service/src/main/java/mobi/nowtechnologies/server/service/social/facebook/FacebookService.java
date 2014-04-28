package mobi.nowtechnologies.server.service.social.facebook;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository;
import mobi.nowtechnologies.server.service.social.core.AbstractOAuth2ApiBindingCustomizer;
import mobi.nowtechnologies.server.service.social.core.EmptyOAuth2ApiBindingCustomizer;
import mobi.nowtechnologies.server.service.social.core.OAuth2ForbiddenException;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.SocialException;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.springframework.util.StringUtils.isEmpty;

public class FacebookService {

    @Resource
    private FacebookUserInfoRepository facebookUserInfoRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private FacebookDataConverter facebookDataConverter;

    private AbstractOAuth2ApiBindingCustomizer templateCustomizer = new EmptyOAuth2ApiBindingCustomizer();

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Transactional
    public void saveFacebookInfoForUser(User inputUser, FacebookProfile profile) {
        facebookUserInfoRepository.deleteForUser(inputUser);
        User refreshedUser = userRepository.findOne(inputUser.getId());
        FacebookUserInfo details = facebookDataConverter.convertForUser(refreshedUser, profile);
        assignProviderInfo(refreshedUser, profile);
        userRepository.save(refreshedUser);
        facebookUserInfoRepository.save(details);
    }

    private void assignProviderInfo(User user, FacebookProfile profile) {
        user.setUserName(profile.getEmail());
        user.setProvider(ProviderType.FACEBOOK);
    }

    public FacebookProfile getAndValidateFacebookProfile(String facebookAccessToken, String inputFacebookId) {
        try {
            FacebookTemplate facebookTemplate = new FacebookTemplate(facebookAccessToken);
            templateCustomizer.customize(facebookTemplate);
            FacebookProfile facebookProfile = facebookTemplate.userOperations().getUserProfile();
            validateProfile(inputFacebookId, facebookProfile);
            return facebookProfile;
        } catch (SocialException se) {
            logger.error("ERROR", se);
            throw new OAuth2ForbiddenException(FacebookConstants.FACEBOOK_INVALID_TOKEN_ERROR_CODE, "invalid authorization token");
        }
    }

    private void validateProfile(String inputFacebookId, FacebookProfile facebookProfile) {
        if (!facebookProfile.getId().equals(inputFacebookId)) {
            throw new OAuth2ForbiddenException(FacebookConstants.FACEBOOK_INVALID_USER_ID_ERROR_CODE, "invalid user facebook id");
        }
        if (isEmpty(facebookProfile.getEmail())) {
            throw new OAuth2ForbiddenException(FacebookConstants.FACEBOOK_EMAIL_IS_NOT_SPECIFIED_ERROR_CODE, "Email is not specified");
        }

    }


}
