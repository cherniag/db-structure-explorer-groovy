package mobi.nowtechnologies.applicationtests.services.http.streamzine.dto.json;

import mobi.nowtechnologies.server.dto.streamzine.BaseContentItemDto;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContentItemDto extends BaseContentItemDto {

    @XmlElement(name = "link_value")
    @JsonProperty(value = "link_value")
    private ListValueWrapper linkValue;

    public ListValueWrapper getLinkValue() {
        return linkValue;
    }
}
