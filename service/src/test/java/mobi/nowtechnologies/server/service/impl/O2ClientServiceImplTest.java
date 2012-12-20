package mobi.nowtechnologies.server.service.impl;

import static org.junit.Assert.assertNotNull;

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

	@Before
	public void setUp()
			throws Exception {
		fixture = new O2ClientServiceImpl();
		fixture.setServerO2Url("https://uat.mqapi.com");
	}
}