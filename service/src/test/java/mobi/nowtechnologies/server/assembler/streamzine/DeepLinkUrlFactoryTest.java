package mobi.nowtechnologies.server.assembler.streamzine;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.*;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.NewsType;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/21/14
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
        String o = deepLinkUrlFactory.create(informationDeeplinkInfo, "hl_uk");
        assertThat(o, is("hl_uk://page/about"));
    }

    @Test
    public void checkCreateLinkValueForInformationAppPageWithAction() throws Exception {
        //prepare data
        InformationDeeplinkInfo informationDeeplinkInfo = new NotificationDeeplinkInfo(LinkLocationType.INTERNAL_AD, "account");
        informationDeeplinkInfo.setAction("subscribe");

        //check
        Object o = deepLinkUrlFactory.create(informationDeeplinkInfo, "hl_uk");

        assertThat(o, instanceOf(String.class));
        assertThat((String)o, is("hl_uk://page/account?action=subscribe"));
    }

    @Test
    public void checkCreateLinkValueForInformationWebPage() throws Exception {
        //prepare data
        InformationDeeplinkInfo informationDeeplinkInfo = new NotificationDeeplinkInfo(LinkLocationType.EXTERNAL_AD, "http://bear.ru");

        //check
        Object o = deepLinkUrlFactory.create(informationDeeplinkInfo, "hl_uk");

        assertThat(o, instanceOf(String.class));
        assertThat((String)o, is("hl_uk://web/aHR0cDovL2JlYXIucnU="));
    }

    @Test
    public void checkCreateLinkValueForMusicTrack() throws Exception {
        //prepare data
        Media media = getMedia(10, "TRACK-10");
        MusicTrackDeeplinkInfo musicTrackDeeplinkInfo = new MusicTrackDeeplinkInfo(media);

        when(deepLinkInfoService.getSubType(musicTrackDeeplinkInfo)).thenReturn((Enum) MusicType.TRACK);

        //check
        Object o = deepLinkUrlFactory.create(musicTrackDeeplinkInfo, "hl_uk");

        assertThat(o, instanceOf(String.class));
        assertThat((String)o, is("mq-app://content/track?id=TRACK-10_null"));
    }

    @Test
    public void checkCreateLinkValueForMusicPlayList() throws Exception {
        //prepare data
        MusicPlayListDeeplinkInfo musicPlayListDeeplinkInfo =  new MusicPlayListDeeplinkInfo(ChartType.HOT_TRACKS);

        when(deepLinkInfoService.getSubType(musicPlayListDeeplinkInfo)).thenReturn((Enum) MusicType.PLAYLIST);

        //check
        String o = deepLinkUrlFactory.create(musicPlayListDeeplinkInfo, "hl_uk");
        assertThat(o, is("hl_uk://content/playlist?id=HOT_TRACKS"));
    }

    @Test
    public void checkCreateLinkValueForNewsStory() throws Exception {
        //prepare data
        Message message = new Message();
        message.setId(10);
        message.setMessageType(NewsDetailDto.MessageType.NEWS);
        NewsStoryDeeplinkInfo newsStoryDeeplinkInfo = new NewsStoryDeeplinkInfo(message);

        when(deepLinkInfoService.getSubType(newsStoryDeeplinkInfo)).thenReturn((Enum) NewsType.STORY);

        //check
        String o = deepLinkUrlFactory.create(newsStoryDeeplinkInfo, "hl_uk");
        assertThat(o, is("hl_uk://content/story?id=10"));
    }

    @Test
    public void checkCreateLinkValueForNewsList() throws Exception {
        //prepare data
        int time = 1419120000;
        Date date = new Date(time);
        NewsListDeeplinkInfo newsListDeeplinkInfo = new NewsListDeeplinkInfo(date);
        when(deepLinkInfoService.getSubType(newsListDeeplinkInfo)).thenReturn((Enum) NewsType.LIST);

        //check
        String o = deepLinkUrlFactory.create(newsListDeeplinkInfo, "hl_uk");
        assertThat(o, is("hl_uk://content/news?id=1419120000"));
    }

    private Media getMedia(Integer id, String isrc) {
        Media media = new Media();
        media.setI(id);
        media.setIsrc(isrc);
        return media;
    }
}
