package mobi.nowtechnologies.server.dto.streamzine;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

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
}
