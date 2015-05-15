package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.dto.streamzine.DeeplinkType;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Dimensions;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.domain.streamzine.PlayerType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.BadgeMapping;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.DeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ManualCompilationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.MusicPlayListDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.MusicTrackDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NewsListDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NewsStoryDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NotificationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.Permission;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.persistence.domain.user.GrantedToType;
import mobi.nowtechnologies.server.persistence.repository.BadgeMappingRepository;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.FilenameAliasRepository;
import mobi.nowtechnologies.server.persistence.repository.MediaRepository;
import mobi.nowtechnologies.server.persistence.repository.MessageRepository;
import mobi.nowtechnologies.server.persistence.repository.ResolutionRepository;
import mobi.nowtechnologies.server.service.streamzine.StreamzineUpdateService;
import mobi.nowtechnologies.server.shared.enums.MessageType;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.Opener.BROWSER;
import static mobi.nowtechnologies.server.shared.Utils.createTimestampToken;

import javax.annotation.Resource;

import java.util.Date;

import com.google.common.collect.Lists;
import org.apache.commons.lang.time.DateUtils;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.core.IsCollectionContaining;
import static org.hamcrest.Matchers.is;

@Transactional
public class GetStreamzineControllerIT extends AbstractControllerTestIT {

    @Resource
    private StreamzineUpdateService streamzineUpdateService;
    @Resource
    private MessageRepository messageRepository;
    @Resource
    private MediaRepository mediaRepository;
    @Resource
    private CommunityRepository communityRepository;
    @Resource
    private ResolutionRepository resolutionRepository;
    @Resource
    private BadgeMappingRepository badgeMappingRepository;
    @Resource
    private FilenameAliasRepository filenameAliasRepository;

    @Test
    public void testGetStreamzineForAnyMQUser_LatestVersion() throws Exception {
        Date updateDate = new Date(System.currentTimeMillis() + 2000L);

        // parameters
        String userName = "test@ukr.net";
        String deviceUID = "b88106713409e92622461a876abcd74b1111";
        String apiVersion = LATEST_SERVER_API_VERSION;
        String communityUrl = "hl_uk";
        String timestamp = "" + updateDate.getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);
        User user = null;

        //
        // Expected JSON data
        //
        final String externalLink = "http://example.com";
        final Message newsMessage = createNewsMessage();
        final Date publishDate = new Date();
        final int chartId = 6;
        final int existingTrackId = 49;
        final Media existingMedia = mediaRepository.findOne(existingTrackId);
        final String deepLinkTypeValue = DeeplinkType.DEEPLINK.name();

        FilenameAlias originalUploadedFile = new FilenameAlias("fileName", "fileName", new Dimensions(100, 100)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        originalUploadedFile = filenameAliasRepository.saveAndFlush(originalUploadedFile);

        prepareDefaultBadge(communityUrl, originalUploadedFile);
        prepareBadge(communityUrl, "IOS", "fileName2", 50, 50, originalUploadedFile);
        FilenameAlias filenameAlias1 = prepareBadge(communityUrl, "IOS", "fileName1", 60, 60, originalUploadedFile);

        Community community = communityRepository.findByRewriteUrlParameter(communityUrl);
        prepareUpdate(updateDate, externalLink, publishDate, newsMessage, chartId, existingMedia, originalUploadedFile, community, user);

        Thread.sleep(2500L);

        // check xml format
        doRequestFrom63(userName, deviceUID, apiVersion, communityUrl, timestamp, userToken, false, "60x60", null).andExpect(status().isOk()).andDo(print());

        // check json format and the correct order of the blocks
        ResultActions resultActions = doRequestFrom63(userName, deviceUID, apiVersion, communityUrl, timestamp, userToken, true, "60x60", null).andExpect(status().isOk()).andDo(print());

        resultActions.andDo(print())
            // check the orders
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[0].block_type", is(ShapeType.WIDE.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[1].block_type", is(ShapeType.SLIM_BANNER.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[2].block_type", is(ShapeType.NARROW.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[3].block_type", is(ShapeType.SLIM_BANNER.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[4].block_type", is(ShapeType.WIDE.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[6].block_type", is(ShapeType.SLIM_BANNER.name()))).andExpect(
            jsonPath("$.response.data[0].value.visual_blocks[5].block_type", is(ShapeType.NARROW.name())))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[0].link_type", is(deepLinkTypeValue)))
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[0].link_value", is("hl-uk://web/aHR0cDovL2V4YW1wbGUuY29t?open=externally")))

            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[0].access_policy.permission", is(Permission.RESTRICTED.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[0].access_policy.grantedTo", IsCollectionContaining.hasItem(GrantedToType.LIMITED.name()))).andExpect(
            jsonPath("$.response.data[0].value.visual_blocks[0].access_policy.grantedTo", IsCollectionContaining.hasItem(GrantedToType.FREETRIAL.name())))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[1].link_type", is(deepLinkTypeValue)))
                // check badges
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[0].badge_icon", is(filenameAlias1.getFileName())))
                //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[2].link_type", is(deepLinkTypeValue))).andExpect(
            jsonPath("$.response.data[0].value.stream_content_items[2].link_value", is("hl-uk://page/subscription_page?action=subscribe")))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[3].link_type", is(deepLinkTypeValue))).andExpect(
            jsonPath("$.response.data[0].value.stream_content_items[3].link_value", is("hl-uk://content/news?id=" + publishDate.getTime())))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[4].link_type", is(deepLinkTypeValue))).andExpect(
            jsonPath("$.response.data[0].value.stream_content_items[4].link_value", is("hl-uk://content/story?id=" + newsMessage.getId())))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[6].link_type", is(deepLinkTypeValue))).andExpect(
            jsonPath("$.response.data[0].value.stream_content_items[6].link_value", is("hl-uk://content/playlist?player=" + PlayerType.MINI_PLAYER_ONLY.getId() + "&id=" + chartId)))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[5].link_type", is(deepLinkTypeValue))).andExpect(
            jsonPath("$.response.data[0].value.stream_content_items[5].link_value", is("hl-uk://content/track?player=" +
                                                                                       PlayerType.REGULAR_PLAYER_ONLY.getId() + "&id=" +
                                                                                       existingMedia.getIsrcTrackId())))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[7].link_type", is(DeeplinkType.ID_LIST.name())))
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[7].link_value[0]", is(existingMedia.getI())));
    }

    @Test
    public void testGetStreamzineForAnyMQUser_400_forBadResolution() throws Exception {
        Date updateDate = new Date(System.currentTimeMillis() + 1000L);

        // parameters
        String userName = "test@ukr.net";
        String deviceUID = "b88106713409e92622461a876abcd74b1111";
        String apiVersion = "6.1";
        String communityUrl = "some_unknown_community";
        String timestamp = "" + updateDate.getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);

        doRequestBefore63(userName, deviceUID, apiVersion, communityUrl, timestamp, userToken, true, "300a400").andExpect(status().isBadRequest());
    }

    @Test
    public void testGetStreamzineForAnyMQUser_404_forNotAvailableCommunity() throws Exception {
        Date updateDate = new Date(System.currentTimeMillis() + 1000L);

        // parameters
        String userName = "test@ukr.net";
        String deviceUID = "b88106713409e92622461a876abcd74b1111";
        String apiVersion = "6.1";
        String communityUrl = "some_unknown_community";
        String timestamp = "" + updateDate.getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);

        // check xml format
        doRequestBefore63(userName, deviceUID, apiVersion, communityUrl, timestamp, userToken, true, "320x800").andExpect(status().isNotFound());
    }

    @Test
    public void testGetStreamzineForAnyMQUser_Success() throws Exception {
        Date updateDate = new Date(System.currentTimeMillis() + 2000L);

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
        final Message newsMessage = createNewsMessage();
        final Date publishDate = new Date();
        final int chartId = 6;
        final int existingTrackId = 49;
        final Media existingMedia = mediaRepository.findOne(existingTrackId);
        final String deepLinkTypeValue = DeeplinkType.DEEPLINK.name();

        FilenameAlias originalUploadedFile = new FilenameAlias("fileName", "fileName", new Dimensions(100, 100)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        originalUploadedFile = filenameAliasRepository.saveAndFlush(originalUploadedFile);

        prepareDefaultBadge(communityUrl, originalUploadedFile);
        FilenameAlias filenameAlias1 = prepareBadge(communityUrl, "IOS", "fileName1", 60, 60, originalUploadedFile);
        FilenameAlias filenameAlias2 = prepareBadge(communityUrl, "IOS", "fileName2", 50, 50, originalUploadedFile);

        Community community = communityRepository.findByRewriteUrlParameter(communityUrl);
        prepareUpdate(updateDate, externalLink, publishDate, newsMessage, chartId, existingMedia, originalUploadedFile, community, user);

        Thread.sleep(2500L);

        // check xml format
        doRequestBefore63(userName, deviceUID, apiVersion, communityUrl, timestamp, userToken, false, "60x60").andExpect(status().isOk()).andDo(print());

        // check json format and the correct order of the blocks
        ResultActions resultActions = doRequestBefore63(userName, deviceUID, apiVersion, communityUrl, timestamp, userToken, true, "60x60").andExpect(status().isOk()).andDo(print());

        resultActions.andDo(print())
            // check the orders
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[0].block_type", is(ShapeType.WIDE.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[1].block_type", is(ShapeType.SLIM_BANNER.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[2].block_type", is(ShapeType.NARROW.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[3].block_type", is(ShapeType.SLIM_BANNER.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[4].block_type", is(ShapeType.WIDE.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[6].block_type", is(ShapeType.SLIM_BANNER.name()))).andExpect(
            jsonPath("$.response.data[0].value.visual_blocks[5].block_type", is(ShapeType.NARROW.name())))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[0].link_type", is(deepLinkTypeValue)))
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[0].link_value", is("hl-uk://web/aHR0cDovL2V4YW1wbGUuY29t?open=externally")))

            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[0].access_policy.permission", is(Permission.RESTRICTED.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[0].access_policy.grantedTo", IsCollectionContaining.hasItem(GrantedToType.LIMITED.name()))).andExpect(
            jsonPath("$.response.data[0].value.visual_blocks[0].access_policy.grantedTo", IsCollectionContaining.hasItem(GrantedToType.FREETRIAL.name())))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[1].link_type", is(deepLinkTypeValue)))
                // check badges
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[0].badge_icon", is(filenameAlias1.getFileName())))
                //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[2].link_type", is(deepLinkTypeValue))).andExpect(
            jsonPath("$.response.data[0].value.stream_content_items[2].link_value", is("hl-uk://page/subscription_page?action=subscribe")))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[3].link_type", is(deepLinkTypeValue))).andExpect(
            jsonPath("$.response.data[0].value.stream_content_items[3].link_value", is("hl-uk://content/news?id=" + publishDate.getTime())))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[4].link_type", is(deepLinkTypeValue))).andExpect(
            jsonPath("$.response.data[0].value.stream_content_items[4].link_value", is("hl-uk://content/story?id=" + newsMessage.getId())))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[6].link_type", is(deepLinkTypeValue))).andExpect(
            jsonPath("$.response.data[0].value.stream_content_items[6].link_value", is("hl-uk://content/playlist?id=" + chartId)))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[5].link_type", is(deepLinkTypeValue))).andExpect(
            jsonPath("$.response.data[0].value.stream_content_items[5].link_value", is("hl-uk://content/track?id=" + existingMedia.getIsrcTrackId())))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[7].link_type", is(DeeplinkType.ID_LIST.name())))
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[7].link_value[0]", is(existingMedia.getI())));
    }


    @Test
    public void testGetStreamzineForAnyMQUserAPI63_Success() throws Exception {
        Date updateDate = new Date(System.currentTimeMillis() + 2000L);

        // parameters
        String userName = "test@ukr.net";
        String deviceUID = "b88106713409e92622461a876abcd74b1111";
        String apiVersion = "6.3";
        String communityUrl = "hl_uk";
        String timestamp = "" + updateDate.getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);
        User user = null;

        //
        // Expected JSON data
        //
        final String externalLink = "http://example.com";
        final Message newsMessage = createNewsMessage();
        final Date publishDate = new Date();
        final int chartId = 6;
        final int existingTrackId = 49;
        final Media existingMedia = mediaRepository.findOne(existingTrackId);
        final String deepLinkTypeValue = DeeplinkType.DEEPLINK.name();

        FilenameAlias originalUploadedFile = new FilenameAlias("fileName", "fileName", new Dimensions(100, 100)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        originalUploadedFile = filenameAliasRepository.saveAndFlush(originalUploadedFile);

        prepareDefaultBadge(communityUrl, originalUploadedFile);
        FilenameAlias filenameAlias1 = prepareBadge(communityUrl, "IOS", "fileName1", 60, 60, originalUploadedFile);
        FilenameAlias filenameAlias2 = prepareBadge(communityUrl, "IOS", "fileName2", 50, 50, originalUploadedFile);

        Community community = communityRepository.findByRewriteUrlParameter(communityUrl);
        prepareUpdate(updateDate, externalLink, publishDate, newsMessage, chartId, existingMedia, originalUploadedFile, community, user);

        Thread.sleep(2500L);

        // check xml format
        doRequestFrom63(userName, deviceUID, apiVersion, communityUrl, timestamp, userToken, false, "60x60", null).andExpect(status().isOk()).andDo(print());

        // check json format and the correct order of the blocks
        ResultActions resultActions = doRequestFrom63(userName, deviceUID, apiVersion, communityUrl, timestamp, userToken, true, "60x60", null).andExpect(status().isOk()).andDo(print());

        resultActions.andDo(print())
            // check the orders
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[0].block_type", is(ShapeType.WIDE.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[1].block_type", is(ShapeType.SLIM_BANNER.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[2].block_type", is(ShapeType.NARROW.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[3].block_type", is(ShapeType.SLIM_BANNER.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[4].block_type", is(ShapeType.WIDE.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[6].block_type", is(ShapeType.SLIM_BANNER.name()))).andExpect(
            jsonPath("$.response.data[0].value.visual_blocks[5].block_type", is(ShapeType.NARROW.name())))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[0].link_type", is(deepLinkTypeValue)))
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[0].link_value", is("hl-uk://web/aHR0cDovL2V4YW1wbGUuY29t?open=externally")))

            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[0].access_policy.permission", is(Permission.RESTRICTED.name())))
            .andExpect(jsonPath("$.response.data[0].value.visual_blocks[0].access_policy.grantedTo", IsCollectionContaining.hasItem(GrantedToType.LIMITED.name()))).andExpect(
            jsonPath("$.response.data[0].value.visual_blocks[0].access_policy.grantedTo", IsCollectionContaining.hasItem(GrantedToType.FREETRIAL.name())))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[1].link_type", is(deepLinkTypeValue)))
                // check badges
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[0].badge_icon", is(filenameAlias1.getFileName())))
                //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[2].link_type", is(deepLinkTypeValue))).andExpect(
            jsonPath("$.response.data[0].value.stream_content_items[2].link_value", is("hl-uk://page/subscription_page?action=subscribe")))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[3].link_type", is(deepLinkTypeValue))).andExpect(
            jsonPath("$.response.data[0].value.stream_content_items[3].link_value", is("hl-uk://content/news?id=" + publishDate.getTime())))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[4].link_type", is(deepLinkTypeValue))).andExpect(
            jsonPath("$.response.data[0].value.stream_content_items[4].link_value", is("hl-uk://content/story?id=" + newsMessage.getId())))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[6].link_type", is(deepLinkTypeValue))).andExpect(
            jsonPath("$.response.data[0].value.stream_content_items[6].link_value", is("hl-uk://content/playlist?player=" + PlayerType.MINI_PLAYER_ONLY.getId() + "&id=" + chartId)))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[5].link_type", is(deepLinkTypeValue))).andExpect(
            jsonPath("$.response.data[0].value.stream_content_items[5].link_value", is("hl-uk://content/track?player=" +
                                                                                       PlayerType.REGULAR_PLAYER_ONLY.getId() + "&id=" +
                                                                                       existingMedia.getIsrcTrackId())))
            //
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[7].link_type", is(DeeplinkType.ID_LIST.name())))
            .andExpect(jsonPath("$.response.data[0].value.stream_content_items[7].link_value[0]", is(existingMedia.getI())));
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
        User user1 = userRepository.findByUserNameAndCommunityUrl(userName1, communityUrl);
        User user2 = userRepository.findByUserNameAndCommunityUrl(userName2, communityUrl);
        User user3 = userRepository.findByUserNameAndCommunityUrl(userName3, communityUrl);
        //
        // Expected JSON data
        //
        final String externalLink = "http://example.com";
        final Message newsMessage = createNewsMessage();
        final Date publishDate = new Date();
        final int chartId = 6;
        final int existingTrackId = 49;
        final Media existingMedia = mediaRepository.findOne(existingTrackId);
        Community community = communityRepository.findByRewriteUrlParameter(communityUrl);

        prepareUpdate(updateDatePast, externalLink, publishDate, newsMessage, chartId, existingMedia, null, community, user1, user2);
        prepareUpdate(updateDateFuture, externalLink, publishDate, newsMessage, chartId, existingMedia, null, community, user1, user2);

        Thread.sleep(2500L);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_STREAMZINE.json").param("APP_VERSION", appVersion).param("COMMUNITY_NAME", communityUrl).param("API_VERSION", apiVersion)
                                                                                            .param("DEVICE_UID", user1.getDeviceUID()).param("USER_NAME", userName1)
                                                                                            .param("USER_TOKEN", createTimestampToken(user1.getToken(), timestamp)).param("WIDTHXHEIGHT", "320x800")
                                                                                            .param("TIMESTAMP", timestamp)).
                   andExpect(status().isOk()).
                   andExpect(jsonPath("$.response.data[0].value.updated").value(updateDateFuture.getTime()));

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_STREAMZINE.json").param("APP_VERSION", appVersion).param("COMMUNITY_NAME", communityUrl).param("API_VERSION", apiVersion)
                                                                                            .param("DEVICE_UID", user2.getDeviceUID()).param("USER_NAME", userName2).param("WIDTHXHEIGHT", "320x800")
                                                                                            .param("USER_TOKEN", createTimestampToken(user2.getToken(), timestamp)).param("TIMESTAMP", timestamp)).
                   andExpect(status().isOk()).
                   andExpect(jsonPath("$.response.data[0].value.updated").value(updateDateFuture.getTime()));

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_STREAMZINE.json").param("APP_VERSION", appVersion).param("COMMUNITY_NAME", communityUrl).param("API_VERSION", apiVersion)
                                                                                            .param("DEVICE_UID", user3.getDeviceUID()).param("USER_NAME", userName3).param("WIDTHXHEIGHT", "320x800")
                                                                                            .param("USER_TOKEN", createTimestampToken(user3.getToken(), timestamp)).param("TIMESTAMP", timestamp))
               .andExpect(status().isOk()).andExpect(jsonPath("$.response.data[0].value.updated").value(updateDatePast.getTime()));
    }

    @Test
    public void testGetStreamzineForSeveralMQUsers_Success_LatestVersion() throws Exception {
        final Date updateDatePast = new Date(System.currentTimeMillis() + 1000L);
        final Date updateDateFuture = new Date(System.currentTimeMillis() + 10000L);

        // parameters
        String userName1 = "test@ukr.net";
        String userName2 = "dnepr@i.ua";
        String userName3 = "mq@mq.com";
        String apiVersion = "6.2";
        String appVersion = "1.0";
        String communityUrl = "hl_uk";
        String timestamp = System.currentTimeMillis() + "";
        User user1 = userRepository.findByUserNameAndCommunityUrl(userName1, communityUrl);
        User user2 = userRepository.findByUserNameAndCommunityUrl(userName2, communityUrl);
        User user3 = userRepository.findByUserNameAndCommunityUrl(userName3, communityUrl);
        //
        // Expected JSON data
        //
        final String externalLink = "http://example.com";
        final Message newsMessage = createNewsMessage();
        final Date publishDate = new Date();
        final int chartId = 6;
        final int existingTrackId = 49;
        final Media existingMedia = mediaRepository.findOne(existingTrackId);
        Community community = communityRepository.findByRewriteUrlParameter(communityUrl);

        prepareUpdate(updateDatePast, externalLink, publishDate, newsMessage, chartId, existingMedia, null, community, user1, user2);
        prepareUpdate(updateDateFuture, externalLink, publishDate, newsMessage, chartId, existingMedia, null, community, user1, user2);

        Thread.sleep(2500L);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_STREAMZINE.json").param("APP_VERSION", appVersion).param("COMMUNITY_NAME", communityUrl).param("API_VERSION", apiVersion)
                                                                                            .param("DEVICE_UID", user1.getDeviceUID()).param("USER_NAME", userName1)
                                                                                            .param("USER_TOKEN", createTimestampToken(user1.getToken(), timestamp)).param("WIDTHXHEIGHT", "320x800")
                                                                                            .param("TIMESTAMP", timestamp)).
                   andExpect(status().isOk()).
                   andExpect(jsonPath("$.response.data[0].value.updated").value(updateDateFuture.getTime()));

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_STREAMZINE.json").param("APP_VERSION", appVersion).param("COMMUNITY_NAME", communityUrl).param("API_VERSION", apiVersion)
                                                                                            .param("DEVICE_UID", user2.getDeviceUID()).param("USER_NAME", userName2).param("WIDTHXHEIGHT", "320x800")
                                                                                            .param("USER_TOKEN", createTimestampToken(user2.getToken(), timestamp)).param("TIMESTAMP", timestamp)).
                   andExpect(status().isOk()).
                   andExpect(jsonPath("$.response.data[0].value.updated").value(updateDateFuture.getTime()));

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_STREAMZINE.json").param("APP_VERSION", appVersion).param("COMMUNITY_NAME", communityUrl).param("API_VERSION", apiVersion)
                                                                                            .param("DEVICE_UID", user3.getDeviceUID()).param("USER_NAME", userName3).param("WIDTHXHEIGHT", "320x800")
                                                                                            .param("USER_TOKEN", createTimestampToken(user3.getToken(), timestamp)).param("TIMESTAMP", timestamp))
               .andExpect(status().isOk()).andExpect(jsonPath("$.response.data[0].value.updated").value(updateDatePast.getTime()));
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
        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_STREAMZINE.json").param("APP_VERSION", userName).param("COMMUNITY_NAME", communityUrl).param("API_VERSION", apiVersion)
                                                                                            .param("DEVICE_UID", deviceUID).param("USER_NAME", userName).param("USER_TOKEN", userToken)
                                                                                            .param("WIDTHXHEIGHT", "1x1").param("TIMESTAMP", timestamp)).
                   andExpect(status().isNotFound());
    }


    private void prepareDefaultBadge(String communityUrl, FilenameAlias originalUploadedFile) {
        Community commmunity = communityRepository.findByRewriteUrlParameter(communityUrl);
        BadgeMapping mapping = BadgeMapping.general(commmunity, originalUploadedFile);
        badgeMappingRepository.saveAndFlush(mapping);
    }

    private FilenameAlias prepareBadge(String communityUrl, String deviceType, String fileName, int width, int height, FilenameAlias originalUploadedFile) {
        Community commmunity = communityRepository.findByRewriteUrlParameter(communityUrl);
        Resolution resolution = resolutionRepository.saveAndFlush(new Resolution(deviceType, width, height));

        BadgeMapping mapping = BadgeMapping.specific(resolution, commmunity, originalUploadedFile);
        mapping.setFilenameAlias(new FilenameAlias(fileName + "_" + width + "x" + height, "title for " + fileName, new Dimensions(5, 5)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES));

        badgeMappingRepository.saveAndFlush(mapping);

        return mapping.getFilenameAlias();
    }

    private ResultActions doRequestBefore63(String userName, String deviceUID, String apiVersion, String communityUrl, String timestamp, String userToken, boolean isJson, String resolution)
        throws Exception {
        final String formatSpecific = (isJson) ?
                                      ".json" :
                                      "";

        return mockMvc.perform(
            post("/" + communityUrl + "/" + apiVersion + "/GET_STREAMZINE" + formatSpecific).param("APP_VERSION", userName).param("COMMUNITY_NAME", communityUrl).param("API_VERSION", apiVersion)
                                                                                            .param("DEVICE_UID", deviceUID).param("USER_NAME", userName).param("USER_TOKEN", userToken)
                                                                                            .param("TIMESTAMP", timestamp).param("WIDTHXHEIGHT", resolution));
    }

    private ResultActions doRequestFrom63(String userName, String deviceUID, String apiVersion, String communityUrl, String timestamp, String userToken, boolean isJson, String resolution,
                                          Object modifiedSinceTime) throws Exception {
        final String formatSpecific = (isJson) ?
                                      ".json" :
                                      "";

        MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
            get("/" + communityUrl + "/" + apiVersion + "/GET_STREAMZINE" + formatSpecific).param("APP_VERSION", userName).param("COMMUNITY_NAME", communityUrl).param("API_VERSION", apiVersion)
                                                                                           .param("DEVICE_UID", deviceUID).param("USER_NAME", userName).param("USER_TOKEN", userToken)
                                                                                           .param("TIMESTAMP", timestamp).param("WIDTHXHEIGHT", resolution);
        if (modifiedSinceTime != null) {
            mockHttpServletRequestBuilder.headers(getHttpHeadersWithIfModifiedSince(modifiedSinceTime));
        }
        return mockMvc.perform(mockHttpServletRequestBuilder);
    }


    private void prepareUpdate(Date updateDate, String externalLink, Date publishDate, Message newsMessage, int chartId, Media track, FilenameAlias filenameAlias, Community community, User... users) {
        Update update = streamzineUpdateService.create(updateDate, community);

        // simulate adding blocks
        Update incomingWithBlocks = createWithBlocks(externalLink, publishDate, newsMessage, chartId, track, filenameAlias, community, users);
        streamzineUpdateService.update(update.getId(), incomingWithBlocks);
    }

    private Update createWithBlocks(String externalLink, Date publishDate, Message newsMessage, int chartId, Media track, FilenameAlias filenameAlias, Community community, User... users) {
        Update u = new Update(DateUtils.addDays(new Date(), 1), community);
        //
        // Not included block
        //
        Block excludedAdd = newBlock(0, ShapeType.NARROW, createMusicTrackDeeplink(track), null);
        excludedAdd.exclude();
        u.addBlock(excludedAdd);
        //
        // Ordinal blocks
        //
        u.addBlock(newBlock(1, ShapeType.WIDE, createNotificationDeeplink(externalLink), filenameAlias));
        u.addBlock(newBlock(2, ShapeType.SLIM_BANNER, createNotificationDeeplink0(), null));
        u.addBlock(newBlock(3, ShapeType.NARROW, createNotificationDeeplink1(), null));
        u.addBlock(newBlock(4, ShapeType.SLIM_BANNER, createNewsListDeeplink(publishDate), null));
        u.addBlock(newBlock(5, ShapeType.WIDE, createNewsStoryDeeplink(newsMessage), null));
        //
        // Added mixed positions to test that values are added according to positions: 5 and 6
        //
        u.addBlock(newBlock(7, ShapeType.SLIM_BANNER, createMusicPlaylistDeeplink(chartId), null));
        u.addBlock(newBlock(8, ShapeType.SLIM_BANNER, createManualCompilationDeeplink(track), null));
        u.addBlock(newBlock(6, ShapeType.NARROW, createMusicTrackDeeplink(track), null));

        if (users != null) {
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
        return new NotificationDeeplinkInfo(LinkLocationType.EXTERNAL_AD, url, BROWSER);
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
        newsStory.setMessageType(MessageType.NEWS);
        newsStory.setCommunity(communityRepository.findByRewriteUrlParameter("hl_uk"));
        return messageRepository.saveAndFlush(newsStory);
    }

    private DeeplinkInfo createMusicPlaylistDeeplink(int chartDetailsId) {
        MusicPlayListDeeplinkInfo d = new MusicPlayListDeeplinkInfo(chartDetailsId, PlayerType.MINI_PLAYER_ONLY);
        return d;
    }

    private DeeplinkInfo createMusicTrackDeeplink(Media media) {
        MusicTrackDeeplinkInfo d = new MusicTrackDeeplinkInfo(media, PlayerType.REGULAR_PLAYER_ONLY);
        return d;
    }

    private Block newBlock(int position, ShapeType shapeType, DeeplinkInfo deepLinkInfo, FilenameAlias originalUploadedFile) {
        Block b = new Block(position, shapeType, deepLinkInfo);
        b.setTitle("title");
        b.setSubTitle("sub title");
        b.setCoverUrl("image_" + System.nanoTime() + ".jpg");
        b.include();
        b.setAccessPolicy(AccessPolicy.enabledForVipOnly());
        if (originalUploadedFile != null) {
            b.setBadgeId(originalUploadedFile.getId());
        }
        return b;
    }

}
