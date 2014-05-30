package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkUrlFactory;
import mobi.nowtechnologies.server.dto.streamzine.StreamzineUpdateDto;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.DeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/20/14
 */
@RunWith(MockitoJUnitRunner.class)
public class StreamzineUpdateAsmTest {
    @Mock
    private DeepLinkUrlFactory deepLinkUrlFactory;
    @InjectMocks
    private StreamzineUpdateAsm streamzineUpdateAsm;

    @Test
    public void testConvertOne() throws Exception {
        long publishTime = System.currentTimeMillis() + 10000L;
        Update update = new Update(new Date(publishTime));
        Block block0 = getBlock(0, ShapeType.WIDE, true);
        Block block2 = getBlock(2, ShapeType.NARROW, true);
        Block block1 = getBlock(1, ShapeType.SLIM_BANNER, true);
        Block block3 = getBlock(4, ShapeType.BUTTON, true);
        Block block4 = getBlock(3, ShapeType.WIDE, false);
        update.addBlock(block0);
        update.addBlock(block1);
        update.addBlock(block2);
        update.addBlock(block3);
        update.addBlock(block4);

        StreamzineUpdateDto streamzineUpdateDto = streamzineUpdateAsm.convertOne(update);
        assertThat(streamzineUpdateDto.getUpdated(), is(publishTime));
        assertThat(streamzineUpdateDto.getBlocks(), hasSize(4));
        assertThat(streamzineUpdateDto.getItems(), hasSize(4));
        assertThat(streamzineUpdateDto.getBlocks().get(0).getShapeType(), is(ShapeType.WIDE));
        assertThat(streamzineUpdateDto.getBlocks().get(1).getShapeType(), is(ShapeType.SLIM_BANNER));
        assertThat(streamzineUpdateDto.getBlocks().get(2).getShapeType(), is(ShapeType.NARROW));
        assertThat(streamzineUpdateDto.getBlocks().get(3).getShapeType(), is(ShapeType.BUTTON));
    }

    private Block getBlock(int position, ShapeType shapeType, boolean include) {
        Block block = new Block(position, shapeType, mock(DeeplinkInfo.class));
        if(include){
            block.include();
        }else{
            block.exclude();
        }
        return block;
    }
}
