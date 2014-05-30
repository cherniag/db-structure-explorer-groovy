package mobi.nowtechnologies.server.dto.streamzine;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DeeplinkValueItemDto extends BaseContentItemDto {
    @XmlElement(name = "link_value")
    @JsonProperty(value = "link_value")
    private String linkValue;

    protected DeeplinkValueItemDto() {
    }

    public DeeplinkValueItemDto(String id, DeeplinkType linkType) {
        super(id, linkType);
    }

    public void setLinkValue(String linkValue) {
        this.linkValue = linkValue;
    }
}
