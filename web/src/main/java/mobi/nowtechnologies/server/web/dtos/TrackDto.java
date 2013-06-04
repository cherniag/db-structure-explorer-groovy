package mobi.nowtechnologies.server.web.dtos;

import mobi.nowtechnologies.common.util.Env;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    public TrackDto(ChartDetail detail, Map<String, String> options) {
        Media media = detail.getMedia();
        this.id = detail.getI();
        this.title = media.getTitle();
        this.artist = media.getArtistName();
        MediaFile imageFile = media.getImageFileSmall();
        String urlToTracks = options.get(Env.URL_TO_TRACKS);
        this.cover = urlToTracks +imageFile.getFilename();
        this.audio = urlToTracks + media.getIsrc() + "P.m4a";
        this.channel = detail.getChannel();
    }

    public static List<TrackDto> toList(Collection<ChartDetail> details, Map options){
        List<TrackDto> result = new ArrayList<TrackDto>();
        for (ChartDetail detail: details)
            result.add(new TrackDto(detail, options ));
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
