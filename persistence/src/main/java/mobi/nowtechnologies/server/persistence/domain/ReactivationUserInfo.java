package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Created by oar on 4/30/2014.
 */
@Entity
@Table(name = "reactivation_user_info", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id"})})
public class ReactivationUserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "reactivation_request")
    private boolean reactivationRequest;

    public Long getId() {
        return id;

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isReactivationRequest() {
        return reactivationRequest;
    }

    public void setReactivationRequest(boolean reactivationRequest) {
        this.reactivationRequest = reactivationRequest;
    }

}
