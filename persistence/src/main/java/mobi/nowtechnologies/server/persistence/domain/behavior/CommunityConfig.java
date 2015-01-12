package mobi.nowtechnologies.server.persistence.domain.behavior;

import mobi.nowtechnologies.server.persistence.domain.Community;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by zam on 12/10/2014.
 */
@Entity
@Table(name = "community_config")
public class CommunityConfig implements Serializable {
    @Id
    @OneToOne
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @OneToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "behavior_config_id", nullable = false)
    private BehaviorConfig behaviorConfig;

    protected CommunityConfig() {
    }

    public void setBehaviorConfig(BehaviorConfig behaviorConfig) {
        this.behaviorConfig = behaviorConfig;
    }

    public BehaviorConfig getBehaviorConfig() {
        return behaviorConfig;
    }

    public boolean requiresBehaviorConfigChange(BehaviorConfigType newType) {
        Assert.notNull(newType);
        return getBehaviorConfig().getType() != newType;
    }

    @Override
    public String toString() {
        return "CommunityConfig{" +
                "community id=" + community.getId() +
                ", behaviorConfig=" + behaviorConfig +
                '}';
    }
}
