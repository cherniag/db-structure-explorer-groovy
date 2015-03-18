package mobi.nowtechnologies.applicationtests.services.http.facebook;

import mobi.nowtechnologies.server.persistence.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.service.social.facebook.impl.mock.AppTestFacebookTokenService;
import mobi.nowtechnologies.server.shared.dto.OAuthProvider;
import mobi.nowtechnologies.server.shared.enums.Gender;

import javax.annotation.Resource;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class FacebookUserInfoGenerator {

    public static final Gender GENDER = Gender.MALE;
    public static final String CITY = "Pripyat";
//    public static final String COUNTRY = "USSR";
    public static final String FIRST_NAME = "Firstname";
    public static final String SURNAME = "Surname";
    public static final String PROFILE_URL = "profile-url";

    @Resource
    private AppTestFacebookTokenService appTestFacebookTokenService;

    public String createAccessToken(String email, String userName, String facebookUserId) {
        SocialNetworkInfo info = doCreateAccessTokenInfo(email, userName, facebookUserId, false);
        return appTestFacebookTokenService.buildToken(info);
    }

    public String createAccessTokenWithCityOnly(String email, String userName, String facebookUserId) {
        SocialNetworkInfo info = doCreateAccessTokenInfo(email, userName, facebookUserId, true);
        return appTestFacebookTokenService.buildToken(info);
    }

    public String createAccessTokenWithIdError(String email, String userName, String facebookUserId) {
        SocialNetworkInfo info = doCreateAccessTokenInfo(email, userName, facebookUserId, false);
        return appTestFacebookTokenService.buildTokenWithIdError(info);
    }

    public String createAccessTokenWithAccesstokenError(String email, String userName, String facebookUserId) {
        SocialNetworkInfo info = doCreateAccessTokenInfo(email, userName, facebookUserId, false);
        return appTestFacebookTokenService.buildTokenWithTokenError(info);
    }

    private SocialNetworkInfo doCreateAccessTokenInfo(String email, String userName, String facebookUserId, boolean excludeCountry) {
        SocialNetworkInfo info = new SocialNetworkInfo(OAuthProvider.FACEBOOK);
        info.setBirthday(new Date());
        info.setEmail(email);
        info.setGender(GENDER);
        info.setLocation(CITY);
//        info.setCountry(COUNTRY);
        info.setUserName(userName);
        info.setSocialNetworkId(facebookUserId);
        info.setFirstName(FIRST_NAME);
        info.setLastName(SURNAME);
        info.setProfileUrl(PROFILE_URL);

//        if (excludeCountry) {
//            info.setCountry(null);
//        }

        return info;
    }
}
