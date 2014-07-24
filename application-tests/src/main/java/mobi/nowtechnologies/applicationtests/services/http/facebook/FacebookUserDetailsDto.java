package mobi.nowtechnologies.applicationtests.services.http.facebook;

import mobi.nowtechnologies.server.shared.dto.social.SocialInfoType;
import mobi.nowtechnologies.server.shared.dto.social.UserDetailsDto;

public class FacebookUserDetailsDto extends UserDetailsDto {
    private SocialInfoType socialInfoType;
    private String facebookId;

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    @Override
    public SocialInfoType getSocialInfoType() {
        return socialInfoType;
    }

    public void setSocialInfoType(SocialInfoType socialInfoType) {
        this.socialInfoType = socialInfoType;
    }
}
