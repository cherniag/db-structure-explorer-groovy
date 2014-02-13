package mobi.nowtechnologies.server.shared.dto.social;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Created by oar on 2/10/14.
 */
public class FacebookUserInfoDto extends SocialInfoDto {

    private String email;


    private String firstName;

    private String surname;

    private String profileUrl;

    private String facebookId;

    private String userName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    @Override
    public SocialInfoType getSocialInfoType() {
        return SocialInfoType.Facebook;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
