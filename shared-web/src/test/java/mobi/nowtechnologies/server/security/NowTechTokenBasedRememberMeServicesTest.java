/**
 * 
 */
package mobi.nowtechnologies.server.security;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetailsService;
/**
 * @author Mayboroda Dmytro
 *
 */
public class NowTechTokenBasedRememberMeServicesTest {
	
	private NowTechTokenBasedRememberMeServices service;
	private String key = "123";
	private UserDetailsService userDetailsService;
	
	@Before
	public void before() {
		userDetailsService = mock(UserDetailsService.class);
		service = new NowTechTokenBasedRememberMeServices(key , userDetailsService);
	}
	
	@Test
	public void encodeUsername_WithColons() {
		String userName = "mac_00:12:34:c2:ee:42";
		String expected = userName.replaceAll(":", "|");
		String encodedUserName = service.getEncodedUserName(userName);
		assertEquals(expected, encodedUserName);
	}
	
	@Test
	public void encodeUsername_WithoutColons() {
		String userName = "thisis@mail.com";
		String encodedUserName = service.getEncodedUserName(userName );
		assertEquals(userName, encodedUserName);
	}
}