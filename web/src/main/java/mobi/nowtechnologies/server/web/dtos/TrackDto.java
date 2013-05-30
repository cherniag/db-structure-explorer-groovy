package mobi.nowtechnologies.server.web.dtos;

import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TrackDto {

    private Integer id;
    private String title;
    private String artist;
    private String cover;
    private String audio;

    public TrackDto() {}

    public TrackDto(ChartDetail detail, String url) {
        Media media = detail.getMedia();
        this.id = detail.getI();
        this.title = media.getTitle();
        this.artist = media.getArtistName();
        MediaFile imageFile = media.getImageFileSmall();
        this.cover = url +imageFile.getFilename();
        this.audio = url + media.getIsrc();
    }

    public static List<TrackDto> toList(Collection<ChartDetail> details, String url){
        List<TrackDto> result = new ArrayList<TrackDto>();
        for (ChartDetail detail: details)
            result.add(new TrackDto(detail, url));
        return result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getCover() {
        return cover;
    }

    public void setCover(String image) {
        this.cover = image;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }
}
