package mobi.nowtechnologies.server.transport.context.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;
import mobi.nowtechnologies.server.service.behavior.ChartBehaviorInfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

@XmlAccessorType(XmlAccessType.NONE)
public class ChartBehaviorDto {
    @XmlElement(name = "behavior")
    @JsonProperty(value = "behavior")
    private ChartBehaviorType chartBehaviorType;

    @JsonProperty("validFrom")
    @XmlElement(name = "validFrom")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "GMT")
    private Date validFrom;

    @XmlElement(name = "lockedAction")
    @JsonProperty(value = "lockedAction")
    private String lockedAction;

    protected ChartBehaviorDto() {
    }

    public ChartBehaviorDto(ChartBehaviorInfo chartBehaviorInfo) {
        this.chartBehaviorType = Preconditions.checkNotNull(chartBehaviorInfo.getChartBehaviorType());
        this.validFrom = Preconditions.checkNotNull(chartBehaviorInfo.getValidFrom());
        this.lockedAction = chartBehaviorInfo.getLockedAction();
    }
}
