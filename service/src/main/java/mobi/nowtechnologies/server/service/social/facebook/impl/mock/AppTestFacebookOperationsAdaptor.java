package mobi.nowtechnologies.server.service.social.facebook.impl.mock;

import mobi.nowtechnologies.server.service.social.facebook.impl.FacebookOperationsAdaptor;

import javax.annotation.Resource;

import org.springframework.social.facebook.api.FacebookProfile;

public class AppTestFacebookOperationsAdaptor extends FacebookOperationsAdaptor {

    @Resource
    private AppTestFacebookTokenService appTestFacebookTokenService;

    public FacebookProfile getFacebookProfile(String accessToken, String userId) {
        return appTestFacebookTokenService.parseToken(accessToken);
    }
}
