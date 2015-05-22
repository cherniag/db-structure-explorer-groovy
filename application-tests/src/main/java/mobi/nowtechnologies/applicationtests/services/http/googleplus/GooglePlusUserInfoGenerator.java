package mobi.nowtechnologies.applicationtests.services.http.googleplus;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.social.domain.GenderType;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.social.domain.SocialNetworkType;
import mobi.nowtechnologies.server.social.service.googleplus.impl.mock.AppTestGooglePlusTokenService;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

@Component
public class GooglePlusUserInfoGenerator {

    private static final String GIVEN_NAME = "Functional";
    private static final String FAMILY_NAME = "Test";
    private static final String IMAGE_URL = "http://WhataTerribleFailure.com/cat.jpg";
    private static final String LOCATION = "Kiev, Ukraine";

    @Resource
    private AppTestGooglePlusTokenService appTestGooglePlusTokenService;

    public String createAccessToken(String email, String userName, String googlePlusUserId) {
        SocialNetworkInfo info = doCreateAccessTokenInfo(googlePlusUserId, email, userName);
        return appTestGooglePlusTokenService.buildToken(info);
    }

    public String createAccessTokenWithAuthError(String email, String userName, String googlePlusUserId) {
        SocialNetworkInfo info = doCreateAccessTokenInfo(googlePlusUserId, email, userName);
        return appTestGooglePlusTokenService.buildTokenWithTokenError(info);
    }

    private SocialNetworkInfo doCreateAccessTokenInfo(String googlePlusUserId, String email, String userName) {
        SocialNetworkInfo info = new SocialNetworkInfo(SocialNetworkType.GOOGLE);
        info.setSocialNetworkId(googlePlusUserId);
        info.setEmail(email);
        info.setBirthday(DateTimeUtils.newDate(3, 11, 1983));
        info.setUserName(userName);
        info.setFirstName(GIVEN_NAME);
        info.setLastName(FAMILY_NAME);
        info.setProfileImageUrl(IMAGE_URL);
        info.setGenderType(GenderType.MALE);
        info.setCity(LOCATION);
        return info;
    }
}
