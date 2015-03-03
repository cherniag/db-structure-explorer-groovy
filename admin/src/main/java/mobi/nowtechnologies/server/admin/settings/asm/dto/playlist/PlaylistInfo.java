package mobi.nowtechnologies.server.admin.settings.asm.dto.playlist;

import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("value")
@XmlAccessorType(XmlAccessType.NONE)
public class PlaylistInfo {

    @JsonProperty(value = "action")
    private String action;

    @JsonProperty(value = "chartBehaviorType")
    private ChartBehaviorType chartBehaviorType;

    public ChartBehaviorType getChartBehaviorType() {
        return chartBehaviorType;
    }

    public void setChartBehaviorType(ChartBehaviorType chartBehaviorType) {
        this.chartBehaviorType = chartBehaviorType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
