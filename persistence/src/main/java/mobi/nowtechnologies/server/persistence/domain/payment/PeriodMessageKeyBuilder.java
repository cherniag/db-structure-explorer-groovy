package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.shared.enums.DurationUnit;

public enum PeriodMessageKeyBuilder {
    Day(DurationUnit.DAYS) {
        @Override
        String getMessageKey(Period period) {
            return period.isOne() ? "per.day" : "for.n.days";
        }
    },
    Week(DurationUnit.WEEKS) {
        @Override
        String getMessageKey(Period period) {
            return period.isOne() ? "per.week" : "for.n.weeks";
        }
    },
    Month(DurationUnit.MONTHS) {
        @Override
        String getMessageKey(Period period) {
            return period.isOne() ? "per.month" : "for.n.months";
        }
    };

    private DurationUnit unit;

    PeriodMessageKeyBuilder(DurationUnit unit) {
        this.unit = unit;
    }

    abstract String getMessageKey(Period period);

    public static PeriodMessageKeyBuilder of(DurationUnit unit) {
        for (PeriodMessageKeyBuilder builder : values()) {
            if(builder.unit == unit) {
                return builder;
            }
        }

        throw new IllegalArgumentException("Unknown " + unit);
    }
}
