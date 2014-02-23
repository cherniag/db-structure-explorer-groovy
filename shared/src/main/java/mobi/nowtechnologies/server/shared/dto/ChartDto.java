package mobi.nowtechnologies.server.shared.dto;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;

/**
 * @author Alexander Kolpakov (akolpakov)
 * @author Titov Mykhaylo (titov)
 *
 */
@XmlRootElement(name="chart")
@JsonTypeName("chart")
public class ChartDto {

    @JsonProperty("playlists")
	private PlaylistDto[] playlistDtos;

    @JsonProperty("tracks")
	private ChartDetailDto[] chartDetailDtos;

	@XmlAnyElement
	public ChartDetailDto[] getChartDetailDtos() {
		return chartDetailDtos;
	}

	public void setChartDetailDtos(ChartDetailDto[] chartDetailDtos) {
		this.chartDetailDtos = chartDetailDtos;
	}

	@XmlAnyElement
	public PlaylistDto[] getPlaylistDtos() {
		return playlistDtos;
	}

	public void setPlaylistDtos(PlaylistDto[] playlistDtos) {
		this.playlistDtos = playlistDtos;
	}

	@Override
	public String toString() {
		return "ChartDto [playlistDtos=" + Arrays.toString(playlistDtos) + ", chartDetailDtos=" + Arrays.toString(chartDetailDtos) + "]";
	}
}
