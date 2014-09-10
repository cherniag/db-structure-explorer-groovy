package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.Opener;

import javax.persistence.Entity;
import javax.persistence.Table;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

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
        isTrue(type.equals(LinkLocationType.INTERNAL_AD));
    }

    public NotificationDeeplinkInfo(LinkLocationType type, String url, Opener opener) {
        super(type, ContentType.PROMOTIONAL, url, opener);
        isTrue(type.equals(LinkLocationType.EXTERNAL_AD));
        notNull(opener);
    }


}
