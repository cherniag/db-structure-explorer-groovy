package mobi.nowtechnologies.common.util;

/**
 * Created by Oleg Artomov on 6/26/2014.
 */
public class TrackIdGenerator {
    public static final String ISRC_TRACK_ID_DELIMITER = "_";

    public static String buildUniqueTrackId(String isrc, Long trackId) {
        return isrc + ISRC_TRACK_ID_DELIMITER + trackId;
    }
}
