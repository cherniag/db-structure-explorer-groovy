package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.streamzine.PlayerType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;

import javax.persistence.*;

import static javax.persistence.FetchType.EAGER;

@Entity
@Table(name = "sz_deeplink_music_track")
public class MusicTrackDeeplinkInfo extends DeeplinkInfo implements PlayableItemDeepLink{

    @OneToOne(fetch = EAGER)
    @JoinColumn(name = "media_id")
    private Media media;

    @Column(name = "player_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlayerType playerType = PlayerType.REGULAR_PLAYER_ONLY;

    protected MusicTrackDeeplinkInfo() {
    }

    public MusicTrackDeeplinkInfo(Media media, PlayerType playerType) {
        this.media = media;
        this.playerType = playerType;
        this.contentType = ContentType.MUSIC;
    }

    public Media getMedia() {
        return media;
    }

    @Override
    public PlayerType getPlayerType() {
        return playerType;
    }

    @Override
    protected DeeplinkInfo provideInstance() {
        MusicTrackDeeplinkInfo copy = new MusicTrackDeeplinkInfo();
        copy.media = media;
        copy.playerType = playerType;
        copy.contentType = contentType;
        return copy;
    }
}
