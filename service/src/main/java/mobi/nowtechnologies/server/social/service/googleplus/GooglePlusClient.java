/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.service.googleplus;

import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.social.service.OAuth2ForbiddenException;


public interface GooglePlusClient {

    OAuth2ForbiddenException INVALID_GOOGLE_PLUS_TOKEN_EXCEPTION = OAuth2ForbiddenException.invalidGooglePlusToken();
    OAuth2ForbiddenException INVALID_GOOGLE_PLUS_USER_ID = OAuth2ForbiddenException.invalidGooglePlusUserId();
    OAuth2ForbiddenException EMPTY_GOOGLE_PLUS_EMAIL = OAuth2ForbiddenException.emptyGooglePlusEmail();

    SocialNetworkInfo getProfileUserInfo(String accessToken);

}
