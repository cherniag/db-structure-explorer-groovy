package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import static java.util.Collections.EMPTY_LIST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
public class ChartDetailsAsm {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartDetailsAsm.class);

    @SuppressWarnings("unchecked")
    public static List<ChartItemDto> toChartItemDtos(List<ChartDetail> list) {
        LOGGER.debug("input parameters chartDetails: [{}]", list);

        List<ChartItemDto> chartItemDtos;
        if (list.isEmpty()) {
            chartItemDtos = EMPTY_LIST;
        }
        else {
            chartItemDtos = new LinkedList<ChartItemDto>();

            for (ChartDetail chartDetail : list) {
                chartItemDtos.add(toChartItemDto(chartDetail));
            }
        }

        LOGGER.info("Output parameter chartItemDtos=[{}]", chartItemDtos);
        return chartItemDtos;

    }

    public static ChartItemDto toChartItemDto(ChartDetail chartDetail) {
        LOGGER.debug("input parameters chartDetail: [{}]", chartDetail);

        ChartItemDto chartItemDto = new ChartItemDto();

        chartItemDto.setId(chartDetail.getI());
        chartItemDto.setChannel(chartDetail.getChannel());
        chartItemDto.setChartId(chartDetail.getChartId());
        chartItemDto.setChgPosition(chartDetail.getChgPosition());
        chartItemDto.setInfo(chartDetail.getInfo());
        chartItemDto.setMediaDto(MediaAsm.toMediaDto(chartDetail.getMedia()));
        chartItemDto.setPosition(chartDetail.getPosition());
        chartItemDto.setPrevPosition(chartDetail.getPrevPosition());
        chartItemDto.setPublishTime(new Date(chartDetail.getPublishTimeMillis()));
        chartItemDto.setIsrc(chartDetail.getMedia().getIsrc());
        chartItemDto.setLocked(chartDetail.getLocked());

        LOGGER.info("Output parameter chartItemDto=[{}]", chartItemDto);
        return chartItemDto;
    }

    @SuppressWarnings("unchecked")
    public static List<ChartItemDto> toChartItemDtosFromMedia(Date selectedPublishDateTime, Integer chartId, List<Media> medias) {
        LOGGER.debug("input parameters medias: [{}]", medias);

        List<ChartItemDto> chartItemDtos;
        if (medias.isEmpty()) {
            chartItemDtos = EMPTY_LIST;
        }
        else {
            chartItemDtos = new LinkedList<ChartItemDto>();

            for (Media media : medias) {
                chartItemDtos.add(toChartItemDto(media, selectedPublishDateTime, chartId));
            }
        }

        LOGGER.info("Output parameter chartItemDtos=[{}]", chartItemDtos);
        return chartItemDtos;

    }

    public static ChartItemDto toChartItemDto(Media media, Date selectedPublishDateTime, Integer chartId) {
        LOGGER.debug("input parameters media: [{}]", media);

        ChartItemDto chartItemDto = new ChartItemDto();

        chartItemDto.setChannel("");
        chartItemDto.setChartId(chartId);
        chartItemDto.setChgPosition(ChgPosition.UNCHANGED);
        chartItemDto.setInfo("");
        chartItemDto.setMediaDto(MediaAsm.toMediaDto(media));
        chartItemDto.setPosition((byte) 0);
        chartItemDto.setPrevPosition((byte) 0);
        chartItemDto.setPublishTime(selectedPublishDateTime);

        LOGGER.info("Output parameter chartItemDto=[{}]", chartItemDto);
        return chartItemDto;
    }
}
