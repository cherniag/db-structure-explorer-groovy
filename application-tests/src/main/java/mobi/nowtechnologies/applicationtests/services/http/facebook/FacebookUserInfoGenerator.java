package mobi.nowtechnologies.applicationtests.services.http.facebook;

import mobi.nowtechnologies.server.apptests.facebook.AppTestDummyFacebookTokenComposer;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.shared.enums.Gender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class FacebookUserInfoGenerator {
    @Resource
    private AppTestDummyFacebookTokenComposer appTestDummyFacebookTokenComposer;

    public String createAccessToken(String email, String userName, String facebookUserId) {
        FacebookUserInfo info = doCreateAccessTokenInfo(email, userName, facebookUserId, false);
        return appTestDummyFacebookTokenComposer.buildToken(info);
    }

    public String createAccessTokenWithCityOnly(String email, String userName, String facebookUserId) {
        FacebookUserInfo info = doCreateAccessTokenInfo(email, userName, facebookUserId, true);
        return appTestDummyFacebookTokenComposer.buildToken(info);
    }

    public String createAccessTokenWithIdError(String email, String userName, String facebookUserId) {
        FacebookUserInfo info = doCreateAccessTokenInfo(email, userName, facebookUserId, false);
        return appTestDummyFacebookTokenComposer.buildTokenWithIdError(info);
    }

    public String createAccessTokenWithAccesstokenError(String email, String userName, String facebookUserId) {
        FacebookUserInfo info = doCreateAccessTokenInfo(email, userName, facebookUserId, false);
        return appTestDummyFacebookTokenComposer.buildTokenWithTokenError(info);
    }

    private FacebookUserInfo doCreateAccessTokenInfo(String email, String userName, String facebookUserId, boolean excludeCountry) {
        FacebookUserInfo info = new FacebookUserInfo();
        info.setBirthday(new Date());
        info.setEmail(email);
        info.setGender(Gender.MALE);
        info.setCity("Pripyat");
        info.setCountry("USSR");
        info.setUserName(userName);
        info.setFacebookId(facebookUserId);
        info.setFirstName("Firstname");
        info.setSurname("Surname");
        info.setProfileUrl("profile-url");

        if(excludeCountry) {
            info.setCountry(null);
        }

        return info;
    }
}
