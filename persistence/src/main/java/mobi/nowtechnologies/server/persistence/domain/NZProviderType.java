package mobi.nowtechnologies.server.persistence.domain;

public enum NZProviderType {
    VODAFONE, NON_VODAFONE;

    public static NZProviderType of(String name) {
        return (VODAFONE_ID.equals(name)) ? VODAFONE : NON_VODAFONE;
    }

    private static final String VODAFONE_ID = "Vodafone";
}
