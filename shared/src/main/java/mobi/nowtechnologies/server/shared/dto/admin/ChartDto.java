package mobi.nowtechnologies.server.shared.dto.admin;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import mobi.nowtechnologies.server.validator.constraints.FileSize;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class ChartDto {

	public static final String CHART_DTO_LIST = "CHART_DTO_LIST";

	public static final String CHART_DTO = "CHART_DTO";

	private Byte id;

	@NotNull
	@Length(min = 1, max = 25)
	private String name;

	@NotNull
	@Length(min = 1, max = 50)
	private String subtitle;

	@FileSize(min = 1, max = 30720)
	private MultipartFile file;

	private String imageFileName;

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

	@Override
	public String toString() {
		return "ChartDto [id=" + id + ", name=" + name + ", subtitle=" + subtitle + ", imageFileName=" + imageFileName + ", file=" + file + "]";
	}

}
