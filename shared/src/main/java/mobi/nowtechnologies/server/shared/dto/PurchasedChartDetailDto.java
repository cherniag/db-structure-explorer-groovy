package mobi.nowtechnologies.server.shared.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@XmlRootElement(name = "track")
public class PurchasedChartDetailDto {
	
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
	private byte previousPosition;
	private String changePosition;
	private String channel;
	
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

	@Override
	public String toString() {
		return "PurchasedChartDetailDto [artist=" + artist + ", audioSize=" + audioSize + ", changePosition=" + changePosition + ", channel=" + channel + ", drmType=" + drmType + ", drmValue="
				+ drmValue + ", genre1=" + genre1 + ", genre2=" + genre2 + ", headerSize=" + headerSize + ", iTunesUrl=" + iTunesUrl + ", imageLargeSize=" + imageLargeSize + ", imageSmallSize="
				+ imageSmallSize + ", info=" + info + ", media=" + media + ", position=" + position + ", previousPosition=" + previousPosition + ", title=" + title + ", trackSize=" + trackSize + "]";
	}
}
