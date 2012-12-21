package mobi.nowtechnologies.server.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import mobi.nowtechnologies.server.dto.O2UserDetails;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;

import org.junit.Before;
import org.junit.Test;

public class O2ClientServiceImplTest {
	private O2ClientServiceImpl fixture;

	@Test
	public void testValidatePhoneNumber_Success()
			throws Exception {
		
		String phoneNumber = "07870111111";

		String result = fixture.validatePhoneNumber(phoneNumber);

		assertNotNull(result);
	}
	
	@Test(expected = InvalidPhoneNumberException.class)
	public void testValidatePhoneNumber_Failure()
			throws Exception {
		
		String phoneNumber = "0787011fff1111";

		fixture.validatePhoneNumber(phoneNumber);
	}
	
	@Test
	public void getUserDetail_Success_with_O2User_and_PAYGtariff() {
		String otac_auth_code = "0000";
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code);
		assertEquals("o2", userDetails.getOperator());
		assertEquals("PAYG", userDetails.getTariff());	
	}
	
	@Test
	public void getUserDetail_Success_with_notO2User_and_PAYGtariff() {
		String otac_auth_code = "1111";
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code);
		assertEquals("non-o2", userDetails.getOperator());
		assertEquals("PAYG", userDetails.getTariff());	
	}
	
	@Test
	public void getUserDetail_Success_with_O2User_and_PAYGMtariff() {
		String otac_auth_code = "2222";
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code);
		assertEquals("o2", userDetails.getOperator());
		assertEquals("PAYGM", userDetails.getTariff());	
	}
	
	@Test
	public void getUserDetail_Success_with_notO2User_and_PAYGMtariff() {
		String otac_auth_code = "3333";
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code);
		assertEquals("non-o2", userDetails.getOperator());
		assertEquals("PAYGM", userDetails.getTariff());	
	}
	
	@Test
	public void getUserDetail_Success_with_O2User_and_Businesstariff() {
		String otac_auth_code = "4444";
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code);
		assertEquals("o2", userDetails.getOperator());
		assertEquals("business", userDetails.getTariff());	
	}
	
	@Test
	public void getUserDetail_Success_with_notO2User_and_Businesstariff() {
		String otac_auth_code = "5555";
		
		O2UserDetails userDetails = fixture.getUserDetails(otac_auth_code);
		assertEquals("non-o2", userDetails.getOperator());
		assertEquals("business", userDetails.getTariff());	
	}
	
	@Test(expected=ExternalServiceException.class)
	public void getUserDetail_Fail() {
		String otac_auth_code = "6666";
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
	}
}