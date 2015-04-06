package mobi.nowtechnologies.server.transport.serviceconfig.dto;

import mobi.nowtechnologies.server.versioncheck.domain.VersionCheckStatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.commons.lang3.builder.ToStringBuilder;


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

    @XmlElement(name = "image")
    @JsonProperty(value = "image")
    private String image;


    protected ServiceConfigDto() {
    }

    public ServiceConfigDto(VersionCheckStatus status, String message, String link, String image) {
        this.status = status;
        this.message = message;
        this.link = link;
        this.image = image;
    }

    public void nullifyImage() {
        image = null;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this).append("status", status).append("message", message).append("link", link).append("image", image).toString();
    }
}


