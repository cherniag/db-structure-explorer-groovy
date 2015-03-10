package mobi.nowtechnologies.server.service.social.facebook.impl.mock;

import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.service.social.facebook.FacebookClient;

import java.text.Format;
import java.text.SimpleDateFormat;

import org.springframework.social.MissingAuthorizationException;
import org.springframework.social.facebook.api.FacebookProfile;

public class AppTestFacebookTokenService {

    public static final String NULL_VALUE_MARKER = "NULL_VALUE_MARKER";

    public static final String ERROR_ID_MARKER = "ERROR_ID_MARKER";
    public static final String ERROR_TOKEN_MARKER = "ERROR_TOKEN_MARKER";

    public static String maskNullValueIfNeed(String value) {
        if (value == null) {
            return NULL_VALUE_MARKER;
        } else {
            return value;
        }
    }

    public static String unmaskNullValueIfNeeded(String value) {
        if (NULL_VALUE_MARKER.equals(value)) {
            return null;
        } else {
            return value;
        }
    }

    public String buildToken(FacebookUserInfo info) {
        String birthdayString = getDateFormat().format(info.getBirthday());

        return info.getFacebookId() + "#" +    // 0
               info.getEmail() + "#" +        // 1
               info.getFirstName() + "#" +    // 2
               info.getSurname() + "#" +      // 3
               info.getCity() + "#" +         // 4
               info.getProfileUrl() + "#" +   // 5
               info.getUserName() + "#" +     // 6
               // for the case when we need to omit the country: put NULL_VALUE_MARKER value instead and recognize it later (unparse)
               maskNullValueIfNeed(info.getCountry()) + "#" + // 7
               info.getGender().name().toLowerCase() + "#" +  // 8
               birthdayString;                                // 9
    }

    public String buildTokenWithIdError(FacebookUserInfo info) {
        return buildToken(info) + "#" + ERROR_ID_MARKER;      // 10
    }

    public String buildTokenWithTokenError(FacebookUserInfo info) {
        return buildToken(info) + "#" + ERROR_TOKEN_MARKER;   // 10
    }

    public FacebookProfile parseToken(String token) {
        final String[] split = token.split("#");

        // take a look on the error marker in the tail: on 10th position there is a error marker
        if (split.length == 11) {
            // reassign the id to simulate the wrong ID
            if (split[10].equals(ERROR_ID_MARKER)) {
                SuccessfulFacebookProfile profile = new SuccessfulFacebookProfile("_broken_different_id_", split[6], "", split[2], split[3], split[8]);
                profile.addOtherInfo(split[1], split[7], split[4], split[9]);
                return profile;
            }

            // create a different type of FacebookProfile to let Template Provider know that
            // it should throw the expected Exception (org.springframework.social.MissingAuthorizationException)
            if (split[10].equals(ERROR_TOKEN_MARKER)) {
                throw new MissingAuthorizationException("provider id");
            }

            throw new IllegalArgumentException();
        } else {
            // no error markers:
            SuccessfulFacebookProfile profile = new SuccessfulFacebookProfile(split[0], split[6], "", split[2], split[3], split[8]);
            profile.addOtherInfo(split[1], split[7], split[4], split[9]);
            return profile;
        }
    }

    private Format getDateFormat() {
        return new SimpleDateFormat(FacebookClient.DATE_FORMAT);
    }

}
