package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.shared.dto.admin.ChartDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class ChartAsm {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChartAsm.class);

	@SuppressWarnings("unchecked")
	public static List<ChartDto> toChartDtos(List<Chart> charts) {
		LOGGER.debug("input parameters charts: [{}]", charts);

		final List<ChartDto> chartDtos;
		if (charts.isEmpty()) {
			chartDtos = Collections.EMPTY_LIST;
		} else {
			chartDtos = new LinkedList<ChartDto>();
			for (Chart chart : charts) {
				chartDtos.add(ChartAsm.toChartDto(chart));
			}
		}

		LOGGER.info("Output parameter chartDtos=[{}]", chartDtos);
		return chartDtos;
	}

	public static ChartDto toChartDto(Chart chart) {
		LOGGER.debug("input parameters chart: [{}], [{}]", chart);
		
		ChartDto chartDto = new ChartDto();
		
		chartDto.setId(chart.getI());
		chartDto.setName(chart.getName());
		
		LOGGER.info("Output parameter chartDto=[{}]", chartDto);
		return chartDto;
	}

}
