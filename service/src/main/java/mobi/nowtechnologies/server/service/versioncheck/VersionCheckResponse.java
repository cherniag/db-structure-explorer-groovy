package mobi.nowtechnologies.server.service.versioncheck;

import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheckStatus;

public class VersionCheckResponse {
    private String messageKey;

    private VersionCheckStatus status;

    private String uri;

    public VersionCheckResponse(String messageKey, VersionCheckStatus status, String uri) {
        this.messageKey = messageKey;
        this.status = status;
        this.uri = uri;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public VersionCheckStatus getStatus() {
        return status;
    }

    public String getUri() {
        return uri;
    }
}
