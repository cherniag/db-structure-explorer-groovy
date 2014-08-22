package mobi.nowtechnologies.server.dto.streamzine;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("linkValue", linkValue)
                .toString();
    }
}
