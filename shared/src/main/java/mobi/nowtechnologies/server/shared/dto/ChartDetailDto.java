package mobi.nowtechnologies.server.shared.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "track")
public class ChartDetailDto {

	private byte position;
	private String media;
	private String title;
	private String artist;
	private String info;
	private String genre1;
	private String genre2;
	private String drmType;
	private byte drmValue;
	private int trackSize;
	private int headerSize;
	private int audioSize;
	private int imageLargeSize;
	private int imageSmallSize;
	private String iTunesUrl;
	private String amazonUrl;
	private byte previousPosition;
	private String changePosition;
	private String channel;
	private int chartDetailVersion;
	private int headerVersion;
	private int audioVersion;
	private int imageLargeVersion;
	private int imageSmallVersion;
	private boolean isArtistUrl;
	private Integer playlistId;

	public ChartDetailDto(ChartDetailDto chartDetailDto) 
	{
	    this.position = chartDetailDto.position;
	    this.media = chartDetailDto.media;
	    this.title = chartDetailDto.title;
	    this.artist = chartDetailDto.artist;
	    this.info = chartDetailDto.info;
	    this.genre1 = chartDetailDto.genre1;
	    this.genre2 = chartDetailDto.genre2;
	    this.drmType = chartDetailDto.drmType;
	    this.drmValue = chartDetailDto.drmValue;
	    this.trackSize = chartDetailDto.trackSize;
	    this.headerSize = chartDetailDto.headerSize;
	    this.audioSize = chartDetailDto.audioSize;
	    this.imageLargeSize = chartDetailDto.imageLargeSize;
	    this.imageSmallSize = chartDetailDto.imageSmallSize;
	    this.iTunesUrl = chartDetailDto.iTunesUrl;
	    this.amazonUrl = chartDetailDto.amazonUrl;
	    this.previousPosition = chartDetailDto.previousPosition;
	    this.changePosition = chartDetailDto.changePosition;
	    this.channel = chartDetailDto.channel;
	    this.chartDetailVersion = chartDetailDto.chartDetailVersion;
	    this.headerVersion = chartDetailDto.headerVersion;
	    this.audioVersion = chartDetailDto.audioVersion;
	    this.imageLargeVersion = chartDetailDto.imageLargeVersion;
	    this.imageSmallVersion = chartDetailDto.imageSmallVersion;
	    this.isArtistUrl = chartDetailDto.isArtistUrl;
	    this.playlistId = chartDetailDto.playlistId;
	}

	public ChartDetailDto() {
	}

	public byte getPosition() {
		return this.position;
	}

	public void setPosition(byte position) {
		this.position = position;
	}

	public String getMedia() {
		return media;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getGenre1() {
		return genre1;
	}

	public void setGenre1(String genre1) {
		this.genre1 = genre1;
	}

	public String getGenre2() {
		return genre2;
	}

	public void setGenre2(String genre2) {
		this.genre2 = genre2;
	}

	public String getDrmType() {
		return drmType;
	}

	public void setDrmType(String drmType) {
		this.drmType = drmType;
	}

	public byte getDrmValue() {
		return drmValue;
	}

	public void setDrmValue(byte drmValue) {
		this.drmValue = drmValue;
	}

	public int getTrackSize() {
		return trackSize;
	}

	public void setTrackSize(int trackSize) {
		this.trackSize = trackSize;
	}

	public int getHeaderSize() {
		return headerSize;
	}

	public void setHeaderSize(int headerSize) {
		this.headerSize = headerSize;
	}

	public int getAudioSize() {
		return audioSize;
	}

	public void setAudioSize(int audioSize) {
		this.audioSize = audioSize;
	}

	public int getImageLargeSize() {
		return imageLargeSize;
	}

	public void setImageLargeSize(int imageLargeSize) {
		this.imageLargeSize = imageLargeSize;
	}

	public int getImageSmallSize() {
		return imageSmallSize;
	}

	public void setImageSmallSize(int imageSmallSize) {
		this.imageSmallSize = imageSmallSize;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public String getiTunesUrl() {
		return iTunesUrl;
	}

	public void setiTunesUrl(String iTunesUrl) {
		this.iTunesUrl = iTunesUrl;
	}

	public String getAmazonUrl() {
		return amazonUrl;
	}

	public void setAmazonUrl(String amazonUrl) {
		this.amazonUrl = amazonUrl;
	}

	public byte getPreviousPosition() {
		return previousPosition;
	}

	public void setPreviousPosition(byte previousPosition) {
		this.previousPosition = previousPosition;
	}

	public String getChangePosition() {
		return changePosition;
	}

	public void setChangePosition(String changePosition) {
		this.changePosition = changePosition;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public int getChartDetailVersion() {
		return chartDetailVersion;
	}

	public void setChartDetailVersion(int chartDetailVersion) {
		this.chartDetailVersion = chartDetailVersion;
	}

	public int getHeaderVersion() {
		return headerVersion;
	}

	public void setHeaderVersion(int headerVersion) {
		this.headerVersion = headerVersion;
	}

	public int getAudioVersion() {
		return audioVersion;
	}

	public void setAudioVersion(int audioVersion) {
		this.audioVersion = audioVersion;
	}

	public int getImageLargeVersion() {
		return imageLargeVersion;
	}

	public void setImageLargeVersion(int imageLargeVersion) {
		this.imageLargeVersion = imageLargeVersion;
	}

	public int getImageSmallVersion() {
		return imageSmallVersion;
	}

	public void setImageSmallVersion(int imageSmallVersion) {
		this.imageSmallVersion = imageSmallVersion;
	}

	public boolean isIsArtistUrl() {
		return isArtistUrl;
	}

	public void setIsArtistUrl(boolean isArtistUrl) {
		this.isArtistUrl = isArtistUrl;
	}

	public Integer getPlaylistId() {
		return playlistId;
	}

	public void setPlaylistId(Integer playlistId) {
		this.playlistId = playlistId;
	}

	@Override
	public String toString() {
		return "ChartDetailDto [position=" + position + ", media=" + media + ", title=" + title + ", artist=" + artist + ", info=" + info + ", genre1=" + genre1 + ", genre2=" + genre2 + ", drmType="
				+ drmType + ", drmValue=" + drmValue + ", trackSize=" + trackSize + ", headerSize=" + headerSize + ", audioSize=" + audioSize + ", imageLargeSize=" + imageLargeSize
				+ ", imageSmallSize=" + imageSmallSize + ", iTunesUrl=" + iTunesUrl + ", amazonUrl=" + amazonUrl + ", previousPosition=" + previousPosition + ", changePosition=" + changePosition
				+ ", channel=" + channel + ", chartDetailVersion=" + chartDetailVersion + ", headerVersion=" + headerVersion + ", audioVersion=" + audioVersion + ", imageLargeVersion="
				+ imageLargeVersion + ", imageSmallVersion=" + imageSmallVersion + ", isArtistUrl=" + isArtistUrl + ", playlistId=" + playlistId + "]";
	}
}
