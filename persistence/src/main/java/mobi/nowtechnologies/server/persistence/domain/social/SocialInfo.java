package mobi.nowtechnologies.server.persistence.domain.social;

import mobi.nowtechnologies.server.persistence.domain.User;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

/**
 * Created by oar on 2/10/14.
 */
@Entity
@Inheritance( strategy = InheritanceType.JOINED )
@Table(name="social_info")
public abstract class SocialInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;

    public abstract String getEmail();

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public abstract String getFirstName();

    public abstract String getAvatarUrl();

}

