/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.service.facebook.impl.mock;

import mobi.nowtechnologies.server.social.service.facebook.FacebookProfileImage;
import mobi.nowtechnologies.server.social.service.facebook.impl.FacebookOperationsAdaptor;

import javax.annotation.Resource;

import org.springframework.social.facebook.api.User;

public class AppTestFacebookOperationsAdaptor extends FacebookOperationsAdaptor {

    public final static String TEST_PROFILE_IMAGE_URL = "http://test-server.com/images/test-image.png";

    @Resource
    private AppTestFacebookTokenService appTestFacebookTokenService;

    @Override
    public User getFacebookProfile(String accessToken, String userId) {
        return appTestFacebookTokenService.parseToken(accessToken);
    }

    @Override
    public FacebookProfileImage getFacebookProfileImage(String accessToken, String userId) {
        return new FacebookProfileImage(TEST_PROFILE_IMAGE_URL, false);
    }
}
