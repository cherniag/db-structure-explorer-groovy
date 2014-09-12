package mobi.nowtechnologies.server.service.versioncheck;

import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheck;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionMessage;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.VersionCheckRepository;
import org.apache.commons.lang3.Range;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URI;
import java.util.*;

import static mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheckStatus.CURRENT;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.util.Assert.isTrue;

public class VersionCheckService {

    private static final Integer MAJOR_MULTIPLIER = 10000;

    private static final Integer MINOR_MULTIPLIER = 1000;

    private static final Integer REVISION_MULTIPLIER = 10;

    @Resource
    private VersionCheckRepository versionCheckRepository;

    @Resource
    private CommunityRepository communityRepository;

    private Comparator<VersionCheck> versionsByStatusComparator;

    private VersionCheckResponse currentVersionResponse;


    public VersionCheckResponse check(UserAgentRequest userAgent) {
        List<VersionCheck> possibleVersions = versionCheckRepository.findByCommunityAndDeviceType(userAgent.getCommunity(), userAgent.getPlatform());
        VersionCheck needVersion = calculateVersion(possibleVersions, userAgent);
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

    private VersionCheck calculateVersion(List<VersionCheck> possibleVersions, UserAgentRequest userAgent) {
        if (!possibleVersions.isEmpty()) {
            List<Range<Integer>> ranges = buildRanges(possibleVersions);
            for (int i = 0, n = ranges.size(); i < n; i++) {
                if (ranges.get(i).contains(buildValue(userAgent))) {
                    return possibleVersions.get(i);
                }
            }
        }
        return null;
    }

    private List<Range<Integer>> buildRanges(List<VersionCheck> possibleVersions) {
        Collections.sort(possibleVersions, versionsByStatusComparator);
        List<Range<Integer>> result = new ArrayList<Range<Integer>>(possibleVersions.size());
        for (int i = 0, n = possibleVersions.size(); i < n; i++) {
            Integer lowestValue = 0;
            if (i > 0) {
                lowestValue = buildValue(possibleVersions.get(i - 1));
            }
            Integer highestValue = buildValue(possibleVersions.get(i));
            result.add(Range.between(lowestValue, highestValue));
        }
        return result;
    }

    private Integer buildFromPartOfVersion(Integer value, Integer multiplier) {
        return value * multiplier;
    }

    private Integer buildVersionNumber(int major, int minor, int revision) {
        return buildFromPartOfVersion(major, MAJOR_MULTIPLIER)
                + buildFromPartOfVersion(minor, MINOR_MULTIPLIER)
                + buildFromPartOfVersion(revision, REVISION_MULTIPLIER);
    }

    private Integer buildValue(VersionCheck versionCheck) {
        return buildVersionNumber(versionCheck.getMajorNumber(), versionCheck.getMinorNumber(), versionCheck.getRevisionNumber());
    }

    private Integer buildValue(UserAgentRequest userAgent) {
        return buildVersionNumber(userAgent.getVersion().major(), userAgent.getVersion().minor(), userAgent.getVersion().revision());

    }

    @PostConstruct
    private void init() {
        currentVersionResponse = new VersionCheckResponse(null, CURRENT, null);
        versionsByStatusComparator = new Comparator<VersionCheck>() {
            @Override
            public int compare(VersionCheck o1, VersionCheck o2) {
                return o1.getStatus().getOrderPosition() - o2.getStatus().getOrderPosition();
            }
        };

        validateDataBeforeStartup();
    }

    private void validateDataBeforeStartup() {
        Collection<Community> communities = communityRepository.findAll();
        Collection<DeviceType> deviceTypes = DeviceTypeDao.getDeviceTypeMapIdAsKeyAndDeviceTypeValue().values();
        for (Community currentCommunity : communities) {
            for (DeviceType deviceType : deviceTypes) {
                List<VersionCheck> possibleVersions = versionCheckRepository.findByCommunityAndDeviceType(currentCommunity, deviceType);
                if (!possibleVersions.isEmpty()) {
                    Range<Integer> previousRange = null;
                    List<Range<Integer>> ranges = buildRanges(possibleVersions);
                    for (Range<Integer> currentRange : ranges) {
                        isTrue(currentRange.getMaximum() > currentRange.getMinimum(), "Invalid range");
                        if (previousRange != null) {
                            isTrue(currentRange.getMinimum() >= previousRange.getMaximum(), "Ranges should not intersect");
                        }
                        previousRange = currentRange;
                    }
                }
            }
        }
    }


}
