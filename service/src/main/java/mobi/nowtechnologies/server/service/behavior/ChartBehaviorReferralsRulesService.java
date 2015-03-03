package mobi.nowtechnologies.server.service.behavior;

import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;

class ChartBehaviorReferralsRulesService {

    ChartBehaviorType newType(ChartBehaviorInfo info) {
        if (info.userStatusType == UserStatusType.FREE_TRIAL) {
            return ChartBehaviorType.NORMAL;
        }

        if (info.userStatusType == UserStatusType.LIMITED) {
            return ChartBehaviorType.SHUFFLED;
        }

        return info.chartBehaviorType;
    }
}
