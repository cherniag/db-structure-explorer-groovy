package mobi.nowtechnologies.server.dto.transport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheckStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@JsonTypeName("versionCheck")
@XmlRootElement(name = "versionCheck")
@XmlAccessorType(XmlAccessType.NONE)
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

    @XmlElement(name = "imageFileName")
    @JsonProperty(value = "imageFileName")
    private String imageFileName;


    protected ServiceConfigDto() {
    }

    public ServiceConfigDto(VersionCheckStatus status, String message, String link, String imageFileName) {
        this.status = status;
        this.message = message;
        this.link = link;
        this.imageFileName = imageFileName;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("status", status)
                .append("message", message)
                .append("link", link)
                .append("imageFileName", imageFileName)
                .toString();
    }
}


