package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.service.FacebookService.UserCredentions;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class UserCredentionsFactory
 {
	private UserCredentionsFactory() {
	}


	public static UserCredentions createUserCredentions() {
		UserCredentions userCredentions = new FacebookService.UserCredentions();
		
		userCredentions.setBirthday("birthday");
		userCredentions.setEmail("email");
		userCredentions.setFirst_name("first_name");
		userCredentions.setGender("gender");
		userCredentions.setId("id");
		userCredentions.setLast_name("last_name");
		userCredentions.setLink("link");
		userCredentions.setLocale("locale");
		userCredentions.setName("name");
		userCredentions.setTimezone("timezone");
		userCredentions.setUpdated_time("updated_time");
		userCredentions.setVerified("verified");
		
		return userCredentions;
	}
}