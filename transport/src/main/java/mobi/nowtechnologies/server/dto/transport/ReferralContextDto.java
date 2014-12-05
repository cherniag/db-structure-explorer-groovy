package mobi.nowtechnologies.server.dto.transport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Created by zam on 11/24/2014.
 */
@JsonRootName("context-referrals")
public class ReferralContextDto {

    @JsonProperty(value = "required")
    private int required;
    @JsonProperty(value = "activated")
    private int activated;

    public void setRequired(int required) {
        this.required = required;
    }

    public void setActivated(int activated) {
        this.activated = activated;
    }

}
