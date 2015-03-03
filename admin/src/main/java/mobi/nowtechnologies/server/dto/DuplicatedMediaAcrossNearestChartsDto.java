package mobi.nowtechnologies.server.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

// @author Titov Mykhaylo (titov) on 13.11.2014.
public class DuplicatedMediaAcrossNearestChartsDto {

    private int chartId;

    private String chartName;

    private long publishTimeMillis;

    private byte position;

    private String trackId;

    public int getChartId() {
        return chartId;
    }

    public void setChartId(int chartId) {
        this.chartId = chartId;
    }

    public String getChartName() {
        return chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public long getPublishTimeMillis() {
        return publishTimeMillis;
    }

    public void setPublishTimeMillis(long publishTimeMillis) {
        this.publishTimeMillis = publishTimeMillis;
    }

    public byte getPosition() {
        return position;
    }

    public void setPosition(byte position) {
        this.position = position;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("trackId", trackId).append("chartId", chartId).append("chartName", chartName).append("publishTimeMillis", publishTimeMillis)
                                        .append("position", position).toString();
    }
}
