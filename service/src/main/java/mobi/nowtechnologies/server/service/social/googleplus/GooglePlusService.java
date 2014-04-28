package mobi.nowtechnologies.server.service.social.googleplus;

import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import mobi.nowtechnologies.server.service.social.core.AbstractOAuth2ApiBindingCustomizer;
import mobi.nowtechnologies.server.service.social.core.OAuth2ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.userinfo.GoogleUserInfo;
import org.springframework.web.client.RestClientException;

import static org.springframework.util.StringUtils.isEmpty;

public class GooglePlusService {
    private AbstractOAuth2ApiBindingCustomizer<GoogleTemplate> templateCustomizer;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public GooglePlusUserInfo getAndValidateProfile(String accessToken, String googlePlusUserId) {
        try {
            GoogleTemplate googleTemplate = new GoogleTemplate(accessToken);
            if(templateCustomizer != null) {
                templateCustomizer.customize(googleTemplate);
            }
            GoogleUserInfo googleUserInfo = googleTemplate.userOperations().getUserInfo();
            validateProfile(googlePlusUserId, googleUserInfo);
            return convertForUser(googleUserInfo);
        } catch (RestClientException se) {
            logger.error("ERROR", se);
            throw OAuth2ForbiddenException.invalidGooglePlusToken();
        }
    }

    private GooglePlusUserInfo convertForUser(GoogleUserInfo profile) {
        GooglePlusUserInfo result = new GooglePlusUserInfo();
        result.setEmail(profile.getEmail());
        result.setGooglePlusId(profile.getId());
        result.setFirstName(profile.getFirstName());
        result.setSurname(profile.getLastName());
        result.setPicture(profile.getProfilePictureUrl());
        return result;
    }

    private void validateProfile(String inputGooglePlusId, GoogleUserInfo googleUserInfo) {
        if (!googleUserInfo.getId().equals(inputGooglePlusId)) {
            throw OAuth2ForbiddenException.invalidGooglePlusUserId();
        }
        if (isEmpty(googleUserInfo.getEmail())) {
            throw OAuth2ForbiddenException.emptyGooglePlusEmail();
        }
    }


}
