package mobi.nowtechnologies.server.trackrepo.controller;

import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:META-INF/application-test.xml",
		"file:src/main/webapp/WEB-INF/trackrepo-servlet.xml"})
@TransactionConfiguration(transactionManager = "trackRepo.TransactionManager", defaultRollback = true)
@Transactional
@Ignore
public class SignInControllerIT extends TestCase {
	@Autowired
	private SignInController fixture;

	@Test
	public void testLogin() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();

		Boolean result = fixture.login(request);

		assertNotNull(result);
		assertEquals(true, result.booleanValue());
	}
	
	@Test
	public void generateMd5Passowrd_Admin$Admin_Successful() {

		String password = "admin";
		String salt = "admin";
	
		Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
		String encodedPass = passwordEncoder.encodePassword(password, salt);
		
		assertEquals("ceb4f32325eda6142bd65215f4c0f371", encodedPass);
	}
	
	@Test
	public void generateMd5Passowrd_album$MQIph5ao2l_Successful() {
		
		String password = "MQIph5ao2l";
		String salt = "album";
		
		Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
		String encodedPass = passwordEncoder.encodePassword(password, salt);
		
		assertEquals("dd0d117d91ac00d7f6e83852ac454669", encodedPass);
	}
}