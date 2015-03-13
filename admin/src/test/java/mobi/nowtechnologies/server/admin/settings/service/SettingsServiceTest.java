package mobi.nowtechnologies.server.admin.settings.service;

import mobi.nowtechnologies.server.admin.settings.asm.dto.SettingsDto;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Duration;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfig;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfigType;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehavior;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartUserStatusBehavior;
import mobi.nowtechnologies.server.persistence.domain.behavior.CommunityConfig;
import mobi.nowtechnologies.server.persistence.domain.behavior.ContentUserStatusBehavior;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.behavior.BehaviorConfigRepository;
import mobi.nowtechnologies.server.persistence.repository.behavior.ChartUserStatusBehaviorRepository;
import mobi.nowtechnologies.server.persistence.repository.behavior.CommunityConfigRepository;
import mobi.nowtechnologies.server.service.behavior.BehaviorInfoService;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import static mobi.nowtechnologies.server.dto.context.ContentBehaviorType.DISABLED;
import static mobi.nowtechnologies.server.dto.context.ContentBehaviorType.ENABLED;

import org.junit.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@Ignore
public class SettingsServiceTest {

    @InjectMocks
    SettingsService settingsService;
    @Mock
    CommunityRepository communityRepository;
    @Mock
    ChartUserStatusBehaviorRepository chartUserStatusBehaviorRepository;
    @Mock
    CommunityConfigRepository communityConfigRepository;
    @Mock
    BehaviorConfigRepository behaviorConfigRepository;
    @Mock
    BehaviorInfoService behaviorInfoService;

    String communityUrl = "url";
    int communityId = 17;

    Community c = mock(Community.class);
    CommunityConfig communityConfig = mock(CommunityConfig.class);
    BehaviorConfig defaultBehaviorConfig = mock(BehaviorConfig.class);
    BehaviorConfig freemiumBehaviorConfig = mock(BehaviorConfig.class);

    ChartBehavior normalChartBehavior = mock(ChartBehavior.class);
    ChartBehavior previewChartBehavior = mock(ChartBehavior.class);
    ChartBehavior shuffleChartBehavior = mock(ChartBehavior.class);
    ContentUserStatusBehavior freeTrialContentUserStatusBehavior = mock(ContentUserStatusBehavior.class);
    ContentUserStatusBehavior subscribedContentUserStatusBehavior = mock(ContentUserStatusBehavior.class);
    ContentUserStatusBehavior limitedContentUserStatusBehavior = mock(ContentUserStatusBehavior.class);

    ArgumentCaptor<Integer> intCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);

    ChartUserStatusBehavior chartUserStatusBehavior = mock(ChartUserStatusBehavior.class);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // repositories
        when(communityRepository.findByRewriteUrlParameter(communityUrl)).thenReturn(c);
        when(c.getId()).thenReturn(communityId);
        when(communityConfigRepository.findByCommunity(c)).thenReturn(communityConfig);
        when(behaviorConfigRepository.findByCommunityIdAndBehaviorConfigType(communityId, BehaviorConfigType.DEFAULT)).thenReturn(defaultBehaviorConfig);
        when(behaviorConfigRepository.findByCommunityIdAndBehaviorConfigType(communityId, BehaviorConfigType.FREEMIUM)).thenReturn(freemiumBehaviorConfig);

        // freemium config
        when(freemiumBehaviorConfig.getChartBehavior(ChartBehaviorType.NORMAL)).thenReturn(normalChartBehavior);
        when(freemiumBehaviorConfig.getChartBehavior(ChartBehaviorType.PREVIEW)).thenReturn(previewChartBehavior);
        when(freemiumBehaviorConfig.getChartBehavior(ChartBehaviorType.SHUFFLED)).thenReturn(shuffleChartBehavior);
        when(freemiumBehaviorConfig.getContentUserStatusBehavior(UserStatusType.FREE_TRIAL)).thenReturn(freeTrialContentUserStatusBehavior);
        when(freemiumBehaviorConfig.getContentUserStatusBehavior(UserStatusType.SUBSCRIBED)).thenReturn(subscribedContentUserStatusBehavior);
        when(freemiumBehaviorConfig.getContentUserStatusBehavior(UserStatusType.LIMITED)).thenReturn(limitedContentUserStatusBehavior);

    }

    @Test
    public void testMakeImportForFreemiumToDefault() throws Exception {
        // given
        when(communityConfig.getBehaviorConfig()).thenReturn(freemiumBehaviorConfig);
        when(communityConfig.requiresBehaviorConfigChange(BehaviorConfigType.DEFAULT)).thenReturn(true);
        //
        // when
        //
        SettingsDto dto = new SettingsDto(BehaviorConfigType.FREEMIUM);
        // referrals
        dto.getReferralDto().setRequired(1);
        dto.getReferralDto().getDurationInfoDto().fromDuration(Duration.forPeriod(2, DurationUnit.WEEKS));
        // playlist type
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.NORMAL).setPlayTrackSeconds(3);
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.NORMAL).setOffline(true);
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.NORMAL).getSkipTracks().setNumber(4);
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.NORMAL).getSkipTracks().getDurationInfoDto().fromDuration(Duration.forPeriod(5, DurationUnit.SECONDS));
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.NORMAL).getMaxTracks().setNumber(6);
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.NORMAL).getMaxTracks().getDurationInfoDto().fromDuration(Duration.forPeriod(7, DurationUnit.MINUTES));
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.PREVIEW).setPlayTrackSeconds(8);
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.PREVIEW).setOffline(true);
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.PREVIEW).getSkipTracks().setNumber(9);
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.PREVIEW).getSkipTracks().getDurationInfoDto().fromDuration(Duration.forPeriod(10, DurationUnit.HOURS));
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.PREVIEW).getMaxTracks().setNumber(11);
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.PREVIEW).getMaxTracks().getDurationInfoDto().fromDuration(Duration.forPeriod(12, DurationUnit.DAYS));
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.SHUFFLED).setPlayTrackSeconds(13);
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.SHUFFLED).setOffline(true);
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.SHUFFLED).getSkipTracks().setNumber(14);
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.SHUFFLED).getSkipTracks().getDurationInfoDto().fromDuration(Duration.forPeriod(15, DurationUnit.WEEKS));
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.SHUFFLED).getMaxTracks().setNumber(16);
        dto.getPlaylistTypeSettings().get(ChartBehaviorType.SHUFFLED).getMaxTracks().getDurationInfoDto().fromDuration(Duration.forPeriod(17, DurationUnit.MONTHS));

        dto.getFavourites().put(UserStatusType.FREE_TRIAL, DISABLED);
        dto.getFavourites().put(UserStatusType.LIMITED, DISABLED);
        dto.getFavourites().put(UserStatusType.SUBSCRIBED, DISABLED);
        dto.getAds().put(UserStatusType.FREE_TRIAL, ENABLED);
        dto.getAds().put(UserStatusType.LIMITED, ENABLED);
        dto.getAds().put(UserStatusType.SUBSCRIBED, ENABLED);

        createEmptyPlaceholdersForChartId(90, dto);
        for (UserStatusType userStatusType : UserStatusType.values()) {
            for (ChartBehaviorType chartBehaviorType : ChartBehaviorType.values()) {
                //                dto.getPlaylistSettings().get(90).get(userStatusType).get(chartBehaviorType).setLocked( userStatusType==UserStatusType.SUBSCRIBED );
                //                dto.getPlaylistSettings().get(90).get(userStatusType).get(chartBehaviorType).setAction("action-" + chartBehaviorType + "-" + userStatusType);
            }
        }

        //
        // invoke
        //
        settingsService.makeImport(communityUrl, dto);

        // then

        //
        // Assertions for Referrals
        //
        verify(communityConfig).requiresBehaviorConfigChange(BehaviorConfigType.DEFAULT);
        verify(behaviorConfigRepository).findByCommunityIdAndBehaviorConfigType(communityId, BehaviorConfigType.DEFAULT);
        verify(behaviorConfigRepository).findByCommunityIdAndBehaviorConfigType(communityId, BehaviorConfigType.FREEMIUM);
        verify(communityConfig).setBehaviorConfig(defaultBehaviorConfig);

        verify(freemiumBehaviorConfig).updateReferralsInfo(intCaptor.capture(), durationCaptor.capture());
        assertEquals(1, intCaptor.getValue().intValue());
        assertEquals(Duration.forPeriod(2, DurationUnit.WEEKS), durationCaptor.getValue());

        //
        // Assertions for playlist type behavior
        //
        verify(normalChartBehavior).setOffline(true);

        verify(previewChartBehavior).setPlayTracksSeconds(8);
        verify(previewChartBehavior).setOffline(true);

        verify(shuffleChartBehavior).setOffline(true);
        verify(shuffleChartBehavior).updateSkipTracksInfo(intCaptor.capture(), durationCaptor.capture());
        assertEquals(14, intCaptor.getValue().intValue());
        assertEquals(Duration.forPeriod(15, DurationUnit.WEEKS), durationCaptor.getValue());
        verify(shuffleChartBehavior).updateMaxTracksInfo(intCaptor.capture(), durationCaptor.capture());
        assertEquals(16, intCaptor.getValue().intValue());
        assertEquals(Duration.forPeriod(17, DurationUnit.MONTHS), durationCaptor.getValue());

        //
        // Assertions for content behavior per user status type
        //
        verify(limitedContentUserStatusBehavior).setFavoritesOff(true);
        verify(freeTrialContentUserStatusBehavior).setFavoritesOff(true);
        verify(subscribedContentUserStatusBehavior).setFavoritesOff(true);
        verify(limitedContentUserStatusBehavior).setAddsOff(false);
        verify(freeTrialContentUserStatusBehavior).setAddsOff(false);
        verify(subscribedContentUserStatusBehavior).setAddsOff(false);

        verify(chartUserStatusBehavior).setAction("action-" + ChartBehaviorType.PREVIEW + "-" + UserStatusType.FREE_TRIAL);
        verify(chartUserStatusBehavior).setAction("action-" + ChartBehaviorType.PREVIEW + "-" + UserStatusType.SUBSCRIBED);
        verify(chartUserStatusBehavior).setAction("action-" + ChartBehaviorType.PREVIEW + "-" + UserStatusType.LIMITED);
        verify(chartUserStatusBehavior).setAction("action-" + ChartBehaviorType.SHUFFLED + "-" + UserStatusType.FREE_TRIAL);
        verify(chartUserStatusBehavior).setAction("action-" + ChartBehaviorType.SHUFFLED + "-" + UserStatusType.SUBSCRIBED);
        verify(chartUserStatusBehavior).setAction("action-" + ChartBehaviorType.SHUFFLED + "-" + UserStatusType.LIMITED);
        verify(chartUserStatusBehavior).setAction("action-" + ChartBehaviorType.NORMAL + "-" + UserStatusType.FREE_TRIAL);
        verify(chartUserStatusBehavior).setAction("action-" + ChartBehaviorType.NORMAL + "-" + UserStatusType.SUBSCRIBED);
        verify(chartUserStatusBehavior).setAction("action-" + ChartBehaviorType.NORMAL + "-" + UserStatusType.LIMITED);
    }

    private void createEmptyPlaceholdersForChartId(int chartId, SettingsDto dto) {
        //        dto.getPlaylistSettings().put(chartId, Maps.<UserStatusType, Map<ChartBehaviorType, PlaylistInfo>>newHashMap());
        //        for (UserStatusType userStatusType : UserStatusType.values()) {
        //            HashMap<ChartBehaviorType, PlaylistInfo> playlistInfos = new HashMap<ChartBehaviorType, PlaylistInfo>();
        //            for (ChartBehaviorType chartBehaviorType : ChartBehaviorType.values()) {
        //                playlistInfos.put(chartBehaviorType, new PlaylistInfo());
        //            }
        //            dto.getPlaylistSettings().get(chartId).put(userStatusType, playlistInfos);
        //        }
    }
}