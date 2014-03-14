package mobi.nowtechnologies.server.service.facebook;

import com.google.common.annotations.VisibleForTesting;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.repository.FacebookUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.facebook.exception.FacebookForbiddenException;
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

    private FacebookDataConverter facebookDataConverter;

    private FacebookTemplateCustomizer templateCustomizer = new EmptyFacebookTemplateCustomizer();

    private Logger logger = LoggerFactory.getLogger(getClass());

    @VisibleForTesting
    public void setTemplateCustomizer(FacebookTemplateCustomizer templateCustomizer) {
        this.templateCustomizer = templateCustomizer;
    }

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
            throw new FacebookForbiddenException(FacebookConstants.FACEBOOK_INVALID_TOKEN_ERROR_CODE, "invalid authorization token");
        }
    }

    private void validateProfile(String inputFacebookId, FacebookProfile facebookProfile) {
        if (!facebookProfile.getId().equals(inputFacebookId)) {
            throw new FacebookForbiddenException(FacebookConstants.FACEBOOK_INVALID_USER_ID_ERROR_CODE, "invalid user facebook id");
        }
        if (isEmpty(facebookProfile.getEmail())) {
            throw new FacebookForbiddenException(FacebookConstants.FACEBOOK_EMAIL_IS_NOT_SPECIFIED_ERROR_CODE, "Email is not specified");
        }

    }

    public void setFacebookDataConverter(FacebookDataConverter facebookDataConverter) {
        this.facebookDataConverter = facebookDataConverter;
    }

}
