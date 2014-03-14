package mobi.nowtechnologies.server.service.facebook;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.User;
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

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public FacebookUserInfo convertForUser(User user, FacebookProfile profile) {
        FacebookUserInfo details = new FacebookUserInfo();
        details.setEmail(profile.getEmail());
        details.setFirstName(profile.getFirstName());
        details.setSurname(profile.getLastName());
        details.setFacebookId(profile.getId());
        details.setUserName(profile.getUsername());
        details.setProfileUrl(GraphApi.GRAPH_API_URL + profile.getUsername() + "/picture?type=large");
        details.setUser(user);
        details.setGender(extractGender(profile));
        details.setBirthday(extractBirthDay(profile));
        assignCityAndCountry(profile, details);
        return details;
    }

    private Date extractBirthDay(FacebookProfile profile) {
        String birthDay = profile.getBirthday();
        if (!isEmpty(birthDay)) {
            try {
                return dateFormat.parse(birthDay);
            } catch (ParseException e) {
                logger.error("ERROR during parse", e);
            }
        }
        return null;
    }

    private Gender extractGender(FacebookProfile profile) {
        String gender = profile.getGender();
        if (!isEmpty(gender)) {
            return gender.equals("male") ? Gender.MALE : Gender.FEMALE;
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
