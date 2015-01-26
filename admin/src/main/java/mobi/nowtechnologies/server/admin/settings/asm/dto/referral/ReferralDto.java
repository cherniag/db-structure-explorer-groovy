package mobi.nowtechnologies.server.admin.settings.asm.dto.referral;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import mobi.nowtechnologies.server.admin.settings.asm.dto.duration.DurationInfoDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@JsonTypeName("referral")
@XmlAccessorType(XmlAccessType.NONE)
public class ReferralDto {
    @JsonProperty(value = "required")
    private int required;

    @JsonProperty(value = "durationInfoDto")
    private DurationInfoDto durationInfoDto = new DurationInfoDto();

    public DurationInfoDto getDurationInfoDto() {
        return durationInfoDto;
    }

    public int getRequired() {
        return required;
    }

    public void setRequired(int required) {
        this.required = required;
    }



}
