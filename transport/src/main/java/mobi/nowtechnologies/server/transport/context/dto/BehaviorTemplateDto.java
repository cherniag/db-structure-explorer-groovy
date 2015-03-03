package mobi.nowtechnologies.server.transport.context.dto;

import mobi.nowtechnologies.server.persistence.domain.Duration;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehavior;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlAccessorType(XmlAccessType.NONE)
public class BehaviorTemplateDto {

    @XmlElement(name = "offline")
    @JsonProperty(value = "offline")
    private boolean offline;

    @XmlElement(name = "playTime")
    @JsonProperty(value = "playTime")
    private Integer playTime;

    @XmlElement(name = "skipTracks")
    @JsonProperty(value = "skipTracks")
    private DurationDto skipTracks;

    @XmlElement(name = "maxTracks")
    @JsonProperty(value = "maxTracks")
    private DurationDto maxTracks;

    public void fill(ChartBehavior chartBehavior) {
        offline = chartBehavior.isOffline();

        if (chartBehavior.getType().isTracksInfoSupported()) {
            Duration skipTracksDuration = chartBehavior.getSkipTracksDuration();
            if (skipTracksDuration != null && skipTracksDuration.containsPeriod()) {
                skipTracks = new DurationDto(chartBehavior.getSkipTracks(), skipTracksDuration);
            }
            Duration maxTracksDuration = chartBehavior.getMaxTracksDuration();
            if (maxTracksDuration != null && maxTracksDuration.containsPeriod()) {
                maxTracks = new DurationDto(chartBehavior.getMaxTracks(), maxTracksDuration);
            }
        }

        if (chartBehavior.getType().isTracksPlayDurationSupported()) {
            playTime = chartBehavior.getPlayTracksSeconds();
        }
    }
}
