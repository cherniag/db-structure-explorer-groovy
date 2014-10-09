package mobi.nowtechnologies.server.service.social.facebook;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.shared.CollectionUtils;
import mobi.nowtechnologies.server.shared.enums.Gender;
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
        details.setEmail(profile.getEmail());
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

    private String getFacebookUserName(FacebookProfile profile) {
        // After 30 Apr 2014 username is null
        boolean beforeVersion2 = profile.getUsername() != null;

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
                Iterable<String> resultOfSplit = Splitter.on(',').omitEmptyStrings().trimResults().split(cityWithCountry);
                List<String> result = Lists.newArrayList(resultOfSplit);
                details.setCity(CollectionUtils.get(result, 0, null));
                details.setCountry(CollectionUtils.get(result, 1, null));
            }
        }
    }
}
