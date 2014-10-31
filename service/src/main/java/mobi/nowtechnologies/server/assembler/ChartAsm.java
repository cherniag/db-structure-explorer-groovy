package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.dto.streamzine.FileNameAliasDto;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import mobi.nowtechnologies.server.persistence.repository.FilenameAliasRepository;
import mobi.nowtechnologies.server.service.streamzine.BadgesService;
import mobi.nowtechnologies.server.shared.dto.PlaylistDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class ChartAsm extends ModelMapper{
	private static final Logger LOGGER = LoggerFactory.getLogger(ChartAsm.class);

    private FilenameAliasRepository filenameAliasRepository;
    private BadgesService badgesService;

	
	public ChartAsm(){
		this.addMappings(new PropertyMap<ChartDetail, PlaylistDto>() {
            @Override
            protected void configure() {
                skip().setId(null);
                skip().setPlaylistTitle(null);
                skip().setSwitchable(null);
                skip().setBadgeIcon(null);
                map().setImage(source.getImageFileName());
            }
        });
	}

	@SuppressWarnings("unchecked")
	public List<ChartDto> toChartDtos(List<ChartDetail> chartDetails) {
		LOGGER.debug("input parameters charts: [{}]", chartDetails);

		if (!chartDetails.isEmpty()) {
            List<ChartDto> chartDtos = new LinkedList<ChartDto>();
			for (ChartDetail chartDetail: chartDetails) {
				chartDtos.add(toChartDto(chartDetail));
			}
            return chartDtos;
		}
        return Collections.EMPTY_LIST;
	}

	public ChartDto toChartDto(ChartDetail chartDetail) {
		LOGGER.debug("input parameters chartDetail: [{}]", chartDetail);

		if(chartDetail.isChartItem()) {
            throw new IllegalArgumentException("ChartDetail is chart item(not details of chart)");
        }
		
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
        chartDto.setFileNameAlias(getBadgeFilenameAliasDto(chartDetail.getBadgeId()));
		
		LOGGER.info("Output parameter chartDto=[{}]", chartDto);
		return chartDto;
	}
	
	public ChartDetail toChart(ChartDto chartDto) {
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
        chartDetail.setBadgeId(chartDto.getBadgeId());

		if (null != chartDto.getFile() && !chartDto.getFile().isEmpty()) {
			Integer id = chart.getI() != null ? chart.getI() : (int)(Math.random() * 100);
			String imageFileName = "CHART_" + System.currentTimeMillis() + "_" + id;
			chartDetail.setImageFileName(imageFileName);
		}
		
		LOGGER.info("Output parameter chartDetail=[{}]", chartDetail);
		return chartDetail;
	}
	
	public PlaylistDto toPlaylistDto(ChartDetail chartDetail, Resolution resolution, Community community, final boolean switchable) {
		LOGGER.debug("input parameters chart: [{}], switchable: [{}]", chartDetail, switchable);
		
		PlaylistDto playlistDto = map(chartDetail, PlaylistDto.class);
		playlistDto.setId(chartDetail.getChart().getI() != null ? chartDetail.getChart().getI() : null);
		playlistDto.setPlaylistTitle(chartDetail.getTitle() != null ? chartDetail.getTitle() : chartDetail.getChart().getName());
		playlistDto.setSwitchable(switchable);

		if(chartDetail.getBadgeId() != null && resolution != null){
            String badgeFileName = badgesService.getBadgeFileName(chartDetail.getBadgeId(), community, resolution);
            playlistDto.setBadgeIcon(badgeFileName);
        }

		LOGGER.info("Output parameter playlistDto=[{}]", playlistDto);
		return playlistDto;
	}

    private FileNameAliasDto getBadgeFilenameAliasDto(Long badgeId) {
        if(badgeId == null) {
            return null;
        }

        FilenameAlias filenameAlias = filenameAliasRepository.findOne(badgeId);

        if(filenameAlias == null) {
            return null;
        } else {
            FileNameAliasDto dto = new FileNameAliasDto();
            dto.setId(filenameAlias.getId());
            dto.setFileName(filenameAlias.getFileName());
            dto.setAlias(filenameAlias.getAlias());
            return dto;
        }
    }


    public void setFilenameAliasRepository(FilenameAliasRepository filenameAliasRepository) {
        this.filenameAliasRepository = filenameAliasRepository;
    }

    public void setBadgesService(BadgesService badgesService) {
        this.badgesService = badgesService;
    }
}
