package mobi.nowtechnologies.server.web.dtos;

import static java.lang.String.valueOf;
import static mobi.nowtechnologies.server.shared.Utils.toByteIfNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mobi.nowtechnologies.common.util.Env;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.User;
/*
   Intentionally define as immutable - w/o setters and final fields.
   If change to some field needed create some copy() methood with new value.
 */
public class PlaylistDto {
	public static final String NAME = "playlist";
	public static final String NAME_LIST = "playlists";

	private final Integer id;
	private final String title;
	private final Integer length;
	private final String cover;
	private final boolean selected;
    private final String description;

    public PlaylistDto(User user, ChartDetail chart, Map<String, Object> options) {
        Byte chartId = chart.getChart().getI();
        this.id = new Integer(chartId);
		this.title = chart.getSubtitle();
		String urlToChartCover = valueOf(options.get(Env.URL_TO_CHART_COVER));
		this.cover = urlToChartCover + chart.getImageFileName();
		this.length = new Integer(chart.getChart().getNumTracks());
        this.description = chart.getChartDescription();
        this.selected = user.isSelectedChart(chart);
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

	public static List<PlaylistDto> toList(User user, List<ChartDetail> charts, Map<String, Object> options) {
		List<PlaylistDto> result = new ArrayList<PlaylistDto>();
		for (ChartDetail chart : charts){
			PlaylistDto playlistDto = new PlaylistDto(user, chart, options);
			result.add(playlistDto);
		}
		return result;
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
