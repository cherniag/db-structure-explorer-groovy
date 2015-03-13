package mobi.nowtechnologies.server.persistence.domain.social;

import mobi.nowtechnologies.server.shared.enums.Gender;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import java.util.Date;

/**
 * Created by oar on 2/7/14.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "facebook_user_info")
public class FacebookUserInfo extends SocialInfo {

    @Column(name = "email", columnDefinition = "char(100)", nullable = false)
    private String email;

    @Column(name = "first_name", columnDefinition = "char(100)")
    private String firstName;

    @Column(name = "surname", columnDefinition = "char(100)")
    private String surname;

    @Column(name = "city", columnDefinition = "char(100)")
    private String city;


    @Column(name = "profile_url", columnDefinition = "char(200)")
    private String profileUrl;

    @Column(name = "fb_id", columnDefinition = "char(100)", nullable = false)
    private String facebookId;


    @Column(name = "user_name", columnDefinition = "char(100)", nullable = false)
    private String userName;

    @Column(name = "country", columnDefinition = "char(100)")
    private String country;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "gender", columnDefinition = "char(10)")
    private Gender gender;

    @Column(name = "date_of_birth")
    private Date birthday;

    @Column(name = "age_range_min")
    private Integer ageRangeMin;

    @Column(name = "age_range_max")
    private Integer ageRangeMax;

    @Column(name = "profile_image_url", columnDefinition = "char(255)")
    private String profileImageUrl;

    @Column(name = "profile_image_silhouette")
    private boolean profileImageSilhouette;

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getAvatarUrl() {
        return getProfileUrl();
    }

    @Override
    public String getSocialId() {
        return getFacebookId();
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getAgeRangeMin() {
        return ageRangeMin;
    }

    public void setAgeRangeMin(Integer ageRangeMin) {
        this.ageRangeMin = ageRangeMin;
    }

    public Integer getAgeRangeMax() {
        return ageRangeMax;
    }

    public void setAgeRangeMax(Integer ageRangeMax) {
        this.ageRangeMax = ageRangeMax;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isProfileImageSilhouette() {
        return profileImageSilhouette;
    }

    public void setProfileImageSilhouette(boolean profileImageSilhouette) {
        this.profileImageSilhouette = profileImageSilhouette;
    }
}
