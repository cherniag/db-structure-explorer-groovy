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
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.job.CreatePendingPaymentJob;
import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.EntityDao;
import mobi.nowtechnologies.server.persistence.dao.PaymentStatusDao;
import mobi.nowtechnologies.server.persistence.dao.UserDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.PaymentStatus;
import mobi.nowtechnologies.server.persistence.domain.PremiumUserPayment;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.security.NowTechTokenBasedRememberMeServices;
import mobi.nowtechnologies.server.service.CountryByIpService;
import mobi.nowtechnologies.server.service.EntityService;
import mobi.nowtechnologies.server.service.FacebookService;
import mobi.nowtechnologies.server.service.FileService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.WeeklyUpdateService;
import mobi.nowtechnologies.server.service.UserService.AmountCurrencyWeeks;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.AppConstants;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.TransactionType;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import mobi.nowtechnologies.server.shared.service.PostService;

import org.apache.http.NameValuePair;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.ElementNameAndTextQualifier;
import org.custommonkey.xmlunit.ElementQualifier;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.examples.RecursiveElementNameAndTextQualifier;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.DispatcherServlet;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The class <code>EntityControllerTest</code> contains tests for the class
 * <code>{@link EntityController}</code>.
 * 
 * @generatedBy CodePro at 20.07.11 15:17, using the Spring generator
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:transport-servlet-test.xml",
		"classpath:META-INF/service-test.xml",
		"classpath:META-INF/dao-test.xml",
		"classpath:META-INF/shared.xml" }, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "transport.EntityController")
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@PrepareForTest(Utils.class)
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

	// @Resource(name = "transport.EntityController")
	// private EntityController entityController;

	@Resource(name = "paymentTask")
	private CreatePendingPaymentJob createPendingPaymentJob;

	@Resource
	private EntityController entityController;

//	@Rule
//	public PowerMockRule powerMockRule = new PowerMockRule();
	
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

	@Test
	public void test_PSMSUserStatusAndPaymentStatusLifeCycle_Success() {
		try {

			PostService mockPostService = new PostService() {
				@Override
				public Response sendHttpPost(String url, List<NameValuePair> nameValuePairs, String body) {
					if (url == null)
						throw new NullPointerException(
								"The parameter url is null");
					if (nameValuePairs == null)
						throw new NullPointerException(
								"The parameter nameValuePairs is null");
					Response response = new Response();
					response.setStatusCode(200);
					response.setMessage("OK");
					return response;
				}
			};

			LOGGER.info("mockPostService created and wired");

			String timestamp = "1";
			String userToken = "1a4d0298535c54cbab054eccaca4c793";
			String userName = "zzz@z.com";
			String apiVersion = "V1.2";
			String communityName = "Now Music";
			String appVersion = "CNBETA";
			String deviceType = "ANDROID";
			String displayName = "Nigel";
			String deviceString = "Device 1";
			String phoneNumber = "00447580381128";
			String operator = "1";

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
					+ "<paymentType>" + UserRegInfo.PaymentType.PREMIUM_USER + "</paymentType>"
					+ "<storedToken>51c7bb77ae9859e18118b014188f34b1</storedToken>"
					+ "<cardBillingAddress>88</cardBillingAddress>"
					+ "<cardBillingCity>London</cardBillingCity>"
					+ "<cardBillingCountry>GB</cardBillingCountry>"
					+ "<cardCv2>123</cardCv2>"
					+ "<cardIssueNumber></cardIssueNumber>"
					+ "<cardHolderFirstName>John</cardHolderFirstName>"
					+ "<cardHolderLastName>Smith</cardHolderLastName>"
					+ "<cardBillingPostCode>412</cardBillingPostCode>"
					+ "<cardStartMonth>1</cardStartMonth>"
					+ "<cardStartYear>2011</cardStartYear>"
					+ "<cardExpirationMonth>1</cardExpirationMonth>"
					+ "<cardExpirationYear>2012</cardExpirationYear>"
					+ "<cardNumber>4929000000006</cardNumber>"
					+ "<cardType>" + UserRegInfo.CardType.VISA + "</cardType>" + "</userRegInfo>";

			MockHttpServletResponse aHttpServletResponse = registerUser(aBody, "2.24.0.1");

			assertEquals(200, aHttpServletResponse.getStatus());

			User user = userService.findByNameAndCommunity(userName, communityName);
			final int userId = user.getId();

			assertEquals(UserStatus.EULA.getCode(), user.getUserStatusId());

			PremiumUserPayment lastPremiumUserPayment = findLastPremiumUserPayment(userId);
			assertNull(lastPremiumUserPayment);

			String paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getPIN_PENDING().getName(), paymentStatus);

			aHttpServletResponse = checkPin(timestamp, userToken, apiVersion,
					userName, communityName, appVersion, user.getPin());

			assertEquals(200, aHttpServletResponse.getStatus());

			user = userService.findByNameAndCommunity(userName, communityName);
			assertEquals(UserStatus.EULA.getCode(), user.getUserStatusId());

			aHttpServletResponse = acc_check(timestamp, userToken, userName,
					apiVersion, communityName, appVersion, null, null, null);

			String responseBody = aHttpServletResponse.getContentAsString();

			assertNotNull(responseBody);
			assertEquals(
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
							+ "<response>"
							+ "<user>"
							+ "<chartItems>40</chartItems>"
							+ "<chartTimestamp>1317302136</chartTimestamp>"
							+ "<deviceType>" + deviceType + "</deviceType>"
							+ "<deviceUID>" + deviceString + "</deviceUID>"
							+ "<displayName>" + displayName + "</displayName>"
							+ "<drmValue>0</drmValue>"
							+ "<newsItems>10</newsItems>"
							+ "<newsTimestamp>1317300123</newsTimestamp>"
							+ "<operator>" + operator + "</operator>"
							+ "<paymentEnabled>true</paymentEnabled>"
							+ "<paymentStatus>AWAITING_PAYMENT</paymentStatus>"
							+ "<paymentType>PSMS</paymentType>"
							+ "<phoneNumber>" + phoneNumber + "</phoneNumber>"
							+ "<status>EULA</status>"
							+ "<subBalance>0</subBalance>"
							+ "</user>"
							+ "</response>", responseBody);

			user = userService.findByNameAndCommunity(userName, communityName);
			assertEquals(UserStatus.EULA.getCode(), user.getUserStatusId());

			paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getAWAITING_PAYMENT().getName(), paymentStatus);

			lastPremiumUserPayment = findLastPremiumUserPayment(userId);

			AmountCurrencyWeeks amountCurrencyWeeks = userService.getUpdateAmountCurrencyWeeks(user);

			assertNotNull(lastPremiumUserPayment);
			assertEquals(AppConstants.STATUS_PENDING, lastPremiumUserPayment.getStatus());
			assertEquals(amountCurrencyWeeks.getWeeks(), lastPremiumUserPayment.getSubweeks());

			// --------------------------------------------------------------------------------------------------
			// CL-2335: User can pay by one policy and get weeks by other policy
			// rules
			// http://jira.dev.now-technologies.mobi:8181/browse/CL-2335
			Integer operator2 = 3;

			PaymentPolicy paymentPolicy = new PaymentPolicy();
			// paymentPolicy.setCommunityid(5);
			// paymentPolicy.setOperator(operator2);
			paymentPolicy.setPaymentType("PSMS");
			paymentPolicy.setShortCode("80988");
			paymentPolicy.setSubcost(new BigDecimal(33));
			paymentPolicy.setSubweeks((byte) 33);

			entityService.saveEntity(paymentPolicy);

			aHttpServletResponse = updatePhone(timestamp, userToken, userName,
					apiVersion, communityName, appVersion, phoneNumber,
					operator2);

			assertEquals(200, aHttpServletResponse.getStatus());

			user = userService.findByNameAndCommunity(userName, communityName);

			paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getPIN_PENDING().getName(), paymentStatus);

			aHttpServletResponse = checkPin(timestamp, userToken, apiVersion,
					userName, communityName, appVersion, user.getPin());

			assertEquals(200, aHttpServletResponse.getStatus());

			user = userService.findByNameAndCommunity(userName, communityName);

			paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getNULL().getName(), paymentStatus);

			AmountCurrencyWeeks newAmountCurrencyWeeks = userService.getUpdateAmountCurrencyWeeks(user);

			assertTrue(newAmountCurrencyWeeks.getWeeks() != amountCurrencyWeeks.getWeeks());
			assertEquals(paymentPolicy.getSubweeks(), newAmountCurrencyWeeks.getWeeks());

			aHttpServletResponse = emulateMigSuccesCallback(lastPremiumUserPayment
					.getInternalTxCode());
			assertEquals(200, aHttpServletResponse.getStatus());

			lastPremiumUserPayment = entityService.findById(PremiumUserPayment.class, lastPremiumUserPayment.getI());

			paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getOK().getName(), paymentStatus);

			aHttpServletResponse = acc_check(timestamp, userToken, userName,
					apiVersion, communityName, appVersion, null, null, null);

			responseBody = aHttpServletResponse.getContentAsString();

			assertNotNull(responseBody);

			final Map<String, Object> nameValueMap = new HashMap<String, Object>();
			nameValueMap.put(PaymentPolicy.Fields.operator.toString(),
					user.getOperator());
			nameValueMap.put(PaymentPolicy.Fields.communityId.toString(),
					CommunityDao.getMapAsNames().get(communityName).getId());
			// PaymentPolicy
			// paymentPolicy=entityService.findByProperties(PaymentPolicy.class,
			// nameValueMap);

			byte freeWeeks = 0;
			Promotion promotion = userDao.getActivePromotion(user.getUserGroupId());
			if (promotion != null)
				freeWeeks = promotion.getFreeWeeks();
			assertTrue(freeWeeks >= 0);

			// int
			// expectedSubBlance=paymentPolicy.getSubweeks()+amountCurrencyWeeks.getWeeks();
			int expectedSubBlance = freeWeeks + amountCurrencyWeeks.getWeeks() - 1;// -1
																					// because
																					// it's
																					// a
																					// first
																					// login
																					// where
																					// status
																					// SUBSCRIBED

			assertEquals(responseBody,
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
							+ "<response>"
							+ "<user>"
							+ "<chartItems>40</chartItems>"
							+ "<chartTimestamp>1317302136</chartTimestamp>"
							+ "<deviceType>" + deviceType + "</deviceType>"
							+ "<deviceUID>" + deviceString + "</deviceUID>"
							+ "<displayName>" + displayName + "</displayName>"
							+ "<drmValue>0</drmValue>"
							+ "<newsItems>10</newsItems>"
							+ "<newsTimestamp>1317300123</newsTimestamp>"
							+ "<operator>" + operator2 + "</operator>"
							+ "<paymentEnabled>true</paymentEnabled>"
							+ "<paymentStatus>OK</paymentStatus>"
							+ "<paymentType>PSMS</paymentType>"
							+ "<phoneNumber>" + phoneNumber + "</phoneNumber>"
							+ "<status>SUBSCRIBED</status>"
							+ "<subBalance>" + expectedSubBlance + "</subBalance>"
							+ "</user>"
							+ "</response>");

			user = userService.findByNameAndCommunity(userName, communityName);
			assertEquals(UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());

			assertEquals(expectedSubBlance, user.getSubBalance());

			int nextSubPayment = user.getNextSubPayment();

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(user.getNextSubPayment() * 1000L);
			SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
			Date dateTime = calendar.getTime();

			LOGGER.info(String.valueOf(nextSubPayment));
			LOGGER.info(dateFormatLocal.format(dateTime));

			assertTrue((Utils.getEpochSeconds() < nextSubPayment) && (Utils.getEpochSeconds() + Utils.WEEK_SECONDS >= nextSubPayment));

			List<AccountLog> accountLogs = entityService.findListByProperty(AccountLog.class, AccountLog.Fields.userId.toString(), userId);

			assertNotNull(accountLogs);
			assertEquals(3, accountLogs.size());

			Map<String, Object> fieldNameValueMap = new HashMap<String, Object>();
			fieldNameValueMap.put(AccountLog.Fields.userId.toString(), userId);
			fieldNameValueMap.put(AccountLog.Fields.transactionType.toString(), TransactionType.CARD_TOP_UP);

			AccountLog accountLog = entityService.findByProperties(AccountLog.class, fieldNameValueMap);
			assertNotNull(accountLog);
			assertEquals(amountCurrencyWeeks.getWeeks(), accountLog.getBalanceAfter());

			fieldNameValueMap.put(AccountLog.Fields.transactionType.toString(), TransactionType.PROMOTION.getCode());
			accountLog = entityService.findByProperties(AccountLog.class, fieldNameValueMap);
			assertNotNull(accountLog);
			assertEquals(freeWeeks + amountCurrencyWeeks.getWeeks(), accountLog.getBalanceAfter());

			fieldNameValueMap.put(AccountLog.Fields.transactionType.toString(), TransactionType.SUBSCRIPTION_CHARGE.getCode());
			accountLog = entityService.findByProperties(AccountLog.class, fieldNameValueMap);
			assertNotNull(accountLog);
			assertEquals(expectedSubBlance, accountLog.getBalanceAfter());

			// ------------------------------------------------------------------------------------------------------------------------------
			// Emulate CL-2348: Balance is not re-filled after it becomes 0
			// http://jira.dev.now-technologies.mobi:8181/browse/CL-2348
			user.setSubBalance((byte) 0);

			userService.updateUser(user);

			// accountUpdateService.updateAccounts();

			user = userService.findByNameAndCommunity(userName, communityName);

			assertEquals((byte) 0, user.getSubBalance());

			paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getAWAITING_PSMS().getName(), paymentStatus);
			assertEquals(UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());

			PremiumUserPayment newlastPremiumUserPayment = findLastPremiumUserPayment(userId);
			assertNotNull(newlastPremiumUserPayment);
			assertTrue(newlastPremiumUserPayment.getTimestamp() > lastPremiumUserPayment.getTimestamp());

			aHttpServletResponse = emulateMigSuccesCallback(newlastPremiumUserPayment
					.getInternalTxCode());
			assertEquals(200, aHttpServletResponse.getStatus());

			user = userService.findByNameAndCommunity(userName, communityName);
			paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getOK().getName(), paymentStatus);
			assertEquals(UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());

			amountCurrencyWeeks = userService.getUpdateAmountCurrencyWeeks(user);
			expectedSubBlance = amountCurrencyWeeks.getWeeks();

			assertEquals(expectedSubBlance, user.getSubBalance());
			// ------------------------------------------------------------------------------------------------
			// CL-2350: Balance is not re-filled after 'nextSubPayment' date
			// pass
			// http://jira.dev.now-technologies.mobi:8181/browse/CL-2350

			user.setSubBalance((byte) 10);
			user.setNextSubPayment(0);

			userService.updateUser(user);

			weeklyUpdateService.updateWeekly();

			user = userService.findByNameAndCommunity(userName, communityName);

			assertEquals((byte) 9, user.getSubBalance());

			// -------------------------------------------------------------------------------------------------
			// CL-2267: User gets to LIMITED status after balance is 0
			// http://jira.dev.now-technologies.mobi:8181/browse/CL-2267
			user.setSubBalance((byte) 0);
			user.setPaymentStatus(PaymentStatusDao.getAWAITING_PSMS().getId());
			user.setNumPsmsRetries(0);

			userService.updateUser(user);

			user = userService.findByNameAndCommunity(userName, communityName);
			paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getAWAITING_PSMS().getName(), paymentStatus);
			assertEquals(UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());

			newlastPremiumUserPayment = findLastPremiumUserPayment(userId);
			assertNotNull(newlastPremiumUserPayment);
			assertTrue(newlastPremiumUserPayment.getTimestamp() > lastPremiumUserPayment.getTimestamp());

			aHttpServletResponse = emulateMigUnSuccessCallback(newlastPremiumUserPayment
					.getInternalTxCode());
			assertEquals(200, aHttpServletResponse.getStatus());

			user = userService.findByNameAndCommunity(userName, communityName);
			paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getPSMS_ERROR().getName(), paymentStatus);
			assertEquals(UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());

			// -------------------------------------------------------------------------------------------------
			// CL-2202: User doesn't move to LIMITED status after failed
			// NextSubscription
			// http://jira.dev.now-technologies.mobi:8181/browse/CL-2202
			user.setSubBalance((byte) 0);
			user.setPaymentStatus(PaymentStatusDao.getOK().getId());
			user.setNumPsmsRetries(0);
			user.setNextSubPayment(0);

			userService.updateUser(user);
			// accountUpdateService.updateAccounts();

			user = userService.findByNameAndCommunity(userName, communityName);
			paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getAWAITING_PSMS().getName(), paymentStatus);
			assertEquals(UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());

			newlastPremiumUserPayment = findLastPremiumUserPayment(userId);
			assertNotNull(newlastPremiumUserPayment);
			assertTrue(newlastPremiumUserPayment.getTimestamp() > lastPremiumUserPayment.getTimestamp());

			aHttpServletResponse = emulateMigUnSuccessCallback(newlastPremiumUserPayment
					.getInternalTxCode());
			assertEquals(200, aHttpServletResponse.getStatus());

			user = userService.findByNameAndCommunity(userName, communityName);
			paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getPSMS_ERROR().getName(), paymentStatus);
			assertEquals(UserStatus.LIMITED.getCode(), user.getUserStatusId());

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
			throw new RuntimeException(e);
		}
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

	private MockHttpServletResponse emulateMigUnSuccessCallback(
			String internalTxCode)
			throws ServletException, IOException {
		if (internalTxCode == null)
			throw new NullPointerException(
					"The parameter internalTxCode is null");

		MockHttpServletResponse aHttpServletResponse;
		MockHttpServletRequest httpServletRequest;
		aHttpServletResponse = new MockHttpServletResponse();
		httpServletRequest = new MockHttpServletRequest("GET",
				"/DRListener");
		httpServletRequest.addParameter("STATUSTYPE", "20");
		httpServletRequest.addParameter("GUID", "12345");
		httpServletRequest.addParameter("STATUS", "1");
		httpServletRequest.addParameter("MESSAGEID", internalTxCode);

		dispatcherServlet.service(httpServletRequest, aHttpServletResponse);
		return aHttpServletResponse;
	}

	private MockHttpServletResponse acc_check(String timestamp,
			String userToken, String userName, String apiVersion,
			String communityName, String appVersion, String deviceType, String pushNotificationToken, String iphoneToken) throws ServletException,
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
		httpServletRequest = new MockHttpServletRequest("POST", "/ACC_CHECK");
		httpServletRequest.setPathInfo("/ACC_CHECK");
		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("USER_NAME", userName);
		httpServletRequest.addParameter("USER_TOKEN", userToken);
		httpServletRequest.addParameter("TIMESTAMP", timestamp);

		httpServletRequest.addParameter("DEVICE_TYPE", deviceType);
		httpServletRequest.addParameter("PUSH_NOTIFICATION_TOKEN", pushNotificationToken);
		httpServletRequest.addParameter("IPHONE_TOKEN", iphoneToken);

		dispatcherServlet.service(httpServletRequest, aHttpServletResponse);
		return aHttpServletResponse;
	}

	@Test
	public void test_PSMSUserStatusAndPaymentOnNotOkMigResponse() {
		// CL-2378: User status is changed instead of paymentStatus on first
		// failed payment
		// http://jira.dev.now-technologies.mobi:8181/browse/CL-2378
		try {

			PostService mockPostService = new PostService() {
				@Override
				public Response sendHttpPost(String url, List<NameValuePair> nameValuePairs, String body) {
					if (url == null)
						throw new NullPointerException(
								"The parameter url is null");
					if (nameValuePairs == null)
						throw new NullPointerException(
								"The parameter nameValuePairs is null");
					Response response = new Response();
					response.setStatusCode(200);
					response.setMessage("");
					return response;
				}
			};

			LOGGER.info("mockPostService created and wired");

			String timestamp = "1";
			String userToken = "1a4d0298535c54cbab054eccaca4c793";
			String userName = "zzz@z.com";
			String apiVersion = "V1.2";
			String communityName = "Now Music";
			String appVersion = "CNBETA";
			String deviceType = "ANDROID";
			String displayName = "Nigel";
			String deviceString = "Device 1";
			String phoneNumber = "00447580381128";
			String operator = "1";

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
					+ "<paymentType>" + UserRegInfo.PaymentType.PREMIUM_USER + "</paymentType>"
					+ "<storedToken>51c7bb77ae9859e18118b014188f34b1</storedToken>"
					+ "<cardBillingAddress>88</cardBillingAddress>"
					+ "<cardBillingCity>London</cardBillingCity>"
					+ "<cardBillingCountry>GB</cardBillingCountry>"
					+ "<cardCv2>123</cardCv2>"
					+ "<cardIssueNumber></cardIssueNumber>"
					+ "<cardHolderFirstName>John</cardHolderFirstName>"
					+ "<cardHolderLastName>Smith</cardHolderLastName>"
					+ "<cardBillingPostCode>412</cardBillingPostCode>"
					+ "<cardStartMonth>1</cardStartMonth>"
					+ "<cardStartYear>2011</cardStartYear>"
					+ "<cardExpirationMonth>1</cardExpirationMonth>"
					+ "<cardExpirationYear>2012</cardExpirationYear>"
					+ "<cardNumber>4929000000006</cardNumber>"
					+ "<cardType>" + UserRegInfo.CardType.VISA + "</cardType>" + "</userRegInfo>";

			MockHttpServletResponse aHttpServletResponse = registerUser(aBody, "2.24.0.1");

			assertEquals(200, aHttpServletResponse.getStatus());

			User user = userService.findByNameAndCommunity(userName, communityName);
			final int userId = user.getId();

			assertEquals(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.getCode(), user.getUserStatusId());

			PremiumUserPayment lastPremiumUserPayment = findLastPremiumUserPayment(userId);
			assertNull(lastPremiumUserPayment);

			String paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getPIN_PENDING().getName(), paymentStatus);

			aHttpServletResponse = checkPin(timestamp, userToken, apiVersion,
					userName, communityName, appVersion, user.getPin());

			assertEquals(200, aHttpServletResponse.getStatus());

			user = userService.findByNameAndCommunity(userName, communityName);
			assertEquals(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.getCode(), user.getUserStatusId());

			aHttpServletResponse = acc_check(timestamp, userToken, userName,
					apiVersion, communityName, appVersion, null, null, null);

			String responseBody = aHttpServletResponse.getContentAsString();

			assertNotNull(responseBody);
			assertEquals(
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
							+ "<response>"
							+ "<user>"
							+ "<chartItems>40</chartItems>"
							+ "<chartTimestamp>1317302136</chartTimestamp>"
							+ "<deviceType>" + deviceType + "</deviceType>"
							+ "<deviceUID>" + deviceString + "</deviceUID>"
							+ "<displayName>" + displayName + "</displayName>"
							+ "<drmValue>0</drmValue>"
							+ "<newsItems>10</newsItems>"
							+ "<newsTimestamp>1317300123</newsTimestamp>"
							+ "<operator>" + operator + "</operator>"
							+ "<paymentEnabled>false</paymentEnabled>"
							+ "<paymentStatus>PSMS_ERROR</paymentStatus>"
							+ "<paymentType>PSMS</paymentType>"
							+ "<phoneNumber>" + phoneNumber + "</phoneNumber>"
							+ "<status>EULA</status>"
							+ "<subBalance>0</subBalance>"
							+ "</user>"
							+ "</response>", responseBody);

			user = userService.findByNameAndCommunity(userName, communityName);
			assertEquals(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.getCode(), user.getUserStatusId());

			paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getPSMS_ERROR().getName(), paymentStatus);

			lastPremiumUserPayment = findLastPremiumUserPayment(userId);
			assertEquals(AppConstants.STATUS_FAIL, lastPremiumUserPayment.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
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

	@Test
	public void test_PSMSUserStatusAndPaymentOnNot200MigHttpStatus() {
		// CL-2378: User status is changed instead of paymentStatus on first
		// failed payment
		// http://jira.dev.now-technologies.mobi:8181/browse/CL-2378
		try {

			PostService mockPostService = new PostService() {
				@Override
				public Response sendHttpPost(String url, List<NameValuePair> nameValuePairs, String body) {
					if (url == null)
						throw new NullPointerException(
								"The parameter url is null");
					if (nameValuePairs == null)
						throw new NullPointerException(
								"The parameter nameValuePairs is null");
					Response response = new Response();
					response.setStatusCode(400);
					response.setMessage("");
					return response;
				}
			};

			LOGGER.info("mockPostService created and wired");

			String timestamp = "1";
			String userToken = "1a4d0298535c54cbab054eccaca4c793";
			String userName = "zzz@z.com";
			String apiVersion = "V1.2";
			String communityName = "Now Music";
			String appVersion = "CNBETA";
			String deviceType = "ANDROID";
			String displayName = "Nigel";
			String deviceString = "Device 1";
			String phoneNumber = "00447580381128";
			String operator = "1";

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
					+ "<paymentType>" + UserRegInfo.PaymentType.PREMIUM_USER + "</paymentType>"
					+ "<storedToken>51c7bb77ae9859e18118b014188f34b1</storedToken>"
					+ "<cardBillingAddress>88</cardBillingAddress>"
					+ "<cardBillingCity>London</cardBillingCity>"
					+ "<cardBillingCountry>GB</cardBillingCountry>"
					+ "<cardCv2>123</cardCv2>"
					+ "<cardIssueNumber></cardIssueNumber>"
					+ "<cardHolderFirstName>John</cardHolderFirstName>"
					+ "<cardHolderLastName>Smith</cardHolderLastName>"
					+ "<cardBillingPostCode>412</cardBillingPostCode>"
					+ "<cardStartMonth>1</cardStartMonth>"
					+ "<cardStartYear>2011</cardStartYear>"
					+ "<cardExpirationMonth>1</cardExpirationMonth>"
					+ "<cardExpirationYear>2012</cardExpirationYear>"
					+ "<cardNumber>4929000000006</cardNumber>"
					+ "<cardType>" + UserRegInfo.CardType.VISA + "</cardType>" + "</userRegInfo>";

			MockHttpServletResponse aHttpServletResponse = registerUser(aBody, "2.24.0.1");

			assertEquals(200, aHttpServletResponse.getStatus());

			User user = userService.findByNameAndCommunity(userName, communityName);
			final int userId = user.getId();

			assertEquals(UserStatus.EULA.getCode(), user.getUserStatusId());

			PremiumUserPayment lastPremiumUserPayment = findLastPremiumUserPayment(userId);
			assertNull(lastPremiumUserPayment);

			String paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getPIN_PENDING().getName(), paymentStatus);

			aHttpServletResponse = checkPin(timestamp, userToken, apiVersion,
					userName, communityName, appVersion, user.getPin());

			assertEquals(200, aHttpServletResponse.getStatus());

			user = userService.findByNameAndCommunity(userName, communityName);
			assertEquals(UserStatus.EULA.getCode(), user.getUserStatusId());

			aHttpServletResponse = acc_check(timestamp, userToken, userName,
					apiVersion, communityName, appVersion, null, null, null);

			String responseBody = aHttpServletResponse.getContentAsString();

			assertNotNull(responseBody);
			assertEquals(
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
							+ "<response>"
							+ "<user>"
							+ "<chartItems>40</chartItems>"
							+ "<chartTimestamp>1317302136</chartTimestamp>"
							+ "<deviceType>" + deviceType + "</deviceType>"
							+ "<deviceUID>" + deviceString + "</deviceUID>"
							+ "<displayName>" + displayName + "</displayName>"
							+ "<drmValue>0</drmValue>"
							+ "<newsItems>10</newsItems>"
							+ "<newsTimestamp>1317300123</newsTimestamp>"
							+ "<operator>" + operator + "</operator>"
							+ "<paymentEnabled>false</paymentEnabled>"
							+ "<paymentStatus>" + PaymentStatusDao.getAWAITING_PSMS().getName() + "</paymentStatus>"
							+ "<paymentType>PSMS</paymentType>"
							+ "<phoneNumber>" + phoneNumber + "</phoneNumber>"
							+ "<status>" + UserStatus.EULA + "</status>"
							+ "<subBalance>0</subBalance>"
							+ "</user>"
							+ "</response>", responseBody);

			user = userService.findByNameAndCommunity(userName, communityName);
			assertEquals(UserStatus.EULA.getCode(), user.getUserStatusId());

			paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getAWAITING_PSMS().getName(), paymentStatus);

			lastPremiumUserPayment = findLastPremiumUserPayment(userId);
			assertEquals(AppConstants.STATUS_PENDING, lastPremiumUserPayment.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	private void registerPSMSUserToSubscridedStatus(UserRegInfo userRegInfo, String timestamp, String userToken, String apiVersion) throws ServletException, IOException {
		if (userRegInfo == null)
			throw new NullPointerException("The parameter userRegInfo is null");
		if (timestamp == null)
			throw new NullPointerException("The parameter timestamp is null");
		if (userToken == null)
			throw new NullPointerException("The parameter userToken is null");
		if (apiVersion == null)
			throw new NullPointerException("The parameter apiVersion is null");

		String userName = userRegInfo.getEmail();
		String communityName = userRegInfo.getCommunityName();
		String appVersion = userRegInfo.getAppVersion();
		String deviceType = userRegInfo.getDeviceType();
		String displayName = userRegInfo.getDisplayName();
		String deviceString = userRegInfo.getDeviceString();
		String phoneNumber = userRegInfo.getPhoneNumber();
		String operator = String.valueOf(userRegInfo.getOperator());
		String storedToken = userRegInfo.getStoredToken();

		if (userName == null)
			throw new NullPointerException("The parameter userName is null");
		if (communityName == null)
			throw new NullPointerException("The parameter communityName is null");
		if (appVersion == null)
			throw new NullPointerException("The parameter appVersion is null");
		if (deviceType == null)
			throw new NullPointerException("The parameter deviceType is null");
		if (displayName == null)
			throw new NullPointerException("The parameter displayName is null");
		if (deviceString == null)
			throw new NullPointerException("The parameter deviceString is null");
		if (phoneNumber == null)
			throw new NullPointerException("The parameter phoneNumber is null");

		PostService mockPostService = new PostService() {
			@Override
			public Response sendHttpPost(String url, List<NameValuePair> nameValuePairs, String body) {
				if (url == null)
					throw new NullPointerException(
							"The parameter url is null");
				if (nameValuePairs == null)
					throw new NullPointerException(
							"The parameter nameValuePairs is null");
				Response response = new Response();
				response.setStatusCode(200);
				response.setMessage("OK");
				return response;
			}
		};

		LOGGER.info("mockPostService created and wired");

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
				+ "<paymentType>" + UserRegInfo.PaymentType.PREMIUM_USER + "</paymentType>"
				+ "<storedToken>" + storedToken + "</storedToken>"
				+ "<cardBillingAddress>88</cardBillingAddress>"
				+ "<cardBillingCity>London</cardBillingCity>"
				+ "<cardBillingCountry>GB</cardBillingCountry>"
				+ "<cardCv2>123</cardCv2>"
				+ "<cardIssueNumber></cardIssueNumber>"
				+ "<cardHolderFirstName>John</cardHolderFirstName>"
				+ "<cardHolderLastName>Smith</cardHolderLastName>"
				+ "<cardBillingPostCode>412</cardBillingPostCode>"
				+ "<cardStartMonth>1</cardStartMonth>"
				+ "<cardStartYear>2011</cardStartYear>"
				+ "<cardExpirationMonth>1</cardExpirationMonth>"
				+ "<cardExpirationYear>2012</cardExpirationYear>"
				+ "<cardNumber>4929000000006</cardNumber>"
				+ "<cardType>" + UserRegInfo.CardType.VISA + "</cardType>" + "</userRegInfo>";

		MockHttpServletResponse aHttpServletResponse = registerUser(aBody, "2.24.0.1");

		assertEquals(200, aHttpServletResponse.getStatus());

		User user = userService.findByNameAndCommunity(userName, communityName);
		final int userId = user.getId();

		assertEquals(UserStatus.EULA.getCode(), user.getUserStatusId());
		assertFalse(user.isPaymentEnabled());

		PremiumUserPayment lastPremiumUserPayment = findLastPremiumUserPayment(userId);
		assertNull(lastPremiumUserPayment);

		// String
		// paymentStatus=PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
		// assertEquals(PaymentStatusDao.getPIN_PENDING().getName(),
		// paymentStatus);

		aHttpServletResponse = checkPin(timestamp, userToken, apiVersion,
				userName, communityName, appVersion, user.getPin());

		assertEquals(200, aHttpServletResponse.getStatus());

		user = userService.findByNameAndCommunity(userName, communityName);
		assertEquals(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.getCode(), user.getUserStatusId());

		aHttpServletResponse = acc_check(timestamp, userToken, userName,
				apiVersion, communityName, appVersion, null, null, null);

		String responseBody = aHttpServletResponse.getContentAsString();

		assertNotNull(responseBody);
		// assertEquals(
		// "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
		// +"<response>"
		// +"<user>"
		// +"<chartItems>40</chartItems>"
		// +"<chartTimestamp>1321452650</chartTimestamp>"
		// +"<deviceType>"+deviceType+"</deviceType>"
		// +"<deviceUID>"+deviceString+"</deviceUID>"
		// +"<displayName>"+displayName+"</displayName>"
		// +"<drmType>PLAYS</drmType>"
		// +"<drmValue>100</drmValue>"
		// +"<newsItems>10</newsItems>"
		// +"<newsTimestamp>1317300123</newsTimestamp>"
		// +"<operator>"+operator+"</operator>"
		// +"<paymentEnabled>true</paymentEnabled>"
		// +"<paymentStatus>AWAITING_PAYMENT</paymentStatus>"
		// +"<paymentType>PSMS</paymentType>"
		// +"<phoneNumber>"+phoneNumber+"</phoneNumber>"
		// +"<status>EULA</status>"
		// +"<subBalance>0</subBalance>"
		// +"</user>"
		// +"</response>",responseBody);

		user = userService.findByNameAndCommunity(userName, communityName);
		assertEquals(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.getCode(), user.getUserStatusId());

		// paymentStatus=PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
		// assertEquals(PaymentStatusDao.getAWAITING_PAYMENT().getName(),
		// paymentStatus);

		// accountUpdateService.makePayment(user);
		createPendingPaymentJob.execute();

		lastPremiumUserPayment = findLastPremiumUserPayment(userId);

		assertNotNull(lastPremiumUserPayment);

		aHttpServletResponse = emulateMigSuccesCallback(lastPremiumUserPayment
				.getInternalTxCode());
		assertEquals(200, aHttpServletResponse.getStatus());

		lastPremiumUserPayment = entityService.findById(PremiumUserPayment.class, lastPremiumUserPayment.getI());

		// paymentStatus=PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
		// assertEquals(PaymentStatusDao.getOK().getName(), paymentStatus);

		aHttpServletResponse = acc_check(timestamp, userToken, userName,
				apiVersion, communityName, appVersion, null, null, null);

		responseBody = aHttpServletResponse.getContentAsString();

		assertNotNull(responseBody);

		final Map<String, Object> nameValueMap = new HashMap<String, Object>();
		nameValueMap.put(PaymentPolicy.Fields.operator.toString(),
				user.getOperator());
		nameValueMap.put(PaymentPolicy.Fields.communityId.toString(),
				CommunityDao.getMapAsNames().get(communityName).getId());
		// PaymentPolicy
		// paymentPolicy=entityService.findByProperties(PaymentPolicy.class,
		// nameValueMap);

		byte freeWeeks = 0;
		Promotion promotion = userDao.getActivePromotion(user.getUserGroupId());
		if (promotion != null)
			freeWeeks = promotion.getFreeWeeks();
		assertTrue(freeWeeks >= 0);
		AmountCurrencyWeeks amountCurrencyWeeks = userService.getUpdateAmountCurrencyWeeks(user);

		// int
		// expectedSubBlance=paymentPolicy.getSubweeks()+amountCurrencyWeeks.getWeeks();
		int expectedSubBlance = freeWeeks + amountCurrencyWeeks.getWeeks() - 1;// -1
																				// because
																				// it's
																				// a
																				// first
																				// login
																				// where
																				// status
																				// SUBSCRIBED

		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
						+ "<response>"
						+ "<user>"
						+ "<chartItems>40</chartItems>"
						+ "<chartTimestamp>1321452650</chartTimestamp>"
						+ "<deviceType>" + deviceType + "</deviceType>"
						+ "<deviceUID>" + deviceString + "</deviceUID>"
						+ "<displayName>" + displayName + "</displayName>"
						+ "<drmType>PLAYS</drmType>"
						+ "<drmValue>100</drmValue>"
						+ "<newsItems>10</newsItems>"
						+ "<newsTimestamp>1317300123</newsTimestamp>"
						+ "<operator>" + operator + "</operator>"
						+ "<paymentEnabled>true</paymentEnabled>"
						+ "<paymentStatus>OK</paymentStatus>"
						+ "<paymentType>PSMS</paymentType>"
						+ "<phoneNumber>" + phoneNumber + "</phoneNumber>"
						+ "<status>SUBSCRIBED</status>"
						+ "<subBalance>" + expectedSubBlance + "</subBalance>"
						+ "</user>"
						+ "</response>", responseBody);

		user = userService.findByNameAndCommunity(userName, communityName);
		assertEquals(UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());

		assertEquals(expectedSubBlance, user.getSubBalance());

		int nextSubPayment = user.getNextSubPayment();

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(user.getNextSubPayment() * 1000L);
		SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
		Date dateTime = calendar.getTime();

		LOGGER.info(String.valueOf(nextSubPayment));
		LOGGER.info(dateFormatLocal.format(dateTime));

		assertTrue((Utils.getEpochSeconds() < nextSubPayment) && (Utils.getEpochSeconds() + Utils.WEEK_SECONDS >= nextSubPayment));
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

	@Test
	public void test_GET_FILE_commnd() {
		try {
			String password = "zzz@z.com";
			String userName = "zzz@z.com";
			String timestamp = "1";
			String apiVersion = "V1.2";
			String communityName = "Now Music";
			String appVersion = "CNBETA";

			String storedToken = Utils.createStoredToken(userName, password);
			String userToken = Utils.createTimestampToken(storedToken, timestamp);

			UserRegInfo userRegInfo = new UserRegInfo();
			userRegInfo.setEmail(userName);
			userRegInfo.setStoredToken(storedToken);
			userRegInfo.setAppVersion(appVersion);
			userRegInfo.setDeviceType("ANDROID");
			userRegInfo.setCommunityName(communityName);
			userRegInfo.setDeviceString("Device 1");
			userRegInfo.setDisplayName("Nigel");
			userRegInfo.setPhoneNumber("07580381128");
			userRegInfo.setOperator(1);

			// String phoneNumber=userRegInfo.getPhoneNumber();
			// String operator=String.valueOf(userRegInfo.getOperator());

			registerPSMSUserToSubscridedStatus(userRegInfo, timestamp, userToken, appVersion);

			String id = "USAT21001886";
			String type = FileService.FileType.AUDIO.name();

			MockHttpServletResponse aHttpServletResponse = getFile(userName,
					timestamp, apiVersion, communityName, appVersion,
					userToken, id, type);

			assertEquals(200, aHttpServletResponse.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
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
	public void test_CHECK_PIN_command() throws Exception {
		// CL-2380: Incorrect status after resolve balance from track purchasing
		// http://jira.dev.now-technologies.mobi:8181/browse/CL-2380

		String password = "zzz@z.com";
		String userName = "zzz@z.com";
		String timestamp = "1";
		String apiVersion = "V1.2";
		String communityName = "Now Music";
		String appVersion = "CNBETA";
		String phoneNumber = "00447580381128";
		int operator = 1;

		String storedToken = Utils.createStoredToken(userName, password);
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

		UserRegInfo userRegInfo = new UserRegInfo();
		userRegInfo.setEmail(userName);
		userRegInfo.setStoredToken(storedToken);
		userRegInfo.setAppVersion(appVersion);
		userRegInfo.setDeviceType("ANDROID");
		userRegInfo.setCommunityName(communityName);
		userRegInfo.setDeviceString("Device 1");
		userRegInfo.setDisplayName("Nigel");
		userRegInfo.setPhoneNumber(phoneNumber);
		userRegInfo.setOperator(operator);

		registerPSMSUserToSubscridedStatus(userRegInfo, timestamp, userToken, apiVersion);

		User user = userService.findByNameAndCommunity(userName, communityName);
		final int userId = user.getId();

		user.setSubBalance((byte) 0);

		userService.updateUser(user);

		MockHttpServletResponse aHttpServletResponse = updatePhone(userName,
				timestamp, apiVersion, communityName, appVersion, phoneNumber,
				operator, userToken);

		assertEquals(200, aHttpServletResponse.getStatus());

		user = userService.findByNameAndCommunity(userName, communityName);

		String paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
		assertEquals(PaymentStatusDao.getPIN_PENDING().getName(), paymentStatus);

		aHttpServletResponse = checkPin(timestamp, userToken, apiVersion,
				userName, communityName, appVersion, user.getPin());

		assertEquals(200, aHttpServletResponse.getStatus());

		user = userService.findByNameAndCommunity(userName, communityName);

		paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
		assertEquals(PaymentStatusDao.getAWAITING_PAYMENT().getName(), paymentStatus);

		PremiumUserPayment lastPremiumUserPayment = findLastPremiumUserPayment(userId);
		assertNotNull(lastPremiumUserPayment);

		assertEquals(AppConstants.STATUS_PENDING, lastPremiumUserPayment.getStatus());

		paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
		assertEquals(PaymentStatusDao.getAWAITING_PSMS().getName(), paymentStatus);

		assertEquals(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());

		aHttpServletResponse = emulateMigSuccesCallback(lastPremiumUserPayment
				.getInternalTxCode());
		assertEquals(200, aHttpServletResponse.getStatus());

		lastPremiumUserPayment = entityService.findById(PremiumUserPayment.class, lastPremiumUserPayment.getI());

		assertEquals(AppConstants.STATUS_OK, lastPremiumUserPayment.getStatus());

		paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
		assertEquals(PaymentStatusDao.getOK().getName(), paymentStatus);

		user = userService.findByNameAndCommunity(userName, communityName);
		assertEquals(UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());

		AmountCurrencyWeeks amountCurrencyWeeks = userService.getUpdateAmountCurrencyWeeks(user);

		int expectedSubBlance = amountCurrencyWeeks.getWeeks();// -1 because
																// it's a first
																// login where
																// status
																// SUBSCRIBED
		assertEquals(expectedSubBlance, user.getSubBalance());
	}

	private PremiumUserPayment findLastPremiumUserPayment(final int userId) {
		Map<String, Object> fieldNameValueMap = new HashMap<String, Object>();
		fieldNameValueMap.put(PremiumUserPayment.Fields.userUID.toString(),
				userId);
		fieldNameValueMap.put(PremiumUserPayment.Fields.timestamp
				.toString(), new Object() {
			@Override
			public String toString() {
				return "(select max("
						+ PremiumUserPayment.Fields.timestamp.toString()
						+ ") from "
						+ PremiumUserPayment.class.getSimpleName()
						+ " m where m."
						+ PremiumUserPayment.Fields.userUID.toString()
						+ "=" + userId + ")";
			}
		});
		PremiumUserPayment lastPremiumUserPayment = entityService
				.findByProperties(PremiumUserPayment.class,
						fieldNameValueMap);
		return lastPremiumUserPayment;
	}

	private MockHttpServletResponse emulateMigSuccesCallback(
			String internalTxCode) throws ServletException,
			IOException {
		if (internalTxCode == null)
			throw new NullPointerException(
					"The parameter internalTxCode is null");

		MockHttpServletRequest httpServletRequest;
		MockHttpServletResponse aHttpServletResponse;
		aHttpServletResponse = new MockHttpServletResponse();
		httpServletRequest = new MockHttpServletRequest("GET",
				"/DRListener");
		httpServletRequest.addParameter("STATUSTYPE", "20");
		httpServletRequest.addParameter("GUID", "12345");
		httpServletRequest.addParameter("STATUS", "0");
		httpServletRequest.addParameter("MESSAGEID", internalTxCode);

		dispatcherServlet.service(httpServletRequest, aHttpServletResponse);
		return aHttpServletResponse;
	}

	private MockHttpServletResponse updatePhone(String userName,
			String timestamp, String apiVersion, String communityName,
			String appVersion, String phoneNumber, int operator,
			String userToken) throws ServletException, IOException {
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
		if (phoneNumber == null)
			throw new NullPointerException("The parameter phoneNumber is null");

		MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/UPDATE_PHONE");
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

	@Test
	public void test_EntityControllerHandleException() {
		try {
			acc_check("", "", "testUser", "", "testCommunity", "", null, null, null);
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

	@Test(expected = ServiceException.class)
	public void test_setFreeSmsURL() {
	}

	@Test(expected = ServiceException.class)
	public void test_setPremiumSmsURL() {
	}

	@Test
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

	@Test
	public void test_REGISTER_USER_comandWithoutPaymentDetails() throws Exception {
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

		UserRegInfo userRegInfo = new UserRegInfo();
		userRegInfo.setEmail(userName);
		userRegInfo.setStoredToken(storedToken);
		userRegInfo.setAppVersion(appVersion);
		userRegInfo.setDeviceType(deviceType);
		userRegInfo.setCommunityName(communityName);
		userRegInfo.setDeviceString(deviceString);
		userRegInfo.setDisplayName(displayName);
		userRegInfo.setPhoneNumber(phoneNumber);
		userRegInfo.setOperator(operator);
		userRegInfo.setPromotionCode("promo");

		registerUnknownPaymentTypeUser(userRegInfo, timestamp, userToken, apiVersion);
	}

	@Test
	public void test_REGISTER_USER_comandWithWrongGeoLocation() throws Exception {
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

		UserRegInfo userRegInfo = new UserRegInfo();
		userRegInfo.setEmail(userName);
		userRegInfo.setStoredToken(storedToken);
		userRegInfo.setAppVersion(appVersion);
		userRegInfo.setDeviceType(deviceType);
		userRegInfo.setCommunityName(communityName);
		userRegInfo.setDeviceString(deviceString);
		userRegInfo.setDisplayName(displayName);
		userRegInfo.setPhoneNumber(phoneNumber);
		userRegInfo.setOperator(operator);
		userRegInfo.setPromotionCode("promo");

		registerUnknownPaymentTypeUser(userRegInfo, timestamp, userToken, apiVersion);
	}

	@Test
	public void test_updatePaymentDetailsPSMS() throws ServletException, IOException {

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

		UserRegInfo userRegInfo = new UserRegInfo();
		userRegInfo.setEmail(userName);
		userRegInfo.setStoredToken(storedToken);
		userRegInfo.setAppVersion(appVersion);
		userRegInfo.setDeviceType(deviceType);
		userRegInfo.setCommunityName(communityName);
		userRegInfo.setDeviceString(deviceString);
		userRegInfo.setDisplayName(displayName);
		userRegInfo.setPhoneNumber(phoneNumber);
		userRegInfo.setOperator(operator);
		userRegInfo.setPaymentType(UserRegInfo.PaymentType.UNKNOWN);
		userRegInfo.setPromotionCode("promo");

		registerUnknownPaymentTypeUser(userRegInfo, timestamp, userToken, apiVersion);

		// Set user in SUBSCRIBED | NULL
		User user = userDao.findByNameAndCommunity(userName, communityName);
		user.setStatus(UserStatusDao.getSubscribedUserStatus());
		user.setPaymentStatus(PaymentStatus.NULL_CODE);
		userService.updateUser(user);

		String aBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<userRegInfo>"
				+ "<phoneNumber>" + phoneNumber + "</phoneNumber>"
				+ "<operator>" + operator + "</operator>"
				+ "<paymentType>" + UserRegInfo.PaymentType.PREMIUM_USER + "</paymentType>"
				+ "<cardBillingAddress>88</cardBillingAddress>"
				+ "<cardBillingCity>London</cardBillingCity>"
				+ "<cardBillingCountry>GB</cardBillingCountry>"
				+ "<cardCv2>123</cardCv2>"
				+ "<cardIssueNumber></cardIssueNumber>"
				+ "<cardHolderFirstName>John</cardHolderFirstName>"
				+ "<cardHolderLastName>Smith</cardHolderLastName>"
				+ "<cardBillingPostCode>412</cardBillingPostCode>"
				+ "<cardStartMonth>1</cardStartMonth>"
				+ "<cardStartYear>2011</cardStartYear>"
				+ "<cardExpirationMonth>1</cardExpirationMonth>"
				+ "<cardExpirationYear>2012</cardExpirationYear>"
				+ "<cardNumber>4929000000006</cardNumber>"
				+ "<cardType>" + UserRegInfo.CardType.VISA + "</cardType>" +
				"</userRegInfo>";

		updatePaymentDetails(userName, timestamp, apiVersion, communityName, appVersion, userToken, aBody);

		MockHttpServletResponse httpServletResponse = checkPin(timestamp, userToken, apiVersion, userName, communityName, appVersion, user.getPin());

		assertEquals(200, httpServletResponse.getStatus());

		user = userDao.findByNameAndCommunity(userName, communityName);
		assertEquals(PaymentStatusDao.getAWAITING_PAYMENT().getId(), user.getPaymentStatus());

		PremiumUserPayment lastPremiumUserPayment = findLastPremiumUserPayment(user.getId());

		MockHttpServletResponse aHttpServletResponse = emulateMigSuccesCallback(lastPremiumUserPayment
				.getInternalTxCode());

		assertEquals(200, aHttpServletResponse.getStatus());

		user = userDao.findByNameAndCommunity(userName, communityName);

		assertNotSame(0, user.getSubBalance());

	}

	@Test
	public void test_updatePaymentDetailsCreditCard() throws ServletException, IOException {
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
		int operator = 0;

		String storedToken = Utils.createStoredToken(userName, password);
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

		UserRegInfo userRegInfo = new UserRegInfo();
		userRegInfo.setEmail(userName);
		userRegInfo.setStoredToken(storedToken);
		userRegInfo.setAppVersion(appVersion);
		userRegInfo.setDeviceType(deviceType);
		userRegInfo.setCommunityName(communityName);
		userRegInfo.setDeviceString(deviceString);
		userRegInfo.setDisplayName(displayName);
		userRegInfo.setPhoneNumber(phoneNumber);
		userRegInfo.setOperator(operator);
		userRegInfo.setPaymentType(UserRegInfo.PaymentType.UNKNOWN);
		userRegInfo.setPromotionCode("promo");

		registerUnknownPaymentTypeUser(userRegInfo, timestamp, userToken, apiVersion);

		// Set user in SUBSCRIBED | NULL
		User user = userDao.findByNameAndCommunity(userName, communityName);
		user.setStatus(UserStatusDao.getSubscribedUserStatus());
		user.setPaymentStatus(PaymentStatus.NULL_CODE);
		userService.updateUser(user);

		String aBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<userRegInfo>"
				+ "<phoneNumber>" + phoneNumber + "</phoneNumber>"
				+ "<operator>" + operator + "</operator>"
				+ "<paymentType>" + UserRegInfo.PaymentType.CREDIT_CARD + "</paymentType>"
				+ "<cardBillingAddress>88</cardBillingAddress>"
				+ "<cardBillingCity>London</cardBillingCity>"
				+ "<cardBillingCountry>GB</cardBillingCountry>"
				+ "<cardCv2>123</cardCv2>"
				+ "<cardIssueNumber></cardIssueNumber>"
				+ "<cardHolderFirstName>John</cardHolderFirstName>"
				+ "<cardHolderLastName>Smith</cardHolderLastName>"
				+ "<cardBillingPostCode>412</cardBillingPostCode>"
				+ "<cardStartMonth>1</cardStartMonth>"
				+ "<cardStartYear>2011</cardStartYear>"
				+ "<cardExpirationMonth>1</cardExpirationMonth>"
				+ "<cardExpirationYear>2012</cardExpirationYear>"
				+ "<cardNumber>4929000000006</cardNumber>"
				+ "<cardType>" + UserRegInfo.CardType.VISA + "</cardType>" +
				"</userRegInfo>";

		updatePaymentDetails(userName, timestamp, apiVersion, communityName, appVersion, userToken, aBody);

		user = userDao.findByNameAndCommunity(userName, communityName);

		assertEquals(PaymentStatusDao.getAWAITING_PAYMENT().getId(), user.getPaymentStatus());

		user = userDao.findByNameAndCommunity(userName, communityName);

		assertNotSame(0, user.getSubBalance());
	}

	@Test
	public void test_makePayment() {
		try {
			String password = "zzz@z.com";
			String userName = "zzz@z.com";
			String timestamp = "1";
			String apiVersion = "V1.2";
			String communityName = "Now Music";
			String appVersion = "CNBETA";
			String phoneNumber = "00447580381128";
			int operator = 1;

			String storedToken = Utils.createStoredToken(userName, password);
			String userToken = Utils.createTimestampToken(storedToken, timestamp);

			UserRegInfo userRegInfo = new UserRegInfo();
			userRegInfo.setEmail(userName);
			userRegInfo.setStoredToken(storedToken);
			userRegInfo.setAppVersion(appVersion);
			userRegInfo.setDeviceType("ANDROID");
			userRegInfo.setCommunityName(communityName);
			userRegInfo.setDeviceString("Device 1");
			userRegInfo.setDisplayName("Nigel");
			userRegInfo.setPhoneNumber(phoneNumber);
			userRegInfo.setOperator(operator);

			registerPSMSUserToSubscridedStatus(userRegInfo, timestamp, userToken, apiVersion);
			User user = userService.findByNameAndCommunity(userName, communityName);

			user.setStatus(UserStatusDao.getEulaUserStatus());
			user.setPaymentStatus(PaymentStatusDao.getNULL().getId());
			entityService.updateEntity(user);

			user = userService.findByNameAndCommunity(userName, communityName);

			assertEquals(UserStatus.EULA.getCode(), user.getUserStatusId());
			assertEquals(PaymentStatusDao.getNULL().getId(), user.getPaymentStatus());

			user = userService.findByNameAndCommunity(userName, communityName);

			assertEquals(UserStatus.EULA.getCode(), user.getUserStatusId());
			assertEquals(PaymentStatusDao.getAWAITING_PSMS().getId(), user.getPaymentStatus());
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

	public void registerUnknownPaymentTypeUser(UserRegInfo userRegInfo, String timestamp, String userToken, String apiVersion) throws ServletException, IOException {
		if (userRegInfo == null)
			throw new NullPointerException("The parameter userRegInfo is null");
		if (timestamp == null)
			throw new NullPointerException("The parameter timestamp is null");
		if (userToken == null)
			throw new NullPointerException("The parameter userToken is null");
		if (apiVersion == null)
			throw new NullPointerException("The parameter apiVersion is null");

		String userName = userRegInfo.getEmail();
		String communityName = userRegInfo.getCommunityName();
		String appVersion = userRegInfo.getAppVersion();
		String deviceType = userRegInfo.getDeviceType();
		String displayName = userRegInfo.getDisplayName();
		String deviceString = userRegInfo.getDeviceString();
		String phoneNumber = userRegInfo.getPhoneNumber();
		String operator = String.valueOf(userRegInfo.getOperator());
		String storedToken = userRegInfo.getStoredToken();
		String promoCode = userRegInfo.getPromotionCode();

		if (userName == null)
			throw new NullPointerException("The parameter userName is null");
		if (communityName == null)
			throw new NullPointerException("The parameter communityName is null");
		if (appVersion == null)
			throw new NullPointerException("The parameter appVersion is null");
		if (deviceType == null)
			throw new NullPointerException("The parameter deviceType is null");
		if (displayName == null)
			throw new NullPointerException("The parameter displayName is null");
		if (deviceString == null)
			throw new NullPointerException("The parameter deviceString is null");
		if (phoneNumber == null)
			throw new NullPointerException("The parameter phoneNumber is null");
		if (promoCode == null)
			throw new NullPointerException("The parameter promotionCode is null");

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

		MockHttpServletResponse aHttpServletResponse = registerUser(aBody, "2.24.0.1");

		assertEquals(200, aHttpServletResponse.getStatus());

		User user = userService.findByNameAndCommunity(userName, communityName);
		final int userId = user.getId();

		assertEquals(UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());
		assertFalse(user.isPaymentEnabled());

		PremiumUserPayment lastPremiumUserPayment = findLastPremiumUserPayment(userId);
		assertNull(lastPremiumUserPayment);

		String paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
		assertEquals(PaymentStatusDao.getNULL().getName(), paymentStatus);
	}

	@Test
	public void test_PSMSUser_PromoCodePromotion_Success() throws Exception {
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
		String promotionCode = "promo";

		String storedToken = Utils.createStoredToken(userName, password);
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

		UserRegInfo userRegInfo = new UserRegInfo();
		userRegInfo.setEmail(userName);
		userRegInfo.setStoredToken(storedToken);
		userRegInfo.setAppVersion(appVersion);
		userRegInfo.setDeviceType(deviceType);
		userRegInfo.setCommunityName(communityName);
		userRegInfo.setDeviceString(deviceString);
		userRegInfo.setDisplayName(displayName);
		userRegInfo.setPhoneNumber(phoneNumber);
		userRegInfo.setOperator(operator);
		userRegInfo.setPromotionCode(promotionCode);

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
				+ "<displayName>" + displayName + "</displayName>"
				+ "<postCode>null</postCode>"
				+ "<paymentType>" + UserRegInfo.PaymentType.UNKNOWN + "</paymentType>"
				+ "<storedToken>" + storedToken + "</storedToken>"
				+ "<promotionCode>" + promotionCode + "</promotionCode>"
				+ "</userRegInfo>";

		LOGGER.info(aBody);

		int timeBeforeRegistration = Utils.getEpochSeconds();

		MockHttpServletResponse mockHttpServletResponse = registerUser(aBody, "2.24.0.1");

		assertEquals(200, mockHttpServletResponse.getStatus());

		User user = userService.findByNameAndCommunity(userName, communityName);
		final int userId = user.getId();

		assertEquals(UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());
		assertFalse(user.isPaymentEnabled());

		assertEquals(0, user.getSubBalance());

		int nextSubPayment = user.getNextSubPayment();
		int twoWeeks = 2 * Utils.WEEK_SECONDS;

		assertTrue(nextSubPayment >= timeBeforeRegistration
				+ twoWeeks
				&& nextSubPayment <= Utils.getEpochSeconds()
						+ twoWeeks);

		PremiumUserPayment lastPremiumUserPayment = findLastPremiumUserPayment(userId);
		assertNull(lastPremiumUserPayment);

		String paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
		assertEquals(PaymentStatusDao.getNULL().getName(), paymentStatus);

		List<AccountLog> accountLogs = entityService.findListByProperty(AccountLog.class, AccountLog.Fields.userId.toString(), userId);

		assertNotNull(accountLogs);
		assertEquals(1, accountLogs.size());

		AccountLog accountLog = accountLogs.get(0);
		assertNotNull(accountLog);
		assertEquals(TransactionType.PROMOTION_BY_PROMO_CODE_APPLIED, accountLog.getTransactionType());
		assertEquals(0, accountLog.getBalanceAfter());

		user = userService.findByNameAndCommunity(userName, communityName);
		assertEquals(UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());

		mockHttpServletResponse = acc_check(timestamp, userToken, userName, apiVersion, communityName, appVersion, null, null, null);
		assertEquals(200, mockHttpServletResponse.getStatus());

		String responseBody = mockHttpServletResponse.getContentAsString();

		assertNotNull(responseBody);

		int expectedSubBlance = 0;

		assertEquals(responseBody,
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
						+ "<response>"
						+ "<user>"
						+ "<chartItems>40</chartItems>"
						+ "<chartTimestamp>1317302136</chartTimestamp>"
						+ "<deviceType>" + deviceType + "</deviceType>"
						+ "<deviceUID>" + deviceString + "</deviceUID>"
						+ "<displayName>" + displayName + "</displayName>"
						+ "<drmValue>0</drmValue>"
						+ "<newsItems>10</newsItems>"
						+ "<newsTimestamp>1317300123</newsTimestamp>"
						+ "<operator>0</operator>"
						+ "<paymentEnabled>false</paymentEnabled>"
						+ "<paymentStatus>NULL</paymentStatus>"
						+ "<paymentType>UNKNOWN</paymentType>"
						+ "<phoneNumber></phoneNumber>"
						+ "<status>SUBSCRIBED</status>"
						+ "<subBalance>" + expectedSubBlance + "</subBalance>"
						+ "</user>"
						+ "</response>");

		user = userService.findByNameAndCommunity(userName, communityName);
		assertEquals(UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());

		assertEquals(expectedSubBlance, user.getSubBalance());

		aBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<userRegInfo>"
				+ "<phoneNumber>" + phoneNumber + "</phoneNumber>"
				+ "<operator>" + operator + "</operator>"
				+ "<paymentType>" + UserRegInfo.PaymentType.PREMIUM_USER + "</paymentType>"
				+ "<cardBillingAddress>88</cardBillingAddress>"
				+ "<cardBillingCity>London</cardBillingCity>"
				+ "<cardBillingCountry>GB</cardBillingCountry>"
				+ "<cardCv2>123</cardCv2>"
				+ "<cardIssueNumber></cardIssueNumber>"
				+ "<cardHolderFirstName>John</cardHolderFirstName>"
				+ "<cardHolderLastName>Smith</cardHolderLastName>"
				+ "<cardBillingPostCode>412</cardBillingPostCode>"
				+ "<cardStartMonth>1</cardStartMonth>"
				+ "<cardStartYear>2011</cardStartYear>"
				+ "<cardExpirationMonth>1</cardExpirationMonth>"
				+ "<cardExpirationYear>2012</cardExpirationYear>"
				+ "<cardNumber>4929000000006</cardNumber>"
				+ "<cardType>" + UserRegInfo.CardType.VISA + "</cardType>" +
				"</userRegInfo>";

		LOGGER.info(aBody);

		mockHttpServletResponse = updatePaymentDetails(userName, timestamp, apiVersion, communityName, appVersion, userToken, aBody);
		assertEquals(200, mockHttpServletResponse.getStatus());

		user = userDao.findByNameAndCommunity(userName, communityName);

		assertEquals(UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());
		assertEquals(PaymentStatusDao.getPIN_PENDING().getId(), user.getPaymentStatus());

		assertEquals(expectedSubBlance, user.getSubBalance());

		mockHttpServletResponse = checkPin(timestamp, userToken, apiVersion, userName, communityName, appVersion, user.getPin());

		assertEquals(200, mockHttpServletResponse.getStatus());

		user = userDao.findByNameAndCommunity(userName, communityName);

		assertEquals(UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());
		assertEquals(PaymentStatusDao.getAWAITING_PAYMENT().getId(), user.getPaymentStatus());

		assertEquals(expectedSubBlance, user.getSubBalance());

		assertEquals(UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());
		assertEquals(PaymentStatusDao.getAWAITING_PSMS().getId(), user.getPaymentStatus());

		lastPremiumUserPayment = findLastPremiumUserPayment(user.getId());

		mockHttpServletResponse = emulateMigSuccesCallback(lastPremiumUserPayment
				.getInternalTxCode());

		assertEquals(200, mockHttpServletResponse.getStatus());

		assertEquals(UserStatus.SUBSCRIBED.getCode(), user.getUserStatusId());
		assertEquals(PaymentStatusDao.getOK().getId(), user.getPaymentStatus());

		byte freeWeeks = 0;
		Promotion promotion = userDao.getActivePromotion(user.getUserGroupId());
		if (promotion != null)
			freeWeeks = promotion.getFreeWeeks();
		assertTrue(freeWeeks >= 0);

		AmountCurrencyWeeks amountCurrencyWeeks = userService.getUpdateAmountCurrencyWeeks(user);

		expectedSubBlance = freeWeeks + amountCurrencyWeeks.getWeeks();
		assertEquals(expectedSubBlance, user.getSubBalance());

		nextSubPayment = user.getNextSubPayment();

		assertTrue(nextSubPayment >= timeBeforeRegistration
				+ twoWeeks
				&& nextSubPayment <= Utils.getEpochSeconds()
						+ twoWeeks);
	}

	public void registerPayPalPaymentTypeUser(UserRegInfo userRegInfo, String timestamp, String userToken, String apiVersion) throws ServletException, IOException {
		if (userRegInfo == null)
			throw new NullPointerException("The parameter userRegInfo is null");
		if (timestamp == null)
			throw new NullPointerException("The parameter timestamp is null");
		if (userToken == null)
			throw new NullPointerException("The parameter userToken is null");
		if (apiVersion == null)
			throw new NullPointerException("The parameter apiVersion is null");

		String userName = userRegInfo.getEmail();
		String communityName = userRegInfo.getCommunityName();
		String appVersion = userRegInfo.getAppVersion();
		String deviceType = userRegInfo.getDeviceType();
		String displayName = userRegInfo.getDisplayName();
		String deviceString = userRegInfo.getDeviceString();
		String phoneNumber = userRegInfo.getPhoneNumber();
		String operator = String.valueOf(userRegInfo.getOperator());
		String storedToken = userRegInfo.getStoredToken();

		if (userName == null)
			throw new NullPointerException("The parameter userName is null");
		if (communityName == null)
			throw new NullPointerException("The parameter communityName is null");
		if (appVersion == null)
			throw new NullPointerException("The parameter appVersion is null");
		if (deviceType == null)
			throw new NullPointerException("The parameter deviceType is null");
		if (displayName == null)
			throw new NullPointerException("The parameter displayName is null");
		if (deviceString == null)
			throw new NullPointerException("The parameter deviceString is null");
		if (phoneNumber == null)
			throw new NullPointerException("The parameter phoneNumber is null");

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
				+ "<paymentType>" + UserRegInfo.PaymentType.PAY_PAL + "</paymentType>"
				+ "<storedToken>" + storedToken + "</storedToken>"
				+ "</userRegInfo>";

		MockHttpServletResponse aHttpServletResponse = registerUser(aBody, "2.24.0.1");

		assertEquals(200, aHttpServletResponse.getStatus());

		User user = userService.findByNameAndCommunity(userName, communityName);
		final int userId = user.getId();

		assertEquals(UserStatus.EULA.getCode(), user.getUserStatusId());
		assertTrue(user.isPaymentEnabled());

		PremiumUserPayment lastPremiumUserPayment = findLastPremiumUserPayment(userId);
		assertNull(lastPremiumUserPayment);

		String paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
		assertEquals(PaymentStatusDao.getNULL().getName(), paymentStatus);
	}

	@Test
	public void test_PayPalService() {
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

			UserRegInfo userRegInfo = new UserRegInfo();
			userRegInfo.setEmail(userName);
			userRegInfo.setStoredToken(storedToken);
			userRegInfo.setAppVersion(appVersion);
			userRegInfo.setDeviceType(deviceType);
			userRegInfo.setCommunityName(communityName);
			userRegInfo.setDeviceString(deviceString);
			userRegInfo.setDisplayName(displayName);
			userRegInfo.setPhoneNumber(phoneNumber);
			userRegInfo.setOperator(operator);
			userRegInfo.setPaymentType(UserRegInfo.PaymentType.PAY_PAL);

			registerPayPalPaymentTypeUser(userRegInfo, timestamp, userToken, apiVersion);

			User user = userDao.findByNameAndCommunity(userName, communityName);

			String billingAgreementDescription = "Test";

			// HashMap<String, String> hashMap=null;
			// while (hashMap==null||hashMap.isEmpty()||hashMap.){
			// try{
			// hashMap = payPalService.getExpressCheckoutDetails(token);
			// }catch (Exception e) {
			// LOGGER.error(e.getMessage(), e);
			// }
			// Thread.sleep(1000L);
			// }
			//
			// LOGGER.info("");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}

	}

	@Test
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
			assertFalse(user.isPaymentEnabled());

			String paymentStatus = PaymentStatusDao.getMapIdAsKey().get(user.getPaymentStatus()).getName();
			assertEquals(PaymentStatusDao.getNULL().getName(), paymentStatus);

			mockHttpServletResponse = acc_check(timestamp, userToken, userName,
					apiVersion, communityName, appVersion, null, null, null);
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
	public void testSET_DRM() {
		try {
			String password = "zzz@z.com";
			String userName = "zzz1@z.com";
			String timestamp = "1";
			String apiVersion = "V1.2";
			String communityName = "Now Music";
			String communityUrl = "nowtop40";
			String appVersion = "CNBETA";

			String storedToken = Utils.createStoredToken(userName, password);
			String userToken = Utils.createTimestampToken(storedToken, timestamp);

			UserRegInfo userRegInfo = new UserRegInfo();
			userRegInfo.setEmail(userName);
			userRegInfo.setStoredToken(storedToken);
			userRegInfo.setAppVersion(appVersion);
			userRegInfo.setDeviceType("ANDROID");
			userRegInfo.setCommunityName(communityName);
			userRegInfo.setDeviceString("Device 1");
			userRegInfo.setDisplayName("Nigel");
			userRegInfo.setPhoneNumber("07580381128");
			userRegInfo.setOperator(1);

			registerPSMSUserToSubscridedStatus(userRegInfo, timestamp, userToken, appVersion);

			getChart(userName, timestamp, apiVersion, communityUrl, communityName, appVersion, userToken, storedToken);

			String isrc = "USUM71100721";
			byte drmValue = 5;

			MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(
					"POST", "/SET_DRM");
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.addHeader("Content-Length", "0");
			httpServletRequest.setRemoteAddr("2.24.0.1");

			httpServletRequest.addParameter(isrc, String.valueOf(drmValue));
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
					+ "<drm>"
					+ "<item>"
					+ "<drmType>PLAYS</drmType>"
					+ "<drmValue>5</drmValue>"
					+ "<mediaUID>USUM71100721</mediaUID>"
					+ "</item>"
					+ "</drm>"
					+ "</response>";
			assertEquals(expected, aHttpServletResponse.getContentAsString());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testSET_PASSWORD() throws Exception {
		try {
			String password = "zzz@z.com";
			String userName = "zzz@z.com";
			String timestamp = "1";
			String apiVersion = "V1.2";
			String communityName = "Now Music";
			String appVersion = "CNBETA";

			String storedToken = Utils.createStoredToken(userName, password);
			String userToken = Utils.createTimestampToken(storedToken, timestamp);

			UserRegInfo userRegInfo = new UserRegInfo();
			userRegInfo.setEmail(userName);
			userRegInfo.setStoredToken(storedToken);
			userRegInfo.setAppVersion(appVersion);
			userRegInfo.setDeviceType("ANDROID");
			userRegInfo.setCommunityName(communityName);
			userRegInfo.setDeviceString("Device 1");
			userRegInfo.setDisplayName("Nigel");
			userRegInfo.setPhoneNumber("07580381128");
			userRegInfo.setOperator(1);

			registerPSMSUserToSubscridedStatus(userRegInfo, timestamp, userToken, appVersion);

			String newPassword = "newPassword";
			String newStoredToken = Utils.createStoredToken(userName, newPassword);

			MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(
					"POST", "/SET_PASSWORD");
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.addHeader("Content-Length", "0");
			httpServletRequest.setRemoteAddr("2.24.0.1");

			httpServletRequest.addParameter("NEW_TOKEN", newStoredToken);
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
					+ "<setPassword>OK</setPassword>"
					+ "</response>";

			assertEquals(expected, aHttpServletResponse.getContentAsString());

			User user = userService.findByNameAndCommunity(userName, communityName);
			assertNotNull(user);

			assertEquals(newStoredToken, user.getToken());

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testSET_DEVICE() throws Exception {
		try {
			String password = "zzz@z.com";
			String userName = "zzz@z.com";
			String timestamp = "1";
			String apiVersion = "V1.2";
			String communityName = "Now Music";
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

			registerPSMSUserToSubscridedStatus(userRegInfo, timestamp, userToken, appVersion);

			MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(
					"POST", "/SET_DEVICE");
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.addHeader("Content-Length", "0");
			httpServletRequest.setRemoteAddr("2.24.0.1");

			User user = userService.findByNameAndCommunity(userName, communityName);
			assertNotNull(user);
			assertEquals(DeviceTypeDao.findIdByName(deviceType), user.getDeviceTypeId());
			assertEquals(deviceString, user.getDeviceString());

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
	// TODO fix test BUY_TRACK for new API
	public void testBUY_TRACK() throws Exception {
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

			getChart(userName, timestamp, apiVersion, comunityUrl, communityName, appVersion, userToken, storedToken);

			String mediaIsrc = "USUM71100721";
			String type = FileService.FileType.HEADER.name();
			getFile(userName, timestamp, apiVersion, communityName, appVersion, userToken, mediaIsrc, type);

			MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(
					"POST", "/BUY_TRACK");
			httpServletRequest.addHeader("Content-Type", "text/xml");
			httpServletRequest.addHeader("Content-Length", "0");
			httpServletRequest.setRemoteAddr("2.24.0.1");

			httpServletRequest.addParameter("MEDIA_UID", mediaIsrc);
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
			
			getChart(userName, timestamp, apiVersion, communityUrl, communityName, appVersion, userToken, storedToken);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testGET_NEWS() throws Exception {
		try {
			String password = "zzz@z.com";
			String userName = "zzz@z.com";
			String timestamp = "2011_12_26_07_04_23";
			String apiVersion = "V1.2";
			String communityName = "Now Music";
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

			// registerPSMSUserToSubscridedStatus(userRegInfo, timestamp,
			// userToken, appVersion);

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

			MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
			MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(
					"POST", "/GET_NEWS");
			httpServletRequest.setPathInfo("/GET_NEWS");
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

			final String contentAsString = aHttpServletResponse.getContentAsString();
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
	private void getChart(String userName, String timestamp, String apiVersion, String communityUrl, String communityName, String appVersion, String userToken, String storedToken)
			throws ServletException, IOException, UnsupportedEncodingException, SAXException {

		String requestURI = "/" + communityUrl + "/" + apiVersion + "/GET_CHART";
		MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
		
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(
				"POST", requestURI);
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.addHeader("Content-Length", "0");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/GET_CHART");

		httpServletRequest.addParameter("APP_VERSION", appVersion);
		httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
		httpServletRequest.addParameter("API_VERSION", apiVersion);
		httpServletRequest.addParameter("USER_NAME", userName);
		httpServletRequest.addParameter("USER_TOKEN", userToken);
		httpServletRequest.addParameter("TIMESTAMP", timestamp);

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
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><user><activation>REGISTERED</activation><chartItems>21</chartItems><chartTimestamp>1321452650</chartTimestamp><deviceType>ANDROID</deviceType><deviceUID></deviceUID><displayName></displayName><drmType>PLAYS</drmType><drmValue>100</drmValue><freeTrial>false</freeTrial><fullyRegistred>true</fullyRegistred><hasOffers>false</hasOffers><hasPotentialPromoCodePromotion>false</hasPotentialPromoCodePromotion><newsItems>10</newsItems><newsTimestamp>1317300123</newsTimestamp><nextSubPaymentSeconds>0</nextSubPaymentSeconds><operator>1</operator><paymentEnabled>false</paymentEnabled><paymentStatus>NULL</paymentStatus><paymentType>UNKNOWN</paymentType><phoneNumber></phoneNumber><promotedDevice>false</promotedDevice><promotedWeeks>0</promotedWeeks><rememberMeToken>enp6QHouY29tOjEzNjE4MTE0NjQyMTA6ZjNmOThiMTg5MWYxNTI0ZDFmNGU0NjRkZjY2ZGRiNGM</rememberMeToken><status>LIMITED</status><subBalance>0</subBalance><timeOfMovingToLimitedStatusSeconds>0</timeOfMovingToLimitedStatusSeconds><userName>zzz@z.com</userName><userToken>c83e65384c92a922a7ff3e91e4c92c35</userToken><oAuthProvider>NONE</oAuthProvider></user><chart><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>5</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>1</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>5</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>1</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>145</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>2</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>145</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>2</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>38</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>3</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>38</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>3</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>44</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>4</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>44</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>4</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1285</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>5</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1285</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>5</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>436</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>6</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>436</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>6</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>44</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>7</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>44</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>7</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>2</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>8</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>2</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>8</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>9</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>9</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>33</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>10</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>33</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>10</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>8888</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>11</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>8888</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>11</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>555</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>12</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>555</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>12</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>2</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>13</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>2</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>13</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>14</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>14</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>6</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>15</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><chartDetailVersion>6</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>15</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>925</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>16</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>925</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>16</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>3</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>17</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>3</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>17</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>18</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>1</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>18</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>11</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>19</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>11</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>19</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>111</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>true</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>20</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>111</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>true</isArtistUrl><media>US-UM7-11-00061</media><playlistId>5</playlistId><position>20</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><bonusTrack><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>98</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>USAT21001886</media><playlistId>5</playlistId><position>21</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></bonusTrack><track><amazonUrl>http%3A%2F%2Fwww.amazon.com%2Fgp%2Fproduct%2F030758836X%2Fref%3Ds9_al_bw_g14_ir03%3Fpf_rd_m%3DATVPDKIKX0DER%26pf_rd_s%3Dcenter-4%26pf_rd_r%3D079680TPPVRZ8J4W5B6Z%26pf_rd_t%3D101%26pf_rd_p%3D1418176682%26pf_rd_i%3D5916596011</amazonUrl><artist>Lmfao/Lauren Bennett/Goonrock</artist><audioSize>1464070</audioSize><audioVersion>1</audioVersion><changePosition>DOWN</changePosition><channel>HEATSEEKER</channel><chartDetailVersion>98</chartDetailVersion><drmType>PLAYS</drmType><drmValue>100</drmValue><genre1>Default</genre1><genre2>Default</genre2><headerSize>162676</headerSize><headerVersion>666</headerVersion><imageLargeSize>41581</imageLargeSize><imageLargeVersion>2</imageLargeVersion><imageSmallSize>6125</imageSmallSize><imageSmallVersion>3</imageSmallVersion><info>LMFAO is an American electro hop duo that formed in 2006 in Los Angeles, California, consisting of rappers and DJs.</info><isArtistUrl>false</isArtistUrl><media>USAT21001886</media><playlistId>5</playlistId><position>21</position><previousPosition>24</previousPosition><title>Party Rock Anthem</title><trackSize>1626744</trackSize><iTunesUrl>http%3A%2F%2Fclkuk.tradedoubler.com%2Fclick%3Fp%3D23708%2526a%3D1997010%2526url%3Dhttp%3A%2F%2Fitunes.apple.com%2Fgb%2Falbum%2Fparty-rock-anthem-feat.-lauren%2Fid449838429%3Fi%3D449838654%2526uo%3D4%2526partnerId%3D2003</iTunesUrl></track><playlist><id>5</id><playlistTitle>Default Chart</playlistTitle><subtitle>Default Chart</subtitle></playlist></chart></response>";
		expected = expected.replaceAll("rememberMeToken.*/rememberMeToken", "rememberMeToken>"+rememberMeToken+"</rememberMeToken");
		expected = expected.replaceAll("userToken.*/userToken", "userToken>"+userToken+"</userToken");
		
		Diff diff = new Diff(expected, contentAsString);
		diff.overrideElementQualifier(new ChartElementQualifier());
		
		XMLAssert.assertXMLEqual(diff, true);
	}

	@Test
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

			getChart(userName, timestamp, apiVersion, comunityUrl, communityName, appVersion, userToken, storedToken);

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

			String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
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
			
		    String expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><user><activation>REGISTERED</activation><chartItems>21</chartItems><chartTimestamp>1321452650</chartTimestamp><deviceType>ANDROID</deviceType><deviceUID></deviceUID><displayName></displayName><drmType>PLAYS</drmType><drmValue>100</drmValue><freeTrial>false</freeTrial><fullyRegistred>true</fullyRegistred><hasOffers>false</hasOffers><hasPotentialPromoCodePromotion>false</hasPotentialPromoCodePromotion><newsItems>10</newsItems><newsTimestamp>1317300123</newsTimestamp><nextSubPaymentSeconds>0</nextSubPaymentSeconds><operator>1</operator><paymentEnabled>false</paymentEnabled><paymentStatus>NULL</paymentStatus><paymentType>UNKNOWN</paymentType><phoneNumber></phoneNumber><promotedDevice>false</promotedDevice><promotedWeeks>0</promotedWeeks><rememberMeToken>"+rememberMeToken+"</rememberMeToken><status>LIMITED</status><subBalance>0</subBalance><timeOfMovingToLimitedStatusSeconds>0</timeOfMovingToLimitedStatusSeconds><userName>zzz@z.com</userName><userToken>"+storedToken+"</userToken><oAuthProvider>NONE</oAuthProvider></user><news><item><body>Blue Ivy Carter, new daughter of Beyonce and JayZ, is already making chart history! Daddy Z features cute cries from little Princess B on his new track, Glory, making her the youngest person ever to appear in the Billboard chart!</body><detail>Blue Ivy Carter, new daughter of Beyonce and JayZ, is already making chart history! Daddy Z features cute cries from little Princess B on his new track, Glory, making her the youngest person ever to appear in the Billboard chart!</detail><i>1</i><id>11</id><messageType>NEWS</messageType><position>1</position><timestampMilis>1315686788000</timestampMilis></item><item><body>The Wanted would love to match the drama and musicality of the Take That shows on their upcoming tour. The boys start a 10 date US Tour on Jan 17th and then head back to the UK for their 1st show on February 15th at the Capital FM Arena in Nottingham.</body><detail>http://google.com.ua</detail><i>2</i><id>92</id><messageType>AD</messageType><position>2</position><timestampMilis>0</timestampMilis></item><item><body>The Wanted would love to match the drama and musicality of the Take That shows on their upcoming tour. The boys start a 10 date US Tour on Jan 17th and then head back to the UK for their 1st show on February 15th at the Capital FM Arena in Nottingham.</body><detail>The Wanted would love to match the drama and musicality of the Take That shows on their upcoming tour. The boys start a 10 date US Tour on Jan 17th and then head back to the UK for their 1st show on February 15th at the Capital FM Arena in Nottingham.</detail><i>2</i><id>12</id><messageFrequence>DAILY</messageFrequence><messageType>POPUP</messageType><position>2</position><timestampMilis>1315686788000</timestampMilis></item><item><body>Flipping back to music after producing new movie W.E., Madonna revealed that her 12th album will be called MDNA and will more than likely be released in March. The first single Gimme All Your Luvin will feature Nicki Minaj and MIA.</body><detail>Flipping back to music after producing new movie W.E., Madonna revealed that her 12th album will be called MDNA and will more than likely be released in March. The first single Gimme All Your Luvin will feature Nicki Minaj and MIA.</detail><i>4</i><id>14</id><messageType>NEWS</messageType><position>4</position><timestampMilis>1315686788000</timestampMilis></item><item><body>Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!</body><detail>Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!</detail><i>7</i><id>17</id><messageType>NEWS</messageType><position>7</position><timestampMilis>1315686788000</timestampMilis></item><item><body>Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!</body><detail>4XLS70CD</detail><i>7</i><id>97</id><messageType>AD</messageType><position>7</position><timestampMilis>0</timestampMilis></item><item><body>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</body><detail>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</detail><i>10</i><id>20</id><messageType>NEWS</messageType><position>10</position><timestampMilis>1315686788000</timestampMilis></item><item><body>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</body><detail>6XLS70CD</detail><i>10</i><id>100</id><messageType>AD</messageType><position>10</position><timestampMilis>0</timestampMilis></item></news></response>";
			
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
			
			//DetailedDiff detailedDiff = new DetailedDiff(diff);

			//List<Difference> allDifferences = detailedDiff.getAllDifferences();
			
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
			
		    expected="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><user><activation>REGISTERED</activation><chartItems>21</chartItems><chartTimestamp>1321452650</chartTimestamp><deviceType>ANDROID</deviceType><deviceUID></deviceUID><displayName></displayName><drmType>PLAYS</drmType><drmValue>100</drmValue><freeTrial>false</freeTrial><fullyRegistred>true</fullyRegistred><hasOffers>false</hasOffers><hasPotentialPromoCodePromotion>false</hasPotentialPromoCodePromotion><newsItems>10</newsItems><newsTimestamp>1317300123</newsTimestamp><nextSubPaymentSeconds>0</nextSubPaymentSeconds><operator>1</operator><paymentEnabled>false</paymentEnabled><paymentStatus>NULL</paymentStatus><paymentType>UNKNOWN</paymentType><phoneNumber></phoneNumber><promotedDevice>false</promotedDevice><promotedWeeks>0</promotedWeeks><rememberMeToken>"+rememberMeToken+"</rememberMeToken><status>LIMITED</status><subBalance>0</subBalance><timeOfMovingToLimitedStatusSeconds>0</timeOfMovingToLimitedStatusSeconds><userName>zzz@z.com</userName><userToken>"+storedToken+"</userToken><oAuthProvider>NONE</oAuthProvider></user><news><item><body>Blue Ivy Carter, new daughter of Beyonce and JayZ, is already making chart history! Daddy Z features cute cries from little Princess B on his new track, Glory, making her the youngest person ever to appear in the Billboard chart!</body><detail>Blue Ivy Carter, new daughter of Beyonce and JayZ, is already making chart history! Daddy Z features cute cries from little Princess B on his new track, Glory, making her the youngest person ever to appear in the Billboard chart!</detail><i>1</i><id>11</id><messageType>NEWS</messageType><position>1</position><timestampMilis>1315686788000</timestampMilis></item><item><body>The Wanted would love to match the drama and musicality of the Take That shows on their upcoming tour. The boys start a 10 date US Tour on Jan 17th and then head back to the UK for their 1st show on February 15th at the Capital FM Arena in Nottingham.</body><detail>The Wanted would love to match the drama and musicality of the Take That shows on their upcoming tour. The boys start a 10 date US Tour on Jan 17th and then head back to the UK for their 1st show on February 15th at the Capital FM Arena in Nottingham.</detail><i>2</i><id>12</id><messageFrequence>DAILY</messageFrequence><messageType>POPUP</messageType><position>2</position><timestampMilis>1315686788000</timestampMilis></item><item><body>Flipping back to music after producing new movie W.E., Madonna revealed that her 12th album will be called MDNA and will more than likely be released in March. The first single Gimme All Your Luvin will feature Nicki Minaj and MIA.</body><detail>Flipping back to music after producing new movie W.E., Madonna revealed that her 12th album will be called MDNA and will more than likely be released in March. The first single Gimme All Your Luvin will feature Nicki Minaj and MIA.</detail><i>4</i><id>14</id><messageType>NEWS</messageType><position>4</position><timestampMilis>1315686788000</timestampMilis></item><item><body>Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!</body><detail>Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!</detail><i>7</i><id>17</id><messageType>NEWS</messageType><position>7</position><timestampMilis>1315686788000</timestampMilis></item><item><body>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</body><detail>The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk</detail><i>10</i><id>20</id><messageType>NEWS</messageType><position>10</position><timestampMilis>1315686788000</timestampMilis></item></news></response>";
		    
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

			// PowerMockito.mockStatic(Utils.class);
			// PowerMockito.when(Utils.getRandomString(Mockito.anyInt())).thenReturn("value");

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
	public void testExceptionHandler() throws Exception {
		String password = "zzz@z.com";
		String userName = "zzz@z.com";
		String timestamp = "2011_12_26_07_04_23";
		String apiVersion = "V1.2";
		String communityName = "Now Music";
		String appVersion = "CNBETA";

		String deviceString = "Device 1";
		String deviceType = UserRegInfo.DeviceType.ANDROID;

		String phoneNumber = "00447580381128";
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

		// registerPSMSUserToSubscridedStatus(userRegInfo, timestamp, userToken,
		// appVersion);

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
				apiVersion, communityName, appVersion, null, null, null);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), mockHttpServletResponse
				.getStatus());

		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><errorMessage><displayMessage>user login/pass check failed for [zzz@z.com] username and community [Now Music]</displayMessage><errorCode>12</errorCode><message>user login/pass check failed for [zzz@z.com] username and community [Now Music]</message></errorMessage></response>",
				mockHttpServletResponse.getContentAsString());
	}

	@Test
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

			int timeBeforeRegistrationSeconds = Utils.getEpochSeconds();

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

			mockHttpServletResponse = acc_check(timestamp, userToken, userName, apiVersion, communityName, appVersion, deviceType, pushNotificationToken, null);
			assertEquals(200, mockHttpServletResponse.getStatus());

			mockHttpServletResponse = acc_check(timestamp, userToken, userName, apiVersion, communityName, appVersion, deviceType, pushNotificationToken, null);
			assertEquals(200, mockHttpServletResponse.getStatus());

			mockHttpServletResponse = acc_check(timestamp, userToken, userName, apiVersion, communityName, appVersion, UserRegInfo.DeviceType.ANDROID, pushNotificationToken, null);
			assertEquals(200, mockHttpServletResponse.getStatus());

			mockHttpServletResponse = acc_check(timestamp, userToken, userName, apiVersion, communityName, appVersion, UserRegInfo.DeviceType.ANDROID, pushNotificationToken, null);
			assertEquals(200, mockHttpServletResponse.getStatus());

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
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
		String communityName = "O2";
		String appVersion = "CNBETA";
		String phone = "07870111111";
		String timestamp = "2011_12_26_07_04_23";
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
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

		httpServletRequest = new MockHttpServletRequest("POST", "/O2/3.6/PHONE_NUMBER");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/O2/3.6/PHONE_NUMBER");

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
	}
	
	@Test
	public void testPHONE_NUMBER_O2_InvalidNumber() throws Exception {
		String userName = "zzz@z.com";
		String apiVersion = "V3.6";
		String communityName = "O2";
		String appVersion = "CNBETA";
		String phone = "07870111111dddd";
		String timestamp = "2011_12_26_07_04_23";
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
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

		httpServletRequest = new MockHttpServletRequest("POST", "/O2/3.6/PHONE_NUMBER");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/O2/3.6/PHONE_NUMBER");

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
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><errorMessage><displayMessage>Invalid phone number format</displayMessage><errorCode>601</errorCode><message>phone.number.invalid.format</message></errorMessage></response>", contentAsString);
	}
	
	@Test
	public void testPHONE_NUMBER_O2_ResendSMS() throws Exception {
		String userName = "zzz@z.com";
		String apiVersion = "V3.6";
		String communityName = "O2";
		String appVersion = "CNBETA";
		String phone = "07870111111";
		String timestamp = "2011_12_26_07_04_23";
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
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

		httpServletRequest = new MockHttpServletRequest("POST", "/O2/3.6/PHONE_NUMBER");
		httpServletRequest.addHeader("Content-Type", "text/xml");
		httpServletRequest.setRemoteAddr("2.24.0.1");
		httpServletRequest.setPathInfo("/O2/3.6/PHONE_NUMBER");

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
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><phoneActivation><activation>ENTERED_NUMBER</activation><phoneNumber>+447870111111</phoneNumber></phoneActivation></response>", contentAsString);
	}
}
