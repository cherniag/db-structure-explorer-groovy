package mobi.nowtechnologies.server.service.behavior;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NavigableSet;

class ChartBehaviorChronology {
    private NavigableSet<ChartBehaviorInfo> infos;

    ChartBehaviorChronology(NavigableSet<ChartBehaviorInfo> infos) {
        this.infos = infos;
    }

    void consume(Date referralMatchedDate, Date referralExpiresDate) {
        Assert.notNull(referralMatchedDate);
        Assert.notNull(referralExpiresDate);

        ChartBehaviorInfo newOne = new ChartBehaviorInfo();
        newOne.validFrom = referralExpiresDate;

        infos.add(newOne);

        newOne.borrow(infos.lower(newOne));
    }

    List<Pair<Period, ChartBehaviorInfo>> toPeriods() {
        List<Pair<Period, ChartBehaviorInfo>> periodToInfos = new ArrayList<>(infos.size());

        for (ChartBehaviorInfo info : infos) {
            Period period = extractPeriod(info, infos);

            Pair<Period, ChartBehaviorInfo> periodToInfo = new ImmutablePair<>(period, info);

            periodToInfos.add(periodToInfo);
        }

        return periodToInfos;
    }

    private static Period extractPeriod(ChartBehaviorInfo current, NavigableSet<ChartBehaviorInfo> infos) {
        final boolean isNotLast = infos.higher(current) != null;

        Period p = new Period();
        p.start = current.getValidFrom();
        p.end = isNotLast ? infos.higher(current).validFrom : null;
        return p;
    }

    static class Period {
        private Date start;
        private Date end;

        public Date getStart() {
            return start;
        }
        public Date getEnd() {
            return end;
        }

        @Override
        public String toString() {
            return "Period{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }
}
