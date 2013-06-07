package mobi.nowtechnologies.server.shared.dto;

import javax.xml.bind.annotation.XmlRootElement;

import mobi.nowtechnologies.server.shared.enums.ChartType;

@XmlRootElement(name = "playlist")
public class PlaylistDto {
	private Integer id;
	private String playlistTitle;
	private String subtitle;
	private String image;
	private String imageTitle;
	private String description;
	private Byte position;
	private ChartType type;

	public Integer getId() {
		return id;
	}

	public ChartType getType() {
		return type;
	}

	public void setType(ChartType type) {
		this.type = type;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPlaylistTitle() {
		return playlistTitle;
	}

	public void setPlaylistTitle(String playlistTitle) {
		this.playlistTitle = playlistTitle;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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

	public Byte getPosition() {
		return position;
	}

	public void setPosition(Byte position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "PlaylistDto [id=" + id + ", playlistTitle=" + playlistTitle + ", subtitle=" + subtitle + ", image=" + image + ", imageTitle=" + imageTitle + ", description=" + description
				+ ", position=" + position + ", type=" + type + "]";
	}
}