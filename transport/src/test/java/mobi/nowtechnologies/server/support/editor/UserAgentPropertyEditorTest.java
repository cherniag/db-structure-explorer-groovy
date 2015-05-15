package mobi.nowtechnologies.server.support.editor;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.support.UserAgent;

import org.springframework.beans.ConversionNotSupportedException;

import org.junit.*;
import org.junit.rules.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserAgentPropertyEditorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    CommunityRepository communityRepository = mock(CommunityRepository.class);
    UserAgentPropertyEditor editor = new UserAgentPropertyEditor(communityRepository) {
        @Override
        DeviceType toDeviceType(String deviceTypeString) {
            if ("android".equalsIgnoreCase(deviceTypeString) || "ios".equalsIgnoreCase(deviceTypeString)) {
                DeviceType deviceType = new DeviceType();
                deviceType.setName(deviceTypeString);
                return deviceType;
            }

            return null;
        }
    };

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUnknownCommunity() throws Exception {
        // given
        final String userAgentHeaderValue = "musicqubed/3.1.1 (Android; MTV)";

        // when
        thrown.expect(IllegalArgumentException.class);
        editor.setAsText(userAgentHeaderValue);

        // then
    }

    @Test
    public void testUnknownDeviceType() throws Exception {
        // given
        final String userAgentHeaderValue = "musicqubed/3.1.1 (UnknownPlatform; MTV)";

        // when
        thrown.expect(IllegalArgumentException.class);
        editor.setAsText(userAgentHeaderValue);

        // then
    }

    @Test
    public void testInvalidUserAgent() throws Exception {
        // given
        final String userAgentHeaderValue = "Some not valid user agent value";

        // when
        thrown.expect(ConversionNotSupportedException.class);
        editor.setAsText(userAgentHeaderValue);

        // then
    }

    @Test
    public void testNotSupported() throws Exception {
        // given
        // when
        thrown.expect(UnsupportedOperationException.class);
        editor.getAsText();

        // then
    }

    @Test
    public void testSetAsTextAllDigitsInVersion() throws Exception {
        // given
        final int communityId = 2;
        final Community community = CommunityFactory.createCommunityMock(communityId, "url");
        final String userAgentHeaderValue = "musicqubed/3.1.1 (Android; MTV)";

        // when
        when(communityRepository.findByRewriteUrlParameter("MTV")).thenReturn(community);
        editor.setAsText(userAgentHeaderValue);

        // then
        UserAgent value = (UserAgent) editor.getValue();

        assertEquals("musicqubed", value.getApplicationName());
        assertEquals(3, value.getVersion().major());
        assertEquals(1, value.getVersion().minor());
        assertEquals(1, value.getVersion().revision());
        assertEquals(DeviceType.ANDROID, value.getPlatform().getName());
        assertEquals(communityId, value.getCommunity().getId().intValue());

        verify(communityRepository).findByRewriteUrlParameter("MTV");
    }

    @Test
    public void testSetAsTextTwoDigitsInVersion() throws Exception {
        // given
        final int communityId = 2;
        final Community community = CommunityFactory.createCommunityMock(communityId, "url");
        final String userAgentHeaderValue = "musicqubed/3.1 (IOS; MTV)";

        // when
        when(communityRepository.findByRewriteUrlParameter("MTV")).thenReturn(community);
        editor.setAsText(userAgentHeaderValue);

        // then
        UserAgent value = (UserAgent) editor.getValue();

        assertEquals("musicqubed", value.getApplicationName());
        assertEquals(3, value.getVersion().major());
        assertEquals(1, value.getVersion().minor());
        assertEquals(0, value.getVersion().revision());
        assertEquals(DeviceType.IOS, value.getPlatform().getName());
        assertEquals(communityId, value.getCommunity().getId().intValue());

        verify(communityRepository).findByRewriteUrlParameter("MTV");
    }

    @Test
    public void testSetAsTextWithQualifierInVersion() throws Exception {
        // given
        final int communityId = 2;
        final Community community = CommunityFactory.createCommunityMock(communityId, "url");
        final String userAgentHeaderValue = "musicqubed/3.1-SNAPSHOT (IOS; MTV)";

        // when
        when(communityRepository.findByRewriteUrlParameter("MTV")).thenReturn(community);
        editor.setAsText(userAgentHeaderValue);

        // then
        UserAgent value = (UserAgent) editor.getValue();

        assertEquals("musicqubed", value.getApplicationName());
        assertEquals(3, value.getVersion().major());
        assertEquals(1, value.getVersion().minor());
        assertEquals(0, value.getVersion().revision());
        assertEquals(DeviceType.IOS, value.getPlatform().getName());
        assertEquals(communityId, value.getCommunity().getId().intValue());

        verify(communityRepository).findByRewriteUrlParameter("MTV");
    }
}