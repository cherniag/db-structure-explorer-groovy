package mobi.nowtechnologies.server.persistence.domain.social;

import mobi.nowtechnologies.server.shared.enums.Gender;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by oar on 4/28/2014.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "google_plus_user_info")
public class GooglePlusUserInfo extends SocialInfo{
    @Column(name="email",columnDefinition="char(100)", nullable = false)
    private String email;

    @Column(name="gp_id",columnDefinition="char(100)", nullable = false)
    private String googlePlusId;

    @Column(name="display_name",columnDefinition="char(100)")
    private String displayName;

    @Column(name="picture_url",columnDefinition="char(100)")
    private String picture;

    @Column(name="date_of_birth")
    private Date birthday;

    @Column(name="location",columnDefinition="char(100)")
    private String location;


    @Enumerated(value=EnumType.STRING)
    @Column(name="gender",columnDefinition="char(10)")
    private Gender gender;

    @Column(name="given_name",columnDefinition="char(100)")
    private String givenName;

    @Column(name="family_name",columnDefinition="char(100)")
    private String familyName;

    @Column(name="home_page",columnDefinition="char(100)")
    private String homePage;

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGooglePlusId() {
        return googlePlusId;
    }

    public void setGooglePlusId(String googlePlusId) {
        this.googlePlusId = googlePlusId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
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


}
