package mobi.nowtechnologies.server.dto;

import mobi.nowtechnologies.server.dto.streamzine.FileNameAliasDto;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.shared.enums.ChartType;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Titov Mykhaylo (titov)
 */
public class ChartDto {

    public static final String CHART_DTO_LIST = "CHART_DTO_LIST";

    public static final String CHART_DTO = "chart";

    private Integer id;

    private Integer chartDetailId;

    private Byte position = 0;

    @NotNull
    @Length(min = 1, max = ChartDetail.TITLE_LENGTH)
    private String name;

    @NotNull
    @Length(min = 1, max = ChartDetail.SUBTITLE_LENGTH)
    private String subtitle;

    private MultipartFile file;

    private String imageFileName;

    private String imageTitle = "Default";

    private String description = "Default";

    private Boolean defaultChart;

    private ChartType chartType;

    private FileNameAliasDto fileNameAlias;

    private Long badgeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getPosition() {
        return position;
    }

    public void setPosition(Byte position) {
        this.position = position;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Integer getChartDetailId() {
        return chartDetailId;
    }

    public void setChartDetailId(Integer chartDetailId) {
        this.chartDetailId = chartDetailId;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public Boolean getDefaultChart() {
        return defaultChart;
    }

    public void setDefaultChart(Boolean defaultChart) {
        this.defaultChart = defaultChart;
    }

    public FileNameAliasDto getFileNameAlias() {
        return fileNameAlias;
    }

    public void setFileNameAlias(FileNameAliasDto fileNameAlias) {
        this.fileNameAlias = fileNameAlias;
    }

    public Long getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(Long badgeId) {
        this.badgeId = badgeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("chartDetailId", chartDetailId).append("position", position).append("name", name).append("subtitle", subtitle).append("file", file)
                                        .append("imageFileName", imageFileName).append("imageTitle", imageTitle).append("description", description).append("defaultChart", defaultChart)
                                        .append("chartType", chartType).append("fileNameAlias", fileNameAlias).toString();
    }
}
