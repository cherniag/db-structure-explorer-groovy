package mobi.nowtechnologies.server.service.social.facebook.impl.mock;

import mobi.nowtechnologies.server.service.social.facebook.impl.FacebookOperationsAdaptor;
import mobi.nowtechnologies.server.service.social.facebook.impl.FacebookProfileImage;

import javax.annotation.Resource;

import org.springframework.social.facebook.api.FacebookProfile;

public class AppTestFacebookOperationsAdaptor extends FacebookOperationsAdaptor {

    public final static String TEST_PROFILE_IMAGE_URL = "http://test-server.com/images/test-image.png";

    @Resource
    private AppTestFacebookTokenService appTestFacebookTokenService;

    @Override
    public FacebookProfile getFacebookProfile(String accessToken, String userId) {
        return appTestFacebookTokenService.parseToken(accessToken);
    }

    @Override
    public FacebookProfileImage getFacebookProfileImage(String accessToken, String userId) {
        return new FacebookProfileImage(TEST_PROFILE_IMAGE_URL, false);
    }
}
