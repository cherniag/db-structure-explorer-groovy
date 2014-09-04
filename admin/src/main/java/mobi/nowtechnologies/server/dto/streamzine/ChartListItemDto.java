package mobi.nowtechnologies.server.dto.streamzine;

import mobi.nowtechnologies.server.shared.enums.ChartType;

public class ChartListItemDto implements Comparable<ChartListItemDto> {
    private String name;
    private String subtitle;
    private String imageFileName;
    private ChartType chartType;
    private int tracksCount;
    private Integer chartDetailId;

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

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
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

    public Integer getChartDetailId() {
        return chartDetailId;
    }

    public void setChartDetailId(Integer chartDetailId) {
        this.chartDetailId = chartDetailId;
    }
}
