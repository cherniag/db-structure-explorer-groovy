package mobi.nowtechnologies.server.shared.dto.admin;


/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class ChartDto {
	
	public static final String CHART_DTO_LIST = "CHART_DTO_LIST";

	private Byte id;
	
	private String name;

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

	@Override
	public String toString() {
		return "ChartDto [id=" + id + ", name=" + name + "]";
	}

}
