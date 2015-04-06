/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.service.googleplus.impl.mock;

import mobi.nowtechnologies.server.social.domain.GenderType;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;

import java.util.Collections;
import java.util.Date;

import org.springframework.social.google.api.plus.Person;
import org.springframework.web.client.RestClientException;

public class AppTestGooglePlusTokenService {

    private static final String AUTH_ERROR = "AUTH_ERROR";

    public String buildToken(SocialNetworkInfo info) {
        return                                      // index
            info.getSocialNetworkId() + "#" +    // 0
            info.getEmail() + "#" +              // 1
            info.getBirthday().getTime() + "#" + // 2
            info.getUserName() + "#" +           // 3
            info.getFirstName() + "#" +          // 4
            info.getLastName() + "#" +           // 5
            info.getProfileImageUrl() + "#" +    // 6
            info.getGenderType() + "#" +         // 7
            info.getCity();                      // 8
    }

    public String buildTokenWithTokenError(SocialNetworkInfo info) {
        return buildToken(info) + "#" + AUTH_ERROR;   // 9
    }

    public Person parseToken(String token) {
        final String[] split = token.split("#");

        if (split.length == 10 && split[9].equals(AUTH_ERROR)) {
            throw new RestClientException("token error");
        } else {
            GenderType gender = GenderType.valueOf(split[7]);
            Date birthday = new Date(Long.parseLong(split[2]));

            return new SuccessfulGooglePlusProfile(split[0], split[4], split[5], split[3], split[6], birthday, gender.getKey(), split[1], Collections.singletonMap(split[8], true));
        }

    }
}
