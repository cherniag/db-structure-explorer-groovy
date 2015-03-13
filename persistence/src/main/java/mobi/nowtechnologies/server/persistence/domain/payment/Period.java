package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import static mobi.nowtechnologies.server.shared.Utils.millisToIntSeconds;
import static mobi.nowtechnologies.server.shared.Utils.secondsToMillis;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;
import static org.joda.time.DateTimeFieldType.dayOfMonth;
import static org.joda.time.DateTimeZone.UTC;
import static org.joda.time.Period.days;
import static org.joda.time.Period.months;

import org.springframework.util.Assert;

/**
 * @autor: Titov Mykhaylo (titov)
 * 24.12.13 15:43
 */
@Embeddable
public class Period {

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_unit", nullable = false)
    private DurationUnit durationUnit = WEEKS;

    @Column(name = "duration", nullable = false)
    private int duration;

    public Period() {
    }

    public Period(DurationUnit durationUnit, int duration) {
        withDurationUnit(durationUnit);
        withDuration(duration);
    }

    public int getDuration() {
        return duration;
    }

    public DurationUnit getDurationUnit() {
        return durationUnit;
    }

    public Period withDuration(int duration) {
        Assert.isTrue(duration >= 0, "The duration [" + duration + "] must be more than 0");
        this.duration = duration;
        return this;
    }

    public Period withDurationUnit(DurationUnit durationUnit) {
        Assert.notNull(durationUnit);
        this.durationUnit = durationUnit;
        return this;
    }

    public boolean isOne() {
        return 1 == duration;
    }

    public int toNextSubPaymentSeconds(int subscriptionStartTimeSeconds) {
        switch (durationUnit) {
            case DAYS:
                return subscriptionStartTimeSeconds + (int) TimeUnit.DAYS.toSeconds(duration);
            case WEEKS:
                return subscriptionStartTimeSeconds + 7 * (int) TimeUnit.DAYS.toSeconds(duration);
            case MONTHS:
                return getNextSubPaymentForMonthlyPeriod(subscriptionStartTimeSeconds);
            default:
                throw new IllegalArgumentException("Unsupported duration unit " + durationUnit);
        }
    }

    private int getNextSubPaymentForMonthlyPeriod(int subscriptionStartTimeSeconds) {
        DateTime dateTime = new DateTime(secondsToMillis(subscriptionStartTimeSeconds), UTC);
        int dayOfMonthBefore = dateTime.get(dayOfMonth());
        dateTime = dateTime.plus(months(duration));
        int dayOfMonthAfter = dateTime.get(dayOfMonth());
        if (dayOfMonthBefore != dayOfMonthAfter) {
            dateTime = dateTime.plus(days(1));
        }
        return millisToIntSeconds(dateTime.getMillis());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("duration", duration).append("durationUnit", durationUnit).toString();
    }

}
