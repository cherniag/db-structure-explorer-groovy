package mobi.nowtechnologies.server.shared.dto.social;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.*;

/**
 * Created by oar on 2/11/14.
 */
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public abstract class UserDetailsDto {

    @JsonProperty("socialInfoType")
    @XmlElement
    public abstract SocialInfoType getSocialInfoType();
}
