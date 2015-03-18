package mobi.nowtechnologies.server.service.social.googleplus;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.persistence.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.service.social.core.AbstractOAuth2ApiBindingCustomizer;
import mobi.nowtechnologies.server.service.social.core.OAuth2ForbiddenException;
import mobi.nowtechnologies.server.shared.dto.OAuthProvider;
import mobi.nowtechnologies.server.shared.enums.Gender;

import javax.annotation.Resource;

import java.util.Date;
import java.util.Map;
import static java.lang.Boolean.TRUE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.plus.Person;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import static org.springframework.util.StringUtils.isEmpty;

public class GooglePlusService {

    public static final String GOOGLE_PLUS_URL = "https://plus.google.com/";

    @Resource
    private GooglePlusTemplateProvider googlePlusTemplateProvider;

    private AbstractOAuth2ApiBindingCustomizer<GoogleTemplate> templateCustomizer;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public SocialNetworkInfo getAndValidateProfile(String accessToken, String googlePlusUserId) {
        try {
            GoogleTemplate googleTemplate = googlePlusTemplateProvider.provide(accessToken);
            if (templateCustomizer != null) {
                templateCustomizer.customize(googleTemplate);
            }
            Person personFromGooglePlus = googleTemplate.plusOperations().getGoogleProfile();
            validateProfile(googlePlusUserId, personFromGooglePlus);
            return convertForUser(personFromGooglePlus);
        } catch (RestClientException se) {
            logger.error("ERROR", se);
            throw OAuth2ForbiddenException.invalidGooglePlusToken();
        }
    }

    private SocialNetworkInfo convertForUser(Person personFromGooglePlus) {
        SocialNetworkInfo result = new SocialNetworkInfo(OAuthProvider.GOOGLE);
        result.setEmail(personFromGooglePlus.getAccountEmail());
        result.setSocialNetworkId(personFromGooglePlus.getId());
        result.setBirthday(extractDateInUTC(personFromGooglePlus));
        result.setUserName(personFromGooglePlus.getDisplayName());
        result.setProfileImageUrl(extractImageUrl(personFromGooglePlus));
        result.setGender(Gender.restore(personFromGooglePlus.getGender()));
        result.setLocation(extractLocation(personFromGooglePlus));
        result.setFirstName(personFromGooglePlus.getGivenName());
        result.setLastName(personFromGooglePlus.getFamilyName());
        result.setProfileUrl(buildHomepageUrl(personFromGooglePlus));
        return result;
    }

    private String extractImageUrl(Person personFromGooglePlus) {
        String imageUrl = personFromGooglePlus.getImageUrl();
        return UriComponentsBuilder.fromHttpUrl(imageUrl).replaceQueryParam("sz", "200").build().toUriString();
    }

    private Date extractDateInUTC(Person personFromGooglePlus) {
        return DateTimeUtils.getDateInUTC(personFromGooglePlus.getBirthday());
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

    private void validateProfile(String inputGooglePlusId, Person person) {
        if (!person.getId().equals(inputGooglePlusId)) {
            throw OAuth2ForbiddenException.invalidGooglePlusUserId();
        }
        if (isEmpty(person.getAccountEmail())) {
            throw OAuth2ForbiddenException.emptyGooglePlusEmail();
        }
    }


}
