/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.itunes;

/**
 * Created by zam on 1/26/2015.
 */
public interface ITunesConnectionConfig {

    String APPLE_IN_APP_I_TUNES_URL = "apple.inApp.iTunesUrl";
    String APPLE_IN_APP_PASSWORD = "apple.inApp.password";

    String getUrl();

    String getPassword();
}
