package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;

import javax.persistence.Entity;
import javax.persistence.Table;

import static mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType.EXTERNAL_AD;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.Opener.BROWSER;

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
        if (EXTERNAL_AD.equals(type)) {
            setOpener(BROWSER);
        }
    }
}
