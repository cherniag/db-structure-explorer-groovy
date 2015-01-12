package mobi.nowtechnologies.server.transport.context.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
* Created by zam on 1/9/2015.
*/
public class ReferralsUnlimitedConditions extends ReferralsConditions {
    public List<ChartBehaviorDto> unlockChartBehaviors(List<ChartBehaviorDto> content) {
        List<ChartBehaviorDto> eventsChronology = new ArrayList<ChartBehaviorDto>(content.size());

        boolean wasPrevUnlocked = false;
        for (Iterator<ChartBehaviorDto> i = content.iterator(); i.hasNext() && !wasPrevUnlocked;) {
            ChartBehaviorDto event = i.next();
            if (event.shouldUnlock()) {
                event.unlock();
                // break the look after we got smth to unlock
                wasPrevUnlocked = true;
            }
            eventsChronology.add(event);
        }

        content.clear();
        content.addAll(eventsChronology);

        return content;
    }
}
