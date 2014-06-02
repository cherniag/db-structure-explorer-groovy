package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class InformationDeeplinkInfo extends DeeplinkInfo {
    @Column(name = "link_type")
    @Enumerated(EnumType.STRING)
    private LinkLocationType linkType;

    @Column(name = "url", length = 2048)
    private String url;

    @Column(name = "action", length = 255)
    private String action;

    protected InformationDeeplinkInfo() {
    }

    protected InformationDeeplinkInfo(LinkLocationType linkType, ContentType contentType, String url) {
        this.linkType = linkType;
        this.contentType = contentType;
        this.url = url;
    }

    @Override
    protected DeeplinkInfo provideInstance() {
        InformationDeeplinkInfo copy = getInstance();
        copy.url = url;
        copy.linkType = linkType;
        copy.action = action;
        return copy;
    }

    protected abstract InformationDeeplinkInfo getInstance();

    public String getUrl() {
        return url;
    }

    public LinkLocationType getLinkType() {
        return linkType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
