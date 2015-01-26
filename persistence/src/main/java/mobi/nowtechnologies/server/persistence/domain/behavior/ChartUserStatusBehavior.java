package mobi.nowtechnologies.server.persistence.domain.behavior;

import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by zam on 12/9/2014.
 */
@Entity
@Table(name = "chart_user_status_behavior")
public class ChartUserStatusBehavior implements Serializable {
    @Id
    private long id;

    @Column(name = "chart_id", nullable = false)
    private int chartId;

    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "chart_behavior_id")
    private ChartBehavior chartBehavior;

    @Column(name = "user_status_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatusType userStatusType;

    @Column(name = "is_locked")
    private boolean locked;

    @Column(name = "action")
    private String action;

    protected ChartUserStatusBehavior() {
    }

    public int getChartId() {
        return chartId;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ChartBehavior getChartBehavior() {
        return chartBehavior;
    }

    public void setChartBehavior(ChartBehavior chartBehavior) {
        this.chartBehavior = chartBehavior;
    }

    public UserStatusType getUserStatusType() {
        return userStatusType;
    }

    @Override
    public String toString() {
        return "ChartUserStatusBehavior{" +
                "  chart id=" + chartId +
                ", action=" + action +
                ", userStatusType=" + userStatusType +
                ", locked=" + locked +
                ", chartBehavior='" + chartBehavior + '\'' +
                '}';
    }
}
