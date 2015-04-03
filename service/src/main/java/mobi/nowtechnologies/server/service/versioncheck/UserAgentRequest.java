package mobi.nowtechnologies.server.service.versioncheck;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.versioncheck.domain.ClientVersion;

public interface UserAgentRequest {

    String getApplicationName();

    ClientVersion getVersion();

    DeviceType getPlatform();

    Community getCommunity();
}
