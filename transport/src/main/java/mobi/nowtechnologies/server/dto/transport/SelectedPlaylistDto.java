package mobi.nowtechnologies.server.dto.transport;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.shared.enums.ChartType;

@XmlRootElement(name = "playlist")
public class SelectedPlaylistDto {
	private ChartType type;
	private Integer id;

	public ChartType getType() {
		return type;
	}

	public void setType(ChartType type) {
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public static SelectedPlaylistDto[] fromChartList(List<Chart> charts) {
		SelectedPlaylistDto[] dtos = new SelectedPlaylistDto[charts.size()];

		int i = 0;
		for (Chart chart : charts) {
			dtos[i] = fromChart(chart);
			i++;
		}

		return dtos;
	}

	public static SelectedPlaylistDto fromChart(Chart chart) {
		SelectedPlaylistDto dto = new SelectedPlaylistDto();
		dto.setId((int) chart.getI());
		dto.setType(chart.getType());

		return dto;
	}

	@Override
	public String toString() {
		return "SelectedPlaylistDto [chartType=" + type + ", id=" + id + "]";
	}

}
