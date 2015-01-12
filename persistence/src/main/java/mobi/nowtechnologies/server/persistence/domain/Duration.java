package mobi.nowtechnologies.server.persistence.domain;

import com.google.common.base.Preconditions;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

/**
 * Created by zam on 12/9/2014.
 */
// TODO: consider with Period
@Embeddable
public class Duration implements Serializable {
    private int amount;

    @Enumerated(EnumType.STRING)
    private DurationUnit unit;

    protected Duration() {
    }

    public static Duration noPeriod() {
        Duration d = new Duration();
        d.amount = 0;
        d.unit = null;
        return d;
    }

    public static Duration forPeriod(int amount, DurationUnit unit) {
        Preconditions.checkState(amount > 0);

        Duration d = new Duration();
        d.amount = amount;
        d.unit = Preconditions.checkNotNull(unit);
        return d;
    }

    public boolean containsPeriod() {
        return amount > 0 && unit != null;
    }

    public int getAmount() {
        return amount;
    }

    public DurationUnit getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Duration)) return false;

        Duration duration = (Duration) o;

        // both are not a period
        if(!duration.containsPeriod() && !containsPeriod()) {
            return true;
        }

        if (amount != duration.amount) return false;
        if (unit != duration.unit) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = amount;
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Duration{" +
                "amount=" + amount +
                ", unit=" + unit +
                '}';
    }
}
