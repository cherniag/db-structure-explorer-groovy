package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.dto.OAuthProvider;
import mobi.nowtechnologies.server.shared.enums.Gender;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.util.Date;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name = "social_network_info")
public class SocialNetworkInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "social_network")
    private OAuthProvider socialNetwork;

    @Column(name = "social_network_id")
    private String socialNetworkId;

    @Column(name = "email")
    private String email;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "date_of_birth")
    private Date birthday;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "location")
    private String location;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "age_range_min")
    private Integer ageRangeMin;

    @Column(name = "age_range_max")
    private Integer ageRangeMax;

    @Column(name = "profile_image_silhouette")
    private boolean profileImageSilhouette;

    protected SocialNetworkInfo(){}

    public SocialNetworkInfo(OAuthProvider socialNetwork){
        this.socialNetwork = Preconditions.checkNotNull(socialNetwork);
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getProfileUrl() {
        return profileUrl;
    }
    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public void setSocialNetworkId(String socialNetworkId) {
        this.socialNetworkId = socialNetworkId;
    }
    public String getSocialNetworkId() {
        return socialNetworkId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
