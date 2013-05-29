package mobi.nowtechnologies.server.dto.transport;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import mobi.nowtechnologies.server.persistence.domain.ChartDetail;

@XmlRootElement(name = "lockedTrack")
public class LockedTrackDto {
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "LockedTrackDto [id=" + id + "]";
	}
	
	public static LockedTrackDto[] fromChartDetailList(List<ChartDetail> chartDetails) {
		LockedTrackDto[] dtos = new LockedTrackDto[chartDetails.size()];

		int i = 0;
		for (ChartDetail chart : chartDetails) {
			dtos[i] = fromChartDetail(chart);
			i++;
		}

		return dtos;
	}

	public static LockedTrackDto fromChartDetail(ChartDetail chartDetail) {
		LockedTrackDto dto = new LockedTrackDto();
		dto.setId((int) chartDetail.getI());

		return dto;
	}
}
