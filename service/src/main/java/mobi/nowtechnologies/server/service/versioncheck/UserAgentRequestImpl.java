package mobi.nowtechnologies.server.service.versioncheck;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;

/**
 * Created by Oleg Artomov on 9/11/2014.
 */
public class UserAgentRequestImpl implements UserAgentRequest {
    private String applicationName;

    private ClientVersionImpl version;

    private DeviceType platform;

    private Community community;

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setVersion(ClientVersionImpl version) {
        this.version = version;
    }

    public void setPlatform(DeviceType platform) {
        this.platform = platform;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }


    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public ClientVersion getVersion() {
        return version;
    }

    @Override
    public DeviceType getPlatform() {
        return platform;
    }

    @Override
    public Community getCommunity() {
        return community;
    }
}