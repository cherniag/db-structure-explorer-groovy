package mobi.nowtechnologies.common.util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by Oleg Artomov on 6/26/2014.
 */
public class TrackIdGenerator {

    public static final String ISRC_TRACK_ID_DELIMITER = "_";

    public static String buildUniqueTrackId(String isrc, Long trackId) {
        return isrc + ISRC_TRACK_ID_DELIMITER + trackId;
    }

    public static Pair<String, Long> parseUniqueTrackId(String value) {
        String[] split = value.split(ISRC_TRACK_ID_DELIMITER);
        return new ImmutablePair<String, Long>(split[0], Long.parseLong(split[1]));
    }
}
