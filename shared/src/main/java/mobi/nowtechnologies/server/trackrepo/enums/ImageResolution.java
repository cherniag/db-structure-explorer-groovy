package mobi.nowtechnologies.server.trackrepo.enums;

import mobi.nowtechnologies.server.trackrepo.Resolution;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
public enum ImageResolution implements Resolution {

    SIZE_ORIGINAL("", "G"),
    SIZE_SMALL("S", "S"),
    SIZE_LARGE("L", "L"),
    SIZE_22("_22", "22"),
    SIZE_21("_21", "21"),
    SIZE_11("_11", "11"),
    SIZE_6("_6", "6"),
    SIZE_3("_3", "3");

    private String suffix;
    private String value;

    private ImageResolution(String suffix, String value) {
        this.suffix = suffix;
        this.value = value;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public String getValue() {
        return value;
    }
}