package mobi.nowtechnologies.server.shared.dto.social;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by oar on 2/11/14.
 */
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public abstract class UserDetailsDto {

    private String profileUrl;

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    @JsonProperty("socialInfoType")
    @XmlElement
    public abstract SocialInfoType getSocialInfoType();
}
