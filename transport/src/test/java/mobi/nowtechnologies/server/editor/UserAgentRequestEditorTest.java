package mobi.nowtechnologies.server.editor;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.service.versioncheck.UserAgentRequest;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Ignore
public class UserAgentRequestEditorTest {
    UserAgentRequestEditor editor = new UserAgentRequestEditor();

    @Test
    public void testSetAsTextAllDigitsInVersion() throws Exception {
        // given
        String userAgentHeaderValue = "musicqubed/3.1.1 (Android; MTV)";

        // when
        editor.setAsText(userAgentHeaderValue);

        // then
        UserAgentRequest value = (UserAgentRequest) editor.getValue();

        assertEquals("musicqubed", value.getApplicationName());

        assertEquals(3, value.getVersion().major());
        assertEquals(1, value.getVersion().minor());
        assertEquals(1, value.getVersion().revision());

        assertEquals(UserRegInfo.DeviceType.ANDROID, value.getApplicationName());

        assertEquals("MTV", value.getCommunity());
    }

    @Test
    public void testSetAsTextTwoDigitsInVersion() throws Exception {
        // given
        String userAgentHeaderValue = "musicqubed/3.1 (Android; MTV)";

        // when
        editor.setAsText(userAgentHeaderValue);

        // then
        UserAgentRequest value = (UserAgentRequest) editor.getValue();

        assertEquals("musicqubed", value.getApplicationName());

        assertEquals(3, value.getVersion().major());
        assertEquals(1, value.getVersion().minor());
        assertEquals(0, value.getVersion().revision());

        assertEquals(UserRegInfo.DeviceType.ANDROID, value.getApplicationName());

        assertEquals("MTV", value.getCommunity());
    }

    @Test
    public void testSetAsTextWithQualifierInVersion() throws Exception {
        // given
        String userAgentHeaderValue = "musicqubed/3.1.SNAPSHOT (Android; MTV)";

        // when
        editor.setAsText(userAgentHeaderValue);

        // then
        UserAgentRequest value = (UserAgentRequest) editor.getValue();

        assertEquals("musicqubed", value.getApplicationName());

        assertEquals(3, value.getVersion().major());
        assertEquals(1, value.getVersion().minor());
        assertEquals(0, value.getVersion().revision());

        assertEquals(UserRegInfo.DeviceType.ANDROID, value.getApplicationName());

        assertEquals("MTV", value.getCommunity());
    }
}