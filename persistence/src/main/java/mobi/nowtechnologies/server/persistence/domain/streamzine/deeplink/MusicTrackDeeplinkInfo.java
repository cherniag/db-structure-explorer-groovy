package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Player;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;

import javax.persistence.*;

@Entity
@Table(name = "sz_deeplink_music_track")
public class MusicTrackDeeplinkInfo extends DeeplinkInfo implements PlayableItemDeepLink{
    @OneToOne
    @JoinColumn(name = "media_id")
    private Media media;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Player player = Player.REGULAR_PLAYER_ONLY;

    protected MusicTrackDeeplinkInfo() {
    }

    public MusicTrackDeeplinkInfo(Media media, Player player) {
        this.media = media;
        this.player = player;
        this.contentType = ContentType.MUSIC;
    }

    public Media getMedia() {
        return media;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    protected DeeplinkInfo provideInstance() {
        MusicTrackDeeplinkInfo copy = new MusicTrackDeeplinkInfo();
        copy.media = media;
        copy.player = player;
        copy.contentType = contentType;
        return copy;
    }
}
