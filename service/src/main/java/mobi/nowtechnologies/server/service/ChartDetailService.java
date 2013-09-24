package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.assembler.ChartDetailsAsm;
import mobi.nowtechnologies.server.persistence.dao.ChartDetailDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemPositionDto;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.apache.commons.lang.Validate.notNull;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class ChartDetailService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChartDetailService.class);

	private DrmService drmService;
	private ChartDetailDao chartDetailDao;
	private EntityService entityService;
	private ChartDetailRepository chartDetailRepository;
	private MediaService mediaService;

	public void setChartDetailDao(ChartDetailDao chartDetailDao) {
		this.chartDetailDao = chartDetailDao;
	}

	public void setDrmService(DrmService drmService) {
		this.drmService = drmService;
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	public void setChartDetailRepository(ChartDetailRepository chartDetailRepository) {
		this.chartDetailRepository = chartDetailRepository;
	}

	public void setMediaService(MediaService mediaService) {
		this.mediaService = mediaService;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ChartDetail> findChartDetailTree(User user, Integer chartId, Date choosedPublishTime, boolean createDrmIfNotExists, boolean fetchLocked) {
		if (user == null)
			throw new ServiceException("The parameter user is null");

		UserGroup userGroup = user.getUserGroup();
		if (userGroup == null)
			throw new ServiceException("The parameter userGroup is null");

		DrmPolicy drmPolicy = userGroup.getDrmPolicy();

		if (drmPolicy == null)
			throw new ServiceException("The parameter drmPolicy is null");

		DrmType drmType = drmPolicy.getDrmType();
		if (drmType == null)
			throw new ServiceException("The parameter drmType is null");

		LOGGER.debug("input parameters user, chartId: [{}], [{}]", user, chartId);

		long choosedPublishTimeMillis = choosedPublishTime.getTime();

		List<ChartDetail> chartDetails;
		Long nearestLatestPublishTimeMillis = chartDetailRepository.findNearestLatestPublishDate(choosedPublishTimeMillis, chartId);
		if (nearestLatestPublishTimeMillis == null) {
			chartDetails = Collections.EMPTY_LIST;
		} else {
			if(fetchLocked)
				chartDetails = chartDetailRepository.findChartDetailTreeForDrmUpdateByChartAndPublishTimeMillis(chartId, nearestLatestPublishTimeMillis);
			else 
				chartDetails = chartDetailRepository.findNotLockedChartDetailTreeForDrmUpdateByChartAndPublishTimeMillis(chartId, nearestLatestPublishTimeMillis);

			for (ChartDetail chartDetail : chartDetails) {
				Media media = chartDetail.getMedia();

				Drm drmForCurrentUser = drmService.findDrmByUserAndMedia(user, media, drmPolicy, createDrmIfNotExists);

				media.setDrms(Collections.singletonList(drmForCurrentUser));
			}
		}

		LOGGER.debug("Output parameter chartDetails=[{}]", chartDetails);
		return chartDetails;
	}



	public boolean isTrackCanBeBoughtAccordingToLicense(String isrc) {
		if (isrc == null)
			throw new ServiceException("The parameter isrc is null");
		Object[] argArray = new Object[] { isrc };
		LOGGER.debug("input parameters isrc: [{}]", argArray);

		boolean isTrackCanBeBoughtAccordingToLicense = chartDetailDao.isTrackCanBeBoughtAccordingToLicense(isrc);

		LOGGER.debug("Output parameter isTrackCanBeBoughtAccordingToLicense=[{}]", isTrackCanBeBoughtAccordingToLicense);
		return isTrackCanBeBoughtAccordingToLicense;
	}

	@Transactional(readOnly = true)
	public List<Long> getAllPublishTimeMillis(Integer chartId) {
		LOGGER.debug("input parameters chartId: [{}]", chartId);

		if (chartId == null)
			throw new ServiceException("The parameter chartId is null");

		List<Long> allPublishTimeMillis = chartDetailRepository.getAllPublishTimeMillis(chartId);

		LOGGER.info("Output parameter allPublishTimeMillis=[{}]", allPublishTimeMillis);
		return allPublishTimeMillis;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<String> getLockedChartItemISRCs(Integer chartId, Date selectedPublishDate) {
		LOGGER.debug("input parameters chartId, selectedPublishDate: [{}], [{}]", chartId, selectedPublishDate);
		
		if (selectedPublishDate == null)
			throw new ServiceException("The parameter selectedPublishDate is null");
		if (chartId == null)
			throw new ServiceException("The parameter chartId is null");
		
		Long nearestLatestPublishTimeMillis = chartDetailRepository.findNearestLatestPublishDate(selectedPublishDate.getTime(), chartId);
		
		final List<String> chartDetails;
		if (nearestLatestPublishTimeMillis != null)
			chartDetails = chartDetailRepository.getLockedChartItemISRCByDate(chartId, nearestLatestPublishTimeMillis);
		else
			chartDetails = Collections.EMPTY_LIST;
		
		LOGGER.info("Output parameter chartDetails=[{}]", chartDetails);
		return chartDetails;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<ChartDetail> getActualChartItems(Integer chartId, Date selectedPublishDate) {
		LOGGER.debug("input parameters chartId, selectedPublishDate: [{}], [{}]", chartId, selectedPublishDate);

		if (selectedPublishDate == null)
			throw new ServiceException("The parameter selectedPublishDate is null");
		if (chartId == null)
			throw new ServiceException("The parameter chartId is null");

		Long nearestLatestPublishTimeMillis = chartDetailRepository.findNearestLatestPublishDate(selectedPublishDate.getTime(), chartId);

		final List<ChartDetail> chartDetails;
		if (nearestLatestPublishTimeMillis != null)
			chartDetails = chartDetailRepository.getActualChartItems(chartId, nearestLatestPublishTimeMillis);
		else
			chartDetails = Collections.EMPTY_LIST;

		LOGGER.info("Output parameter chartDetails=[{}]", chartDetails);
		return chartDetails;
	}

	@Transactional(readOnly = true)
	public List<ChartDetail> getChartItemsByDate(Integer chartId, Date selectedPublishDate, boolean changePosition) {
		LOGGER.debug("input parameters chartId, selectedPublishDate: [{}], [{}]", chartId, selectedPublishDate);

		notNull(selectedPublishDate , "The parameter selectedPublishDate is null");
		notNull(chartId , "The parameter chartId is null");

		List<ChartDetail> chartDetails = chartDetailRepository.getChartItemsByDate(chartId, selectedPublishDate.getTime());
		if (chartDetails == null || chartDetails.size() == 0) {
			final List<ChartDetail> clonedChartDetails;

			final long choosedPublishTimeMillis = selectedPublishDate.getTime();

			Long nearestLatestPublishTimeMillis = chartDetailRepository.findNearestLatestPublishDate(choosedPublishTimeMillis, chartId);
			if (nearestLatestPublishTimeMillis != null) {
				chartDetails = chartDetailRepository.getChartItemsByDate(chartId, nearestLatestPublishTimeMillis);

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
			} else
				return Collections.<ChartDetail> emptyList();
		}

		LOGGER.info("Output parameter chartDetails=[{}]", chartDetails);
		return chartDetails;
	}

	@Transactional(readOnly = true)
	public ChartDetail getChartItemById(Integer chartItemId) {
		LOGGER.info("input parameters chartItemId: [{}]", chartItemId);

		if (chartItemId == null)
			throw new ServiceException("The parameter chartItemId is null");

		ChartDetail chartDetail = chartDetailRepository.findById(chartItemId);

		LOGGER.info("Output parameter chartDetail=[{}]", chartDetail);
		return chartDetail;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean delete(Integer chartItemId) {
		LOGGER.debug("input parameters chartItemId: [{}]", chartItemId);

		if (chartItemId == null)
			throw new ServiceException("The parameter chartItemId is null");

		chartDetailRepository.delete(chartItemId);
		boolean success = true;

		LOGGER.info("Output parameter success=[{}]", success);
		return success;

	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ChartDetail> cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(Date choosedPublishDate, Integer chartId, boolean minorUpdate) {
		LOGGER.debug("input parameters choosedPublishDate, chartId, minorUpdate: [{}], [{}], [{}]", new Object[] { choosedPublishDate, chartId, minorUpdate });

		if (chartId == null)
			throw new ServiceException("The parameter chartId is null");
		if (choosedPublishDate == null)
			throw new ServiceException("The parameter choosedPublishDate is null");

		final List<ChartDetail> clonedChartDetails;

		final long choosedPublishTimeMillis = choosedPublishDate.getTime();
		final long count = chartDetailRepository.getCount(chartId, choosedPublishTimeMillis);
		boolean isNewsForChoosedPublishDateAlreadyExist = (count > 0);
		if (!isNewsForChoosedPublishDateAlreadyExist) {

			Long nearestLatestPublishTimeMillis = chartDetailRepository.findNearestLatestPublishDate(choosedPublishTimeMillis, chartId);
			if (nearestLatestPublishTimeMillis != null) {
				List<ChartDetail> chartDetails = chartDetailRepository.findByChartAndPublishTimeMillis(chartId, nearestLatestPublishTimeMillis);

				clonedChartDetails = new LinkedList<ChartDetail>();
				for (ChartDetail chartDetail : chartDetails) {
					ChartDetail clonedChartDetail = ChartDetail.newInstance(chartDetail);

					clonedChartDetail.setPublishTimeMillis(choosedPublishTimeMillis);

					if (!minorUpdate) {
						clonedChartDetail.setPrevPosition(chartDetail.getPosition());
						clonedChartDetail.setChgPosition(ChgPosition.UNCHANGED);
					}

					clonedChartDetails.add(clonedChartDetail);
				}
				chartDetailRepository.save(clonedChartDetails);

			} else
				clonedChartDetails = Collections.EMPTY_LIST;
		} else
			clonedChartDetails = Collections.EMPTY_LIST;

		LOGGER.info("Output parameter clonedChartDetails=[{}]", clonedChartDetails);
		return clonedChartDetails;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { ServiceCheckedException.class, RuntimeException.class })
	public ChartDetail saveChartItem(ChartItemDto chartItemDto, Chart chart) throws ServiceCheckedException {
		LOGGER.debug("input parameters chartItemDto, chart: [{}], [{}]", chartItemDto, chart);

		if (chartItemDto == null)
			throw new ServiceException("The parameter chartItemDto is null");
		if (chart == null)
			throw new ServiceException("The parameter chart is null");

		ChartDetail chartDetail = new ChartDetail();
		chartDetail = ChartDetailsAsm.fromChartItemDto(chartItemDto, chartDetail);

		final Date publishTime = chartItemDto.getPublishTime();
		final long publishTimeMillis = publishTime.getTime();

		Byte position = chartDetailRepository.findMaxPosition(chart, publishTimeMillis);
		if (position != null) {
			position++;
		} else
			position = 1;

		final byte prevPosition = (byte) 0;
		boolean isBonus = isBonus(chartItemDto.getChannel());

		final ChgPosition chgPosition = getChgPosition(position, prevPosition, isBonus);

		Media media = mediaService.findById(chartItemDto.getMediaDto().getId());

		chartDetail.setChart(chart);
		chartDetail.setPosition(position);
		chartDetail.setChgPosition(chgPosition);
		chartDetail.setMedia(media);
		chartDetail.setPrevPosition(prevPosition);

		try {
			chartDetail = chartDetailRepository.save(chartDetail);
		} catch (DataIntegrityViolationException e) {
			LOGGER.warn(e.getMessage(), e);
			throw new ServiceCheckedException("chartItems.constraints.violation.error", "Couldn't save chart item via constraint violation", e);
		}

		LOGGER.info("Output parameter chartDetail=[{}]", chartDetail);
		return chartDetail;
	}

	public boolean isBonus(String channel) {
		LOGGER.debug("input parameters channel: [{}]", channel);
		boolean isBonus = (channel != null);
		LOGGER.info("Output parameter isBonus=[{}]", isBonus);
		return isBonus;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public ChartDetail updateChartItem(ChartItemDto chartItemDto, Chart chart) {
		LOGGER.debug("input parameters chartItemDto, chart: [{}], [{}]", chartItemDto, chart);

		if (chartItemDto == null)
			throw new ServiceException("The parameter chartItemDto is null");
		if (chart == null)
			throw new ServiceException("The parameter chart is null");

		ChartDetail chartDetail = chartDetailRepository.findOne(chartItemDto.getId());

		chartDetail = ChartDetailsAsm.fromChartItemDto(chartItemDto, chartDetail);

		boolean isBonus = isBonus(chartDetail.getChannel());
		final ChgPosition chgPosition = getChgPosition(chartDetail.getPosition(), chartDetail.getPrevPosition(), isBonus);

		chartDetail.setChgPosition(chgPosition);

		chartDetail = chartDetailRepository.save(chartDetail);

		LOGGER.info("Output parameter chartDetail=[{}]", chartDetail);
		return chartDetail;
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
				Media media = new Media();
				media.setI(chartItemDto.getMediaDto().getId());

				chartDetail.setChannel(StringUtils.hasText(chartItemDto.getChannel()) ? chartItemDto.getChannel().replace("&apos;", "'") : null);
				chartDetail.setChgPosition(chartItemDto.getChgPosition());
				chartDetail.setInfo(chartItemDto.getInfo().replace("&apos;", "'"));
				chartDetail.setPosition(chartItemDto.getPosition());
				chartDetail.setPrevPosition(chartItemDto.getPrevPosition());
				chartDetail.setPublishTimeMillis(chartItemDto.getPublishTime().getTime());
				chartDetail.setChart(chart);
				chartDetail.setMedia(media);
				chartDetail.setLocked(chartItemDto.getLocked());

				newChartItems.add(chartDetail);
			}

			if (chartItemDto.getId() != null)
				chartDetailRepository.delete(chartItemDto.getId());
		}

		if (!newChartItems.isEmpty()) {
			chartDetailRepository.flush();
			chartDetailRepository.save(newChartItems);
		}

		LOGGER.info("Output parameter saveChartItems(chartItemList) chartDetail=[{}]", newChartItems);
		return newChartItems;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ChartDetail> updateChartItemsPositions(Date selectedPublishDateTime, Integer chartId, int afterPosition, int chPosition) {
		LOGGER.debug("input parameters selectedPublishDateTime, chartId, afterPosition, chPosition: [{}], [{}], [{}], [{}]", new Object[] { selectedPublishDateTime, chartId,
				afterPosition, chPosition });
		List<Integer> ids = chartDetailRepository.getIdsByDateAndPosition(chartId, selectedPublishDateTime.getTime(), (byte) afterPosition);

		ChartItemPositionDto positionDto = new ChartItemPositionDto();
		Map<Integer, Byte> positionMap = positionDto.getPositionMap();
		int position = afterPosition + 1;
		for (Integer id : ids) {
			positionMap.put(id, (byte) (position + chPosition));
			position++;
		}

		final List<ChartDetail> chartDetails = updateChartItemsPositions(positionDto);
		LOGGER.info("Output parameter chartDetails=[{}]", chartDetails);
		return chartDetails;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ChartDetail> updateChartItemsPositions(ChartItemPositionDto chartItemPositionDto) {
		LOGGER.debug("input parameters chartItemPositionDto: [{}]", chartItemPositionDto);

		if (chartItemPositionDto == null)
			throw new ServiceException("The parameter chartItemPositionDto is null");

		Map<Integer, Byte> idPositionMap = chartItemPositionDto.getPositionMap();

		final List<ChartDetail> chartDetails;

		if (idPositionMap.isEmpty()) {
			chartDetails = Collections.EMPTY_LIST;
		} else {
			chartDetails = chartDetailRepository.getByIds(idPositionMap.keySet());
			for (ChartDetail chartDetail : chartDetails) {
				Byte position = idPositionMap.get(chartDetail.getI());
				chartDetail.setPosition(position);
				boolean isBonus = isBonus(chartDetail.getChannel());
				ChgPosition chgPosition = getChgPosition(position, chartDetail.getPrevPosition(), isBonus);
				chartDetail.setChgPosition(chgPosition);
			}
			chartDetailRepository.save(chartDetails);
		}

		LOGGER.info("Output parameter chartDetails=[{}]", chartDetails);
		return chartDetails;
	}

	public ChgPosition getChgPosition(byte position, byte prevPosition, boolean isBonus) {
		LOGGER.debug("input parameters position, prevPosition: [{}], [{}]", position, prevPosition);

		if (position < 0)
			throw new IllegalArgumentException("The parameter position [" + position + "] is less than 0");
		if (prevPosition < 0)
			throw new IllegalArgumentException("The parameter position [" + prevPosition + "] is less than 0");

		final ChgPosition chgPosition;
		if (!isBonus) {
			if (prevPosition != 0) {
				int dif = position - prevPosition;
				if (dif > 0)
					chgPosition = ChgPosition.DOWN;
				else if (dif < 0)
					chgPosition = ChgPosition.UP;
				else
					chgPosition = ChgPosition.UNCHANGED;
			} else
				chgPosition = ChgPosition.UNCHANGED;
		} else {
			chgPosition = ChgPosition.NONE;
		}

		LOGGER.info("Output parameter chgPosition=[{}]", chgPosition);
		return chgPosition;
	}

	@Transactional(readOnly = true)
	public List<String> getAllChannels() {

		List<String> allChannels = chartDetailRepository.getAllChannels();

		LOGGER.info("Output parameter allChannels=[{}]", allChannels);
		return allChannels;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean deleteChartItems(Integer chartId, long selectedPublishDateTime) {
		LOGGER.debug("input parameters chartId, selectedPublishDateTime: [{}], [{}]", chartId, selectedPublishDateTime);

		List<ChartDetail> chartDetails = chartDetailRepository.getAllActualChartDetails(chartId, selectedPublishDateTime);

		chartDetailRepository.delete(chartDetails);
		boolean success = true;

		LOGGER.debug("Output parameter success=[{}]", success);
		return success;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int updateChartItems(Integer chartId, long selectedPublishDateTime, long newPublishDateTime) throws ServiceCheckedException{
		LOGGER.debug("input parameters chartId, selectedPublishDateTime, newPublishDateTime: [{}], [{}]", new Object[] { chartId, selectedPublishDateTime, newPublishDateTime });

		final long count = chartDetailRepository.getCount(chartId, newPublishDateTime);
		boolean isItemsForChoosedPublishDateAlreadyExist = (count > 0);
		if (isItemsForChoosedPublishDateAlreadyExist)
			throw new ServiceCheckedException("chartItems.changingPublishTimeOnAlreadyScheduledTime.error", "Coudn't change the chart items publishDateTime from [" + selectedPublishDateTime + "] to " + newPublishDateTime + " for chartId [" + chartId
					+ "] because ones already exists");

		int updatedRowCount = chartDetailRepository.updateChartItems(newPublishDateTime, selectedPublishDateTime, chartId);
		if (updatedRowCount <= 0)
			throw new ServiceCheckedException("chartItems.notExisted.changingPublishTime.error","Unexpected updated records count [" + updatedRowCount + "] for selectedPublishDateTime [" + selectedPublishDateTime + "] and chartId [" + chartId + "]");

		LOGGER.info("Output parameter updatedRowCount=[{}]", updatedRowCount);
		return updatedRowCount;

	}
	
	@Transactional(readOnly = true)
	public Long findNearestLatestPublishTimeMillis(Community community, long choosedPublishTimeMillis){
		LOGGER.debug("input parameters community, choosedPublishTimeMillis: [{}], [{}]", community, choosedPublishTimeMillis);
		
		Long nearestLatestPublishTimeMillis = chartDetailRepository.findNearestLatestPublishDate(choosedPublishTimeMillis, community);
		
		LOGGER.debug("Output parameter nearestLatestPublishTimeMillis=[{}]", nearestLatestPublishTimeMillis);
		return nearestLatestPublishTimeMillis;
	}
}
