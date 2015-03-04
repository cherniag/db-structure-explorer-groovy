package mobi.nowtechnologies.server.web.dtos;

import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/*
   Intentionally define as immutable - w/o setters and final fields.
   If change to some field needed create some copy() methood with new value.
 */
public class TrackDto {

    private final Integer id;
    private final String title;
    private final String artist;
    private final String cover;
    private final String audio;
    private final String channel;

    public TrackDto(ChartDetail detail, String urlToTracks) {
        Media media = detail.getMedia();
        this.id = media.getI();
        this.title = media.getTitle();
        this.artist = media.getArtistName();
        MediaFile imageFile = media.getImageFileSmall();
        this.cover = urlToTracks + imageFile.getFilename();
        this.audio = urlToTracks + media.getIsrc() + "P.m4a";
        this.channel = detail.getChannel();
    }

    public static List<TrackDto> toList(Collection<ChartDetail> details, String utlToTracks) {
        List<TrackDto> result = new ArrayList<TrackDto>();
        for (ChartDetail detail : details) {
            result.add(new TrackDto(detail, utlToTracks));
        }
        return result;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getCover() {
        return cover;
    }

    public String getAudio() {
        return audio;
    }

    public String getChannel() {
        return channel;
    }
}
