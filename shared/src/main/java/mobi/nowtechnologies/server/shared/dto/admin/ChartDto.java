package mobi.nowtechnologies.server.shared.dto.admin;

import javax.validation.constraints.NotNull;

import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.validator.constraints.FileSize;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class ChartDto {

	public static final String CHART_DTO_LIST = "CHART_DTO_LIST";

	public static final String CHART_DTO = "chart";

	private Byte id;
	
	private Integer chartDetailId;

	private Byte position = 0;

	@NotNull
	@Length(min = 1, max = 25)
	private String name;

	@NotNull
	@Length(min = 1, max = 50)
	private String subtitle;
	
	@FileSize(min = 1, max = 50000, message="file size must be from 1 to 50 kbytes")
	private MultipartFile file;

	private String imageFileName;

	@NotNull
	@Length(min = 1, max = 50)
	private String imageTitle = "Default";
	
	@NotNull
	@Length(min = 1, max = 50)
	private String description = "Default";
	
	private ChartType chartType;

	public Byte getId() {
		return id;
	}

	public void setId(Byte id) {
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

	@Override
	public String toString() {
		return "ChartDto [id=" + id + ", chartDetailId=" + chartDetailId + ", position=" + position + ", name=" + name + ", subtitle=" + subtitle + ", file=" + file + ", imageFileName="
				+ imageFileName + ", imageTitle=" + imageTitle + ", description=" + description + ", chartType=" + chartType + "]";
	}

}
