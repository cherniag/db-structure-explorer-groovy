
package mobi.nowtechnologies.server.transport.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletException;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.job.CreatePendingPaymentJob;
import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.EntityDao;
import mobi.nowtechnologies.server.persistence.dao.PaymentStatusDao;
import mobi.nowtechnologies.server.persistence.dao.UserDao;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.CountryByIpService;
import mobi.nowtechnologies.server.service.EntityService;
import mobi.nowtechnologies.server.service.FacebookService;
import mobi.nowtechnologies.server.service.FileService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.WeeklyUpdateService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.impl.ITunesServiceImpl;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import mobi.nowtechnologies.server.shared.service.BasicResponse;
import mobi.nowtechnologies.server.shared.service.PostService;

import org.apache.http.NameValuePair;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.ElementQualifier;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.DispatcherServlet;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:transport-servlet-test.xml",
		"classpath:META-INF/service-test.xml",
		"classpath:META-INF/soap.xml",
		"classpath:META-INF/dao-test.xml",
		"classpath:META-INF/soap.xml",
		"classpath:META-INF/shared.xml" }, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "transport.EntityController", webapp = "classpath:.")
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@PrepareForTest(Utils.class)
@Ignore
public class IntegrationTestIT {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(IntegrationTestIT.class.getName());
	
	@Autowired
	private DispatcherServlet dispatcherServlet;

	@Resource(name = "service.UserService")
	private UserService userService;

	@Resource(name = "service.FileService")
	private FileService fileService;

	@Resource(name = "service.EntityService")
	private EntityService entityService;

	@Resource(name = "service.CountryByIpService")
	private CountryByIpService countryByIpService;

	@Resource(name = "persistence.EntityDao")
	private EntityDao entityDao;

	@Resource(name = "persistence.UserDao")
	private UserDao userDao;

	@Resource(name = "service.WeeklyUpdateService")
	private WeeklyUpdateService weeklyUpdateService;
	
	@Resource(name = "service.ITunesService")
	private ITunesServiceImpl iTunesServiceImpl;
	
	@Resource(name = "shared.PostService")
	private PostService postService;
	
	@Value("${apple.inApp.password}")
	private String appleInAppPassword;
	
	@Value("${apple.inApp.iTunesUrl}")
	private String appleInAppITunesUrl;

	@Resource(name = "paymentTask")
	private CreatePendingPaymentJob createPendingPaymentJob;

	@Resource
	private EntityController entityController;

	@PostConstruct
	public void setUp() {
		XMLUnit.setControlParser(
				"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
		XMLUnit.setTestParser(
				"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
		XMLUnit.setSAXParserFactory(
				"org.apache.xerces.jaxp.SAXParserFactoryImpl");
		XMLUnit.setIgnoreWhitespace(true);
	}

	private MockHttpServletResponse updatePhone(String timestamp,
			String userToken, String userName, String apiVersion,
			String communityName, String appVersion, String phoneNumber,
			Integer operator) throws ServletException, IOException {
		if (timestamp == null)
			throw new NullPointerException("The parameter timestamp is null");
		if (userToken == null)
			throw new NullPointerException("The parameter userToken is null");
		if (userName == null)
			throw new NullPointerException("The parameter userName is null");
		if (apiVersion == null)
			throw new NullPointerException("The parameter apiVersion is null");
		if (communityName == null)
			throw new NullPointerException("The parameter communityName is null");
		if (appVersion == null)
			throw new NullPointerException("The parameter appVersion is null");
		if (phoneNumber == null)
			throw new NullPointerException("The parameter phoneNumber is null");
		if (operator == null)
			throw new NullPointerException("The parameter operator is null");

		MockHttpServletResponse aHttpServletResponse;
		MockHttpServletRequest httpServletRequest;
		aHttpServletResponse = new MockHttpServletResponse();
		httpServletRequest = new MockHttpServletRequest("POST", "/UPDATE_PHONE");
		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("USER_NAME", userName);
		httpServletRequest.addParameter("USER_TOKEN", userToken);
		httpServletRequest.addParameter("TIMESTAMP", timestamp);
		httpServletRequest.addParameter("PHONE_NUMBER", phoneNumber);
		httpServletRequest.addParameter("OPERATOR", String.valueOf(operator));

		dispatcherServlet.service(httpServletRequest, aHttpServletResponse);
		return aHttpServletResponse;
	}

	private MockHttpServletResponse acc_check(String timestamp,
			String userToken, String userName, String apiVersion,
			String communityName, String appVersion, String deviceType, String pushNotificationToken, String iphoneToken, String transactionReceipt) throws ServletException,
			IOException {
		if (timestamp == null)
			throw new NullPointerException("The parameter timestamp is null");
		if (userToken == null)
			throw new NullPointerException("The parameter userToken is null");
		if (userName == null)
			throw new NullPointerException("The parameter userName is null");
		if (apiVersion == null)
			throw new NullPointerException("The parameter apiVersion is null");
		if (communityName == null)
			throw new NullPointerException("The parameter communityName is null");
		if (appVersion == null)
			throw new NullPointerException("The parameter appVersion is null");

		MockHttpServletResponse aHttpServletResponse;
		MockHttpServletRequest httpServletRequest;
		aHttpServletResponse = new MockHttpServletResponse();
		final String requestURI = "/o2/3.9/ACC_CHECK";
		httpServletRequest = new MockHttpServletRequest("POST", requestURI);
		httpServletRequest.setPathInfo(requestURI);
		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("USER_NAME", userName);
		httpServletRequest.addParameter("USER_TOKEN", userToken);
		httpServletRequest.addParameter("TIMESTAMP", timestamp);

		httpServletRequest.addParameter("DEVICE_TYPE", deviceType);
		httpServletRequest.addParameter("PUSH_NOTIFICATION_TOKEN", pushNotificationToken);
		httpServletRequest.addParameter("IPHONE_TOKEN", iphoneToken);
		httpServletRequest.addParameter("TRANSACTION_RECEIPT", transactionReceipt);

		dispatcherServlet.service(httpServletRequest, aHttpServletResponse);
		return aHttpServletResponse;
	}

	private MockHttpServletResponse registerUser(String aBody, String remoteAddr)
			throws ServletException, IOException {
		if (aBody == null)
			throw new NullPointerException("The parameter aBody is null");
		if (remoteAddr == null)
			throw new NullPointerException("The parameter remoteAddr is null");

		LOGGER.info(aBody);

		byte[] bodyBytes = aBody.getBytes();

		MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/REGISTER_USER");
		httpServletRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
		httpServletRequest.addHeader("Content-Length", "" + bodyBytes.length);
		httpServletRequest.setContent(bodyBytes);
		httpServletRequest.setRemoteAddr(remoteAddr);
		httpServletRequest.setPathInfo("/REGISTER_USER");
		dispatcherServlet.service(httpServletRequest, aHttpServletResponse);
		return aHttpServletResponse;
	}

	private MockHttpServletResponse checkPin(String timestamp,
			String userToken, String apiVersion, String userName,
			String communityName, String appVersion, String pin)
			throws ServletException, IOException {
		if (timestamp == null)
			throw new NullPointerException("The parameter timestamp is null");
		if (userToken == null)
			throw new NullPointerException("The parameter userToken is null");
		if (apiVersion == null)
			throw new NullPointerException("The parameter apiVersion is null");
		if (userName == null)
			throw new NullPointerException("The parameter userName is null");
		if (communityName == null)
			throw new NullPointerException("The parameter communityName is null");
		if (appVersion == null)
			throw new NullPointerException("The parameter appVersion is null");
		if (pin == null)
			throw new NullPointerException("The parameter pin is null");

		MockHttpServletResponse aHttpServletResponse;
		MockHttpServletRequest httpServletRequest;
		aHttpServletResponse = new MockHttpServletResponse();
		httpServletRequest = new MockHttpServletRequest("POST", "/CHECK_PIN");
		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("USER_NAME", userName);
		httpServletRequest.addParameter("USER_TOKEN", userToken);
		httpServletRequest.addParameter("TIMESTAMP", timestamp);
		httpServletRequest.addParameter("PIN", pin);

		dispatcherServlet.service(httpServletRequest, aHttpServletResponse);
		return aHttpServletResponse;
	}

	private MockHttpServletResponse getFile(String userName, String timestamp,
			String apiVersion, String communityName, String appVersion,
			String userToken, String id, String type) throws ServletException,
			IOException {
		if (userName == null)
			throw new NullPointerException("The parameter userName is null");
		if (timestamp == null)
			throw new NullPointerException("The parameter timestamp is null");
		if (apiVersion == null)
			throw new NullPointerException("The parameter apiVersion is null");
		if (communityName == null)
			throw new NullPointerException("The parameter communityName is null");
		if (appVersion == null)
			throw new NullPointerException("The parameter appVersion is null");
		if (userToken == null)
			throw new NullPointerException("The parameter userToken is null");
		if (id == null)
			throw new NullPointerException("The parameter id is null");
		if (type == null)
			throw new NullPointerException("The parameter type is null");

		MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(
				"POST", "/GET_FILE");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.addHeader("Content-Length", "0");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/GET_FILE");

		httpServletRequest.addParameter("ID", id);
		httpServletRequest.addParameter("TYPE", type);
		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("USER_NAME", userName);
		httpServletRequest.addParameter("USER_TOKEN", userToken);
		httpServletRequest.addParameter("TIMESTAMP", timestamp);

		dispatcherServlet.service(httpServletRequest, aHttpServletResponse);
		return aHttpServletResponse;
	}

	@Test
	public void test_EntityControllerHandleException() {
		try {
			acc_check("", "", "testUser", "", "testCommunity", "", null, null, null, null);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void test_FileControllerHandleException() {
		try {
			getFile("testUser",
					"", "", "testCommunity", "",
					"", "", "");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
    @Ignore
	public void test_GET_PAYMENT_POLICY_command() {
		try {
			String apiVersion = "V1.2";
			String communityName = "Now Music";
			String appVersion = "CNBETA";

			MockHttpServletResponse aHttpServletResponse;
			MockHttpServletRequest httpServletRequest;
			aHttpServletResponse = new MockHttpServletResponse();
			httpServletRequest = new MockHttpServletRequest("POST",
					"/GET_PAYMENT_POLICY");
			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);

			dispatcherServlet.service(httpServletRequest, aHttpServletResponse);

			String responseBody = aHttpServletResponse.getContentAsString();

			assertNotNull(responseBody);

			assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
					+ "<response>"
					+ "<PaymentPolicy>"
					+ "<id>8</id>"
					+ "<operator>0</operator>"
					+ "<paymentType>creditCard</paymentType>"
					+ "<shortCode></shortCode>"
					+ "<subcost>5</subcost>"
					+ "<subweeks>5</subweeks>"
					+ "</PaymentPolicy>"
					+ "<PaymentPolicy>"
					+ "<id>9</id>"
					+ "<operator>2</operator>"
					+ "<operatorName>Vodafone UK</operatorName>"
					+ "<paymentType>PSMS</paymentType>"
					+ "<shortCode>80988</shortCode>"
					+ "<subcost>4</subcost>"
					+ "<subweeks>3</subweeks>"
					+ "</PaymentPolicy>"
					+ "<PaymentPolicy>"
					+ "<id>10</id>"
					+ "<operator>4</operator>"
					+ "<operatorName>T-Mobile UK</operatorName>"
					+ "<paymentType>PSMS</paymentType>"
					+ "<shortCode>80988</shortCode>"
					+ "<subcost>4</subcost>"
					+ "<subweeks>3</subweeks>"
					+ "</PaymentPolicy>"
					+ "<PaymentPolicy>"
					+ "<id>11</id>"
					+ "<operator>14</operator>"
					+ "<operatorName>Virgin Mobile</operatorName>"
					+ "<paymentType>PSMS</paymentType>"
					+ "<shortCode>80988</shortCode>"
					+ "<subcost>4</subcost>"
					+ "<subweeks>3</subweeks>"
					+ "</PaymentPolicy>"
					+ "<PaymentPolicy>"
					+ "<id>12</id>"
					+ "<operator>1</operator>"
					+ "<operatorName>Orange UK</operatorName>"
					+ "<paymentType>PSMS</paymentType>"
					+ "<shortCode>80988</shortCode>"
					+ "<subcost>4</subcost>"
					+ "<subweeks>3</subweeks>"
					+ "</PaymentPolicy>"
					+ "<PaymentPolicy>"
					+ "<id>13</id>"
					+ "<operator>5</operator>"
					+ "<operatorName>Three UK</operatorName>"
					+ "<paymentType>PSMS</paymentType>"
					+ "<shortCode>80988</shortCode>"
					+ "<subcost>4</subcost>"
					+ "<subweeks>3</subweeks>"
					+ "</PaymentPolicy>"
					+ "<PaymentPolicy>"
					+ "<id>189</id>"
					+ "<operator>0</operator>"
					+ "<paymentType>PAY_PAL</paymentType>"
					+ "<shortCode></shortCode>"
					+ "<subcost>5</subcost>"
					+ "<subweeks>3</subweeks>"
					+ "</PaymentPolicy>"
					+ "</response>", responseBody);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	private MockHttpServletResponse updatePaymentDetails(String userName,
			String timestamp, String apiVersion, String communityName,
			String appVersion, String userToken, String aBody) throws ServletException, IOException {

		if (aBody == null)
			throw new NullPointerException("The parameter aBody is null");
		if (userName == null)
			throw new NullPointerException("The parameter userName is null");
		if (timestamp == null)
			throw new NullPointerException("The parameter timestamp is null");
		if (apiVersion == null)
			throw new NullPointerException("The parameter apiVersion is null");
		if (communityName == null)
			throw new NullPointerException("The parameter communityName is null");
		if (appVersion == null)
			throw new NullPointerException("The parameter appVersion is null");

		LOGGER.info(aBody);

		MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/UPDATE_PAYMENT_DETAILS");
		httpServletRequest.setPathInfo("/UPDATE_PAYMENT_DETAILS");

		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("USER_NAME", userName);
		httpServletRequest.addParameter("USER_TOKEN", userToken);
		httpServletRequest.addParameter("TIMESTAMP", timestamp);
		httpServletRequest.addParameter("BODY", aBody);

		dispatcherServlet.service(httpServletRequest, aHttpServletResponse);
		return aHttpServletResponse;
	}

	@Test
    @Ignore
	public void test_ACC_CHECK_UNKNOWN_NO_PROMOCODE() {
		try {
			String password = "zzz@z.com";
			String userName = "zzz@z.com";
			String timestamp = "1";
			String apiVersion = "V1.2";
			String communityName = "Now Music";
			String appVersion = "CNBETA";

			String deviceType = "ANDROID";
			String displayName = "Nigel";
			String deviceString = "Device 1";
			String phoneNumber = "00447580381128";
			int operator = 1;

			String storedToken = Utils.createStoredToken(userName, password);
			String userToken = Utils.createTimestampToken(storedToken, timestamp);
			String promoCode = "";

			String aBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
					+ "<userRegInfo>"
					+ "<address>33333</address>"
					+ "<appVersion>" + appVersion + "</appVersion>"
					+ "<apiVersion>" + apiVersion + "</apiVersion>"
					+ "<deviceType>" + deviceType + "</deviceType>"
					+ "<deviceString>" + deviceString + "</deviceString>"
					+ "<countryFullName>Great Britain</countryFullName>"
					+ "<phoneNumber>" + phoneNumber + "</phoneNumber>"
					+ "<operator>" + operator + "</operator>"
					+ "<city>33</city>"
					+ "<firstName>33</firstName>"
					+ "<lastName>33</lastName>"
					+ "<email>" + userName + "</email>"
					+ "<communityName>" + communityName + "</communityName>"
					+ "<displayName>" + displayName + "</displayName>"
					+ "<postCode>null</postCode>"
					+ "<paymentType>" + UserRegInfo.PaymentType.UNKNOWN + "</paymentType>"
					+ "<promotionCode>" + promoCode + "</promotionCode>"
					+ "<storedToken>" + storedToken + "</storedToken>"
					+ "</userRegInfo>";

			MockHttpServletResponse mockHttpServletResponse = registerUser(aBody, "2.24.0.1");
			assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());

			User user = userDao.findByNameAndCommunity(userName, communityName);
			assertNotNull(user);

			assertEquals(UserStatus.EULA.getCode(), user.getUserStatusId());

			String paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getNULL().getName(), paymentStatus);

			mockHttpServletResponse = acc_check(timestamp, userToken, userName,
					apiVersion, communityName, appVersion, null, null, null, null);
			assertEquals(HttpStatus.UNAUTHORIZED.value(), mockHttpServletResponse
					.getStatus());

			assertEquals(
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><errorMessage><displayMessage>user login/pass check failed for [zzz@z.com] username and community [Now Music]</displayMessage><message>user login/pass check failed for [zzz@z.com] username and community [Now Music]</message></errorMessage></response>",
					mockHttpServletResponse.getContentAsString());

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}

	}

	@Test
    @Ignore
	public void getPromoCode_Successful() {
		try {
			String apiVersion = "V1.2";
			String communityName = "Now Music";
			String appVersion = "CNBETA";

			MockHttpServletResponse aHttpServletResponse;
			MockHttpServletRequest httpServletRequest;

			aHttpServletResponse = new MockHttpServletResponse();
			httpServletRequest = new MockHttpServletRequest("POST", "/GET_PROMO_CODE");
			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);

			dispatcherServlet.service(httpServletRequest, aHttpServletResponse);

			String responseBody = aHttpServletResponse.getContentAsString();

			assertNotNull(responseBody);

			assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><promoCode>promo</promoCode></response>", responseBody);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testSET_DEVICE() throws Exception {
		try {
			String userName = "zzz@z.com";
			String apiVersion = "V3.6";
			String communityName = "o2";
			String appVersion = "CNBETA";
			String phone = "07870111111";
			String timestamp = "2011_12_26_07_04_23";
			String deviceType = UserRegInfo.DeviceType.ANDROID;
			String deviceString = "Device 1";

			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/o2/3.6/SIGN_UP_DEVICE");
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.setRemoteAddr("2.24.0.1");
			httpServletRequest.setPathInfo("/o2/3.6/SIGN_UP_DEVICE");

			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("DEVICE_UID", userName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);
			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

			MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
			dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

			assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
			String contentAsString = mockHttpServletResponse.getContentAsString();
			String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
			String userToken = Utils.createTimestampToken(storedToken, timestamp);

			MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
			final String setDevicerequestURI = "/SET_DEVICE";
			httpServletRequest = new MockHttpServletRequest(
					"POST", setDevicerequestURI);
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.addHeader("Content-Length", "0");
			httpServletRequest.setRemoteAddr("2.24.0.1");
			httpServletRequest.setPathInfo(setDevicerequestURI);

			User user = userService.findByNameAndCommunity(userName, communityName);
			assertNotNull(user);
			assertEquals(DeviceTypeDao.findIdByName(deviceType), user.getDeviceTypeId());
			//assertEquals(deviceString, user.getDeviceString());

			String newDeviceType = UserRegInfo.DeviceType.BLACKBERRY;
			String newDeviceString = "newDeviceString";

			httpServletRequest.addParameter("DEVICE_TYPE", newDeviceType);
			httpServletRequest.addParameter("DEVICE_UID", newDeviceString);
			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);
			httpServletRequest.addParameter("USER_NAME", userName);
			httpServletRequest.addParameter("USER_TOKEN", userToken);
			httpServletRequest.addParameter("TIMESTAMP", timestamp);

			dispatcherServlet.service(httpServletRequest, aHttpServletResponse);

			assertEquals(200, aHttpServletResponse.getStatus());

			String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
					+ "<response>"
					+ "<deviceSet>OK</deviceSet>"
					+ "</response>";

			assertEquals(expected, aHttpServletResponse.getContentAsString());

			user = userService.findById(user.getId());
			assertNotNull(user);
			assertEquals(DeviceTypeDao.findIdByName(newDeviceType), user.getDeviceTypeId());
			assertEquals(newDeviceString, user.getDeviceString());

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
    @Ignore
	public void testBUY_TRACK() throws Exception {
		try {
			String userName = "zzz@z.com";
			String apiVersion = "3.5";
			String communityName = "o2";
			String appVersion = "CNBETA";
			String phone = "07870111111";
			String timestamp = "2011_12_26_07_04_23";
			String deviceType = UserRegInfo.DeviceType.ANDROID;
			String comunityUrl = "o2";

			final String requestURI = "/"+comunityUrl+"/"+apiVersion+"/SIGN_UP_DEVICE";
			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", requestURI);
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.setRemoteAddr("2.24.0.1");
			httpServletRequest.setPathInfo(requestURI);

			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("DEVICE_UID", userName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);
			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

			MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
			dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

			assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
			String contentAsString = mockHttpServletResponse.getContentAsString();
			String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
			String userToken = Utils.createTimestampToken(storedToken, timestamp);

			String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><user><activation>REGISTERED</activation><chartItems>21</chartItems><chartTimestamp>1321452650</chartTimestamp><deviceType>ANDROID</deviceType><deviceUID></deviceUID><displayName></displayName><drmType>PLAYS</drmType><drmValue>100</drmValue><freeTrial>false</freeTrial><fullyRegistred>true</fullyRegistred><hasOffers>false</hasOffers><hasPotentialPromoCodePromotion>false</hasPotentialPromoCodePromotion><newsItems>10</newsItems><newsTimestamp>1317300123</newsTimestamp><nextSubPaymentSeconds>0</nextSubPaymentSeconds><operator>1</operator><paymentEnabled>false</paymentEnabled><paymentStatus>NULL</paymentStatus><paymentType>UNKNOWN</paymentType><phoneNumber></phoneNumber><promotedDevice>false</promotedDevice><promotedWeeks>0</promotedWeeks><rememberMeToken>enp6QHouY29tOjEzNjIwNDQ3Nzk1Mjg6MTBmNzI3YWFkMWMxZmM3NGMxMmYxYmUyN2E1ODU0ODI</rememberMeToken><status>LIMITED</status><subBalance>0</subBalance><timeOfMovingToLimitedStatusSeconds>0</timeOfMovingToLimitedStatusSeconds><userName>zzz@z.com</userName><userToken>c6c97e872b7e77ce4c4396069a220e0d</userToken><oAuthProvider>NONE</oAuthProvider></user><chart><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>5</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>1</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>145</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>2</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>38</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>3</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>44</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>4</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1285</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>5</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>436</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>6</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>44</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>7</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>2</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>8</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>9</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>33</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>10</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>8888</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>11</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>555</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>12</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>2</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>13</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>14</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>6</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>15</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>925</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>16</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>3</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>17</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>18</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>11</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>19</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>111</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>true</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>20</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>98</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>USAT21001886</media><playlistId>5</playlistId><position>21</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><playlist><id>5</id><playlistTitle>Default Chart</playlistTitle><subtitle>Default Chart</subtitle></playlist></chart></response>";
			getChart(expected, userName, timestamp, apiVersion, comunityUrl, communityName, appVersion, userToken, storedToken);

			String mediaIsrc = "USUM71100721";
			String type = FileService.FileType.HEADER.name();
			getFile(userName, timestamp, apiVersion, communityName, appVersion, userToken, mediaIsrc, type);

			MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
			final String buyTrackRequestURI = "/BUY_TRACK";
			httpServletRequest = new MockHttpServletRequest(
					"POST", buyTrackRequestURI);
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.addHeader("Content-Length", "0");
			httpServletRequest.setRemoteAddr("2.24.0.1");
			httpServletRequest.setPathInfo(buyTrackRequestURI);

			httpServletRequest.addParameter("MEDIA_UID", mediaIsrc);
			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);
			httpServletRequest.addParameter("USER_NAME", userName);
			httpServletRequest.addParameter("USER_TOKEN", userToken);
			httpServletRequest.addParameter("TIMESTAMP", timestamp);

			dispatcherServlet.service(httpServletRequest, aHttpServletResponse);

			assertEquals(200, aHttpServletResponse.getStatus());

			expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
					+ "<response>"
					+ "<user>"
					+ "<chartItems>40</chartItems>"
					+ "<chartTimestamp>1321452650</chartTimestamp>"
					+ "<deviceType>ANDROID</deviceType>"
					+ "<deviceUID>Device 1</deviceUID>"
					+ "<displayName>Nigel</displayName>"
					+ "<drmType>PLAYS</drmType>"
					+ "<drmValue>100</drmValue>"
					+ "<newsItems>10</newsItems>"
					+ "<newsTimestamp>1317300123</newsTimestamp>"
					+ "<operator>1</operator>"
					+ "<paymentEnabled>true</paymentEnabled>"
					+ "<paymentStatus>OK</paymentStatus>"
					+ "<paymentType>PSMS</paymentType>"
					+ "<phoneNumber>07580381128</phoneNumber>"
					+ "<status>SUBSCRIBED</status>"
					+ "<subBalance>6</subBalance>"
					+ "</user>"
					+ "<buyTrack>OK</buyTrack>"
					+ "</response>";

			assertEquals(expected, aHttpServletResponse.getContentAsString());

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
    @Ignore
	public void testGET_CHART() throws Exception {
		try {
			String userName = "zzz@z.com";
			String timestamp = "2011_12_26_07_04_23";
			String apiVersion = "3.5";
			String communityName = "Now Music";
			String appVersion = "CNBETA";
			String communityUrl="nowtop40";

			String deviceType = UserRegInfo.DeviceType.ANDROID;

			String ipAddress = "2.24.0.1";

			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/" + apiVersion + "/SIGN_UP_DEVICE");
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.setRemoteAddr(ipAddress);
			httpServletRequest.setPathInfo("/" + apiVersion + "/SIGN_UP_DEVICE");

			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("DEVICE_UID", userName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);
			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

			MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
			dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

			assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());

			String contentAsString = mockHttpServletResponse.getContentAsString();
			String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
			String userToken = Utils.createTimestampToken(storedToken, timestamp);
			
			String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><user><activation>REGISTERED</activation><chartItems>21</chartItems><chartTimestamp>1321452650</chartTimestamp><deviceType>ANDROID</deviceType><deviceUID></deviceUID><displayName></displayName><drmType>PLAYS</drmType><drmValue>100</drmValue><freeTrial>false</freeTrial><fullyRegistred>true</fullyRegistred><hasOffers>false</hasOffers><hasPotentialPromoCodePromotion>false</hasPotentialPromoCodePromotion><newsItems>10</newsItems><newsTimestamp>1317300123</newsTimestamp><nextSubPaymentSeconds>0</nextSubPaymentSeconds><operator>1</operator><paymentEnabled>false</paymentEnabled><paymentStatus>NULL</paymentStatus><paymentType>UNKNOWN</paymentType><phoneNumber></phoneNumber><promotedDevice>false</promotedDevice><promotedWeeks>0</promotedWeeks><rememberMeToken>enp6QHouY29tOjEzNjIwNTAzNjY2Mzk6Nzg3NmY1NTJlYmM1ZWVhMWVkZjk5ZjM5M2NlOThiNDc</rememberMeToken><status>LIMITED</status><subBalance>0</subBalance><timeOfMovingToLimitedStatusSeconds>0</timeOfMovingToLimitedStatusSeconds><userName>zzz@z.com</userName><userToken>a10be9afc8033f3e141d81e74f608434</userToken><oAuthProvider>NONE</oAuthProvider></user><chart><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>5</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>1</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>145</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>2</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>38</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>3</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>44</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>4</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1285</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>5</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>436</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>6</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>44</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>7</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>2</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>8</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>9</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>33</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>10</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>8888</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>11</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>555</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>12</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>2</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>13</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>14</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>6</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>15</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>925</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>16</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>3</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>17</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>18</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>11</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>19</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>111</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>true</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>20</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>98</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>USAT21001886</media><playlistId>5</playlistId><position>21</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><playlist><id>5</id><playlistTitle>Default Chart</playlistTitle><subtitle>Default Chart</subtitle></playlist></chart></response>";
			getChart(expected, userName, timestamp, apiVersion, communityUrl, communityName, appVersion, userToken, storedToken);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testGET_CHART_O2_v3p6() throws Exception {
		try {
			String password = "zzz@z.com";
			String userName = "zzz@z.com";
			String timestamp = "2011_12_26_07_04_23";
			String apiVersion = "3.6";
			String communityName = "o2";
			String appVersion = "CNBETA";
			String communityUrl="o2";
			
			String deviceString = "Device 1";
			String deviceType = UserRegInfo.DeviceType.ANDROID;
			
			String ipAddress = "2.24.0.1";
			
			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/" + apiVersion + "/SIGN_UP_DEVICE");
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.setRemoteAddr(ipAddress);
			httpServletRequest.setPathInfo("/" + apiVersion + "/SIGN_UP_DEVICE");
			
			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("DEVICE_UID", userName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);
			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("DEVICE_TYPE", deviceType);
			
			MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
			dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);
			
			assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
			
			String contentAsString = mockHttpServletResponse.getContentAsString();
			String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
			String userToken = Utils.createTimestampToken(storedToken, timestamp);
			
			String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><user><activation>REGISTERED</activation><chartItems>21</chartItems><chartTimestamp>1321452650</chartTimestamp><deviceType>ANDROID</deviceType><deviceUID></deviceUID><displayName></displayName><drmType>PLAYS</drmType><drmValue>100</drmValue><freeTrial>false</freeTrial><fullyRegistred>true</fullyRegistred><hasOffers>false</hasOffers><hasPotentialPromoCodePromotion>false</hasPotentialPromoCodePromotion><newsItems>10</newsItems><newsTimestamp>1317300123</newsTimestamp><nextSubPaymentSeconds>0</nextSubPaymentSeconds><operator>1</operator><paymentEnabled>false</paymentEnabled><paymentStatus>NULL</paymentStatus><paymentType>UNKNOWN</paymentType><phoneNumber></phoneNumber><promotedDevice>false</promotedDevice><promotedWeeks>0</promotedWeeks><rememberMeToken>enp6QHouY29tOjEzNjIwNDk2NjgzMTQ6YmU0ZDgyOGQ0ZmFiMDEwMDM2NjU0NzgxODIwNjBiM2E</rememberMeToken><status>LIMITED</status><subBalance>0</subBalance><timeOfMovingToLimitedStatusSeconds>0</timeOfMovingToLimitedStatusSeconds><userName>zzz@z.com</userName><userToken>b36646dd7dcf6d76bec4404aa1ed1f71</userToken><oAuthProvider>NONE</oAuthProvider></user><chart><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>5</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>1</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>145</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>2</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>38</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>3</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>44</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>4</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1285</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>5</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>436</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>6</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>44</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>7</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>2</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>8</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>9</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>33</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>10</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>8888</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>11</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>555</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>12</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>2</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>13</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>14</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>6</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>15</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>925</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>16</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>3</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>17</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>18</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>11</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>19</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>111</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>true</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>20</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>98</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>USAT21001886</media><playlistId>5</playlistId><position>21</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><playlist><id>5</id><playlistTitle>Default Chart</playlistTitle><subtitle>Default Chart</subtitle></playlist></chart></response>";
			getChart(expected, userName, timestamp, apiVersion, communityUrl, communityName, appVersion, userToken, storedToken);
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testGET_CHART_O2_v3p7() throws Exception {
		try {
			String password = "zzz@z.com";
			String userName = "zzz@z.com";
			String timestamp = "2011_12_26_07_04_23";
			String apiVersion = "3.7";
			String communityName = "o2";
			String appVersion = "CNBETA";
			String communityUrl="o2";

			String deviceString = "Device 1";
			String deviceType = UserRegInfo.DeviceType.ANDROID;

			String ipAddress = "2.24.0.1";

			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/" + apiVersion + "/SIGN_UP_DEVICE");
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.setRemoteAddr(ipAddress);
			httpServletRequest.setPathInfo("/" + apiVersion + "/SIGN_UP_DEVICE");

			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("DEVICE_UID", userName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);
			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

			MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
			dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

			assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());

			String contentAsString = mockHttpServletResponse.getContentAsString();
			String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
			String userToken = Utils.createTimestampToken(storedToken, timestamp);
			
			String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><user><activation>REGISTERED</activation><chartItems>21</chartItems><chartTimestamp>1321452650</chartTimestamp><deviceType>ANDROID</deviceType><deviceUID></deviceUID><displayName></displayName><drmType>PLAYS</drmType><drmValue>100</drmValue><freeTrial>false</freeTrial><fullyRegistred>true</fullyRegistred><hasOffers>false</hasOffers><hasPotentialPromoCodePromotion>false</hasPotentialPromoCodePromotion><newsItems>10</newsItems><newsTimestamp>1317300123</newsTimestamp><nextSubPaymentSeconds>0</nextSubPaymentSeconds><operator>1</operator><paymentEnabled>false</paymentEnabled><paymentStatus>NULL</paymentStatus><paymentType>UNKNOWN</paymentType><phoneNumber></phoneNumber><promotedDevice>false</promotedDevice><promotedWeeks>0</promotedWeeks><rememberMeToken>enp6QHouY29tOjEzNjIwNDk2NjgzMTQ6YmU0ZDgyOGQ0ZmFiMDEwMDM2NjU0NzgxODIwNjBiM2E</rememberMeToken><status>LIMITED</status><subBalance>0</subBalance><timeOfMovingToLimitedStatusSeconds>0</timeOfMovingToLimitedStatusSeconds><userName>zzz@z.com</userName><userToken>b36646dd7dcf6d76bec4404aa1ed1f71</userToken><oAuthProvider>NONE</oAuthProvider></user><chart><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>5</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>1</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>145</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>2</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>38</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>3</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>44</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>4</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1285</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>5</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>436</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>6</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>44</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>7</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>2</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>8</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>9</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>33</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>10</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>8888</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>11</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>555</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>12</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>2</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>13</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>14</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>6</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>15</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>925</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>16</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>3</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>17</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>18</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>11</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>19</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>111</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>true</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>20</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>98</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>USAT21001886</media><playlistId>5</playlistId><position>21</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><playlist><id>5</id><playlistTitle>Default Chart</playlistTitle><subtitle>Default Chart</subtitle></playlist></chart></response>";
			getChart(expected, userName, timestamp, apiVersion, communityUrl, communityName, appVersion, userToken, storedToken);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

    @Test
    public void testGET_CHART_O2_v3p8() throws Exception {
        try {
            String password = "zzz@z.com";
            String userName = "zzz@z.com";
            String timestamp = "2011_12_26_07_04_23";
            String apiVersion = "3.8";
            String communityName = "o2";
            String appVersion = "CNBETA";
            String communityUrl="o2";

            String deviceString = "Device 1";
            String deviceType = UserRegInfo.DeviceType.ANDROID;

            String ipAddress = "2.24.0.1";

            MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/" + apiVersion + "/SIGN_UP_DEVICE");
            httpServletRequest.addHeader("Content-Type", "text/xml");
            httpServletRequest.setRemoteAddr(ipAddress);
            httpServletRequest.setPathInfo("/" + apiVersion + "/SIGN_UP_DEVICE");

            httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
            httpServletRequest.addParameter("DEVICE_UID", userName);
            httpServletRequest.addParameter("API_VERSION", apiVersion);
            httpServletRequest.addParameter("APP_VERSION", appVersion);
            httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

            MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
            dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

            assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());

            String contentAsString = mockHttpServletResponse.getContentAsString();
            String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
            String userToken = Utils.createTimestampToken(storedToken, timestamp);

            String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><user><activation>REGISTERED</activation><chartItems>21</chartItems><chartTimestamp>1321452650</chartTimestamp><deviceType>ANDROID</deviceType><deviceUID></deviceUID><displayName></displayName><drmType>PLAYS</drmType><drmValue>100</drmValue><freeTrial>false</freeTrial><fullyRegistred>true</fullyRegistred><hasOffers>false</hasOffers><hasPotentialPromoCodePromotion>false</hasPotentialPromoCodePromotion><newsItems>10</newsItems><newsTimestamp>1317300123</newsTimestamp><nextSubPaymentSeconds>0</nextSubPaymentSeconds><operator>1</operator><paymentEnabled>false</paymentEnabled><paymentStatus>NULL</paymentStatus><paymentType>UNKNOWN</paymentType><phoneNumber></phoneNumber><promotedDevice>false</promotedDevice><promotedWeeks>0</promotedWeeks><rememberMeToken>enp6QHouY29tOjEzNjIwNDk2NjgzMTQ6YmU0ZDgyOGQ0ZmFiMDEwMDM2NjU0NzgxODIwNjBiM2E</rememberMeToken><status>LIMITED</status><subBalance>0</subBalance><timeOfMovingToLimitedStatusSeconds>0</timeOfMovingToLimitedStatusSeconds><userName>zzz@z.com</userName><userToken>b36646dd7dcf6d76bec4404aa1ed1f71</userToken><oAuthProvider>NONE</oAuthProvider></user><chart><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>5</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>1</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>145</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>2</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>38</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>3</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>44</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>4</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1285</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>5</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>436</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>6</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>44</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>7</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>2</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>8</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>9</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>33</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>10</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>8888</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>11</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>555</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>12</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>2</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>13</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>14</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>6</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>15</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>925</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>16</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>3</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>17</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>18</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>11</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>19</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>111</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>true</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>20</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>98</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>USAT21001886</media><playlistId>5</playlistId><position>21</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><playlist><id>5</id><playlistTitle>Default Chart</playlistTitle><subtitle>Default Chart</subtitle></playlist></chart></response>";
            getChart(expected, userName, timestamp, apiVersion, communityUrl, communityName, appVersion, userToken, storedToken);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

	@Test
    @Ignore
	public void testGET_NEWS() throws Exception {
		try {
			String password = "zzz@z.com";
			String userName = "zzz@z.com";
			String timestamp = "2011_12_26_07_04_23";
			String apiVersion = "3.7";
			String communityName = "o2";
			String appVersion = "CNBETA";
			String communityUrl="o2";

			String deviceString = "Device 1";
			String deviceType = UserRegInfo.DeviceType.ANDROID;

			String ipAddress = "2.24.0.1";

			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/" + apiVersion + "/SIGN_UP_DEVICE");
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.setRemoteAddr(ipAddress);
			httpServletRequest.setPathInfo("/" + apiVersion + "/SIGN_UP_DEVICE");

			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("DEVICE_UID", userName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);
			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

			MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
			dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

			assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());

			String contentAsString = mockHttpServletResponse.getContentAsString();
			String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
			String userToken = Utils.createTimestampToken(storedToken, timestamp);

			MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
			httpServletRequest = new MockHttpServletRequest(
					"POST", "/" + communityUrl + "/" +apiVersion+ "/GET_NEWS");
			httpServletRequest.setPathInfo(communityUrl + "/" +apiVersion+ "/GET_NEWS");
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.addHeader("Content-Length", "0");
			httpServletRequest.setRemoteAddr("2.24.0.1");

			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);
			httpServletRequest.addParameter("USER_NAME", userName);
			httpServletRequest.addParameter("USER_TOKEN", userToken);
			httpServletRequest.addParameter("TIMESTAMP", timestamp);

			dispatcherServlet.service(httpServletRequest, aHttpServletResponse);

			assertEquals(200, aHttpServletResponse.getStatus());

			contentAsString = aHttpServletResponse.getContentAsString();
			String ending = contentAsString.substring(718);
			LOGGER.debug(ending);
			assertTrue(contentAsString
					.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><user><chartItems>21</chartItems><chartTimestamp>1321452650</chartTimestamp><deviceType>ANDROID</deviceType><deviceUID>Device 1</deviceUID><displayName>displayName</displayName><drmType>PLAYS</drmType><drmValue>100</drmValue><fullyRegistred>true</fullyRegistred><newsItems>10</newsItems><newsTimestamp>1317300123</newsTimestamp><operator>1</operator><paymentEnabled>false</paymentEnabled><paymentStatus>NULL</paymentStatus><paymentType>UNKNOWN</paymentType><phoneNumber></phoneNumber><promotedDevice>false</promotedDevice><promotedWeeks>0</promotedWeeks><status>SUBSCRIBED</status><subBalance>0</subBalance><timeOfMovingToLimitedStatusSeconds>"));
			assertEquals(
					"</timeOfMovingToLimitedStatusSeconds><userName>zzz@z.com</userName><userToken>f2ad4ecbe7b82b873224a2ccbcf3f3c2</userToken><oAuthProvider>NONE</oAuthProvider></user><news><item><body>Blue Ivy Carter, new daughter of Beyonce and JayZ, is already making chart history! Daddy Z features cute cries from little Princess B on his new track, Glory, making her the youngest person ever to appear in the Billboard chart!</body><detail>Blue Ivy Carter, new daughter of Beyonce and JayZ, is already making chart history! Daddy Z features cute cries from little Princess B on his new track, Glory, making her the youngest person ever to appear in the Billboard chart!</detail><i>1</i><messageType>NEWS</messageType><position>1</position><timestampMilis>1315686788000</timestampMilis></item><item><body>Flipping back to music after producing new movie W.E., Madonna revealed that her 12th album will be called MDNA and will more than likely be released in March. The first single Gimme All Your Luvin will feature Nicki Minaj and MIA.</body><detail>Flipping back to music after producing new movie W.E., Madonna revealed that her 12th album will be called MDNA and will more than likely be released in March. The first single Gimme All Your Luvin will feature Nicki Minaj and MIA.</detail><i>4</i><messageType>NEWS</messageType><position>4</position><timestampMilis>1315686788000</timestampMilis></item><item><body>Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!</body><detail>Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!</detail><i>7</i><messageType>NEWS</messageType><position>7</position><timestampMilis>1315686788000</timestampMilis></item><item><body>With Little Mix winning X Factor, an original Sugababes reunion and a rumoured new 10th anniversary Girls Aloud album, 2012 looks like the year of the girl band! Wonder what the Spice Girls are up to?</body><detail>With Little Mix winning X Factor, an original Sugababes reunion and a rumoured new 10th anniversary Girls Aloud album, 2012 looks like the year of the girl band! Wonder what the Spice Girls are up to?</detail><i>9</i><messageFrequence>DAILY</messageFrequence><messageType>NOTIFICATION</messageType><position>9</position><timestampMilis>1315686788000</timestampMilis></item><item><body>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</body><detail>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</detail><i>10</i><messageType>NEWS</messageType><position>10</position><timestampMilis>1315686788000</timestampMilis></item></news></response>",
					ending);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
    @Ignore
	public void testGET_NEWS_with_uniqId() throws Exception {
		try {
			String password = "zzz@z.com";
			String userName = "zzz@z.com";
			String timestamp = "2011_12_26_07_04_23";
			String apiVersion = "3.7";
			String communityName = "o2";
			String appVersion = "CNBETA";
			String communityUrl="o2";
			String uniqueId = "dfdf";
			
			String deviceString = "Device 1";
			String deviceType = UserRegInfo.DeviceType.ANDROID;
			
			String ipAddress = "2.24.0.1";
			
			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/" + apiVersion + "/SIGN_UP_DEVICE");
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.setRemoteAddr(ipAddress);
			httpServletRequest.setPathInfo("/" + apiVersion + "/SIGN_UP_DEVICE");
			
			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("DEVICE_UID", userName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);
			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("DEVICE_TYPE", deviceType);
			
			MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
			dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);
			
			assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
			
			String contentAsString = mockHttpServletResponse.getContentAsString();
			String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
			String userToken = Utils.createTimestampToken(storedToken, timestamp);
			
			MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
			httpServletRequest = new MockHttpServletRequest(
					"POST", "/"+ uniqueId+"/" + communityUrl + "/" +apiVersion+ "/GET_NEWS");
			httpServletRequest.setPathInfo(communityUrl + "/" +apiVersion+ "/GET_NEWS");
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.addHeader("Content-Length", "0");
			httpServletRequest.setRemoteAddr("2.24.0.1");
			
			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);
			httpServletRequest.addParameter("USER_NAME", userName);
			httpServletRequest.addParameter("USER_TOKEN", userToken);
			httpServletRequest.addParameter("TIMESTAMP", timestamp);
			
			dispatcherServlet.service(httpServletRequest, aHttpServletResponse);
			
			assertEquals(200, aHttpServletResponse.getStatus());
			
			contentAsString = aHttpServletResponse.getContentAsString();
			String ending = contentAsString.substring(718);
			LOGGER.debug(ending);
			assertTrue(contentAsString
					.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><user><chartItems>21</chartItems><chartTimestamp>1321452650</chartTimestamp><deviceType>ANDROID</deviceType><deviceUID>Device 1</deviceUID><displayName>displayName</displayName><drmType>PLAYS</drmType><drmValue>100</drmValue><fullyRegistred>true</fullyRegistred><newsItems>10</newsItems><newsTimestamp>1317300123</newsTimestamp><operator>1</operator><paymentEnabled>false</paymentEnabled><paymentStatus>NULL</paymentStatus><paymentType>UNKNOWN</paymentType><phoneNumber></phoneNumber><promotedDevice>false</promotedDevice><promotedWeeks>0</promotedWeeks><status>SUBSCRIBED</status><subBalance>0</subBalance><timeOfMovingToLimitedStatusSeconds>"));
			assertEquals(
					"</timeOfMovingToLimitedStatusSeconds><userName>zzz@z.com</userName><userToken>f2ad4ecbe7b82b873224a2ccbcf3f3c2</userToken><oAuthProvider>NONE</oAuthProvider></user><news><item><body>Blue Ivy Carter, new daughter of Beyonce and JayZ, is already making chart history! Daddy Z features cute cries from little Princess B on his new track, Glory, making her the youngest person ever to appear in the Billboard chart!</body><detail>Blue Ivy Carter, new daughter of Beyonce and JayZ, is already making chart history! Daddy Z features cute cries from little Princess B on his new track, Glory, making her the youngest person ever to appear in the Billboard chart!</detail><i>1</i><messageType>NEWS</messageType><position>1</position><timestampMilis>1315686788000</timestampMilis></item><item><body>Flipping back to music after producing new movie W.E., Madonna revealed that her 12th album will be called MDNA and will more than likely be released in March. The first single Gimme All Your Luvin will feature Nicki Minaj and MIA.</body><detail>Flipping back to music after producing new movie W.E., Madonna revealed that her 12th album will be called MDNA and will more than likely be released in March. The first single Gimme All Your Luvin will feature Nicki Minaj and MIA.</detail><i>4</i><messageType>NEWS</messageType><position>4</position><timestampMilis>1315686788000</timestampMilis></item><item><body>Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!</body><detail>Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!</detail><i>7</i><messageType>NEWS</messageType><position>7</position><timestampMilis>1315686788000</timestampMilis></item><item><body>With Little Mix winning X Factor, an original Sugababes reunion and a rumoured new 10th anniversary Girls Aloud album, 2012 looks like the year of the girl band! Wonder what the Spice Girls are up to?</body><detail>With Little Mix winning X Factor, an original Sugababes reunion and a rumoured new 10th anniversary Girls Aloud album, 2012 looks like the year of the girl band! Wonder what the Spice Girls are up to?</detail><i>9</i><messageFrequence>DAILY</messageFrequence><messageType>NOTIFICATION</messageType><position>9</position><timestampMilis>1315686788000</timestampMilis></item><item><body>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</body><detail>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</detail><i>10</i><messageType>NEWS</messageType><position>10</position><timestampMilis>1315686788000</timestampMilis></item></news></response>",
					ending);
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private void getChart(String expected, String userName, String timestamp, String apiVersion, String communityUrl, String communityName, String appVersion, String userToken, String storedToken)
			throws ServletException, IOException, UnsupportedEncodingException, SAXException {

		String requestURI = "/" + communityUrl + "/" + apiVersion + "/GET_CHART";
		MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
		
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(
				"POST", requestURI);
		httpServletRequest.addHeader("Content-Type", "text/ xml");
		httpServletRequest.addHeader("Content-Length", "0");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo(requestURI);

		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("USER_NAME", userName);
		httpServletRequest.addParameter("USER_TOKEN", userToken);
		httpServletRequest.addParameter("TIMESTAMP", timestamp);
		httpServletRequest.addParameter("DEVICE_UID", userName);

		dispatcherServlet.service(httpServletRequest, aHttpServletResponse);

		assertEquals(200, aHttpServletResponse.getStatus());

		final String contentAsString = aHttpServletResponse.getContentAsString();
		
		final String rememberMeToken= contentAsString.substring(contentAsString.indexOf("<rememberMeToken>")+"<rememberMeToken>".length(), contentAsString.indexOf("</rememberMeToken>"));
		userToken= contentAsString.substring(contentAsString.indexOf("<userToken>")+"<userToken>".length(), contentAsString.indexOf("</userToken>"));
		
		class ChartElementQualifier implements ElementQualifier {

			@Override
			public boolean qualifyForComparison(Element expectedElement, Element actualElement) {

				final String expectedNodeName = expectedElement.getNodeName();
				final String actualNodeName = actualElement.getNodeName();
				final Node expectedParentNode = expectedElement.getParentNode();
				final Node actualParentNode = actualElement.getParentNode();

				boolean isComparable = false;
				if ((expectedNodeName.equals("bonusTrack") && actualNodeName.equals("bonusTrack"))||(expectedNodeName.equals("track") && actualNodeName.equals("track"))) {

					if (actualParentNode != null && actualParentNode.getNodeName().equals("chart") && actualParentNode.getParentNode() != null
							&& actualParentNode.getParentNode().getNodeName().equals("response")) {

						NodeList actualNodeList = actualElement.getChildNodes();
						NodeList expectedNodeList = expectedElement.getChildNodes();
						if (actualNodeList.getLength() == expectedNodeList.getLength()) {

							Map<String, String> expectedMap = new HashMap<String, String>();
							Map<String, String> actualMap = new HashMap<String, String>();
							for (int i = 0; i < actualNodeList.getLength(); i++) {
								populate(actualNodeList, actualMap, i);

								populate(expectedNodeList, expectedMap, i);
							}
							
							String actualPosition = actualMap.get("position");
							if (expectedMap.get("position").equals(actualPosition)) {
								isComparable = true;
							}

						}
					}

				}else if (expectedNodeName.equals(actualNodeName)){
					if(expectedParentNode.getNodeName().equals(actualParentNode.getNodeName())){
						isComparable = true;
					}
				}
				return isComparable;
			}

			private Map<String, String> populate(NodeList nodeList, Map<String, String> map, int i) {
				Node currentNode = nodeList.item(i);
				populate(currentNode, "position", map );
				return map;
			}	
			
			private Map<String, String> populate(Node node, String nodeName, Map<String, String> map){
				if (node.getNodeName().equals(nodeName)){
					map.put(nodeName, node.getTextContent()); 
				}
				return map;
			}
		}
		
	
		expected = expected.replaceAll("rememberMeToken.*/rememberMeToken", "rememberMeToken>"+rememberMeToken+"</rememberMeToken");
		expected = expected.replaceAll("userToken.*/userToken", "userToken>"+userToken+"</userToken");
		
		Diff diff = new Diff(expected, contentAsString);
		diff.overrideElementQualifier(new ChartElementQualifier());
		
		//XMLAssert.assertXMLEqual(diff, true);
	}

	@Test
    @Ignore
	public void testBUY_BONUS_TRACK() throws Exception {
		try {
			String password = "zzz@z.com";
			String userName = "zzz@z.com";
			String timestamp = "1";
			String apiVersion = "V1.2";
			String communityName = "Now Music";
			String comunityUrl = "nowtop40";
			String appVersion = "CNBETA";

			String deviceString = "Device 1";
			String deviceType = UserRegInfo.DeviceType.ANDROID;

			String storedToken = Utils.createStoredToken(userName, password);
			String userToken = Utils.createTimestampToken(storedToken, timestamp);

			UserRegInfo userRegInfo = new UserRegInfo();
			userRegInfo.setEmail(userName);
			userRegInfo.setStoredToken(storedToken);
			userRegInfo.setAppVersion(appVersion);
			userRegInfo.setDeviceType(deviceType);
			userRegInfo.setCommunityName(communityName);
			userRegInfo.setDeviceString(deviceString);
			userRegInfo.setDisplayName("Nigel");
			userRegInfo.setPhoneNumber("07580381128");
			userRegInfo.setOperator(1);

			String aBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
					+ "<userRegInfo>"
					+ "<address>33333</address>"
					+ "<appVersion>" + appVersion + "</appVersion>"
					+ "<apiVersion>" + apiVersion + "</apiVersion>"
					+ "<deviceType>" + deviceType + "</deviceType>"
					+ "<deviceString>" + deviceString + "</deviceString>"
					+ "<countryFullName>Great Britain</countryFullName>"
					+ "<city>33</city>"
					+ "<firstName>33</firstName>"
					+ "<lastName>33</lastName>"
					+ "<email>" + userName + "</email>"
					+ "<communityName>" + communityName + "</communityName>"
					+ "<displayName>displayName</displayName>"
					+ "<postCode>null</postCode>"
					+ "<paymentType>" + UserRegInfo.PaymentType.UNKNOWN + "</paymentType>"
					+ "<storedToken>" + storedToken + "</storedToken>"
					+ "<promotionCode>promo</promotionCode>"
					+ "</userRegInfo>";

			MockHttpServletResponse mockHttpServletResponse = registerUser(aBody, "2.24.0.1");

			assertEquals(200, mockHttpServletResponse.getStatus());

			String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><user><activation>REGISTERED</activation><chartItems>21</chartItems><chartTimestamp>1321452650</chartTimestamp><deviceType>ANDROID</deviceType><deviceUID></deviceUID><displayName></displayName><drmType>PLAYS</drmType><drmValue>100</drmValue><freeTrial>false</freeTrial><fullyRegistred>true</fullyRegistred><hasOffers>false</hasOffers><hasPotentialPromoCodePromotion>false</hasPotentialPromoCodePromotion><newsItems>10</newsItems><newsTimestamp>1317300123</newsTimestamp><nextSubPaymentSeconds>0</nextSubPaymentSeconds><operator>1</operator><paymentEnabled>false</paymentEnabled><paymentStatus>NULL</paymentStatus><paymentType>UNKNOWN</paymentType><phoneNumber></phoneNumber><promotedDevice>false</promotedDevice><promotedWeeks>0</promotedWeeks><rememberMeToken>enp6QHouY29tOjEzNjIwNDQ3Nzk1Mjg6MTBmNzI3YWFkMWMxZmM3NGMxMmYxYmUyN2E1ODU0ODI</rememberMeToken><status>LIMITED</status><subBalance>0</subBalance><timeOfMovingToLimitedStatusSeconds>0</timeOfMovingToLimitedStatusSeconds><userName>zzz@z.com</userName><userToken>c6c97e872b7e77ce4c4396069a220e0d</userToken><oAuthProvider>NONE</oAuthProvider></user><chart><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>5</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>1</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>145</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>2</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>38</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>3</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>44</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>4</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1285</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>5</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>436</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>6</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>44</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>7</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>2</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>8</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>9</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>33</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>10</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>8888</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>11</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>555</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>12</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>2</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>13</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>14</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>6</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>15</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>925</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>16</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>3</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>17</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>18</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>11</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>19</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>111</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>true</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>20</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>98</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>USAT21001886</media><playlistId>5</playlistId><position>21</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><playlist><id>5</id><playlistTitle>Default Chart</playlistTitle><subtitle>Default Chart</subtitle></playlist></chart></response>";
			getChart(expected,userName, timestamp, apiVersion, comunityUrl, communityName, appVersion, userToken, storedToken);

			String mediaIsrc = "USUM71100721";
			String type = FileService.FileType.HEADER.name();
			getFile(userName, timestamp, apiVersion, communityName, appVersion, userToken, mediaIsrc, type);

			MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(
					"POST", "/BUY_TRACK");
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.addHeader("Content-Length", "0");
			httpServletRequest.setRemoteAddr("2.24.0.1");
			httpServletRequest.setPathInfo("/BUY_TRACK");

			httpServletRequest.addParameter("MEDIA_UID", mediaIsrc);
			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);
			httpServletRequest.addParameter("USER_NAME", userName);
			httpServletRequest.addParameter("USER_TOKEN", userToken);
			httpServletRequest.addParameter("TIMESTAMP", timestamp);

			dispatcherServlet.service(httpServletRequest, aHttpServletResponse);

			assertEquals(500, aHttpServletResponse.getStatus());

			expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
					+ "<response>"
					+ "<errorMessage>"
					+ "<displayMessage>We're sorry, but this Hot Track is not available to buy at the moment!</displayMessage>"
					+ "<message>We're sorry, but this Hot Track is not available to buy at the moment!</message>"
					+ "</errorMessage>"
					+ "</response>";

			assertEquals(expected, aHttpServletResponse.getContentAsString());

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
    @Ignore
	public void testGET_FILTERED_NEWS() throws Exception {
		try {
			String userName = "zzz@z.com";
			String timestamp = "2011_12_26_07_04_23";
			String apiVersion = "3.6";
			String communityName = "o2";
			String appVersion = "CNBETA";
			String communityUrl="o2";

			String deviceType = UserRegInfo.DeviceType.ANDROID;

			String ipAddress = "2.24.0.1";

			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/" + apiVersion + "/SIGN_UP_DEVICE");
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.setRemoteAddr(ipAddress);
			httpServletRequest.setPathInfo("/" + apiVersion + "/SIGN_UP_DEVICE");

			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("DEVICE_UID", userName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);
			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

			MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
			dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

			assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());

			String contentAsString = mockHttpServletResponse.getContentAsString();
			String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
			String userToken = Utils.createTimestampToken(storedToken, timestamp);

			String requestURI = "/"+communityUrl+"/"+apiVersion+"/GET_NEWS";
			
			httpServletRequest = new MockHttpServletRequest(
					"POST", requestURI);
			httpServletRequest.setPathInfo(requestURI);

			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.addHeader("Content-Length", "0");
			httpServletRequest.setRemoteAddr("2.24.0.1");

			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);
			httpServletRequest.addParameter("USER_NAME", userName);
			httpServletRequest.addParameter("USER_TOKEN", userToken);
			httpServletRequest.addParameter("TIMESTAMP", timestamp);
			httpServletRequest.addParameter("DEVICE_UID", userName);
			
			mockHttpServletResponse = new MockHttpServletResponse();

			dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

			assertEquals(200, mockHttpServletResponse.getStatus());
			
			contentAsString = mockHttpServletResponse.getContentAsString();
			
		    String rememberMeToken= contentAsString.substring(contentAsString.indexOf("<rememberMeToken>")+"<rememberMeToken>".length(), contentAsString.indexOf("</rememberMeToken>"));
			
		    String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><user><activation>REGISTERED</activation><chartItems>21</chartItems><chartTimestamp>1321452650</chartTimestamp><deviceType>ANDROID</deviceType><deviceUID></deviceUID><displayName></displayName><drmType>PLAYS</drmType><drmValue>100</drmValue><freeTrial>false</freeTrial><fullyRegistred>true</fullyRegistred><graceCreditSeconds>0</graceCreditSeconds><hasOffers>false</hasOffers><hasPotentialPromoCodePromotion>false</hasPotentialPromoCodePromotion><newsItems>10</newsItems><newsTimestamp>1317300123</newsTimestamp><nextSubPaymentSeconds>0</nextSubPaymentSeconds><operator>1</operator><paymentEnabled>false</paymentEnabled><paymentStatus>NULL</paymentStatus><paymentType>UNKNOWN</paymentType><phoneNumber></phoneNumber><promotedDevice>false</promotedDevice><promotedWeeks>0</promotedWeeks><rememberMeToken>"+rememberMeToken+"</rememberMeToken><status>LIMITED</status><subBalance>0</subBalance><timeOfMovingToLimitedStatusSeconds>0</timeOfMovingToLimitedStatusSeconds><userName>zzz@z.com</userName><userToken>"+storedToken+"</userToken><oAuthProvider>NONE</oAuthProvider></user><news><item><body>Blue Ivy Carter, new daughter of Beyonce and JayZ, is already making chart history! Daddy Z features cute cries from little Princess B on his new track, Glory, making her the youngest person ever to appear in the Billboard chart!</body><detail>Blue Ivy Carter, new daughter of Beyonce and JayZ, is already making chart history! Daddy Z features cute cries from little Princess B on his new track, Glory, making her the youngest person ever to appear in the Billboard chart!</detail><i>1</i><id>11</id><messageType>NEWS</messageType><position>1</position><timestampMilis>1315686788000</timestampMilis></item><item><body>The Wanted would love to match the drama and musicality of the Take That shows on their upcoming tour. The boys start a 10 date US Tour on Jan 17th and then head back to the UK for their 1st show on February 15th at the Capital FM Arena in Nottingham.</body><detail>http://google.com.ua</detail><i>92</i><id>92</id><messageType>AD</messageType><position>2</position><timestampMilis>0</timestampMilis></item><item><body>The Wanted would love to match the drama and musicality of the Take That shows on their upcoming tour. The boys start a 10 date US Tour on Jan 17th and then head back to the UK for their 1st show on February 15th at the Capital FM Arena in Nottingham.</body><detail>The Wanted would love to match the drama and musicality of the Take That shows on their upcoming tour. The boys start a 10 date US Tour on Jan 17th and then head back to the UK for their 1st show on February 15th at the Capital FM Arena in Nottingham.</detail><i>12</i><id>12</id><messageFrequence>DAILY</messageFrequence><messageType>POPUP</messageType><position>2</position><timestampMilis>1315686788000</timestampMilis></item><item><actionButtonText>action button text 2</actionButtonText><actionType>OFFICIAL_TOP_40_PLAYLIST</actionType><body>The Wanted would love to match the drama and musicality of the Take That shows on their upcoming tour. The boys start a 10 date US Tour on Jan 17th and then head back to the UK for their 1st show on February 15th at the Capital FM Arena in Nottingham.</body><detail>rich popup title2</detail><i>102</i><id>102</id><messageFrequence>ONCE_AFTER_1ST_TRACK_DOWNLOAD</messageFrequence><messageType>RICH_POPUP</messageType><position>2</position><timestampMilis>0</timestampMilis></item><item><actionButtonText>action button text 3</actionButtonText><actionType>JUST_IN_PLAYLIST</actionType><body>The NOW! Top 40 Chart delivers all the hits, all the time, straight to your mobile! Subscribed already? Congrats! If not, please get your groove on to keep the hits coming! Click in app or via your email! No email? Check your junk folder! Go for it!</body><detail>rich popup title3</detail><i>103</i><id>103</id><messageFrequence>WEEKLY</messageFrequence><messageType>RICH_POPUP</messageType><position>3</position><timestampMilis>0</timestampMilis></item><item><actionButtonText>action button text 4</actionButtonText><actionType>OUR_PLAYLIST</actionType><body>Flipping back to music after producing new movie W.E., Madonna revealed that her 12th album will be called MDNA and will more than likely be released in March. The first single Gimme All Your Luvin will feature Nicki Minaj and MIA.</body><detail>rich popup title4</detail><i>104</i><id>104</id><messageFrequence>DAILY</messageFrequence><messageType>RICH_POPUP</messageType><position>4</position><timestampMilis>0</timestampMilis></item><item><body>Flipping back to music after producing new movie W.E., Madonna revealed that her 12th album will be called MDNA and will more than likely be released in March. The first single Gimme All Your Luvin will feature Nicki Minaj and MIA.</body><detail>Flipping back to music after producing new movie W.E., Madonna revealed that her 12th album will be called MDNA and will more than likely be released in March. The first single Gimme All Your Luvin will feature Nicki Minaj and MIA.</detail><i>4</i><id>14</id><messageType>NEWS</messageType><position>4</position><timestampMilis>1315686788000</timestampMilis></item><item><actionButtonText>action button text 5</actionButtonText><actionType>THE_4TH_PLAYLIST</actionType><body>Grammy producers are keen for Adele to open the legendary awards ceremony in February but will have to wait for an answer as the singer is still recovering from throat surgery. The Someone Like You singer has been nominated for six awards.</body><detail>rich popup title5</detail><i>105</i><id>105</id><messageFrequence>ONCE</messageFrequence><messageType>RICH_POPUP</messageType><position>5</position><timestampMilis>0</timestampMilis></item><item><action>3XLS70CD</action><actionButtonText>action button text 6</actionButtonText><actionType>A_SPECIFIC_TRACK</actionType><body>MasterCard and the Brits have launched an amazing competition in which fans can duet with either JLS, Emeli Sande or Labrinth. The winner will get VIP tickets to the awards show itself AND appear in an advert with their favourite popstar!</body><detail>rich popup title6</detail><i>106</i><id>106</id><messageFrequence>ONCE_AFTER_1ST_TRACK_DOWNLOAD</messageFrequence><messageType>RICH_POPUP</messageType><position>6</position><timestampMilis>0</timestampMilis></item><item><body>Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!</body><detail>Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!</detail><i>7</i><id>17</id><messageType>NEWS</messageType><position>7</position><timestampMilis>1315686788000</timestampMilis></item><item><actionButtonText>action button text 7</actionButtonText><actionType>THE_NEWS_LIST</actionType><body>Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!</body><detail>rich popup title7</detail><i>107</i><id>107</id><messageFrequence>DAILY</messageFrequence><messageType>RICH_POPUP</messageType><position>7</position><timestampMilis>0</timestampMilis></item><item><body>Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!</body><detail>4XLS70CD</detail><i>97</i><id>97</id><messageType>AD</messageType><position>7</position><timestampMilis>0</timestampMilis></item><item><action>999</action><actionButtonText>action button text 8</actionButtonText><actionType>A_SPECIFIC_NEWS_STORY</actionType><body>Lady Gaga has apparently taken up darts! The Born This Way singer recently accompanied her Vampire Diaries actor boyfriend, Taylor Kinney, to a pub game and apparently she is hooked! Taylor partnered Gaga as her love interest in her video You and I.</body><detail>rich popup title8</detail><i>108</i><id>108</id><messageType>RICH_POPUP</messageType><position>8</position><timestampMilis>0</timestampMilis></item><item><actionButtonText>action button text 9</actionButtonText><actionType>THE_ACCOUNT_SCREEN</actionType><body>With Little Mix winning X Factor, an original Sugababes reunion and a rumoured new 10th anniversary Girls Aloud album, 2012 looks like the year of the girl band! Wonder what the Spice Girls are up to?</body><detail>rich popup title9</detail><i>109</i><id>109</id><messageFrequence>ONCE_AFTER_1ST_TRACK_DOWNLOAD</messageFrequence><messageType>RICH_POPUP</messageType><position>9</position><timestampMilis>0</timestampMilis></item><item><body>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</body><detail>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</detail><i>10</i><id>20</id><messageType>NEWS</messageType><position>10</position><timestampMilis>1315686788000</timestampMilis></item><item><action>http://i.ua</action><actionButtonText>action button text 10</actionButtonText><actionType>EXTERNAL_URL</actionType><body>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</body><detail>rich popup title10</detail><i>110</i><id>110</id><messageType>RICH_POPUP</messageType><position>10</position><timestampMilis>0</timestampMilis></item><item><body>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</body><detail>6XLS70CD</detail><i>100</i><id>100</id><messageType>AD</messageType><position>10</position><timestampMilis>0</timestampMilis></item><item><action>http://i.ua</action><actionButtonText>action button text 11</actionButtonText><actionType>MOBILE_WEB_PORTAL</actionType><body>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</body><detail>rich popup title11</detail><i>111</i><id>111</id><messageType>RICH_POPUP</messageType><position>11</position><timestampMilis>0</timestampMilis></item></news></response>";
			
			class MessageElementQualifier implements ElementQualifier {

				private static final String ID = "id";

				@Override
				public boolean qualifyForComparison(Element expectedElement, Element actualElement) {

					final String expectedNodeName = expectedElement.getNodeName();
					final String actualNodeName = actualElement.getNodeName();
					final Node expectedParentNode = expectedElement.getParentNode();
					final Node actualParentNode = actualElement.getParentNode();

					boolean isComparable = false;
					if (expectedNodeName.equals("item") && actualNodeName.equals("item")) {

						if (actualParentNode != null && actualParentNode.getNodeName().equals("news") && actualParentNode.getParentNode() != null
								&& actualParentNode.getParentNode().getNodeName().equals("response")) {

							NodeList actualNodeList = actualElement.getChildNodes();
							NodeList expectedNodeList = expectedElement.getChildNodes();
							if (actualNodeList.getLength() == expectedNodeList.getLength()) {

								Map<String, String> expectedMap = new HashMap<String, String>();
								Map<String, String> actualMap = new HashMap<String, String>();
								for (int i = 0; i < actualNodeList.getLength(); i++) {
									populate(actualNodeList, actualMap, i);

									populate(expectedNodeList, expectedMap, i);
								}
								
								String actualId = actualMap.get(ID);
//								String actualMessageType = actualMap.get("messageType");
//								String actualPosition = actualMap.get("position");
//								if (expectedMap.get("messageType").equals(actualMessageType) && expectedMap.get("position").equals(actualPosition)) {
//									isComparable = true;
//								}
								if (expectedMap.get(ID).equals(actualId)) {
									isComparable = true;
								}

							}
						}

					}else if (expectedNodeName.equals(actualNodeName)){
						if(expectedParentNode.getNodeName().equals(actualParentNode.getNodeName())){
							isComparable = true;
						}
					}
					return isComparable;
				}

				private Map<String, String> populate(NodeList nodeList, Map<String, String> map, int i) {
					Node currentNode = nodeList.item(i);
					populate(currentNode, "messageType", map );
					populate(currentNode, "position", map );
					populate(currentNode, ID, map );
					return map;
				}	
				
				private Map<String, String> populate(Node node, String nodeName, Map<String, String> map){
					if (node.getNodeName().equals(nodeName)){
						map.put(nodeName, node.getTextContent()); 
					}
					return map;
				}
			}
			
			Diff diff = new Diff(expected, contentAsString);
			diff.overrideElementQualifier(new MessageElementQualifier());
			
			XMLAssert.assertXMLEqual(diff, true);
			
			requestURI = "/"+communityUrl+"/3.5/GET_NEWS";
			
			httpServletRequest = new MockHttpServletRequest(
					"POST", requestURI);
			httpServletRequest.setPathInfo(requestURI);

			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.addHeader("Content-Length", "0");
			httpServletRequest.setRemoteAddr("2.24.0.1");

			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);
			httpServletRequest.addParameter("USER_NAME", userName);
			httpServletRequest.addParameter("USER_TOKEN", userToken);
			httpServletRequest.addParameter("TIMESTAMP", timestamp);
			httpServletRequest.addParameter("DEVICE_UID", userName);
			
			mockHttpServletResponse = new MockHttpServletResponse();

			dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

			assertEquals(200, mockHttpServletResponse.getStatus());
			
			contentAsString = mockHttpServletResponse.getContentAsString();
			
		    rememberMeToken= contentAsString.substring(contentAsString.indexOf("<rememberMeToken>")+"<rememberMeToken>".length(), contentAsString.indexOf("</rememberMeToken>"));
			
		    expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><user><activation>REGISTERED</activation><chartItems>21</chartItems><chartTimestamp>1321452650</chartTimestamp><deviceType>ANDROID</deviceType><deviceUID></deviceUID><displayName></displayName><drmType>PLAYS</drmType><drmValue>100</drmValue><freeTrial>false</freeTrial><fullyRegistred>true</fullyRegistred><graceCreditSeconds>0</graceCreditSeconds><hasOffers>false</hasOffers><hasPotentialPromoCodePromotion>false</hasPotentialPromoCodePromotion><newsItems>10</newsItems><newsTimestamp>1317300123</newsTimestamp><nextSubPaymentSeconds>0</nextSubPaymentSeconds><operator>1</operator><paymentEnabled>false</paymentEnabled><paymentStatus>NULL</paymentStatus><paymentType>UNKNOWN</paymentType><phoneNumber></phoneNumber><promotedDevice>false</promotedDevice><promotedWeeks>0</promotedWeeks><rememberMeToken>"+rememberMeToken+"</rememberMeToken><status>LIMITED</status><subBalance>0</subBalance><timeOfMovingToLimitedStatusSeconds>0</timeOfMovingToLimitedStatusSeconds><userName>zzz@z.com</userName><userToken>"+storedToken+"</userToken><oAuthProvider>NONE</oAuthProvider></user><news><item><body>Blue Ivy Carter, new daughter of Beyonce and JayZ, is already making chart history! Daddy Z features cute cries from little Princess B on his new track, Glory, making her the youngest person ever to appear in the Billboard chart!</body><detail>Blue Ivy Carter, new daughter of Beyonce and JayZ, is already making chart history! Daddy Z features cute cries from little Princess B on his new track, Glory, making her the youngest person ever to appear in the Billboard chart!</detail><i>1</i><id>11</id><messageType>NEWS</messageType><position>1</position><timestampMilis>1315686788000</timestampMilis></item><item><body>The Wanted would love to match the drama and musicality of the Take That shows on their upcoming tour. The boys start a 10 date US Tour on Jan 17th and then head back to the UK for their 1st show on February 15th at the Capital FM Arena in Nottingham.</body><detail>The Wanted would love to match the drama and musicality of the Take That shows on their upcoming tour. The boys start a 10 date US Tour on Jan 17th and then head back to the UK for their 1st show on February 15th at the Capital FM Arena in Nottingham.</detail><i>12</i><id>12</id><messageFrequence>DAILY</messageFrequence><messageType>POPUP</messageType><position>2</position><timestampMilis>1315686788000</timestampMilis></item><item><actionButtonText>action button text 2</actionButtonText><actionType>OFFICIAL_TOP_40_PLAYLIST</actionType><body>The Wanted would love to match the drama and musicality of the Take That shows on their upcoming tour. The boys start a 10 date US Tour on Jan 17th and then head back to the UK for their 1st show on February 15th at the Capital FM Arena in Nottingham.</body><detail>rich popup title2</detail><i>102</i><id>102</id><messageFrequence>ONCE_AFTER_1ST_TRACK_DOWNLOAD</messageFrequence><messageType>RICH_POPUP</messageType><position>2</position><timestampMilis>0</timestampMilis></item><item><actionButtonText>action button text 3</actionButtonText><actionType>JUST_IN_PLAYLIST</actionType><body>The NOW! Top 40 Chart delivers all the hits, all the time, straight to your mobile! Subscribed already? Congrats! If not, please get your groove on to keep the hits coming! Click in app or via your email! No email? Check your junk folder! Go for it!</body><detail>rich popup title3</detail><i>103</i><id>103</id><messageFrequence>WEEKLY</messageFrequence><messageType>RICH_POPUP</messageType><position>3</position><timestampMilis>0</timestampMilis></item><item><body>Flipping back to music after producing new movie W.E., Madonna revealed that her 12th album will be called MDNA and will more than likely be released in March. The first single Gimme All Your Luvin will feature Nicki Minaj and MIA.</body><detail>Flipping back to music after producing new movie W.E., Madonna revealed that her 12th album will be called MDNA and will more than likely be released in March. The first single Gimme All Your Luvin will feature Nicki Minaj and MIA.</detail><i>4</i><id>14</id><messageType>NEWS</messageType><position>4</position><timestampMilis>1315686788000</timestampMilis></item><item><actionButtonText>action button text 4</actionButtonText><actionType>OUR_PLAYLIST</actionType><body>Flipping back to music after producing new movie W.E., Madonna revealed that her 12th album will be called MDNA and will more than likely be released in March. The first single Gimme All Your Luvin will feature Nicki Minaj and MIA.</body><detail>rich popup title4</detail><i>104</i><id>104</id><messageFrequence>DAILY</messageFrequence><messageType>RICH_POPUP</messageType><position>4</position><timestampMilis>0</timestampMilis></item><item><actionButtonText>action button text 5</actionButtonText><actionType>THE_4TH_PLAYLIST</actionType><body>Grammy producers are keen for Adele to open the legendary awards ceremony in February but will have to wait for an answer as the singer is still recovering from throat surgery. The Someone Like You singer has been nominated for six awards.</body><detail>rich popup title5</detail><i>105</i><id>105</id><messageFrequence>ONCE</messageFrequence><messageType>RICH_POPUP</messageType><position>5</position><timestampMilis>0</timestampMilis></item><item><action>3XLS70CD</action><actionButtonText>action button text 6</actionButtonText><actionType>A_SPECIFIC_TRACK</actionType><body>MasterCard and the Brits have launched an amazing competition in which fans can duet with either JLS, Emeli Sande or Labrinth. The winner will get VIP tickets to the awards show itself AND appear in an advert with their favourite popstar!</body><detail>rich popup title6</detail><i>106</i><id>106</id><messageFrequence>ONCE_AFTER_1ST_TRACK_DOWNLOAD</messageFrequence><messageType>RICH_POPUP</messageType><position>6</position><timestampMilis>0</timestampMilis></item><item><body>Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!</body><detail>Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!</detail><i>7</i><id>17</id><messageType>NEWS</messageType><position>7</position><timestampMilis>1315686788000</timestampMilis></item><item><actionButtonText>action button text 7</actionButtonText><actionType>THE_NEWS_LIST</actionType><body>Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!</body><detail>rich popup title7</detail><i>107</i><id>107</id><messageFrequence>DAILY</messageFrequence><messageType>RICH_POPUP</messageType><position>7</position><timestampMilis>0</timestampMilis></item><item><action>999</action><actionButtonText>action button text 8</actionButtonText><actionType>A_SPECIFIC_NEWS_STORY</actionType><body>Lady Gaga has apparently taken up darts! The Born This Way singer recently accompanied her Vampire Diaries actor boyfriend, Taylor Kinney, to a pub game and apparently she is hooked! Taylor partnered Gaga as her love interest in her video You and I.</body><detail>rich popup title8</detail><i>108</i><id>108</id><messageType>RICH_POPUP</messageType><position>8</position><timestampMilis>0</timestampMilis></item><item><actionButtonText>action button text 9</actionButtonText><actionType>THE_ACCOUNT_SCREEN</actionType><body>With Little Mix winning X Factor, an original Sugababes reunion and a rumoured new 10th anniversary Girls Aloud album, 2012 looks like the year of the girl band! Wonder what the Spice Girls are up to?</body><detail>rich popup title9</detail><i>109</i><id>109</id><messageFrequence>ONCE_AFTER_1ST_TRACK_DOWNLOAD</messageFrequence><messageType>RICH_POPUP</messageType><position>9</position><timestampMilis>0</timestampMilis></item><item><body>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</body><detail>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</detail><i>10</i><id>20</id><messageType>NEWS</messageType><position>10</position><timestampMilis>1315686788000</timestampMilis></item><item><action>http://i.ua</action><actionButtonText>action button text 10</actionButtonText><actionType>EXTERNAL_URL</actionType><body>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</body><detail>rich popup title10</detail><i>110</i><id>110</id><messageType>RICH_POPUP</messageType><position>10</position><timestampMilis>0</timestampMilis></item><item><action>http://i.ua</action><actionButtonText>action button text 11</actionButtonText><actionType>MOBILE_WEB_PORTAL</actionType><body>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</body><detail>rich popup title11</detail><i>111</i><id>111</id><messageType>RICH_POPUP</messageType><position>11</position><timestampMilis>0</timestampMilis></item></news></response>";
		    
		    diff = new Diff(expected, contentAsString);
			diff.overrideElementQualifier(new MessageElementQualifier());
			
			XMLAssert.assertXMLEqual(diff, true);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	@Ignore
	public void testFB_ACC_CHECK() throws Exception {
		try {

			String apiVersion = "V1.2";
			String communityName = "Now Music";
			String appVersion = "CNBETA";
			String deviceUID = "deviceUID";
			String facebookToken = "facebookToken";
			String iphoneToken = "iphoneToken";

			class MockFacebookService extends FacebookService {
				public UserCredentions getUserCredentions(String facebookToken) {
					UserCredentions userCredentions = new UserCredentions();
					userCredentions.setBirthday("birthday");
					userCredentions.setEmail("x");
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

			MockFacebookService mockFacebookService = new MockFacebookService();
			entityController.setFacebookService(mockFacebookService);

			MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(
					"POST", "/FB_ACC_CHECK");
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.addHeader("Content-Length", "0");
			httpServletRequest.setRemoteAddr("2.24.0.1");

			httpServletRequest.addParameter("APP_VERSION", appVersion);
			httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
			httpServletRequest.addParameter("API_VERSION", apiVersion);
			httpServletRequest.addParameter("DEVICE_UID", deviceUID);
			httpServletRequest.addParameter("FB_TOKEN", facebookToken);
			httpServletRequest.addParameter("IPHONE_TOKEN", iphoneToken);

			dispatcherServlet.service(httpServletRequest, aHttpServletResponse);

			assertEquals(200, aHttpServletResponse.getStatus());

			String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><user><chartItems>21</chartItems><chartTimestamp>1321452650</chartTimestamp><deviceType>NONE</deviceType><deviceUID>deviceUID</deviceUID><displayName>first_name</displayName><drmType>PLAYS</drmType><drmValue>100</drmValue><newsItems>10</newsItems><newsTimestamp>1317300123</newsTimestamp><operator>1</operator><paymentEnabled>false</paymentEnabled><paymentStatus>NULL</paymentStatus><paymentType>UNKNOWN</paymentType><phoneNumber></phoneNumber><status>SUBSCRIBED</status><subBalance>0</subBalance><userName>x</userName><userToken>c2d623b10777787765ea9fe706aa975e</userToken></user></response>";
			assertEquals(expected, aHttpServletResponse.getContentAsString());

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
    @Ignore
	public void testExceptionHandler() throws Exception {
		String password = "zzz@z.com";
		String userName = "zzz@z.com";
		String timestamp = "2011_12_26_07_04_23";
		String apiVersion = "V1.2";
		String communityName = "Now Music";
		String appVersion = "CNBETA";

		String deviceString = "Device 1";
		String deviceType = UserRegInfo.DeviceType.ANDROID;

		int operator = 1;

		String storedToken = Utils.createStoredToken(userName, password);
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

		UserRegInfo userRegInfo = new UserRegInfo();
		userRegInfo.setEmail(userName);
		userRegInfo.setStoredToken(storedToken);
		userRegInfo.setAppVersion(appVersion);
		userRegInfo.setDeviceType(deviceType);
		userRegInfo.setCommunityName(communityName);
		userRegInfo.setDeviceString(deviceString);
		userRegInfo.setDisplayName("Nigel");
		userRegInfo.setPhoneNumber("07580381128");
		userRegInfo.setOperator(1);

		String aBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<userRegInfo>"
				+ "<address>33333</address>"
				+ "<appVersion>" + appVersion + "</appVersion>"
				+ "<apiVersion>" + apiVersion + "</apiVersion>"
				+ "<deviceType>" + deviceType + "</deviceType>"
				+ "<deviceString>" + deviceString + "</deviceString>"
				+ "<countryFullName>Great Britain</countryFullName>"
				+ "<city>33</city>"
				+ "<firstName>33</firstName>"
				+ "<lastName>33</lastName>"
				+ "<email>" + userName + "</email>"
				+ "<communityName>" + communityName + "</communityName>"
				+ "<displayName>displayName</displayName>"
				+ "<postCode>null</postCode>"
				+ "<paymentType>" + UserRegInfo.PaymentType.UNKNOWN + "</paymentType>"
				+ "<storedToken>" + storedToken + "</storedToken>"
				+ "<promotionCode>promo</promotionCode>"
				+ "</userRegInfo>";

		MockHttpServletResponse mockHttpServletResponse = registerUser(aBody, "195.214.195.105");
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><errorMessage><displayMessage>Sorry, your country isn't supported yet.</displayMessage><message>Sorry, your country isn't supported yet.</message></errorMessage></response>",
				mockHttpServletResponse.getContentAsString());

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), mockHttpServletResponse.getStatus());

		mockHttpServletResponse = registerUser(aBody, "2.24.0.1");

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());

		aBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<userRegInfo>"
				+ "<phoneNumber>+3(809)-130-08-666</phoneNumber>"
				+ "<operator>" + operator + "</operator>"
				+ "<paymentType>" + UserRegInfo.PaymentType.PREMIUM_USER + "</paymentType>"
				+ "</userRegInfo>";

		mockHttpServletResponse = updatePaymentDetails(userName, timestamp, apiVersion, communityName, appVersion, userToken, aBody);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), mockHttpServletResponse.getStatus());
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><errorMessage><displayMessage>The property 'phoneNumber' in UserRegInfoServer object contains invalid phone number value: +3(809)-130-08-666</displayMessage><message>The property 'phoneNumber' in UserRegInfoServer object contains invalid phone number value: +3(809)-130-08-666</message></errorMessage></response>",
				mockHttpServletResponse.getContentAsString());

		aBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<userRegInfo>"
				+ "<operator>" + operator + "</operator>"
				+ "<paymentType>" + UserRegInfo.PaymentType.PREMIUM_USER + "</paymentType>"
				+ "</userRegInfo>";

		mockHttpServletResponse = updatePaymentDetails(userName, timestamp, apiVersion, communityName, appVersion, userToken, aBody);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), mockHttpServletResponse.getStatus());
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><errorMessage><displayMessage>The parameter phoneNumber is null</displayMessage><message>The parameter phoneNumber is null</message></errorMessage></response>",
				mockHttpServletResponse.getContentAsString());

		String wrongUserToken = "wrongUserToken";

		mockHttpServletResponse = acc_check(timestamp, wrongUserToken, userName,
				apiVersion, communityName, appVersion, null, null, null, null);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), mockHttpServletResponse
				.getStatus());

		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><errorMessage><displayMessage>user login/pass check failed for [zzz@z.com] username and community [Now Music]</displayMessage><errorCode>12</errorCode><message>user login/pass check failed for [zzz@z.com] username and community [Now Music]</message></errorMessage></response>",
				mockHttpServletResponse.getContentAsString());
	}

	@Test
    @Ignore
	public void testSIGN_UP() throws Exception {
		String password = "zzz@z.com";
		String userName = "zzz@z.com";
		String timestamp = "2011_12_26_07_04_23";
		String apiVersion = "V1.2";
		String communityName = "Now Music";
		String appVersion = "CNBETA";
		Boolean termsConfirmed = true;

		MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/SIGN_UP");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/SIGN_UP");

		httpServletRequest.addParameter("communityName", communityName);
		httpServletRequest.addParameter("email", userName);
		httpServletRequest.addParameter("confirmPassword", password);
		httpServletRequest.addParameter("password", password);
		httpServletRequest.addParameter("termsConfirmed", termsConfirmed.toString());
		httpServletRequest.addParameter("apiVersion", apiVersion);
		httpServletRequest.addParameter("appVersion", appVersion);

		dispatcherServlet.service(httpServletRequest, aHttpServletResponse);
		assertEquals(HttpStatus.OK.value(), aHttpServletResponse.getStatus());
	}

	@Test
    @Ignore
	public void testACC_CHECK() throws Exception {
		try {
			String password = "zzz@z.com";
			String userName = "zzz@z.com";
			String timestamp = "2011_12_26_07_04_23";
			String apiVersion = "V1.2";
			String communityName = "Now Music";
			String appVersion = "CNBETA";

			String deviceString = "Device 1";
			String deviceType = UserRegInfo.DeviceType.IOS;

			String storedToken = Utils.createStoredToken(userName, password);
			String userToken = Utils.createTimestampToken(storedToken, timestamp);

			String pushNotificationToken = "6e3be508f214c9b749b4dd8191e68acf8c897444";

			UserRegInfo userRegInfo = new UserRegInfo();
			userRegInfo.setEmail(userName);
			userRegInfo.setStoredToken(storedToken);
			userRegInfo.setAppVersion(appVersion);
			userRegInfo.setDeviceType(deviceType);
			userRegInfo.setCommunityName(communityName);
			userRegInfo.setDeviceString(deviceString);
			userRegInfo.setDisplayName("Nigel");
			userRegInfo.setPhoneNumber("07580381128");
			userRegInfo.setOperator(1);

			String aBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
					+ "<userRegInfo>"
					+ "<address>33333</address>"
					+ "<appVersion>" + appVersion + "</appVersion>"
					+ "<apiVersion>" + apiVersion + "</apiVersion>"
					+ "<deviceType>" + deviceType + "</deviceType>"
					+ "<deviceString>" + deviceString + "</deviceString>"
					+ "<countryFullName>Great Britain</countryFullName>"
					+ "<city>33</city>"
					+ "<firstName>33</firstName>"
					+ "<lastName>33</lastName>"
					+ "<email>" + userName + "</email>"
					+ "<communityName>" + communityName + "</communityName>"
					+ "<displayName>displayName</displayName>"
					+ "<postCode>null</postCode>"
					+ "<paymentType>" + UserRegInfo.PaymentType.UNKNOWN + "</paymentType>"
					+ "<storedToken>" + storedToken + "</storedToken>"
					+ "<promotionCode>promo8</promotionCode>"
					+ "</userRegInfo>";

			MockHttpServletResponse mockHttpServletResponse = registerUser(aBody, "2.24.0.1");

			assertEquals(200, mockHttpServletResponse.getStatus());

			mockHttpServletResponse = acc_check(timestamp, userToken, userName, apiVersion, communityName, appVersion, deviceType, pushNotificationToken, null, null);
			assertEquals(200, mockHttpServletResponse.getStatus());

			mockHttpServletResponse = acc_check(timestamp, userToken, userName, apiVersion, communityName, appVersion, deviceType, pushNotificationToken, null, null);
			assertEquals(200, mockHttpServletResponse.getStatus());

			mockHttpServletResponse = acc_check(timestamp, userToken, userName, apiVersion, communityName, appVersion, UserRegInfo.DeviceType.ANDROID, pushNotificationToken, null, null);
			assertEquals(200, mockHttpServletResponse.getStatus());

			mockHttpServletResponse = acc_check(timestamp, userToken, userName, apiVersion, communityName, appVersion, UserRegInfo.DeviceType.ANDROID, pushNotificationToken, null, null);
			assertEquals(200, mockHttpServletResponse.getStatus());

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
    @Ignore
	public void testUPDATE_USER_DETAILS() throws Exception {
		String password = "zzz@z.com";
		String userName = "zzz@z.com";
		String apiVersion = "V1.2";
		String communityName = "Now Music";
		String appVersion = "CNBETA";

		String newUserName = "ggg@ggg.com";
		String newDeviceUID = "aaaa@dddd.com";

		String deviceType = UserRegInfo.DeviceType.ANDROID;

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/SIGN_UP_DEVICE");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/SIGN_UP_DEVICE");

		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("DEVICE_UID", userName);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		String contentAsString = mockHttpServletResponse.getContentAsString();

		String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));

		httpServletRequest = new MockHttpServletRequest("POST", "/UPDATE_USER_DETAILS");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/UPDATE_USER_DETAILS");

		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("USER_NAME", newUserName);
		httpServletRequest.addParameter("DEVICE_UID", userName);
		httpServletRequest.addParameter("STORED_TOKEN", storedToken);
		httpServletRequest.addParameter("NEW_PASSWORD", password);
		httpServletRequest.addParameter("NEW_CONFIRM_PASSWORD", password);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("APP_VERSION", appVersion);

		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);
		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		
		httpServletRequest = new MockHttpServletRequest("POST", "/SIGN_UP_DEVICE");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/SIGN_UP_DEVICE");

		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("DEVICE_UID", newDeviceUID);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

		mockHttpServletResponse = new MockHttpServletResponse();
		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		contentAsString = mockHttpServletResponse.getContentAsString();

		storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
		
		httpServletRequest = new MockHttpServletRequest("POST", "/UPDATE_USER_DETAILS");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/UPDATE_USER_DETAILS");

		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("USER_NAME", newUserName);
		httpServletRequest.addParameter("DEVICE_UID", newDeviceUID);
		httpServletRequest.addParameter("STORED_TOKEN", storedToken);
		httpServletRequest.addParameter("NEW_PASSWORD", password);
		httpServletRequest.addParameter("NEW_CONFIRM_PASSWORD", password);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("APP_VERSION", appVersion);

		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);
		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
	}

	@Test
	public void testAPPLY_INITIAL_PROMOTION() throws Exception {
		String userName = "zzz@z.com";
		String apiVersion = "3.5";
		String communityName = "Now Music";
		String appVersion = "CNBETA";
		String timestamp = "2011_12_26_07_04_23";
		String deviceType = UserRegInfo.DeviceType.ANDROID;
		String ipAddress = "2.24.0.1";

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/" + apiVersion + "/SIGN_UP_DEVICE");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr(ipAddress);
		httpServletRequest.setPathInfo("/" + apiVersion + "/SIGN_UP_DEVICE");

		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("DEVICE_UID", userName);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		final String contentAsString = mockHttpServletResponse.getContentAsString();
		String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());

		httpServletRequest = new MockHttpServletRequest("POST", "/" + apiVersion + "/APPLY_INIT_PROMO");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr(ipAddress);
		httpServletRequest.setPathInfo("/" + apiVersion + "/APPLY_INIT_PROMO");

		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("USER_NAME", userName);
		httpServletRequest.addParameter("USER_TOKEN", userToken);
		httpServletRequest.addParameter("TIMESTAMP", timestamp);

		mockHttpServletResponse = new MockHttpServletResponse();
		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
	}
	
	@Test
	public void testSING_UP_DEVICE_O2() throws Exception {
		String userName = "zzz@z.com";
		String apiVersion = "V3.6";
		String communityName = "Now Music";
		String appVersion = "CNBETA";

		String deviceType = UserRegInfo.DeviceType.ANDROID;

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/O2/3.6/SIGN_UP_DEVICE");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/O2/3.6/SIGN_UP_DEVICE");

		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("DEVICE_UID", userName);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		String contentAsString = mockHttpServletResponse.getContentAsString();

		String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
		String activation = contentAsString.substring(contentAsString.indexOf("<activation>") + "<activation>".length(), contentAsString.indexOf("</activation>"));
		
		assertNotNull(storedToken);
		assertNotNull(activation);
		assertEquals("REGISTERED", activation);
	}
	
	@Test
	public void testPHONE_NUMBER_O2() throws Exception {
		String userName = "zzz@z.com";
		String apiVersion = "V3.6";
		String communityName = "o2";
		String appVersion = "CNBETA";
		String phone = "07870111111";
		String timestamp = "2011_12_26_07_04_23";
		String deviceType = UserRegInfo.DeviceType.ANDROID;

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/o2/3.6/SIGN_UP_DEVICE");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/o2/3.6/SIGN_UP_DEVICE");

		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("DEVICE_UID", userName);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		String contentAsString = mockHttpServletResponse.getContentAsString();
		String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

		httpServletRequest = new MockHttpServletRequest("POST", "/o2/3.6/PHONE_NUMBER");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/o2/3.6/PHONE_NUMBER");

		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("USER_NAME", userName);
		httpServletRequest.addParameter("USER_TOKEN", userToken);
		httpServletRequest.addParameter("TIMESTAMP", timestamp);
		httpServletRequest.addParameter("PHONE", phone);

		mockHttpServletResponse = new MockHttpServletResponse();
		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		contentAsString = mockHttpServletResponse.getContentAsString();

		assertNotNull(contentAsString);
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><phoneActivation><activation>ENTERED_NUMBER</activation><phoneNumber>+447870111111</phoneNumber><redeemServerUrl>https://identity.o2.co.uk</redeemServerUrl></phoneActivation></response>", contentAsString);
	}
	
	@Test
    @Ignore
	public void testPHONE_NUMBER_O2_InvalidNumber() throws Exception {
		String userName = "zzz@z.com";
		String apiVersion = "V3.6";
		String communityName = "o2";
		String appVersion = "CNBETA";
		String phone = "07870111111dddd";
		String timestamp = "2011_12_26_07_04_23";
		String deviceType = UserRegInfo.DeviceType.ANDROID;

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/o2/3.6/SIGN_UP_DEVICE");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/o2/3.6/SIGN_UP_DEVICE");

		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("DEVICE_UID", userName);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		String contentAsString = mockHttpServletResponse.getContentAsString();
		String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

		httpServletRequest = new MockHttpServletRequest("POST", "/o2/3.6/PHONE_NUMBER");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/o2/3.6/PHONE_NUMBER");

		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("USER_NAME", userName);
		httpServletRequest.addParameter("USER_TOKEN", userToken);
		httpServletRequest.addParameter("TIMESTAMP", timestamp);
		httpServletRequest.addParameter("PHONE", phone);

		mockHttpServletResponse = new MockHttpServletResponse();
		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		contentAsString = mockHttpServletResponse.getContentAsString();

		assertNotNull(contentAsString);
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><phoneActivation><activation>ENTERED_NUMBER</activation><phoneNumber>+447870111111</phoneNumber><redeemServerUrl>https://identity.o2.co.uk</redeemServerUrl></phoneActivation></response>", contentAsString);
	}
	
	@Test
    @Ignore
	public void testPHONE_NUMBER_O2_ResendSMS() throws Exception {
		String userName = "zzz@z.com";
		String apiVersion = "V3.6";
		String communityName = "o2";
		String appVersion = "CNBETA";
		String phone = "07870111111";
		String timestamp = "2011_12_26_07_04_23";
		String deviceType = UserRegInfo.DeviceType.ANDROID;

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/o2/3.6/SIGN_UP_DEVICE");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/o2/3.6/SIGN_UP_DEVICE");

		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("DEVICE_UID", userName);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		String contentAsString = mockHttpServletResponse.getContentAsString();
		String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

		httpServletRequest = new MockHttpServletRequest("POST", "/o2/3.6/PHONE_NUMBER");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/o2/3.6/PHONE_NUMBER");

		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("USER_NAME", userName);
		httpServletRequest.addParameter("USER_TOKEN", userToken);
		httpServletRequest.addParameter("TIMESTAMP", timestamp);
		httpServletRequest.addParameter("PHONE", phone);

		mockHttpServletResponse = new MockHttpServletResponse();
		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		contentAsString = mockHttpServletResponse.getContentAsString();

		assertNotNull(contentAsString);
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><phoneActivation><activation>ENTERED_NUMBER</activation><phoneNumber>+447870111111</phoneNumber></phoneActivation></response>", contentAsString);
		
		httpServletRequest = new MockHttpServletRequest("POST", "/O2/3.6/PHONE_NUMBER");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/O2/3.6/PHONE_NUMBER");

		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("USER_NAME", userName);
		httpServletRequest.addParameter("USER_TOKEN", userToken);
		httpServletRequest.addParameter("TIMESTAMP", timestamp);

		mockHttpServletResponse = new MockHttpServletResponse();
		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		contentAsString = mockHttpServletResponse.getContentAsString();

		assertNotNull(contentAsString);
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><phoneActivation><activation>ENTERED_NUMBER</activation><phoneNumber>+447870111111</phoneNumber><redeemServerUrl>https://identity.o2.co.uk</redeemServerUrl></phoneActivation></response>", contentAsString);
	}
	
	@Test
    @Ignore
	public void testACC_CHECK_with_TRANSACTION_RECEIPT_param() throws Exception {
		String userName = "+447111111114";
		String apiVersion = "V3.6";
		String communityName = "o2";
		String appVersion = "CNBETA";
		String timestamp = "2011_12_26_07_04_23";
		String base64EncodedAppStoreReceipt = "ewoJInNpZ25hdHVyZSIgPSAiQXNyQUNod2dETm1IYmIvSHU2UU5JbHZEUTY4TEE3dWIvQWlkY3huS2JSeTl1NTYyWWM4VHNtUGROQzcwcmo5KzBxOVE1UlBKYTdMK3lYb2ltU05TS0pCVDd2OVozVjgra2dVNkNwQmFPb1dyOW50TDhOdWZwdmxicUh5dUdjMm1vS1pOYXFsM2JFLzlRVWpkK1FKR2tPMVNmNVVWRDRpeDQ0MGlmMlMzd0p2K0FBQURWekNDQTFNd2dnSTdvQU1DQVFJQ0NHVVVrVTNaV0FTMU1BMEdDU3FHU0liM0RRRUJCUVVBTUg4eEN6QUpCZ05WQkFZVEFsVlRNUk13RVFZRFZRUUtEQXBCY0hCc1pTQkpibU11TVNZd0pBWURWUVFMREIxQmNIQnNaU0JEWlhKMGFXWnBZMkYwYVc5dUlFRjFkR2h2Y21sMGVURXpNREVHQTFVRUF3d3FRWEJ3YkdVZ2FWUjFibVZ6SUZOMGIzSmxJRU5sY25ScFptbGpZWFJwYjI0Z1FYVjBhRzl5YVhSNU1CNFhEVEE1TURZeE5USXlNRFUxTmxvWERURTBNRFl4TkRJeU1EVTFObG93WkRFak1DRUdBMVVFQXd3YVVIVnlZMmhoYzJWU1pXTmxhWEIwUTJWeWRHbG1hV05oZEdVeEd6QVpCZ05WQkFzTUVrRndjR3hsSUdsVWRXNWxjeUJUZEc5eVpURVRNQkVHQTFVRUNnd0tRWEJ3YkdVZ1NXNWpMakVMTUFrR0ExVUVCaE1DVlZNd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKQW9HQkFNclJqRjJjdDRJclNkaVRDaGFJMGc4cHd2L2NtSHM4cC9Sd1YvcnQvOTFYS1ZoTmw0WElCaW1LalFRTmZnSHNEczZ5anUrK0RyS0pFN3VLc3BoTWRkS1lmRkU1ckdYc0FkQkVqQndSSXhleFRldngzSExFRkdBdDFtb0t4NTA5ZGh4dGlJZERnSnYyWWFWczQ5QjB1SnZOZHk2U01xTk5MSHNETHpEUzlvWkhBZ01CQUFHamNqQndNQXdHQTFVZEV3RUIvd1FDTUFBd0h3WURWUjBqQkJnd0ZvQVVOaDNvNHAyQzBnRVl0VEpyRHRkREM1RllRem93RGdZRFZSMFBBUUgvQkFRREFnZUFNQjBHQTFVZERnUVdCQlNwZzRQeUdVakZQaEpYQ0JUTXphTittVjhrOVRBUUJnb3Foa2lHOTJOa0JnVUJCQUlGQURBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQUVhU2JQanRtTjRDL0lCM1FFcEszMlJ4YWNDRFhkVlhBZVZSZVM1RmFaeGMrdDg4cFFQOTNCaUF4dmRXLzNlVFNNR1k1RmJlQVlMM2V0cVA1Z204d3JGb2pYMGlreVZSU3RRKy9BUTBLRWp0cUIwN2tMczlRVWU4Y3pSOFVHZmRNMUV1bVYvVWd2RGQ0TndOWXhMUU1nNFdUUWZna1FRVnk4R1had1ZIZ2JFL1VDNlk3MDUzcEdYQms1MU5QTTN3b3hoZDNnU1JMdlhqK2xvSHNTdGNURXFlOXBCRHBtRzUrc2s0dHcrR0szR01lRU41LytlMVFUOW5wL0tsMW5qK2FCdzdDMHhzeTBiRm5hQWQxY1NTNnhkb3J5L0NVdk02Z3RLc21uT09kcVRlc2JwMGJzOHNuNldxczBDOWRnY3hSSHVPTVoydG04bnBMVW03YXJnT1N6UT09IjsKCSJwdXJjaGFzZS1pbmZvIiA9ICJld29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSaGRHVXRjSE4wSWlBOUlDSXlNREV6TFRBeUxURXlJREE1T2pVeU9qQTRJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dvSkluQjFjbU5vWVhObExXUmhkR1V0YlhNaUlEMGdJakV6TmpBMk9URTFNamd3TXpraU93b0pJblZ1YVhGMVpTMXBaR1Z1ZEdsbWFXVnlJaUE5SUNJelpHTTFOakUwTldaa1pqWmpOREU0WXpRNFlqSm1ZelZrTVRNd09HSTBOR1ZoTlRkaVltUmlJanNLQ1NKdmNtbG5hVzVoYkMxMGNtRnVjMkZqZEdsdmJpMXBaQ0lnUFNBaU1UQXdNREF3TURBMk5EYzFOVGsxT0NJN0Nna2laWGh3YVhKbGN5MWtZWFJsSWlBOUlDSXhNell3TmpreE56QTRNRE01SWpzS0NTSjBjbUZ1YzJGamRHbHZiaTFwWkNJZ1BTQWlNVEF3TURBd01EQTJORGMxTlRrMU9DSTdDZ2tpYjNKcFoybHVZV3d0Y0hWeVkyaGhjMlV0WkdGMFpTMXRjeUlnUFNBaU1UTTJNRFk1TVRVeU9ETTVOaUk3Q2draWQyVmlMVzl5WkdWeUxXeHBibVV0YVhSbGJTMXBaQ0lnUFNBaU1UQXdNREF3TURBeU5qWXpOVEl3TlNJN0Nna2lZblp5Y3lJZ1BTQWlNUzR3SWpzS0NTSmxlSEJwY21WekxXUmhkR1V0Wm05eWJXRjBkR1ZrTFhCemRDSWdQU0FpTWpBeE15MHdNaTB4TWlBd09UbzFOVG93T0NCQmJXVnlhV05oTDB4dmMxOUJibWRsYkdWeklqc0tDU0pwZEdWdExXbGtJaUE5SUNJMk1ESTNNalU0TWpnaU93b0pJbVY0Y0dseVpYTXRaR0YwWlMxbWIzSnRZWFIwWldRaUlEMGdJakl3TVRNdE1ESXRNVElnTVRjNk5UVTZNRGdnUlhSakwwZE5WQ0k3Q2draWNISnZaSFZqZEMxcFpDSWdQU0FpWTI5dExtMTFjMmxqY1hWaVpXUXViekl1WVhWMGIzSmxibVYzTG5SbGMzUWlPd29KSW5CMWNtTm9ZWE5sTFdSaGRHVWlJRDBnSWpJd01UTXRNREl0TVRJZ01UYzZOVEk2TURnZ1JYUmpMMGROVkNJN0Nna2liM0pwWjJsdVlXd3RjSFZ5WTJoaGMyVXRaR0YwWlNJZ1BTQWlNakF4TXkwd01pMHhNaUF4TnpvMU1qb3dPQ0JGZEdNdlIwMVVJanNLQ1NKaWFXUWlJRDBnSW1OdmJTNXRkWE5wWTNGMVltVmtMbTh5SWpzS0NTSndkWEpqYUdGelpTMWtZWFJsTFhCemRDSWdQU0FpTWpBeE15MHdNaTB4TWlBd09UbzFNam93T0NCQmJXVnlhV05oTDB4dmMxOUJibWRsYkdWeklqc0tDU0p4ZFdGdWRHbDBlU0lnUFNBaU1TSTdDbjA9IjsKCSJlbnZpcm9ubWVudCIgPSAiU2FuZGJveCI7CgkicG9kIiA9ICIxMDAiOwoJInNpZ25pbmctc3RhdHVzIiA9ICIwIjsKfQ==";
		final String transactionReceipt = base64EncodedAppStoreReceipt.replaceAll("=", "\\\\u003d");
		final String originalTransactionId = "1000000064861007";
		final long expiresDate = 1360756242000L;
		final String appStoreOriginalTransactionId = "1000000064861007";

		String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
		String userToken = Utils.createTimestampToken(storedToken, timestamp);
		
		final String expectedBody = "{\"receipt-data\":\""+transactionReceipt+"\",\"password\":\""+appleInAppPassword+"\"}";
		
		final String appStoreProductId = "com.musicqubed.o2.autorenew.test";

		final BasicResponse expectedResponse = new BasicResponse();
		expectedResponse.setStatusCode(200);
		expectedResponse.setMessage("{ \"receipt\" : { \"original_purchase_date_pst\" : \"2013-02-13 03:41:43 America/Los_Angeles\", \"unique_identifier\" : \"80d70017aae1547196bc92c02c3f83cc5f9e4cc6\", \"original_transaction_id\" : \""+originalTransactionId+"\", \"expires_date\" : \""+expiresDate+"\", \"transaction_id\" : \""+appStoreOriginalTransactionId+"\", \"quantity\" : \"1\", \"product_id\" : \""+appStoreProductId+"\", \"original_purchase_date_ms\" : \"1360755703334\", \"bid\" : \"com.musicqubed.o2\", \"web_order_line_item_id\" : \"1000000026638439\", \"bvrs\" : \"1.0\", \"expires_date_formatted\" : \"2013-02-13 11:44:42 Etc/GMT\", \"purchase_date\" : \"2013-02-13 11:41:42 Etc/GMT\", \"purchase_date_ms\" : \"1360755702795\", \"expires_date_formatted_pst\" : \"2013-02-13 03:44:42 America/Los_Angeles\", \"purchase_date_pst\" : \"2013-02-13 03:41:42 America/Los_Angeles\", \"original_purchase_date\" : \"2013-02-13 11:41:43 Etc/GMT\", \"item_id\" : \"602725828\" }, \"latest_receipt_info\" : { \"original_purchase_date_pst\" : \"2013-02-13 03:41:43 America/Los_Angeles\", \"unique_identifier\" : \"80d70017aae1547196bc92c02c3f83cc5f9e4cc6\", \"original_transaction_id\" : \""+originalTransactionId+"\", \"expires_date\" : \""+expiresDate+"\", \"transaction_id\" : \""+appStoreOriginalTransactionId+"\", \"quantity\" : \"1\", \"product_id\" : \""+appStoreProductId+"\", \"original_purchase_date_ms\" : \"1360755703000\", \"bid\" : \"com.musicqubed.o2\", \"web_order_line_item_id\" : \"1000000026638446\", \"bvrs\" : \"1.0\", \"expires_date_formatted\" : \"2013-02-13 11:50:42 Etc/GMT\", \"purchase_date\" : \"2013-02-13 11:47:42 Etc/GMT\", \"purchase_date_ms\" : \"1360756062000\", \"expires_date_formatted_pst\" : \"2013-02-13 03:50:42 America/Los_Angeles\", \"purchase_date_pst\" : \"2013-02-13 03:47:42 America/Los_Angeles\", \"original_purchase_date\" : \"2013-02-13 11:41:43 Etc/GMT\", \"item_id\" : \"602725828\" }, \"status\" : 0, \"latest_receipt\" : \""+transactionReceipt+"\" }");

		PostService mockPostService = new PostService(){
			@Override
			public BasicResponse sendHttpPost(String url, List<NameValuePair> nameValuePairs, String body) {
				assertEquals(appleInAppITunesUrl, url);
				assertEquals(expectedBody, body);
				assertNull(nameValuePairs);
				
				return expectedResponse;
			}
		};
		
		iTunesServiceImpl.setPostService(mockPostService);
		
		MockHttpServletResponse mockHttpServletResponse = acc_check(timestamp, userToken, userName, apiVersion, communityName, appVersion, UserRegInfo.DeviceType.ANDROID, null, null, base64EncodedAppStoreReceipt);
		assertEquals(200, mockHttpServletResponse.getStatus());
		
		String actualAccCheckResult = mockHttpServletResponse.getContentAsString();
		
		assertNotNull(actualAccCheckResult);
		
	    String rememberMeToken= actualAccCheckResult.substring(actualAccCheckResult.indexOf("<rememberMeToken>")+"<rememberMeToken>".length(), actualAccCheckResult.indexOf("</rememberMeToken>"));
		
		String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><user><appStoreProductId>com.musicqubed.o2.autorenew.test</appStoreProductId><chartItems>21</chartItems><chartTimestamp>1321452650</chartTimestamp><deviceType>IOS</deviceType><deviceUID>IOS</deviceUID><drmType>PLAYS</drmType><drmValue>100</drmValue><freeTrial>false</freeTrial><fullyRegistred>false</fullyRegistred><hasOffers>false</hasOffers><hasPotentialPromoCodePromotion>false</hasPotentialPromoCodePromotion><newsItems>10</newsItems><newsTimestamp>1317300123</newsTimestamp><nextSubPaymentSeconds>1988143200</nextSubPaymentSeconds><operator>1</operator><paymentEnabled>false</paymentEnabled><paymentStatus>NULL</paymentStatus><paymentType>UNKNOWN</paymentType><phoneNumber>+447111111111</phoneNumber><promotedDevice>false</promotedDevice><promotedWeeks>1037</promotedWeeks><provider>non-o2</provider><rememberMeToken>"+rememberMeToken+"</rememberMeToken><status>SUBSCRIBED</status><subBalance>0</subBalance><timeOfMovingToLimitedStatusSeconds>1988143200</timeOfMovingToLimitedStatusSeconds><userName>+447111111114</userName><userToken>"+storedToken+"</userToken><oAuthProvider>NONE</oAuthProvider></user></response>";
		
		XMLAssert.assertXMLEqual(expected, actualAccCheckResult);
	}
	
	@Test
    @Ignore
	public void testUPDATE_USER_FACEBOOK_DETAILS() throws Exception {
		String userName = "zzz@z.com";
		String apiVersion = "V3.6";
		String communityName = "o2";
		String appVersion = "CNBETA";
		String phone = "07870111111";
		String timestamp = "2011_12_26_07_04_23";
		String deviceType = UserRegInfo.DeviceType.ANDROID;

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/o2/3.6/SIGN_UP_DEVICE");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/o2/3.6/SIGN_UP_DEVICE");

		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("DEVICE_UID", userName);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		String contentAsString = mockHttpServletResponse.getContentAsString();
		String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

		final String updateUserFacebookDetailsRequestURI = "/o2/3.6/UPDATE_USER_FACEBOOK_DETAILS";
		httpServletRequest = new MockHttpServletRequest("POST", updateUserFacebookDetailsRequestURI);
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo(updateUserFacebookDetailsRequestURI);

		httpServletRequest.addParameter("communityName", communityName);
		httpServletRequest.addParameter("appVersion", appVersion);
		httpServletRequest.addParameter("apiVersion", apiVersion);
		httpServletRequest.addParameter("facebookToken", "facebookToken");
		httpServletRequest.addParameter("deviceUID", "deviceUID");

		mockHttpServletResponse = new MockHttpServletResponse();
		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
	}
	
	@Test
	public void testGET_PURCHASED_CONTENT_INFO() throws Exception {
		String userName = "zzz@z.com";
		String apiVersion = "V3.6";
		String communityName = "o2";
		String appVersion = "CNBETA";
		String phone = "07870111111";
		String timestamp = "2011_12_26_07_04_23";
		String deviceType = UserRegInfo.DeviceType.ANDROID;

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/o2/3.6/SIGN_UP_DEVICE");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/o2/3.6/SIGN_UP_DEVICE");

		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("DEVICE_UID", userName);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		String contentAsString = mockHttpServletResponse.getContentAsString();
		String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

		final String getPurchasedContentInfoRequestURI = "/o2/3.6/GET_PURCHASED_CONTENT_INFO";
		httpServletRequest = new MockHttpServletRequest("POST", getPurchasedContentInfoRequestURI);
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo(getPurchasedContentInfoRequestURI);

		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("USER_NAME", userName);
		httpServletRequest.addParameter("USER_TOKEN", userToken);
		httpServletRequest.addParameter("TIMESTAMP", timestamp);
		httpServletRequest.addParameter("APP_VERSION", appVersion);

		mockHttpServletResponse = new MockHttpServletResponse();
		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
	}
	
	@Test
	public void testDRListener() throws Exception {
		String messageId = "messageId";
		String statusType = "statusType";
		String guid = "guid";
		String status = "status";
		String description = "description";

		MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("GET", "/DRListener");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/DRListener");

		httpServletRequest.addParameter("MESSAGEID", messageId);
		httpServletRequest.addParameter("STATUSTYPE", statusType);
		httpServletRequest.addParameter("GUID", guid);
		httpServletRequest.addParameter("STATUS", status);
		httpServletRequest.addParameter("DESCRIPTION", description);

		dispatcherServlet.service(httpServletRequest, aHttpServletResponse);
		assertEquals(HttpStatus.OK.value(), aHttpServletResponse.getStatus());
	}
	
	@Test
	public void testMOListener() throws Exception {
		String action = "messageId";
		String mobile = "mobile";
		String operatorMigName = "operatorMigName";

		MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("GET", "/MOListener");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/MOListener");

		httpServletRequest.addParameter("BODY", action);
		httpServletRequest.addParameter("OADC", mobile);
		httpServletRequest.addParameter("CONNECTION", operatorMigName);

		dispatcherServlet.service(httpServletRequest, aHttpServletResponse);
		assertEquals(HttpStatus.OK.value(), aHttpServletResponse.getStatus());
    }

}
