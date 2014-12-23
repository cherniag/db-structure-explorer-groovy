package mobi.nowtechnologies.server.service.social.facebook;

import com.google.common.base.Splitter;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.shared.enums.Gender;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.GraphApi;
import org.springframework.social.facebook.api.Reference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by oar on 3/14/14.
 */
public class FacebookDataConverter {
    private static final String DATE_FORMAT = "MM/dd/yyyy";

    private Logger logger = LoggerFactory.getLogger(getClass());

    public FacebookUserInfo convert(FacebookProfile profile) {
        final String usernameOrId = getFacebookUserName(profile);

        FacebookUserInfo details = new FacebookUserInfo();
        details.setEmail(emailOrOtherId(profile));
        details.setFirstName(profile.getFirstName());
        details.setSurname(profile.getLastName());
        details.setFacebookId(profile.getId());
        details.setUserName(usernameOrId);
        details.setProfileUrl(GraphApi.GRAPH_API_URL + usernameOrId + "/picture?type=large");
        details.setGender(Gender.restore(profile.getGender()));
        details.setBirthday(extractBirthDay(profile));
        assignCityAndCountry(profile, details);
        return details;
    }

    private String emailOrOtherId(FacebookProfile profile) {
        String email = profile.getEmail();

        if (StringUtils.isEmpty(email)) {
            String facebookUserName = getFacebookUserName(profile);
            logger.info("Empty or absent email for id [{}]", facebookUserName);
            return facebookUserName + "@facebook.com";
        }

        return email;
    }

    private String getFacebookUserName(FacebookProfile profile) {
        // After 30 Apr 2014 username is null
        boolean beforeVersion2 = StringUtils.isNotEmpty(profile.getUsername());

        return beforeVersion2 ? profile.getUsername() : profile.getId();
    }

    private Date extractBirthDay(FacebookProfile profile) {
        String birthDay = profile.getBirthday();
        if (!isEmpty(birthDay)) {
            try {
                return new SimpleDateFormat(DATE_FORMAT).parse(birthDay);
            } catch (ParseException e) {
                logger.error("ERROR during parse", e);
            }
        }
        return null;
    }

    private void assignCityAndCountry(FacebookProfile profile, FacebookUserInfo details) {
        Reference loc = profile.getLocation();
        if (loc != null) {
            String cityWithCountry = loc.getName();
            if (!isEmpty(cityWithCountry)) {
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
