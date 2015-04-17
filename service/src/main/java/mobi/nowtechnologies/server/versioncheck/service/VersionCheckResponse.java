/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.versioncheck.service;

import mobi.nowtechnologies.server.versioncheck.domain.VersionCheckStatus;


public class VersionCheckResponse {

    private String messageKey;

    private VersionCheckStatus status;

    private String uri;

    private String imageFileName;

    public VersionCheckResponse(String messageKey, VersionCheckStatus status, String uri, String imageFileName) {
        this.messageKey = messageKey;
        this.status = status;
        this.uri = uri;
        this.imageFileName = imageFileName;
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

    public String getImageFileName() {
        return imageFileName;
    }
}
