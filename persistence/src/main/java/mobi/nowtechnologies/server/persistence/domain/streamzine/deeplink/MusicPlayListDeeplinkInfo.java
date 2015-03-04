package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.streamzine.PlayerType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "sz_deeplink_music_list")
public class MusicPlayListDeeplinkInfo extends DeeplinkInfo implements PlayableItemDeepLink {

    @Column(name = "chart_id")
    private Integer chartId;

    @Column(name = "player_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlayerType playerType = PlayerType.REGULAR_PLAYER_ONLY;

    protected MusicPlayListDeeplinkInfo() {
    }

    public MusicPlayListDeeplinkInfo(Integer chartId, PlayerType playerType) {
        this.chartId = chartId;
        this.playerType = playerType;
        this.contentType = ContentType.MUSIC;
    }

    public Integer getChartId() {
        return chartId;
    }

    @Override
    public PlayerType getPlayerType() {
        return playerType;
    }

    @Override
    protected DeeplinkInfo provideInstance() {
        MusicPlayListDeeplinkInfo copy = new MusicPlayListDeeplinkInfo();
        copy.chartId = chartId;
        copy.playerType = playerType;
        return copy;
    }
}
