/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.service.facebook;

import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FacebookService {

    private static Logger log = LoggerFactory.getLogger(FacebookService.class);

    @Resource
    FacebookClient facebookClient;

    String userId;
    String userProfileImageUrlId;

    public SocialNetworkInfo getFacebookUserInfo(String accessToken, String inputFacebookId) {
        SocialNetworkInfo facebookProfileInfo = facebookClient.getProfileUserInfo(accessToken, userId);
        if (!facebookProfileInfo.getSocialNetworkId().equals(inputFacebookId)) {
            log.warn("inputFacebookId should match id on Facebook!");
            throw FacebookClient.INVALID_FACEBOOK_USER_ID;
        }

        FacebookProfileImage facebookProfileImage = facebookClient.getProfileImage(accessToken, userProfileImageUrlId);
        facebookProfileInfo.setProfileImageUrl(facebookProfileImage.getUrl());
        facebookProfileInfo.setProfileImageSilhouette(facebookProfileImage.isSilhouette());

        return facebookProfileInfo;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserProfileImageUrlId(String userProfileImageUrlId) {
        this.userProfileImageUrlId = userProfileImageUrlId;
    }
}
