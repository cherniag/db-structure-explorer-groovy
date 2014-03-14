package mobi.nowtechnologies.server.shared.dto.social;

import mobi.nowtechnologies.server.shared.enums.Gender;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Created by oar on 2/10/14.
 */
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class FacebookUserDetailsDto extends UserDetailsDto {

    private String email;


    private String firstName;

    private String surname;

    private String profileUrl;

    private String facebookId;

    private String userName;

    private String location;

    private Gender gender;

    private String birthDay;

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }


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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
