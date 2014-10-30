package mobi.nowtechnologies.server.shared.enums;

// @author Titov Mykhaylo (titov) on 17.10.2014.
public enum DurationUnit {
    DAYS, WEEKS, MONTHS;

    public int compareWith(DurationUnit durationUnit){
        switch (this){
            case DAYS:
                return durationUnit.equals(DAYS) ? 0 : -1;
            case WEEKS:
                if (durationUnit.equals(DAYS)) return 1;
                if (durationUnit.equals(MONTHS)) return -1;
                return 0;
            case MONTHS:
                return durationUnit.equals(MONTHS) ? 0 : 1;
            default:
                throw new IllegalArgumentException("Unknown duration unit " + durationUnit);
        }
    }
}
