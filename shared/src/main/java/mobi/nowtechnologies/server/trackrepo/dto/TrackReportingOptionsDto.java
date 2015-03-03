package mobi.nowtechnologies.server.trackrepo.dto;

import mobi.nowtechnologies.server.trackrepo.enums.ReportingType;

import javax.validation.constraints.NotNull;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

// @author Titov Mykhaylo (titov) on 07.11.2014.
public class TrackReportingOptionsDto {

    @NotNull
    private Long trackId;

    @NotNull
    private ReportingType reportingType;

    private Set<String> negativeTags;

    public Long getTrackId() {
        return trackId;
    }

    public void setTrackId(Long trackId) {
        this.trackId = trackId;
    }

    public ReportingType getReportingType() {
        return reportingType;
    }

    public void setReportingType(ReportingType reportingType) {
        this.reportingType = reportingType;
    }

    public Set<String> getNegativeTags() {
        return negativeTags;
    }

    public void setNegativeTags(Set<String> negativeTags) {
        this.negativeTags = negativeTags;
    }

    public TrackReportingOptionsDto withTrackId(Long trackId) {
        this.trackId = trackId;
        return this;
    }

    public TrackReportingOptionsDto withReportingType(ReportingType reportingType) {
        this.reportingType = reportingType;
        return this;
    }

    public TrackReportingOptionsDto withNegativeTags(Set<String> negativeTags) {
        this.negativeTags = negativeTags;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).append("trackId", trackId).append("reportingType", reportingType).append("negativeTags", negativeTags).toString();
    }
}
