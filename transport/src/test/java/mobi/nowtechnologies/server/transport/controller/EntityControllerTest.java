package mobi.nowtechnologies.server.transport.controller;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.dao.PaymentStatusDao;
import mobi.nowtechnologies.server.persistence.domain.DeviceUserData;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.DeviceUserDataService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.enums.UserStatus;

import org.aspectj.apache.bcel.classfile.Method;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/transport-servlet.xml", "classpath:META-INF/service-test.xml",
		"classpath:META-INF/dao-test.xml", "/META-INF/shared.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@Ignore
public class EntityControllerTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(EntityControllerTest.class.getName());

	@Resource(name = "transport.EntityController")
	EntityController entityController;

	@Resource(name = "service.UserService")
	UserService userService;
		
	@Autowired
	DeviceUserDataService deviceUserDataService;

	@Test
	public void verifyThatXtifyTokenCanBeSavedThroughRestApi() throws NoSuchMethodException {

		EntityController controller = createMock(EntityController.class,
				EntityController.class.getMethod("accountCheck", HttpServletRequest.class,
						String.class,
						String.class,
						String.class,
						String.class,
						String.class,
						String.class,
						String.class,
						String.class,
						String.class,
						String.class));
		controller.setDeviceUserDataService(deviceUserDataService);
		expect(controller.accountCheck((HttpServletRequest) anyObject(),
				(String) anyObject(),
				(String) anyObject(),
				(String) anyObject(),
				(String) anyObject(),
				(String) anyObject(),
				(String) anyObject(),
				(String) anyObject(),
				(String) anyObject(),
				(String) anyObject(),
				(String) anyObject()
				)).andReturn(null);
		replay(controller);
		controller.accountCheckWithXtifyToken(null,
				null,
				"Now Music",
				null,
				"test@test.com",
				null,
				null,
				null,
				"deviceUID",
				null,
				null,
				"1234");

		verify(controller);
		DeviceUserData data = deviceUserDataService.getByXtifyToken("1234");
		assertNotNull(data);
		assertEquals("deviceUID", data.getDeviceUID());
	}

	@Test
	@Ignore
	public void testRegisterUser_ValidParamsCardWithoutIssueNumberTag() throws Exception {
		String aBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<userRegInfo>" + "<address>33333</address>"
				+ "<appVersion>CNBETA</appVersion>" + "<apiVersion>V1.2</apiVersion>" + "<deviceType>BLACKBERRY</deviceType>"
				+ "<countryFullName>Great Britain</countryFullName>" + "<city>33</city>" + "<firstName>33</firstName>" + "<lastName>33</lastName>"
				+ "<email>q@q.com</email>" + "<displayName>33</displayName>" + "<postCode>null</postCode>" + "<paymentType>creditCard</paymentType>"
				+ "<storedToken>493c467551c6da6c0e8538a6a55572e0</storedToken>" + "<cardBillingAddress>88</cardBillingAddress>"
				+ "<cardBillingCity>London</cardBillingCity>" + "<cardBillingCountry>GB</cardBillingCountry>" + "<cardCv2>123</cardCv2>"
				+ "<cardHolderFirstName>John</cardHolderFirstName>" + "<cardHolderLastName>Smith</cardHolderLastName>"
				+ "<cardBillingPostCode>412</cardBillingPostCode>" + "<cardStartMonth>1</cardStartMonth>" + "<cardStartYear>2011</cardStartYear>"
				+ "<cardExpirationMonth>1</cardExpirationMonth>" + "<cardExpirationYear>2012</cardExpirationYear>" + "<cardNumber>4929000000006</cardNumber>"
				+ "<cardType>VISA</cardType>" + "</userRegInfo>";
		MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setRemoteAddr("2.24.0.1");

		entityController.registerUser(aBody, aHttpServletResponse, httpServletRequest);
		assertEquals(aHttpServletResponse.getStatus(), 200);

	}

	@Test
	@Rollback(value = false)
	public void testRegisterUserCheckPinAccountCheckSuccess() {
		String timestamp = "1";
		String userToken = "1a4d0298535c54cbab054eccaca4c793";
		String userName = "zzz@z.com";
		String apiVersion = "V1.2";
		String communityName = "Metal Hammer";
		String appVersion = "CNBETA";
		String deviceType = "ANDROID";
		String displayName = "Nigel";
		String deviceString = "Device 1";
		String phoneNumber = "00447580381128";
		String operator = "1";

		String aBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<userRegInfo>" + "<address>33333</address>" + "<appVersion>"
				+ appVersion + "</appVersion>" + "<apiVersion>" + apiVersion + "</apiVersion>" + "<deviceType>" + deviceType + "</deviceType>"
				+ "<deviceString>" + deviceString + "</deviceString>" + "<countryFullName>Great Britain</countryFullName>" + "<phoneNumber>" + phoneNumber
				+ "</phoneNumber>" + "<operator>" + operator + "</operator>" + "<city>33</city>" + "<firstName>33</firstName>" + "<lastName>33</lastName>"
				+ "<email>" + userName + "</email>" + "<communityName>" + communityName + "</communityName>" + "<displayName>" + displayName + "</displayName>"
				+ "<postCode>null</postCode>" + "<paymentType>" + UserRegInfo.PaymentType.PREMIUM_USER + "</paymentType>"
				+ "<storedToken>51c7bb77ae9859e18118b014188f34b1</storedToken>" + "<cardBillingAddress>88</cardBillingAddress>"
				+ "<cardBillingCity>London</cardBillingCity>" + "<cardBillingCountry>GB</cardBillingCountry>" + "<cardCv2>123</cardCv2>"
				+ "<cardIssueNumber></cardIssueNumber>" + "<cardHolderFirstName>John</cardHolderFirstName>" + "<cardHolderLastName>Smith</cardHolderLastName>"
				+ "<cardBillingPostCode>412</cardBillingPostCode>" + "<cardStartMonth>1</cardStartMonth>" + "<cardStartYear>2011</cardStartYear>"
				+ "<cardExpirationMonth>1</cardExpirationMonth>" + "<cardExpirationYear>2012</cardExpirationYear>" + "<cardNumber>4929000000006</cardNumber>"
				+ "<cardType>" + UserRegInfo.CardType.VISA + "</cardType>" + "</userRegInfo>";
		MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setRemoteAddr("2.24.0.1");

		entityController.registerUser(aBody, aHttpServletResponse, httpServletRequest);
		assertEquals(aHttpServletResponse.getStatus(), 200);

		aHttpServletResponse = new MockHttpServletResponse();
		httpServletRequest = new MockHttpServletRequest();
		User user = userService.findByName(userName);
		entityController.checkPin(appVersion, communityName, apiVersion, userName, userToken, timestamp, user.getPin(), aHttpServletResponse,
				httpServletRequest);
		assertEquals(aHttpServletResponse.getStatus(), 200);

		ModelAndView modelAndView = entityController
				.accountCheck(null, appVersion, communityName, apiVersion, userName, userToken, timestamp, null, null, null, null);
		assertNotNull(modelAndView);
		Map<String, Object> modelMap = modelAndView.getModel();
		assertNotNull(modelMap);
		assertTrue(modelMap.entrySet().size() == 1);

		Object object = modelMap.get(Response.class.toString());
		assertTrue(object instanceof Response);
		Response response = (Response) object;
		assertNotNull(response);
		Object[] objects = response.getObject();
		assertTrue(objects.length == 1);

		assertTrue(objects[0] instanceof AccountCheckDTO);
		AccountCheckDTO receivedAccountCheck = (AccountCheckDTO) objects[0];

		AccountCheckDTO accountCheck = new AccountCheckDTO();
		accountCheck.setChartItems((byte) 25);
		accountCheck.setChartTimestamp(1313172060);
		accountCheck.setDeviceType(deviceType);
		accountCheck.setDeviceUID(deviceString);
		accountCheck.setDisplayName(displayName);
		accountCheck.setDrmType(null);
		accountCheck.setDrmValue((byte) 0);
		accountCheck.setNewsTimestamp(1312560155);
		accountCheck.setNewsItems((byte) 10);
		accountCheck.setStatus(UserStatus.EULA.toString());
		accountCheck.setSubBalance((byte) 0);
		accountCheck.setPhoneNumber(phoneNumber);
		accountCheck.setOperator(Integer.valueOf(operator));
		// accountCheck.setPaymentStatus("AWAITING_PSMS");

		assertEquals(accountCheck.getChartItems(), receivedAccountCheck.getChartItems());
		assertEquals(accountCheck.getChartTimestamp(), receivedAccountCheck.getChartTimestamp());
		assertEquals(accountCheck.getDeviceType(), receivedAccountCheck.getDeviceType());
		assertEquals(accountCheck.getDeviceUID(), receivedAccountCheck.getDeviceUID());
		assertEquals(accountCheck.getDisplayName(), receivedAccountCheck.getDisplayName());
		assertEquals(accountCheck.getDrmType(), receivedAccountCheck.getDrmType());
		assertEquals(accountCheck.getDrmValue(), receivedAccountCheck.getDrmValue());
		assertEquals(accountCheck.getNewsTimestamp(), receivedAccountCheck.getNewsTimestamp());
		assertEquals(accountCheck.getNewsItems(), receivedAccountCheck.getNewsItems());
		assertEquals(accountCheck.getStatus(), receivedAccountCheck.getStatus());
		assertEquals(accountCheck.getSubBalance(), receivedAccountCheck.getSubBalance());
		assertEquals(accountCheck.getPhoneNumber(), receivedAccountCheck.getPhoneNumber());
		assertEquals(accountCheck.getOperator(), receivedAccountCheck.getOperator());
		// assertEquals(accountCheck.getPaymentStatus(),receivedAccountCheck.getPaymentStatus());
		String receivedPaymentStatus = receivedAccountCheck.getPaymentStatus();
		assertTrue(receivedPaymentStatus.equals(PaymentStatusDao.getAWAITING_PSMS().getName())
				|| receivedPaymentStatus.equals(PaymentStatusDao.getOK().getName()));
	}

	/**
	 * Run the void registerUser(String,HttpServletResponse,HttpServletRequest)
	 * method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.07.11 15:17
	 */
	@Test
	public void testRegisterUser_ValidParamsCardIssueNumberTagIsEmpty() {
		String aBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<userRegInfo>" + "<address>33333</address>"
				+ "<appVersion>CNBETA</appVersion>" + "<apiVersion>V1.2</apiVersion>" + "<deviceType>BLACKBERRY</deviceType>"
				+ "<deviceString>Device 1</deviceString>" + "<countryFullName>Great Britain</countryFullName>" + "<phoneNumber>00447580381128</phoneNumber>"
				+ "<operator>1</operator>" + "<city>33</city>" + "<firstName>33</firstName>" + "<lastName>33</lastName>" + "<email>i@i.com</email>"
				+ "<communityName>Metal Hammer</communityName>" + "<displayName>33</displayName>" + "<postCode>null</postCode>" + "<paymentType>"
				+ UserRegInfo.PaymentType.PREMIUM_USER + "</paymentType>" + "<storedToken>51c7bb77ae9859e18118b014188f34b1</storedToken>"
				+ "<cardBillingAddress>88</cardBillingAddress>" + "<cardBillingCity>London</cardBillingCity>" + "<cardBillingCountry>GB</cardBillingCountry>"
				+ "<cardCv2>123</cardCv2>" + "<cardIssueNumber></cardIssueNumber>" + "<cardHolderFirstName>John</cardHolderFirstName>"
				+ "<cardHolderLastName>Smith</cardHolderLastName>" + "<cardBillingPostCode>412</cardBillingPostCode>" + "<cardStartMonth>1</cardStartMonth>"
				+ "<cardStartYear>2011</cardStartYear>" + "<cardExpirationMonth>1</cardExpirationMonth>" + "<cardExpirationYear>2012</cardExpirationYear>"
				+ "<cardNumber>4929000000006</cardNumber>" + "<cardType>" + UserRegInfo.CardType.VISA + "</cardType>" + "</userRegInfo>";
		MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setRemoteAddr("2.24.0.1");

		entityController.registerUser(aBody, aHttpServletResponse, httpServletRequest);
		assertEquals(aHttpServletResponse.getStatus(), 200);

	}

}