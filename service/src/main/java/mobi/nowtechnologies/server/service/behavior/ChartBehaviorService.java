package mobi.nowtechnologies.server.service.behavior;

import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkUrlFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfig;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartUserStatusBehavior;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartUserStatusBehaviors;
import mobi.nowtechnologies.server.persistence.domain.referral.UserReferralsSnapshot;
import mobi.nowtechnologies.server.persistence.repository.behavior.ChartUserStatusBehaviorRepository;

import javax.annotation.Resource;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.tuple.Pair;

public class ChartBehaviorService {

    private final static String UNLOCK_ACTION = "refer_a_friend";
    @Resource
    ChartUserStatusBehaviorRepository chartUserStatusBehaviorRepository;
    @Resource
    DeepLinkUrlFactory deepLinkUrlFactory;
    @Resource
    ChartBehaviorReferralsService chartBehaviorReferralsService;

    public Map<Integer, Collection<ChartBehaviorInfo>> info(BehaviorConfig behaviorConfig, List<Pair<UserStatusType, Date>> userStatusTypeDateMap, UserReferralsSnapshot snapshot, User user,
                                                            Date serverTime) {
        Map<Integer, Map<UserStatusType, ChartUserStatusBehavior>> orderedByChart = chartToUserStatusBehaviorMapping(behaviorConfig, userStatusTypeDateMap);

        Map<Integer, Collection<ChartBehaviorInfo>> info = new HashMap<>();
        for (Map.Entry<Integer, Map<UserStatusType, ChartUserStatusBehavior>> chartToStatusBehaviorMapping : orderedByChart.entrySet()) {
            NavigableSet<ChartBehaviorInfo> infos = createInfos(user, chartToStatusBehaviorMapping, userStatusTypeDateMap);

            chartBehaviorReferralsService.apply(infos, snapshot, serverTime);

            int chartId = chartToStatusBehaviorMapping.getKey();
            info.put(chartId, infos);
        }

        return info;
    }

    private Map<Integer, Map<UserStatusType, ChartUserStatusBehavior>> chartToUserStatusBehaviorMapping(BehaviorConfig behaviorConfig, List<Pair<UserStatusType, Date>> userStatusTypeSinceChronology) {
        List<ChartUserStatusBehavior> chartUserStatusBehaviorList = chartUserStatusBehaviorRepository.findByBehaviorConfig(behaviorConfig, convertToFilter(userStatusTypeSinceChronology));
        return ChartUserStatusBehaviors.from(chartUserStatusBehaviorList).order();
    }

    private Set<UserStatusType> convertToFilter(List<Pair<UserStatusType, Date>> userStatusTypeSinceChronology) {
        Set<UserStatusType> userStatusTypesFilter = new HashSet<>();
        for (Pair<UserStatusType, Date> userStatusTypeDatePair : userStatusTypeSinceChronology) {
            userStatusTypesFilter.add(userStatusTypeDatePair.getKey());
        }
        return userStatusTypesFilter;
    }

    @VisibleForTesting
    NavigableSet<ChartBehaviorInfo> createInfos(User user, Map.Entry<Integer, Map<UserStatusType, ChartUserStatusBehavior>> chartToStatusBehaviorMapping,
                                                List<Pair<UserStatusType, Date>> userStatusTypeSinceChronology) {
        NavigableSet<ChartBehaviorInfo> infos = new TreeSet<>();

        for (Map.Entry<UserStatusType, Date> chronology : userStatusTypeSinceChronology) {
            UserStatusType userStatusType = chronology.getKey();
            ChartUserStatusBehavior value = chartToStatusBehaviorMapping.getValue().get(userStatusType);

            String calcedLockedAction = calcLockedAction(chartToStatusBehaviorMapping.getKey(), user, value);

            ChartBehaviorInfo i = new ChartBehaviorInfo();
            i.userStatusType = userStatusType;
            i.chartBehaviorType = value.getChartBehavior().getType();
            i.lockedAction = calcedLockedAction;
            i.canBeUnlocked = calcIsLocked(value.getAction());
            i.validFrom = chronology.getValue();
            infos.add(i);
        }

        return infos;
    }

    private String calcLockedAction(int chartId, User user, ChartUserStatusBehavior behavior) {
        if (behavior.getAction() == null) {
            return null;
        } else {
            return deepLinkUrlFactory.createForChart(user.getCommunity(), chartId, behavior.getAction());
        }
    }

    private boolean calcIsLocked(String lockedAction) {
        return UNLOCK_ACTION.equals(lockedAction);
    }

}
