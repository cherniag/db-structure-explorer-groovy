package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sz_deeplink_news_list")
public class NewsListDeeplinkInfo extends DeeplinkInfo {
    @Column(name = "publish_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishDate;

    protected NewsListDeeplinkInfo() {
    }

    public NewsListDeeplinkInfo(Date publishDate) {
        if (publishDate != null) {
            this.publishDate = new Date(publishDate.getTime());
        }
        this.contentType = ContentType.NEWS;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    @Override
    protected DeeplinkInfo provideInstance() {
        NewsListDeeplinkInfo copy = new NewsListDeeplinkInfo();
        copy.publishDate = publishDate;
        return copy;
    }
}
