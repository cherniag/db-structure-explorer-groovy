package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.streamzine.Player;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;

import javax.persistence.*;

@Entity
@Table(name = "sz_deeplink_music_list")
public class MusicPlayListDeeplinkInfo extends DeeplinkInfo implements PlayableItemDeepLink{

    @Column(name="chart_id", columnDefinition="tinyint(4)")
    private Integer chartId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Player player = Player.REGULAR_PLAYER_ONLY;

    protected MusicPlayListDeeplinkInfo() {
    }

    public MusicPlayListDeeplinkInfo(Integer chartId, Player player) {
        this.chartId = chartId;
        this.player = player;
        this.contentType = ContentType.MUSIC;
    }

    public Integer getChartId() {
        return chartId;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    protected DeeplinkInfo provideInstance() {
        MusicPlayListDeeplinkInfo copy = new MusicPlayListDeeplinkInfo();
        copy.chartId = chartId;
        copy.player = player;
        return copy;
    }
}
