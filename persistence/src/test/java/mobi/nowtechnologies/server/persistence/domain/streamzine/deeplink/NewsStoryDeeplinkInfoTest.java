package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        final NewsDetailDto.MessageType notNewsType = NewsDetailDto.MessageType.POPUP;

        Message m = mock(Message.class);
        when(m.getMessageType()).thenReturn(notNewsType);
        return m;
    }

    private Message createNewsMessage(int id) {
        final NewsDetailDto.MessageType newsType = NewsDetailDto.MessageType.NEWS;

        Message m = mock(Message.class);
        when(m.getMessageType()).thenReturn(newsType);
        when(m.getId()).thenReturn(id);

        return m;
    }
}
