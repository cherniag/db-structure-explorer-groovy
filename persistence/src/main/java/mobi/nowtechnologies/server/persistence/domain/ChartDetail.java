package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import static javax.persistence.EnumType.ORDINAL;
import static javax.persistence.FetchType.EAGER;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "tb_chartDetail", uniqueConstraints = @UniqueConstraint(columnNames = {"media", "chart", "publishTimeMillis"}))
public class ChartDetail {

    public static final int TITLE_LENGTH = 255;
    public static final int SUBTITLE_LENGTH = 255;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChartDetail.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer i;

    @Column(name = "chart", insertable = false, updatable = false)
    private Integer chartId;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "chart")
    private Chart chart;

    @Column(name = "media", insertable = false, updatable = false)
    private Integer mediaId;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "media")
    private Media media;

    @Column(name = "info", columnDefinition = "text")
    @Lob()
    private String info;

    private byte position;

    private Byte prevPosition;

    @Column(name = "chgPosition", insertable = false, updatable = false)
    private Integer chgPositionId;

    @Enumerated(ORDINAL)
    private ChgPosition chgPosition;

    private String channel;

    @Column(name = "image_filename")
    private String imageFileName;

    @Column(name = "image_title")
    private String imageTitle;

    @Column(name = "title", length = TITLE_LENGTH, nullable = true)
    private String title;

    @Column(name = "subtitle", length = SUBTITLE_LENGTH, nullable = true)
    private String subtitle;

    private long publishTimeMillis;

    private Boolean locked;

    private Boolean defaultChart;

    @Column(name = "badge_filename_id")
    private Long badgeId;

    @Version
    private Integer version;

    public ChartDetail() {
    }

    public static ChartDetail newInstance(ChartDetail chartDetail) {
        LOGGER.debug("input parameters chartDetail: [{}]", chartDetail);

        ChartDetail newChartDetail = new ChartDetail();
        newChartDetail.setChannel(chartDetail.getChannel());
        newChartDetail.setChart(chartDetail.getChart());
        newChartDetail.setChgPosition(chartDetail.getChgPosition());
        newChartDetail.setInfo(chartDetail.getInfo());
        if (chartDetail.getMedia() != null) {
            newChartDetail.setMedia(chartDetail.getMedia());
        }
        newChartDetail.setPosition(chartDetail.getPosition());
        newChartDetail.setPrevPosition(chartDetail.getPrevPosition());
        newChartDetail.setTitle(chartDetail.getTitle());
        newChartDetail.setSubtitle(chartDetail.getSubtitle());
        newChartDetail.setImageFileName(chartDetail.getImageFileName());
        newChartDetail.setImageTitle(chartDetail.getImageTitle());
        newChartDetail.setPublishTimeMillis(chartDetail.getPublishTimeMillis());
        newChartDetail.setDefaultChart(chartDetail.getDefaultChart());
        newChartDetail.setBadgeId(chartDetail.getBadgeId());

        LOGGER.info("Output parameter newChartDetail=[{}]", newChartDetail);
        return newChartDetail;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getI() {
        return this.i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public boolean isDefaultChart() {
        return defaultChart;
    }

    public void setDefaultChart(boolean defaultChart) {
        this.defaultChart = defaultChart;
    }

    public Chart getChart() {
        return this.chart;
    }

    public void setChart(Chart chart) {
        this.chart = chart;
        chartId = chart.getI();
    }

    public Integer getChartId() {
        return chartId;
    }

    public Media getMedia() {
        return this.media;
    }

    public void setMedia(Media media) {
        this.media = media;
        mediaId = media.getI() != null ?
                  media.getI() :
                  null;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public ChartType getChartType() {
        return chart.getType();
    }

    public String getChartDescription() {
        return getInfo();
    }

    public Boolean getDefaultChart() {
        return defaultChart;
    }

    public void setDefaultChart(Boolean defaultChart) {
        this.defaultChart = defaultChart;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public byte getPosition() {
        return this.position;
    }

    public void setPosition(byte position) {
        this.position = position;
    }

    public int getMediaId() {
        return mediaId;
    }

    public Byte getPrevPosition() {
        return prevPosition;
    }

    public void setPrevPosition(Byte prevPosition) {
        this.prevPosition = prevPosition;
    }

    public ChgPosition getChgPosition() {
        return chgPosition;
    }

    public void setChgPosition(ChgPosition chgPosition) {
        this.chgPosition = chgPosition;
    }

    public Integer getChgPositionId() {
        return chgPositionId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public long getPublishTimeMillis() {
        return publishTimeMillis;
    }

    public void setPublishTimeMillis(long publishTimeMillis) {
        this.publishTimeMillis = publishTimeMillis;
    }

    @Deprecated
    public int getVersionAsPrimitive() {
        return version != null ?
               version :
               0;
    }

    @Deprecated
    public void setVersionAsPrimitive(int version) {
        this.version = version;
    }

    public boolean isChartItem() {
        return media != null;
    }

    public ChartDetail withI(Integer i) {
        setI(i);
        return this;
    }

    public ChartDetail withMedia(Media media) {
        setMedia(media);
        return this;
    }

    public ChartDetail withChart(Chart chart) {
        setChart(chart);
        return this;
    }

    public ChartDetail withPrevPosition(Byte prevPosition) {
        setPrevPosition(prevPosition);
        return this;
    }

    public ChartDetail withChgPosition(ChgPosition chgPosition) {
        setChgPosition(chgPosition);
        return this;
    }

    public ChartDetail withChannel(String channel) {
        setChannel(channel);
        return this;
    }

    public ChartDetail withPosition(int position) {
        this.position = (byte) position;
        return this;
    }

    public ChartDetail withPublishTime(long time) {
        this.setPublishTimeMillis(time);
        return this;
    }

    public Long getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(Long badgeId) {
        this.badgeId = badgeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("i", i).append("chartId", chartId).append("mediaId", mediaId).append("info", info).append("position", position).append("prevPosition", prevPosition)
                                        .append("chgPositionId", chgPositionId).append("channel", channel).append("imageFileName", imageFileName).append("imageTitle", imageTitle)
                                        .append("title", title).append("subtitle", subtitle).append("publishTimeMillis", publishTimeMillis).append("locked", locked)
                                        .append("defaultChart", defaultChart).append("badgeId", badgeId).append("version", version).toString();
    }

}