package mobi.nowtechnologies.server.assembler.streamzine;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.*;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/21/14
 */
public class DeepLinkUrlFactoryTest {

    private DeepLinkUrlFactory deepLinkUrlFactory = new DeepLinkUrlFactory();

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
        String o = deepLinkUrlFactory.create(informationDeeplinkInfo);
        assertThat(o, is("mq-app://page/about"));
    }

    @Test
    public void checkCreateLinkValueForInformationAppPageWithAction() throws Exception {
        //prepare data
        InformationDeeplinkInfo informationDeeplinkInfo = new NotificationDeeplinkInfo(LinkLocationType.INTERNAL_AD, "account");
        informationDeeplinkInfo.setAction("subscribe");

        //check
        Object o = deepLinkUrlFactory.create(informationDeeplinkInfo);

        assertThat(o, instanceOf(String.class));
        assertThat((String)o, is("mq-app://page/account?action=subscribe"));
    }

    @Test
    public void checkCreateLinkValueForInformationWebPage() throws Exception {
        //prepare data
        InformationDeeplinkInfo informationDeeplinkInfo = new NotificationDeeplinkInfo(LinkLocationType.EXTERNAL_AD, "http://bear.ru");

        //check
        Object o = deepLinkUrlFactory.create(informationDeeplinkInfo);

        assertThat(o, instanceOf(String.class));
        assertThat((String)o, is("mq-app://web/aHR0cDovL2JlYXIucnU="));
    }

    @Test
    public void checkCreateLinkValueForMusicTrack() throws Exception {
        //prepare data
        Media media = getMedia(10, "TRACK-10");
        MusicTrackDeeplinkInfo musicTrackDeeplinkInfo = new MusicTrackDeeplinkInfo(media);

        //check
        Object o = deepLinkUrlFactory.create(musicTrackDeeplinkInfo);

        assertThat(o, instanceOf(String.class));
        assertThat((String)o, is("mq-app://content/track?id=TRACK-10"));
    }

    @Test
    public void checkCreateLinkValueForMusicPlayList() throws Exception {
        //prepare data
        MusicPlayListDeeplinkInfo musicPlayListDeeplinkInfo =  new MusicPlayListDeeplinkInfo(ChartType.HOT_TRACKS);

        //check
        String o = deepLinkUrlFactory.create(musicPlayListDeeplinkInfo);
        assertThat(o, is("mq-app://content/playlist?id=HOT_TRACKS"));
    }

    @Test
    public void checkCreateLinkValueForNewsStory() throws Exception {
        //prepare data
        Message message = new Message();
        message.setId(10);
        message.setMessageType(NewsDetailDto.MessageType.NEWS);
        NewsStoryDeeplinkInfo newsStoryDeeplinkInfo = new NewsStoryDeeplinkInfo(message);

        //check
        String o = deepLinkUrlFactory.create(newsStoryDeeplinkInfo);
        assertThat(o, is("mq-app://content/story?id=10"));
    }

    @Test
    public void checkCreateLinkValueForNewsList() throws Exception {
        //prepare data
        int time = 1419120000;
        Date date = new Date(time);
        NewsListDeeplinkInfo newsListDeeplinkInfo = new NewsListDeeplinkInfo(date);

        //check
        String o = deepLinkUrlFactory.create(newsListDeeplinkInfo);
        assertThat(o, is("mq-app://content/news?id=1419120000"));
    }

    private Media getMedia(Integer id, String isrc) {
        Media media = new Media();
        media.setI(id);
        media.setIsrc(isrc);
        return media;
    }
}
