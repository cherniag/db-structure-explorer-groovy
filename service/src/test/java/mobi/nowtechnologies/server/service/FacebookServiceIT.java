package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.service.FacebookService.UserCredentions;
import mobi.nowtechnologies.server.shared.service.PostService;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class FacebookServiceIT {
	
	private FacebookService service;
	
	@Before
	public void before() {
		service = new FacebookService();
			service.setPostService(new PostService());
	}
	
	@Test
	public void getUserCredentions_Successful() {
		UserCredentions userCredentions = service.getUserCredentions("samsung", "AAADPSZB3jaQUBANDS9bZCS7FrhZCNHwH3KZCrYVwrvu8ExV2yTor9lZCgESZB7QNJhZAUsRZBbZAxNPjyN2bheySZAzztNII8ZA3lRyg3PycnN0tgZDZD");
		assertNotNull(userCredentions);
	}
}