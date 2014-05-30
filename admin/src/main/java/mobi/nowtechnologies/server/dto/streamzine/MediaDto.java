package mobi.nowtechnologies.server.dto.streamzine;

import mobi.nowtechnologies.server.shared.dto.admin.ArtistDto;

public class MediaDto {
    private String isrc;
    private ArtistDto artistDto;
    private String fileName;
    private String title;

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
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
}
