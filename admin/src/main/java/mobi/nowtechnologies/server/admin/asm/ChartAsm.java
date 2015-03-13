package mobi.nowtechnologies.server.admin.asm;

import mobi.nowtechnologies.server.dto.ChartDto;
import mobi.nowtechnologies.server.dto.streamzine.FileNameAliasDto;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.repository.FilenameAliasRepository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 */
public class ChartAsm {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartAsm.class);

    private FilenameAliasRepository filenameAliasRepository;

    public List<ChartDto> toChartDtos(List<ChartDetail> chartDetails) {
        LOGGER.debug("input parameters charts: [{}]", chartDetails);

        if (!chartDetails.isEmpty()) {
            List<ChartDto> chartDtos = new LinkedList<ChartDto>();
            for (ChartDetail chartDetail : chartDetails) {
                chartDtos.add(toChartDto(chartDetail));
            }
            return chartDtos;
        }
        return Collections.emptyList();
    }

    public ChartDto toChartDto(ChartDetail chartDetail) {
        LOGGER.debug("input parameters chartDetail: [{}]", chartDetail);

        if (chartDetail.isChartItem()) {
            throw new IllegalArgumentException("ChartDetail is chart item(not details of chart)");
        }

        ChartDto chartDto = new ChartDto();

        Chart chart = chartDetail.getChart();

        chartDto.setId(chart.getI());
        chartDto.setName(chartDetail.getTitle() != null ?
                         chartDetail.getTitle() :
                         chart.getName());
        chartDto.setChartDetailId(chartDetail.getI());
        chartDto.setPosition(chartDetail.getTitle() != null ?
                             chartDetail.getPosition() :
                             null);
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
            Integer id = chart.getI() != null ?
                         chart.getI() :
                         (int) (Math.random() * 100);
            String imageFileName = "CHART_" + System.currentTimeMillis() + "_" + id;
            chartDetail.setImageFileName(imageFileName);
        }

        LOGGER.info("Output parameter chartDetail=[{}]", chartDetail);
        return chartDetail;
    }

    private FileNameAliasDto getBadgeFilenameAliasDto(Long badgeId) {
        if (badgeId == null) {
            return null;
        }

        FilenameAlias filenameAlias = filenameAliasRepository.findOne(badgeId);

        if (filenameAlias == null) {
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
}
