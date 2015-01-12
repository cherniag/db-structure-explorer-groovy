package mobi.nowtechnologies.server.transport.context.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;

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

    public ChartBehaviorDto(Date validFrom, ChartBehaviorType chartBehaviorType) {
        this.validFrom = validFrom;
        this.chartBehaviorType = chartBehaviorType;
    }

    public void setLockedAction(String lockedAction) {
        this.lockedAction = lockedAction;
    }

    public ChartBehaviorType getChartBehaviorType() {
        return chartBehaviorType;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public boolean shouldUnlock() {
        return lockedAction != null;
    }

    public void unlock() {
        lockedAction = null;
        if (ChartBehaviorType.PREVIEW.equals(chartBehaviorType)) {
            chartBehaviorType = ChartBehaviorType.NORMAL;
        }
    }
}
