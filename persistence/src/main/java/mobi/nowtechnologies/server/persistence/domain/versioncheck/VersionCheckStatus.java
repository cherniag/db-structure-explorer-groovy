package mobi.nowtechnologies.server.persistence.domain.versioncheck;

import com.google.common.collect.Sets;

import java.util.Set;

public enum VersionCheckStatus {
    CURRENT,
    SUGGESTED_UPDATE,
    FORCED_UPDATE,
    REVOKED,
    MIGRATED;

    public static Set<VersionCheckStatus> getAllStatuses(){
        return Sets.newHashSet(values());
    }

    public static Set<VersionCheckStatus> getAllStatusesWithoutMigrated(){
        Set<VersionCheckStatus> statuses = Sets.newHashSet(VersionCheckStatus.values());
        statuses.remove(VersionCheckStatus.MIGRATED);
        return statuses;
    }
}
