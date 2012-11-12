package mobi.nowtechnologies.server.shared.dto;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@XmlRootElement(name="chart")
public class ChartDto {
	
	private ChartDetailDto[] chartDetailDtos;

	@XmlAnyElement
	public ChartDetailDto[] getChartDetailDtos() {
		return chartDetailDtos;
	}

	public void setChartDetailDtos(ChartDetailDto[] chartDetailDtos) {
		this.chartDetailDtos = chartDetailDtos;
	}

	@Override
	public String toString() {
		return "ChartDto [chartDetailDtos=" + Arrays.toString(chartDetailDtos) + "]";
	}

}
