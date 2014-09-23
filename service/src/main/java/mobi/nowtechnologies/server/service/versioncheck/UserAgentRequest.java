package mobi.nowtechnologies.server.service.versioncheck;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.ClientVersion;

public interface UserAgentRequest {
    String getApplicationName();

    ClientVersion getVersion();

    DeviceType getPlatform();

    Community getCommunity();
}
