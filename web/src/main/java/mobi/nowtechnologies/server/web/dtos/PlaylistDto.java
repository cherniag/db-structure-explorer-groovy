package mobi.nowtechnologies.server.web.dtos;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDto {
    public static final String NAME = "playlist";
    public static final String NAME_LIST = "playlists";

    private Integer id;
    private String title;
    private Integer length;
    private String cover;

    public PlaylistDto() {}

    public PlaylistDto(ChartDetail chart) {
        this.id = new Integer(chart.getChart().getI());
        this.title = chart.getTitle();
        this.cover = chart.getImageFileName();
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

    public static List<PlaylistDto> toList(List<ChartDetail> charts) {
        List<PlaylistDto> result = new ArrayList<PlaylistDto>();
        for (ChartDetail chart : charts)
            result.add(new PlaylistDto(chart));
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
}
