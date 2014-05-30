package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.Media;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "sz_deeplink_music_track")
public class MusicTrackDeeplinkInfo extends DeeplinkInfo {
    @OneToOne
    @JoinColumn(name = "media_id")
    private Media media;

    protected MusicTrackDeeplinkInfo() {
    }

    public MusicTrackDeeplinkInfo(Media media) {
        this.media = media;
        this.contentType = ContentType.MUSIC;
    }

    public Media getMedia() {
        return media;
    }

    @Override
    protected DeeplinkInfo provideInstance() {
        MusicTrackDeeplinkInfo copy = new MusicTrackDeeplinkInfo();
        copy.media = media;
        copy.contentType = contentType;
        return copy;
    }
}
