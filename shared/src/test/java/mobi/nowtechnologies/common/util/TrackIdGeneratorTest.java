package mobi.nowtechnologies.common.util;

import org.apache.commons.lang3.tuple.Pair;

import org.junit.*;
import static org.junit.Assert.*;


public class TrackIdGeneratorTest {

    @Test
    public void testBuildUniqueTrackId() throws Exception {
        assertEquals("USUM71401800_730664", TrackIdGenerator.buildUniqueTrackId("USUM71401800", 730664L));
    }

    @Test
    public void testParseUniqueTrackId() throws Exception {
        String mediaTrackId = "USUM71401800_730664";
        Pair<String, Long> stringLongPair = TrackIdGenerator.parseUniqueTrackId(mediaTrackId);
        assertEquals(730664L, stringLongPair.getRight().longValue());
        assertEquals("USUM71401800", stringLongPair.getLeft());
    }
}