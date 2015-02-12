package mobi.nowtechnologies.server.shared.enums;

// @author Titov Mykhaylo (titov) on 17.10.2014.
public enum DurationUnit {
    SECONDS(1), MINUTES(2), HOURS(3), DAYS(4), WEEKS(5), MONTHS(6), YEARS(7);

    private int periodWeight;

    DurationUnit(int periodWeight) {
        this.periodWeight = periodWeight;
    }

    public int getPeriodWeight() {
        return periodWeight;
    }
}
