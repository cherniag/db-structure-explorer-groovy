/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.service.googleplus.impl.mock;

import mobi.nowtechnologies.server.social.service.googleplus.impl.GooglePlusOperationsAdaptor;

import javax.annotation.Resource;

import org.springframework.social.google.api.plus.Person;
/**
 * @author Anton Zemliankin
 */
public class AppTestGooglePlusOperationsAdaptor extends GooglePlusOperationsAdaptor {

    @Resource
    private AppTestGooglePlusTokenService appTestGooglePlusTokenService;

    @Override
    public Person getGooglePlusProfile(String accessToken) {
        return appTestGooglePlusTokenService.parseToken(accessToken);
    }

}
