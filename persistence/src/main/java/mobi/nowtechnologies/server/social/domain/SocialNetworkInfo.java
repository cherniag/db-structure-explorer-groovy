/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Date;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "social_network_info")
public class SocialNetworkInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "social_network_type")
    private SocialNetworkType socialNetworkType;

    @Column(name = "social_network_id")
    private String socialNetworkId;

    @Column(name = "email")
    private String email;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "gender_type")
    private GenderType genderType;

    @Column(name = "date_of_birth")
    private Date birthday;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "age_range_min")
    private Integer ageRangeMin;

    @Column(name = "age_range_max")
    private Integer ageRangeMax;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "profile_image_silhouette")
    private boolean profileImageSilhouette;

    protected SocialNetworkInfo() {}

    public SocialNetworkInfo(SocialNetworkType socialNetworkType) {
        this.socialNetworkType = Preconditions.checkNotNull(socialNetworkType);
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

    public GenderType getGenderType() {
        return genderType;
    }

    public void setGenderType(GenderType genderType) {
        this.genderType = genderType;
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

    public String getSocialNetworkId() {
        return socialNetworkId;
    }

    public void setSocialNetworkId(String socialNetworkId) {
        this.socialNetworkId = socialNetworkId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
