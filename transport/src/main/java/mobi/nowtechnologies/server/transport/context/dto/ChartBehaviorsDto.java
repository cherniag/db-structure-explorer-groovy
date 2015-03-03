package mobi.nowtechnologies.server.transport.context.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlAccessorType(XmlAccessType.NONE)
public class ChartBehaviorsDto implements Comparable<ChartBehaviorsDto> {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChartBehaviorsDto)) {
            return false;
        }

        ChartBehaviorsDto that = (ChartBehaviorsDto) o;

        if (id != that.id) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }


    @Override
    public int compareTo(ChartBehaviorsDto o) {
        return Integer.valueOf(id).compareTo(o.id);
    }
}
