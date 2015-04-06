/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.service;

import mobi.nowtechnologies.server.service.exception.ServiceException;

/**
 * Created by oar on 2/17/14.
 */
public class OAuth2ForbiddenException extends ServiceException {

    private OAuth2ForbiddenException(SocialErrorCodes errorCode) {
        super(errorCode.errorCode, errorCode.message, errorCode.message);
    }

    public static OAuth2ForbiddenException invalidFacebookToken() {
        return new OAuth2ForbiddenException(SocialErrorCodes.INVALID_FACEBOOK_TOKEN);
    }

    public static OAuth2ForbiddenException invalidFacebookUserId() {
        return new OAuth2ForbiddenException(SocialErrorCodes.INVALID_FACEBOOK_USER_ID);
    }

    public static OAuth2ForbiddenException invalidGooglePlusToken() {
        return new OAuth2ForbiddenException(SocialErrorCodes.INVALID_GOOGLE_PLUS_TOKEN);
    }

    public static OAuth2ForbiddenException invalidGooglePlusUserId() {
        return new OAuth2ForbiddenException(SocialErrorCodes.INVALID_GOOGLE_PLUS_USER_ID);
    }

    public static OAuth2ForbiddenException emptyGooglePlusEmail() {
        return new OAuth2ForbiddenException(SocialErrorCodes.EMPTY_GOOGLE_PLUS_EMAIL);
    }

    private static enum SocialErrorCodes {
        INVALID_FACEBOOK_TOKEN("660", "invalid authorization token"),
        INVALID_FACEBOOK_USER_ID("661", "invalid user facebook id"),
        EMPTY_FACEBOOK_EMAIL("662", "email is not specified"),

        INVALID_GOOGLE_PLUS_TOKEN("760", "invalid authorization token"),
        INVALID_GOOGLE_PLUS_USER_ID("761", "invalid user google plus id"),
        EMPTY_GOOGLE_PLUS_EMAIL("762", "email is not specified");

        private final String errorCode;
        private final String message;

        SocialErrorCodes(String errorCode, String message) {
            this.errorCode = errorCode;
            this.message = message;
        }
    }
}
