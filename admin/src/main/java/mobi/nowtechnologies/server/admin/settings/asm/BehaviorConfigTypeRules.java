package mobi.nowtechnologies.server.admin.settings.asm;

import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfigType;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: Gennadii Cherniaiev Date: 2/18/2015
 */
public enum BehaviorConfigTypeRules {
    FREEMIUM_RULES(BehaviorConfigType.FREEMIUM, Arrays.asList(UserStatusType.values()), Arrays.asList(ChartBehaviorType.values())),
    DEFAULT_RULES(BehaviorConfigType.DEFAULT, Arrays.asList(UserStatusType.FREE_TRIAL), Arrays.asList(ChartBehaviorType.NORMAL, ChartBehaviorType.PREVIEW));

    private BehaviorConfigType configType;
    private List<UserStatusType> userStatusTypes;
    private List<ChartBehaviorType> chartBehaviorTypes;

    BehaviorConfigTypeRules(BehaviorConfigType configType, List<UserStatusType> userStatusTypes, List<ChartBehaviorType> chartBehaviorTypes) {
        this.configType = configType;
        this.userStatusTypes = userStatusTypes;
        this.chartBehaviorTypes = chartBehaviorTypes;
    }

    public static List<UserStatusType> allowedUserStatusTypes(BehaviorConfigType behaviorConfigType) {
        return new ArrayList<>(find(behaviorConfigType).userStatusTypes);
    }


    public static List<ChartBehaviorType> allowedChartBehaviorTypes(BehaviorConfigType behaviorConfigType) {
        return new ArrayList<>(find(behaviorConfigType).chartBehaviorTypes);
    }

    private static BehaviorConfigTypeRules find(BehaviorConfigType behaviorConfigType) {
        for (BehaviorConfigTypeRules rules : values()) {
            if (behaviorConfigType == rules.configType) {
                return rules;
            }
        }
        throw new IllegalArgumentException("Couldn't find rules for config type " + behaviorConfigType);
    }
}
