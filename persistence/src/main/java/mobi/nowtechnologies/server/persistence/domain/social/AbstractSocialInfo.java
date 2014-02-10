package mobi.nowtechnologies.server.persistence.domain.social;

import mobi.nowtechnologies.server.persistence.domain.User;

import javax.persistence.*;

/**
 * Created by oar on 2/10/14.
 */
@Entity
@Inheritance( strategy = InheritanceType.JOINED )
@DiscriminatorColumn(name = "source")
@Table(name="tb_abstractSocialInfo", uniqueConstraints = {@UniqueConstraint(columnNames = {"userUID", "infoSource"})})
public abstract class AbstractSocialInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "userUID", nullable = false)
    @ManyToOne
    private User user;


    @Column(name = "infoSource")
    private SocialInfoType sourceType;


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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractSocialInfo that = (AbstractSocialInfo) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

