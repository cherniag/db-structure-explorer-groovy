package mobi.nowtechnologies.server.shared.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

// @author Alexander Kolpakov (akolpakov)
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
    private int audioSize;
    private int imageLargeSize;
    private int imageSmallSize;
    private String iTunesUrl;
    private String amazonUrl;
    private byte previousPosition;
    private String changePosition;
    private String channel;
    private int chartDetailVersion;
    private boolean isArtistUrl;
    private Integer duration;
    private Integer playlistId;

    public ChartDetailDto(ChartDetailDto chartDetailDto) {
        this.position = chartDetailDto.position;
        this.media = chartDetailDto.media;
        this.title = chartDetailDto.title;
        this.artist = chartDetailDto.artist;
        this.info = chartDetailDto.info;
        this.genre1 = chartDetailDto.genre1;
        this.genre2 = chartDetailDto.genre2;
        this.drmType = chartDetailDto.drmType;
        this.drmValue = chartDetailDto.drmValue;
        this.audioSize = chartDetailDto.audioSize;
        this.imageLargeSize = chartDetailDto.imageLargeSize;
        this.imageSmallSize = chartDetailDto.imageSmallSize;
        this.iTunesUrl = chartDetailDto.iTunesUrl;
        this.amazonUrl = chartDetailDto.amazonUrl;
        this.previousPosition = chartDetailDto.previousPosition;
        this.changePosition = chartDetailDto.changePosition;
        this.channel = chartDetailDto.channel;
        this.chartDetailVersion = chartDetailDto.chartDetailVersion;
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

    public void setMedia(String media) {
        this.media = media;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public boolean isArtistUrl() {

        return isArtistUrl;
    }

    public void setArtistUrl(boolean artistUrl) {
        isArtistUrl = artistUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("position", position).append("media", media).append("title", title).append("artist", artist)
                                                            .append("info", info).append("genre1", genre1).append("genre2", genre2).append("drmType", drmType).append("drmValue", drmValue)
                                                            .append("audioSize", audioSize).append("imageLargeSize", imageLargeSize).append("imageSmallSize", imageSmallSize)
                                                            .append("iTunesUrl", iTunesUrl).append("amazonUrl", amazonUrl).append("previousPosition", previousPosition)
                                                            .append("changePosition", changePosition).append("channel", channel).append("chartDetailVersion", chartDetailVersion)
                                                            .append("isArtistUrl", isArtistUrl).append("duration", duration).append("playlistId", playlistId).toString();
    }
}
