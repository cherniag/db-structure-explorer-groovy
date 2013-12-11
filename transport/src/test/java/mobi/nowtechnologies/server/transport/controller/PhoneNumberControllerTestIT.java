package mobi.nowtechnologies.server.transport.controller;

import com.sentaca.spring.smpp.mo.MOMessage;
import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.o2.O2Service;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;
import mobi.nowtechnologies.server.service.sms.SMSMessageProcessorContainer;
import mobi.nowtechnologies.server.service.sms.SMSResponse;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;
import mobi.nowtechnologies.server.shared.Utils;
import org.jsmpp.bean.DeliverSm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smslib.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
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
		"classpath:META-INF/shared.xml",
        "classpath:META-INF/smpp.xml"}, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "transport.AccCheckController", webapp = "classpath:.")
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
//@Transactional
public class PhoneNumberControllerTestIT {
	
	private MockMvc mockMvc;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private SMSMessageProcessorContainer processorContainer;

    @Autowired
    @Qualifier("vf_nz.service.UserService")
    private UserService vfUserService;

    @Autowired
    @Qualifier("service.UserService")
    private UserService userService;

    @Autowired
    private O2ProviderServiceImpl o2ProviderService;

    @Autowired
    private O2Service o2Service;

    @Autowired
    @Qualifier("vf_nz.service.SmsProviderSpy")
    private VFNZSMSGatewayServiceImpl vfGatewayServiceSpy;

    private O2ProviderServiceImpl o2ProviderServiceSpy;
    private O2Service o2ServiceMock;

    @Before
    public void setUp() throws Exception {
        mockMvc = webApplicationContextSetup((WebApplicationContext)applicationContext).build();

        O2ProviderServiceImpl o2ProviderServiceTarget = o2ProviderService;
        o2ProviderServiceSpy = spy(o2ProviderServiceTarget);
        o2ServiceMock = mock(O2Service.class);

        o2ProviderServiceSpy.setO2Service(o2ServiceMock);
        userService.setMobileProviderService(o2ProviderServiceSpy);

        reset(vfGatewayServiceSpy);
    }

    @After
    public void tireDown(){
        o2ProviderService.setO2Service(o2Service);
        userService.setMobileProviderService(o2ProviderService);
    }

    @Test
    public void testActivatePhoneNumber_O2_Success() throws Exception {
        String userName = "+447111111113";
        String phone = "+447111111113";
        String apiVersion = "4.0";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        O2SubscriberData subscriberData = new O2SubscriberData();
        subscriberData.setTariff4G(true);
        subscriberData.setBusinessOrConsumerSegment(true);
        subscriberData.setDirectOrIndirect4GChannel(true);
        subscriberData.setContractPostPayOrPrePay(true);
        subscriberData.setProviderO2(true);
        doReturn(subscriberData).when(o2ServiceMock).getSubscriberData(phone);
        doReturn(new PhoneNumberValidationData().withPhoneNumber(phone)).when(o2ProviderServiceSpy).validatePhoneNumber(phone);

        ResultActions resultActions = mockMvc.perform(
                post("/some_key/"+communityUrl+"/"+apiVersion+"/PHONE_NUMBER")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("PHONE", phone)
        ).andExpect(status().isOk());

        verify(o2ServiceMock, times(1)).getSubscriberData(phone);
        verify(o2ProviderServiceSpy, times(1)).validatePhoneNumber(phone);

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<activation>ENTERED_NUMBER</activation><phoneNumber>+447111111113</phoneNumber>"));

        resultActions = mockMvc.perform(
                post("/someid/"+communityUrl+"/"+apiVersion+"/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());

        aHttpServletResponse = resultActions.andReturn().getResponse();
        resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<provider>o2</provider>"));
        assertTrue(resultXml.contains("<hasAllDetails>true</hasAllDetails>"));
    }

    @Test
    public void testActivatePhoneNumber_Promoted_O2_Success() throws Exception {
        String userName = "+447111111114";
        String phone = "+447111111114";
        String apiVersion = "4.0";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        O2SubscriberData subscriberData = new O2SubscriberData();
        subscriberData.setTariff4G(true);
        subscriberData.setBusinessOrConsumerSegment(true);
        subscriberData.setDirectOrIndirect4GChannel(true);
        subscriberData.setContractPostPayOrPrePay(true);
        subscriberData.setProviderO2(true);
        doReturn(subscriberData).when(o2ServiceMock).getSubscriberData(phone);
        doReturn(new PhoneNumberValidationData().withPhoneNumber(phone)).when(o2ProviderServiceSpy).validatePhoneNumber(phone);

        ResultActions resultActions = mockMvc.perform(
                post("/some_key/"+communityUrl+"/"+apiVersion+"/PHONE_NUMBER")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("PHONE", phone)
        ).andExpect(status().isOk());

        verify(o2ServiceMock, times(0)).getSubscriberData(phone);
        verify(o2ProviderServiceSpy, times(1)).validatePhoneNumber(phone);

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<activation>ENTERED_NUMBER</activation><phoneNumber>+447111111114</phoneNumber>"));

        resultActions = mockMvc.perform(
                post("/someid/"+communityUrl+"/"+apiVersion+"/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());

        aHttpServletResponse = resultActions.andReturn().getResponse();
        resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<provider>o2</provider>"));
        assertTrue(resultXml.contains("<hasAllDetails>true</hasAllDetails>"));
    }

    @Test
    public void testActivatePhoneNumber_NZ_VF_Success() throws Exception {
    	String userName = "+642102247311";
    	String phone = "+642102247311";
		String apiVersion = "5.0";
		String communityName = "vf_nz";
		String communityUrl = "vf_nz";
		String timestamp = "2011_12_26_07_04_23";
		String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
		String userToken = Utils.createTimestampToken(storedToken, timestamp);
        User user = vfUserService.findByNameAndCommunity(userName, communityName);
        user.setProvider(null);
        vfUserService.updateUser(user);

        doAnswer(new Answer<SMSResponse>() {
            @Override
            public SMSResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new SMSResponse() {
                    @Override
                    public String getMessage() {
                        return "";
                    }

                    @Override
                    public boolean isSuccessful() {
                        return true;
                    }
                };
            }
        }).when(vfGatewayServiceSpy).send(eq("+642102247311"), anyString(), eq("4003"));

		ResultActions resultActions = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/PHONE_NUMBER")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("PHONE", phone)
        ).andExpect(status().isOk());
		
		MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
		String resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><phoneActivation><activation>ENTERED_NUMBER</activation><phoneNumber>+642102247311</phoneNumber></phoneActivation></response>"));

        user = vfUserService.findByNameAndCommunity(userName, communityName);
        assertEquals(null, user.getProvider());

        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSourceAddr("5804");
        deliverSm.setDestAddress("642102247311");
        MOMessage message = new MOMessage("5804", "642102247311", "OnNet", Message.MessageEncodings.ENC8BIT);
        processorContainer.processInboundMessage(deliverSm, message);

        Thread.sleep(1000);

        resultActions = mockMvc.perform(
                post("/someid/"+communityUrl+"/"+apiVersion+"/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());

        aHttpServletResponse = resultActions.andReturn().getResponse();
        resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<provider>vf</provider>"));
        assertTrue(resultXml.contains("<hasAllDetails>true</hasAllDetails>"));

        verify(vfGatewayServiceSpy, times(1)).send(eq("+642102247311"), anyString(), eq("4003"));
    }

    @Test
    public void testActivatePhoneNumber_NZ_NON_VF_Success() throws Exception {
        String userName = "+64279000456";
        String phone = "+64279000456";
        String apiVersion = "5.0";
        String communityName = "vf_nz";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        User user = vfUserService.findByNameAndCommunity(userName, communityName);
        user.setProvider(null);
        vfUserService.updateUser(user);

        ResultActions resultActions = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/PHONE_NUMBER")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("PHONE", phone)
        ).andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><phoneActivation><activation>ENTERED_NUMBER</activation><phoneNumber>+64279000456</phoneNumber></phoneActivation></response>"));

        user = vfUserService.findByNameAndCommunity(userName, communityName);
        assertEquals(null, user.getProvider());

        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSourceAddr("5804");
        deliverSm.setDestAddress("642102247311");
        MOMessage message = new MOMessage("5804", "64279000456", "OffNet", Message.MessageEncodings.ENC8BIT);
        processorContainer.processInboundMessage(deliverSm, message);

        resultActions = mockMvc.perform(
                post("/someid/"+communityUrl+"/"+apiVersion+"/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());

        aHttpServletResponse = resultActions.andReturn().getResponse();
        resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<provider>non-vf</provider>"));
        assertTrue(resultXml.contains("<hasAllDetails>true</hasAllDetails>"));
        verify(vfGatewayServiceSpy, times(1)).send(eq("+64279000456"), anyString(), eq("4003"));
    }

    @Test
    public void testResendPinOnActivatePhoneNumber() throws Exception {
        String userName = "+64279000456";
        String apiVersion = "5.0";
        String communityName = "vf_nz";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        User user = vfUserService.findByNameAndCommunity(userName, communityName);
        user.setProvider(null);
        vfUserService.updateUser(user);
        ResultActions resultActions = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/PHONE_NUMBER")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultXml = aHttpServletResponse.getContentAsString();
        assertTrue(resultXml.contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><phoneActivation><activation>ENTERED_NUMBER</activation><phoneNumber>+64279000456</phoneNumber></phoneActivation></response>"));
        user = vfUserService.findByNameAndCommunity(userName, communityName);
        assertEquals(null, user.getProvider());
        verify(vfGatewayServiceSpy, times(1)).send(eq("+64279000456"), anyString(), eq("4003"));
    }
}