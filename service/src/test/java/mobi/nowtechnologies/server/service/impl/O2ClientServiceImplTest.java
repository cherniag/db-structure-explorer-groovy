package mobi.nowtechnologies.server.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import javax.xml.transform.dom.DOMSource;

import mobi.nowtechnologies.server.dto.O2UserDetails;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.TextImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ O2ClientServiceImpl.class})
public class O2ClientServiceImplTest {
	private O2ClientServiceImpl fixture;
	
	@Mock
	private RestTemplate mockRestTemplate;

	@SuppressWarnings("unchecked")
	@Test
	public void testValidatePhoneNumber_Success()
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
		
		when(mockRestTemplate.postForObject(anyString(), any(Object.class), any(Class.class))).thenReturn(response);

		String result = fixture.validatePhoneNumber(phoneNumber);
		
		verify(mockRestTemplate, times(1)).postForObject(anyString(), any(Object.class), any(Class.class));

		assertNotNull(result);
		assertEquals(expectedPhoneNumber, result);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = InvalidPhoneNumberException.class)
	public void testValidatePhoneNumber_Failure()
			throws Exception {
		
		String phoneNumber = "0787011fff1111";

		when(mockRestTemplate.postForObject(anyString(), any(Object.class), any(Class.class))).thenThrow(new InvalidPhoneNumberException());
		
		fixture.validatePhoneNumber(phoneNumber);
	}
	
	@Test
	public void getUserDetail_Success_with_O2User_and_PAYGtariff() {
		String otac_auth_code = "00000000-c768-4fe7-bb56-a5e0c722cd44";
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code);
		assertEquals("o2", userDetails.getOperator());
		assertEquals("PAYG", userDetails.getTariff());	
	}
	
	@Test
	public void getUserDetail_Success_with_notO2User_and_PAYGtariff() {
		String otac_auth_code = "1111sfdf1345qwdf";
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code);
		assertEquals("non-o2", userDetails.getOperator());
		assertEquals("PAYG", userDetails.getTariff());	
	}
	
	@Test
	public void getUserDetail_Success_with_O2User_and_PAYGMtariff() {
		String otac_auth_code = "22221234asdfasd";
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code);
		assertEquals("o2", userDetails.getOperator());
		assertEquals("PAYGM", userDetails.getTariff());	
	}
	
	@Test
	public void getUserDetail_Success_with_notO2User_and_PAYGMtariff() {
		String otac_auth_code = "3333asdfasdf";
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code);
		assertEquals("non-o2", userDetails.getOperator());
		assertEquals("PAYGM", userDetails.getTariff());	
	}
	
	@Test
	public void getUserDetail_Success_with_O2User_and_Businesstariff() {
		String otac_auth_code = "4444asdfasdf";
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code);
		assertEquals("o2", userDetails.getOperator());
		assertEquals("business", userDetails.getTariff());	
	}
	
	@Test
	public void getUserDetail_Success_with_notO2User_and_Businesstariff() {
		String otac_auth_code = "5555asdfasdf";
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code);
		assertEquals("non-o2", userDetails.getOperator());
		assertEquals("business", userDetails.getTariff());	
	}
	
	@Test(expected=ExternalServiceException.class)
	public void getUserDetail_Fail() {
		String otac_auth_code = "6666fasdffwqe";
		fixture.getUserDetails(otac_auth_code);
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
	
	@Before
	public void setUp()
			throws Exception {
		fixture = new O2ClientServiceImpl();
		fixture.setServerO2Url("https://uat.mqapi.com");
		
		whenNew(RestTemplate.class).withNoArguments().thenReturn(mockRestTemplate);
		fixture.init();
	}
}
