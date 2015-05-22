/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.support;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.versioncheck.domain.ClientVersion;

public interface UserAgent {

    String getApplicationName();

    ClientVersion getVersion();

    DeviceType getPlatform();

    Community getCommunity();
}
