package mobi.nowtechnologies.server.transport.context.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfig;
import mobi.nowtechnologies.server.persistence.domain.referral.UserReferralsSnapshot;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

// Created by zam on 11/24/2014.
@JsonRootName("context-referrals")
@XmlAccessorType(XmlAccessType.NONE)
public class ReferralsContextDto {
    @JsonProperty("required")
    @XmlElement(name = "required")
    private int required = BehaviorConfig.IGNORE;

    @JsonProperty("activated")
    @XmlElement(name = "activated")
    private int activated = BehaviorConfig.IGNORE;

    public void fill(UserReferralsSnapshot snapshot) {
        required = snapshot.getRequiredReferrals();

        if (snapshot.isMatched()) {
            activated = required;
        } else {
            activated = snapshot.getCurrentReferrals();
        }
    }

}
