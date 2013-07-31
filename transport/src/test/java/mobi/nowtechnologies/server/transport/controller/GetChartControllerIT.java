package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.setup.MockMvcBuilders.webApplicationContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:transport-servlet-test.xml",
		"classpath:META-INF/service-test.xml",
		"classpath:META-INF/soap.xml",
		"classpath:META-INF/dao-test.xml",
		"classpath:META-INF/soap.xml",
		"classpath:META-INF/shared.xml" }, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "transport.AccCheckController", webapp = "classpath:.")
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class GetChartControllerIT {
	
	private MockMvc mockMvc;

	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private ChartRepository chartRepository;

	@Autowired
	private ChartDetailRepository chartDetailRepository;

    @Autowired
	private CommunityRepository communityRepository;

    @Autowired
	private MediaFileRepository mediaFileRepository;

    @Before
    public void setUp() {
        mockMvc = webApplicationContextSetup((WebApplicationContext)applicationContext).build();
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
		
		ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());
		
		MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
		String resultXml = aHttpServletResponse.getContentAsString();
		
        assertTrue(resultXml.contains("<type>VIDEO_CHART</type>"));
        assertTrue(resultXml.contains("<duration>10000</duration>"));
        assertTrue(!resultXml.contains("<bonusTrack>"));
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

		ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
                        .param("APP_VERSION", apiVersion)
                        .param("COMMUNITY_NAME", apiVersion)
        ).andExpect(status().isOk());

		MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
		String resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(!resultXml.contains("<type>VIDEO_CHART</type>"));
        assertTrue(!resultXml.contains("<bonusTrack>"));
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

		ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
                        .param("APP_VERSION", apiVersion)
                        .param("COMMUNITY_NAME", apiVersion)
        ).andExpect(status().isOk());

		MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
		String resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(!resultXml.contains("<type>VIDEO_CHART</type>"));
        assertTrue(!resultXml.contains("<type>FOURTH_CHART</type>"));
        assertTrue(!resultXml.contains("<type>FIFTH_CHART</type>"));
        assertTrue(!resultXml.contains("<bonusTrack>"));
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

        generateChartAllTypesForO2();

		ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
                        .param("APP_VERSION", apiVersion)
                        .param("API_VERSION", apiVersion)
                        .param("COMMUNITY_NAME", apiVersion)
        ).andExpect(status().isOk());

		MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
		String resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(!resultXml.contains("<type>VIDEO_CHART</type>"));
        assertTrue(!resultXml.contains("<type>FOURTH_CHART</type>"));
        assertTrue(!resultXml.contains("<type>FIFTH_CHART</type>"));
        assertTrue(resultXml.contains("<bonusTrack>"));
    }
    
    private void generateChartAllTypesForO2(){
        Community o2Community = communityRepository.findOne((byte)7);
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
