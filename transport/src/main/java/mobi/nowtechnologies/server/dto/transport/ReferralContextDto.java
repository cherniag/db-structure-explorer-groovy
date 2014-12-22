package mobi.nowtechnologies.server.dto.transport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

// Created by zam on 11/24/2014.
@JsonRootName("context-referrals")
public class ReferralContextDto {

    @JsonProperty(value = "required")
    private int required;
    @JsonProperty(value = "activated")
    private int activated;

    public void setRequired(int required) {
        this.required = required;
    }

    public int getRequired() {
        return required;
    }

    public void setActivated(int activated) {
        this.activated = activated;
    }

    public int getActivated() {
        return activated;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .append("required", required)
                .append("activated", activated)
                .toString();
    }
}
