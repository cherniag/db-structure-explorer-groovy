package mobi.nowtechnologies.server.dto.streamzine;

import com.google.common.collect.Lists;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

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
    private List<BaseContentItemDto> items = new ArrayList<BaseContentItemDto>();

    public StreamzineUpdateDto(long updated) {
        this.updated = updated;
    }

    public StreamzineUpdateDto() {
    }

    public void addVisualBlock(VisualBlock visualBlock) {
        blocks.add(visualBlock);
    }

    public void addContentItem(BaseContentItemDto contentItemDto) {
        items.add(contentItemDto);
    }

    public long getUpdated() {
        return updated;
    }

    public List<VisualBlock> getBlocks() {
        return Lists.newArrayList(blocks);
    }

    public List<BaseContentItemDto> getItems() {
        return Lists.newArrayList(items);
    }
}
