/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.service.facebook.impl;

import mobi.nowtechnologies.server.social.service.facebook.FacebookProfileImage;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.social.InvalidAuthorizationException;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.UserOperations;
import org.springframework.social.facebook.api.impl.FacebookTemplate;

public class FacebookOperationsAdaptor {

    private static Logger log = LoggerFactory.getLogger(FacebookOperationsAdaptor.class);

    public User getFacebookProfile(String accessToken, String userId) {
        UserOperations userOperations = new FacebookTemplate(accessToken).userOperations();
        return Strings.isNullOrEmpty(userId) ?
               userOperations.getUserProfile() :
               userOperations.getUserProfile(userId);
    }

    public FacebookProfileImage getFacebookProfileImage(String accessToken, String userId) {
        Facebook facebook = new FacebookTemplate(accessToken);
        JsonNode res;
        try {
            res = facebook.fetchObject(userId, JsonNode.class);

            String url = res.get("data").get("url").asText();
            boolean isSilhouette = res.get("data").get("is_silhouette").asBoolean();

            return new FacebookProfileImage(url, isSilhouette);
        } catch (InvalidAuthorizationException e) {
            log.warn("Could not obtain profile image url, facebook user id: " + userId, e);
            return null;
        }
    }
}
