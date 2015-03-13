package mobi.nowtechnologies.server.service.behavior;

import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfig;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfigType;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;

class ChartBehaviorReferralsRulesService {

    ChartBehaviorType newType(BehaviorConfig behaviorConfig, ChartBehaviorInfo info) {
        if (info.userStatusType == UserStatusType.FREE_TRIAL) {
            return ChartBehaviorType.NORMAL;
        }

        if (behaviorConfig.getType().equals(BehaviorConfigType.FREEMIUM) && info.userStatusType == UserStatusType.LIMITED) {
            return ChartBehaviorType.SHUFFLED;
        }

        return info.chartBehaviorType;
    }
}
