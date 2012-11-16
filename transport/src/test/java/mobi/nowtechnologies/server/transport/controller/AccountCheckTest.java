package mobi.nowtechnologies.server.transport.controller;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.service.PostService;
import mobi.nowtechnologies.server.shared.service.PostService.Response;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class AccountCheckTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountCheckTest.class);

	private static final String URL = "http://pc-myti.kyiv.ciklum.net:8080";
	private static final String SERVLET_URL_PATTERN = "/transport/request.php";
	//private static ServletTester ServletTester;
	private static String baseUrl;
	private static UserService userService;

	public void testCheckCredentialsAndStatus_Success() throws Exception {

		String userName = "nr@rbt.com";
		String password = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestamp = sdf.format(Calendar.getInstance().getTime());
		//String storredToken = userService.getStoredToken(userName, password);
		String storedToken = "26b34b31237dfffb4caeb9518ad1ce02";
		String appVersion = "CN Commercial Beta";
		String apiVersion = "CN";
		String command = "ACC_CHECK";

		NameValuePair[] nameValuePairs = new NameValuePair[6];
		nameValuePairs[0] = new BasicNameValuePair("command", command);
		nameValuePairs[1] = new BasicNameValuePair("USER_NAME", userName);
		nameValuePairs[2] = new BasicNameValuePair(
				"USER_TOKEN", Utils.createTimestampToken(storedToken, timestamp));
		nameValuePairs[3] = new BasicNameValuePair("APP_VERSION", appVersion);
		nameValuePairs[4] = new BasicNameValuePair("API_VERSION", apiVersion);
		nameValuePairs[5] = new BasicNameValuePair("TIMESTAMP", timestamp);

		PostService postService = new PostService();
		Response response = postService.sendHttpPost(URL + SERVLET_URL_PATTERN,
				Arrays.asList(nameValuePairs), null);

		String testResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<response>"
				+ "<user>"
				+ "<chartItems>40</chartItems>"
				+ "<chartTimestamp>1306908977</chartTimestamp>"
				+ "<deviceType>2</deviceType>"
				+ "<deviceUID>iPhone</deviceUID>"
				+ "<displayName>Nigel</displayName>"
				+ "<drmType>PLAYS</drmType>"
				+ "<drmValue>100</drmValue>"
				+ "<newsItems>10</newsItems>"
				+ "<newsTimestamp>1306942494</newsTimestamp>"
				+ "<status>0</status>"
				+ "<subBalance>4</subBalance>"
				+ "</user>" + "</response>";
		assertEquals(testResult, response.getMessage());
	}
	
	public static void main(String[] args) {
		//new org.junit.runner.JUnitCore().run(AccountCheckTest.class);
		AccountCheckTest accountCheckTest= new AccountCheckTest();
		try {
			accountCheckTest.initServletContainer();
			accountCheckTest.testCheckCredentialsAndStatus_Success();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
	}

	// @Test
	// @Ignore
	// public void testCheckCredentialsAndStatus_1() throws Exception {
	//
	// String userName = "nr@rbt.com";
	// String password = "";
	// String userToken = userService.getStoredToken(userName, password);
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// String timestamp = sdf.format(Calendar.getInstance().getTime());
	// String appVersion = "CN Commercial Beta";
	// String apiVersion = "CN";
	// String command = "ACC_CHECK";
	// String requetParammeters = "command=" + command + "&USER_NAME="
	// + userName + "&USER_TOKEN=" + userToken + "&APP_VERSION="
	// + appVersion + "&API_VERSION=" + apiVersion + "&TIMESTAMP="
	// + timestamp;
	//
	// PostService postService = new PostService();
	// String response = postService.sendHttpPost(baseUrl
	// + SERVLET_URL_PATTERN, requetParammeters);
	//
	// String testResult =
	// "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
	// + "<response>"
	// + "<user>"
	// + "<chartItems>40</chartItems>"
	// + "<chartTimestamp>1306908977</chartTimestamp>"
	// + "<deviceType>2</deviceType>"
	// + "<deviceUID>iPhone</deviceUID>"
	// + "<displayName>Nigel</displayName>"
	// + "<drmType>PLAYS</drmType>"
	// + "<drmValue>100</drmValue>"
	// + "<newsItems>10</newsItems>"
	// + "<newsTimestamp>1306942494</newsTimestamp>"
	// + "<status>0</status>"
	// + "<subBalance>5</subBalance>"
	// + "</user>" + "</response>";
	// assertEquals(testResult, response);
	// }

	@BeforeClass
	public static void initServletContainer() throws Exception {
		// ServletTester = new ServletTester();
		// ServletTester.setContextPath("/");
		// ServletTester.addServlet(DispatcherServlet.class,
		// SERVLET_URL_PATTERN);
		// baseUrl = ServletTester.createSocketConnector(true);
		// ServletTester.start();

		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { "/META-INF/service.xml", "/META-INF/dao-test.xml" });
		userService = (UserService) appContext.getBean("service.UserService");
	}

	/**
	 * Stops the Jetty container.
	 */
	@AfterClass
	public static void cleanupServletContainer() throws Exception {
		// ServletTester.stop();
	}

}
