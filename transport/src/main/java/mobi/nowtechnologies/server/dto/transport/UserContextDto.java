package mobi.nowtechnologies.server.dto.transport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by zam on 11/21/2014.
 */
@XmlRootElement(name = "context")
@JsonRootName("context")
public class UserContextDto {

    @JsonProperty(value = "referrals")
    private ReferralContextDto referralContextDto;

    public UserContextDto() {
    }

    public void setReferralContextDto(ReferralContextDto referralContextDto) {
        this.referralContextDto = referralContextDto;
    }
}
