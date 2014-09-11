package mobi.nowtechnologies.server.service.versioncheck;

import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheckStatus;

import java.net.URI;

public class VersionCheckResponse {
    private String messageKey;

    private VersionCheckStatus status;

    private URI uri;

    public VersionCheckResponse(String messageKey, VersionCheckStatus status, URI uri) {
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

    public URI getUri() {
        return uri;
    }
}
