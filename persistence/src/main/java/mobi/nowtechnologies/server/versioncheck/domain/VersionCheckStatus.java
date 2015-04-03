/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.versioncheck.domain;

import java.util.Set;

import com.google.common.collect.Sets;

public enum VersionCheckStatus {
    CURRENT,
    SUGGESTED_UPDATE,
    FORCED_UPDATE,
    REVOKED,
    MIGRATED;

    public static Set<VersionCheckStatus> getAllStatuses() {
        return Sets.newHashSet(values());
    }

    public static Set<VersionCheckStatus> getAllStatusesWithoutMigrated() {
        Set<VersionCheckStatus> statuses = Sets.newHashSet(VersionCheckStatus.values());
        statuses.remove(VersionCheckStatus.MIGRATED);
        return statuses;
    }
}
