package mobi.nowtechnologies.server.transport.controller;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.dto.streamzine.DeeplinkType;
import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.*;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.GrantedToType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.Permission;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.persistence.repository.MediaRepository;
import mobi.nowtechnologies.server.persistence.repository.MessageRepository;
import mobi.nowtechnologies.server.service.streamzine.StreamzineUpdateService;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import org.apache.commons.lang.time.DateUtils;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import javax.annotation.Resource;
import java.util.Date;

import static mobi.nowtechnologies.server.shared.Utils.createTimestampToken;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class GetStreamzineControllerIT extends AbstractControllerTestIT {
    @Resource
    private StreamzineUpdateService streamzineUpdateService;
    @Resource
    private MessageRepository messageRepository;
    @Resource
    private MediaRepository mediaRepository;

    @Test
    public void testGetStreamzineForAnyMQUser_Success() throws Exception {
        Date updateDate = new Date(System.currentTimeMillis() + 1000L);

        // parameters
        String userName = "test@ukr.net";
        String deviceUID = "b88106713409e92622461a876abcd74b1111";
        String apiVersion = "6.1";
        String communityUrl = "hl_uk";
        String timestamp = "" + updateDate.getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);
        User user = null;

        //
        // Expected JSON data
        //
        final String externalLink = "http://example.com";
        final String badgeUrl = "badgeUrl";
        final Message newsMessage = createNewsMessage();
        final Date publishDate = new Date();
        final ChartType chartType = ChartType.BASIC_CHART;
        final int existingTrackId = 49;
        final Media existingMedia = mediaRepository.findOne(existingTrackId);
        final String deepLinkTypeValue = DeeplinkType.DEEPLINK.name();

        prepareUpdate(updateDate, externalLink, publishDate, newsMessage, chartType, existingMedia, badgeUrl, user);

        Thread.sleep(1200L);

        // check xml format
        doRequest(userName, deviceUID, apiVersion, communityUrl, timestamp, userToken, false).andDo(print());

        // check json format and the correct order of the blocks
        ResultActions resultActions = doRequest(userName, deviceUID, apiVersion, communityUrl, timestamp, userToken, true);

        resultActions.andDo(print())
                        // check the orders
                .andExpect(jsonPath("$.response.data[0].value.visual_blocks[0].block_type", is(ShapeType.WIDE.name())))
                .andExpect(jsonPath("$.response.data[0].value.visual_blocks[1].block_type", is(ShapeType.SLIM_BANNER.name())))
                .andExpect(jsonPath("$.response.data[0].value.visual_blocks[2].block_type", is(ShapeType.NARROW.name())))
                .andExpect(jsonPath("$.response.data[0].value.visual_blocks[3].block_type", is(ShapeType.SLIM_BANNER.name())))
                .andExpect(jsonPath("$.response.data[0].value.visual_blocks[4].block_type", is(ShapeType.WIDE.name())))
                .andExpect(jsonPath("$.response.data[0].value.visual_blocks[6].block_type", is(ShapeType.SLIM_BANNER.name())))
                .andExpect(jsonPath("$.response.data[0].value.visual_blocks[5].block_type", is(ShapeType.NARROW.name())))
                        //
                .andExpect(jsonPath("$.response.data[0].value.stream_content_items[0].link_type", is(deepLinkTypeValue)))
                .andExpect(jsonPath("$.response.data[0].value.stream_content_items[0].link_value", is("mq-app://web/aHR0cDovL2V4YW1wbGUuY29t")))

                .andExpect(jsonPath("$.response.data[0].value.visual_blocks[0].access_policy.permission", is(Permission.RESTRICTED.name())))
                .andExpect(jsonPath("$.response.data[0].value.visual_blocks[0].access_policy.grantedTo", IsCollectionContaining.hasItem(GrantedToType.LIMITED.name())))
                .andExpect(jsonPath("$.response.data[0].value.visual_blocks[0].access_policy.grantedTo", IsCollectionContaining.hasItem(GrantedToType.FREETRIAL.name())))
                // .andExpect(jsonPath("$.response.data[0].value.visual_blocks[0].access_policy.grantedTo[1]", is(GrantedToType.FREETRIAL.name())))
                        //
                .andExpect(jsonPath("$.response.data[0].value.stream_content_items[1].link_type", is(deepLinkTypeValue)))
                .andExpect(jsonPath("$.response.data[0].value.stream_content_items[2].badge_icon", is(badgeUrl)))
                        //
                .andExpect(jsonPath("$.response.data[0].value.stream_content_items[2].link_type", is(deepLinkTypeValue)))
                .andExpect(jsonPath("$.response.data[0].value.stream_content_items[2].link_value", is("mq-app://page/subscription_page?action=subscribe")))
                        //
                .andExpect(jsonPath("$.response.data[0].value.stream_content_items[3].link_type", is(deepLinkTypeValue)))
                .andExpect(jsonPath("$.response.data[0].value.stream_content_items[3].link_value", is("mq-app://content/news?id=" + String.valueOf(publishDate.getTime()))))
                        //
                .andExpect(jsonPath("$.response.data[0].value.stream_content_items[4].link_type", is(deepLinkTypeValue)))
                .andExpect(jsonPath("$.response.data[0].value.stream_content_items[4].link_value", is("mq-app://content/story?id=" + String.valueOf(newsMessage.getId()))))
                        //
                .andExpect(jsonPath("$.response.data[0].value.stream_content_items[6].link_type", is(deepLinkTypeValue)))
                .andExpect(jsonPath("$.response.data[0].value.stream_content_items[6].link_value", is("mq-app://content/playlist?id=" + ChartType.BASIC_CHART.name())))
                        //
                .andExpect(jsonPath("$.response.data[0].value.stream_content_items[5].link_type", is(deepLinkTypeValue)))
                .andExpect(jsonPath("$.response.data[0].value.stream_content_items[5].link_value", is("mq-app://content/track?id=" + String.valueOf(existingMedia.getIsrc()))))
                        //
                .andExpect(jsonPath("$.response.data[0].value.stream_content_items[7].link_type", is(DeeplinkType.ID_LIST.name())))
                .andExpect(jsonPath("$.response.data[0].value.stream_content_items[7].link_value[0]", is(existingMedia.getI())));
    }

    private ResultActions doRequest(String userName, String deviceUID, String apiVersion, String communityUrl, String timestamp, String userToken, boolean isJson) throws Exception {
        final String formatSpecific = (isJson) ? ".json" : "";

        return mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_STREAMZINE" + formatSpecific)
                        .param("APP_VERSION", userName)
                        .param("COMMUNITY_NAME", communityUrl)
                        .param("API_VERSION", apiVersion)
                        .param("DEVICE_UID", deviceUID)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("WIDTHXHEIGHT", "320x800")
        ).andExpect(status().isOk())
                ;
    }


    @Test
     public void testGetStreamzineForSpecificMQUser_Success() throws Exception {
        final Date updateDate = new Date(System.currentTimeMillis() + 1000L);

        // parameters
        String userName = "test@ukr.net";
        String deviceUID = "b88106713409e92622461a876abcd74b1111";
        String apiVersion = "6.1";
        String communityUrl = "hl_uk";
        String timestamp = "" + updateDate.getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);
        User user = null;
        //
        // Expected JSON data
        //
        final String externalLink = "http://example.com";
        final String badgeUrl = "badgeUrl";
        final Message newsMessage = createNewsMessage();
        final Date publishDate = new Date();
        final ChartType chartType = ChartType.BASIC_CHART;
        final int existingTrackId = 49;
        final Media existingMedia = mediaRepository.findOne(existingTrackId);

        prepareUpdate(updateDate, externalLink, publishDate, newsMessage, chartType, existingMedia, badgeUrl, user);

        Thread.sleep(1200L);

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_STREAMZINE.json")
                        .param("APP_VERSION", userName)
                        .param("COMMUNITY_NAME", communityUrl)
                        .param("API_VERSION", apiVersion)
                        .param("DEVICE_UID", deviceUID)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("WIDTHXHEIGHT", "320x800")
                        .param("TIMESTAMP", timestamp)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.response.data[0].value.updated").value(updateDate.getTime()));

        user = userRepository.findOne(userName, communityUrl);
        final Date updateDateForSpecificUser = new Date(System.currentTimeMillis() + 1000L);
        prepareUpdate(updateDateForSpecificUser, externalLink, publishDate, newsMessage, chartType, existingMedia, badgeUrl, user);

        Thread.sleep(2000L);

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_STREAMZINE.json")
                        .param("APP_VERSION", userName)
                        .param("COMMUNITY_NAME", communityUrl)
                        .param("API_VERSION", apiVersion)
                        .param("DEVICE_UID", deviceUID)
                        .param("WIDTHXHEIGHT", "320x800")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.response.data[0].value.updated").value(updateDateForSpecificUser.getTime()));
    }

    @Test
    public void testGetStreamzineForSeveralMQUsers_Success() throws Exception {
        final Date updateDatePast = new Date(System.currentTimeMillis() + 1000L);
        final Date updateDateFuture = new Date(System.currentTimeMillis() + 10000L);

        // parameters
        String userName1 = "test@ukr.net";
        String userName2 = "dnepr@i.ua";
        String userName3 = "mq@mq.com";
        String apiVersion = "6.1";
        String appVersion = "1.0";
        String communityUrl = "hl_uk";
        String timestamp = System.currentTimeMillis() + "";
        User user1 = userRepository.findOne(userName1, communityUrl);
        User user2 = userRepository.findOne(userName2, communityUrl);
        User user3 = userRepository.findOne(userName3, communityUrl);
        //
        // Expected JSON data
        //
        final String externalLink = "http://example.com";
        final String badgeUrl = "badgeUrl";
        final Message newsMessage = createNewsMessage();
        final Date publishDate = new Date();
        final ChartType chartType = ChartType.BASIC_CHART;
        final int existingTrackId = 49;
        final Media existingMedia = mediaRepository.findOne(existingTrackId);

        prepareUpdate(updateDatePast, externalLink, publishDate, newsMessage, chartType, existingMedia, badgeUrl, user1, user2);
        prepareUpdate(updateDateFuture, externalLink, publishDate, newsMessage, chartType, existingMedia, badgeUrl, user1, user2);

        Thread.sleep(1200L);

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion +"/GET_STREAMZINE.json")
                        .param("APP_VERSION", appVersion)
                        .param("COMMUNITY_NAME", communityUrl)
                        .param("API_VERSION", apiVersion)
                        .param("DEVICE_UID", user1.getDeviceUID())
                        .param("USER_NAME", userName1)
                        .param("USER_TOKEN", createTimestampToken(user1.getToken(), timestamp))
                        .param("WIDTHXHEIGHT", "320x800")
                        .param("TIMESTAMP", timestamp)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.response.data[0].value.updated").value(updateDateFuture.getTime()));

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion +"/GET_STREAMZINE.json")
                        .param("APP_VERSION", appVersion)
                        .param("COMMUNITY_NAME", communityUrl)
                        .param("API_VERSION", apiVersion)
                        .param("DEVICE_UID", user2.getDeviceUID())
                        .param("USER_NAME", userName2)
                        .param("WIDTHXHEIGHT", "320x800")
                        .param("USER_TOKEN",  createTimestampToken(user2.getToken(), timestamp))
                        .param("TIMESTAMP", timestamp)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.response.data[0].value.updated").value(updateDateFuture.getTime()));

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion +"/GET_STREAMZINE.json")
                        .param("APP_VERSION", appVersion)
                        .param("COMMUNITY_NAME", communityUrl)
                        .param("API_VERSION", apiVersion)
                        .param("DEVICE_UID", user3.getDeviceUID())
                        .param("USER_NAME", userName3)
                        .param("WIDTHXHEIGHT", "320x800")
                        .param("USER_TOKEN",  createTimestampToken(user3.getToken(), timestamp))
                        .param("TIMESTAMP", timestamp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.data[0].value.updated").value(updateDatePast.getTime()));
    }
    @Test
    public void testGetStreamzineForO2User_Fail() throws Exception {
        Date now = new Date();
        final Date updateDate = DateUtils.addDays(now, 1);
        final Date futureDate = DateUtils.addDays(updateDate, 1);

        // parameters
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "6.1";
        String communityUrl = "o2";
        String timestamp = "" + futureDate.getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_STREAMZINE.json")
                        .param("APP_VERSION", userName)
                        .param("COMMUNITY_NAME", communityUrl)
                        .param("API_VERSION", apiVersion)
                        .param("DEVICE_UID", deviceUID)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)).
                andExpect(status().isNotFound());
    }

    private void prepareUpdate(Date updateDate, String externalLink, Date publishDate, Message newsMessage, ChartType chartType, Media track, String badgeUrl, User... users) {
        Update update = streamzineUpdateService.create(updateDate);

        // simulate adding blocks
        Update incomingWithBlocks = createWithBlocks(externalLink, publishDate, newsMessage, chartType, track, badgeUrl, users);
        streamzineUpdateService.update(update.getId(), incomingWithBlocks);
    }

    private Update createWithBlocks(String externalLink, Date publishDate, Message newsMessage, ChartType chartType, Media track, String badgeUrl, User... users) {
        Update u = new Update(DateUtils.addDays(new Date(), 1));
        //
        // Not included block
        //
        Block excludedAdd = newBlock(0, ShapeType.NARROW, createMusicTrackDeeplink(track), badgeUrl);
        excludedAdd.exclude();
        u.addBlock(excludedAdd);
        //
        // Ordinal blocks
        //
        u.addBlock(newBlock(1, ShapeType.WIDE, createNotificationDeeplink(externalLink), badgeUrl));
        u.addBlock(newBlock(2, ShapeType.SLIM_BANNER, createNotificationDeeplink0(), badgeUrl));
        u.addBlock(newBlock(3, ShapeType.NARROW, createNotificationDeeplink1(), badgeUrl));
        u.addBlock(newBlock(4, ShapeType.SLIM_BANNER, createNewsListDeeplink(publishDate), badgeUrl));
        u.addBlock(newBlock(5, ShapeType.WIDE, createNewsStoryDeeplink(newsMessage), badgeUrl));
        //
        // Added mixed positions to test that values are added according to positions: 5 and 6
        //
        u.addBlock(newBlock(7, ShapeType.SLIM_BANNER, createMusicPlaylistDeeplink(chartType), badgeUrl));
        u.addBlock(newBlock(8, ShapeType.SLIM_BANNER, createManualCompilationDeeplink(track), badgeUrl));
        u.addBlock(newBlock(6, ShapeType.NARROW, createMusicTrackDeeplink(track), badgeUrl));

        if(users != null){
            for (User user : users) {
                u.addUser(user);
            }
        }

        return u;
    }

    private DeeplinkInfo createManualCompilationDeeplink(Media one) {
        ManualCompilationDeeplinkInfo d = new ManualCompilationDeeplinkInfo(Lists.newArrayList(one));
        return d;
    }

    private DeeplinkInfo createNotificationDeeplink(String url) {
        NotificationDeeplinkInfo d = new NotificationDeeplinkInfo(LinkLocationType.EXTERNAL_AD, url);
        return d;
    }

    private DeeplinkInfo createNotificationDeeplink0() {
        DeeplinkInfo d = new NotificationDeeplinkInfo(LinkLocationType.INTERNAL_AD, "subscription_page");
        return d;
    }

    private DeeplinkInfo createNotificationDeeplink1() {
        NotificationDeeplinkInfo d = new NotificationDeeplinkInfo(LinkLocationType.INTERNAL_AD, "subscription_page");
        d.setAction("subscribe");
        return d;
    }

    private DeeplinkInfo createNewsListDeeplink(Date publishDate) {
        NewsListDeeplinkInfo d = new NewsListDeeplinkInfo(publishDate);
        return d;
    }

    private DeeplinkInfo createNewsStoryDeeplink(Message message) {
        NewsStoryDeeplinkInfo d = new NewsStoryDeeplinkInfo(message);
        return d;
    }

    private Message createNewsMessage() {
        Message newsStory = new Message();
        newsStory.setTitle("title");
        newsStory.setActivated(true);
        newsStory.setBody("very interesting new story");
        newsStory.setPosition(1);
        newsStory.setPublishTimeMillis(new Date().getTime());
        newsStory.setMessageType(NewsDetailDto.MessageType.NEWS);
        newsStory.setCommunity(CommunityDao.getCommunity("hl_uk"));
        return messageRepository.saveAndFlush(newsStory);
    }

    private DeeplinkInfo createMusicPlaylistDeeplink(ChartType chartType) {
        MusicPlayListDeeplinkInfo d = new MusicPlayListDeeplinkInfo(chartType);
        return d;
    }

    private DeeplinkInfo createMusicTrackDeeplink(Media media) {
        MusicTrackDeeplinkInfo d = new MusicTrackDeeplinkInfo(media);
        return d;
    }

    private Block newBlock(int position, ShapeType shapeType, DeeplinkInfo deepLinkInfo, String badgeUrl) {
        Block b = new Block(position, shapeType, deepLinkInfo);
        if(badgeUrl != null) {
            b.setBadgeUrl(badgeUrl);
        }
        b.setTitle("title");
        b.setSubTitle("sub title");
        b.setCoverUrl("image_" + System.nanoTime() + ".jpg");
        b.include();
        b.setAccessPolicy(AccessPolicy.enabledForVipOnly());
        return b;
    }
}
