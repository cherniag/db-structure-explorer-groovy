package mobi.nowtechnologies.server.assembler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.shared.dto.PlaylistDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartDto;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class ChartAsm extends ModelMapper{
	protected final static ModelMapper modelMapper = new ChartAsm();
	private static final Logger LOGGER = LoggerFactory.getLogger(ChartAsm.class);
	
	private ChartAsm(){
		this.addMappings(new PropertyMap<ChartDetail, PlaylistDto>() {
			@Override
			protected void configure() {
				skip().setId(null);
				skip().setPlaylistTitle(null);	
				skip().setSwitchable(null);	
				map().setImage(source.getImageFileName());
			}
		});
	}

	@SuppressWarnings("unchecked")
	public static List<ChartDto> toChartDtos(List<ChartDetail> chartDetails) {
		LOGGER.debug("input parameters charts: [{}]", chartDetails);

		if (!chartDetails.isEmpty()) {
            List<ChartDto> chartDtos = new LinkedList<ChartDto>();
			for (ChartDetail chartDetail: chartDetails) {
				chartDtos.add(ChartAsm.toChartDto(chartDetail));
			}
            return chartDtos;
		}
        return Collections.EMPTY_LIST;
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
		chartDto.setDefaultChart(chartDetail.getDefaultChart());
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
		chartDetail.setDefaultChart(chartDto.getDefaultChart());
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
	
	public static PlaylistDto toPlaylistDto(ChartDetail chartDetail,final boolean switchable) {
		LOGGER.debug("input parameters chart: [{}], [{}]", chartDetail);
		
		PlaylistDto playlistDto = modelMapper.map(chartDetail, PlaylistDto.class);
		playlistDto.setId(chartDetail.getChart().getI() != null ? chartDetail.getChart().getI().intValue() : null);
		playlistDto.setPlaylistTitle(chartDetail.getTitle() != null ? chartDetail.getTitle() : chartDetail.getChart().getName());
		playlistDto.setSwitchable(switchable);
		
		LOGGER.info("Output parameter playlistDto=[{}]", playlistDto);
		return playlistDto;
	}
}
