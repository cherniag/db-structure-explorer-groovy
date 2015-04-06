/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.service.googleplus.impl;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.social.domain.GenderType;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.social.domain.SocialNetworkType;
import mobi.nowtechnologies.server.social.service.googleplus.GooglePlusClient;

import javax.annotation.Resource;

import java.util.Date;
import java.util.Map;
import static java.lang.Boolean.TRUE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.social.google.api.plus.Person;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
/**
 * @author Anton Zemliankin
 */
public class GooglePlusClientImpl implements GooglePlusClient {

    private static Logger log = LoggerFactory.getLogger(GooglePlusClientImpl.class);

    @Resource
    GooglePlusOperationsAdaptor googlePlusOperationsAdaptor;

    @Override
    public SocialNetworkInfo getProfileUserInfo(String accessToken) {
        log.debug("requesting GooglePlusProfile");
        Person profile;
        try {
            profile = googlePlusOperationsAdaptor.getGooglePlusProfile(accessToken);
        } catch (RestClientException se) {
            log.error(String.format("Unexpected SocialException: %s", se.getMessage()), se);
            throw INVALID_GOOGLE_PLUS_TOKEN_EXCEPTION;
        }

        SocialNetworkInfo result = new SocialNetworkInfo(SocialNetworkType.GOOGLE);
        result.setEmail(profile.getAccountEmail());
        result.setSocialNetworkId(profile.getId());
        result.setBirthday(extractDateInUTC(profile));
        result.setUserName(profile.getDisplayName());
        result.setProfileImageUrl(extractImageUrl(profile));
        result.setGenderType(GenderType.restore(profile.getGender()));
        result.setCity(extractLocation(profile));
        result.setFirstName(profile.getGivenName());
        result.setLastName(profile.getFamilyName());
        return result;
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

    private String extractImageUrl(Person personFromGooglePlus) {
        String imageUrl = personFromGooglePlus.getImageUrl();
        return UriComponentsBuilder.fromHttpUrl(imageUrl).replaceQueryParam("sz", "200").build().toUriString();
    }

    private Date extractDateInUTC(Person personFromGooglePlus) {
        return DateTimeUtils.getDateInUTC(personFromGooglePlus.getBirthday());
    }

}
