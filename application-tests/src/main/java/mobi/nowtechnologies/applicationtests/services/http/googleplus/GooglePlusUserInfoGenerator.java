package mobi.nowtechnologies.applicationtests.services.http.googleplus;

import mobi.nowtechnologies.server.service.social.googleplus.impl.mock.AppTestGooglePlusTokenService;

import javax.annotation.Resource;

import java.util.Calendar;

import org.springframework.stereotype.Component;

@Component
public class GooglePlusUserInfoGenerator {

    private static final String GIVEN_NAME = "Functional";
    private static final String FAMILY_NAME = "Test";
    private static final String IMAGE_URL = "http://WhataTerribleFailure.com/cat.jpg";
    private static final boolean MALE = true;
    private static final String LOCATION = "Kiev, Ukraine";
    private static final String URL = "homepage-url";

    @Resource
    private AppTestGooglePlusTokenService appTestGooglePlusTokenService;

    public String createAccessToken(String email, String userName, String googlePlusUserId) {
        return appTestGooglePlusTokenService.build(googlePlusUserId, email, getDate(), userName, GIVEN_NAME, FAMILY_NAME, IMAGE_URL, MALE, LOCATION, URL);
    }

    public String createAccessTokenWithAuthError(String email, String userName, String googlePlusUserId) {
        return appTestGooglePlusTokenService.buildTokenWithTokenError(googlePlusUserId, email, getDate(), userName, GIVEN_NAME, FAMILY_NAME, IMAGE_URL, MALE, LOCATION, URL);
    }

    private long getDate() {
        Calendar c = Calendar.getInstance();
        c.set(1983, Calendar.NOVEMBER, 3, 3, 3, 3);
        return c.getTime().getTime();
    }
}
