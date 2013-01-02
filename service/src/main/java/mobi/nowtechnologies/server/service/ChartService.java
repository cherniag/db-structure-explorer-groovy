package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.ChartDetailDto;
import mobi.nowtechnologies.server.shared.dto.ChartDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemPositionDto;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class ChartService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChartService.class);
	
	private UserService userService;
	private ChartDetailService chartDetailService;
	private ChartRepository chartRepository;
	private MediaService mediaService;
	private CommunityResourceBundleMessageSource messageSource;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public void setChartDetailService(ChartDetailService chartDetailService) {
		this.chartDetailService = chartDetailService;
	}
	

	public void setChartRepository(ChartRepository chartRepository) {
		this.chartRepository = chartRepository;
	}
	
	public void setMediaService(MediaService mediaService) {
		this.mediaService = mediaService;
	}
	
	public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public Object[] processGetChartCommand(User user, String communityName) {
		if (user == null)
			throw new ServiceException("The parameter user is null");
		if (communityName == null)
			throw new ServiceException("The parameter communityName is null");

		LOGGER.debug("input parameters user, communityName: [{}], [{}]", new Object[]{user, communityName});
		int userId = user.getId();

		user = userService.findUserTree(userId);
		
		AccountCheckDTO accountCheck = user.toAccountCheckDTO(null); 
		
		UserGroup userGroup = user.getUserGroup();
		byte chartId = userGroup.getChartId();
		
		List<ChartDetail> chartDetails = chartDetailService.findChartDetailTreeAndUpdateDrm(user, chartId);

		String defaultAmazonUrl = messageSource.getMessage(communityName, "get.chart.command.default.amazon.url", null, "get.chart.command.default.amazon.url", null);
		
		List<ChartDetailDto> chartDetailDtos = ChartDetail.toChartDetailDtoList(chartDetails, defaultAmazonUrl);

		ChartDto chartDto = new ChartDto();
		chartDto.setChartDetailDtos(chartDetailDtos.toArray(new ChartDetailDto[0]));
		Object[] objects = new Object[]{accountCheck, chartDto};

		LOGGER.debug("Output parameter objects=[{}]", objects);
		return objects;
	}

	@Transactional(readOnly = true)
	public List<Long> getAllPublishTimeMillis(Byte chartId) {
		LOGGER.debug("input parameters chartId: [{}]", chartId);
		
		List<Long> allPublishTimeMillis = chartDetailService.getAllPublishTimeMillis(chartId);
		
		LOGGER.info("Output parameter allPublishTimeMillis=[{}]", allPublishTimeMillis);
		return allPublishTimeMillis;
	}

	@Transactional(readOnly = true)
	public List<ChartDetail> getActualChartItems(Byte chartId, Date selectedPublishDate) {
		LOGGER.debug("input parameters chartId, selectedPublishDate: [{}], [{}]", chartId, selectedPublishDate);
		
		List<ChartDetail> chartDetails = chartDetailService.getActualChartItems(chartId, selectedPublishDate);
		
		LOGGER.info("Output parameter chartDetails=[{}]", chartDetails);
		return chartDetails;
	}

	@Transactional(readOnly = true)
	public List<ChartDetail> getChartItemsByDate(Byte chartId, Date selectedPublishDate) {
		LOGGER.debug("input parameters chartId, selectedPublishDate: [{}], [{}]", chartId, selectedPublishDate);
		
		List<ChartDetail> chartDetails = chartDetailService.getChartItemsByDate(chartId, selectedPublishDate, true);
		
		LOGGER.info("Output parameter chartDetails=[{}]", chartDetails);
		return chartDetails;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public List<ChartDetail> cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(Date choosedPublishDate, Byte chartId) {
		LOGGER.debug("input parameters choosedPublishDate, chartId: [{}], [{}]", choosedPublishDate, chartId);
		
		List<ChartDetail> clonedChartDetails = chartDetailService.cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(choosedPublishDate, chartId, false);

		LOGGER.info("Output parameter clonedChartDetails=[{}]", clonedChartDetails);
		return clonedChartDetails;
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={ServiceCheckedException.class, RuntimeException.class})
	public ChartDetail saveChartItem(ChartItemDto chartItemDto) throws ServiceCheckedException {
		LOGGER.debug("input parameters chartItemDto: [{}]", chartItemDto);
		
		Chart chart = chartRepository.findOne(chartItemDto.getChartId());
		
		ChartDetail chartDetail = chartDetailService.saveChartItem(chartItemDto, chart);
		
		LOGGER.info("Output parameter chartDetail=[{}]", chartDetail);
		return chartDetail;
	}

	@Transactional(readOnly = true)
	public ChartDetail getChartItemById(Integer chartItemId) {
		LOGGER.debug("input parameters chartItemId: [{}]", chartItemId);
		
		ChartDetail chartDetail = chartDetailService.getChartItemById(chartItemId);
		
		LOGGER.info("Output parameter chartDetail=[{}]", chartDetail);
		return chartDetail;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public ChartDetail updateChartItem(ChartItemDto chartItemDto) {
		LOGGER.debug("input parameters chartItemDto: [{}]", chartItemDto);
		
		Chart chart = chartRepository.findOne(chartItemDto.getChartId());
		
		ChartDetail chartDetail = chartDetailService.updateChartItem(chartItemDto, chart);
		
		LOGGER.info("Output parameter chartDetail=[{}]", chartDetail);
		return chartDetail;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public boolean delete(Integer chartItemId) {
		LOGGER.debug("input parameters chartItemId: [{}]", chartItemId);
		
		ChartDetail chartDetail = getChartItemById(chartItemId);
		boolean success = chartDetailService.delete(chartItemId);
		updateChartItemsPositions(new Date(chartDetail.getPublishTimeMillis()), chartDetail.getChart().getI(), chartDetail.getPosition(), -1);
		
		LOGGER.info("Output parameter success=[{}]", success);
		return success;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public List<ChartDetail> updateChartItemsPositions(ChartItemPositionDto chartItemPositionDto) {
		LOGGER.debug("input parameters chartItemPositionDto: [{}]", chartItemPositionDto);
		
		List<ChartDetail> chartDetails = chartDetailService.updateChartItemsPositions(chartItemPositionDto);
		
		LOGGER.info("Output parameter chartDetails=[{}]", chartDetails);
		return chartDetails;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public List<ChartDetail> updateChartItemsPositions(Date selectedPublishDateTime, Byte chartId, int afterPosition, int chPosition) {
		LOGGER.debug("input parameters updateChartItemsPositions(selectedPublishDateTime, chartId, afterPosition, chPosition): [{}]", new Object[]{selectedPublishDateTime, chartId, afterPosition, chPosition});
		
		List<ChartDetail> chartDetails = chartDetailService.updateChartItemsPositions(selectedPublishDateTime, chartId, afterPosition, chPosition);
		
		LOGGER.info("Output parameter updateChartItemsPositions(selectedPublishDateTime, chartId, afterPosition, chPosition): chartDetails=[{}]", chartDetails);
		return chartDetails;
	}

	@Transactional(readOnly = true)
	public List<Chart> getChartsByCommunityURL(String communityURL) {
		LOGGER.debug("input parameters communityURL: [{}]", communityURL);
		
		List<Chart> charts = chartRepository.getByCommunityURL(communityURL);
		
		LOGGER.info("Output parameter charts=[{}]", charts);
		return charts;
	}

	@Transactional(readOnly = true)
	public List<String> getAllChannels() {
		
		List<String> allChannels = chartDetailService.getAllChannels();
		
		LOGGER.info("Output parameter allChannels=[{}]", allChannels);
		return allChannels;
	}

	@Transactional(readOnly = true)
	public List<Media> getMedias(String searchWords) {
		LOGGER.debug("input parameters searchWords: [{}]", searchWords);
		
		List<Media> medias = mediaService.getMedias(searchWords); 
		
		LOGGER.info("Output parameter medias=[{}]", medias);
		return medias;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public boolean deleteChartItems(Byte chartId, Date selectedPublishDateTime) {
		LOGGER.debug("input parameters chartId, selectedPublishDateTime: [{}], [{}]", chartId, selectedPublishDateTime);
		
		if (chartId == null)
			throw new NullPointerException("The parameter chartId is null");
		if (selectedPublishDateTime == null)
			throw new NullPointerException("The parameter selectedPublishDateTime is null");
		
		boolean success = chartDetailService.deleteChartItems(chartId, selectedPublishDateTime.getTime());
		
		LOGGER.debug("Output parameter success=[{}]", success);
		return success;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public List<ChartDetail> minorUpdateIfOnesDoesNotExistForSelectedPublishDate(Date selectedPublishDateTime, Byte chartId) {
		LOGGER.debug("input parameters choosedPublishDate, chartId: [{}], [{}]", selectedPublishDateTime, chartId);
		
		List<ChartDetail> clonedChartDetails = chartDetailService.cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(selectedPublishDateTime, chartId, true);

		LOGGER.info("Output parameter clonedChartDetails=[{}]", clonedChartDetails);
		return clonedChartDetails;
		
	}
}
