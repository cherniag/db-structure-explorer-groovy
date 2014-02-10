package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.*;


@Entity
@Table(name = "activation_emails",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "email", "deviceUID", "token"})})
public class ActivationEmail {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "deviceUID", nullable = false)
    private String deviceUID;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "activated", columnDefinition = "bit default 0")
    private boolean activated;

    public ActivationEmail(User user, String email, String deviceUID, String token) {
        this.user = user;
        this.email = email;
        this.deviceUID = deviceUID;
        this.token = token;
    }

    public ActivationEmail() {
    }

    public static String generateToken(String email, User user) {
        return "test";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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