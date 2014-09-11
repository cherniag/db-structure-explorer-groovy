package mobi.nowtechnologies.server.dto.transport;

import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheckStatus;

public class ServiceConfigDto {
    private VersionCheckStatus versionCheckStatus;
    private String message;
    private String link;

    public ServiceConfigDto(VersionCheckStatus versionCheckStatus, String message, String link) {
        this.versionCheckStatus = versionCheckStatus;
        this.message = message;
        this.link = link;
    }

    public VersionCheckStatus getVersionCheckStatus() {
        return versionCheckStatus;
    }

    public String getMessage() {
        return message;
    }

    public String getLink() {
        return link;
    }
}
