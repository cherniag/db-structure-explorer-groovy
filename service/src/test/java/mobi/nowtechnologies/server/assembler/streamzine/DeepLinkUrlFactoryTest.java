package mobi.nowtechnologies.server.assembler.streamzine;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.streamzine.PlayerType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.InformationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ManualCompilationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.MusicPlayListDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.MusicTrackDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NewsListDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NewsStoryDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NotificationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.NewsType;
import mobi.nowtechnologies.server.shared.enums.MessageType;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.Opener.BROWSER;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

import org.junit.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Gennadii Cherniaiev Date: 3/21/14
 */
public class DeepLinkUrlFactoryTest {

    @Mock
    private DeepLinkInfoService deepLinkInfoService;

    @InjectMocks
    private DeepLinkUrlFactory deepLinkUrlFactory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void checkCreateLinkValueForManualCompilation() throws Exception {
        //prepare data
        List<Media> medias = Lists.newArrayList();
        medias.add(getMedia(10, "TRACK-10"));
        medias.add(getMedia(20, "TRACK-20"));
        medias.add(getMedia(30, "TRACK-30"));
        ManualCompilationDeeplinkInfo manualCompilationDeeplinkInfo = new ManualCompilationDeeplinkInfo(medias);

        //check
        List<Integer> o = deepLinkUrlFactory.create(manualCompilationDeeplinkInfo);
        assertTrue(o.contains(10));
        assertTrue(o.contains(20));
        assertTrue(o.contains(30));
    }

    @Test
    public void checkCreateLinkValueForInformationAppPage() throws Exception {
        //prepare data
        InformationDeeplinkInfo informationDeeplinkInfo = new NotificationDeeplinkInfo(LinkLocationType.INTERNAL_AD, "about");

        //check
        String o = deepLinkUrlFactory.create(informationDeeplinkInfo, createCommunity("hl_uk"), false);
        assertThat(o, is("hl-uk://page/about"));
    }

    @Test
    public void checkCreateLinkValueForInformationAppPageWithAction() throws Exception {
        //prepare data
        InformationDeeplinkInfo informationDeeplinkInfo = new NotificationDeeplinkInfo(LinkLocationType.INTERNAL_AD, "account");
        informationDeeplinkInfo.setAction("subscribe");

        //check
        Object o = deepLinkUrlFactory.create(informationDeeplinkInfo, createCommunity("hl_uk"), false);

        assertThat(o, instanceOf(String.class));
        assertThat((String) o, is("hl-uk://page/account?action=subscribe"));
    }

    @Test
    public void checkCreateLinkValueForInformationWebPage() throws Exception {
        //prepare data
        InformationDeeplinkInfo informationDeeplinkInfo = new NotificationDeeplinkInfo(LinkLocationType.EXTERNAL_AD, "http://bear.ru", BROWSER);

        //check
        Object o = deepLinkUrlFactory.create(informationDeeplinkInfo, createCommunity("o2"), false);

        assertThat(o, instanceOf(String.class));
        assertThat((String) o, is("o2://web/aHR0cDovL2JlYXIucnU=?open=externally"));
    }

    @Test
    public void checkCreateLinkValueForMusicTrack() throws Exception {
        //prepare data
        Media media = getMedia(10, "TRACK-10");
        MusicTrackDeeplinkInfo musicTrackDeeplinkInfo = new MusicTrackDeeplinkInfo(media, PlayerType.REGULAR_PLAYER_ONLY);

        when(deepLinkInfoService.getSubType(musicTrackDeeplinkInfo)).thenReturn((Enum) MusicType.TRACK);

        //check
        Object o = deepLinkUrlFactory.create(musicTrackDeeplinkInfo, createCommunity("hl_uk"), false);

        assertThat(o, instanceOf(String.class));
        assertThat((String) o, is("hl-uk://content/track?id=TRACK-10_null"));
    }

    @Test
    public void checkCreateLinkValueForMusicPlayList() throws Exception {
        //prepare data
        MusicPlayListDeeplinkInfo musicPlayListDeeplinkInfo = new MusicPlayListDeeplinkInfo(666, PlayerType.REGULAR_PLAYER_ONLY);

        when(deepLinkInfoService.getSubType(musicPlayListDeeplinkInfo)).thenReturn((Enum) MusicType.PLAYLIST);

        //check
        String o = deepLinkUrlFactory.create(musicPlayListDeeplinkInfo, createCommunity("hl_uk"), false);
        assertThat(o, is("hl-uk://content/playlist?id=666"));
    }

    @Test
    public void checkCreateLinkValueForMusicPlayListAfter62() throws Exception {
        //prepare data
        MusicPlayListDeeplinkInfo musicPlayListDeeplinkInfo = new MusicPlayListDeeplinkInfo(666, PlayerType.REGULAR_PLAYER_ONLY);

        when(deepLinkInfoService.getSubType(musicPlayListDeeplinkInfo)).thenReturn((Enum) MusicType.PLAYLIST);

        //check
        String o = deepLinkUrlFactory.create(musicPlayListDeeplinkInfo, createCommunity("hl_uk"), true);
        assertThat(o, is("hl-uk://content/playlist?player=regular&id=666"));
    }

    @Test
    public void checkCreateLinkValueForNewsStory() throws Exception {
        //prepare data
        Message message = new Message();
        message.setId(10);
        message.setMessageType(MessageType.NEWS);
        NewsStoryDeeplinkInfo newsStoryDeeplinkInfo = new NewsStoryDeeplinkInfo(message);

        when(deepLinkInfoService.getSubType(newsStoryDeeplinkInfo)).thenReturn((Enum) NewsType.STORY);

        //check
        String o = deepLinkUrlFactory.create(newsStoryDeeplinkInfo, createCommunity("hl_uk"), false);
        assertThat(o, is("hl-uk://content/story?id=10"));
    }


    @Test
    public void checkCreateLinkValueForNewsList() throws Exception {
        //prepare data
        int time = 1419120000;
        Date date = new Date(time);
        NewsListDeeplinkInfo newsListDeeplinkInfo = new NewsListDeeplinkInfo(date);
        when(deepLinkInfoService.getSubType(newsListDeeplinkInfo)).thenReturn((Enum) NewsType.LIST);

        //check
        String o = deepLinkUrlFactory.create(newsListDeeplinkInfo, createCommunity("hl_uk"), false);
        assertThat(o, is("hl-uk://content/news?id=1419120000"));
    }

    @Test
    public void checkUrlLinkForFreemiumChartWhenActionIsNull() {
        String link = deepLinkUrlFactory.createForChart(createCommunity("mtv1"), 7, null);
        assertThat(link, is("mtv1://content/playlist?id=7"));
    }

    @Test
    public void checkUrlLinkForFreemiumChartWhenActionHasValue() {
        String link = deepLinkUrlFactory.createForChart(createCommunity("mtv1"), 7, "refer_a_friend");
        assertThat(link, is("mtv1://content/playlist?id=7&action=refer_a_friend"));
    }

    private Community createCommunity(String url) {
        Community c = mock(Community.class);
        when(c.getRewriteUrlParameter()).thenReturn(url);
        return c;
    }

    private Media getMedia(Integer id, String isrc) {
        Media media = new Media();
        media.setI(id);
        media.setIsrc(isrc);
        return media;
    }
}
