package mobi.nowtechnologies.server.service;

import java.util.*;

import mobi.nowtechnologies.server.assembler.ChartAsm;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.*;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemPositionDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
	private ChartDetailRepository chartDetailRepository;
	private MediaService mediaService;
	private CommunityResourceBundleMessageSource messageSource;
	private CloudFileService cloudFileService;

	public void setCloudFileService(CloudFileService cloudFileService) {
		this.cloudFileService = cloudFileService;
	}
	
	public void setChartDetailRepository(ChartDetailRepository chartDetailRepository) {
		this.chartDetailRepository = chartDetailRepository;
	}

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

	@Transactional(propagation = Propagation.REQUIRED)
	public Object[] processGetChartCommand(User user, String communityName, boolean createDrmIfNotExists, boolean fetchLocked) {
		if (user == null)
			throw new ServiceException("The parameter user is null");
		if (communityName == null)
			throw new ServiceException("The parameter communityName is null");

		LOGGER.debug("input parameters user, communityName: [{}], [{}]", new Object[] { user, communityName });
		int userId = user.getId();

		user = userService.findUserTree(userId);

		AccountCheckDTO accountCheck = user.toAccountCheckDTO(null, null);

		List<ChartDetail> charts = getChartsByCommunity(null, communityName, null);
		
		Map<ChartType, Integer> chartGroups = new HashMap<ChartType, Integer>();
		for(ChartDetail chart:charts){
			Integer count = chartGroups.get(chart.getChart().getType());
			count = count != null ? count : 0;
			chartGroups.put(chart.getChart().getType(), count+1);
		}

		List<ChartDetail> chartDetails = new ArrayList<ChartDetail>();
		List<PlaylistDto> playlistDtos = new ArrayList<PlaylistDto>();
		for (ChartDetail chart : charts) {	
			Boolean switchable = chartGroups.get(chart.getChart().getType()) > 1 ? true : false;
			if(!switchable || user.isSelectedChart(chart)){
				chartDetails.addAll(chartDetailService.findChartDetailTree(user, chart.getChart().getI(), createDrmIfNotExists, fetchLocked));
				playlistDtos.add(ChartAsm.toPlaylistDto(chart, switchable));
			}
		}

		String defaultAmazonUrl = messageSource.getMessage(communityName, "get.chart.command.default.amazon.url", null, "get.chart.command.default.amazon.url", null);

		List<ChartDetailDto> chartDetailDtos = ChartDetail.toChartDetailDtoList(chartDetails, defaultAmazonUrl);

		ChartDto chartDto = new ChartDto();
		chartDto.setPlaylistDtos(playlistDtos.toArray(new PlaylistDto[playlistDtos.size()]));
		chartDto.setChartDetailDtos(chartDetailDtos.toArray(new ChartDetailDto[0]));
		Object[] objects = new Object[] { accountCheck, chartDto };

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

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<ChartDetail> getLockedChartItems(String communityName, User user) {
		LOGGER.debug("input parameters communityName: [{}]", communityName);
		
		if(user.isPending() || user.isSubscribed() || user.isExpiring())
			return Collections.EMPTY_LIST;
		
		List<Chart> charts = chartRepository.getByCommunityName(communityName);
		
		List<ChartDetail> chartDetails = new ArrayList<ChartDetail>();
		for (Chart chart : charts) {
			List<String> chartDetailISRCs = chartDetailService.getLockedChartItemISRCs(chart.getI(), new Date());
			for(String isrc : chartDetailISRCs){
				Media media = new Media();
				media.setIsrc(isrc);
				ChartDetail chartDetail = new ChartDetail();
				chartDetail.setMedia(media);
				chartDetails.add(chartDetail);
			}
		}
		
		LOGGER.info("Output parameter chartDetails=[{}]", chartDetails);
		return chartDetails;
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

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ChartDetail> cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(Date choosedPublishDate, Byte chartId) {
		LOGGER.debug("input parameters choosedPublishDate, chartId: [{}], [{}]", choosedPublishDate, chartId);

		List<ChartDetail> clonedChartDetails = chartDetailService.cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(choosedPublishDate, chartId, false);

		LOGGER.info("Output parameter clonedChartDetails=[{}]", clonedChartDetails);
		return clonedChartDetails;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { ServiceCheckedException.class, RuntimeException.class })
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

	@Transactional(propagation = Propagation.REQUIRED)
	public ChartDetail updateChartItem(ChartItemDto chartItemDto) {
		LOGGER.debug("input parameters chartItemDto: [{}]", chartItemDto);

		Chart chart = chartRepository.findOne(chartItemDto.getChartId());

		ChartDetail chartDetail = chartDetailService.updateChartItem(chartItemDto, chart);

		LOGGER.info("Output parameter chartDetail=[{}]", chartDetail);
		return chartDetail;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean delete(Integer chartItemId) {
		LOGGER.debug("input parameters chartItemId: [{}]", chartItemId);

		ChartDetail chartDetail = getChartItemById(chartItemId);
		boolean success = chartDetailService.delete(chartItemId);
		updateChartItemsPositions(new Date(chartDetail.getPublishTimeMillis()), chartDetail.getChart().getI(), chartDetail.getPosition(), -1);

		LOGGER.info("Output parameter success=[{}]", success);
		return success;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ChartDetail> updateChartItemsPositions(ChartItemPositionDto chartItemPositionDto) {
		LOGGER.debug("input parameters chartItemPositionDto: [{}]", chartItemPositionDto);

		List<ChartDetail> chartDetails = chartDetailService.updateChartItemsPositions(chartItemPositionDto);

		LOGGER.info("Output parameter chartDetails=[{}]", chartDetails);
		return chartDetails;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ChartDetail> updateChartItemsPositions(Date selectedPublishDateTime, Byte chartId, int afterPosition, int chPosition) {
		LOGGER.debug("input parameters updateChartItemsPositions(selectedPublishDateTime, chartId, afterPosition, chPosition): [{}]", new Object[] { selectedPublishDateTime, chartId, afterPosition,
				chPosition });

		List<ChartDetail> chartDetails = chartDetailService.updateChartItemsPositions(selectedPublishDateTime, chartId, afterPosition, chPosition);

		LOGGER.info("Output parameter updateChartItemsPositions(selectedPublishDateTime, chartId, afterPosition, chPosition): chartDetails=[{}]", chartDetails);
		return chartDetails;
	}

	@Transactional(readOnly = true)
	public List<ChartDetail> getChartsByCommunity(String communityURL, String communityName, ChartType chartType) {
		LOGGER.debug("input parameters communityURL, communityName, chartType: [{}] [{}]", new Object[] { communityURL, communityName, chartType });

		List<Chart> charts = Collections.emptyList();
		if (communityURL != null)
			charts = chartType != null ? chartRepository.getByCommunityURLAndChartType(communityURL, chartType)
										: chartRepository.getByCommunityURL(communityURL); 
		else if(communityName != null)
			charts = chartType != null ? chartRepository.getByCommunityNameAndChartType(communityName, chartType)
										:chartRepository.getByCommunityName(communityName); 
		
		List<ChartDetail> chartDetails = getChartDetails(charts, new Date(), false);
		
		LOGGER.info("Output parameter charts=[{}]", charts);
		return chartDetails;
	}
	
	@Transactional(readOnly = true)
	public List<ChartDetail> getChartDetails(List<Chart> charts, Date selectedPublishDateTime, boolean clone) {
		LOGGER.debug("input parameters charts: [{}]", new Object[] { charts });
		
		List<ChartDetail> chartDetails = new ArrayList<ChartDetail>();
		
		if(charts == null)
			return chartDetails;
		
		long choosedPublishTimeMillis = selectedPublishDateTime != null ? selectedPublishDateTime.getTime() : new Date().getTime();
		
		for(Chart chart : charts){
			ChartDetail chartDetail = null;
			
			chartDetail = chartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(chart.getI(), choosedPublishTimeMillis);
			if(chartDetail == null){
				Long nearestLatestPublishTimeMillis = chartDetailRepository.findNearestLatestChartPublishDate(choosedPublishTimeMillis, chart.getI());
				if (nearestLatestPublishTimeMillis != null){
					chartDetail = chartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(chart.getI(), nearestLatestPublishTimeMillis);
					if(clone && chartDetail!=null){						
						chartDetail = ChartDetail.newInstance(chartDetail);
						chartDetail.setPublishTimeMillis(choosedPublishTimeMillis);
					}
				}	
			}
			
			if(chartDetail == null){
				chartDetail = new ChartDetail();
				chartDetail.setChart(chart);
			}
			
			chartDetails.add(chartDetail);
		}
		
		Collections.sort(chartDetails, new Comparator<ChartDetail>() {
			@Override
			public int compare(ChartDetail o1, ChartDetail o2) {
				if(o1.getPosition() > o2.getPosition())
					return 1;
				else if(o1.getPosition() < o2.getPosition())
					return -1;
				else 
					return 0;
			}
		});
		
		LOGGER.info("Output parameter charts=[{}]", chartDetails);
		return chartDetails;
	}
	
	@Transactional(readOnly = true)
	public Chart getChartById(Byte chartId) {
		LOGGER.debug("input parameters chartId: [{}] [{}]", new Object[] { chartId });

		Chart chart = chartRepository.findOne(chartId);
		
		LOGGER.info("Output parameter chart=[{}]", chart);
		return chart;
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

	@Transactional(propagation = Propagation.REQUIRED)
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

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ChartDetail> minorUpdateIfOnesDoesNotExistForSelectedPublishDate(Date selectedPublishDateTime, Byte chartId) {
		LOGGER.debug("input parameters choosedPublishDate, chartId: [{}], [{}]", selectedPublishDateTime, chartId);

		List<ChartDetail> clonedChartDetails = chartDetailService.cloneChartItemsForSelectedPublishDateIfOnesDoesNotExist(selectedPublishDateTime, chartId, true);

		LOGGER.info("Output parameter clonedChartDetails=[{}]", clonedChartDetails);
		return clonedChartDetails;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public ChartDetail updateChart(ChartDetail chartDetail, MultipartFile imageFile) {
		LOGGER.debug("input updateChart(Chart chart) [{}]", chartDetail);

		if(chartDetail != null){
			if(chartDetail.getI() != null){
				ChartDetail createdOne = chartDetailRepository.findOne(chartDetail.getI());
				chartDetail.setVersion(createdOne.getVersion());
			}
			
			chartDetail = chartDetailRepository.save(chartDetail);
			
			if (null != imageFile && !imageFile.isEmpty())
				cloudFileService.uploadFile(imageFile, chartDetail.getImageFileName());
		}
		
		LOGGER.debug("Output updateChart(Chart chart)", chartDetail);
		
		return chartDetail;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User selectChartByType(Integer userId, Integer playlistId) {
		LOGGER.info("select chart by type input  [{}] [{}]", userId, playlistId);
		
		Chart chart = chartRepository.findOne(playlistId.byteValue());
		User user = userService.getUserWithSelectedCharts(userId);
		
		if(user != null && chart != null){
			List<Chart> playlists = new ArrayList<Chart>();
			if(user.getSelectedCharts() != null){
				for (Chart playlist : user.getSelectedCharts()) {
					if(playlist.getType() != chart.getType()){
						playlists.add(playlist);
					}
				}
			}
			
			playlists.add(chart);
			user.setSelectedCharts(playlists);
			
			userService.updateUser(user);
			
			LOGGER.info("select chart by type done [{}] [{}]", chart, user);
		}
		
		return user;
	}
}
