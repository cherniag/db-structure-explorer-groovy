package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;
import mobi.nowtechnologies.server.service.sms.SMSMessageProcessorContainer;
import mobi.nowtechnologies.server.service.sms.SMSResponse;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import java.io.ByteArrayInputStream;

import com.sentaca.spring.smpp.mo.MOMessage;
import org.jsmpp.bean.DeliverSm;
import org.smslib.Message;
import org.w3c.dom.Document;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.junit.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

public class PhoneNumberControllerTestIT extends AbstractControllerTestIT {

    @Resource(name = "vf_nz.service.SMPPProcessorContainer")
    private SMSMessageProcessorContainer processorContainer;

    @Resource(name = "vf_nz.service.UserService")
    private UserService vfUserService;

    @Resource(name = "vf_nz.service.SmsProviderSpy")
    private VFNZSMSGatewayServiceImpl vfGatewayServiceSpy;

    @Test
    public void testActivatePhoneNumber_LatestVersion() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a";
        String phone = "+64279000456";
        String apiVersion = LATEST_SERVER_API_VERSION;
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc
            .perform(post("/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER.json").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("PHONE", phone))
            .andExpect(status().isOk()).andExpect(content().string(("{\"response\":{\"data\":" +
                                                                    "[{\"phoneActivation\":" +
                                                                    "{\"activation\":\"ENTERED_NUMBER\",\"phoneNumber\":\"+64279000456\"}" +
                                                                    "}]}}")));
    }

    @Test
    public void testActivatePhoneNumber_O2_Success() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a444";
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

        mockMvc.perform(
            post("/some_key/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("PHONE", phone))
               .andExpect(status().isOk()).andExpect(xpath("/response/phoneActivation/activation").string("ENTERED_NUMBER"))
               .andExpect(xpath("/response/phoneActivation/phoneNumber").string("+447111111113"));

        verify(o2ServiceMock, times(1)).getSubscriberData(phone);
        verify(o2ProviderServiceSpy, times(1)).validatePhoneNumber(phone);


        mockMvc.perform(post("/someid/" + communityUrl + "/" + apiVersion + "/ACC_CHECK").param("COMMUNITY_NAME", communityName).param("USER_NAME", userName).param("USER_TOKEN", userToken)
                                                                                         .param("TIMESTAMP", timestamp)).andExpect(status().isOk()).andDo(print()).
                   andExpect(xpath("/response/user/provider").string("o2")).
                   andExpect(xpath("/response/user/hasAllDetails").booleanValue(true));

    }

    @Test
    public void testActivatePhoneNumber_Promoted_O2_Success() throws Exception {
        String userName = "b88106713409e92622461a876abcd74b444";
        String phone = "+447111111114";
        String apiVersion = "4.0";
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

        mockMvc.perform(
            post("/some_key/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("PHONE", phone))
               .andExpect(status().isOk()).andExpect(xpath("/response/phoneActivation/activation").string("ENTERED_NUMBER"))
               .andExpect(xpath("/response/phoneActivation/phoneNumber").string("+447111111114"));
        ;

        verify(o2ServiceMock, times(0)).getSubscriberData(phone);
        verify(o2ProviderServiceSpy, times(1)).validatePhoneNumber(phone);

        mockMvc.perform(post("/someid/" + communityUrl + "/" + apiVersion + "/ACC_CHECK").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp))
               .andExpect(status().isOk()).
            andExpect(xpath("/response/user/provider").string("o2")).
                   andExpect(xpath("/response/user/hasAllDetails").booleanValue(true));
    }

    @Test
    public void testActivatePhoneNumber_NZ_VF_Success() throws Exception {
        String userName = "b88106713409e92622461a876abcd74b";
        String phone = "+642102247311";
        String apiVersion = "5.0";
        String communityName = "vf_nz";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityName);
        user.setProvider(null);
        vfUserService.updateUser(user);

        doAnswer(new Answer<SMSResponse>() {
            @Override
            public SMSResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new SMSResponse() {
                    @Override
                    public boolean isSuccessful() {
                        return true;
                    }

                    @Override
                    public String getDescriptionError() {
                        return null;
                    }
                };
            }
        }).when(vfGatewayServiceSpy).send(eq("+642102247311"), anyString(), eq("4003"));

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("PHONE", phone))
               .andExpect(status().isOk()).andExpect(content().string(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><phoneActivation><activation>ENTERED_NUMBER</activation><phoneNumber>+642102247311</phoneNumber></phoneActivation" +
            "></response>"));


        user = userRepository.findByUserNameAndCommunityUrl(userName, communityName);
        assertEquals(null, user.getProvider());

        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSourceAddr("5804");
        deliverSm.setDestAddress("642102247311");
        MOMessage message = new MOMessage("5804", "642102247311", "OnNet", Message.MessageEncodings.ENC8BIT);
        processorContainer.processInboundMessage(deliverSm, message);

        Thread.sleep(1000);

        mockMvc.perform(post("/someid/" + communityUrl + "/" + apiVersion + "/ACC_CHECK").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp))
               .andExpect(status().isOk()).
            andExpect(xpath("/response/user/provider").string("vf")).
                   andExpect(xpath("/response/user/hasAllDetails").booleanValue(true));

        verify(vfGatewayServiceSpy, times(1)).send(eq("+642102247311"), anyString(), eq("4003"));
    }

    @Test
    public void testActivatePhoneNumber_NZ_NON_VF_Success() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a";
        String phone = "+64279000456";
        String apiVersion = "5.0";
        String communityName = "vf_nz";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityName);
        user.setProvider(null);
        vfUserService.updateUser(user);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("PHONE", phone))
               .andExpect(status().isOk()).andExpect(content().string(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><phoneActivation><activation>ENTERED_NUMBER</activation><phoneNumber>+64279000456</phoneNumber></phoneActivation" +
            "></response>"));


        user = userRepository.findByUserNameAndCommunityUrl(userName, communityName);
        assertEquals(null, user.getProvider());

        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSourceAddr("5804");
        deliverSm.setDestAddress("642102247311");
        MOMessage message = new MOMessage("5804", "64279000456", "OffNet", Message.MessageEncodings.ENC8BIT);
        processorContainer.processInboundMessage(deliverSm, message);

        Thread.sleep(1000);

        mockMvc.perform(post("/someid/" + communityUrl + "/" + apiVersion + "/ACC_CHECK").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp))
               .andExpect(status().isOk()).
            andExpect(xpath("/response/user/provider").string("non-vf")).
                   andExpect(xpath("/response/user/hasAllDetails").booleanValue(true));

        verify(vfGatewayServiceSpy, times(1)).send(eq("+64279000456"), anyString(), eq("4003"));
    }

    @Test
    public void testResendPinOnActivatePhoneNumber_VFNZ() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a";
        String apiVersion = "5.0";
        String communityName = "vf_nz";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("COMMUNITY_NAME", communityName).param("USER_NAME", userName).param("USER_TOKEN", userToken)
                                                                                     .param("TIMESTAMP", timestamp)).andExpect(status().isOk()).andExpect(content().string(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><phoneActivation><activation>ENTERED_NUMBER</activation><phoneNumber>+64279000456</phoneNumber></phoneActivation" +
            "></response>"));


        verify(vfGatewayServiceSpy, times(0)).send(eq("+64279000456"), anyString(), eq("5804"));

        Thread.sleep(1000);

        verify(vfGatewayServiceSpy, times(1)).send(eq("+64279000456"), anyString(), eq("4003"));
    }

    @Test
    public void testResendPinOnActivatePhoneNumber_O2() throws Exception {
        String userName = "b88106713409e92622461a876abcd74b555";
        String apiVersion = "3.6";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        RestTemplate restTemplate = new RestTemplate(){
            @Override
            public <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
                try {
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document document = documentBuilder.parse(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><user><msisdn>+447111111114</msisdn></user>".getBytes()));

                    return (T) new DOMSource(document);
                } catch (Exception e) {
                    throw new RestClientException(e.getMessage(), e);
                }
            }
        };

        o2Service.setRestTemplate(restTemplate);
        o2ProviderServiceSpy.setO2Service(o2Service);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("COMMUNITY_NAME", communityName).param("USER_NAME", userName).param("USER_TOKEN", userToken)
                                                                                     .param("TIMESTAMP", timestamp)).andExpect(status().isOk()).andExpect(
            content().string(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><phoneActivation><activation>ENTERED_NUMBER</activation><phoneNumber>+447111111114</phoneNumber" +
                "><redeemServerUrl" +
                ">http://uat.mqapi.com</redeemServerUrl></phoneActivation></response>"));
    }

    @Test
    @Transactional
    public void testActivatePhoneNumberNZ_VF_NotPresentUserWithoutMobile() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a";
        String apiVersion = "5.0";
        String communityName = "vf_nz";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        resetMobile(userName, communityName);

        mockMvc.perform(post("/somekey/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("COMMUNITY_NAME", communityName).param("USER_NAME", userName).param("USER_TOKEN", userToken)
                                                                                             .param("TIMESTAMP", timestamp)).andExpect(status().isForbidden())
               .andExpect(xpath("/response/errorMessage/errorCode").string("604"));
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
            post("/some_key/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("PHONE", phone))
               .andExpect(status().isOk()).andExpect(xpath("/response/errorMessage/errorCode").number(601d));
    }

    @Test
    @Transactional
    public void testActivatePhoneNumberNZ_VF_EmptyUserWithoutMobile() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a";
        String phone = "";
        String apiVersion = "5.0";
        String communityName = "vf_nz";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        resetMobile(userName, communityName);

        mockMvc.perform(post("/somekey/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("COMMUNITY_NAME", communityName).param("USER_NAME", userName).param("USER_TOKEN", userToken)
                                                                                             .param("TIMESTAMP", timestamp).param("PHONE", phone)).andExpect(status().isForbidden())
               .andExpect(xpath("/response/errorMessage/errorCode").string("604"));
    }

    @Test
    @Transactional
    public void testActivatePhoneNumberNZ_VF_NotPresentUserWithMobile() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a";
        String apiVersion = "5.0";
        String communityName = "vf_nz";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(post("/somekey/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("COMMUNITY_NAME", communityName).param("USER_NAME", userName).param("USER_TOKEN", userToken)
                                                                                             .param("TIMESTAMP", timestamp)).andExpect(status().isOk())
               .andExpect(xpath("/response/errorMessage/errorCode").doesNotExist()).andExpect(xpath("/response/phoneActivation/activation").string(ActivationStatus.ENTERED_NUMBER.name()));
    }

    @Test
    @Transactional
    public void testActivatePhoneNumberNZ_VF_EmptyUserWithMobile() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a";
        String phone = "";
        String apiVersion = "5.0";
        String communityName = "vf_nz";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        mockMvc.perform(post("/somekey/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("COMMUNITY_NAME", communityName).param("USER_NAME", userName).param("USER_TOKEN", userToken)
                                                                                             .param("TIMESTAMP", timestamp).param("PHONE", phone)).andExpect(status().isOk())
               .andExpect(xpath("/response/errorMessage/errorCode").string("601"));
    }

    @Test
    @Transactional
    public void testActivatePhoneNumberO2_NotPresentUserWithoutMobile() throws Exception {
        String userName = "b88106713409e92622461a876abcd74b444";
        String apiVersion = "4.0";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        resetMobile(userName, communityName);

        mockMvc.perform(post("/somekey/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("COMMUNITY_NAME", communityName).param("USER_NAME", userName).param("USER_TOKEN", userToken)
                                                                                             .param("TIMESTAMP", timestamp)).andExpect(status().isOk())
               .andExpect(xpath("/response/errorMessage/errorCode").string("601"));
    }

    @Test
    @Transactional
    public void testActivatePhoneNumberO2_EmptyUserWithoutMobile() throws Exception {
        String userName = "b88106713409e92622461a876abcd74b444";
        String phone = "";
        String apiVersion = "4.0";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        resetMobile(userName, communityName);

        mockMvc.perform(post("/somekey/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("COMMUNITY_NAME", communityName).param("USER_NAME", userName).param("USER_TOKEN", userToken)
                                                                                             .param("TIMESTAMP", timestamp).param("PHONE", phone)).andExpect(status().isOk())
               .andExpect(xpath("/response/errorMessage/errorCode").string("601"));
    }

    @Test
    @Transactional
    public void testActivatePhoneNumberO2_NotPresentUserWithMobile() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a444";
        String apiVersion = "4.0";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(post("/somekey/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("COMMUNITY_NAME", communityName).param("USER_NAME", userName).param("USER_TOKEN", userToken)
                                                                                             .param("TIMESTAMP", timestamp)).andExpect(status().isOk())
               .andExpect(xpath("/response/errorMessage/errorCode").doesNotExist()).andExpect(xpath("/response/phoneActivation/activation").string(ActivationStatus.ENTERED_NUMBER.name()));
    }

    @Test
    @Transactional
    public void testActivatePhoneNumberO2_EmptyUserWithMobile() throws Exception {
        String userName = "b88106713409e92622461a876abcd74b444";
        String phone = "";
        String apiVersion = "4.0";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        mockMvc.perform(post("/somekey/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("COMMUNITY_NAME", communityName).param("USER_NAME", userName).param("USER_TOKEN", userToken)
                                                                                             .param("TIMESTAMP", timestamp).param("PHONE", phone)).andExpect(status().isOk())
               .andExpect(xpath("/response/errorMessage/errorCode").string("601"));
    }

    @Test
    public void testActivatePhoneNumber_v6d0_Json_Success() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a";
        String phone = "+64279000456";
        String apiVersion = "6.0";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc
            .perform(post("/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER.json").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("PHONE", phone))
            .andExpect(status().isOk()).andExpect(content().string(("{\"response\":{\"data\":" +
                                                                    "[{\"phoneActivation\":" +
                                                                    "{\"activation\":\"ENTERED_NUMBER\",\"phoneNumber\":\"+64279000456\"}" +
                                                                    "}]}}")));
    }

    @Test
    public void testActivatePhoneNumber_401_Failure() throws Exception {
        String userName = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
        String phone = "+6xxxxxxxxxxxxxx";
        String apiVersion = "5.0";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("PHONE", phone))
               .andExpect(status().isUnauthorized());
    }

    @Test
    public void testActivatePhoneNumber_400_Failure() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a";
        String phone = "+6xxxxxxxxxxxxxx";
        String apiVersion = "5.0";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("USER_NAME", userName).param("TIMESTAMP", timestamp).param("PHONE", phone))
               .andExpect(status().isInternalServerError());
    }

    @Test
    public void testActivatePhoneNumberV5d3_400_Failure() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a";
        String phone = "+6xxxxxxxxxxxxxx";
        String apiVersion = "5.3";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("USER_NAME", userName).param("TIMESTAMP", timestamp).param("PHONE", phone))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void testActivatePhoneNumber_404_Failure() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a";
        String phone = "+6xxxxxxxxxxxxxx";
        String apiVersion = "3.5";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER").param("USER_NAME", userName).param("TIMESTAMP", timestamp).param("PHONE", phone))
               .andExpect(status().isNotFound());
    }


    @Override
    @After
    public void tireDown() {
        super.tireDown();
        reset(vfGatewayServiceSpy);
    }

    private void resetMobile(String userName, String communityName) {
        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityName);
        user.setMobile(null);

        userService.updateUser(user);
    }
}