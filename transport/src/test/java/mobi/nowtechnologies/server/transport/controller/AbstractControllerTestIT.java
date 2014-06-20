package mobi.nowtechnologies.server.transport.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.job.UpdateO2UserTask;
import mobi.nowtechnologies.server.log4j.InMemoryEventAppender;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.*;
import mobi.nowtechnologies.server.persistence.utils.SQLTestInitializer;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.impl.OtacValidationServiceImpl;
import mobi.nowtechnologies.server.service.impl.details.O2ProviderDetailsExtractor;
import mobi.nowtechnologies.server.service.o2.O2Service;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderServiceImpl;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.io.IOException;

import static mobi.nowtechnologies.server.persistence.domain.Promotion.ADD_FREE_WEEKS_PROMOTION;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType.NEWS;
import static mobi.nowtechnologies.server.shared.enums.ChgPosition.DOWN;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
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
    protected O2ProviderServiceImpl o2ProviderService;

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

    @Resource(name = "promotionRepository")
    private PromotionRepository promotionRepository;

    @Resource(name = "promoCodeRepository")
    private PromoCodeRepository promoCodeRepository;

    @Resource(name = "userGroupRepository")
    private UserGroupRepository userGroupRepository;

    @Resource(name = "chartDetailRepository")
    private ChartDetailRepository chartDetailRepository;

    @Resource(name = "chartRepository")
    private ChartRepository chartRepository;

    @Resource(name = "mediaRepository")
    private MediaRepository mediaRepository;

    @Resource(name = "messageRepository")
    private MessageRepository messageRepository;

    @Resource
    private O2ProviderDetailsExtractor o2ProviderDetailsExtractor;

    private InMemoryEventAppender inMemoryEventAppender = new InMemoryEventAppender();

    private static int position = 0;
    private static Promotion promotion;
    private static Message message;
    private static ChartDetail chartDetail;

    @After
    public void tireDown() {
        o2ProviderService.setO2Service(o2Service);
        userService.setMobileProviderService(o2ProviderService);
        ReflectionTestUtils.setField(applyInitPromoController, "updateO2UserTask", updateO2UserTaskSpy);
        sqlTestInitializer.cleanDynamicTestData();
        Logger.getRootLogger().removeAppender(inMemoryEventAppender);
    }

    @Before
    public void setUp() throws Exception {
        Logger.getRootLogger().addAppender(inMemoryEventAppender);

        mockMvc = webAppContextSetup(applicationContext).build();

        O2ProviderServiceImpl o2ProviderServiceTarget = o2ProviderService;
        o2ProviderServiceSpy = spy(o2ProviderServiceTarget);
        updateO2UserTaskSpy = spy(updateO2UserTask);
        o2ServiceMock = mock(O2Service.class);

        o2ProviderServiceSpy.setO2Service(o2ServiceMock);
        userService.setMobileProviderService(o2ProviderServiceSpy);
        userService.setO2ClientService(o2ProviderServiceSpy);
        ReflectionTestUtils.setField(o2ProviderDetailsExtractor, "o2ProviderService", o2ProviderServiceSpy);
        ReflectionTestUtils.setField(applyInitPromoController, "updateO2UserTask", updateO2UserTaskSpy);

        sqlTestInitializer.prepareDynamicTestData("classpath:META-INF/dynamic-test-data.sql");

        UserGroup userGroup = userGroupRepository.findOne(9);

        if (isNull(promotion)) {
            promotion = promotionRepository.save(new Promotion().withUserGroup(userGroup).withDescription("").withEndDate(Integer.MAX_VALUE).withIsActive(true).withFreeWeeks((byte) 8).withType(ADD_FREE_WEEKS_PROMOTION));

            promoCodeRepository.save(new PromoCode().withPromotion(promotion).withCode("promo8").withMediaType(AUDIO));
        }
        Community community = userGroup.getCommunity();

        chartDetail = chartDetailRepository.save(new ChartDetail().withChart(userGroup.getChart()).withMedia(mediaRepository.findOne(50)).withPrevPosition((byte) 1)
                .withChgPosition(DOWN)
                .withChannel("HEATSEEKER"));

        message = messageRepository.save(new Message().withMessageType(NEWS).withPosition(position++).withCommunity(community).withBody("").withPublishTimeMillis(1).withTitle("").withActivated(true));
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

    protected void validateLoggingForClass(Class loggerClass, Class throwableClass, int expectedForCritical, int expectedForWarn, int totalCountWithStackTrace) {
        assertEquals(expectedForCritical, inMemoryEventAppender.countOfErrorsWithStackTraceForLogger(loggerClass));
        assertEquals(expectedForWarn, inMemoryEventAppender.countOfWarnWithStackTraceForLogger(loggerClass));
        assertEquals(totalCountWithStackTrace, inMemoryEventAppender.totalCountOfMessagesWithStackTraceForException(throwableClass));
    }

}