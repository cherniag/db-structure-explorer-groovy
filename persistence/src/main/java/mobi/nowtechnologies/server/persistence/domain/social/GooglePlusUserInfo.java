package mobi.nowtechnologies.server.persistence.domain.social;

import javax.persistence.*;

/**
 * Created by oar on 4/28/2014.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "google_plus_user_info", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"}), @UniqueConstraint(columnNames = {"gp_id"})})
public class GooglePlusUserInfo extends SocialInfo{
    @Column(name="email",columnDefinition="char(100)", nullable = false)
    private String email;

    @Column(name="gp_id",columnDefinition="char(100)", nullable = false)
    private String googlePlusId;

    @Column(name="first_name",columnDefinition="char(100)")
    private String firstName;

    @Column(name="surname",columnDefinition="char(100)")
    private String surname;

    @Column(name="picture",columnDefinition="char(100)")
    private String picture;

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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
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


}
