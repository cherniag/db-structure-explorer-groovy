package mobi.nowtechnologies.server.shared.dto.social;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by oar on 2/11/14.
 */
public abstract class SocialInfoDto {

    @JsonProperty("socialInfoType")
    public abstract SocialInfoType getSocialInfoType();
}
