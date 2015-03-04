package mobi.nowtechnologies.server.persistence.domain.behavior;

import mobi.nowtechnologies.server.persistence.domain.UserStatusType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import java.io.Serializable;

/**
 * Created by zam on 12/9/2014.
 */
@Entity
@Table(name = "content_user_status_behavior")
public class ContentUserStatusBehavior implements Serializable {

    @Id
    private long id;

    // TODO: do we need this mapping?
    @OneToOne
    @JoinColumn(name = "behavior_config_id")
    private BehaviorConfig behaviorConfig;

    @Column(name = "user_status_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatusType userStatusType;

    @Column(name = "are_favorites_off")
    private boolean favoritesOff;

    @Column(name = "are_ads_off")
    private boolean addsOff;

    protected ContentUserStatusBehavior() {
    }

    public boolean isAddsOff() {
        return addsOff;
    }

    public void setAddsOff(boolean addsOff) {
        this.addsOff = addsOff;
    }

    public boolean isFavoritesOff() {
        return favoritesOff;
    }

    public void setFavoritesOff(boolean favoritesOff) {
        this.favoritesOff = favoritesOff;
    }

    public UserStatusType getUserStatusType() {
        return userStatusType;
    }

    @Override
    public String toString() {
        return "ContentUserStatusBehavior{" +
               "addsOff=" + addsOff +
               ", behaviorConfig=" + behaviorConfig +
               ", favoritesOff=" + favoritesOff +
               ", userStatusType=" + userStatusType +
               '}';
    }
}
