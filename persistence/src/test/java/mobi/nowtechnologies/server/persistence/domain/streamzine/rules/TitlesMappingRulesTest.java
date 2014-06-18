package mobi.nowtechnologies.server.persistence.domain.streamzine.rules;

import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TitlesMappingRulesTest {

    @Test
    public void testWide() throws Exception {
        ShapeType shapeType = ShapeType.WIDE;
        assertThat(TitlesMappingRules.hasTitle(shapeType), is(true));
        assertThat(TitlesMappingRules.hasSubTitle(shapeType), is(true));
    }

    @Test
    public void testNarrow() throws Exception {
        ShapeType shapeType = ShapeType.NARROW;
        assertThat(TitlesMappingRules.hasTitle(shapeType), is(true));
        assertThat(TitlesMappingRules.hasSubTitle(shapeType), is(false));
    }

    @Test
    public void testSlimBanner() throws Exception {
        ShapeType shapeType = ShapeType.SLIM_BANNER;
        assertThat(TitlesMappingRules.hasTitle(shapeType), is(false));
        assertThat(TitlesMappingRules.hasSubTitle(shapeType), is(false));
    }
}