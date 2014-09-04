package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkUrlFactory;
import mobi.nowtechnologies.server.dto.streamzine.StreamzineUpdateDto;
import mobi.nowtechnologies.server.persistence.domain.Community;
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
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/20/14
 */
@RunWith(MockitoJUnitRunner.class)
public class StreamzineUpdateAsmTest {
    @Mock
    private DeepLinkUrlFactory deepLinkUrlFactory;
    @Mock
    private DeepLinkInfoService deepLinkInfoService;

    @InjectMocks
    private StreamzineUpdateAsm streamzineUpdateAsm;

    @Test
    public void testConvertOne() throws Exception {
        Community community = mock(Community.class);
        long publishTime = System.currentTimeMillis() + 10000L;
        Update update = new Update(new Date(publishTime), community);
        Block block0 = getBlock(0, ShapeType.WIDE, true, "title", "subTitle");
        Block block2 = getBlock(2, ShapeType.NARROW, true, "title", "");
        Block block1 = getBlock(1, ShapeType.SLIM_BANNER, true, "title", "subTitle");
        Block block3 = getBlock(4, ShapeType.SLIM_BANNER, true, null, null);
        Block block4 = getBlock(3, ShapeType.WIDE, false, "title", "subTitle");
        update.addBlock(block0);
        update.addBlock(block1);
        update.addBlock(block2);
        update.addBlock(block3);
        update.addBlock(block4);

        StreamzineUpdateDto streamzineUpdateDto = streamzineUpdateAsm.convertOne(update, "hl_uk");
        assertThat(streamzineUpdateDto.getUpdated(), is(publishTime));
        assertThat(streamzineUpdateDto.getBlocks(), hasSize(4));
        assertThat(streamzineUpdateDto.getItems(), hasSize(4));
        assertThat(streamzineUpdateDto.getBlocks().get(0).getShapeType(), is(ShapeType.WIDE));
        assertThat(streamzineUpdateDto.getBlocks().get(1).getShapeType(), is(ShapeType.SLIM_BANNER));
        assertThat(streamzineUpdateDto.getBlocks().get(2).getShapeType(), is(ShapeType.NARROW));
        assertThat(streamzineUpdateDto.getBlocks().get(3).getShapeType(), is(ShapeType.SLIM_BANNER));
    }

    @Test
    public void testConvertOneWithTitles() throws Exception {
        Community community = mock(Community.class);
        long publishTime = System.currentTimeMillis() + 10000L;
        Update update = new Update(new Date(publishTime), community);
        update.addBlock(getBlock(0, ShapeType.WIDE, true, "title", "subTitle"));
        update.addBlock(getBlock(1, ShapeType.WIDE, true, null, ""));
        update.addBlock(getBlock(2, ShapeType.NARROW, true, "  ", "subTitle"));// ensure that empty string is correct
        update.addBlock(getBlock(3, ShapeType.NARROW, true, "", null));
        update.addBlock(getBlock(4, ShapeType.SLIM_BANNER, true, "title", "subTitle"));
        update.addBlock(getBlock(5, ShapeType.SLIM_BANNER, true, null, ""));

        StreamzineUpdateDto streamzineUpdateDto = streamzineUpdateAsm.convertOne(update, "hl_uk");
        assertThat(streamzineUpdateDto.getItems(), hasSize(6));
        // WIDE
        assertThat(streamzineUpdateDto.getBlocks().get(0).getShapeType(), is(ShapeType.WIDE));
        assertThat(streamzineUpdateDto.getItems().get(0).getTitle(), is("title"));
        assertThat(streamzineUpdateDto.getItems().get(0).getSubTitle(), is("subTitle"));
        assertThat(streamzineUpdateDto.getBlocks().get(1).getShapeType(), is(ShapeType.WIDE));
        assertThat(streamzineUpdateDto.getItems().get(1).getTitle(), nullValue());
        assertThat(streamzineUpdateDto.getItems().get(1).getSubTitle(), nullValue());
        // NARROW
        assertThat(streamzineUpdateDto.getBlocks().get(2).getShapeType(), is(ShapeType.NARROW));
        assertThat(streamzineUpdateDto.getItems().get(2).getTitle(), is("  "));
        assertThat(streamzineUpdateDto.getItems().get(2).getSubTitle(), nullValue());
        assertThat(streamzineUpdateDto.getBlocks().get(3).getShapeType(), is(ShapeType.NARROW));
        assertThat(streamzineUpdateDto.getItems().get(3).getTitle(), nullValue());
        assertThat(streamzineUpdateDto.getItems().get(3).getSubTitle(), nullValue());
        // SLIM_BANNER
        assertThat(streamzineUpdateDto.getBlocks().get(4).getShapeType(), is(ShapeType.SLIM_BANNER));
        assertThat(streamzineUpdateDto.getItems().get(4).getTitle(), nullValue());
        assertThat(streamzineUpdateDto.getItems().get(4).getSubTitle(), nullValue());
        assertThat(streamzineUpdateDto.getBlocks().get(5).getShapeType(), is(ShapeType.SLIM_BANNER));
        assertThat(streamzineUpdateDto.getItems().get(5).getTitle(), nullValue());
        assertThat(streamzineUpdateDto.getItems().get(5).getSubTitle(), nullValue());
    }

    private Block getBlock(int position, ShapeType shapeType, boolean include, String title, String subTitle) {
        Block block = new Block(position, shapeType, mock(DeeplinkInfo.class));
        block.setTitle(title);
        block.setSubTitle(subTitle);
        if(include){
            block.include();
        }else{
            block.exclude();
        }
        return block;
    }
}
