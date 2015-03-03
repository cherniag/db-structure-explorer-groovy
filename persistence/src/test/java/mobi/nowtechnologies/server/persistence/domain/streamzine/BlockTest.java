package mobi.nowtechnologies.server.persistence.domain.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NotificationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BlockTest {

    @Test
    public void testInclude() throws Exception {
        Block block = createBlock(0);
        block.include();

        assertTrue(block.isIncluded());
    }

    @Test
    public void testExclude() throws Exception {
        Block block = createBlock(0);
        block.exclude();

        assertFalse(block.isIncluded());
    }

    private Block createBlock(int position) {
        return new Block(position, ShapeType.SLIM_BANNER, mock(NotificationDeeplinkInfo.class));
    }
}
