package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.shared.enums.MessageType;

import org.junit.*;
import static org.mockito.Mockito.*;

public class NewsStoryDeeplinkInfoTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorForImproperType() throws Exception {
        Message message = createNotNewsMessage();
        new NewsStoryDeeplinkInfo(message);
    }

    @Test
    public void testConstructorForProperType() throws Exception {
        final int id = 1;
        Message message = createNewsMessage(id);
        NewsStoryDeeplinkInfo info = new NewsStoryDeeplinkInfo(message);

        Assert.assertEquals(id, info.getMessage().getId().intValue());
    }

    private Message createNotNewsMessage() {
        final MessageType notNewsType = MessageType.POPUP;

        Message m = mock(Message.class);
        when(m.getMessageType()).thenReturn(notNewsType);
        return m;
    }

    private Message createNewsMessage(int id) {
        final MessageType newsType = MessageType.NEWS;

        Message m = mock(Message.class);
        when(m.getMessageType()).thenReturn(newsType);
        when(m.getId()).thenReturn(id);

        return m;
    }
}
