/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.service.facebook.impl;

import mobi.nowtechnologies.server.social.domain.GenderType;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.social.domain.SocialNetworkType;
import mobi.nowtechnologies.server.social.service.facebook.FacebookClient;
import mobi.nowtechnologies.server.social.service.facebook.FacebookProfileImage;

import javax.annotation.Resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.social.SocialException;
import org.springframework.social.facebook.api.AgeRange;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.User;

/**
 * Created by oar on 3/14/14.
 */
public class FacebookClientImpl implements FacebookClient {

    private static Logger log = LoggerFactory.getLogger(FacebookClientImpl.class);

    @Resource
    FacebookOperationsAdaptor facebookOperationsAdaptor;

    @Override
    public SocialNetworkInfo getProfileUserInfo(String accessToken, String userId) {
        log.debug("requesting FacebookProfile by userId: [{}]", userId);
        User profile;
        try {
            profile = facebookOperationsAdaptor.getFacebookProfile(accessToken, userId);
        } catch (SocialException se) {
            log.error(String.format("Unexpected SocialException: %s", se.getMessage()), se);
            throw INVALID_FACEBOOK_TOKEN_EXCEPTION;
        }

        SocialNetworkInfo details = new SocialNetworkInfo(SocialNetworkType.FACEBOOK);
        String id = profile.getId();

        details.setEmail(getEmail(profile));
        details.setBirthday(getBirthdayDate(profile));
        details.setGenderType(GenderType.restore(profile.getGender()));
        assignCityAndCountry(profile, details);

        details.setFirstName(profile.getFirstName());
        details.setLastName(profile.getLastName());
        details.setSocialNetworkId(id);
        details.setUserName(id);
        AgeRange ageRange = profile.getAgeRange();
        details.setAgeRangeMin(ageRange.getMin());
        details.setAgeRangeMax(ageRange.getMax());

        return details;
    }

    @Override
    public FacebookProfileImage getProfileImage(String accessToken, String userId) {
        return facebookOperationsAdaptor.getFacebookProfileImage(accessToken, userId);
    }

    String getEmail(User profile) {
        String email = profile.getEmail();
        if (Strings.isNullOrEmpty(email)) {
            String id = profile.getId();
            log.info("Empty or absent email for id [{}]", id);
            email = String.format("%s@facebook.com", id);
        }
        return email;
    }

    Date getBirthdayDate(User profile) {
        String birthday = profile.getBirthday();
        if (!Strings.isNullOrEmpty(birthday)) {
            try {
                return new SimpleDateFormat(DATE_FORMAT).parse(birthday);
            } catch (ParseException e) {
                log.error(String.format("Can't parse birthday: [%s]", birthday), e);
            }
        }
        return null;
    }

    void assignCityAndCountry(User profile, SocialNetworkInfo details) {
        Reference loc = profile.getLocation();
        if (loc != null) {
            String cityWithCountry = loc.getName();
            if (!Strings.isNullOrEmpty(cityWithCountry)) {
                List<String> result = Splitter.on(',').omitEmptyStrings().trimResults().splitToList(cityWithCountry);
                if (result.size() > 0) {
                    details.setCity(result.get(0));
                }
                if (result.size() > 1) {
                    details.setCountry(result.get(1));
                }
            }
        }
    }
}
