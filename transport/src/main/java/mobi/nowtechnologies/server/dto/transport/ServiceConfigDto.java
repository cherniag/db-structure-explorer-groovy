package mobi.nowtechnologies.server.dto.transport;

import com.fasterxml.jackson.annotation.JsonProperty;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheckStatus;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "serviceConfig")
public class ServiceConfigDto {
    @XmlElement(name = "status")
    @JsonProperty(value = "status")
    private VersionCheckStatus status;

    @XmlElement(name = "message")
    @JsonProperty(value = "message")
    private String message;

    @XmlElement(name = "link")
    @JsonProperty(value = "link")
    private String link;

    protected ServiceConfigDto() {
    }

    public ServiceConfigDto(VersionCheckStatus status, String message, String link) {
        this.status = status;
        this.message = message;
        this.link = link;
    }
}


