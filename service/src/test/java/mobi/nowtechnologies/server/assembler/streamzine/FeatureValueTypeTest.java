package mobi.nowtechnologies.server.assembler.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.DeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.InformationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class FeatureValueTypeTest {

    private DeeplinkInfo deeplinkInfo;
    private FeatureValueType expected;

    public FeatureValueTypeTest(DeeplinkInfo deeplinkInfo, FeatureValueType expected) {
        this.deeplinkInfo = deeplinkInfo;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {getInformationDeeplinkInfo(LinkLocationType.EXTERNAL_AD, null), FeatureValueType.WEB},
                {getInformationDeeplinkInfo(LinkLocationType.INTERNAL_AD, null), FeatureValueType.PAGE},
                {getInformationDeeplinkInfo(LinkLocationType.INTERNAL_AD, "news"), FeatureValueType.CONTENT},
                {mock(DeeplinkInfo.class), FeatureValueType.CONTENT}
        });
    }

    @Test
    public void testFeatureValueTypeCreation() {
        assertEquals(expected, FeatureValueType.of(deeplinkInfo));
    }

    private static InformationDeeplinkInfo getInformationDeeplinkInfo(LinkLocationType linkLocationType, String url) {
        InformationDeeplinkInfo mock = mock(InformationDeeplinkInfo.class);
        when(mock.getLinkType()).thenReturn(linkLocationType);
        when(mock.getUrl()).thenReturn(url);
        return mock;
    }
}