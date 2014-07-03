package mobi.nowtechnologies.server.dto.streamzine;

import mobi.nowtechnologies.server.shared.dto.admin.ArtistDto;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MediaDto {
    private String isrc;
    private String trackId;
    private ArtistDto artistDto;
    private String fileName;
    private String title;

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public ArtistDto getArtistDto() {
        return artistDto;
    }

    public void setArtistDto(ArtistDto artistDto) {
        this.artistDto = artistDto;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("isrc", isrc)
                .append("trackId", trackId)
                .append("artistDto", artistDto)
                .append("fileName", fileName)
                .append("title", title)
                .toString();
    }
}
