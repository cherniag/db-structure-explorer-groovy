package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.Utils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(name = "activation_emails",
       uniqueConstraints = {@UniqueConstraint(name = "activation_emails_email_deviceUID_token",
                                              columnNames = {"email", "deviceUID", "token"})})
public class ActivationEmail {

    public static final String ID = "mid";
    public static final String TOKEN = "token";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "deviceUID", nullable = false)
    private String deviceUID;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "activated", columnDefinition = "bit default 0")
    private boolean activated;

    public ActivationEmail(String email, String deviceUID, String token) {
        this.email = email;
        this.deviceUID = deviceUID;
        this.token = token;
    }

    public ActivationEmail() {
    }

    public static String generateToken(String email, String deviceUID) {
        return Utils.md5(email + deviceUID + Utils.getRandomString(7) + System.currentTimeMillis());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeviceUID() {
        return deviceUID;
    }

    public void setDeviceUID(String deviceUID) {
        this.deviceUID = deviceUID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
