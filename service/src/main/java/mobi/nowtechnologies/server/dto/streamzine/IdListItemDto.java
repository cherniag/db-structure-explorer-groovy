package mobi.nowtechnologies.server.dto.streamzine;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class IdListItemDto extends BaseContentItemDto {

    @XmlElement(name = "link_value")
    @JsonProperty(value = "link_value")
    private List<Integer> linkValue = new ArrayList<Integer>();

    protected IdListItemDto() {
    }

    public IdListItemDto(String id, DeeplinkType linkType) {
        super(id, linkType);
    }

    public void setLinkValue(List<Integer> linkValue) {
        this.linkValue.addAll(linkValue);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("linkValue", linkValue).toString();
    }
}
