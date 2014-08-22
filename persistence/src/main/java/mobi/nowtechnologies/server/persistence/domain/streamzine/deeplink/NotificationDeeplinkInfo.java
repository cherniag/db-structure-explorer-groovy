package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sz_deeplink_promotional")
public class NotificationDeeplinkInfo extends InformationDeeplinkInfo {
    protected NotificationDeeplinkInfo() {
    }

    @Override
    protected InformationDeeplinkInfo getInstance() {
        return new NotificationDeeplinkInfo();
    }

    public NotificationDeeplinkInfo(LinkLocationType type, String url) {
        super(type, ContentType.PROMOTIONAL, url);
    }
}
