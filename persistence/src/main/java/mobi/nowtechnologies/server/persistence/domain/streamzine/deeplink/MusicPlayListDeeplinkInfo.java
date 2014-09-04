package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.shared.enums.ChartType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sz_deeplink_music_list")
public class MusicPlayListDeeplinkInfo extends DeeplinkInfo {

    @Column(name = "chart_detail_id")
    private Integer chartDetailId;

    protected MusicPlayListDeeplinkInfo() {
    }

    public MusicPlayListDeeplinkInfo(Integer chartDetailId) {
        this.chartDetailId = chartDetailId;
        this.contentType = ContentType.MUSIC;
    }

    public Integer getChartDetailId() {
        return chartDetailId;
    }

    @Override
    protected DeeplinkInfo provideInstance() {
        MusicPlayListDeeplinkInfo copy = new MusicPlayListDeeplinkInfo();
        copy.chartDetailId = chartDetailId;
        return copy;
    }

}
