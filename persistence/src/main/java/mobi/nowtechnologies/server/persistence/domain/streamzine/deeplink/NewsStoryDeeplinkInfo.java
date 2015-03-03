package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.shared.enums.MessageType;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.util.Assert;

@Entity
@Table(name = "sz_deeplink_news_story")
public class NewsStoryDeeplinkInfo extends DeeplinkInfo {

    @OneToOne
    @JoinColumn(name = "news_id")
    private Message message;

    protected NewsStoryDeeplinkInfo() {
    }

    public NewsStoryDeeplinkInfo(Message message) {
        if (message != null) {
            Assert.isTrue(message.getMessageType() == MessageType.NEWS);
        }

        this.message = message;
        this.contentType = ContentType.NEWS;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    protected DeeplinkInfo provideInstance() {
        NewsStoryDeeplinkInfo copy = new NewsStoryDeeplinkInfo();
        copy.message = message;
        return copy;
    }
}
