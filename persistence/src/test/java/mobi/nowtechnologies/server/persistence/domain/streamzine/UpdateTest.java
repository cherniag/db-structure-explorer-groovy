package mobi.nowtechnologies.server.persistence.domain.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.MusicPlayListDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NotificationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType.BUTTON;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UpdateTest {
    @Test(expected = IllegalArgumentException.class)
    public void testCanCreate() throws Exception {
        Date yesterday = DateUtils.addDays(new Date(), -1);
        new Update(yesterday);
    }

    @Test
    public void testCanEditInFuture() throws Exception {
        Date dateAfterToday = DateUtils.addDays(new Date(), 1);
        Update update = new Update(dateAfterToday);
        Assert.assertTrue(update.canEdit());
    }

    @Test
    public void testFill() throws Exception {
        final int position = 0;

        Update from = createUpdateInFuture();
        from.addBlock(createBlock(position));

        Update update = createUpdateInFuture();
        update.updateFrom(from);

        Assert.assertEquals(position, update.getIncludedBlocks().get(0).getPosition());
    }

    @Test
    public void testCopyFrom() throws Exception {
        final int position = 0;

        Update from = createUpdateInFuture();
        from.addBlock(createBlock(position));

        Update update = createUpdateInFuture();
        update.cloneBlocks(from);

        Assert.assertEquals(position, update.getIncludedBlocks().get(0).getPosition());
    }

    @Test
    public void shouldCloneBlocksAsIncluded(){
        //given
        Update updateWithBlocks = new Update(addDays(new Date(), 1));
        Block block = createBlock(5);
        block.exclude();
        updateWithBlocks.addBlock(block);

        Update updateBlocksAcceptor = new Update(addDays(new Date(), 1));

        //when
        updateBlocksAcceptor.cloneBlocks(updateWithBlocks);

        //then
        assertThat(updateBlocksAcceptor.getBlocks().size(), is(1));
        assertThat(updateBlocksAcceptor.getBlocks().get(0).isIncluded(), is(true));
    }

    private Block createBlock(int position) {
        NotificationDeeplinkInfo deeplinkInfo = mock(NotificationDeeplinkInfo.class);
        when(deeplinkInfo.copy(any(Block.class))).thenReturn(deeplinkInfo);

        Block block = new Block(position, BUTTON, deeplinkInfo);
        block.include();
        return block;
    }

    private Update createUpdateInFuture() {
        Date dateAfterToday = DateUtils.addDays(new Date(), 1);
        return new Update(dateAfterToday);
    }


}
