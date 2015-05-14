package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.service.chart.ChartDetailsConverter;
import mobi.nowtechnologies.server.service.chart.ChartSupportResult;
import mobi.nowtechnologies.server.service.chart.GetChartContentManager;
import mobi.nowtechnologies.server.shared.dto.ChartDetailDto;
import mobi.nowtechnologies.server.shared.dto.ChartDto;
import mobi.nowtechnologies.server.shared.dto.PlaylistDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Collections.emptyList;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.joda.time.DateTimeZone.UTC;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
public class ChartService implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartService.class);

    private UserService userService;
    private ChartDetailService chartDetailService;
    private ChartRepository chartRepository;
    private ChartDetailRepository chartDetailRepository;
    private CommunityResourceBundleMessageSource messageSource;
    private CloudFileService cloudFileService;
    private ChartDetailsConverter chartDetailsConverter;
    private ApplicationContext applicationContext;

    @Transactional(propagation = Propagation.REQUIRED)
    public ChartDto processGetChartCommand(User user, boolean createDrmIfNotExists, boolean fetchLocked, Resolution resolution, boolean isPlayListLockSupported, boolean withChartUpdateId) {
        LOGGER.debug("input parameters user=[{}], createDrmIfNotExists=[{}], fetchLocked=[{}], resolution=[{}], isPlayListLockSupported=[{}]", user, createDrmIfNotExists, fetchLocked, resolution,
                     isPlayListLockSupported);

        user = userService.getUserWithSelectedCharts(user.getId());
        Community community = user.getUserGroup().getCommunity();
        String rewriteUrlParameter = community.getRewriteUrlParameter();

        List<ChartDetail> charts = getChartsByCommunity(null, rewriteUrlParameter, null);

        Map<ChartType, Integer> chartGroups = new HashMap<ChartType, Integer>();
        for (ChartDetail chart : charts) {
            Integer count = chartGroups.get(chart.getChart().getType());
            count = count != null ?
                    count :
                    0;
            chartGroups.put(chart.getChart().getType(), count + 1);
        }

        List<ChartDetail> chartDetails = new ArrayList<ChartDetail>();
        List<PlaylistDto> playlistDtos = new ArrayList<PlaylistDto>();
        GetChartContentManager supporter = resolveChartSupporter(rewriteUrlParameter);
        for (ChartDetail chartUpdateMarker : charts) {
            ChartSupportResult result = supporter.support(user, chartGroups, chartUpdateMarker);
            if (result.isSupport()) {
                List<ChartDetail> chartDetailTree = chartDetailService.findChartDetailTree(chartUpdateMarker.getChart().getI(), new Date(), fetchLocked);
                chartDetails.addAll(chartDetailTree);

                PlaylistDto playlistDto = chartDetailsConverter.toPlaylistDto(chartUpdateMarker, resolution, community, result.isSwitchable(), withChartUpdateId);

                playlistDtos.add(playlistDto);
            }
        }

        List<ChartDetailDto> chartDetailDtos = chartDetailsConverter.toChartDetailDtoList(chartDetails, community);

        ChartDto chartDto = new ChartDto();
        chartDto.setPlaylistDtos(playlistDtos.toArray(new PlaylistDto[playlistDtos.size()]));
        chartDto.setChartDetailDtos(chartDetailDtos.toArray(new ChartDetailDto[0]));

        LOGGER.debug("Output parameter chartDto=[{}]", chartDto);
        return chartDto;
    }

    private GetChartContentManager resolveChartSupporter(String communityName) {
        String beanName = messageSource.getMessage(communityName, "getChartContentManager.beanName", null, null);

        Assert.hasText(beanName);

        return applicationContext.getBean(beanName, GetChartContentManager.class);
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<ChartDetail> getLockedChartItems(User user) {
        String communityName = user.getUserGroup().getCommunity().getName();
        LOGGER.debug("input parameters communityName: [{}]", communityName);

        if ((user.isOnFreeTrial() && user.hasActivePaymentDetails()) || user.isOnBoughtPeriod() || user.isOnWhiteListedVideoAudioFreeTrial()) {
            return Collections.EMPTY_LIST;
        }

        List<Chart> charts = chartRepository.findByCommunityName(communityName);

        List<ChartDetail> chartDetails = new ArrayList<ChartDetail>();
        for (Chart chart : charts) {
            List<Media> lockedItems = chartDetailService.getLockedChartItemISRCs(chart.getI(), new Date());
            for (Media lockedItem : lockedItems) {
                ChartDetail chartDetail = new ChartDetail();
                chartDetail.setMedia(lockedItem);
                chartDetails.add(chartDetail);
            }
        }

        LOGGER.info("Output parameter chartDetails=[{}]", chartDetails);
        return chartDetails;
    }

    @Transactional(readOnly = true)
    public List<ChartDetail> getChartsByCommunity(String communityURL, String communityName, ChartType chartType) {
        LOGGER.debug("input parameters communityURL=[{}], communityName=[{}], chartType=[{}]", communityURL, communityName, chartType);

        List<Chart> charts = emptyList();
        if (communityURL != null) {
            charts = chartType != null ?
                     chartRepository.findByCommunityURLAndChartType(communityURL, chartType) :
                     chartRepository.findByCommunityURL(communityURL);
        } else if (communityName != null) {
            charts = chartType != null ?
                     chartRepository.findByCommunityNameAndChartType(communityName, chartType) :
                     chartRepository.findByCommunityName(communityName);
        }

        List<ChartDetail> chartDetails = getChartDetails(charts, new Date(), false);

        LOGGER.info("Output parameter charts=[{}]", charts);
        return chartDetails;
    }

    @Transactional(readOnly = true)
    public List<ChartDetail> getChartsByCommunityAndPublishTime(String communityRewriteUrl, Date publishDate) {
        LOGGER.debug("input parameters communityURL [{}], publishDate [{}]", communityRewriteUrl, publishDate);
        List<Chart> charts = chartRepository.findByCommunityURL(communityRewriteUrl);
        List<ChartDetail> chartDetails = getChartDetails(charts, publishDate, false);
        LOGGER.info("Output parameter charts=[{}]", charts);
        return chartDetails;
    }

    @Transactional(readOnly = true)
    public List<ChartDetail> getChartDetails(List<Chart> charts, Date selectedPublishDateTime, boolean clone) {
        LOGGER.debug("input parameters charts: [{}]", new Object[] {charts});

        List<ChartDetail> chartDetails = new ArrayList<ChartDetail>();
        if (isNull(charts)) {
            return chartDetails;
        }

        for (Chart chart : charts) {
            Long lastPublishTimeMillis = isNotNull(selectedPublishDateTime) ?
                                         selectedPublishDateTime.getTime() :
                                         new Date().getTime();
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
                if (o1.getPosition() > o2.getPosition()) {
                    return 1;
                } else if (o1.getPosition() < o2.getPosition()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        LOGGER.info("Output parameter charts=[{}]", chartDetails);
        return chartDetails;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ChartDetail updateChart(ChartDetail chartDetail, MultipartFile imageFile) {
        LOGGER.debug("input updateChart(Chart chart) [{}]", chartDetail);

        if (chartDetail != null) {
            if (isChartDetailAlreadyPresent(chartDetail)) {
                ChartDetail createdOne = chartDetailRepository.findOne(chartDetail.getI());
                chartDetail.setVersionAsPrimitive(createdOne.getVersionAsPrimitive());
            }

            chartDetail = chartDetailRepository.save(chartDetail);

            if (null != imageFile && !imageFile.isEmpty()) {
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

        if (user != null && chart != null) {
            List<Chart> playLists = new ArrayList<Chart>();
            if (user.getSelectedCharts() != null) {
                for (Chart playlist : user.getSelectedCharts()) {
                    if (playlist.getType() != chart.getType()) {
                        playLists.add(playlist);
                    }
                }
            }

            playLists.add(chart);
            user.setSelectedCharts(playLists);

            userService.updateUser(user);

            LOGGER.info("select chart by type done [{}] [{}]", chart, user);
        }

        return user;
    }

    @Transactional(readOnly = true)
    public List<ChartDetail> getDuplicatedMediaChartDetails(String communityUrl, int excludedChartId, long selectedTimeMillis, List<Integer> mediaIds) {

        LOGGER.info("Attempt to find duplicated tracks among given tracks across all charts' updates of [{}] community for [{}] date with [{}] chart updates exclusion", communityUrl,
                    new DateTime(selectedTimeMillis, UTC), excludedChartId);

        if (mediaIds.isEmpty()) {
            return emptyList();
        }

        List<Chart> charts = chartRepository.findByCommunityURLAndExcludedChartId(communityUrl, excludedChartId);

        Long featureUpdateOfExcludedChartPublishTimeMillis = chartDetailRepository.findNearestFeatureChartPublishDate(selectedTimeMillis, excludedChartId);
        if (isNull(featureUpdateOfExcludedChartPublishTimeMillis)) {
            featureUpdateOfExcludedChartPublishTimeMillis = Long.MAX_VALUE;
        }

        List<ChartDetail> duplicatedMediaChartDetails = new ArrayList<ChartDetail>();
        for (Chart chart : charts) {
            Long lastUpdatePublishTimeMillis = chartDetailRepository.findNearestLatestChartPublishDate(selectedTimeMillis, chart.getI());
            Long featureUpdatePublishTimeMillis =
                chartDetailRepository.findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, chart.getI());

            List<Long> publishTimeMillisList = new ArrayList<Long>(2);
            if (isNotNull(lastUpdatePublishTimeMillis)) {
                publishTimeMillisList.add(lastUpdatePublishTimeMillis);
            }
            if (isNotNull(featureUpdatePublishTimeMillis)) {
                publishTimeMillisList.add(featureUpdatePublishTimeMillis);
            }
            if (!publishTimeMillisList.isEmpty()) {
                duplicatedMediaChartDetails.addAll(chartDetailRepository.findDuplicatedMediaChartDetails(chart, publishTimeMillisList, mediaIds));
            }
        }

        LOGGER.info("[{}] duplicated tracks found", duplicatedMediaChartDetails.size());

        return duplicatedMediaChartDetails;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setChartDetailsConverter(ChartDetailsConverter chartDetailsConverter) {
        this.chartDetailsConverter = chartDetailsConverter;
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

}
