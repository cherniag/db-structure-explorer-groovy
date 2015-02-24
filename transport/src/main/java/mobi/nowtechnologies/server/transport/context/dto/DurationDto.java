package mobi.nowtechnologies.server.transport.context.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.persistence.domain.Duration;
import org.springframework.util.Assert;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class DurationDto {
    @XmlElement(name = "number")
    @JsonProperty(value = "number")
    private int number;

    @XmlElement(name = "durationHours")
    @JsonProperty(value = "durationHours")
    private int durationHours;

    public DurationDto() {
    }

    public DurationDto(int number, Duration duration) {
        Assert.isTrue(duration.containsPeriod());

        this.number = number;
        this.durationHours = DateTimeUtils.toHours(duration.getAmount(), duration.getUnit());
    }
}