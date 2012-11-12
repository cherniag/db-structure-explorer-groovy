package mobi.nowtechnologies.server.shared.dto.admin;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class ArtistDto {
	
	private int id;

	private String info;

	private String name;
	
	private String realName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	@Override
	public String toString() {
		return "ArtistDto [id=" + id + ", info=" + info + ", name=" + name + ", realName=" + realName + "]";
	}

}
