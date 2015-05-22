package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.MediaRepository;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.dto.admin.MediaDto;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.lang.Validate.notNull;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
public class ChartDetailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChartDetailService.class);

    private ChartDetailRepository chartDetailRepository;

    private MediaRepository mediaRepository;

    public void setMediaRepository(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public void setChartDetailRepository(ChartDetailRepository chartDetailRepository) {
        this.chartDetailRepository = chartDetailRepository;
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRED)
    public List<ChartDetail> findChartDetailTree(Integer chartId, Date choosedPublishTime, boolean fetchLocked) {
        LOGGER.debug("input parameters user, chartId: [{}], [{}]", chartId);

        long choosedPublishTimeMillis = choosedPublishTime.getTime();

        List<ChartDetail> chartDetails;
        Long nearestLatestPublishTimeMillis = chartDetailRepository.findNearestLatestPublishDate(choosedPublishTimeMillis, chartId);
        if (nearestLatestPublishTimeMillis == null) {
            chartDetails = Collections.EMPTY_LIST;
        } else {
            if (fetchLocked) {
                chartDetails = chartDetailRepository.findChartDetailTreeForDrmUpdateByChartAndPublishTimeMillis(chartId, nearestLatestPublishTimeMillis);
            } else {
                chartDetails = chartDetailRepository.findNotLockedChartDetailTreeForDrmUpdateByChartAndPublishTimeMillis(chartId, nearestLatestPublishTimeMillis);
            }
        }

        LOGGER.debug("Output parameter chartDetails=[{}]", chartDetails);
        return chartDetails;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Media> getLockedChartItemISRCs(Integer chartId, Date selectedPublishDate) {
        LOGGER.debug("input parameters chartId, selectedPublishDate: [{}], [{}]", chartId, selectedPublishDate);

        if (selectedPublishDate == null) {
            throw new ServiceException("The parameter selectedPublishDate is null");
        }
        if (chartId == null) {
            throw new ServiceException("The parameter chartId is null");
        }

        Long nearestLatestPublishTimeMillis = chartDetailRepository.findNearestLatestPublishDate(selectedPublishDate.getTime(), chartId);

        final List<Media> chartDetails;
        if (nearestLatestPublishTimeMillis != null) {
            chartDetails = chartDetailRepository.findLockedChartItemByDate(chartId, nearestLatestPublishTimeMillis);
        } else {
            chartDetails = Collections.EMPTY_LIST;
        }

        LOGGER.info("Output parameter chartDetails=[{}]", chartDetails);
        return chartDetails;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<ChartDetail> getActualChartItems(Integer chartId, Date selectedPublishDate) {
        LOGGER.debug("input parameters chartId, selectedPublishDate: [{}], [{}]", chartId, selectedPublishDate);

        if (selectedPublishDate == null) {
            throw new ServiceException("The parameter selectedPublishDate is null");
        }
        if (chartId == null) {
            throw new ServiceException("The parameter chartId is null");
        }

        Long nearestLatestPublishTimeMillis = chartDetailRepository.findNearestLatestPublishDate(selectedPublishDate.getTime(), chartId);

        final List<ChartDetail> chartDetails;
        if (nearestLatestPublishTimeMillis != null) {
            chartDetails = chartDetailRepository.findActualChartItems(chartId, nearestLatestPublishTimeMillis);
        } else {
            chartDetails = Collections.EMPTY_LIST;
        }

        LOGGER.info("Output parameter chartDetails=[{}]", chartDetails);
        return chartDetails;
    }

    @Transactional(readOnly = true)
    public List<ChartDetail> getChartItemsByDate(Integer chartId, Date selectedPublishDate, boolean changePosition) {
        LOGGER.debug("input parameters chartId, selectedPublishDate: [{}], [{}]", chartId, selectedPublishDate);

        notNull(selectedPublishDate, "The parameter selectedPublishDate is null");
        notNull(chartId, "The parameter chartId is null");

        List<ChartDetail> chartDetails = chartDetailRepository.findChartItemsByDate(chartId, selectedPublishDate.getTime());
        if (chartDetails == null || chartDetails.size() == 0) {
            final List<ChartDetail> clonedChartDetails;

            final long choosedPublishTimeMillis = selectedPublishDate.getTime();

            Long nearestLatestPublishTimeMillis = chartDetailRepository.findNearestLatestPublishDate(choosedPublishTimeMillis, chartId);
            if (nearestLatestPublishTimeMillis != null) {
                chartDetails = chartDetailRepository.findChartItemsByDate(chartId, nearestLatestPublishTimeMillis);

                clonedChartDetails = new LinkedList<ChartDetail>();
                for (ChartDetail chartDetail : chartDetails) {
                    ChartDetail clonedChartDetail = ChartDetail.newInstance(chartDetail);

                    clonedChartDetail.setPublishTimeMillis(choosedPublishTimeMillis);

                    if (changePosition) {
                        clonedChartDetail.setPrevPosition(chartDetail.getPosition());
                        clonedChartDetail.setChgPosition(ChgPosition.UNCHANGED);
                    }

                    clonedChartDetails.add(clonedChartDetail);
                }

                return clonedChartDetails;
            } else {
                return Collections.<ChartDetail>emptyList();
            }
        }

        LOGGER.info("Output parameter chartDetails=[{}]", chartDetails);
        return chartDetails;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<ChartDetail> saveChartItems(List<ChartItemDto> chartItemList) {
        LOGGER.debug("input parameters saveChartItems(chartItemList): [{}]", chartItemList);

        List<ChartDetail> newChartItems = new LinkedList<ChartDetail>();

        for (ChartItemDto chartItemDto : chartItemList) {
            if (chartItemDto.getPosition() > 0) {
                ChartDetail chartDetail = new ChartDetail();
                Chart chart = new Chart();
                chart.setI(chartItemDto.getChartId());
                chartDetail.setChannel(StringUtils.hasText(chartItemDto.getChannel()) ?
                                       chartItemDto.getChannel().replace("&apos;", "'") :
                                       null);
                chartDetail.setChgPosition(chartItemDto.getChgPosition());
                chartDetail.setInfo(chartItemDto.getInfo().replace("&apos;", "'"));
                chartDetail.setPosition(chartItemDto.getPosition());
                chartDetail.setPrevPosition(chartItemDto.getPrevPosition());
                chartDetail.setPublishTimeMillis(chartItemDto.getPublishTime().getTime());
                chartDetail.setChart(chart);
                chartDetail.setMedia(saveMediaInfo(chartItemDto.getMediaDto()));
                chartDetail.setLocked(chartItemDto.getLocked());

                newChartItems.add(chartDetail);
            }

            if (chartItemDto.getId() != null) {
                chartDetailRepository.delete(chartItemDto.getId());
            }
        }

        if (!newChartItems.isEmpty()) {
            chartDetailRepository.flush();
            chartDetailRepository.save(newChartItems);
        }

        LOGGER.info("Output parameter saveChartItems(chartItemList) chartDetail=[{}]", newChartItems);
        return newChartItems;
    }

    private Media saveMediaInfo(MediaDto mediaDto) {
        Media media = mediaRepository.findOne(mediaDto.getId());
        media.setiTunesUrl(Utils.decodeUrl(mediaDto.getITunesUrl()));
        return mediaRepository.save(media);
    }

    @Transactional(readOnly = true)
    public List<String> getAllChannels() {

        List<String> allChannels = chartDetailRepository.findAllChannels();

        LOGGER.info("Output parameter allChannels=[{}]", allChannels);
        return allChannels;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deleteChartItems(Integer chartId, long selectedPublishDateTime) {
        LOGGER.debug("input parameters chartId, selectedPublishDateTime: [{}], [{}]", chartId, selectedPublishDateTime);

        List<ChartDetail> chartDetails = chartDetailRepository.findAllActualChartDetails(chartId, selectedPublishDateTime);

        chartDetailRepository.delete(chartDetails);
        boolean success = true;

        LOGGER.debug("Output parameter success=[{}]", success);
        return success;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int updateChartItems(Integer chartId, long selectedPublishDateTime, long newPublishDateTime) throws ServiceCheckedException {
        LOGGER.debug("input parameters chartId, selectedPublishDateTime, newPublishDateTime: [{}], [{}], [{}]", chartId, selectedPublishDateTime, newPublishDateTime);

        final long count = chartDetailRepository.countChartDetail(chartId, newPublishDateTime);
        boolean isItemsForChoosedPublishDateAlreadyExist = (count > 0);
        if (isItemsForChoosedPublishDateAlreadyExist) {
            throw new ServiceCheckedException("chartItems.changingPublishTimeOnAlreadyScheduledTime.error",
                                              "Couldn't change the chart items publishDateTime from [" + selectedPublishDateTime + "] to " + newPublishDateTime + " for chartId [" + chartId +
                                              "] because ones already exists");
        }

        int updatedRowCount = chartDetailRepository.updateChartItems(newPublishDateTime, selectedPublishDateTime, chartId);
        if (updatedRowCount <= 0) {
            throw new ServiceCheckedException("chartItems.notExisted.changingPublishTime.error",
                                              "Unexpected updated records count [" + updatedRowCount + "] for selectedPublishDateTime [" + selectedPublishDateTime + "] and chartId [" + chartId + "]");
        }

        LOGGER.info("Output parameter updatedRowCount=[{}]", updatedRowCount);
        return updatedRowCount;

    }
}
