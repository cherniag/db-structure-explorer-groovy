package mobi.nowtechnologies.server.transport.context.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by zam on 1/9/2015.
 */
public class ReferralsLimitedConditions extends ReferralsConditions {

    private final Date expirationDate;

    public ReferralsLimitedConditions(Date expirationDate) {
        if (expirationDate == null) {
            throw new NullPointerException("expirationDate can't be null");
        }
        this.expirationDate = expirationDate;
    }

    public List<ChartBehaviorDto> unlockChartBehaviors(List<ChartBehaviorDto> content) {
        Map<Date, ChartBehaviorDto> eventsChronology = new TreeMap<Date, ChartBehaviorDto>();

        int i = content.size();
        while (--i >= 0) {
            ChartBehaviorDto event = content.get(i);
            if (event.getValidFrom().before(expirationDate)) {
                if (event.shouldUnlock()) {
                    ChartBehaviorDto unlocked = new ChartBehaviorDto(event.getValidFrom(), event.getChartBehaviorType());
                    unlocked.unlock();
                    eventsChronology.put(event.getValidFrom(), unlocked);
                }
                // move event to expirationDate if either this of first needs to be unlocked
                if (event.shouldUnlock() || content.get(0).shouldUnlock()) {
                    event.setValidFrom(expirationDate);
                }
            }

            if (!eventsChronology.containsKey(event.getValidFrom())) {
                eventsChronology.put(event.getValidFrom(), event);
            }
        }

        content.clear();
        content.addAll(eventsChronology.values());

        return content;
    }
}
