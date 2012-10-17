package mobi.nowtechnologies.server.dto;

public class CommunityDto {
	
	private String url;
	private boolean active;
	
	public CommunityDto() {
	}
	
	public CommunityDto(String communityUrl) {
		super();
		this.url = communityUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}