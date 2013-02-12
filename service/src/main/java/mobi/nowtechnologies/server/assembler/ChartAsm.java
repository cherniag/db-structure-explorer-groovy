package mobi.nowtechnologies.server.assembler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.shared.dto.PlaylistDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		chartDto.setSubtitle(chart.getSubtitle());
		chartDto.setImageFileName(chart.getImageFileName());
		
		LOGGER.info("Output parameter chartDto=[{}]", chartDto);
		return chartDto;
	}
	
	public static Chart toChart(ChartDto chartDto) {
		LOGGER.debug("input parameters chart: [{}], [{}]", chartDto);
		
		Chart chart = new Chart();
		
		chart.setI(chartDto.getId());
		chart.setName(chartDto.getName());
		chart.setSubtitle(chartDto.getSubtitle());
		chart.setImageFileName(chartDto.getImageFileName());
		
		if (null != chartDto.getFile() && !chartDto.getFile().isEmpty()) {
			Integer id = chart.getI() != null ? chart.getI() : (int)(Math.random() * 100);
			String imageFileName = "CHART_" + System.currentTimeMillis() + "_" + id;
			chart.setImageFileName(imageFileName);
		}
		
		LOGGER.info("Output parameter chartDto=[{}]", chart);
		return chart;
	}
	
	public static PlaylistDto toPlaylistDto(Chart chart) {
		LOGGER.debug("input parameters chart: [{}], [{}]", chart);
		
		PlaylistDto playlistDto = new PlaylistDto();
		
		playlistDto.setId(chart.getI() != null ? chart.getI().intValue() : null);
		playlistDto.setPlaylistTitle(chart.getName());
		playlistDto.setSubtitle(chart.getSubtitle());
		playlistDto.setImage(chart.getImageFileName());
		playlistDto.setType(chart.getType());
		
		LOGGER.info("Output parameter playlistDto=[{}]", playlistDto);
		return playlistDto;
	}
}
