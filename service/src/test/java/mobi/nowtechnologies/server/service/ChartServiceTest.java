package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.assembler.UserAsm;
import mobi.nowtechnologies.server.persistence.domain.Artist;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.ChartDetailFactory;
import mobi.nowtechnologies.server.persistence.domain.ChartFactory;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Genre;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaFile;
import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.service.chart.ChartDetailsConverter;
import mobi.nowtechnologies.server.service.chart.CommunityGetChartContentManager;
import mobi.nowtechnologies.server.service.chart.GetChartContentManager;
import mobi.nowtechnologies.server.service.streamzine.BadgesService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.ChartDetailDto;
import mobi.nowtechnologies.server.shared.dto.ChartDto;
import mobi.nowtechnologies.server.shared.dto.PlaylistDto;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import org.springframework.context.ApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.*;

import static org.hamcrest.core.Is.is;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(UserAsm.class)
public class ChartServiceTest {

    ChartService chartServiceFixture;

    @Mock
    ChartRepository mockChartRepository;
    @Mock
    ChartDetailRepository mockChartDetailRepository;
    @Mock
    UserService mockUserService;
    @Mock
    CommunityResourceBundleMessageSource mockMessageSource;
    @Mock
    ChartDetailService mockChartDetailService;
    @Mock
    CloudFileService mockCloudFileService;
    @Mock
    ApplicationContext mockApplicationContext;
    @Mock
    BadgesService badgesService;


    User testUser;
    CommunityGetChartContentManager getChartContentManager = new CommunityGetChartContentManager();

    @Before
    public void setUp() throws Exception {

        testUser = new User().withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("kyiv")));
        testUser.setId(1);

        when(mockUserService.findUserTree(anyInt())).thenReturn(testUser);

        PowerMockito.mockStatic(UserAsm.class);

        chartServiceFixture = spy(new ChartService());
        chartServiceFixture.setChartRepository(mockChartRepository);
        chartServiceFixture.setUserService(mockUserService);
        chartServiceFixture.setMessageSource(mockMessageSource);
        chartServiceFixture.setChartDetailService(mockChartDetailService);
        chartServiceFixture.setCloudFileService(mockCloudFileService);
        chartServiceFixture.setChartDetailRepository(mockChartDetailRepository);
        chartServiceFixture.setApplicationContext(mockApplicationContext);


        ChartDetailsConverter chartDetailsConverter = new ChartDetailsConverter();
        chartDetailsConverter.setBadgesService(badgesService);
        chartDetailsConverter.setMessageSource(mockMessageSource);
        when(mockMessageSource.getMessage(Community.O2_COMMUNITY_REWRITE_URL, "itunes.urlCountryCode", null, null)).thenReturn("GB");
        when(mockMessageSource.getMessage(Community.VF_NZ_COMMUNITY_REWRITE_URL, "itunes.urlCountryCode", null, null)).thenReturn("NZ");
        when(mockMessageSource.getMessage(Community.HL_COMMUNITY_REWRITE_URL, "itunes.urlCountryCode", null, null)).thenReturn("GB");
        chartServiceFixture.setChartDetailsConverter(spy(chartDetailsConverter));
    }

    @Test
    public void testSelectChartByType_NotNullChartNotNullUserNotNullSelectedCharts_Success() throws Exception {
        List<Chart> charts = Arrays.asList(ChartFactory.createChart(), ChartFactory.createChart());
        charts.get(1).setType(ChartType.OTHER_CHART);

        Chart selectedChart = ChartFactory.createChart();
        selectedChart.setI(2);
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setSelectedCharts(charts);

        when(mockChartRepository.findOne(eq(selectedChart.getI()))).thenReturn(selectedChart);
        when(mockUserService.getUserWithSelectedCharts(eq(user.getId()))).thenReturn(user);
        when(mockUserService.updateUser(eq(user))).thenReturn(user);

        User result = chartServiceFixture.selectChartByType(user.getId(), selectedChart.getI());

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(2, result.getSelectedCharts().size());
        assertEquals(selectedChart, result.getSelectedCharts().get(1));

        verify(mockChartRepository, times(1)).findOne(eq(selectedChart.getI()));
        verify(mockUserService, times(1)).getUserWithSelectedCharts(eq(user.getId()));
        verify(mockUserService, times(1)).updateUser(eq(user));
    }

    @Test
    public void testSelectChartByType_NotNullChartNotNullUserNullSelectedCharts_Success() throws Exception {
        Chart selectedChart = ChartFactory.createChart();
        selectedChart.setI(2);
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setSelectedCharts(null);

        when(mockChartRepository.findOne(eq(selectedChart.getI()))).thenReturn(selectedChart);
        when(mockUserService.getUserWithSelectedCharts(eq(user.getId()))).thenReturn(user);
        when(mockUserService.updateUser(eq(user))).thenReturn(user);

        User result = chartServiceFixture.selectChartByType(user.getId(), selectedChart.getI());

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(1, result.getSelectedCharts().size());
        assertEquals(selectedChart, result.getSelectedCharts().get(0));

        verify(mockChartRepository, times(1)).findOne(eq(selectedChart.getI()));
        verify(mockUserService, times(1)).getUserWithSelectedCharts(eq(user.getId()));
        verify(mockUserService, times(1)).updateUser(eq(user));
    }

    @Test
    public void testSelectChartByType_NullChartNotNullUser_Success() throws Exception {
        List<Chart> charts = Arrays.asList(ChartFactory.createChart(), ChartFactory.createChart());
        charts.get(1).setType(ChartType.OTHER_CHART);

        Chart selectedChart = ChartFactory.createChart();
        selectedChart.setI(2);
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setSelectedCharts(charts);

        when(mockChartRepository.findOne(eq(selectedChart.getI()))).thenReturn(null);
        when(mockUserService.getUserWithSelectedCharts(eq(user.getId()))).thenReturn(user);
        when(mockUserService.updateUser(eq(user))).thenReturn(user);

        User result = chartServiceFixture.selectChartByType(user.getId(), selectedChart.getI());

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(2, result.getSelectedCharts().size());
        assertEquals(charts.get(1), result.getSelectedCharts().get(1));

        verify(mockChartRepository, times(1)).findOne(eq(selectedChart.getI()));
        verify(mockUserService, times(1)).getUserWithSelectedCharts(eq(user.getId()));
        verify(mockUserService, times(0)).updateUser(eq(user));
    }

    @Test
    public void testSelectChartByType_NotNullChartNullUser_Success() throws Exception {
        List<Chart> charts = Arrays.asList(ChartFactory.createChart(), ChartFactory.createChart());
        charts.get(1).setType(ChartType.OTHER_CHART);

        Chart selectedChart = ChartFactory.createChart();
        selectedChart.setI(2);
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setSelectedCharts(charts);

        when(mockChartRepository.findOne(eq(selectedChart.getI()))).thenReturn(selectedChart);
        when(mockUserService.getUserWithSelectedCharts(eq(user.getId()))).thenReturn(null);
        when(mockUserService.updateUser(eq(user))).thenReturn(user);

        User result = chartServiceFixture.selectChartByType(user.getId(), selectedChart.getI());

        assertNull(result);

        verify(mockChartRepository, times(1)).findOne(eq(selectedChart.getI()));
        verify(mockUserService, times(1)).getUserWithSelectedCharts(eq(user.getId()));
        verify(mockUserService, times(0)).updateUser(eq(user));
    }

    @Test
    public void testGetLockedChartItems_NotSubscribedNotPendingNotExpiring_Success() throws Exception {
        List<Media> chartDetailIds = singletonList(new Media());
        List<Chart> charts = singletonList(ChartFactory.createChart());
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        String communityName = "chartsnow";
        when(mockChartRepository.findByCommunityName(anyString())).thenReturn(charts);
        when(mockChartDetailService.getLockedChartItemISRCs(eq(charts.get(0).getI()), any(Date.class))).thenReturn(chartDetailIds);

        List<ChartDetail> result = chartServiceFixture.getLockedChartItems(user);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(chartDetailIds.get(0), result.get(0).getMedia());

        verify(mockChartRepository, times(1)).findByCommunityName(anyString());
        verify(mockChartDetailService, times(1)).getLockedChartItemISRCs(eq(charts.get(0).getI()), any(Date.class));
    }

    @Test
    public void testGetLockedChartItems_UserSubscribedOnFreeTrial_Success() throws Exception {
        List<Media> chartDetailIds = singletonList(new Media());
        List<Chart> charts = singletonList(ChartFactory.createChart());
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        PaymentDetails paymentDetails = new SagePayCreditCardPaymentDetails();
        paymentDetails.setActivated(true);
        user.setCurrentPaymentDetails(paymentDetails);
        user.setNextSubPayment(Utils.getEpochSeconds() + 48 * 60 * 60);
        user.setFreeTrialExpiredMillis(user.getNextSubPayment() * 1000L);

        when(mockChartRepository.findByCommunityName(anyString())).thenReturn(charts);
        when(mockChartDetailService.getLockedChartItemISRCs(eq(charts.get(0).getI()), any(Date.class))).thenReturn(chartDetailIds);

        List<ChartDetail> result = chartServiceFixture.getLockedChartItems(user);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(mockChartRepository, times(0)).findByCommunityName(anyString());
        verify(mockChartDetailService, times(0)).getLockedChartItemISRCs(eq(charts.get(0).getI()), any(Date.class));
    }

    @Test
    public void testGetLockedChartItems_UserPending_Success() throws Exception {
        List<Media> chartDetailIds = singletonList(new Media());
        List<Chart> charts = singletonList(ChartFactory.createChart());
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        PaymentDetails paymentDetails = new SagePayCreditCardPaymentDetails();
        paymentDetails.setActivated(true);
        user.setCurrentPaymentDetails(paymentDetails);
        user.setNextSubPayment(Utils.getEpochSeconds() + 10 * 60 * 60);
        user.setLastSuccessfulPaymentDetails(paymentDetails);

        when(mockChartRepository.findByCommunityName(anyString())).thenReturn(charts);
        when(mockChartDetailService.getLockedChartItemISRCs(eq(charts.get(0).getI()), any(Date.class))).thenReturn(chartDetailIds);

        List<ChartDetail> result = chartServiceFixture.getLockedChartItems(user);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(mockChartRepository, times(0)).findByCommunityName(anyString());
        verify(mockChartDetailService, times(0)).getLockedChartItemISRCs(eq(charts.get(0).getI()), any(Date.class));
    }

    @Test
    public void testGetLockedChartItems_UserExpiring_Success() throws Exception {
        List<Media> chartDetailIds = singletonList(new Media());
        List<Chart> charts = singletonList(ChartFactory.createChart());
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        PaymentDetails paymentDetails = new SagePayCreditCardPaymentDetails();
        paymentDetails.setActivated(false);
        paymentDetails.setLastPaymentStatus(PaymentDetailsStatus.SUCCESSFUL);
        user.setCurrentPaymentDetails(paymentDetails);
        user.setNextSubPayment(Utils.getEpochSeconds() + 10 * 60 * 60);
        user.setLastSuccessfulPaymentDetails(paymentDetails);

        when(mockChartRepository.findByCommunityName(anyString())).thenReturn(charts);
        when(mockChartDetailService.getLockedChartItemISRCs(eq(charts.get(0).getI()), any(Date.class))).thenReturn(chartDetailIds);

        List<ChartDetail> result = chartServiceFixture.getLockedChartItems(user);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(mockChartRepository, times(0)).findByCommunityName(anyString());
        verify(mockChartDetailService, times(0)).getLockedChartItemISRCs(eq(charts.get(0).getI()), any(Date.class));
    }

    @Test
    public void shouldReturnEmptyListForUserOnWhiteListedVideoAudioFreeTrial() throws Exception {
        //given
        String communityName = "chartsnow";
        User user = new User().withFreeTrialExpiredMillis(Long.MAX_VALUE).withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO).withPromotion(new Promotion().withIsWhiteListed(true)));
        user.setUserGroup(new UserGroup().withCommunity(new Community().withName(communityName)));

        when(mockChartRepository.findByCommunityName(anyString())).thenReturn(Collections.<Chart>singletonList(new Chart()));
        when(mockChartDetailService.getLockedChartItemISRCs(any(Integer.class), any(Date.class))).thenReturn(Collections.<Media>emptyList());

        //when
        List<ChartDetail> result = chartServiceFixture.getLockedChartItems(user);

        assertNotNull(result);
        assertEquals(Collections.<ChartDetail>emptyList(), result);

        verify(mockChartRepository, times(0)).findByCommunityName(anyString());
        verify(mockChartDetailService, times(0)).getLockedChartItemISRCs(any(Integer.class), any(Date.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetChartsByCommunity_NullNameNotNullType_Success() throws Exception {
        List<ChartDetail> chartDetails = singletonList(ChartDetailFactory.createChartDetail());
        String communityUrl = "chartsnow";
        String communityName = null;
        ChartType chartType = ChartType.OTHER_CHART;

        when(mockChartRepository.findByCommunityName(anyString())).thenReturn(singletonList(new Chart()));
        when(mockChartRepository.findByCommunityURL(anyString())).thenReturn(singletonList(new Chart()));
        when(mockChartRepository.findByCommunityURLAndChartType(anyString(), any(ChartType.class))).thenReturn(singletonList(new Chart()));
        doReturn(chartDetails).when(chartServiceFixture).getChartDetails(any(List.class), any(Date.class), eq(false));

        List<ChartDetail> result = chartServiceFixture.getChartsByCommunity(communityUrl, communityName, chartType);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(mockChartRepository, times(0)).findByCommunityName(anyString());
        verify(mockChartRepository, times(0)).findByCommunityURL(anyString());
        verify(mockChartRepository, times(1)).findByCommunityURLAndChartType(anyString(), any(ChartType.class));
        verify(chartServiceFixture, times(1)).getChartDetails(any(List.class), any(Date.class), eq(false));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetChartsByCommunity_NullNameNullType_Success() throws Exception {
        List<ChartDetail> chartDetails = singletonList(ChartDetailFactory.createChartDetail());
        String communityUrl = "chartsnow";
        String communityName = null;

        when(mockChartRepository.findByCommunityName(anyString())).thenReturn(singletonList(new Chart()));
        when(mockChartRepository.findByCommunityURL(anyString())).thenReturn(singletonList(new Chart()));
        doReturn(chartDetails).when(chartServiceFixture).getChartDetails(any(List.class), any(Date.class), eq(false));

        List<ChartDetail> result = chartServiceFixture.getChartsByCommunity(communityUrl, communityName, null);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(mockChartRepository, times(0)).findByCommunityName(anyString());
        verify(mockChartRepository, times(1)).findByCommunityURL(anyString());
        verify(chartServiceFixture, times(1)).getChartDetails(any(List.class), any(Date.class), eq(false));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetChartsByCommunity_NullUrlNotNullType_Success() throws Exception {
        List<ChartDetail> chartDetails = singletonList(ChartDetailFactory.createChartDetail());
        String communityUrl = null;
        String communityName = "chartsnow";
        ChartType chartType = ChartType.OTHER_CHART;

        when(mockChartRepository.findByCommunityName(anyString())).thenReturn(singletonList(new Chart()));
        when(mockChartRepository.findByCommunityURL(anyString())).thenReturn(singletonList(new Chart()));
        when(mockChartRepository.findByCommunityNameAndChartType(anyString(), any(ChartType.class))).thenReturn(singletonList(new Chart()));
        doReturn(chartDetails).when(chartServiceFixture).getChartDetails(any(List.class), any(Date.class), eq(false));

        List<ChartDetail> result = chartServiceFixture.getChartsByCommunity(communityUrl, communityName, chartType);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(mockChartRepository, times(0)).findByCommunityName(anyString());
        verify(mockChartRepository, times(0)).findByCommunityURL(anyString());
        verify(mockChartRepository, times(1)).findByCommunityNameAndChartType(anyString(), any(ChartType.class));
        verify(chartServiceFixture, times(1)).getChartDetails(any(List.class), any(Date.class), eq(false));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetChartsByCommunity_NullUrlNullType_Success() throws Exception {
        List<ChartDetail> chartDetails = singletonList(ChartDetailFactory.createChartDetail());
        String communityUrl = null;
        String communityName = "chartsnow";

        when(mockChartRepository.findByCommunityName(anyString())).thenReturn(singletonList(new Chart()));
        when(mockChartRepository.findByCommunityURL(anyString())).thenReturn(singletonList(new Chart()));
        doReturn(chartDetails).when(chartServiceFixture).getChartDetails(any(List.class), any(Date.class), eq(false));

        List<ChartDetail> result = chartServiceFixture.getChartsByCommunity(communityUrl, communityName, null);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(mockChartRepository, times(1)).findByCommunityName(anyString());
        verify(mockChartRepository, times(0)).findByCommunityURL(anyString());
        verify(chartServiceFixture, times(1)).getChartDetails(any(List.class), any(Date.class), eq(false));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetChartsByCommunity_NullUrlAndNullNameNullType_Success() throws Exception {
        List<ChartDetail> chartDetails = EMPTY_LIST;
        String communityUrl = null;
        String communityName = null;

        when(mockChartRepository.findByCommunityName(anyString())).thenReturn(singletonList(new Chart()));
        when(mockChartRepository.findByCommunityURL(anyString())).thenReturn(singletonList(new Chart()));
        doReturn(chartDetails).when(chartServiceFixture).getChartDetails(any(List.class), any(Date.class), eq(false));

        List<ChartDetail> result = chartServiceFixture.getChartsByCommunity(communityUrl, communityName, null);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(mockChartRepository, times(0)).findByCommunityName(anyString());
        verify(mockChartRepository, times(0)).findByCommunityURL(anyString());
        verify(chartServiceFixture, times(1)).getChartDetails(any(List.class), any(Date.class), eq(false));
    }


    @Test
    public void testProcessGetChartCommand_Success() throws Exception {
        User user = mock(User.class);
        Community c = mock(Community.class);
        when(c.getRewriteUrlParameter()).thenReturn("o2");
        UserGroup userGroup = mock(UserGroup.class);
        when(userGroup.getCommunity()).thenReturn(c);
        when(user.getUserGroup()).thenReturn(userGroup);
        Resolution resolution = mock(Resolution.class);

        Media media = getMediaInstance(1);

        ChartDetail basicChart = ChartDetailFactory.createChartDetail();
        basicChart.getChart().setType(ChartType.BASIC_CHART);
        basicChart.getChart().setI(1);
        basicChart.setDefaultChart(true);
        ChartDetail basicChart1 = ChartDetailFactory.createChartDetail();
        basicChart1.getChart().setType(ChartType.BASIC_CHART);
        basicChart1.getChart().setI(5);
        basicChart1.setBadgeId(908L);
        ChartDetail topChart = ChartDetailFactory.createChartDetail();
        topChart.getChart().setType(ChartType.HOT_TRACKS);
        topChart.getChart().setI(2);
        ChartDetail otherChart1 = ChartDetailFactory.createChartDetail();
        otherChart1.getChart().setType(ChartType.OTHER_CHART);
        otherChart1.getChart().setI(3);
        otherChart1.setDefaultChart(true);
        ChartDetail otherChart2 = ChartDetailFactory.createChartDetail();
        otherChart2.getChart().setType(ChartType.OTHER_CHART);
        otherChart2.getChart().setI(4);
        otherChart2.setBadgeId(909L);
        ChartDetail videoChart3 = ChartDetailFactory.createChartDetail();
        videoChart3.getChart().setType(ChartType.VIDEO_CHART);
        videoChart3.getChart().setI(6);
        when(user.getSelectedCharts()).thenReturn(Arrays.asList(otherChart2.getChart(), basicChart1.getChart()));
        when(user.isSelectedChart(basicChart1)).thenReturn(true);
        when(user.isSelectedChart(otherChart2)).thenReturn(true);

        when(mockUserService.getUserWithSelectedCharts(anyInt())).thenReturn(user);

        ChartDetail basicChartDetail = getChartDetailInstance(0, 1, media, basicChart.getChart());
        ChartDetail basicChartDetail1 = getChartDetailInstance(0, 1, media, basicChart1.getChart());
        ChartDetail topChartDetail = getChartDetailInstance(0, 2, media, topChart.getChart());
        ChartDetail otherChartDetail1 = getChartDetailInstance(0, 3, media, otherChart1.getChart());
        ChartDetail otherChartDetail2 = getChartDetailInstance(0, 3, media, otherChart2.getChart());
        ChartDetail videoChartDetail = getChartDetailInstance(0, 5, media, videoChart3.getChart());
        videoChartDetail.getMedia().setHeaderFile(null);

        doReturn(Arrays.asList(basicChart, basicChart1, topChart, otherChart2, otherChart1, videoChart3)).when(chartServiceFixture)
                                                                                                         .getChartsByCommunity(eq((String) null), anyString(), any(ChartType.class));
        when(mockChartDetailService.findChartDetailTree(eq(1), any(Date.class), anyBoolean())).thenReturn(Arrays.asList(basicChartDetail));
        when(mockChartDetailService.findChartDetailTree(eq(2), any(Date.class), anyBoolean())).thenReturn(Arrays.asList(topChartDetail));
        when(mockChartDetailService.findChartDetailTree(eq(3), any(Date.class), anyBoolean())).thenReturn(Arrays.asList(otherChartDetail1));
        when(mockChartDetailService.findChartDetailTree(eq(4), any(Date.class), anyBoolean())).thenReturn(Arrays.asList(otherChartDetail2));
        when(mockChartDetailService.findChartDetailTree(eq(5), any(Date.class), anyBoolean())).thenReturn(Arrays.asList(basicChartDetail1));
        when(mockChartDetailService.findChartDetailTree(eq(6), any(Date.class), anyBoolean())).thenReturn(Arrays.asList(videoChartDetail));
        when(mockMessageSource.getMessage(anyString(), eq("getChartContentManager.beanName"), any(Object[].class), any(Locale.class))).thenReturn("communityChartManager");
        when(mockMessageSource.getMessage(anyString(), eq("get.chart.command.default.amazon.url"), any(Object[].class), anyString(), any(Locale.class))).thenReturn("defaultAmazonUrl");
        when(mockApplicationContext.getBean("communityChartManager", GetChartContentManager.class)).thenReturn(getChartContentManager);
        when(badgesService.getBadgeFileName(eq(908L), eq(c), eq(resolution))).thenReturn("image_908");
        when(badgesService.getBadgeFileName(909L, c, resolution)).thenReturn("image_909");

        ChartDto result = chartServiceFixture.processGetChartCommand(user, true, true, resolution, false, false);

        assertNotNull(result);

        PlaylistDto[] playlists = result.getPlaylistDtos();
        assertNotNull(playlists);
        assertEquals(4, playlists.length);
        assertEquals(basicChart1.getTitle(), playlists[0].getPlaylistTitle());
        assertEquals("image_908", playlists[0].getBadgeIcon());
        assertEquals(topChart.getTitle(), playlists[1].getPlaylistTitle());
        assertEquals(otherChart2.getTitle(), playlists[2].getPlaylistTitle());
        assertEquals(basicChart1.getSubtitle(), playlists[0].getSubtitle());
        assertEquals(topChart.getSubtitle(), playlists[1].getSubtitle());
        assertEquals(otherChart2.getSubtitle(), playlists[2].getSubtitle());
        assertEquals(basicChart1.getImageFileName(), playlists[0].getImage());
        assertEquals(topChart.getImageFileName(), playlists[1].getImage());
        assertEquals(otherChart2.getImageFileName(), playlists[2].getImage());
        assertEquals("image_909", playlists[2].getBadgeIcon());
        assertEquals(basicChart1.getChart().getI().byteValue(), playlists[0].getId().byteValue());
        assertEquals(topChart.getChart().getI().byteValue(), playlists[1].getId().byteValue());
        assertEquals(otherChart2.getChart().getI().byteValue(), playlists[2].getId().byteValue());

        ChartDetailDto[] list = result.getChartDetailDtos();
        assertNotNull(list);
        assertEquals(4, list.length);
        assertEquals(ChartDetailDto.class, list[0].getClass());
        assertEquals(ChartDetailDto.class, list[1].getClass());
        assertEquals(ChartDetailDto.class, list[2].getClass());
        assertEquals(1, list[0].getPosition());
        assertEquals(42, list[1].getPosition());
        assertEquals(53, list[2].getPosition());
        assertEquals(basicChart1.getChart().getI().byteValue(), list[0].getPlaylistId().byteValue());
        assertEquals(topChart.getChart().getI().byteValue(), list[1].getPlaylistId().byteValue());
        assertEquals(otherChart2.getChart().getI().byteValue(), list[2].getPlaylistId().byteValue());

        verify(chartServiceFixture).getChartsByCommunity(eq((String) null), anyString(), any(ChartType.class));
        verify(mockChartDetailService).findChartDetailTree(eq(5), any(Date.class), anyBoolean());
        verify(mockChartDetailService).findChartDetailTree(eq(2), any(Date.class), anyBoolean());
        verify(mockChartDetailService, times(0)).findChartDetailTree(eq(3), any(Date.class), anyBoolean());
        verify(mockChartDetailService, times(0)).findChartDetailTree(eq(1), any(Date.class), anyBoolean());
        verify(mockChartDetailService).findChartDetailTree(eq(4), any(Date.class), anyBoolean());
    }

    @Test
    public void testUpdateChart_Success() throws Exception {
        Community community = mock(Community.class);

        ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
        ChartDetail chartDetail1 = ChartDetailFactory.createChartDetail();
        chartDetail1.setI(chartDetail.getI());
        chartDetail1.setVersionAsPrimitive(5);
        MultipartFile imageFile = new MockMultipartFile("file", "1".getBytes());

        when(mockChartDetailRepository.findOne(eq(chartDetail.getI()))).thenReturn(chartDetail1);
        when(mockChartDetailRepository.save(eq(chartDetail))).thenReturn(chartDetail);
        when(mockCloudFileService.uploadFile(any(MultipartFile.class), anyString(), anyMap())).thenReturn(true);

        ChartDetail result = chartServiceFixture.updateChart(chartDetail, imageFile);

        assertNotNull(result);
        assertEquals(chartDetail1.getVersionAsPrimitive(), result.getVersionAsPrimitive());
        assertEquals(chartDetail.getTitle(), result.getTitle());
        assertEquals(chartDetail.getSubtitle(), result.getSubtitle());
        assertEquals(chartDetail.getImageFileName(), result.getImageFileName());

        verify(mockChartDetailRepository, times(1)).findOne(eq(chartDetail.getI()));
        verify(mockChartDetailRepository, times(1)).save(eq(chartDetail));
        verify(mockCloudFileService, times(1)).uploadFile(any(MultipartFile.class), anyString());
    }

    @Test
    public void testUpdateChart_FileNull_Success() throws Exception {
        Community community = mock(Community.class);
        when(community.getRewriteUrlParameter()).thenReturn("hl_uk");

        ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
        Date publishDate = new Date();
        chartDetail.setPublishTimeMillis(publishDate.getTime());
        chartDetail.setI(null);
        MultipartFile imageFile = new MockMultipartFile("file", "".getBytes());

        when(mockChartDetailRepository.save(eq(chartDetail))).thenReturn(chartDetail);
        when(mockCloudFileService.uploadFile(any(MultipartFile.class), anyString(), anyMap())).thenReturn(true);

        ChartDetail result = chartServiceFixture.updateChart(chartDetail, imageFile);

        assertNotNull(result);
        assertEquals(chartDetail.getTitle(), result.getTitle());
        assertEquals(chartDetail.getSubtitle(), result.getSubtitle());
        assertEquals(chartDetail.getImageFileName(), result.getImageFileName());

        verify(mockChartDetailRepository, times(0)).findOne(eq(chartDetail.getI()));
        verify(mockChartDetailRepository, times(1)).save(eq(chartDetail));
        verify(mockCloudFileService, times(0)).uploadFile(any(MultipartFile.class), anyString());
    }

    @Test
    public void testUpdateChart_FileEmpty_Success() throws Exception {
        ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
        chartDetail.setI(null);
        MultipartFile imageFile = null;

        when(mockChartDetailRepository.save(eq(chartDetail))).thenReturn(chartDetail);
        when(mockCloudFileService.uploadFile(any(MultipartFile.class), anyString(), anyMap())).thenReturn(true);

        ChartDetail result = chartServiceFixture.updateChart(chartDetail, imageFile);

        assertNotNull(result);
        assertEquals(chartDetail.getTitle(), result.getTitle());
        assertEquals(chartDetail.getSubtitle(), result.getSubtitle());
        assertEquals(chartDetail.getImageFileName(), result.getImageFileName());

        verify(mockChartDetailRepository, times(0)).findOne(eq(chartDetail.getI()));
        verify(mockChartDetailRepository, times(1)).save(eq(chartDetail));
        verify(mockCloudFileService, times(0)).uploadFile(any(MultipartFile.class), anyString(), anyMap());
    }

    @Test
    public void testUpdateChart_ChartNull_Failure() throws Exception {
        ChartDetail chartDetail = null;
        MultipartFile imageFile = new MockMultipartFile("file", "1".getBytes());

        when(mockChartDetailRepository.save(eq(chartDetail))).thenReturn(chartDetail);
        when(mockCloudFileService.uploadFile(any(MultipartFile.class), anyString(), anyMap())).thenReturn(true);

        ChartDetail result = chartServiceFixture.updateChart(chartDetail, imageFile);

        assertNull(result);

        verify(mockChartDetailRepository, times(0)).save(eq(chartDetail));
        verify(mockCloudFileService, times(0)).uploadFile(any(MultipartFile.class), anyString(), anyMap());
    }

    @Test
    public void testGetChartDetails_IsChoosedChartDetails_Success() throws Exception {
        ChartDetail chartDetail1 = ChartDetailFactory.createChartDetail();
        chartDetail1.getChart().setI(1);
        ChartDetail chartDetail2 = ChartDetailFactory.createChartDetail();
        chartDetail2.getChart().setI(2);
        ChartDetail chartDetail3 = ChartDetailFactory.createChartDetail();
        chartDetail3.getChart().setI(3);
        List<Chart> charts = Arrays.asList(chartDetail1.getChart(), chartDetail2.getChart(), chartDetail3.getChart());
        Long nearestLatestDate = new Date().getTime() - 10000;
        Date chosenDate = new Date();

        when(mockChartDetailRepository.findNearestLatestPublishDate(anyLong(), anyInt())).thenReturn(nearestLatestDate);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(1), eq(nearestLatestDate))).thenReturn(chartDetail1);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(2), eq(nearestLatestDate))).thenReturn(chartDetail2);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(3), eq(nearestLatestDate))).thenReturn(chartDetail3);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(1), eq(chosenDate.getTime()))).thenReturn(chartDetail1);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(2), eq(chosenDate.getTime()))).thenReturn(chartDetail2);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(3), eq(chosenDate.getTime()))).thenReturn(chartDetail3);
        when(mockChartDetailRepository.countChartDetailTreeByChartAndPublishTimeMillis(eq(1), eq(chosenDate.getTime()))).thenReturn(1L);
        when(mockChartDetailRepository.countChartDetailTreeByChartAndPublishTimeMillis(eq(2), eq(chosenDate.getTime()))).thenReturn(1L);
        when(mockChartDetailRepository.countChartDetailTreeByChartAndPublishTimeMillis(eq(3), eq(chosenDate.getTime()))).thenReturn(1L);

        List<ChartDetail> result = chartServiceFixture.getChartDetails(charts, chosenDate, false);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(chartDetail1.getI(), result.get(0).getI());
        assertEquals(chartDetail2.getI(), result.get(1).getI());
        assertEquals(chartDetail3.getI(), result.get(2).getI());
        assertEquals(chartDetail1.getTitle(), result.get(0).getTitle());
        assertEquals(chartDetail2.getTitle(), result.get(1).getTitle());
        assertEquals(chartDetail3.getTitle(), result.get(2).getTitle());
        assertEquals(1, result.get(0).getChart().getNumTracks());
        assertEquals(1, result.get(1).getChart().getNumTracks());
        assertEquals(1, result.get(2).getChart().getNumTracks());

        verify(mockChartDetailRepository, times(0)).findNearestLatestPublishDate(anyLong(), anyInt());
        verify(mockChartDetailRepository, times(0)).findChartWithDetailsByChartAndPublishTimeMillis(eq(1), eq(nearestLatestDate));
        verify(mockChartDetailRepository, times(0)).findChartWithDetailsByChartAndPublishTimeMillis(eq(2), eq(nearestLatestDate));
        verify(mockChartDetailRepository, times(0)).findChartWithDetailsByChartAndPublishTimeMillis(eq(3), eq(nearestLatestDate));
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(1), eq(chosenDate.getTime()));
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(2), eq(chosenDate.getTime()));
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(3), eq(chosenDate.getTime()));
        verify(mockChartDetailRepository, times(1)).countChartDetailTreeByChartAndPublishTimeMillis(eq(1), eq(chosenDate.getTime()));
        verify(mockChartDetailRepository, times(1)).countChartDetailTreeByChartAndPublishTimeMillis(eq(2), eq(chosenDate.getTime()));
        verify(mockChartDetailRepository, times(1)).countChartDetailTreeByChartAndPublishTimeMillis(eq(3), eq(chosenDate.getTime()));
    }

    @Test
    public void testGetChartDetails_CloneIsLatestDetailsNotChosenChartDetails_Success() throws Exception {
        ChartDetail chartDetail1 = ChartDetailFactory.createChartDetail();
        chartDetail1.getChart().setI(1);
        ChartDetail chartDetail2 = ChartDetailFactory.createChartDetail();
        chartDetail2.getChart().setI(2);
        ChartDetail chartDetail3 = ChartDetailFactory.createChartDetail();
        chartDetail3.getChart().setI(3);
        List<Chart> charts = Arrays.asList(chartDetail1.getChart(), chartDetail2.getChart(), chartDetail3.getChart());
        Long nearestLatestDate = new Date().getTime() - 100000;
        Date chosenDate = new Date();

        when(mockChartDetailRepository.findNearestLatestChartPublishDate(anyLong(), anyInt())).thenReturn(nearestLatestDate);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(1), eq(nearestLatestDate))).thenReturn(chartDetail1);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(2), eq(nearestLatestDate))).thenReturn(chartDetail2);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(3), eq(nearestLatestDate))).thenReturn(chartDetail3);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(1), eq(chosenDate.getTime()))).thenReturn(null);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(2), eq(chosenDate.getTime()))).thenReturn(null);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(3), eq(chosenDate.getTime()))).thenReturn(null);
        when(mockChartDetailRepository.countChartDetailTreeByChartAndPublishTimeMillis(eq(1), eq(nearestLatestDate))).thenReturn(1L);
        when(mockChartDetailRepository.countChartDetailTreeByChartAndPublishTimeMillis(eq(2), eq(nearestLatestDate))).thenReturn(1L);
        when(mockChartDetailRepository.countChartDetailTreeByChartAndPublishTimeMillis(eq(3), eq(nearestLatestDate))).thenReturn(1L);

        List<ChartDetail> result = chartServiceFixture.getChartDetails(charts, chosenDate, true);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(null, result.get(0).getI());
        assertEquals(null, result.get(1).getI());
        assertEquals(null, result.get(2).getI());
        assertEquals(chartDetail1.getTitle(), result.get(0).getTitle());
        assertEquals(chartDetail2.getTitle(), result.get(1).getTitle());
        assertEquals(chartDetail3.getTitle(), result.get(2).getTitle());
        assertEquals(1, result.get(0).getChart().getNumTracks());
        assertEquals(1, result.get(1).getChart().getNumTracks());
        assertEquals(1, result.get(2).getChart().getNumTracks());

        verify(mockChartDetailRepository, times(3)).findNearestLatestChartPublishDate(anyLong(), anyInt());
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(1), eq(nearestLatestDate));
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(2), eq(nearestLatestDate));
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(3), eq(nearestLatestDate));
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(1), eq(chosenDate.getTime()));
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(2), eq(chosenDate.getTime()));
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(3), eq(chosenDate.getTime()));
        verify(mockChartDetailRepository, times(1)).countChartDetailTreeByChartAndPublishTimeMillis(eq(1), eq(nearestLatestDate));
        verify(mockChartDetailRepository, times(1)).countChartDetailTreeByChartAndPublishTimeMillis(eq(2), eq(nearestLatestDate));
        verify(mockChartDetailRepository, times(1)).countChartDetailTreeByChartAndPublishTimeMillis(eq(3), eq(nearestLatestDate));
    }

    @Test
    public void testGetChartDetails_NotCloneIsLatestDetailsNotChosenChartDetails_Success() throws Exception {
        ChartDetail chartDetail1 = ChartDetailFactory.createChartDetail();
        chartDetail1.getChart().setI(1);
        ChartDetail chartDetail2 = ChartDetailFactory.createChartDetail();
        chartDetail2.getChart().setI(2);
        ChartDetail chartDetail3 = ChartDetailFactory.createChartDetail();
        chartDetail3.getChart().setI(3);
        List<Chart> charts = Arrays.asList(chartDetail1.getChart(), chartDetail2.getChart(), chartDetail3.getChart());
        Long nearestLatestDate = new Date().getTime() - 100000;
        Date chosenDate = new Date();

        when(mockChartDetailRepository.findNearestLatestChartPublishDate(anyLong(), anyInt())).thenReturn(nearestLatestDate);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(1), eq(nearestLatestDate))).thenReturn(chartDetail1);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(2), eq(nearestLatestDate))).thenReturn(chartDetail2);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(3), eq(nearestLatestDate))).thenReturn(chartDetail3);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(1), eq(chosenDate.getTime()))).thenReturn(null);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(2), eq(chosenDate.getTime()))).thenReturn(null);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(3), eq(chosenDate.getTime()))).thenReturn(null);
        when(mockChartDetailRepository.countChartDetailTreeByChartAndPublishTimeMillis(eq(1), eq(nearestLatestDate))).thenReturn(1L);
        when(mockChartDetailRepository.countChartDetailTreeByChartAndPublishTimeMillis(eq(2), eq(nearestLatestDate))).thenReturn(1L);
        when(mockChartDetailRepository.countChartDetailTreeByChartAndPublishTimeMillis(eq(3), eq(nearestLatestDate))).thenReturn(1L);

        List<ChartDetail> result = chartServiceFixture.getChartDetails(charts, chosenDate, false);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(chartDetail1.getI(), result.get(0).getI());
        assertEquals(chartDetail2.getI(), result.get(1).getI());
        assertEquals(chartDetail3.getI(), result.get(2).getI());
        assertEquals(chartDetail1.getTitle(), result.get(0).getTitle());
        assertEquals(chartDetail2.getTitle(), result.get(1).getTitle());
        assertEquals(chartDetail3.getTitle(), result.get(2).getTitle());
        assertEquals(1, result.get(0).getChart().getNumTracks());
        assertEquals(1, result.get(1).getChart().getNumTracks());
        assertEquals(1, result.get(2).getChart().getNumTracks());

        verify(mockChartDetailRepository, times(3)).findNearestLatestChartPublishDate(anyLong(), anyInt());
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(1), eq(nearestLatestDate));
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(2), eq(nearestLatestDate));
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(3), eq(nearestLatestDate));
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(1), eq(chosenDate.getTime()));
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(2), eq(chosenDate.getTime()));
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(3), eq(chosenDate.getTime()));
        verify(mockChartDetailRepository, times(1)).countChartDetailTreeByChartAndPublishTimeMillis(eq(1), eq(nearestLatestDate));
        verify(mockChartDetailRepository, times(1)).countChartDetailTreeByChartAndPublishTimeMillis(eq(2), eq(nearestLatestDate));
        verify(mockChartDetailRepository, times(1)).countChartDetailTreeByChartAndPublishTimeMillis(eq(3), eq(nearestLatestDate));
    }

    @Test
    public void testGetChartDetails_NotLatestDetailsNotChosenChartDetails_Success() throws Exception {
        ChartDetail chartDetail1 = ChartDetailFactory.createChartDetail();
        chartDetail1.getChart().setI(1);
        ChartDetail chartDetail2 = ChartDetailFactory.createChartDetail();
        chartDetail2.getChart().setI(2);
        ChartDetail chartDetail3 = ChartDetailFactory.createChartDetail();
        chartDetail3.getChart().setI(3);
        List<Chart> charts = Arrays.asList(chartDetail1.getChart(), chartDetail2.getChart(), chartDetail3.getChart());
        Long nearestLatestDate = null;
        Date chosenDate = new Date();

        when(mockChartDetailRepository.findNearestLatestChartPublishDate(anyLong(), anyInt())).thenReturn(nearestLatestDate);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(1), eq(nearestLatestDate))).thenReturn(chartDetail1);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(2), eq(nearestLatestDate))).thenReturn(chartDetail1);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(3), eq(nearestLatestDate))).thenReturn(chartDetail1);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(1), eq(chosenDate.getTime()))).thenReturn(null);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(2), eq(chosenDate.getTime()))).thenReturn(null);
        when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq(3), eq(chosenDate.getTime()))).thenReturn(null);

        List<ChartDetail> result = chartServiceFixture.getChartDetails(charts, chosenDate, false);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(null, result.get(0).getTitle());
        assertEquals(null, result.get(1).getTitle());
        assertEquals(null, result.get(2).getTitle());
        assertEquals(0, result.get(0).getChart().getNumTracks());
        assertEquals(0, result.get(1).getChart().getNumTracks());
        assertEquals(0, result.get(2).getChart().getNumTracks());

        verify(mockChartDetailRepository, times(3)).findNearestLatestChartPublishDate(anyLong(), anyInt());
        verify(mockChartDetailRepository, times(0)).findChartWithDetailsByChartAndPublishTimeMillis(eq(1), eq(nearestLatestDate));
        verify(mockChartDetailRepository, times(0)).findChartWithDetailsByChartAndPublishTimeMillis(eq(2), eq(nearestLatestDate));
        verify(mockChartDetailRepository, times(0)).findChartWithDetailsByChartAndPublishTimeMillis(eq(3), eq(nearestLatestDate));
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(1), eq(chosenDate.getTime()));
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(2), eq(chosenDate.getTime()));
        verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq(3), eq(chosenDate.getTime()));
    }

    @Test
    public void shouldReturnDuplicatedMediaChartDetails() {
        //given
        String communityUrl = "mtv1";
        long selectedTimeMillis = 111;
        List<Integer> mediaIds = asList(333, 666, 999);
        int excludedChartId = 3;

        List<Chart> charts = asList(new Chart().withI(1), new Chart().withI(2));

        when(mockChartRepository.findByCommunityURLAndExcludedChartId(communityUrl, excludedChartId)).thenReturn(charts);

        Long featureUpdateOfExcludedChartPublishTimeMillis = Long.MAX_VALUE;
        when(mockChartDetailRepository.findNearestFeatureChartPublishDate(selectedTimeMillis, excludedChartId)).thenReturn(null);

        when(mockChartDetailRepository.findNearestLatestChartPublishDate(selectedTimeMillis, 1)).thenReturn(11L);
        when(mockChartDetailRepository.findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 1)).thenReturn(12L);

        when(mockChartDetailRepository.findNearestLatestChartPublishDate(selectedTimeMillis, 2)).thenReturn(21L);
        when(mockChartDetailRepository.findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 2)).thenReturn(22L);

        when(mockChartDetailRepository.findDuplicatedMediaChartDetails(charts.get(0), asList(11L, 12L), mediaIds)).thenReturn(asList(new ChartDetail().withI(111), new ChartDetail().withI(112)));
        when(mockChartDetailRepository.findDuplicatedMediaChartDetails(charts.get(1), asList(21L, 22L), mediaIds)).thenReturn(asList(new ChartDetail().withI(221), new ChartDetail().withI(222)));

        //when
        List<ChartDetail> duplicatedMediaChartDetails = chartServiceFixture.getDuplicatedMediaChartDetails(communityUrl, excludedChartId, selectedTimeMillis, mediaIds);

        //then
        assertThat(duplicatedMediaChartDetails.size(), is(4));

        assertThat(duplicatedMediaChartDetails.get(0).getI(), is(111));
        assertThat(duplicatedMediaChartDetails.get(1).getI(), is(112));
        assertThat(duplicatedMediaChartDetails.get(2).getI(), is(221));
        assertThat(duplicatedMediaChartDetails.get(3).getI(), is(222));

        verify(mockChartRepository).findByCommunityURLAndExcludedChartId(communityUrl, excludedChartId);

        verify(mockChartDetailRepository).findNearestFeatureChartPublishDate(selectedTimeMillis, excludedChartId);

        verify(mockChartDetailRepository).findNearestLatestChartPublishDate(selectedTimeMillis, 1);
        verify(mockChartDetailRepository).findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 1);

        verify(mockChartDetailRepository).findNearestLatestChartPublishDate(selectedTimeMillis, 2);
        verify(mockChartDetailRepository).findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 2);

        verify(mockChartDetailRepository).findDuplicatedMediaChartDetails(charts.get(0), asList(11L, 12L), mediaIds);
        verify(mockChartDetailRepository).findDuplicatedMediaChartDetails(charts.get(1), asList(21L, 22L), mediaIds);
    }

    @Test
    public void shouldReturnDuplicatedMediaChartDetailsTillFeatureUpdateOfExcludedChartPublishTimeMillis() {
        //given
        String communityUrl = "mtv1";
        long selectedTimeMillis = 111;
        List<Integer> mediaIds = asList(333, 666, 999);
        int excludedChartId = 3;

        List<Chart> charts = asList(new Chart().withI(1), new Chart().withI(2));

        when(mockChartRepository.findByCommunityURLAndExcludedChartId(communityUrl, excludedChartId)).thenReturn(charts);

        Long featureUpdateOfExcludedChartPublishTimeMillis = 555L;
        when(mockChartDetailRepository.findNearestFeatureChartPublishDate(selectedTimeMillis, excludedChartId)).thenReturn(featureUpdateOfExcludedChartPublishTimeMillis);

        when(mockChartDetailRepository.findNearestLatestChartPublishDate(selectedTimeMillis, 1)).thenReturn(11L);
        when(mockChartDetailRepository.findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 1)).thenReturn(12L);

        when(mockChartDetailRepository.findNearestLatestChartPublishDate(selectedTimeMillis, 2)).thenReturn(21L);
        when(mockChartDetailRepository.findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 2)).thenReturn(22L);

        when(mockChartDetailRepository.findDuplicatedMediaChartDetails(charts.get(0), asList(11L, 12L), mediaIds)).thenReturn(asList(new ChartDetail().withI(111), new ChartDetail().withI(112)));
        when(mockChartDetailRepository.findDuplicatedMediaChartDetails(charts.get(1), asList(21L, 22L), mediaIds)).thenReturn(asList(new ChartDetail().withI(221), new ChartDetail().withI(222)));

        //when
        List<ChartDetail> duplicatedMediaChartDetails = chartServiceFixture.getDuplicatedMediaChartDetails(communityUrl, excludedChartId, selectedTimeMillis, mediaIds);

        //then
        assertThat(duplicatedMediaChartDetails.size(), is(4));

        assertThat(duplicatedMediaChartDetails.get(0).getI(), is(111));
        assertThat(duplicatedMediaChartDetails.get(1).getI(), is(112));
        assertThat(duplicatedMediaChartDetails.get(2).getI(), is(221));
        assertThat(duplicatedMediaChartDetails.get(3).getI(), is(222));

        verify(mockChartRepository).findByCommunityURLAndExcludedChartId(communityUrl, excludedChartId);

        verify(mockChartDetailRepository).findNearestFeatureChartPublishDate(selectedTimeMillis, excludedChartId);

        verify(mockChartDetailRepository).findNearestLatestChartPublishDate(selectedTimeMillis, 1);
        verify(mockChartDetailRepository).findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 1);

        verify(mockChartDetailRepository).findNearestLatestChartPublishDate(selectedTimeMillis, 2);
        verify(mockChartDetailRepository).findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 2);

        verify(mockChartDetailRepository).findDuplicatedMediaChartDetails(charts.get(0), asList(11L, 12L), mediaIds);
        verify(mockChartDetailRepository).findDuplicatedMediaChartDetails(charts.get(1), asList(21L, 22L), mediaIds);
    }

    @Test
    public void shouldReturnDuplicatedMediaChartDetailsWhenNoFeatureChartsUpdates() {
        //given
        String communityUrl = "mtv1";
        long selectedTimeMillis = 111;
        List<Integer> mediaIds = asList(333, 666, 999);
        int excludedChartId = 3;

        List<Chart> charts = asList(new Chart().withI(1), new Chart().withI(2));

        when(mockChartRepository.findByCommunityURLAndExcludedChartId(communityUrl, excludedChartId)).thenReturn(charts);

        Long featureUpdateOfExcludedChartPublishTimeMillis = 555L;
        when(mockChartDetailRepository.findNearestFeatureChartPublishDate(selectedTimeMillis, excludedChartId)).thenReturn(featureUpdateOfExcludedChartPublishTimeMillis);

        when(mockChartDetailRepository.findNearestLatestChartPublishDate(selectedTimeMillis, 1)).thenReturn(11L);
        when(mockChartDetailRepository.findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 1)).thenReturn(null);

        when(mockChartDetailRepository.findNearestLatestChartPublishDate(selectedTimeMillis, 2)).thenReturn(21L);
        when(mockChartDetailRepository.findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 2)).thenReturn(null);

        when(mockChartDetailRepository.findDuplicatedMediaChartDetails(charts.get(0), asList(11L), mediaIds)).thenReturn(asList(new ChartDetail().withI(111), new ChartDetail().withI(112)));
        when(mockChartDetailRepository.findDuplicatedMediaChartDetails(charts.get(1), asList(21L), mediaIds)).thenReturn(asList(new ChartDetail().withI(221), new ChartDetail().withI(222)));

        //when
        List<ChartDetail> duplicatedMediaChartDetails = chartServiceFixture.getDuplicatedMediaChartDetails(communityUrl, excludedChartId, selectedTimeMillis, mediaIds);

        //then
        assertThat(duplicatedMediaChartDetails.size(), is(4));

        assertThat(duplicatedMediaChartDetails.get(0).getI(), is(111));
        assertThat(duplicatedMediaChartDetails.get(1).getI(), is(112));
        assertThat(duplicatedMediaChartDetails.get(2).getI(), is(221));
        assertThat(duplicatedMediaChartDetails.get(3).getI(), is(222));

        verify(mockChartRepository).findByCommunityURLAndExcludedChartId(communityUrl, excludedChartId);

        verify(mockChartDetailRepository).findNearestFeatureChartPublishDate(selectedTimeMillis, excludedChartId);

        verify(mockChartDetailRepository).findNearestLatestChartPublishDate(selectedTimeMillis, 1);
        verify(mockChartDetailRepository).findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 1);

        verify(mockChartDetailRepository).findNearestLatestChartPublishDate(selectedTimeMillis, 2);
        verify(mockChartDetailRepository).findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 2);

        verify(mockChartDetailRepository).findDuplicatedMediaChartDetails(charts.get(0), asList(11L), mediaIds);
        verify(mockChartDetailRepository).findDuplicatedMediaChartDetails(charts.get(1), asList(21L), mediaIds);
    }

    @Test
    public void shouldReturnDuplicatedMediaChartDetailsWhenNoLatestChartUpdate() {
        //given
        String communityUrl = "mtv1";
        long selectedTimeMillis = 111;
        List<Integer> mediaIds = asList(333, 666, 999);
        int excludedChartId = 3;

        List<Chart> charts = asList(new Chart().withI(1), new Chart().withI(2));

        when(mockChartRepository.findByCommunityURLAndExcludedChartId(communityUrl, excludedChartId)).thenReturn(charts);

        Long featureUpdateOfExcludedChartPublishTimeMillis = 555L;
        when(mockChartDetailRepository.findNearestFeatureChartPublishDate(selectedTimeMillis, excludedChartId)).thenReturn(featureUpdateOfExcludedChartPublishTimeMillis);

        when(mockChartDetailRepository.findNearestLatestChartPublishDate(selectedTimeMillis, 1)).thenReturn(null);
        when(mockChartDetailRepository.findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 1)).thenReturn(12L);

        when(mockChartDetailRepository.findNearestLatestChartPublishDate(selectedTimeMillis, 2)).thenReturn(21L);
        when(mockChartDetailRepository.findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 2)).thenReturn(22L);

        when(mockChartDetailRepository.findDuplicatedMediaChartDetails(charts.get(0), asList(12L), mediaIds)).thenReturn(asList(new ChartDetail().withI(111), new ChartDetail().withI(112)));
        when(mockChartDetailRepository.findDuplicatedMediaChartDetails(charts.get(1), asList(21L, 22L), mediaIds)).thenReturn(asList(new ChartDetail().withI(221), new ChartDetail().withI(222)));

        //when
        List<ChartDetail> duplicatedMediaChartDetails = chartServiceFixture.getDuplicatedMediaChartDetails(communityUrl, excludedChartId, selectedTimeMillis, mediaIds);

        //then
        assertThat(duplicatedMediaChartDetails.size(), is(4));

        assertThat(duplicatedMediaChartDetails.get(0).getI(), is(111));
        assertThat(duplicatedMediaChartDetails.get(1).getI(), is(112));
        assertThat(duplicatedMediaChartDetails.get(2).getI(), is(221));
        assertThat(duplicatedMediaChartDetails.get(3).getI(), is(222));

        verify(mockChartRepository).findByCommunityURLAndExcludedChartId(communityUrl, excludedChartId);

        verify(mockChartDetailRepository).findNearestFeatureChartPublishDate(selectedTimeMillis, excludedChartId);

        verify(mockChartDetailRepository).findNearestLatestChartPublishDate(selectedTimeMillis, 1);
        verify(mockChartDetailRepository).findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 1);

        verify(mockChartDetailRepository).findNearestLatestChartPublishDate(selectedTimeMillis, 2);
        verify(mockChartDetailRepository).findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 2);

        verify(mockChartDetailRepository).findDuplicatedMediaChartDetails(charts.get(0), asList(12L), mediaIds);
        verify(mockChartDetailRepository).findDuplicatedMediaChartDetails(charts.get(1), asList(21L, 22L), mediaIds);
    }

    @Test
    public void shouldReturnEmptyDuplicatedMediaChartDetailsListWhenNoCharts() {
        //given
        String communityUrl = "mtv1";
        long selectedTimeMillis = 111;
        List<Integer> mediaIds = asList(333, 666, 999);
        int excludedChartId = 3;

        List<Chart> charts = new ArrayList<Chart>();

        when(mockChartRepository.findByCommunityURLAndExcludedChartId(communityUrl, excludedChartId)).thenReturn(charts);

        Long featureUpdateOfExcludedChartPublishTimeMillis = Long.MAX_VALUE;

        //when
        List<ChartDetail> duplicatedMediaChartDetails = chartServiceFixture.getDuplicatedMediaChartDetails(communityUrl, excludedChartId, selectedTimeMillis, mediaIds);

        //then
        assertThat(duplicatedMediaChartDetails.size(), is(0));

        verify(mockChartRepository).findByCommunityURLAndExcludedChartId(communityUrl, excludedChartId);

        verify(mockChartDetailRepository).findNearestFeatureChartPublishDate(selectedTimeMillis, excludedChartId);

        verify(mockChartDetailRepository, times(0)).findNearestLatestChartPublishDate(eq(selectedTimeMillis), anyInt());
        verify(mockChartDetailRepository, times(0)).findNearestFeatureChartPublishDateBeforeGivenDate(eq(selectedTimeMillis), eq(featureUpdateOfExcludedChartPublishTimeMillis), anyInt());

        verify(mockChartDetailRepository, times(0)).findNearestLatestChartPublishDate(eq(selectedTimeMillis), anyInt());
        verify(mockChartDetailRepository, times(0)).findNearestFeatureChartPublishDateBeforeGivenDate(eq(selectedTimeMillis), eq(featureUpdateOfExcludedChartPublishTimeMillis), anyInt());

        verify(mockChartDetailRepository, times(0)).findDuplicatedMediaChartDetails(any(Chart.class), anyListOf(Long.class), anyListOf(Integer.class));
        verify(mockChartDetailRepository, times(0)).findDuplicatedMediaChartDetails(any(Chart.class), anyListOf(Long.class), anyListOf(Integer.class));
    }

    @Test
    public void shouldReturnEmptyDuplicatedMediaChartDetailsListWhenNoAnyMedias() {
        //given
        String communityUrl = "mtv1";
        long selectedTimeMillis = 111;
        List<Integer> mediaIds = emptyList();
        int excludedChartId = 3;

        //when
        List<ChartDetail> duplicatedMediaChartDetails = chartServiceFixture.getDuplicatedMediaChartDetails(communityUrl, excludedChartId, selectedTimeMillis, mediaIds);

        //then
        assertThat(duplicatedMediaChartDetails.size(), is(0));

        verify(mockChartRepository, times(0)).findByCommunityURLAndExcludedChartId(anyString(), anyInt());

        verify(mockChartDetailRepository, times(0)).findNearestFeatureChartPublishDate(anyLong(), anyInt());

        verify(mockChartDetailRepository, times(0)).findNearestLatestChartPublishDate(anyLong(), anyInt());
        verify(mockChartDetailRepository, times(0)).findNearestFeatureChartPublishDateBeforeGivenDate(anyLong(), anyLong(), anyInt());

        verify(mockChartDetailRepository, times(0)).findDuplicatedMediaChartDetails(any(Chart.class), anyListOf(Long.class), anyListOf(Integer.class));
        verify(mockChartDetailRepository, times(0)).findDuplicatedMediaChartDetails(any(Chart.class), anyListOf(Long.class), anyListOf(Integer.class));
    }

    @Test
    public void shouldReturnEmptyDuplicatedMediaChartDetailsListWhenNoAnyUpdates() {
        //given
        String communityUrl = "mtv1";
        long selectedTimeMillis = 111;
        List<Integer> mediaIds = asList(333, 666, 999);
        int excludedChartId = 3;

        List<Chart> charts = asList(new Chart().withI(1), new Chart().withI(2));

        when(mockChartRepository.findByCommunityURLAndExcludedChartId(communityUrl, excludedChartId)).thenReturn(charts);

        Long featureUpdateOfExcludedChartPublishTimeMillis = Long.MAX_VALUE;
        when(mockChartDetailRepository.findNearestFeatureChartPublishDate(selectedTimeMillis, excludedChartId)).thenReturn(null);

        when(mockChartDetailRepository.findNearestLatestChartPublishDate(selectedTimeMillis, 1)).thenReturn(null);
        when(mockChartDetailRepository.findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 1)).thenReturn(null);

        when(mockChartDetailRepository.findNearestLatestChartPublishDate(selectedTimeMillis, 2)).thenReturn(null);
        when(mockChartDetailRepository.findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 2)).thenReturn(null);

        when(mockChartDetailRepository.findDuplicatedMediaChartDetails(charts.get(0), new ArrayList<Long>(), mediaIds)).thenReturn(new ArrayList<ChartDetail>());
        when(mockChartDetailRepository.findDuplicatedMediaChartDetails(charts.get(1), new ArrayList<Long>(), mediaIds)).thenReturn(new ArrayList<ChartDetail>());

        //when
        List<ChartDetail> duplicatedMediaChartDetails = chartServiceFixture.getDuplicatedMediaChartDetails(communityUrl, excludedChartId, selectedTimeMillis, mediaIds);

        //then
        assertThat(duplicatedMediaChartDetails.size(), is(0));

        verify(mockChartRepository).findByCommunityURLAndExcludedChartId(communityUrl, excludedChartId);

        verify(mockChartDetailRepository).findNearestFeatureChartPublishDate(selectedTimeMillis, excludedChartId);

        verify(mockChartDetailRepository).findNearestLatestChartPublishDate(selectedTimeMillis, 1);
        verify(mockChartDetailRepository).findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 1);

        verify(mockChartDetailRepository).findNearestLatestChartPublishDate(selectedTimeMillis, 2);
        verify(mockChartDetailRepository).findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 2);

        verify(mockChartDetailRepository, times(0)).findDuplicatedMediaChartDetails(any(Chart.class), anyListOf(Long.class), anyListOf(Integer.class));
        verify(mockChartDetailRepository, times(0)).findDuplicatedMediaChartDetails(any(Chart.class), anyListOf(Long.class), anyListOf(Integer.class));
    }

    @Test
    public void shouldReturnEmptyDuplicatedMediaChartDetailsListWhenNoDuplicates() {
        //given
        String communityUrl = "mtv1";
        long selectedTimeMillis = 111;
        List<Integer> mediaIds = asList(333, 666, 999);
        int excludedChartId = 3;

        List<Chart> charts = asList(new Chart().withI(1), new Chart().withI(2));

        when(mockChartRepository.findByCommunityURLAndExcludedChartId(communityUrl, excludedChartId)).thenReturn(charts);

        Long featureUpdateOfExcludedChartPublishTimeMillis = Long.MAX_VALUE;
        when(mockChartDetailRepository.findNearestFeatureChartPublishDate(selectedTimeMillis, excludedChartId)).thenReturn(null);

        when(mockChartDetailRepository.findNearestLatestChartPublishDate(selectedTimeMillis, 1)).thenReturn(11L);
        when(mockChartDetailRepository.findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 1)).thenReturn(12L);

        when(mockChartDetailRepository.findNearestLatestChartPublishDate(selectedTimeMillis, 2)).thenReturn(21L);
        when(mockChartDetailRepository.findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 2)).thenReturn(22L);

        when(mockChartDetailRepository.findDuplicatedMediaChartDetails(charts.get(0), asList(11L, 12L), mediaIds)).thenReturn(new ArrayList<ChartDetail>());
        when(mockChartDetailRepository.findDuplicatedMediaChartDetails(charts.get(1), asList(21L, 22L), mediaIds)).thenReturn(new ArrayList<ChartDetail>());

        //when
        List<ChartDetail> duplicatedMediaChartDetails = chartServiceFixture.getDuplicatedMediaChartDetails(communityUrl, excludedChartId, selectedTimeMillis, mediaIds);

        //then
        assertThat(duplicatedMediaChartDetails.size(), is(0));

        verify(mockChartRepository).findByCommunityURLAndExcludedChartId(communityUrl, excludedChartId);

        verify(mockChartDetailRepository).findNearestFeatureChartPublishDate(selectedTimeMillis, excludedChartId);

        verify(mockChartDetailRepository).findNearestLatestChartPublishDate(selectedTimeMillis, 1);
        verify(mockChartDetailRepository).findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 1);

        verify(mockChartDetailRepository).findNearestLatestChartPublishDate(selectedTimeMillis, 2);
        verify(mockChartDetailRepository).findNearestFeatureChartPublishDateBeforeGivenDate(selectedTimeMillis, featureUpdateOfExcludedChartPublishTimeMillis, 2);

        verify(mockChartDetailRepository).findDuplicatedMediaChartDetails(charts.get(0), asList(11L, 12L), mediaIds);
        verify(mockChartDetailRepository).findDuplicatedMediaChartDetails(charts.get(1), asList(21L, 22L), mediaIds);
    }

    private ChartDetail getChartDetailInstance(final long publishTimeMillis, int i, Media media, final Chart chart) {
        ChartDetail originalChartDetail = new ChartDetail();
        originalChartDetail.setChannel("channel" + i);
        originalChartDetail.setChart(chart);
        originalChartDetail.setChgPosition(ChgPosition.DOWN);
        originalChartDetail.setI(i);
        originalChartDetail.setInfo("info" + i);
        originalChartDetail.setMedia(media);
        originalChartDetail.setPosition((byte) i);
        originalChartDetail.setPrevPosition((byte) 0);
        originalChartDetail.setPublishTimeMillis(publishTimeMillis);
        originalChartDetail.setVersionAsPrimitive(i);
        return originalChartDetail;
    }

    private Media getMediaInstance(int i) {
        Genre genre = new Genre();

        MediaFile mediaFile = new MediaFile();
        mediaFile.setI(1);
        mediaFile.setFilename("Some filename");

        Artist artist = new Artist();
        artist.setI(1);
        artist.setName("Some artist name");

        Media media = new Media();
        media.setGenre(genre);
        media.setArtist(artist);
        media.setImageFileSmall(mediaFile);
        media.setAudioFile(mediaFile);
        media.setImageFIleLarge(mediaFile);
        media.setHeaderFile(mediaFile);
        media.setI(i);
        return media;
    }
}