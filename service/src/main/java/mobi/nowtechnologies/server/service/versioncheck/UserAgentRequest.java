package mobi.nowtechnologies.server.service.versioncheck;

public interface UserAgentRequest {
    String getApplicationName();

    ClientVersion getVersion();

    String getPlatform();

    String getCommunity();
}
