package mobi.nowtechnologies.server.service.behavior;

import mobi.nowtechnologies.server.persistence.domain.referral.UserReferralsSnapshot;
import mobi.nowtechnologies.server.service.behavior.ChartBehaviorChronology.Period;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.NavigableSet;

class ChartBehaviorReferralsService {
    @Resource
    ChartBehaviorReferralsRulesService chartBehaviorReferralsRulesService;

    // API
    void apply(NavigableSet<ChartBehaviorInfo> infos, UserReferralsSnapshot snapshot, Date serverTime) {
        if (snapshot.isActual(serverTime)) {
            ChartBehaviorChronology chronology = new ChartBehaviorChronology(infos);
            if (!snapshot.hasNoDuration()) {
                chronology.consume(snapshot.getMatchedDate(), snapshot.getReferralsExpiresDate());
            }

            changeChartBehavior(chronology, snapshot);
        }
    }

    private void changeChartBehavior(ChartBehaviorChronology chronology, UserReferralsSnapshot snapshot) {
        final List<Pair<Period, ChartBehaviorInfo>> periods = chronology.toPeriods();

        for (int index = 0; index < periods.size(); index++) {
            Pair<Period, ChartBehaviorInfo> pair = periods.get(index);

            Period datePeriod = pair.getKey();
            ChartBehaviorInfo info = pair.getValue();

            if(snapshot.includes(datePeriod.getStart(), datePeriod.getEnd())) {
                boolean canBeUnlocked = info.canBeUnlocked();
                if(canBeUnlocked || (index>0 && periods.get(index-1).getValue().wasUnlocked())) {
                    info.chartBehaviorType = chartBehaviorReferralsRulesService.newType(info);
                    info.unlock();
                }
            }
        }
    }
}
