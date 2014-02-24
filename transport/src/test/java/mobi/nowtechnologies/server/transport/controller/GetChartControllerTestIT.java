package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.MediaFile;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.MediaFileRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.NotTransactional;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

public class GetChartControllerTestIT extends AbstractControllerTestIT {

    @Autowired
    private ChartRepository chartRepository;

    @Autowired
    private ChartDetailRepository chartDetailRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Test
    public void testGetChart_O2_v6d0AndJsonAndAccCheckInfo_Success() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.1";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        generateChartAllTypesForO2();

        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultJson = aHttpServletResponse.getContentAsString();

        assertTrue(resultJson.contains("\"type\":\"VIDEO_CHART\""));
        assertTrue(resultJson.contains("\"duration\":10000"));
        assertTrue(!resultJson.contains("\"bonusTrack\""));
        assertTrue(resultJson.contains("\"tracks\""));
        assertTrue(resultJson.contains("\"playlists\""));
        assertTrue(resultJson.contains("\"chart\""));
        assertTrue(resultJson.contains("\"user\""));

        ResultActions accountCheckCall = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());
        checkAccountCheck(resultActions, accountCheckCall);
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

        generateChartAllTypesForO2();

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk()).andDo(print()).
                andExpect(xpath("/response/chart/playlist[type='VIDEO_CHART']").exists())
                .andExpect(xpath("/response/chart/track[duration=10000]").exists())
                .andExpect(xpath("/response/chart/bonusTrack").doesNotExist());
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

        generateChartAllTypesForO2();

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
                        .param("APP_VERSION", apiVersion)
                        .param("COMMUNITY_NAME", apiVersion)
        ).andExpect(status().isOk())
                .andExpect(xpath("/response/chart/playlist[type='VIDEO_CHART']").doesNotExist())
                .andExpect(xpath("/response/chart/bonusTrack").doesNotExist());
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

        generateChartAllTypesForO2();

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
                        .param("APP_VERSION", apiVersion)
                        .param("COMMUNITY_NAME", apiVersion)
        ).andExpect(status().isOk())
                .andExpect(xpath("/response/chart/playlist[type='VIDEO_CHART']").doesNotExist())
                .andExpect(xpath("/response/chart/playlist[type='FOURTH_CHART']").doesNotExist())
                .andExpect(xpath("/response/chart/playlist[type='FIFTH_CHART']").doesNotExist())
                .andExpect(xpath("/response/chart/bonusTrack").doesNotExist());
    }

    @Test
    @NotTransactional
    public void testGetChart_O2_v3d6_Success() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "3.6";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        generateChartAllTypesForO2();

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
                        .param("APP_VERSION", apiVersion)
                        .param("API_VERSION", apiVersion)
                        .param("COMMUNITY_NAME", apiVersion)
        ).andExpect(status().isOk()).andDo(print())
                .andExpect(xpath("/response/chart/playlist[type='VIDEO_CHART']").doesNotExist())
                .andExpect(xpath("/response/chart/playlist[type='FOURTH_CHART']").doesNotExist())
                .andExpect(xpath("/response/chart/playlist[type='FIFTH_CHART']").doesNotExist())
                .andExpect(xpath("/response/chart/bonusTrack").exists());
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
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetChart_400_Failure() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART")
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetChartV5d3_400_Failure() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.3";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART")
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldInvalidPhoneNumberGivenVersionLessOrEqual5() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a444";
        String phone = "+44711111xxxxx";
        String apiVersion = "4.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/some_key/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("PHONE", phone)
        ).andExpect(status().isOk())
                .andExpect(xpath("/response/errorMessage/errorCode").number(601d));
    }

    @Test
    public void testGetChart_404_Failure() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "3.5";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART")
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isNotFound());
    }

    private void generateChartAllTypesForO2() {
        Community o2Community = communityRepository.findOne(7);
        Chart chart = chartRepository.findOne(5);

        Chart hotChart = new Chart();
        hotChart.setType(ChartType.HOT_TRACKS);
        hotChart.getCommunities().add(o2Community);
        hotChart.setGenre(chart.getGenre());
        chartRepository.save(hotChart);

        Chart otherChart = new Chart();
        otherChart.setType(ChartType.OTHER_CHART);
        otherChart.getCommunities().add(o2Community);
        otherChart.setGenre(chart.getGenre());
        chartRepository.save(otherChart);

        Chart fourthChart = new Chart();
        fourthChart.setType(ChartType.FOURTH_CHART);
        fourthChart.getCommunities().add(o2Community);
        fourthChart.setGenre(chart.getGenre());
        chartRepository.save(fourthChart);

        Chart fifthChart = new Chart();
        fifthChart.setType(ChartType.FIFTH_CHART);
        fifthChart.getCommunities().add(o2Community);
        fifthChart.setGenre(chart.getGenre());
        chartRepository.save(fifthChart);

        Chart videoChart = new Chart();
        videoChart.setType(ChartType.VIDEO_CHART);
        videoChart.getCommunities().add(o2Community);
        videoChart.setGenre(chart.getGenre());
        chartRepository.save(videoChart);


        ChartDetail chartDetail = chartDetailRepository.findOne(22);

        ChartDetail hotDetail = new ChartDetail();
        hotDetail.setChart(hotChart);
        hotDetail.setChannel("chanell");
        hotDetail.setMedia(chartDetail.getMedia());
        hotDetail.setPosition(chartDetail.getPosition());
        hotDetail.setPrevPosition(chartDetail.getPrevPosition());
        hotDetail.setChgPosition(chartDetail.getChgPosition());
        hotDetail.setPublishTimeMillis(chartDetail.getPublishTimeMillis());
        chartDetailRepository.save(hotDetail);

        ChartDetail otherDetail = new ChartDetail();
        otherDetail.setChart(otherChart);
        otherDetail.setChannel("chanell");
        otherDetail.setMedia(chartDetail.getMedia());
        otherDetail.setPosition(chartDetail.getPosition());
        otherDetail.setPrevPosition(chartDetail.getPrevPosition());
        otherDetail.setChgPosition(chartDetail.getChgPosition());
        otherDetail.setPublishTimeMillis(chartDetail.getPublishTimeMillis());
        chartDetailRepository.save(otherDetail);

        ChartDetail fourthDetail = new ChartDetail();
        fourthDetail.setChart(fourthChart);
        fourthDetail.setChannel("channell");
        fourthDetail.setMedia(chartDetail.getMedia());
        fourthDetail.setPosition(chartDetail.getPosition());
        fourthDetail.setPrevPosition(chartDetail.getPrevPosition());
        fourthDetail.setChgPosition(chartDetail.getChgPosition());
        fourthDetail.setPublishTimeMillis(chartDetail.getPublishTimeMillis());
        chartDetailRepository.save(fourthDetail);

        ChartDetail fifthDetail = new ChartDetail();
        fifthDetail.setChart(fifthChart);
        fifthDetail.setChannel("chanell");
        fifthDetail.setMedia(chartDetail.getMedia());
        fifthDetail.setPosition(chartDetail.getPosition());
        fifthDetail.setPrevPosition(chartDetail.getPrevPosition());
        fifthDetail.setChgPosition(chartDetail.getChgPosition());
        fifthDetail.setPublishTimeMillis(chartDetail.getPublishTimeMillis());
        chartDetailRepository.save(fifthDetail);

        ChartDetail videoDetail = new ChartDetail();
        videoDetail.setChart(videoChart);
        videoDetail.setMedia(chartDetail.getMedia());
        videoDetail.setPosition(chartDetail.getPosition());
        videoDetail.setPrevPosition(chartDetail.getPrevPosition());
        videoDetail.setChgPosition(chartDetail.getChgPosition());
        videoDetail.setPublishTimeMillis(chartDetail.getPublishTimeMillis());
        chartDetailRepository.save(videoDetail);

        MediaFile videoFile = chartDetail.getMedia().getAudioFile();
        videoFile.setDuration(10000);
        mediaFileRepository.save(videoFile);
    }
}
