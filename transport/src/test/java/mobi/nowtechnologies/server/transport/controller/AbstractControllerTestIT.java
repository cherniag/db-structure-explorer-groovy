package mobi.nowtechnologies.server.transport.controller;

import com.jayway.jsonpath.JsonPath;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.job.UpdateO2UserTask;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.utils.SQLTestInitializer;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.impl.OtacValidationServiceImpl;
import mobi.nowtechnologies.server.service.o2.O2Service;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderServiceImpl;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({
        @ContextConfiguration(locations = {
                "classpath:transport-root-test.xml"}),
        @ContextConfiguration(locations = {
                "classpath:transport-servlet-test.xml"})})
@WebAppConfiguration
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
public abstract class AbstractControllerTestIT {

    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private O2ProviderServiceImpl o2ProviderService;

    @Autowired
    private OtacValidationServiceImpl otacValidationService;

    @Autowired
    private ApplyInitPromoController applyInitPromoController;

    @Autowired
    private O2Service o2Service;

    @Autowired
    private UpdateO2UserTask updateO2UserTask;

    protected O2ProviderServiceImpl o2ProviderServiceSpy;
    protected UpdateO2UserTask updateO2UserTaskSpy;
    protected O2Service o2ServiceMock;

    @Autowired
    @Qualifier("service.UserService")
    protected UserService userService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    private SQLTestInitializer sqlTestInitializer;

    private JsonPath jsonPath = JsonPath.compile("$.response.data[0].user");

    private ObjectMapper objectMapper = new ObjectMapper();

    @After
    public void tireDown() {
        o2ProviderService.setO2Service(o2Service);
        userService.setMobileProviderService(o2ProviderService);
        ReflectionTestUtils.setField(applyInitPromoController, "updateO2UserTask", updateO2UserTaskSpy);
        sqlTestInitializer.cleanDynamicTestData();
    }

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(applicationContext).build();

        O2ProviderServiceImpl o2ProviderServiceTarget = o2ProviderService;
        o2ProviderServiceSpy = spy(o2ProviderServiceTarget);
        updateO2UserTaskSpy = spy(updateO2UserTask);
        o2ServiceMock = mock(O2Service.class);

        o2ProviderServiceSpy.setO2Service(o2ServiceMock);
        userService.setMobileProviderService(o2ProviderServiceSpy);
        userService.setO2ClientService(o2ProviderServiceSpy);
        otacValidationService.setO2ProviderService(o2ProviderServiceSpy);
        ReflectionTestUtils.setField(applyInitPromoController, "updateO2UserTask", updateO2UserTaskSpy);

        sqlTestInitializer.prepareDynamicTestData("classpath:META-INF/dynamic-test-data.sql");
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
}