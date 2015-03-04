package mobi.nowtechnologies.server.persistence.domain.behavior;

import mobi.nowtechnologies.server.persistence.domain.Community;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import java.io.Serializable;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import org.springframework.util.Assert;

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

    public BehaviorConfig getBehaviorConfig() {
        return behaviorConfig;
    }

    public void setBehaviorConfig(BehaviorConfig behaviorConfig) {
        this.behaviorConfig = behaviorConfig;
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
