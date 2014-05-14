package mobi.nowtechnologies.server.shared.dto.social;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Created by oar on 4/28/2014.
 */
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class GooglePlusUserDetailsDto extends UserDetailsDto{

    private String googlePlusId;


    private String email;

    private String firstName;

    private String surname;


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


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public SocialInfoType getSocialInfoType() {
        return SocialInfoType.GooglePlus;
    }

    public String getGooglePlusId() {
        return googlePlusId;
    }

    public void setGooglePlusId(String googlePlusId) {
        this.googlePlusId = googlePlusId;
    }


}
