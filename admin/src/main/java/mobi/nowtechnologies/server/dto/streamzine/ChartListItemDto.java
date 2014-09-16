package mobi.nowtechnologies.server.dto.streamzine;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ChartListItemDto implements Comparable<ChartListItemDto> {
    private String name;
    private String subtitle;
    private String imageFileName;
    private int tracksCount;
    private Integer chartId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name == null) ? "" : name;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public int getTracksCount() {
        return tracksCount;
    }

    public void setTracksCount(int tracksCount) {
        this.tracksCount = tracksCount;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Override
    public int compareTo(ChartListItemDto o) {
        return name.compareTo(o.name);
    }

    public Integer getChartId() {
        return chartId;
    }

    public void setChartId(Integer chartId) {
        this.chartId = chartId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("subtitle", subtitle)
                .append("imageFileName", imageFileName)
                .append("tracksCount", tracksCount)
                .append("chartId", chartId)
                .toString();
    }
}
