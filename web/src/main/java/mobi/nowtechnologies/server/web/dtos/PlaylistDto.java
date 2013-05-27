package mobi.nowtechnologies.server.web.dtos;

public class PlaylistDto {
	public static final String NAME = "playlist";
	public static final String NAME_LIST = "playlists";

	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "PlaylistDto [id=" + id + "]";
	}
}
