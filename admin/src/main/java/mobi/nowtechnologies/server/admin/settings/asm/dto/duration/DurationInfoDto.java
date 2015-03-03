package mobi.nowtechnologies.server.admin.settings.asm.dto.duration;

import mobi.nowtechnologies.server.persistence.domain.Duration;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("durationInfoDto")
@XmlAccessorType(XmlAccessType.NONE)
public class DurationInfoDto {

    @JsonProperty(value = "amount")
    private int amount;

    @JsonProperty(value = "durationUnit")
    private DurationUnit durationUnit;

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setDurationUnit(DurationUnit durationUnit) {
        this.durationUnit = durationUnit;
    }

    public Duration toDuration() {
        if (amount <= 0 || durationUnit == null) {
            return Duration.noPeriod();
        }
        else {
            return Duration.forPeriod(amount, durationUnit);
        }
    }

    public void fromDuration(Duration duration) {
        if (duration.containsPeriod()) {
            amount = duration.getAmount();
            durationUnit = duration.getUnit();
        }
        else {
            amount = 0;
            durationUnit = null;
        }
    }
}
