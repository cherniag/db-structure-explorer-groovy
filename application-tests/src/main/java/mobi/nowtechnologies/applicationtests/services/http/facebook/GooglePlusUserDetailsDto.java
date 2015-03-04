package mobi.nowtechnologies.applicationtests.services.http.facebook;

import mobi.nowtechnologies.server.shared.dto.social.SocialInfoType;
import mobi.nowtechnologies.server.shared.dto.social.UserDetailsDto;

public class GooglePlusUserDetailsDto extends UserDetailsDto {

    private SocialInfoType socialInfoType;
    private String googlePlusId;

    public String getGooglePlusId() {
        return googlePlusId;
    }

    @Override
    public SocialInfoType getSocialInfoType() {
        return socialInfoType;
    }

    public void setSocialInfoType(SocialInfoType socialInfoType) {
        this.socialInfoType = socialInfoType;
    }
}
