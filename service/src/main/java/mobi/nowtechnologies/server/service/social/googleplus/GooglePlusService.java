package mobi.nowtechnologies.server.service.social.googleplus;

import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import mobi.nowtechnologies.server.service.social.core.AbstractOAuth2ApiBindingCustomizer;
import mobi.nowtechnologies.server.service.social.core.OAuth2ForbiddenException;
import mobi.nowtechnologies.server.shared.enums.Gender;
import mobi.nowtechnologies.server.shared.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.plus.Person;
import org.springframework.social.google.api.userinfo.GoogleUserInfo;
import org.springframework.web.client.RestClientException;

import java.util.Date;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static mobi.nowtechnologies.server.service.social.googleplus.GooglePlusConstants.GOOGLE_PLUS_URL;
import static org.springframework.util.StringUtils.isEmpty;

public class GooglePlusService {
    private AbstractOAuth2ApiBindingCustomizer<GoogleTemplate> templateCustomizer;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public GooglePlusUserInfo getAndValidateProfile(String accessToken, String googlePlusUserId) {
        try {
            GoogleTemplate googleTemplate = new GoogleTemplate(accessToken);
            if (templateCustomizer != null) {
                templateCustomizer.customize(googleTemplate);
            }
            GoogleUserInfo googleUserInfo = googleTemplate.userOperations().getUserInfo();
            validateProfile(googlePlusUserId, googleUserInfo);
            Person personFromGooglePlus = googleTemplate.plusOperations().getGoogleProfile();
            return convertForUser(googleUserInfo, personFromGooglePlus);
        } catch (RestClientException se) {
            logger.error("ERROR", se);
            throw OAuth2ForbiddenException.invalidGooglePlusToken();
        }
    }

    private GooglePlusUserInfo convertForUser(GoogleUserInfo profile, Person personFromGooglePlus) {
        GooglePlusUserInfo result = new GooglePlusUserInfo();
        result.setEmail(profile.getEmail());
        result.setGooglePlusId(personFromGooglePlus.getId());
        result.setBirthday(extractDateInUTC(personFromGooglePlus));
        result.setDisplayName(personFromGooglePlus.getDisplayName());
        result.setPicture(personFromGooglePlus.getImageUrl());
        result.setGender(extractGender(personFromGooglePlus));
        result.setLocation(extractLocation(personFromGooglePlus));
        result.setGivenName(personFromGooglePlus.getGivenName());
        result.setFamilyName(personFromGooglePlus.getFamilyName());
        result.setHomePage(buildHomepageUrl(personFromGooglePlus));
        return result;
    }

    private Date extractDateInUTC(Person personFromGooglePlus) {
        return DateUtils.getDateInUTC(personFromGooglePlus.getBirthday());
    }

    private String buildHomepageUrl(Person personFromGooglePlus) {
        return GOOGLE_PLUS_URL + personFromGooglePlus.getId();
    }


    private String extractLocation(Person personFromGooglePlus) {
        if (personFromGooglePlus.getPlacesLived() != null) {
            for (Map.Entry<String, Boolean> currentLocation : personFromGooglePlus.getPlacesLived().entrySet()) {
                if (TRUE.equals(currentLocation.getValue())) {
                    return currentLocation.getKey();
                }
            }
        }
        return null;
    }

    private Gender extractGender(Person personFromGooglePlus) {
        String gender = personFromGooglePlus.getGender();
        if (!StringUtils.isEmpty(gender)) {
            if ("male".equals(gender)) {
                return Gender.MALE;
            }
            if ("female".equals(gender)) {
                return Gender.FEMALE;
            }
        }
        return null;
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
