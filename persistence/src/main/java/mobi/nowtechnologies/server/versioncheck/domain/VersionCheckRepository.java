/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.versioncheck.domain;

import mobi.nowtechnologies.server.device.domain.DeviceType;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Oleg Artomov on 9/11/2014.
 */
public interface VersionCheckRepository extends JpaRepository<VersionCheck, Long> {

    @Query(value = "select entity from #{#entityName} entity where " +
                   "(entity.communityId=?1 and entity.deviceType=?2 and entity.applicationName=?3) and " +
                   "(entity.majorNumber>?4 or " +
                   "(entity.majorNumber = ?4 and entity.minorNumber>?5) or " +
                   "(entity.majorNumber = ?4 and entity.minorNumber = ?5 and entity.revisionNumber>?6) or " +
                   "(entity.majorNumber = ?4 and entity.minorNumber = ?5 and entity.revisionNumber=?6)) and " +
                   "entity.status in ?7")
    List<VersionCheck> findSuitableVersions(int communityId,
                                            DeviceType platform,
                                            String applicationName,
                                            int majorNumber,
                                            int minorNumber,
                                            int revisionNumber,
                                            Collection<VersionCheckStatus> includedStatuses,
                                            Pageable pageable);

    @Query(value = "select entity from #{#entityName} entity where " +
                   "entity.communityId=?1 and entity.deviceType=?2 and entity.applicationName=?3 and " +
                   "entity.majorNumber = ?4 and entity.minorNumber = ?5 and entity.revisionNumber=?6 and entity.qualifier=?7 and " +
                   "entity.status in ?8")
    List<VersionCheck> findSuitableVersionsWithQualifier(int communityId,
                                                         DeviceType platform,
                                                         String applicationName,
                                                         int majorNumber,
                                                         int minorNumber,
                                                         int revisionNumber,
                                                         String qualifier,
                                                         Collection<VersionCheckStatus> includedStatuses,
                                                         Pageable pageable);
}
