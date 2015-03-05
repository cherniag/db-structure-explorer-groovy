package mobi.nowtechnologies.server.service.social.facebook.impl;

import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.service.social.facebook.FacebookClient;
import mobi.nowtechnologies.server.shared.enums.Gender;

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
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.GraphApi;
import org.springframework.social.facebook.api.Reference;

/**
 * Created by oar on 3/14/14.
 */
public class FacebookClientImpl implements FacebookClient {

    private static Logger log = LoggerFactory.getLogger(FacebookClientImpl.class);

    @Resource
    FacebookOperationsAdaptor facebookOperationsAdaptor;

    @Override
    public FacebookUserInfo getProfileUserInfo(String accessToken, String userId) {
        log.debug("requesting FacebookProfile by userId: [{}]", userId);
        FacebookProfile profile;
        try {
            profile = facebookOperationsAdaptor.getFacebookProfile(accessToken, userId);
        }
        catch (SocialException se) {
            log.error(String.format("Unexpected SocialException: %s", se.getMessage()), se);
            throw INVALID_FACEBOOK_TOKEN_EXCEPTION;
        }

        FacebookUserInfo details = new FacebookUserInfo();
        String id = profile.getId();

        details.setEmail(getEmail(profile));
        details.setBirthday(getBirthdayDate(profile));
        details.setGender(Gender.restore(profile.getGender()));
        details.setProfileUrl(String.format("%s%s/picture?type=large", GraphApi.GRAPH_API_URL, id));
        assignCityAndCountry(profile, details);

        details.setFirstName(profile.getFirstName());
        details.setSurname(profile.getLastName());
        details.setFacebookId(id);
        details.setUserName(id);
        AgeRange ageRange = profile.getAgeRange();
        details.setAgeRangeMin(ageRange.getMin());
        details.setAgeRangeMax(ageRange.getMax());

        return details;
    }

    String getEmail(FacebookProfile profile) {
        String email = profile.getEmail();
        if (Strings.isNullOrEmpty(email)) {
            String id = profile.getId();
            log.info("Empty or absent email for id [{}]", id);
            email = String.format("%s@facebook.com", id);
        }
        return email;
    }

    Date getBirthdayDate(FacebookProfile profile) {
        String birthday = profile.getBirthday();
        if (!Strings.isNullOrEmpty(birthday)) {
            try {
                return new SimpleDateFormat(DATE_FORMAT).parse(birthday);
            }
            catch (ParseException e) {
                log.error(String.format("Can't parse birthday: [%s]", birthday), e);
            }
        }
        return null;
    }

    void assignCityAndCountry(FacebookProfile profile, FacebookUserInfo details) {
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