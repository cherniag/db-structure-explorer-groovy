package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserLog;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLogType;
import mobi.nowtechnologies.server.persistence.repository.UserLogRepository;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.DevicePromotionsService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;
import mobi.nowtechnologies.server.service.exception.LimitPhoneNumberValidationException;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.O2ServiceImpl;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

import javax.xml.transform.dom.DOMSource;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.TextImpl;

import org.springframework.web.client.RestTemplate;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({O2ProviderServiceImpl.class})
public class O2ClientServiceImplTest {

    private O2ProviderServiceImpl fixture;
    private O2ProviderServiceImpl fixture2;

    @Mock
    private RestTemplate mockRestTemplate;

    @Mock
    private CommunityService mockCommunityService;

    @Mock
    private DevicePromotionsService mockDeviceService;

    @Mock
    private UserService userServiceMock;

    @Mock
    private UserLogRepository mockUserLogRepository;

    private O2ServiceImpl o2serviceImpl = new O2ServiceImpl();


    @Before
    public void setUp() throws Exception {
        final Community community = CommunityFactory.createCommunity();
        community.setRewriteUrlParameter("o2");
        community.setName("o2");
        when(mockCommunityService.getCommunityByName(eq("o2"))).thenReturn(community);

        fixture = new O2ProviderServiceImpl();
        fixture.setCommunityService(mockCommunityService);
        fixture.setDeviceService(mockDeviceService);
        fixture.setServerO2Url("https://prod.mqapi.com");
        fixture.setRedeemServerO2Url("https://identity.o2.co.uk");
        fixture.setPromotedServerO2Url("https://uat.mqapi.com");
        fixture.setRedeemPromotedServerO2Url("https://uat.mqapi.com");
        fixture.setUserLogRepository(mockUserLogRepository);
        fixture.setLimitValidatePhoneNumber(9);
        fixture.setO2Service(o2serviceImpl);

        fixture2 = new O2ProviderServiceImpl();
        fixture2.setServerO2Url("https://uat.mqapi.com");
        fixture2.setCommunityService(mockCommunityService);
        fixture2.setO2Service(o2serviceImpl);
        o2serviceImpl.setRestTemplate(mockRestTemplate);
    }

    @SuppressWarnings("unchecked")
    //@Test
    public void testValidatePhoneNumber_NotPromoted_Success() throws Exception {

        String phoneNumber = "07870111111";
        String expectedPhoneNumber = "+447870111111";

        DOMSource response = new DOMSource();
        DocumentImpl root = new DocumentImpl();
        ElementImpl user = new ElementImpl(root, "user");
        ElementImpl msisdn = new ElementImpl(root, "msisdn");
        TextImpl number = new TextImpl(root, expectedPhoneNumber);
        msisdn.appendChild(number);
        user.appendChild(msisdn);
        root.appendChild(user);
        response.setNode(root);

        when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(false);
        when(mockRestTemplate.postForObject(anyString(), any(Object.class), any(Class.class))).thenReturn(response);

        PhoneNumberValidationData result = fixture.validatePhoneNumber(phoneNumber);

        //verify(mockRestTemplate, times(1)).postForObject(anyString(), any(Object.class), any(Class.class));

        assertNotNull(result);
        assertEquals(expectedPhoneNumber, result.getPhoneNumber());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValidatePhoneNumber_PromotedWithSpaces_Success() throws Exception {

        String phoneNumber = "078 701 11111";
        String expectedPhoneNumber = "+447870111111";

        DOMSource response = new DOMSource();
        DocumentImpl root = new DocumentImpl();
        ElementImpl user = new ElementImpl(root, "user");
        ElementImpl msisdn = new ElementImpl(root, "msisdn");
        TextImpl number = new TextImpl(root, expectedPhoneNumber);
        msisdn.appendChild(number);
        user.appendChild(msisdn);
        root.appendChild(user);
        response.setNode(root);

        when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);
        when(mockRestTemplate.postForObject(anyString(), any(Object.class), any(Class.class))).thenReturn(response);
        when(mockUserLogRepository.countByPhoneNumberAndDay(anyString(), any(UserLogType.class), anyLong())).thenReturn(1L);
        when(mockUserLogRepository.save(any(UserLog.class))).thenReturn(null);

        PhoneNumberValidationData result = fixture.validatePhoneNumber(phoneNumber);

        //verify(mockRestTemplate, times(1)).postForObject(anyString(), any(Object.class), any(Class.class));

        assertNotNull(result);
        assertEquals(expectedPhoneNumber, result.getPhoneNumber());

        verify(mockUserLogRepository).save(any(UserLog.class));
        verify(mockUserLogRepository).countByPhoneNumberAndDay(anyString(), any(UserLogType.class), anyLong());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValidatePhoneNumber_InvalidPhoneNumber_Failure() throws Exception {

        String phoneNumber = "0787011fff1111";

        when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);
        when(mockRestTemplate.postForObject(anyString(), any(Object.class), any(Class.class))).thenThrow(new InvalidPhoneNumberException(phoneNumber));
        when(mockUserLogRepository.countByPhoneNumberAndDay(anyString(), any(UserLogType.class), anyLong())).thenReturn(1L);
        when(mockUserLogRepository.save(any(UserLog.class))).thenReturn(null);

        try {
            fixture.validatePhoneNumber(phoneNumber);
            fail();
        } catch (Exception e) {
            if (!(e instanceof InvalidPhoneNumberException)) {
                fail();
            }
        }

        verify(mockUserLogRepository, times(0)).save(any(UserLog.class));
        verify(mockUserLogRepository, times(0)).countByPhoneNumberAndDay(anyString(), any(UserLogType.class), anyLong());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValidatePhoneNumber_ShortInvalidPhoneNumber_Failure() throws Exception {

        String phoneNumber = "0787011";

        when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);
        when(mockRestTemplate.postForObject(anyString(), any(Object.class), any(Class.class))).thenThrow(new InvalidPhoneNumberException(phoneNumber));
        when(mockUserLogRepository.countByPhoneNumberAndDay(anyString(), any(UserLogType.class), anyLong())).thenReturn(1L);
        when(mockUserLogRepository.save(any(UserLog.class))).thenReturn(null);

        try {
            fixture.validatePhoneNumber(phoneNumber);
            fail();
        } catch (Exception e) {
            if (!(e instanceof InvalidPhoneNumberException)) {
                fail();
            }
        }

        verify(mockUserLogRepository, times(0)).save(any(UserLog.class));
        verify(mockUserLogRepository, times(0)).countByPhoneNumberAndDay(anyString(), any(UserLogType.class), anyLong());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValidatePhoneNumber_LimitPhoneNumber_Failure() throws Exception {

        String phoneNumber = "07870111111";

        when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);
        when(mockRestTemplate.postForObject(anyString(), any(Object.class), any(Class.class))).thenThrow(new InvalidPhoneNumberException(phoneNumber));
        when(mockUserLogRepository.countByPhoneNumberAndDay(anyString(), any(UserLogType.class), anyLong())).thenReturn(10L);
        when(mockUserLogRepository.save(any(UserLog.class))).thenReturn(null);

        try {
            fixture.validatePhoneNumber(phoneNumber);
            fail();
        } catch (Exception e) {
            if (!(e instanceof LimitPhoneNumberValidationException)) {
                fail();
            }
        }

        verify(mockUserLogRepository, times(0)).save(any(UserLog.class));
        verify(mockUserLogRepository, times(1)).countByPhoneNumberAndDay(anyString(), any(UserLogType.class), anyLong());
    }

    @Test
    @Ignore
    public void getUserDetail_Success_with_O2User_and_PAYGTariff() {
        String phoneNumber = "+447870111111";
        String otac_auth_code = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        Community community = new Community();

        when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);

        ProviderUserDetails userDetails = fixture.getUserDetails(otac_auth_code, phoneNumber, community);
        assertEquals("o2", userDetails.operator);
        assertEquals("PAYG", userDetails.contract);

    }

    @Test
    @Ignore
    public void getUserDetail_Success_with_notO2User_and_PAYGTariff() {
        String phoneNumber = "+447870111111";
        String otac_auth_code = "11111111-c768-4fe7-bb56-a5e0c722cd44";
        Community community = new Community();

        when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);

        ProviderUserDetails userDetails = fixture.getUserDetails(otac_auth_code, phoneNumber, community);
        assertEquals("non-o2", userDetails.operator);
        assertEquals("PAYG", userDetails.contract);

    }

    @Test
    @Ignore
    public void getUserDetail_Success_with_O2User_and_PAYGMTariff() {
        String phoneNumber = "+447870111111";
        String otac_auth_code = "22222222-c768-4fe7-bb56-a5e0c722cd44";
        Community community = new Community();

        when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);

        ProviderUserDetails userDetails = fixture.getUserDetails(otac_auth_code, phoneNumber, community);
        assertEquals("o2", userDetails.operator);
        assertEquals("PAYM", userDetails.contract);

    }

    @Test
    @Ignore
    public void getUserDetail_Success_with_notO2User_and_PAYGMTariff() {
        String phoneNumber = "+447870111111";
        String otac_auth_code = "33333333-c768-4fe7-bb56-a5e0c722cd44";
        Community community = new Community();

        when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);

        ProviderUserDetails userDetails = fixture.getUserDetails(otac_auth_code, phoneNumber, community);
        assertEquals("non-o2", userDetails.operator);
        assertEquals("PAYM", userDetails.contract);

    }

    @Test
    @Ignore
    public void getUserDetail_Success_with_O2User_and_BusinessTariff() {
        String phoneNumber = "+447870111111";
        String otac_auth_code = "44444444-c768-4fe7-bb56-a5e0c722cd44";
        Community community = new Community();

        when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);

        ProviderUserDetails userDetails = fixture.getUserDetails(otac_auth_code, phoneNumber, community);
        assertEquals("o2", userDetails.operator);
        assertEquals("business", userDetails.contract);

    }

    @Test
    @Ignore
    public void getUserDetail_Success_with_notO2User_and_BusinessTariff() {
        String phoneNumber = "+447870111111";
        String otac_auth_code = "55555555-c768-4fe7-bb56-a5e0c722cd44";
        Community community = new Community();

        when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);

        ProviderUserDetails userDetails = fixture.getUserDetails(otac_auth_code, phoneNumber, community);
        assertEquals("non-o2", userDetails.operator);
        assertEquals("business", userDetails.contract);

    }

    @Test(expected = ExternalServiceException.class)
    public void getUserDetail_Fail() {
        String phoneNumber = "+447870111111";
        String otac_auth_code = "6666fasdffwqe";
        Community community = new Community();

        when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(false);

        fixture.getUserDetails(otac_auth_code, phoneNumber, community);
    }

    @Test
    public void testGetRedeemServerO2Url_Promoted_Success() throws Exception {
        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);

        String result = fixture.getRedeemServerO2Url(user.getMobile());

        assertEquals("https://uat.mqapi.com", result);

        Mockito.verify(mockDeviceService, times(1)).isPromotedDevicePhone(any(Community.class), anyString(), anyString());
    }

    @Test
    public void testGetRedeemServerO2Url_NotPromoted_Success() throws Exception {
        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(false);

        String result = fixture.getRedeemServerO2Url(user.getMobile());

        assertEquals("https://identity.o2.co.uk", result);

        Mockito.verify(mockDeviceService, times(1)).isPromotedDevicePhone(any(Community.class), anyString(), anyString());
    }


}
