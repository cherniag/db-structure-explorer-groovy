package mobi.nowtechnologies.server.service.social.googleplus;

import com.google.common.annotations.VisibleForTesting;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.social.GooglePlusUserInfoRepository;
import mobi.nowtechnologies.server.service.social.core.AbstractOAuth2ApiBindingCustomizer;
import mobi.nowtechnologies.server.service.social.core.EmptyOAuth2ApiBindingCustomizer;
import mobi.nowtechnologies.server.service.social.core.OAuth2ForbiddenException;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.userinfo.GoogleUserInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import javax.annotation.Resource;

import static org.springframework.util.StringUtils.isEmpty;

public class GooglePlusService {

    @Resource
    private GooglePlusUserInfoRepository googlePlusUserInfoRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private GooglePlusDataConverter googlePlusDataConverter;

    private AbstractOAuth2ApiBindingCustomizer templateCustomizer = new EmptyOAuth2ApiBindingCustomizer();

    private Logger logger = LoggerFactory.getLogger(getClass());

    @VisibleForTesting
    public void setTemplateCustomizer(AbstractOAuth2ApiBindingCustomizer templateCustomizer) {
        this.templateCustomizer = templateCustomizer;
    }

    @Transactional
    public void saveGooglePlusInfoForUser(User inputUser, GoogleUserInfo profile) {
        googlePlusUserInfoRepository.deleteForUser(inputUser);
        User refreshedUser = userRepository.findOne(inputUser.getId());
        GooglePlusUserInfo details = googlePlusDataConverter.convertForUser(refreshedUser, profile);
        assignProviderInfo(refreshedUser, profile);
        userRepository.save(refreshedUser);
        googlePlusUserInfoRepository.save(details);
    }

    private void assignProviderInfo(User user, GoogleUserInfo profile) {
        user.setUserName(profile.getEmail());
        user.setProvider(ProviderType.GOOGLE_PLUS);
    }

    public GoogleUserInfo getAndValidateProfile(String accessToken, String googlePlusUserId) {
        try {
            GoogleTemplate googleTemplate = new GoogleTemplate(accessToken);
            templateCustomizer.customize(googleTemplate);
            GoogleUserInfo googleUserInfo = googleTemplate.userOperations().getUserInfo();
            validateProfile(googlePlusUserId, googleUserInfo);
            return googleUserInfo;
        } catch (RestClientException se) {
            logger.error("ERROR", se);
            throw new OAuth2ForbiddenException(GooglePlusConstants.GOOGLE_PLUS_INVALID_TOKEN_ERROR_CODE, "invalid authorization token");
        }
    }

    private void validateProfile(String inputGooglePlusId, GoogleUserInfo googleUserInfo) {
        if (!googleUserInfo.getId().equals(inputGooglePlusId)) {
            throw new OAuth2ForbiddenException(GooglePlusConstants.GOOGLE_PLUS_INVALID_USER_ID_ERROR_CODE, "invalid user google plus id");
        }
        if (isEmpty(googleUserInfo.getEmail())) {
            throw new OAuth2ForbiddenException(GooglePlusConstants.GOOGLE_PLUS_EMAIL_IS_NOT_SPECIFIED_ERROR_CODE, "Email is not specified");
        }
    }


}
