package mobi.nowtechnologies.server.web.dtos;

import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.User;

import java.util.ArrayList;
import java.util.List;
/*
   Intentionally define as immutable - w/o setters and final fields.
   If change to some field needed create some copy() methood with new value.
 */
public class PlaylistDto {

    public static final String NAME = "playlist";

    private final Integer id;
    private final String title;
    private final Integer length;
    private final String cover;
    private final boolean selected;
    private final String description;

    public PlaylistDto(User user, ChartDetail chart, String urlToChartCover) {
        this.id = chart.getChart().getI();
        this.title = chart.getSubtitle();
        this.cover = urlToChartCover + chart.getImageFileName();
        this.length = (int) chart.getChart().getNumTracks();
        this.description = chart.getChartDescription();
        this.selected = user.isSelectedChart(chart);
    }

    public static List<PlaylistDto> toList(User user, List<ChartDetail> charts, String urlToChartCover) {
        List<PlaylistDto> result = new ArrayList<PlaylistDto>();
        for (ChartDetail chart : charts) {
            PlaylistDto playlistDto = new PlaylistDto(user, chart, urlToChartCover);
            result.add(playlistDto);
        }
        return result;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "PlaylistDto [id=" + id + "]";
    }

    public String getTitle() {
        return title;
    }

    public Integer getLength() {
        return length;
    }

    public String getCover() {
        return cover;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getDescription() {
        return description;
    }

}
