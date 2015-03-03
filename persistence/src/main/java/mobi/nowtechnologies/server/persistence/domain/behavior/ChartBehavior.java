package mobi.nowtechnologies.server.persistence.domain.behavior;

import mobi.nowtechnologies.server.persistence.domain.Duration;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import java.io.Serializable;

import org.hibernate.annotations.Cascade;

/**
 * Created by zam on 12/9/2014.
 */
@Entity
@Table(name = "chart_behavior")
public class ChartBehavior implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "behavior_config_id")
    private BehaviorConfig behaviorConfig;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChartBehaviorType type;

    @Column(name = "is_offline")
    private boolean offline = true;

    @Column(name = "max_tracks")
    private int maxTracks = BehaviorConfig.IGNORE;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "amount", column = @Column(name = "max_tracks_duration")), @AttributeOverride(name = "unit", column = @Column(name = "max_tracks_duration_type"))})
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private Duration maxTracksDuration = Duration.noPeriod();

    @Column(name = "skip_tracks")
    private int skipTracks = BehaviorConfig.IGNORE;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "amount", column = @Column(name = "skip_tracks_duration")), @AttributeOverride(name = "unit", column = @Column(name = "skip_tracks_duration_type"))})
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private Duration skipTracksDuration = Duration.noPeriod();

    @Column(name = "play_tracks_seconds")
    private int playTracksSeconds = BehaviorConfig.IGNORE;

    protected ChartBehavior() {
    }

    public ChartBehaviorType getType() {
        return type;
    }

    public void setType(ChartBehaviorType type) {
        this.type = type;
    }

    public void updateMaxTracksInfo(int count, Duration duration) {
        maxTracks = (count < 0) ?
                    0 :
                    count;
        maxTracksDuration = duration;
    }

    public void updateSkipTracksInfo(int count, Duration duration) {
        skipTracks = (count < 0) ?
                     0 :
                     count;
        skipTracksDuration = duration;
    }

    public long getId() {
        return id;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public int getPlayTracksSeconds() {
        return playTracksSeconds;
    }

    public void setPlayTracksSeconds(int playTracksSeconds) {
        this.playTracksSeconds = playTracksSeconds;
    }

    public int getMaxTracks() {
        return maxTracks;
    }

    public Duration getMaxTracksDuration() {
        return maxTracksDuration;
    }

    public int getSkipTracks() {
        return skipTracks;
    }

    public Duration getSkipTracksDuration() {
        return skipTracksDuration;
    }

    public BehaviorConfig getBehaviorConfig() {
        return behaviorConfig;
    }

    @Override
    public String toString() {
        return "ChartBehavior{" +
               "behaviorConfig=" + behaviorConfig +
               ", type=" + type +
               ", offline=" + offline +
               ", maxTracks=" + maxTracks +
               ", maxTracksDuration=" + maxTracksDuration +
               ", skipTracks=" + skipTracks +
               ", skipTracksDuration=" + skipTracksDuration +
               ", playTracksSeconds=" + playTracksSeconds +
               '}';
    }
}
