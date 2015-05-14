package mobi.nowtechnologies.server.service.chart;

import mobi.nowtechnologies.server.persistence.domain.Artist;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.ChartDetailFactory;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Genre;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaFile;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import mobi.nowtechnologies.server.service.streamzine.BadgesService;
import mobi.nowtechnologies.server.shared.dto.ChartDetailDto;
import mobi.nowtechnologies.server.shared.dto.PlaylistDto;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import static mobi.nowtechnologies.common.util.TrackIdGenerator.ISRC_TRACK_ID_DELIMITER;
import static mobi.nowtechnologies.server.persistence.domain.Community.O2_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.shared.enums.ChgPosition.UNCHANGED;

import java.util.Locale;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * Author: Gennadii Cherniaiev Date: 3/11/14
 */
@RunWith(MockitoJUnitRunner.class)
public class ChartDetailsConverterTest {

    private static final String AFFILIATE_TOKEN_O2 = "1234567890";
    private static final String CAMPAIGN_TOKEN_O2 = "abcdefg";
    private static final String AFFILIATE_TOKEN_HL_UK = "at_hl";
    private static final String CAMPAIGN_TOKEN_HL_UK = "ct_hl";
    @Mock
    private CommunityResourceBundleMessageSource messageSource;
    @Mock
    private BadgesService badgesService;
    @InjectMocks
    private ChartDetailsConverter chartDetailsConverter;
    private ChartDetail chartDetail;
    private ChartDetailDto chartDetailDto;

    @Before
    public void setUp() throws Exception {
        when(messageSource.getMessage(eq(Community.O2_COMMUNITY_REWRITE_URL), eq("itunes.affiliate.token"), any(Object[].class), any(String.class), any(Locale.class))).thenReturn(AFFILIATE_TOKEN_O2);
        when(messageSource.getMessage(eq(Community.O2_COMMUNITY_REWRITE_URL), eq("itunes.campaign.token"), any(Object[].class), any(String.class), any(Locale.class))).thenReturn(CAMPAIGN_TOKEN_O2);
        when(messageSource.getMessage(eq(Community.HL_COMMUNITY_REWRITE_URL), eq("itunes.affiliate.token"), any(Object[].class), any(String.class), any(Locale.class)))
            .thenReturn(AFFILIATE_TOKEN_HL_UK);
        when(messageSource.getMessage(eq(Community.HL_COMMUNITY_REWRITE_URL), eq("itunes.campaign.token"), any(Object[].class), any(String.class), any(Locale.class))).thenReturn(CAMPAIGN_TOKEN_HL_UK);
        when(messageSource.getMessage(Community.O2_COMMUNITY_REWRITE_URL, "itunes.urlCountryCode", null, null)).thenReturn("GB");
        when(messageSource.getMessage(Community.VF_NZ_COMMUNITY_REWRITE_URL, "itunes.urlCountryCode", null, null)).thenReturn("NZ");
        when(messageSource.getMessage(Community.HL_COMMUNITY_REWRITE_URL, "itunes.urlCountryCode", null, null)).thenReturn("GB");
    }


    @Test
    public void testToChartDetailDtoWithOldLinkBeforeCutover() throws Exception {
        ChartDetail chartDetail = prepareChartDetail();
        chartDetail.getMedia().setiTunesUrl("http://clkuk.tradedoubler.com/click?p=23708%26a=1997010%26url=http://itunes.apple.com/gb/album/monster/id440880917?i=440880925%26uo=4%26partnerId=2003");
        Community community = getCommunity("o2");
        chartDetailsConverter.setiTunesLinkFormatCutoverTimeMillis(System.currentTimeMillis() + 15000L);
        ChartDetailDto chartDetailDto = chartDetailsConverter.toChartDetailDto(chartDetail, community, "");
        MatcherAssert.assertThat(chartDetailDto.getiTunesUrl(), is("http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%26a%3D1997010%26url%3Dhttp%3A%2F%2Fitunes.apple" +
                                                                   ".com%2FGB%2Falbum%2Fmonster%2Fid440880917%3Fi%3D440880925%26uo%3D4%26partnerId%3D2003"));
    }

    @Test
    public void testToChartDetailDtoWithNewLinkBeforeCutover() throws Exception {
        ChartDetail chartDetail = prepareChartDetail();
        chartDetail.getMedia().setiTunesUrl("http://itunes.apple.com/gb/album/monster/id440880917?i=440880925%26uo=4");
        Community community = getCommunity("o2");
        chartDetailsConverter.setiTunesLinkFormatCutoverTimeMillis(System.currentTimeMillis() + 15000L);
        ChartDetailDto chartDetailDto = chartDetailsConverter.toChartDetailDto(chartDetail, community, "");
        MatcherAssert.assertThat(chartDetailDto.getiTunesUrl(), is("http://itunes.apple.com/gb/album/monster/id440880917?i=440880925%26uo=4"));
    }

    @Test
    public void testToChartDetailDtoWithOldLinkAfterCutover() throws Exception {
        ChartDetail chartDetail = prepareChartDetail();
        chartDetail.getMedia().setiTunesUrl("http://clkuk.tradedoubler.com/click?p=23708%26a=1997010%26url=http://itunes.apple.com/gb/album/monster/id440880917?i=440880925%26uo=4%26partnerId=2003");
        Community community = getCommunity("o2");
        chartDetailsConverter.setiTunesLinkFormatCutoverTimeMillis(System.currentTimeMillis() - 15000L);
        ChartDetailDto chartDetailDto = chartDetailsConverter.toChartDetailDto(chartDetail, community, "");
        MatcherAssert.assertThat(chartDetailDto.getiTunesUrl(),
                                 is("http%3A%2F%2Fitunes.apple.com%2FGB%2Falbum%2Fmonster%2Fid440880917%3Fi%3D440880925%26uo%3D4%26at%3D" + AFFILIATE_TOKEN_O2 + "%26ct%3D" + CAMPAIGN_TOKEN_O2));
    }

    @Test
    public void testToChartDetailDtoWithOldLinkAfterCutoverHL() throws Exception {
        ChartDetail chartDetail = prepareChartDetail();
        chartDetail.getMedia().setiTunesUrl("http://clkuk.tradedoubler.com/click?p=23708%26a=1997010%26url=http://itunes.apple.com/fr/album/monster/id440880917?i=440880925%26uo=4%26partnerId=2003");
        Community community = getCommunity("hl_uk");
        chartDetailsConverter.setiTunesLinkFormatCutoverTimeMillis(System.currentTimeMillis() - 15000L);
        ChartDetailDto chartDetailDto = chartDetailsConverter.toChartDetailDto(chartDetail, community, "");
        MatcherAssert.assertThat(chartDetailDto.getiTunesUrl(),
                                 is("http%3A%2F%2Fitunes.apple.com%2FGB%2Falbum%2Fmonster%2Fid440880917%3Fi%3D440880925%26uo%3D4%26at%3D" + AFFILIATE_TOKEN_HL_UK + "%26ct%3D" + CAMPAIGN_TOKEN_HL_UK));
    }


    @Test
    public void testToChartDetailDtoWithNewLinkAfterCutover() throws Exception {
        ChartDetail chartDetail = prepareChartDetail();
        chartDetail.getMedia().setiTunesUrl("http://itunes.apple.com/gb/album/monster/id440880917?i=440880925%26uo=4");
        Community community = getCommunity("o2");
        chartDetailsConverter.setiTunesLinkFormatCutoverTimeMillis(System.currentTimeMillis() - 15000L);
        ChartDetailDto chartDetailDto = chartDetailsConverter.toChartDetailDto(chartDetail, community, "");
        MatcherAssert.assertThat(chartDetailDto.getiTunesUrl(),
                                 is("http%3A%2F%2Fitunes.apple.com%2FGB%2Falbum%2Fmonster%2Fid440880917%3Fi%3D440880925%26uo%3D4%26at%3D" + AFFILIATE_TOKEN_O2 + "%26ct%3D" + CAMPAIGN_TOKEN_O2));
    }

    @Test
    public void testToChartDetailDtoWithNewLinkAfterCutoverHL() throws Exception {
        ChartDetail chartDetail = prepareChartDetail();
        chartDetail.getMedia().setiTunesUrl("http://itunes.apple.com/au/album/monster/id440880917?i=440880925%26uo=4");
        Community community = getCommunity("hl_uk");
        chartDetailsConverter.setiTunesLinkFormatCutoverTimeMillis(System.currentTimeMillis() - 15000L);
        ChartDetailDto chartDetailDto = chartDetailsConverter.toChartDetailDto(chartDetail, community, "");
        MatcherAssert.assertThat(chartDetailDto.getiTunesUrl(),
                                 is("http%3A%2F%2Fitunes.apple.com%2FGB%2Falbum%2Fmonster%2Fid440880917%3Fi%3D440880925%26uo%3D4%26at%3D" + AFFILIATE_TOKEN_HL_UK + "%26ct%3D" + CAMPAIGN_TOKEN_HL_UK));
    }

    @Test
    public void testToChartDetailDtoWithNewLinkAfterCutoverAndAbsentCountry() throws Exception {
        ChartDetail chartDetail = prepareChartDetail();
        chartDetail.getMedia().setiTunesUrl("http://itunes.apple.com/gb/album/monster/id440880917?i=440880925%26uo=4");
        Community community = getCommunity("country");
        chartDetailsConverter.setiTunesLinkFormatCutoverTimeMillis(System.currentTimeMillis() - 15000L);
        ChartDetailDto chartDetailDto = chartDetailsConverter.toChartDetailDto(chartDetail, community, "");
        MatcherAssert.assertThat(chartDetailDto.getiTunesUrl(), is("http://itunes.apple.com/gb/album/monster/id440880917?i=440880925%26uo=4"));
    }

    @Test
    public void shouldConvertToChartDetailDto() throws Exception {
        chartDetailsConverter.setiTunesLinkFormatCutoverTimeMillis(System.currentTimeMillis() + 15000L);
        //given
        String iTunesUrl = "http://clkuk.tradedoubler.com/click?p=23708%26a=1997010%26url=https://itunes.apple.com/gb/album/inhaler/id573269843?i=573269988%26uo=4%26partnerId=2003";
        chartDetail = new ChartDetail().withChgPosition(UNCHANGED).withPrevPosition(Byte.MAX_VALUE).withChart(new Chart().withI(Integer.MAX_VALUE).withGenre(new Genre())).withMedia(
            new Media().withITunesUrl(iTunesUrl).withGenre(new Genre()).withImageFileLarge(new MediaFile()).withImageFileSmall(new MediaFile()).withAudioFile(new MediaFile()).withArtist(new Artist()));
        Community community = new Community().withRewriteUrl(O2_COMMUNITY_REWRITE_URL);

        //when
        chartDetailDto = chartDetailsConverter.toChartDetailDto(chartDetail, community, "https://m.7digital.com/NZ/releases/1425249#t15720039?partner=3734");

        //then
        shouldConvertToChartDetailDtoSuccessfully();
    }

    @Test
    public void testToPlaylistDto_Success() throws Exception {
        ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
        Chart chart = chartDetail.getChart();

        PlaylistDto result = chartDetailsConverter.toPlaylistDto(chartDetail, null, null, true, false);

        assertNotNull(result);
        assertEquals(chartDetail.getTitle(), result.getPlaylistTitle());
        assertEquals(chart.getI().byteValue(), result.getId().byteValue());
        assertEquals(chartDetail.getImageFileName(), result.getImage());
        assertEquals(chartDetail.getSubtitle(), result.getSubtitle());
        assertEquals(chartDetail.getPosition(), result.getPosition().byteValue());
        assertEquals(chartDetail.getImageTitle(), result.getImageTitle());
        assertEquals(chartDetail.getInfo(), result.getDescription());
        assertEquals(true, result.getSwitchable());
        assertEquals(chart.getType(), result.getType());
    }

    @Test
    public void testToPlaylistDto_NullTitle_Success() throws Exception {
        ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
        chartDetail.setTitle(null);
        Chart chart = chartDetail.getChart();

        PlaylistDto result = chartDetailsConverter.toPlaylistDto(chartDetail, null, null, false, false);

        assertNotNull(result);
        assertEquals(chart.getName(), result.getPlaylistTitle());
    }

    @Test
    public void testToPlaylistDtoWithBadge() throws Exception {
        ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
        chartDetail.setBadgeId(123L);
        Chart chart = chartDetail.getChart();
        Community community = mock(Community.class);
        Resolution resolution = mock(Resolution.class);

        when(badgesService.getBadgeFileName(123L, community, resolution)).thenReturn("badgeIconFileName");

        PlaylistDto result = chartDetailsConverter.toPlaylistDto(chartDetail, resolution, community, true, false);

        assertNotNull(result);
        assertEquals(chartDetail.getTitle(), result.getPlaylistTitle());
        assertEquals(chart.getI().byteValue(), result.getId().byteValue());
        assertEquals(chartDetail.getImageFileName(), result.getImage());
        assertEquals(chartDetail.getSubtitle(), result.getSubtitle());
        assertEquals(chartDetail.getPosition(), result.getPosition().byteValue());
        assertEquals(chartDetail.getImageTitle(), result.getImageTitle());
        assertEquals(chartDetail.getInfo(), result.getDescription());
        assertEquals(true, result.getSwitchable());
        assertEquals(chart.getType(), result.getType());
        assertEquals("badgeIconFileName", result.getBadgeIcon());
    }

    @Test
    public void testToPlaylistDtoWithChartUpdateId() throws Exception {
        final Integer updateId = 15;
        final boolean withUpdateId = true;

        ChartDetail chartDetail = mock(ChartDetail.class);
        when(chartDetail.getChart()).thenReturn(mock(Chart.class));
        when(chartDetail.getI()).thenReturn(updateId);

        PlaylistDto result = chartDetailsConverter.toPlaylistDto(chartDetail, null, null, false, withUpdateId);

        assertEquals(updateId, result.getChartUpdateId());
    }

    private void shouldConvertToChartDetailDtoSuccessfully() {
        assertThat(chartDetailDto, Is.is(notNullValue()));
        assertThat(chartDetailDto.getPosition(), Is.is(chartDetail.getPosition()));
        assertThat(chartDetailDto.getPlaylistId(), Is.is(chartDetail.getChart().getI()));
        Media media = chartDetail.getMedia();
        assertThat(chartDetailDto.getArtist(), Is.is(media.getArtistName()));
        assertThat(chartDetailDto.getAudioSize(), Is.is(media.getAudioSize()));
        assertThat(chartDetailDto.getGenre1(), Is.is(chartDetail.getChart().getGenre().getName()));
        assertThat(chartDetailDto.getGenre2(), Is.is(media.getGenre().getName()));

        int audioSize = media.getAudioSize();
        int headerSize = media.getHeaderSize();

        assertThat(chartDetailDto.getImageLargeSize(), Is.is(media.getImageLargeSize()));
        assertThat(chartDetailDto.getImageSmallSize(), Is.is(media.getImageSmallSize()));
        assertThat(chartDetailDto.getInfo(), Is.is(chartDetail.getInfo()));
        assertThat(chartDetailDto.getMedia(), Is.is(media.getIsrc() + ISRC_TRACK_ID_DELIMITER + media.getTrackId()));
        assertThat(chartDetailDto.getTitle(), Is.is(media.getTitle()));
        assertThat(chartDetailDto.getChartDetailVersion(), Is.is(chartDetail.getVersionAsPrimitive()));
        assertThat(chartDetailDto.getDuration(), Is.is(media.getAudioFile().getDuration()));
        assertThat(chartDetailDto.getAmazonUrl(), Is.is("https%3A%2F%2Fm.7digital.com%2FGB%2Freleases%2F1425249%23t15720039%3Fpartner%3D3734"));
        assertThat(chartDetailDto.getiTunesUrl(), Is.is("http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%26a%3D1997010%26url%3Dhttps%3A%2F%2Fitunes.apple" +
                                                        ".com%2FGB%2Falbum%2Finhaler%2Fid573269843%3Fi%3D573269988%26uo%3D4%26partnerId%3D2003"));
        assertThat(chartDetailDto.isArtistUrl(), Is.is(media.getAreArtistUrls()));
        assertThat(chartDetailDto.getPreviousPosition(), Is.is(chartDetail.getPrevPosition()));
        assertThat(chartDetailDto.getChangePosition(), Is.is(chartDetail.getChgPosition().getLabel()));
        assertThat(chartDetailDto.getChannel(), Is.is(chartDetail.getChannel()));
    }

    private Community getCommunity(String rewriteUrlParameter) {
        Community community = new Community();
        community.setRewriteUrlParameter(rewriteUrlParameter);
        return community;
    }

    private ChartDetail prepareChartDetail() {
        Chart chart = new Chart();
        chart.setI(1);
        Genre genre = new Genre();
        chart.setGenre(genre);
        ChartDetail chartDetail = new ChartDetail();
        Media media = new Media();
        media.setArtist(new Artist());
        media.setiTunesUrl("");
        MediaFile mediaFile = new MediaFile();
        media.setAudioFile(mediaFile);
        media.setGenre(genre);
        media.setImageFIleLarge(mediaFile);
        media.setImageFileSmall(mediaFile);
        chartDetail.setChart(chart);
        chartDetail.setMedia(media);
        chartDetail.setChgPosition(ChgPosition.UNCHANGED);
        chartDetail.setPrevPosition(Byte.valueOf((byte) 1));
        return chartDetail;
    }
}
