package mobi.nowtechnologies.common.util;

/**
 * Created by Oleg Artomov on 6/26/2014.
 */
public class TrackIdGenerator {

    public static String buildUniqueTrackId(String isrc, Long trackId) {
        return isrc + "_" + trackId;
    }
}
