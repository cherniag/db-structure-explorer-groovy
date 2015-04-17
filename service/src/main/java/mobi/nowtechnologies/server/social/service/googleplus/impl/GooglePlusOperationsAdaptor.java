/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.service.googleplus.impl;

import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.plus.Person;

/**
 * @author Anton Zemliankin
 */

public class GooglePlusOperationsAdaptor {

    public Person getGooglePlusProfile(String accessToken) {
        GoogleTemplate googleTemplate = new GoogleTemplate(accessToken);
        return googleTemplate.plusOperations().getGoogleProfile();
    }
}
