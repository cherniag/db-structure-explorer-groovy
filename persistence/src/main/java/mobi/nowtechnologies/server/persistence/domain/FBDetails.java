package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.*;

/**
 * Created by oar on 2/7/14.
 */
@Entity
@Table(name = "tb_fbDetails", uniqueConstraints = {@UniqueConstraint(columnNames = {"userUID"})})
public class FBDetails {
    private static final long serialVersionUID = 2546198857668889092L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @JoinColumn(name = "userUID", nullable = false)
    @OneToOne
    private User user;

    @Column(name="email",columnDefinition="char(30)", nullable = false)
    private String email;

    @Column(name="firstName",columnDefinition="char(30)")
    private String firstName;

    @Column(name="surname",columnDefinition="char(30)")
    private String surname;

    @Column(name="profileUrl",columnDefinition="char(200)")
    private String profileUrl;

    @Column(name="fbId",columnDefinition="char(30)", nullable = false)
    private String facebookId;


    @Column(name="userName",columnDefinition="char(300)", nullable = false)
    private String userName;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Long getId() {
        return id;
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


}
