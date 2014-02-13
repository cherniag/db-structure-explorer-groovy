package mobi.nowtechnologies.server.transport.controller;

import com.jayway.jsonpath.JsonPath;
import mobi.nowtechnologies.server.job.UpdateO2UserTask;
import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.utils.SQLTestInitializer;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.impl.OtacValidationServiceImpl;
import mobi.nowtechnologies.server.service.o2.O2Service;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.springframework.test.web.server.setup.MockMvcBuilders.webApplicationContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:transport-servlet-test.xml",
        "classpath:task-processors.xml",
        "classpath:META-INF/service-test.xml",
        "classpath:META-INF/service-mocked-test.xml",
        "classpath:META-INF/soap.xml",
        "classpath:META-INF/dao-test.xml",
        "classpath:META-INF/smpp.xml",
        "classpath:META-INF/soap.xml",
        "classpath:META-INF/shared.xml"}, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "transport.controller", webapp = "classpath:.")
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
public abstract class AbstractControllerTestIT {

    protected MockMvc mockMvc;

    @Autowired
    private ApplicationContext applicationContext;

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

    @After
    public void tireDown() {
        o2ProviderService.setO2Service(o2Service);
        userService.setMobileProviderService(o2ProviderService);
        applyInitPromoController.setUpdateO2UserTask(updateO2UserTaskSpy);
        sqlTestInitializer.cleanDynamicTestData();
    }

    @Before
    public void setUp() throws Exception {
        mockMvc = webApplicationContextSetup((WebApplicationContext) applicationContext).build();

        O2ProviderServiceImpl o2ProviderServiceTarget = o2ProviderService;
        o2ProviderServiceSpy = spy(o2ProviderServiceTarget);
        updateO2UserTaskSpy = spy((UpdateO2UserTask) ((Advised) updateO2UserTask).getTargetSource().getTarget());
        o2ServiceMock = mock(O2Service.class);

        o2ProviderServiceSpy.setO2Service(o2ServiceMock);
        userService.setMobileProviderService(o2ProviderServiceSpy);
        userService.setO2ClientService(o2ProviderServiceSpy);
        otacValidationService.setO2ProviderService(o2ProviderServiceSpy);
        applyInitPromoController.setUpdateO2UserTask(updateO2UserTaskSpy);

        sqlTestInitializer.prepareDynamicTestData("classpath:META-INF/dynamic-test-data.sql");
    }

    private net.minidev.json.JSONObject getAccCheckContent(final String contentAsString) {
        return jsonPath.read(contentAsString);
    }

    protected net.minidev.json.JSONObject getAccCheckContent(final ResultActions resultActions) throws UnsupportedEncodingException {
        return getAccCheckContent(resultActions.andReturn().getResponse().getContentAsString());
    }

    protected void checkAccountCheck(ResultActions actionCall, ResultActions accountCheckCall) throws UnsupportedEncodingException {
        assertEquals(getAccCheckContent(actionCall), getAccCheckContent(accountCheckCall));
    }
}