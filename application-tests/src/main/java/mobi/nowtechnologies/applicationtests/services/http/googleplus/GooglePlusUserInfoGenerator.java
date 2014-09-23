package mobi.nowtechnologies.applicationtests.services.http.googleplus;

import mobi.nowtechnologies.server.apptests.googleplus.AppTestGooglePlusTokenService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;

@Component
public class GooglePlusUserInfoGenerator {
    @Resource
    private AppTestGooglePlusTokenService appTestGooglePlusTokenService;

    public String createAccessToken(String email, String userName, String googlePlusUserId) {
        Calendar c = Calendar.getInstance();
        c.set(1983, Calendar.NOVEMBER, 3, 3, 3, 3);
        long time = c.getTime().getTime();
        return appTestGooglePlusTokenService.build(googlePlusUserId, email, time, userName, "Functional", "Test", "http://WhataTerribleFailure.com/cat.jpg", true, "Kiev, Ukraine", "homepage-url");
    }
}
