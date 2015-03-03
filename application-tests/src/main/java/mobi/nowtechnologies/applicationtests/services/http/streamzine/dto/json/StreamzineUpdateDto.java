package mobi.nowtechnologies.applicationtests.services.http.streamzine.dto.json;

import mobi.nowtechnologies.server.dto.streamzine.VisualBlock;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

@JsonTypeName("value")
@XmlRootElement(name = "update")
@XmlAccessorType(XmlAccessType.NONE)
public class StreamzineUpdateDto {

    @XmlElement(name = "updated")
    @JsonProperty(value = "updated")
    private long updated;

    @XmlElement(name = "visual_blocks")
    @JsonProperty(value = "visual_blocks")
    private List<VisualBlock> blocks = new ArrayList<VisualBlock>();

    @XmlElement(name = "stream_content_items")
    @JsonProperty(value = "stream_content_items")
    private List<ContentItemDto> items = new ArrayList<ContentItemDto>();

    public Pair<VisualBlock, ContentItemDto> get(int index) {
        VisualBlock block = blocks.get(index);
        ContentItemDto dto = items.get(index);
        return new ImmutablePair<VisualBlock, ContentItemDto>(block, dto);
    }

    public long getUpdated() {
        return updated;
    }
}
