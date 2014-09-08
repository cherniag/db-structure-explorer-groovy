package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sz_deeplink_music_list")
public class MusicPlayListDeeplinkInfo extends DeeplinkInfo {

    @Column(name="chart_id", columnDefinition="tinyint(4)")
    private Integer chartId;

    protected MusicPlayListDeeplinkInfo() {
    }

    public MusicPlayListDeeplinkInfo(Integer chartId) {
        this.chartId = chartId;
        this.contentType = ContentType.MUSIC;
    }

    public Integer getChartId() {
        return chartId;
    }

    @Override
    protected DeeplinkInfo provideInstance() {
        MusicPlayListDeeplinkInfo copy = new MusicPlayListDeeplinkInfo();
        copy.chartId = chartId;
        return copy;
    }
}
