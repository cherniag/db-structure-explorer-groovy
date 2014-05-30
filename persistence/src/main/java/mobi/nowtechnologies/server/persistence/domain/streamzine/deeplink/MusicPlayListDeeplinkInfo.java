package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.shared.enums.ChartType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sz_deeplink_music_list")
public class MusicPlayListDeeplinkInfo extends DeeplinkInfo {
    @Column(name = "chart_type")
    private ChartType chartType;

    protected MusicPlayListDeeplinkInfo() {
    }

    public MusicPlayListDeeplinkInfo(ChartType chartType) {
        this.chartType = chartType;
        this.contentType = ContentType.MUSIC;
    }

    public ChartType getChartType() {
        return chartType;
    }

    @Override
    protected DeeplinkInfo provideInstance() {
        MusicPlayListDeeplinkInfo copy = new MusicPlayListDeeplinkInfo();
        copy.chartType = chartType;
        return copy;
    }
}
