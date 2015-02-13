package mobi.nowtechnologies.server.apptests.googleplus;

import mobi.nowtechnologies.server.shared.enums.Gender;
import org.springframework.social.google.api.plus.Person;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class AppTestGooglePlusTokenService {

    private static final String AUTH_ERROR = "AUTH_ERROR";

    public String build(String id,
                        String email,
                        long time,
                        String displayName,
                        String givenName,
                        String familyName,
                        String imageUrl,
                        boolean male,
                        String location,
                        String homepageUrl) {
        return                        // index
                id + "#" +            // 0
                email + "#" +         // 1
                time + "#" +          // 2
                displayName + "#" +   // 3
                givenName + "#" +     // 4
                familyName + "#" +    // 5
                imageUrl + "#" +      // 6
                male  + "#" +         // 7
                location + "#" +      // 8
                homepageUrl;    // 9
    }

    public String buildTokenWithTokenError(String id,
                                           String email,
                                           long time,
                                           String displayName,
                                           String givenName,
                                           String familyName,
                                           String imageUrl,
                                           boolean male,
                                           String location,
                                           String homepageUrl) {
        return build(id, email, time, displayName, givenName, familyName, imageUrl, male, location, homepageUrl) + "#" + AUTH_ERROR;   // 10
    }

    public Person parse(String token) {
        final String[] values = token.split("#");

        if (values.length == 11 && values[10].equals(AUTH_ERROR)) {
            return new FailureGooglePlusPerson();
        }

        return new Person() {
            @Override
            public String getUrl() {
                return values[9];
            }

            @Override
            public String getId() {
                return values[0];
            }

            @Override
            public String getGivenName() {
                return values[4];
            }

            @Override
            public String getFamilyName() {
                return values[5];
            }

            @Override
            public String getDisplayName() {
                return values[3];
            }

            @Override
            public String getImageUrl() {
                return values[6];
            }

            @Override
            public Date getBirthday() {
                return new Date(Long.parseLong(values[2]));
            }

            @Override
            public String getGender() {
                return (Boolean.valueOf(values[7])) ? Gender.MALE.getKey() : Gender.FEMALE.getKey() ;
            }

            @Override
            public String getAccountEmail() {
                return values[1];
            }

            @Override
            public Map<String, Boolean> getPlacesLived() {
                return Collections.singletonMap(values[8], true);
            }
        };
    }
}
