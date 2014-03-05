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

    @Column(name="location",columnDefinition="char(100)")
    private String location;


    @Column(name="profile_url",columnDefinition="char(200)")
    private String profileUrl;

    @Column(name="fb_id",columnDefinition="char(100)", nullable = false)
    private String facebookId;


    @Column(name="user_name",columnDefinition="char(100)", nullable = false)
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
