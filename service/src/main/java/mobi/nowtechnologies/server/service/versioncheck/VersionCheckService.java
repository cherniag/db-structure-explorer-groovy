package mobi.nowtechnologies.server.service.versioncheck;

import mobi.nowtechnologies.server.persistence.domain.versioncheck.ClientVersion;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheck;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheckStatus;
import mobi.nowtechnologies.server.persistence.repository.VersionCheckRepository;
import static mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheck.MAJOR_NUMBER_PROPERTY_NAME;
import static mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheck.MINOR_NUMBER_PROPERTY_NAME;
import static mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheck.REVISION_NUMBER_PROPERTY_NAME;
import static mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheckStatus.CURRENT;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Iterables;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Created by Oleg Artomov on 9/11/2014.
 */
public class VersionCheckService {

    @Resource
    private VersionCheckRepository versionCheckRepository;

    private VersionCheckResponse currentVersionResponse;

    private Pageable pagingAndSortingData;

    public VersionCheckResponse check(UserAgentRequest userAgent, Set<VersionCheckStatus> includedStatuses) {
        List<VersionCheck> possibleVersions = getVersionChecks(userAgent, includedStatuses);

        VersionCheck needVersion = Iterables.get(possibleVersions, 0, null);
        if (needVersion == null) {
            return currentVersionResponse;
        }

        return new VersionCheckResponse(needVersion.getMessage().getMessageKey(), needVersion.getStatus(), needVersion.getMessage().getUrl(), needVersion.getImageFileName());
    }

    private List<VersionCheck> getVersionChecks(UserAgentRequest userAgent, Set<VersionCheckStatus> includedStatuses) {
        ClientVersion v = userAgent.getVersion();
        if (v.qualifier() == null) {
            return versionCheckRepository
                .findSuitableVersions(userAgent.getCommunity(), userAgent.getPlatform(), userAgent.getApplicationName(), v.major(), v.minor(), v.revision(), includedStatuses, pagingAndSortingData);
        }
        else {
            return versionCheckRepository
                .findSuitableVersionsWithQualifier(userAgent.getCommunity(), userAgent.getPlatform(), userAgent.getApplicationName(), v.major(), v.minor(), v.revision(), v.qualifier(),
                                                   includedStatuses, pagingAndSortingData);
        }
    }

    @PostConstruct
    private void init() {
        currentVersionResponse = new VersionCheckResponse(null, CURRENT, null, null);
        Sort sorting = new Sort(new Sort.Order(Sort.Direction.ASC, MAJOR_NUMBER_PROPERTY_NAME), new Sort.Order(Sort.Direction.ASC, MINOR_NUMBER_PROPERTY_NAME),
                                new Sort.Order(Sort.Direction.ASC, REVISION_NUMBER_PROPERTY_NAME));
        pagingAndSortingData = new PageRequest(0, 1, sorting);
    }

}
