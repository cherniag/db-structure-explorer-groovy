/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.versioncheck.service;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.versioncheck.domain.ClientVersion;
import mobi.nowtechnologies.server.versioncheck.domain.VersionCheck;
import mobi.nowtechnologies.server.versioncheck.domain.VersionCheckRepository;
import mobi.nowtechnologies.server.versioncheck.domain.VersionCheckStatus;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Iterables;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


public class VersionCheckService {

    @Resource
    private VersionCheckRepository versionCheckRepository;

    private VersionCheckResponse currentVersionResponse;

    private Pageable pagingAndSortingData;

    public VersionCheckResponse check(int communityId, DeviceType platform, String applicationName, ClientVersion v, Set<VersionCheckStatus> includedStatuses) {
        List<VersionCheck> possibleVersions = getVersionChecks(communityId, platform, applicationName, v, includedStatuses);

        VersionCheck needVersion = Iterables.get(possibleVersions, 0, null);
        if (needVersion == null) {
            return currentVersionResponse;
        }

        return new VersionCheckResponse(needVersion.getMessage().getMessageKey(), needVersion.getStatus(), needVersion.getMessage().getUrl(), needVersion.getImageFileName());
    }

    private List<VersionCheck> getVersionChecks(int communityId, DeviceType platform, String applicationName, ClientVersion v, Set<VersionCheckStatus> includedStatuses) {
        if (v.qualifier() == null) {
            return versionCheckRepository.findSuitableVersions(communityId, platform, applicationName, v.major(), v.minor(), v.revision(), includedStatuses, pagingAndSortingData);
        } else {
            return versionCheckRepository.findSuitableVersionsWithQualifier(communityId,
                                                                            platform,
                                                                            applicationName,
                                                                            v.major(),
                                                                            v.minor(),
                                                                            v.revision(),
                                                                            v.qualifier(),
                                                                            includedStatuses,
                                                                            pagingAndSortingData);
        }
    }

    @PostConstruct
    private void init() {
        currentVersionResponse = new VersionCheckResponse(null, VersionCheckStatus.CURRENT, null, null);

        Sort sorting = new Sort(new Sort.Order(Sort.Direction.ASC, VersionCheck.MAJOR_NUMBER_PROPERTY_NAME),
                                new Sort.Order(Sort.Direction.ASC, VersionCheck.MINOR_NUMBER_PROPERTY_NAME),
                                new Sort.Order(Sort.Direction.ASC, VersionCheck.REVISION_NUMBER_PROPERTY_NAME));
        pagingAndSortingData = new PageRequest(0, 1, sorting);
    }

}
