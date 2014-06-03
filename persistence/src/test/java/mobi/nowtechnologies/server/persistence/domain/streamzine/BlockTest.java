package mobi.nowtechnologies.server.persistence.domain.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NotificationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

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
        return new Block(position, ShapeType.BUTTON, mock(NotificationDeeplinkInfo.class));
    }
}
