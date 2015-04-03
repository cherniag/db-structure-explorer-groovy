/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.service.googleplus;

import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GooglePlusService {

    @Resource
    GooglePlusClient googlePlusClient;
    private Logger log = LoggerFactory.getLogger(GooglePlusService.class);

    public SocialNetworkInfo getGooglePlusUserInfo(String accessToken, String inputGooglePlusId) {
        SocialNetworkInfo googlePlusProfileInfo = googlePlusClient.getProfileUserInfo(accessToken);
        if (!googlePlusProfileInfo.getSocialNetworkId().equals(inputGooglePlusId)) {
            log.warn("inputGooglePlusId should match id on GooglePlus!");
            throw GooglePlusClient.INVALID_GOOGLE_PLUS_USER_ID;
        }
        if (StringUtils.isEmpty(googlePlusProfileInfo.getEmail())) {
            log.warn("GooglePlus profile should have email!");
            throw GooglePlusClient.EMPTY_GOOGLE_PLUS_EMAIL;
        }

        return googlePlusProfileInfo;
    }

}
