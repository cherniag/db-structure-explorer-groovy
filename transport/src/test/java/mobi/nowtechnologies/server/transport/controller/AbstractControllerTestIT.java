package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.job.UpdateO2UserTask;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.MediaRepository;
import mobi.nowtechnologies.server.persistence.repository.MessageRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.utils.SQLTestInitializer;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.impl.details.O2ProviderDetailsExtractor;
import mobi.nowtechnologies.server.service.o2.O2Service;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.O2ServiceImpl;
import static mobi.nowtechnologies.server.shared.enums.ChgPosition.DOWN;
import static mobi.nowtechnologies.server.shared.enums.MessageType.NEWS;

import javax.annotation.Resource;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import static com.google.common.net.HttpHeaders.IF_MODIFIED_SINCE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.WebApplicationContext;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({@ContextConfiguration(locations = {"classpath:transport-root-test.xml"}), @ContextConfiguration(locations = {"classpath:transport-servlet-test.xml"})})
@WebAppConfiguration
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
public abstract class AbstractControllerTestIT {

    public static final String LATEST_SERVER_API_VERSION = "6.11";
    private static AtomicInteger position = new AtomicInteger(0);
    protected MockMvc mockMvc;
    @Autowired
    protected O2ProviderServiceImpl o2ProviderService;
    @Autowired
    protected O2ServiceImpl o2Service;
    protected O2ProviderServiceImpl o2ProviderServiceSpy;
    protected UpdateO2UserTask updateO2UserTaskSpy;
    protected O2Service o2ServiceMock;
    @Autowired
    @Qualifier("service.UserService")
    protected UserService userService;
    @Autowired
    protected UserRepository userRepository;
    @Resource(name = "messageRepository")
    MessageRepository messageRepository;
    @Autowired
    private WebApplicationContext applicationContext;
    @Autowired
    private ApplyInitPromoController applyInitPromoController;
    @Autowired
    private UpdateO2UserTask updateO2UserTask;
    @Autowired
    private SQLTestInitializer sqlTestInitializer;
    private JsonPath jsonPath = JsonPath.compile(AccountCheckResponseConstants.USER_JSON_PATH);
    private ObjectMapper objectMapper = new ObjectMapper();
    @Resource(name = "userGroupRepository")
    private UserGroupRepository userGroupRepository;
    @Resource(name = "chartDetailRepository")
    private ChartDetailRepository chartDetailRepository;
    @Resource(name = "mediaRepository")
    private MediaRepository mediaRepository;
    @Resource
    private O2ProviderDetailsExtractor o2ProviderDetailsExtractor;

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(applicationContext).build();

        O2ProviderServiceImpl o2ProviderServiceTarget = o2ProviderService;
        o2ProviderServiceSpy = spy(o2ProviderServiceTarget);
        updateO2UserTaskSpy = spy(updateO2UserTask);
        o2ServiceMock = mock(O2Service.class);

        o2ProviderServiceSpy.setO2Service(o2ServiceMock);
        userService.setMobileProviderService(o2ProviderServiceSpy);
        ReflectionTestUtils.setField(o2ProviderDetailsExtractor, "o2ProviderService", o2ProviderServiceSpy);
        ReflectionTestUtils.setField(applyInitPromoController, "updateO2UserTask", updateO2UserTaskSpy);

        sqlTestInitializer.prepareDynamicTestData("classpath:META-INF/dynamic-test-data.sql");

        UserGroup userGroup = userGroupRepository.findOne(9);

        Community community = userGroup.getCommunity();

        chartDetailRepository.save(new ChartDetail().withChart(userGroup.getChart()).withMedia(mediaRepository.findOne(50)).withPrevPosition((byte) 1).withChgPosition(DOWN).withChannel("HEATSEEKER")
                                                    .withPublishTime(new Date().getTime()));

        messageRepository
            .save(new Message().withMessageType(NEWS).withPosition(position.getAndIncrement()).withCommunity(community).withBody("").withPublishTimeMillis(1).withTitle("").withActivated(true));
    }

    @After
    public void tireDown() {
        o2ProviderService.setO2Service(o2Service);
        userService.setMobileProviderService(o2ProviderService);
        ReflectionTestUtils.setField(applyInitPromoController, "updateO2UserTask", updateO2UserTaskSpy);
        sqlTestInitializer.cleanDynamicTestData();
    }


    private net.minidev.json.JSONObject getAccCheckContentAsJsonObject(final ResultActions resultActions) throws IOException {
        return jsonPath.read(resultActions.andReturn().getResponse().getContentAsString());
    }

    protected AccountCheckDto getAccCheckContent(final ResultActions resultActions) throws IOException {
        return objectMapper.readValue(getAccCheckContentAsJsonObject(resultActions).toJSONString(), AccountCheckDto.class);
    }

    protected void checkAccountCheck(ResultActions actionCall, ResultActions accountCheckCall) throws IOException {
        assertEquals(getAccCheckContentAsJsonObject(actionCall), getAccCheckContentAsJsonObject(accountCheckCall));
    }

    protected HttpHeaders getHttpHeadersWithIfModifiedSince(Object value) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (value instanceof Long) {
            httpHeaders.setIfModifiedSince((Long) value);
        }
        if (value instanceof String) {
            httpHeaders.set(IF_MODIFIED_SINCE, (String) value);
        }
        if (value instanceof Date) {
            httpHeaders.setIfModifiedSince(((Date) value).getTime());
        }
        return httpHeaders;
    }


}