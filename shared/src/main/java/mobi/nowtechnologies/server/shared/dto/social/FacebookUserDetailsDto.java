package mobi.nowtechnologies.server.shared.dto.social;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Created by oar on 2/10/14.
 */
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class FacebookUserDetailsDto extends UserDetailsDto {


    private String facebookId;


    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }


    @Override
    public SocialInfoType getSocialInfoType() {
        return SocialInfoType.Facebook;
    }

}
