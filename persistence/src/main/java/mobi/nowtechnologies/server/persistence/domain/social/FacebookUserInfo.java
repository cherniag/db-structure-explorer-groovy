package mobi.nowtechnologies.server.persistence.domain.social;

import javax.persistence.*;

/**
 * Created by oar on 2/7/14.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "facebook_user_info", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"}), @UniqueConstraint(columnNames = {"fb_id"})})
public class FacebookUserInfo extends SocialInfo {
    private static final long serialVersionUID = 2546198857668889092L;

    @Column(name="email",columnDefinition="char(100)", nullable = false)
    private String email;

    @Column(name="first_name",columnDefinition="char(100)")
    private String firstName;

    @Column(name="surname",columnDefinition="char(100)")
    private String surname;

    @Column(name="city",columnDefinition="char(100)")
    private String city;


    @Column(name="profile_url",columnDefinition="char(200)")
    private String profileUrl;

    @Column(name="fb_id",columnDefinition="char(100)", nullable = false)
    private String facebookId;


    @Column(name="user_name",columnDefinition="char(100)", nullable = false)
    private String userName;

    @Column(name="country",columnDefinition="char(100)")
    private String country;

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }
}
