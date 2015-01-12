package mobi.nowtechnologies.server.persistence.domain.behavior;

import mobi.nowtechnologies.server.persistence.domain.UserStatusType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartUserStatusBehaviors {
    private List<ChartUserStatusBehavior> chartUserStatusBehaviors = new ArrayList<ChartUserStatusBehavior>();

    private ChartUserStatusBehaviors() {
    }

    public static ChartUserStatusBehaviors from(List<ChartUserStatusBehavior> many) {
        ChartUserStatusBehaviors chartUserStatusBehaviors = new ChartUserStatusBehaviors();
        chartUserStatusBehaviors.chartUserStatusBehaviors.addAll(many);
        return chartUserStatusBehaviors;
    }


    public Map<Integer, Map<UserStatusType, ChartUserStatusBehavior>> order() {
        Map<Integer, Map<UserStatusType, ChartUserStatusBehavior>> ordered = new HashMap<Integer, Map<UserStatusType, ChartUserStatusBehavior>>();

        for (ChartUserStatusBehavior chartUserStatusBehavior : chartUserStatusBehaviors) {
            final int chartId = chartUserStatusBehavior.getChartId();

            if(!ordered.containsKey(chartId)) {
                ordered.put(chartId, createEmptyForTypes());
            }

            ordered.get(chartId).put(chartUserStatusBehavior.getUserStatusType(), chartUserStatusBehavior);
        }

        return ordered;
    }

    private HashMap<UserStatusType, ChartUserStatusBehavior> createEmptyForTypes() {
        HashMap<UserStatusType, ChartUserStatusBehavior> map = new HashMap<UserStatusType, ChartUserStatusBehavior>();
        for (UserStatusType userStatusType : UserStatusType.values()) {
            map.put(userStatusType, null);
        }
        return map;
    }
}
