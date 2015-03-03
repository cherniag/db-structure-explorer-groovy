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
        this.name = (name == null) ?
                    "" :
                    name;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChartListItemDto that = (ChartListItemDto) o;

        if (tracksCount != that.tracksCount) {
            return false;
        }
        if (chartId != null ?
            !chartId.equals(that.chartId) :
            that.chartId != null) {
            return false;
        }
        if (imageFileName != null ?
            !imageFileName.equals(that.imageFileName) :
            that.imageFileName != null) {
            return false;
        }
        if (name != null ?
            !name.equals(that.name) :
            that.name != null) {
            return false;
        }
        if (subtitle != null ?
            !subtitle.equals(that.subtitle) :
            that.subtitle != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ?
                     name.hashCode() :
                     0;
        result = 31 * result + (subtitle != null ?
                                subtitle.hashCode() :
                                0);
        result = 31 * result + (imageFileName != null ?
                                imageFileName.hashCode() :
                                0);
        result = 31 * result + tracksCount;
        result = 31 * result + (chartId != null ?
                                chartId.hashCode() :
                                0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).append("subtitle", subtitle).append("imageFileName", imageFileName).append("tracksCount", tracksCount).append("chartId", chartId)
                                        .toString();
    }
}
