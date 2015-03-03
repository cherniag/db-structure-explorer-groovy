package mobi.nowtechnologies.server.trackrepo.enums;

import mobi.nowtechnologies.server.trackrepo.Resolution;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
public enum VideoResolution implements Resolution {

    RATE_ORIGINAL("", "");

    private String suffix;
    private String value;

    private VideoResolution(String suffix, String value) {
        this.suffix = suffix;
        this.value = value;
    }

    public String getSuffix() {
        return suffix;
    }

    @Override
    public String getValue() {
        return value;
    }
}