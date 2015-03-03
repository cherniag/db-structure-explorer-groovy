package mobi.nowtechnologies.server.trackrepo.utils;

/**
 * Created by Oleg Artomov on 9/24/2014.
 */
public enum BitRate {
    BITRATE48("48"), BITRATE96("96");

    private final String value;

    BitRate(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
