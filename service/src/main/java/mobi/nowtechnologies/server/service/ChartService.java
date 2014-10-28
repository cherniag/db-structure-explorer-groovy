package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.assembler.ChartAsm;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.service.chart.ChartSupportResult;
import mobi.nowtechnologies.server.service.chart.GetChartContentManager;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.ChartDetailDto;
import mobi.nowtechnologies.server.shared.dto.ChartDto;
import mobi.nowtechnologies.server.shared.dto.ContentDtoResult;
import mobi.nowtechnologies.server.shared.dto.PlaylistDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.utils.ChartDetailsConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
public class ChartService implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChartService.class);

    private UserService userService;
    private ChartDetailService chartDetailService;
    private ChartRepository chartRepository;
    private DrmService drmService;
    private ChartDetailRepository chartDetailRepository;
    private CommunityResourceBundleMessageSource messageSource;
    private CloudFileService cloudFileService;
    private ChartDetailsConverter chartDetailsConverter;
    private ApplicationContext applicationContext;

    private CacheContentService cacheContentService;

    public void setCacheContentService(CacheContentService cacheContentService) {
        this.cacheContentService = cacheContentService;
    }

    public void setChartDetailsConverter(ChartDetailsConverter chartDetailsConverter) {
        this.chartDetailsConverter = chartDetailsConverter;
    }

    public void setDrmService(DrmService drmService) {
        this.drmService = drmService;
    }

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

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ContentDtoResult<ChartDto> processGetChartCommand(User user, String communityName, boolean createDrmIfNotExists, boolean fetchLocked, Long lastChartUpdateFromClient) {
        if (user == null)
            throw new ServiceException("The parameter user is null");

        user = userService.getUserWithSelectedCharts(user.getId());

        UserGroup userGroup = user.getUserGroup();
        if (userGroup == null)
            throw new ServiceException("The parameter userGroup is null");

        DrmPolicy drmPolicy = userGroup.getDrmPolicy();

        if (drmPolicy == null)
            throw new ServiceException("The parameter drmPolicy is null");

        DrmType drmType = drmPolicy.getDrmType();
        if (drmType == null)
            throw new ServiceException("The parameter drmType is null");

        if (communityName == null)
            throw new ServiceException("The parameter communityName is null");

        LOGGER.debug("input parameters user, communityName: [{}], [{}]", new Object[] { user, communityName });

        List<ChartDetail> charts = getChartsByCommunity(null, communityName, null);

        Map<ChartType, Integer> chartGroups = new HashMap<ChartType, Integer>();
        for(ChartDetail chart:charts){
            Integer count = chartGroups.get(chart.getChart().getType());
            count = count != null ? count : 0;
            chartGroups.put(chart.getChart().getType(), count+1);
        }

        List<ChartDetail> chartDetails = new ArrayList<ChartDetail>();
        List<PlaylistDto> playlistDtos = new ArrayList<PlaylistDto>();
        GetChartContentManager supporter = resolveChartSupporter(communityName);
        for (ChartDetail chart : charts) {
            ChartSupportResult result = supporter.support(user, chartGroups, chart);
            if (result.isSupport()){
                chartDetails.addAll(chartDetailService.findChartDetailTree(chart.getChart().getI(), new Date(), fetchLocked));
                playlistDtos.add(ChartAsm.toPlaylistDto(chart, result.isSwitchable()));
            }
        }
        Long lastUpdateTimeForChartDetails = findMaxPublishDate(chartDetails);
        if (lastChartUpdateFromClient != null && lastChartUpdateFromClient > 0) {
            cacheContentService.checkCacheContent(lastChartUpdateFromClient, lastUpdateTimeForChartDetails);
        }

        String defaultAmazonUrl = messageSource.getMessage(communityName, "get.chart.command.default.amazon.url", null, "get.chart.command.default.amazon.url", null);

        for (ChartDetail chartDetail : chartDetails) {
            Media media = chartDetail.getMedia();

            Drm drmForCurrentUser = drmService.findDrmByUserAndMedia(user, media, drmPolicy, createDrmIfNotExists);

            media.setDrms(Collections.singletonList(drmForCurrentUser));
        }

        List<ChartDetailDto> chartDetailDtos = chartDetailsConverter.toChartDetailDtoList(chartDetails, user.getUserGroup().getCommunity(), defaultAmazonUrl);

        ChartDto chartDto = new ChartDto();
        chartDto.setPlaylistDtos(playlistDtos.toArray(new PlaylistDto[playlistDtos.size()]));
        chartDto.setChartDetailDtos(chartDetailDtos.toArray(new ChartDetailDto[0]));

        LOGGER.debug("Output parameter chartDto=[{}]", chartDto);
        return new ContentDtoResult<ChartDto>(lastUpdateTimeForChartDetails, chartDto);
    }

    private Long findMaxPublishDate(List<ChartDetail> chartDetails) {
        Long result = -1l;
        for (ChartDetail currentDetail : chartDetails) {
            if (currentDetail.getPublishTimeMillis() > result) {
                result = currentDetail.getPublishTimeMillis();
            }
        }
        return result;
    }

    private GetChartContentManager resolveChartSupporter(String communityName) {
        String beanName = messageSource.getMessage(communityName, "getChartContentManager.beanName", null, null);

        Assert.hasText(beanName);

        return applicationContext.getBean(beanName, GetChartContentManager.class);
    }

    @Transactional(readOnly = true)
    public List<Long> getAllPublishTimeMillis(Integer chartId) {
        LOGGER.debug("input parameters chartId: [{}]", chartId);

        List<Long> allPublishTimeMillis = chartDetailService.getAllPublishTimeMillis(chartId);

        LOGGER.info("Output parameter allPublishTimeMillis=[{}]", allPublishTimeMillis);
        return allPublishTimeMillis;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<ChartDetail> getLockedChartItems(User user) {
        String communityName = user.getUserGroup().getCommunity().getName();
        LOGGER.debug("input parameters communityName: [{}]", communityName);

        if((user.isOnFreeTrial() && user.hasActivePaymentDetails()) || user.isOnBoughtPeriod() || user.isOnWhiteListedVideoAudioFreeTrial())
            return Collections.EMPTY_LIST;

        List<Chart> charts = chartRepository.getByCommunityName(communityName);

        List<ChartDetail> chartDetails = new ArrayList<ChartDetail>();
        for (Chart chart : charts) {
            List<Media> lockedItems = chartDetailService.getLockedChartItemISRCs(chart.getI(), new Date());
            for(Media lockedItem : lockedItems){
                ChartDetail chartDetail = new ChartDetail();
                chartDetail.setMedia(lockedItem);
                chartDetails.add(chartDetail);
            }
        }

        LOGGER.info("Output parameter chartDetails=[{}]", chartDetails);
        return chartDetails;
    }

    @Transactional(readOnly = true)
    public List<ChartDetail> getActualChartItems(Integer chartId, Date selectedPublishDate) {
        LOGGER.debug("input parameters chartId, selectedPublishDate: [{}], [{}]", chartId, selectedPublishDate);

        List<ChartDetail> chartDetails = chartDetailService.getActualChartItems(chartId, selectedPublishDate);

        LOGGER.info("Output parameter chartDetails=[{}]", chartDetails);
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
    public List<ChartDetail> getChartsByCommunityAndPublishTime(String communityRewriteUrl, Date publishDate){
        LOGGER.debug("input parameters communityURL [{}], publishDate [{}]", communityRewriteUrl, publishDate);
        List<Chart> charts = chartRepository.getByCommunityURL(communityRewriteUrl);
        List<ChartDetail> chartDetails = getChartDetails(charts, publishDate, false);
        LOGGER.info("Output parameter charts=[{}]", charts);
        return chartDetails;
    }

    @Transactional(readOnly = true)
    public List<ChartDetail> getChartDetails(List<Chart> charts, Date selectedPublishDateTime, boolean clone) {
        LOGGER.debug("input parameters charts: [{}]", new Object[]{charts});

        List<ChartDetail> chartDetails = new ArrayList<ChartDetail>();
        if (isNull(charts)) return chartDetails;

        for (Chart chart : charts) {
            Long lastPublishTimeMillis = isNotNull(selectedPublishDateTime)? selectedPublishDateTime.getTime() : new Date().getTime();
            ChartDetail chartDetail = chartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(chart.getI(), lastPublishTimeMillis);
            if (isNull(chartDetail)) {
                lastPublishTimeMillis = chartDetailRepository.findNearestLatestChartPublishDate(lastPublishTimeMillis, chart.getI());
                if (isNotNull(lastPublishTimeMillis)) {
                    chartDetail = chartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(chart.getI(), lastPublishTimeMillis);
                    if (clone && isNotNull(chartDetail)) {
                        chartDetail = ChartDetail.newInstance(chartDetail);
                        chartDetail.setPublishTimeMillis(lastPublishTimeMillis);
                    }
                }
            }

            if (isNull(chartDetail)) {
                chartDetail = new ChartDetail();
                chartDetail.setChart(chart);
                chart.setNumTracks((byte) 0);
            } else {
                Long numTracks = chartDetailRepository.countChartDetailTreeByChartAndPublishTimeMillis(chart.getI(), lastPublishTimeMillis);
                chartDetail.getChart().setNumTracks(numTracks.byteValue());
            }
            chartDetails.add(chartDetail);
        }

        Collections.sort(chartDetails, new Comparator<ChartDetail>() {
            @Override
            public int compare(ChartDetail o1, ChartDetail o2) {
                if (o1.getPosition() > o2.getPosition())
                    return 1;
                else if (o1.getPosition() < o2.getPosition())
                    return -1;
                else
                    return 0;
            }
        });

        LOGGER.info("Output parameter charts=[{}]", chartDetails);
        return chartDetails;
    }

    @Transactional(readOnly = true)
    public Chart getChartById(Integer chartId) {
        LOGGER.debug("input parameters chartId: [{}] [{}]", new Object[] { chartId });

        Chart chart = chartRepository.findOne(chartId);

        LOGGER.info("Output parameter chart=[{}]", chart);
        return chart;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deleteChartItems(Integer chartId, Date selectedPublishDateTime) {
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
    public ChartDetail updateChart(ChartDetail chartDetail, MultipartFile imageFile) {
        LOGGER.debug("input updateChart(Chart chart) [{}]", chartDetail);

        if(chartDetail != null){
            if(isChartDetailAlreadyPresent(chartDetail)){
                ChartDetail createdOne = chartDetailRepository.findOne(chartDetail.getI());
                chartDetail.setVersionAsPrimitive(createdOne.getVersionAsPrimitive());
            }

            chartDetail = chartDetailRepository.save(chartDetail);

            if (null != imageFile && !imageFile.isEmpty()){
                cloudFileService.uploadFile(imageFile, chartDetail.getImageFileName());
            }

        }

        LOGGER.debug("Output updateChart(Chart chart)", chartDetail);

        return chartDetail;
    }

    private boolean isChartDetailAlreadyPresent(ChartDetail chartDetail) {
        return chartDetail.getI() != null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User selectChartByType(Integer userId, Integer playlistId) {
        LOGGER.info("select chart by type input  [{}] [{}]", userId, playlistId);

        Chart chart = chartRepository.findOne(playlistId);
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
