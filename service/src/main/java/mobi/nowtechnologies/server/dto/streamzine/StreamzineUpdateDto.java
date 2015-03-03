package mobi.nowtechnologies.server.dto.streamzine;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.ToStringBuilder;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("updated", updated).append("blocks", blocks).append("items", items).toString();
    }
}
