package mobi.nowtechnologies.server.web.dtos;

import mobi.nowtechnologies.common.util.Env;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlaylistDto {
    public static final String NAME = "playlist";
    public static final String NAME_LIST = "playlists";

    private Integer id;
    private String title;
    private Integer length;
    private String cover;
    private boolean selected;

    public PlaylistDto() {}

    public PlaylistDto(ChartDetail chart, Map<String , String > options) {
        this.id = new Integer(chart.getChart().getI());
        this.title = chart.getTitle();
        String urlToChartCover = options.get(Env.URL_TO_CHART_COVER);
        this.cover = urlToChartCover + chart.getImageFileName();
        this.length =  new Integer(chart.getChart().getNumTracks());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PlaylistDto [id=" + id + "]";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static List<PlaylistDto> toList(List<ChartDetail> charts, Map<String, String> options) {
        List<PlaylistDto> result = new ArrayList<PlaylistDto>();
        for (ChartDetail chart : charts)
            result.add(new PlaylistDto(chart, options));
        return result;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
