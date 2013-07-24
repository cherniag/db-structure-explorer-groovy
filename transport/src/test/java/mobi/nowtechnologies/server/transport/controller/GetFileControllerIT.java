package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.FileType;
import mobi.nowtechnologies.server.persistence.domain.MediaFile;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.MediaFileRepository;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class GetFileControllerIT {
	
	private MockMvc mockMvc;

	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	@Qualifier("service.UserService")
	private UserService userService;
	
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
        String fileType = "VIDEO";
		String apiVersion = "4.0";
		String communityUrl = "o2";
		String timestamp = "2011_12_26_07_04_23";
		String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

        String mediaId = generateVideoMedia();
		
		ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_FILE")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("ID", mediaId)
                        .param("TYPE", fileType)
                        .header("Content-Type", "text/xml").
                        header("Content-Length", "0")
        ).andExpect(status().isOk());
		
		MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
		String resultXml = aHttpServletResponse.getContentAsString();
		
        assertTrue(resultXml.endsWith("2560690503001_hits.mp4"));
    }
    
    private String generateVideoMedia(){
        ChartDetail chartDetail = chartDetailRepository.findOne(22);

        FileType videoType = new FileType();
        videoType.setI(mobi.nowtechnologies.server.trackrepo.enums.FileType.VIDEO.getId().byteValue());

        MediaFile videoFile = chartDetail.getMedia().getAudioFile();
        videoFile.setDuration(10000);
        videoFile.setFilename("2560662929001");
        videoFile.setFileType(videoType);
        mediaFileRepository.save(videoFile);

        return chartDetail.getMedia().getIsrc();
    }
}
