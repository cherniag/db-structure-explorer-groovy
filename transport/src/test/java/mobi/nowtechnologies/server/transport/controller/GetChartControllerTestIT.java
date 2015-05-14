package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Genre;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Dimensions;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.BadgeMapping;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import mobi.nowtechnologies.server.persistence.repository.BadgeMappingRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.GenreRepository;
import mobi.nowtechnologies.server.persistence.repository.ResolutionRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;
import static mobi.nowtechnologies.server.persistence.domain.Community.HL_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.persistence.domain.Community.O2_COMMUNITY_REWRITE_URL;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

public class GetChartControllerTestIT extends AbstractControllerTestIT {

    private static final String OLD_ITUNES_URL_O2 =
        "http%3A%2F%2Fitunes.apple.com%2FGB%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%26uo%3D4%26at%3Dat_for_o2%26ct%3Dct_for_o2";
    private static final String OLD_ITUNES_URL_HL_UK =
        "http%3A%2F%2Fitunes.apple.com%2FGB%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%26uo%3D4%26at%3Dat_for_hl_uk%26ct%3Dct_for_hl_uk";
    private static final String NEW_ITUNES_URL_O2 = "http%3A%2F%2Fitunes.apple.com%2FGB%2Falbum%2Fmonster%2Fid440880917%3Fi%3D440880925%26uo%3D4%26at%3Dat_for_o2%26ct%3Dct_for_o2";

    private static final ChartType[] CHART_TYPES = new ChartType[] {ChartType.HOT_TRACKS, ChartType.OTHER_CHART, ChartType.FOURTH_CHART, ChartType.FIFTH_CHART, ChartType.VIDEO_CHART};

    @Resource
    private ChartRepository chartRepository;

    @Resource
    private ChartDetailRepository chartDetailRepository;

    @Resource
    private CommunityRepository communityRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private UserGroupRepository userGroupRepository;

    @Resource
    private ResolutionRepository resolutionRepository;

    @Resource
    private BadgeMappingRepository badgeMappingRepository;

    @Resource
    private GenreRepository genreRepository;

    @Test
    public void testGetChart_LatestVersion() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = LATEST_SERVER_API_VERSION;
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String widthHeight = "720x1280";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        prepareChartsForO2();

        mockMvc.perform(
            get("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID)
                                                                          .param("WIDTHXHEIGHT", widthHeight)).andExpect(status().isOk())
               .andExpect(jsonPath("response.data[0].user.userName").value(userName)).andExpect(jsonPath("response.data[0].user.deviceUID").value(deviceUID))
               .andExpect(jsonPath("response.data[1].chart.playlists[0].type").value(CHART_TYPES[0].name()))
               .andExpect(jsonPath("response.data[1].chart.playlists[1].type").value(CHART_TYPES[1].name()))
               .andExpect(jsonPath("response.data[1].chart.playlists[2].type").value(CHART_TYPES[2].name()))
               .andExpect(jsonPath("response.data[1].chart.playlists[3].type").value(CHART_TYPES[3].name()))
               .andExpect(jsonPath("response.data[1].chart.playlists[4].type").value(CHART_TYPES[4].name())).andExpect(jsonPath("response.data[1].chart.tracks[0].title").value("Party Rock Anthem"))
               .andExpect(jsonPath("response.data[1].chart.tracks[0].artist").value("Lmfao/Lauren Bennett/Goonrock")).andExpect(jsonPath("response.data[1].chart.tracks[0].genre1").value("Default"))
               .andExpect(jsonPath("response.data[1].chart.tracks[0].duration").value(10000)).andExpect(jsonPath("response.data[1].chart.bonusTrack").doesNotExist());
    }

    @Test
    public void testGetChartWithChartUpdateId() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "6.11";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String widthHeight = "720x1280";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        Map<ChartType, List<ChartDetail>> updateMap = prepareChartsForO2();

        Integer htChartUpdateId = updateMap.get(ChartType.HOT_TRACKS).get(0).getI();
        Integer ocChartUpdateId = updateMap.get(ChartType.OTHER_CHART).get(0).getI();
        Integer ftChartUpdateId = updateMap.get(ChartType.FOURTH_CHART).get(0).getI();
        Integer ffChartUpdateId = updateMap.get(ChartType.FIFTH_CHART).get(0).getI();
        Integer vcChartUpdateId = updateMap.get(ChartType.VIDEO_CHART).get(0).getI();

        mockMvc.perform(get("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json").param("USER_NAME", userName)
                                                                                      .param("USER_TOKEN", userToken)
                                                                                      .param("TIMESTAMP", timestamp)
                                                                                      .param("DEVICE_UID", deviceUID)
                                                                                      .param("WIDTHXHEIGHT", widthHeight)).andDo(print()).andExpect(status().isOk())
               .andExpect(jsonPath("response.data[1].chart.playlists[?(@.type == 'HOT_TRACKS')].chartUpdateId").value(htChartUpdateId))
               .andExpect(jsonPath("response.data[1].chart.playlists[?(@.type == 'OTHER_CHART')].chartUpdateId").value(ocChartUpdateId))
               .andExpect(jsonPath("response.data[1].chart.playlists[?(@.type == 'FOURTH_CHART')].chartUpdateId").value(ftChartUpdateId))
               .andExpect(jsonPath("response.data[1].chart.playlists[?(@.type == 'FIFTH_CHART')].chartUpdateId").value(ffChartUpdateId))
               .andExpect(jsonPath("response.data[1].chart.playlists[?(@.type == 'VIDEO_CHART')].chartUpdateId").value(vcChartUpdateId));
    }

    @Test
    public void testGetChartPlayListLocked() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "6.7";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String widthHeight = "720x1280";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        Map<ChartType, List<ChartDetail>> updateMap = prepareChartsForO2();

        // track in FOURTH_CHART is locked
        ChartDetail fourth = updateMap.get(ChartType.FOURTH_CHART).get(1);
        fourth.setLocked(true);
        chartDetailRepository.save(fourth);

        ChartDetail other = updateMap.get(ChartType.OTHER_CHART).get(1);
        other.setLocked(false);
        chartDetailRepository.save(other);

        mockMvc.perform(
            get("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID)
                                                                          .param("WIDTHXHEIGHT", widthHeight)).andExpect(status().isOk())
               .andExpect(jsonPath("response.data[1].chart.playlists[?(@.type == 'HOT_TRACKS')].locked").doesNotExist())
               .andExpect(jsonPath("response.data[1].chart.playlists[?(@.type == 'OTHER_CHART')].locked").doesNotExist())
               .andExpect(jsonPath("response.data[1].chart.playlists[?(@.type == 'FOURTH_CHART')].locked").doesNotExist())
               .andExpect(jsonPath("response.data[1].chart.playlists[?(@.type == 'FIFTH_CHART')].locked").doesNotExist())
               .andExpect(jsonPath("response.data[1].chart.playlists[?(@.type == 'VIDEO_CHART')].locked").doesNotExist())
               .andExpect(jsonPath("response.data[1].chart.playlists[?(@.type == 'BASIC_CHART')].locked").doesNotExist());
    }

    @Test
    public void testGetChart_O2_v5d1AndJsonAndAccCheckInfo_Success() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.1";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        prepareChartsForO2();

        mockMvc.perform(
            post("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID))
               .andExpect(status().isOk()).andExpect(jsonPath("response.data[0].user.userName").value(userName)).andExpect(jsonPath("response.data[0].user.deviceUID").value(deviceUID))
               .andExpect(jsonPath("response.data[1].chart.playlists[0].type").value(CHART_TYPES[0].name()))
               .andExpect(jsonPath("response.data[1].chart.playlists[1].type").value(CHART_TYPES[1].name()))
               .andExpect(jsonPath("response.data[1].chart.playlists[2].type").value(CHART_TYPES[2].name()))
               .andExpect(jsonPath("response.data[1].chart.playlists[3].type").value(CHART_TYPES[3].name()))
               .andExpect(jsonPath("response.data[1].chart.playlists[4].type").value(CHART_TYPES[4].name())).andExpect(jsonPath("response.data[1].chart.tracks[0].title").value("Party Rock Anthem"))
               .andExpect(jsonPath("response.data[1].chart.tracks[0].artist").value("Lmfao/Lauren Bennett/Goonrock")).andExpect(jsonPath("response.data[1].chart.tracks[0].genre1").value("Default"))
               .andExpect(jsonPath("response.data[1].chart.tracks[0].duration").value(10000)).andExpect(jsonPath("response.data[1].chart.bonusTrack").doesNotExist());
    }

    @Test
    public void testGetChart_O2_v6d0AndJsonAndAccCheckInfo_Success() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        prepareChartsForO2();

        mockMvc.perform(
            post("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID))
               .andExpect(status().isOk()).andExpect(jsonPath("response.data[0].user.userName").value(userName)).andExpect(jsonPath("response.data[0].user.deviceUID").value(deviceUID))
               .andExpect(jsonPath("response.data[1].chart.playlists[0].type").value(CHART_TYPES[0].name()))
               .andExpect(jsonPath("response.data[1].chart.playlists[1].type").value(CHART_TYPES[1].name()))
               .andExpect(jsonPath("response.data[1].chart.playlists[2].type").value(CHART_TYPES[2].name()))
               .andExpect(jsonPath("response.data[1].chart.playlists[3].type").value(CHART_TYPES[3].name()))
               .andExpect(jsonPath("response.data[1].chart.playlists[4].type").value(CHART_TYPES[4].name())).andExpect(jsonPath("response.data[1].chart.tracks[0].title").value("Party Rock Anthem"))
               .andExpect(jsonPath("response.data[1].chart.tracks[0].artist").value("Lmfao/Lauren Bennett/Goonrock")).andExpect(jsonPath("response.data[1].chart.tracks[0].genre1").value("Default"))
               .andExpect(jsonPath("response.data[1].chart.tracks[0].duration").value(10000)).andExpect(jsonPath("response.data[1].chart.bonusTrack").doesNotExist());
    }


    @Test
    public void testGetChartO2WithBadges() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "6.4";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String widthHeight = "640x960";

        // prepare badges
        FilenameAlias originalUploadedFile = new FilenameAlias("badge_picture_orig", "alias_orig", new Dimensions(1000, 1000)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        prepareDefaultBadge(communityUrl, originalUploadedFile);
        prepareBadge(communityUrl, "IOS", "badge_picture", 640, 1136, 20, 20, originalUploadedFile);
        prepareBadge(communityUrl, "IOS", "badge_picture", 640, 960, 10, 10, originalUploadedFile);
        prepareBadge(communityUrl, "ANDROID", "badge_picture", 640, 960, 10, 10, originalUploadedFile);

        // prepare chart update
        Map<ChartType, List<ChartDetail>> updateMap = prepareChartsForO2();

        // set explicitly chart update with badge
        ChartDetail updateMarker = updateMap.get(ChartType.HOT_TRACKS).get(0);
        updateMarker.setBadgeId(originalUploadedFile.getId());
        chartDetailRepository.save(updateMarker);

        mockMvc.perform(
            get("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID)
                                                                          .param("WIDTHXHEIGHT", widthHeight)).andExpect(status().isOk())
               .andExpect(jsonPath("response.data[1].chart.playlists[0].badge_icon").value("badge_picture_IOS_640x960"));
    }

    @Test
    public void testGetChart_O2_v4d0_Success() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "4.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        prepareChartsForO2();

        mockMvc.perform(
            post("/" + communityUrl + "/" + apiVersion + "/GET_CHART").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID))
               .andExpect(status().isOk()).andExpect(xpath("/response/chart/playlist[type='VIDEO_CHART']").exists()).andExpect(xpath("/response/user/lockedTrack/media").string("US-UM7-11-00061_4"))
               .andExpect(xpath("/response/chart/track[duration=10000]").exists()).andExpect(xpath("/response/chart/track[iTunesUrl='" + OLD_ITUNES_URL_O2.replace("%", "%%") + "']").exists())
               .andExpect(xpath("/response/chart/track[iTunesUrl='" + NEW_ITUNES_URL_O2.replace("%", "%%") + "']").exists()).andExpect(xpath("/response/chart/bonusTrack").doesNotExist());
    }

    @Test
    public void testGetChart_O2_v3d8_Success() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "3.8";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        prepareChartsForO2();

        mockMvc.perform(
            post("/" + communityUrl + "/" + apiVersion + "/GET_CHART").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID)
                                                                      .param("APP_VERSION", apiVersion).param("COMMUNITY_NAME", apiVersion)).andExpect(status().isOk())
               .andExpect(xpath("/response/chart/track[iTunesUrl='" + OLD_ITUNES_URL_O2.replace("%", "%%") + "']").exists())
               .andExpect(xpath("/response/chart/track[iTunesUrl='" + NEW_ITUNES_URL_O2.replace("%", "%%") + "']").exists())
               .andExpect(xpath("/response/chart/playlist[type='VIDEO_CHART']").doesNotExist()).andExpect(xpath("/response/chart/bonusTrack").doesNotExist());
    }

    @Test
    public void testGetChart_O2_v3d7_Success() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "3.7";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        prepareChartsForO2();

        mockMvc.perform(
            post("/" + communityUrl + "/" + apiVersion + "/GET_CHART").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID)
                                                                      .param("APP_VERSION", apiVersion).param("COMMUNITY_NAME", apiVersion)).andExpect(status().isOk())
               .andExpect(xpath("/response/chart/track[iTunesUrl='" + OLD_ITUNES_URL_O2.replace("%", "%%") + "']").exists())
               .andExpect(xpath("/response/chart/track[iTunesUrl='" + NEW_ITUNES_URL_O2.replace("%", "%%") + "']").exists())
               .andExpect(xpath("/response/chart/playlist[type='VIDEO_CHART']").doesNotExist()).andExpect(xpath("/response/chart/playlist[type='FOURTH_CHART']").doesNotExist())
               .andExpect(xpath("/response/chart/playlist[type='FIFTH_CHART']").doesNotExist()).andExpect(xpath("/response/chart/bonusTrack").doesNotExist());
    }

    @Test
    public void testGetChart_O2_v3d6_Success() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "3.6";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        prepareChartsForO2();

        mockMvc.perform(
            post("/" + communityUrl + "/" + apiVersion + "/GET_CHART").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID)
                                                                      .param("APP_VERSION", apiVersion).param("API_VERSION", apiVersion).param("COMMUNITY_NAME", apiVersion)).andExpect(status().isOk())
               .andExpect(xpath("/response/chart/playlist[type='VIDEO_CHART']").doesNotExist()).andExpect(xpath("/response/chart/playlist[type='FOURTH_CHART']").doesNotExist())
               .andExpect(xpath("/response/chart/playlist[type='FIFTH_CHART']").doesNotExist()).andExpect(xpath("/response/chart/bonusTrack").exists());
    }

    @Test
    public void testGetChart_401_Failure() throws Exception {
        String userName = "+447xxxxxxxxx";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
            post("/" + communityUrl + "/" + apiVersion + "/GET_CHART").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID))
               .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetChart_400_Failure() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_CHART").param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID))
               .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetChartV5d3_400_Failure() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.3";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_CHART").param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetChart_404_Failure() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "3.5";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_CHART").param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID))
               .andExpect(status().isNotFound());
    }


    @Test
    public void tesGetChartForHL() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.2";
        String communityUrl = "hl_uk";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        Community hlCommunity = communityRepository.findByRewriteUrlParameter(HL_COMMUNITY_REWRITE_URL);
        Community o2Community = communityRepository.findByRewriteUrlParameter(O2_COMMUNITY_REWRITE_URL);
        UserGroup hlUserGroup = userGroupRepository.findByCommunity(hlCommunity);
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, o2Community);
        user.setUserGroup(hlUserGroup);
        userRepository.saveAndFlush(user);
        generateChartAllTypesForHL();
        mockMvc.perform(
            post("/" + communityUrl + "/" + apiVersion + "/GET_CHART").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID)
                                                                      .param("APP_VERSION", apiVersion).param("API_VERSION", apiVersion).param("COMMUNITY_NAME", apiVersion)).andExpect(status().isOk())
               .andExpect(xpath("//chart/playlist").nodeCount(7)).andExpect(xpath("//chart/playlist[type/text()='HOT_TRACKS']").nodeCount(1))
               .andExpect(xpath("//chart/playlist[type/text()='FIFTH_CHART']").nodeCount(1)).andExpect(xpath("//chart/playlist[type/text()='HL_UK_PLAYLIST_1']").nodeCount(1))
               .andExpect(xpath("//chart/playlist[type/text()='HL_UK_PLAYLIST_2']").nodeCount(2)).andExpect(xpath("//chart/playlist[type/text()='OTHER_CHART']").nodeCount(1))
               .andExpect(xpath("//chart/playlist[type/text()='FOURTH_CHART']").nodeCount(1))
               .andExpect(xpath("/response/chart/track[iTunesUrl='" + OLD_ITUNES_URL_HL_UK.replace("%", "%%") + "']").exists());
    }

    private void prepareDefaultBadge(String communityUrl, FilenameAlias originalUploadedFile) {
        Community community = communityRepository.findByName(communityUrl);
        BadgeMapping mapping = BadgeMapping.general(community, originalUploadedFile);
        badgeMappingRepository.saveAndFlush(mapping);
    }

    private FilenameAlias prepareBadge(String communityUrl, String deviceType, String fileName, int width, int height, int iconWidth, int iconHeight, FilenameAlias originalUploadedFile) {
        Community community = communityRepository.findByName(communityUrl);
        Resolution resolution = resolutionRepository.saveAndFlush(new Resolution(deviceType, width, height));

        BadgeMapping mapping = BadgeMapping.specific(resolution, community, originalUploadedFile);
        mapping.setFilenameAlias(new FilenameAlias(fileName + "_" + deviceType + "_" + width + "x" + height, "title for " + fileName, new Dimensions(iconWidth, iconHeight))
                                     .forDomain(FilenameAlias.Domain.HEY_LIST_BADGES));

        badgeMappingRepository.saveAndFlush(mapping);

        return mapping.getFilenameAlias();
    }

    private Map<ChartType, List<ChartDetail>> prepareChartsForO2() {
        final long publishTimestamp = new Date().getTime();

        Map<ChartType, List<ChartDetail>> updateMap = new HashMap<>();

        Genre genre = genreRepository.findByName("Default");
        Media media = chartDetailRepository.findOne(22).getMedia();

        // create and save charts for community
        List<Chart> charts = new ArrayList<>();
        for (ChartType chartType : CHART_TYPES) {
            Chart createdChart = createChart(chartType, genre);
            charts.add(createdChart);
            updateMap.put(chartType, new ArrayList<ChartDetail>());
        }

        // create and save update markers (chart details with media null)
        for (Chart created : charts) {
            ChartDetail updateMarker = createUpdateMarker(created, publishTimestamp);
            updateMap.get(created.getType()).add(updateMarker);
        }

        // create and save chart item
        for (Chart created : charts) {
            ChartDetail chartItem = createChartItem(created, media, publishTimestamp);
            updateMap.get(created.getType()).add(chartItem);
        }

        return updateMap;
    }

    private ChartDetail createChartItem(Chart chart, Media media, long publishTimeMillis) {
        ChartDetail chartItem = new ChartDetail();
        chartItem.setChart(chart);
        chartItem.setChannel("chanell");
        chartItem.setMedia(media);
        chartItem.setPosition((byte) 1);
        chartItem.setPrevPosition((byte) 1);
        chartItem.setChgPosition(ChgPosition.UNCHANGED);
        chartItem.setLocked(null);
        chartItem.setPublishTimeMillis(publishTimeMillis);
        return chartDetailRepository.save(chartItem);
    }

    private ChartDetail createUpdateMarker(Chart chart, long publishTimeMillis) {
        ChartDetail updateMarker = new ChartDetail();
        updateMarker.setChart(chart);
        updateMarker.setChannel("chanell");
        updateMarker.setTitle("title_" + chart.getI());
        updateMarker.setSubtitle("subTitle_" + chart.getI());
        updateMarker.setPosition((byte) 0);
        updateMarker.setPublishTimeMillis(publishTimeMillis);
        return chartDetailRepository.save(updateMarker);
    }

    private Chart createChart(ChartType chartType, Genre genre) {
        Chart chart = new Chart();
        chart.setType(chartType);
        chart.getCommunities().add(communityRepository.findOne(7));
        chart.setGenre(genre);
        return chartRepository.save(chart);
    }


    private void generateChartAllTypesForHL() {
        Chart chart = chartRepository.findOne(14);
        ChartDetail chartDetail = chartDetailRepository.findOne(22);
        ChartDetail hotDetail = new ChartDetail();
        hotDetail.setChart(chart);
        hotDetail.setChannel("chanell");
        hotDetail.setMedia(chartDetail.getMedia());
        hotDetail.setPosition(chartDetail.getPosition());
        hotDetail.setPrevPosition(chartDetail.getPrevPosition());
        hotDetail.setChgPosition(chartDetail.getChgPosition());
        hotDetail.setPublishTimeMillis(chartDetail.getPublishTimeMillis());
        chartDetailRepository.saveAndFlush(hotDetail);
    }
}
