package mobi.nowtechnologies.server.assembler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
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
	public static List<ChartDto> toChartDtos(List<ChartDetail> chartDetails) {
		LOGGER.debug("input parameters charts: [{}]", chartDetails);

		final List<ChartDto> chartDtos;
		if (chartDetails.isEmpty()) {
			chartDtos = Collections.EMPTY_LIST;
		} else {
			chartDtos = new LinkedList<ChartDto>();
			for (ChartDetail chartDetail: chartDetails) {
				chartDtos.add(ChartAsm.toChartDto(chartDetail));
			}
		}

		LOGGER.info("Output parameter chartDtos=[{}]", chartDtos);
		return chartDtos;
	}

	public static ChartDto toChartDto(ChartDetail chartDetail) {
		LOGGER.debug("input parameters chartDetail: [{}]", chartDetail);
		if(chartDetail.isChartItem())
			throw new IllegalArgumentException("ChartDetail is chart item(not details of chart)");
		
		ChartDto chartDto = new ChartDto();
		
		Chart chart = chartDetail.getChart();
		
		chartDto.setId(chart.getI());
		
		chartDto.setName(chartDetail.getTitle() != null ? chartDetail.getTitle() : chart.getName());
		chartDto.setChartDetailId(chartDetail.getI());
		chartDto.setPosition(chartDetail.getTitle() != null ? chartDetail.getPosition() : null);
		chartDto.setSubtitle(chartDetail.getSubtitle());
		chartDto.setImageFileName(chartDetail.getImageFileName());
		chartDto.setImageTitle(chartDetail.getImageTitle());
		chartDto.setChartType(chart.getType());
		chartDto.setDescription(chartDetail.getInfo());
		
		LOGGER.info("Output parameter chartDto=[{}]", chartDto);
		return chartDto;
	}
	
	public static ChartDetail toChart(ChartDto chartDto) {
		LOGGER.debug("input parameters chart: [{}]", chartDto);
		
		Chart chart = new Chart();
		ChartDetail chartDetail = new ChartDetail();
		chartDetail.setChart(chart);
		
		chart.setI(chartDto.getId());
		chartDetail.setI(chartDto.getChartDetailId());
		chartDetail.setTitle(chartDto.getName());
		chartDetail.setPosition(chartDto.getPosition());
		chartDetail.setSubtitle(chartDto.getSubtitle());
		chartDetail.setImageTitle(chartDto.getImageTitle());
		chartDetail.setInfo(chartDto.getDescription());
		chartDetail.setImageFileName(chartDto.getImageFileName());
		
		if (null != chartDto.getFile() && !chartDto.getFile().isEmpty()) {
			Integer id = chart.getI() != null ? chart.getI() : (int)(Math.random() * 100);
			String imageFileName = "CHART_" + System.currentTimeMillis() + "_" + id;
			chartDetail.setImageFileName(imageFileName);
		}
		
		LOGGER.info("Output parameter chartDetail=[{}]", chartDetail);
		return chartDetail;
	}
	
	public static PlaylistDto toPlaylistDto(ChartDetail chartDetail) {
		LOGGER.debug("input parameters chart: [{}], [{}]", chartDetail);
		
		PlaylistDto playlistDto = new PlaylistDto();
		Chart chart = chartDetail.getChart();
		
		playlistDto.setId(chart.getI() != null ? chart.getI().intValue() : null);
		playlistDto.setPlaylistTitle(chartDetail.getTitle() != null ? chartDetail.getTitle() : chart.getName());	
		playlistDto.setSubtitle(chartDetail.getSubtitle());
		playlistDto.setPosition(chartDetail.getPosition());
		playlistDto.setDescription(chartDetail.getInfo());
		playlistDto.setImage(chartDetail.getImageFileName());
		playlistDto.setImageTitle(chartDetail.getImageTitle());
		playlistDto.setType(chart.getType());
		
		LOGGER.info("Output parameter playlistDto=[{}]", playlistDto);
		return playlistDto;
	}
}
