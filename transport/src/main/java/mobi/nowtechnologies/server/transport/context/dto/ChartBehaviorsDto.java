package mobi.nowtechnologies.server.transport.context.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
public class ChartBehaviorsDto {
    @XmlElement(name = "ID")
    @JsonProperty(value = "ID")
    private int id;

    @XmlElement(name = "behavior")
    @JsonProperty(value = "behavior")
    private List<ChartBehaviorDto> behavior = new ArrayList<ChartBehaviorDto>();

    protected ChartBehaviorsDto() {
    }

    public ChartBehaviorsDto(int chartId) {
        this.id = chartId;
    }

    public List<ChartBehaviorDto> getBehavior() {
        return behavior;
    }

}
