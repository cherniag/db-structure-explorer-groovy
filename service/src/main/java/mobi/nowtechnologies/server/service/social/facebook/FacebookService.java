package mobi.nowtechnologies.server.service.social.facebook;

import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.service.social.core.AbstractOAuth2ApiBindingCustomizer;
import mobi.nowtechnologies.server.service.social.core.OAuth2ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.SocialException;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.impl.FacebookTemplate;

import javax.annotation.Resource;

import static org.springframework.util.StringUtils.isEmpty;

public class FacebookService {
    @Resource
    private FacebookDataConverter facebookDataConverter;
    @Resource
    private FacebookTemplateProvider facebookTemplateProvider;

    private AbstractOAuth2ApiBindingCustomizer<FacebookTemplate> templateCustomizer;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public FacebookUserInfo getAndValidateFacebookProfile(String facebookAccessToken, String inputFacebookId) {
        try {
            FacebookTemplate facebookTemplate = facebookTemplateProvider.provide(facebookAccessToken);
            if(templateCustomizer != null) {
                templateCustomizer.customize(facebookTemplate);
            }
            FacebookProfile facebookProfile = facebookTemplate.userOperations().getUserProfile();
            validateProfile(inputFacebookId, facebookProfile);
            return facebookDataConverter.convert(facebookProfile);
        } catch (SocialException se) {
            logger.error("ERROR", se);
            throw OAuth2ForbiddenException.invalidFacebookToken();
        }
    }

    private void validateProfile(String inputFacebookId, FacebookProfile facebookProfile) {
        if (!facebookProfile.getId().equals(inputFacebookId)) {
            throw OAuth2ForbiddenException.invalidFacebookUserId();
        }
        if (isEmpty(facebookProfile.getEmail())) {
            throw OAuth2ForbiddenException.emptyFacebookEmail();
        }

    }


}
