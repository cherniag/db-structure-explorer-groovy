package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.shared.enums.DurationUnit;

public enum PeriodMessageKeyBuilder {
    Day(DurationUnit.DAYS) {
        @Override
        public String getMessageKey(Period period) {
            return period.isOne() ?
                   "per.day" :
                   "for.n.days";
        }
    },
    Week(DurationUnit.WEEKS) {
        @Override
        public String getMessageKey(Period period) {
            return period.isOne() ?
                   "per.week" :
                   "for.n.weeks";
        }
    },
    Month(DurationUnit.MONTHS) {
        @Override
        public String getMessageKey(Period period) {
            return period.isOne() ?
                   "per.month" :
                   "for.n.months";
        }
    };

    private DurationUnit unit;

    PeriodMessageKeyBuilder(DurationUnit unit) {
        this.unit = unit;
    }

    public static PeriodMessageKeyBuilder of(DurationUnit unit) {
        for (PeriodMessageKeyBuilder builder : values()) {
            if (builder.unit == unit) {
                return builder;
            }
        }

        throw new IllegalArgumentException("Unknown " + unit);
    }

    public abstract String getMessageKey(Period period);
}
