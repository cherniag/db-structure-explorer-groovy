package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Collections.unmodifiableMap;
import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;
import static mobi.nowtechnologies.server.shared.Utils.millisToIntSeconds;
import static mobi.nowtechnologies.server.shared.Utils.secondsToMillis;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.DAYS;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.MONTHS;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;
import static org.joda.time.DateTimeFieldType.dayOfMonth;
import static org.joda.time.DateTimeZone.UTC;
import static org.joda.time.Period.days;
import static org.joda.time.Period.months;

/**
 * @autor: Titov Mykhaylo (titov)
 * 24.12.13 15:43
 */
@Embeddable
public class Period{

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_unit", nullable = false)
    private DurationUnit durationUnit;

    @Column(name = "duration", nullable = false)
    private long duration;

    public long getDuration() {
        return duration;
    }

    private int getDurationAsInt() {
        return (int)duration;
    }

    public DurationUnit getDurationUnit() {
        return durationUnit;
    }

    public Period withDuration(long duration){
        this.duration = duration;
        return this;
    }

    public Period withDurationUnit(DurationUnit durationUnit){
        this.durationUnit = durationUnit;
        return this;
    }

    private static interface PeriodConverter{
        public int toNextSubPaymentSeconds(Period period, int subscriptionStartTimeSeconds);

        public String toMessageCode(Period period);
    }

    private static final PeriodConverter DAYS_PERIOD_CONVERTER = new PeriodConverter(){
        @Override
        public int toNextSubPaymentSeconds(Period period, int subscriptionStartTimeSeconds) {
            return subscriptionStartTimeSeconds + (int) TimeUnit.DAYS.toSeconds(period.getDuration());
        }

        @Override
        public String toMessageCode(Period period) {
            if(period.getDuration() == 1) {
                return "per.day";
            }
            return "for.n.days";
        }
    };

    private static final PeriodConverter WEEKS_PERIOD_CONVERTER = new PeriodConverter(){
        @Override
        public int toNextSubPaymentSeconds(Period period, int subscriptionStartTimeSeconds) {
            return subscriptionStartTimeSeconds + 7*(int) TimeUnit.DAYS.toSeconds(period.duration);
        }

        @Override
        public String toMessageCode(Period period) {
            if(period.getDuration() == 1) {
                return "per.week";
            }
            return "for.n.weeks";
        }
    };

    private static final PeriodConverter MONTHS_PERIOD_CONVERTER = new PeriodConverter(){
        @Override
        public int toNextSubPaymentSeconds(Period period, int subscriptionStartTimeSeconds) {
            return getNextSubPaymentForMonthlyPeriod(period, subscriptionStartTimeSeconds);
        }

        @Override
        public String toMessageCode(Period period) {
            if(period.getDuration() == 1) {
                return "per.month";
            }
            return "for.n.months";
        }
    };

    private static final Map<DurationUnit, PeriodConverter> DURATION_UNIT_PERIOD_CONVERTER_MAP;

    static{
        Map<DurationUnit, PeriodConverter> map = new EnumMap<DurationUnit, PeriodConverter>(DurationUnit.class);

        map.put(DAYS, DAYS_PERIOD_CONVERTER);
        map.put(WEEKS, WEEKS_PERIOD_CONVERTER);
        map.put(MONTHS, MONTHS_PERIOD_CONVERTER);

        DURATION_UNIT_PERIOD_CONVERTER_MAP = unmodifiableMap(map);
    }

    public int toNextSubPaymentSeconds(int oldNextSubPaymentSeconds){
        int subscriptionStartTimeSeconds = max(getEpochSeconds(), oldNextSubPaymentSeconds);
        return DURATION_UNIT_PERIOD_CONVERTER_MAP.get(durationUnit).toNextSubPaymentSeconds(this, subscriptionStartTimeSeconds);
    }

    public String toMessageCode(){
        return DURATION_UNIT_PERIOD_CONVERTER_MAP.get(durationUnit).toMessageCode(this);
    }

    private static int getNextSubPaymentForMonthlyPeriod(Period period, int subscriptionStartTimeSeconds){
        DateTime dateTime = new DateTime(secondsToMillis(subscriptionStartTimeSeconds), UTC);
        int dayOfMonthBefore = dateTime.get(dayOfMonth());
        dateTime = dateTime.plus(months(period.getDurationAsInt()));
        int dayOfMonthAfter = dateTime.get(dayOfMonth());
        if (dayOfMonthBefore != dayOfMonthAfter) {
            dateTime = dateTime.plus(days(1));
        }
        return millisToIntSeconds(dateTime.getMillis());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("duration", duration)
                .append("durationUnit", durationUnit)
                .toString();
    }
}
