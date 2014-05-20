package mobi.nowtechnologies.server.shared.dto.social;


import com.fasterxml.jackson.annotation.JsonProperty;
import mobi.nowtechnologies.server.shared.enums.Gender;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by oar on 2/11/14.
 */
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public abstract class UserDetailsDto {

    private String profileUrl;

    private String email;

    private String firstName;

    private String surname;

    private String location;

    private String userName;

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

    private Gender gender;

    private String birthDay;



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

    @JsonProperty("socialInfoType")
    @XmlElement
    public abstract SocialInfoType getSocialInfoType();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
