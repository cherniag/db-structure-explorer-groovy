package mobi.nowtechnologies.server.service.versioncheck;

import com.google.common.collect.Iterables;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheck;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionMessage;
import mobi.nowtechnologies.server.persistence.repository.VersionCheckRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URI;
import java.util.List;

import static mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheck.MAJOR_NUMBER_PROPERTY_NAME;
import static mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheck.MINOR_NUMBER_PROPERTY_NAME;
import static mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheck.REVISION_NUMBER_PROPERTY_NAME;
import static mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheckStatus.CURRENT;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class VersionCheckService {


    @Resource
    private VersionCheckRepository versionCheckRepository;

    private VersionCheckResponse currentVersionResponse;

    private Pageable pagingAndSortingData;


    public VersionCheckResponse check(UserAgentRequest userAgent) {
        List<VersionCheck> possibleVersions =
                versionCheckRepository.findSuitableVersions(userAgent.getCommunity(), userAgent.getPlatform(), userAgent.getApplicationName(),
                        userAgent.getVersion().major(), userAgent.getVersion().minor(), userAgent.getVersion().revision(), pagingAndSortingData);
        VersionCheck needVersion = Iterables.get(possibleVersions, 0, null);
        if (needVersion == null) {
            return currentVersionResponse;
        }
        return convert(needVersion);
    }

    private VersionCheckResponse convert(VersionCheck needVersion) {
        VersionMessage message = needVersion.getMessage();
        URI uri = null;
        if (!isEmpty(message.getUrl())) {
            uri = URI.create(message.getUrl());
        }
        return new VersionCheckResponse(message.getMessageKey(),
                needVersion.getStatus(), uri);
    }

    @PostConstruct
    private void init() {
        currentVersionResponse = new VersionCheckResponse(null, CURRENT, null);
        Sort sorting = new Sort(new Sort.Order(Sort.Direction.ASC, MAJOR_NUMBER_PROPERTY_NAME),
                new Sort.Order(Sort.Direction.ASC, MINOR_NUMBER_PROPERTY_NAME), new Sort.Order(Sort.Direction.ASC, REVISION_NUMBER_PROPERTY_NAME));
        pagingAndSortingData = new PageRequest(0, 1, sorting);
    }



}
