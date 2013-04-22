package mobi.nowtechnologies.server.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import javax.xml.transform.dom.DOMSource;

import mobi.nowtechnologies.server.dto.O2UserDetails;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.DeviceService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;
import mobi.nowtechnologies.server.service.o2.impl.O2ClientServiceImpl;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.TextImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ O2ClientServiceImpl.class})
public class O2ClientServiceImplIT {
	private O2ClientServiceImpl fixture;
	
	private O2ClientServiceImpl fixture2;
	
	@Mock
	private RestTemplate mockRestTemplate;
	
	@Mock
	private CommunityService mockCommunityService;
	
	@Mock
	private DeviceService mockDeviceService;
	

	@SuppressWarnings("unchecked")
	//@Test
	public void testValidatePhoneNumber_NotPromoted_Success()
			throws Exception {
		
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

		String result = fixture.validatePhoneNumber(phoneNumber);
		
		//verify(mockRestTemplate, times(1)).postForObject(anyString(), any(Object.class), any(Class.class));

		assertNotNull(result);
		assertEquals(expectedPhoneNumber, result);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testValidatePhoneNumber_Promoted_Success()
			throws Exception {
		
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
		
		when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);
		when(mockRestTemplate.postForObject(anyString(), any(Object.class), any(Class.class))).thenReturn(response);

		String result = fixture.validatePhoneNumber(phoneNumber);
		
		//verify(mockRestTemplate, times(1)).postForObject(anyString(), any(Object.class), any(Class.class));

		assertNotNull(result);
		assertEquals(expectedPhoneNumber, result);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = InvalidPhoneNumberException.class)
	public void testValidatePhoneNumber_Failure()
			throws Exception {
		
		String phoneNumber = "0787011fff1111";

		when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);
		when(mockRestTemplate.postForObject(anyString(), any(Object.class), any(Class.class))).thenThrow(new InvalidPhoneNumberException());
		
		fixture.validatePhoneNumber(phoneNumber);
	}
	
	@Test
	public void getUserDetail_Success_with_O2User_and_PAYGtariff() {
		String phoneNumber = "+447870111111";
		String otac_auth_code = "00000000-c768-4fe7-bb56-a5e0c722cd44";
		
		when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code, phoneNumber);
		assertEquals("o2", userDetails.getOperator());
		assertEquals("PAYG", userDetails.getTariff());	
	}
	
	@Test
	public void getUserDetail_Success_with_notO2User_and_PAYGtariff() {
		String phoneNumber = "+447870111111";
		String otac_auth_code = "11111111-c768-4fe7-bb56-a5e0c722cd44";
		
		when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code, phoneNumber);
		assertEquals("non-o2", userDetails.getOperator());
		assertEquals("PAYG", userDetails.getTariff());	
	}
	
	@Test
	public void getUserDetail_Success_with_O2User_and_PAYGMtariff() {
		String phoneNumber = "+447870111111";
		String otac_auth_code = "22222222-c768-4fe7-bb56-a5e0c722cd44";
		
		when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code, phoneNumber);
		assertEquals("o2", userDetails.getOperator());
		assertEquals("PAYM", userDetails.getTariff());	
	}
	
	@Test
	public void getUserDetail_Success_with_notO2User_and_PAYGMtariff() {
		String phoneNumber = "+447870111111";
		String otac_auth_code = "33333333-c768-4fe7-bb56-a5e0c722cd44";
		
		when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code, phoneNumber);
		assertEquals("non-o2", userDetails.getOperator());
		assertEquals("PAYM", userDetails.getTariff());	
	}
	
	@Test
	public void getUserDetail_Success_with_O2User_and_Businesstariff() {
		String phoneNumber = "+447870111111";
		String otac_auth_code = "44444444-c768-4fe7-bb56-a5e0c722cd44";
		
		when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code, phoneNumber);
		assertEquals("o2", userDetails.getOperator());
		assertEquals("business", userDetails.getTariff());	
	}
	
	@Test
	public void getUserDetail_Success_with_notO2User_and_Businesstariff() {
		String phoneNumber = "+447870111111";
		String otac_auth_code = "55555555-c768-4fe7-bb56-a5e0c722cd44";
		
		when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code, phoneNumber);
		assertEquals("non-o2", userDetails.getOperator());
		assertEquals("business", userDetails.getTariff());	
	}
	
	@Test(expected=ExternalServiceException.class)
	public void getUserDetail_Fail() {
		String phoneNumber = "+447870111111";
		String otac_auth_code = "6666fasdffwqe";
		
		when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);
		
		fixture.getUserDetails(otac_auth_code, phoneNumber);
	}
	
	@Test
	public void isO2User_Successful() {
		boolean o2User = fixture.isO2User(new O2UserDetails("o2", "any"));
		assertEquals(true, o2User);
	}
	
	@Test
	public void isO2User_Fail() {
		boolean o2User = fixture.isO2User(new O2UserDetails("non-o2", "any"));
		assertEquals(false, o2User);
	}
	
	@Test
	public void isO2User_Fail_with_badResponse() {
		boolean o2User = fixture.isO2User(new O2UserDetails(null, "any"));
		assertEquals(false, o2User);
	}
	
	@Test
	public void testGetRedeemServerO2Url_Promoted_Success() throws Exception{
		final User user = UserFactory.createUser();
				
		when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(true);
		
		String result = fixture.getRedeemServerO2Url(user.getMobile());
	
		assertEquals("https://uat.mqapi.com", result);
		
		Mockito.verify(mockDeviceService, times(1)).isPromotedDevicePhone(any(Community.class), anyString(), anyString());
	}
	
	@Test
	public void testGetRedeemServerO2Url_NotPromoted_Success() throws Exception{
		final User user = UserFactory.createUser();
				
		when(mockDeviceService.isPromotedDevicePhone(any(Community.class), anyString(), anyString())).thenReturn(false);
		
		String result = fixture.getRedeemServerO2Url(user.getMobile());
	
		assertEquals("https://identity.o2.co.uk", result);
		
		Mockito.verify(mockDeviceService, times(1)).isPromotedDevicePhone(any(Community.class), anyString(), anyString());
	}
	
	@Before
	public void setUp()
			throws Exception {
		final Community community = CommunityFactory.createCommunity();		
		community.setRewriteUrlParameter("o2");
		community.setName("o2");
		when(mockCommunityService.getCommunityByName(eq("o2"))).thenReturn(community);
		
		fixture = new O2ClientServiceImpl();
		fixture.setCommunityService(mockCommunityService);
		fixture.setDeviceService(mockDeviceService);
		fixture.setServerO2Url("https://prod.mqapi.com");
		fixture.setRedeemServerO2Url("https://identity.o2.co.uk");
		fixture.setPromotedServerO2Url("https://uat.mqapi.com");
		fixture.setRedeemPromotedServerO2Url("https://uat.mqapi.com");
		
		//whenNew(RestTemplate.class).withNoArguments().thenReturn(mockRestTemplate);
		fixture.init();
		
		fixture2 = new O2ClientServiceImpl();
		fixture2.setServerO2Url("https://uat.mqapi.com");
		fixture2.setCommunityService(mockCommunityService);
		fixture2.init();
	}
}
