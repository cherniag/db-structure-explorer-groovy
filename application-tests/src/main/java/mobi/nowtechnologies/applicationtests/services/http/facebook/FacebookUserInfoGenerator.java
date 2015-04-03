package mobi.nowtechnologies.applicationtests.services.http.facebook;

import mobi.nowtechnologies.server.social.domain.GenderType;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.social.domain.SocialNetworkType;
import mobi.nowtechnologies.server.social.service.facebook.impl.mock.AppTestFacebookTokenService;

import javax.annotation.Resource;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class FacebookUserInfoGenerator {

    public static final String CITY = "Pripyat";
    public static final String COUNTRY = "USSR";
    public static final String FIRST_NAME = "Firstname";
    public static final String SURNAME = "Surname";

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
        SocialNetworkInfo info = new SocialNetworkInfo(SocialNetworkType.FACEBOOK);
        info.setBirthday(new Date());
        info.setEmail(email);
        info.setGenderType(GenderType.MALE);
        info.setCity(CITY);
        info.setUserName(userName);
        info.setSocialNetworkId(facebookUserId);
        info.setFirstName(FIRST_NAME);
        info.setLastName(SURNAME);

        if (!excludeCountry) {
            info.setCountry(COUNTRY);
        }

        return info;
    }
}
