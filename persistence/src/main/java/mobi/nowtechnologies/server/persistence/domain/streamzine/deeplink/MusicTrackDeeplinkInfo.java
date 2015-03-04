package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.streamzine.PlayerType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import static javax.persistence.FetchType.EAGER;

import org.springframework.util.Assert;

@Entity
@Table(name = "sz_deeplink_music_track")
public class MusicTrackDeeplinkInfo extends DeeplinkInfo implements PlayableItemDeepLink {

    @OneToOne(fetch = EAGER)
    @JoinColumn(name = "media_id")
    private Media media;

    @Column(name = "player_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlayerType playerType = PlayerType.getDefaultPlayerType();

    protected MusicTrackDeeplinkInfo() {
    }

    public MusicTrackDeeplinkInfo(Media media, PlayerType playerType) {
        Assert.notNull(playerType);

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
