package mobi.nowtechnologies.server.transport.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
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
import org.xml.sax.InputSource;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@RunWith(Theories.class)
@ContextConfiguration(locations = {
		"classpath:transport-servlet-test.xml",
		"classpath:META-INF/soap.xml",
		"classpath:META-INF/service-test.xml",
		"classpath:META-INF/dao-test.xml",
		"classpath:META-INF/shared.xml" }, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "transport.EntityController")
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class UnsubscribeControllerIT {

	private static final XPathExpression PHONE_NUMBER_XPATHEXPRESSION;
	private static final XPathExpression OPERATOR_XPATHEXPRESSION;

	static {
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xPath = xPathFactory.newXPath();
		try {
			PHONE_NUMBER_XPATHEXPRESSION = xPath.compile("//member[contains(name,'MSISDN') or contains(name,'*MSISDN*') or contains(key,'MSISDN') or contains(key,'*MSISDN*')]/value");
			OPERATOR_XPATHEXPRESSION = xPath.compile("//member[contains(name,'NETWORK') or contains(name,'*NETWORK*') or contains(key,'NETWORK') or contains(key,'*NETWORK*')]/value");
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	@Autowired
	private DispatcherServlet dispatcherServlet;

	@Autowired
	private PaymentDetailsRepository paymentDetailsRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	protected CommunityResourceBundleMessageSource messageSource;
	
	private static int count=0;

	@DataPoints
	public static final String[] xml = new String[] { "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<methodCall>"
			+ "<methodName>XMLRPCHandler.getContent</methodName>"
			+ "<params>"
			+ "<param>"
			+ "<value>"
			+ "<struct>"
			+ "<member>"
			+ "<name>SHORTCODE</name>"
			+ "<value>8320100</value>"
			+ "</member>"
			+ "<member>"
			+ "<name>NETWORK</name>"
			+ "<value>O2</value>"
			+ "</member>"
			+ "<member>"
			+ "<name>MSISDN</name>"
			+ "<value>00447585927651</value>"
			+ "</member>"
			+ "<member>"
			+ "<name>msg</name>"
			+ "<value>Rpctest</value>"
			+ "</member>"
			+ "</struct>"
			+ "</value>"
			+ "</param>"
			+ "</params>"
			+ "</methodCall>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<methodCall>"
			+ "<methodName>XMLRPCHandler.getContent</methodName>"
			+ "<params>"
			+ "<param>"
			+ "<value>"
			+ "<struct>"
			+ "<member>"
			+ "<name>*SHORTCODE*</name>"
			+ "<value>*8320100*</value>"
			+ "</member>"
			+ "<member>"
			+ "<name>*NETWORK*</name>"
			+ "<value>*O2*</value>"
			+ "</member>"
			+ "<member>"
			+ "<name>*MSISDN*</name>"
			+ "<value>*00447585927651*</value>"
			+ "</member>"
			+ "<member>"
			+ "<name>*msg*</name>"
			+ "<value>*Rpctest*</value>"
			+ "</member>"
			+ "</struct>"
			+ "</value>"
			+ "</param>"
			+ "</params>"
			+ "</methodCall>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<methodCall>"
			+ "<methodName>XMLRPCHandler.getContent</methodName>"
			+ "<params>"
			+ "<param>"
			+ "<value>"
			+ "<struct>"
			+ "<member>"
			+ "<key>SHORTCODE</key>"
			+ "<value>8320100</value>"
			+ "</member>"
			+ "<member>"
			+ "<key>NETWORK</key>"
			+ "<value>O2</value>"
			+ "</member>"
			+ "<member>"
			+ "<key>MSISDN</key>"
			+ "<value>00447585927651</value>"
			+ "</member>"
			+ "<member>"
			+ "<key>msg</key>"
			+ "<value>Rpctest</value>"
			+ "</member>"
			+ "</struct>"
			+ "</value>"
			+ "</param>"
			+ "</params>"
			+ "</methodCall>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<methodCall>"
			+ "<methodName>XMLRPCHandler.getContent</methodName>"
			+ "<params>"
			+ "<param>"
			+ "<value>"
			+ "<struct>"
			+ "<member>"
			+ "<key>*SHORTCODE*</key>"
			+ "<value>*8320100*</value>"
			+ "</member>"
			+ "<member>"
			+ "<key>*NETWORK*</key>"
			+ "<value>*O2*</value>"
			+ "</member>"
			+ "<member>"
			+ "<key>*MSISDN*</key>"
			+ "<value>*00447585927651*</value>"
			+ "</member>"
			+ "<member>"
			+ "<key>*msg*</key>"
			+ "<value>*Rpctest*</value>"
			+ "</member>"
			+ "</struct>"
			+ "</value>"
			+ "</param>"
			+ "</params>"
			+ "</methodCall>" };

	private TestContextManager testContextManager;
	private static O2PSMSPaymentDetails o2psmsPaymentDetails;

	@Before
	public void setUpContext() throws Exception {
		this.testContextManager = new TestContextManager(getClass());
		this.testContextManager.prepareTestInstance(this);

		if (count==0){
			User user = UserFactory.createUser();
			
			user = userRepository.save(user);
			
			o2psmsPaymentDetails = new O2PSMSPaymentDetails();
			o2psmsPaymentDetails.setActivated(true);
			o2psmsPaymentDetails.setCreationTimestampMillis(0L);
			o2psmsPaymentDetails.setDisableTimestampMillis(0L);
			o2psmsPaymentDetails.setMadeRetries(0);
			o2psmsPaymentDetails.setRetriesOnError(0);
			o2psmsPaymentDetails.setOwner(user);
	
			o2psmsPaymentDetails = paymentDetailsRepository.save(o2psmsPaymentDetails);
			
			user.setCurrentPaymentDetails(o2psmsPaymentDetails);
			
			user = userRepository.save(user);
		}
	}

	@Theory
	public void test_unsubscribe_success(String xml) throws Exception {
		
		count++;

		String community = "o2";
		String requestURI = "/" + community + "/3.8/stop_subscription";
		String method = "POST";

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		StringReader characterStream = new StringReader(xml);

		InputSource source = new InputSource(characterStream);

		String receivedPhoneNumber = (String) PHONE_NUMBER_XPATHEXPRESSION.evaluate(source, XPathConstants.STRING);
		final String o2PsmsPhoneNumber = receivedPhoneNumber.replaceAll("\\*", "");

		o2psmsPaymentDetails.setPhoneNumber(o2PsmsPhoneNumber);

		o2psmsPaymentDetails = paymentDetailsRepository.save(o2psmsPaymentDetails);

		MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest(method, requestURI);

		mockHttpServletRequest.setPathInfo(requestURI);
		mockHttpServletRequest.addHeader("Content-Length", "409");
		mockHttpServletRequest.addHeader("Content-Type", "text/xml");
		mockHttpServletRequest.addHeader("User-Agent", "Java1.3.1_06");
		mockHttpServletRequest.addHeader("Host", "goat.london.02.net:8080");
		mockHttpServletRequest.addHeader("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
		mockHttpServletRequest.setRemoteAddr("2.24.0.1");
		mockHttpServletRequest.setContent(xml.getBytes());

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		long beforeUnsubscribeMillis = Utils.getEpochMillis();

		dispatcherServlet.service(mockHttpServletRequest, mockHttpServletResponse);

		long afterUnsubscribeMillis = Utils.getEpochMillis();

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());

		String responseBody = mockHttpServletResponse.getContentAsString();

		assertNotNull(responseBody);

		String message = messageSource.getMessage(community, "unsubscribe.mrs.message", null, null);
		assertEquals(message, responseBody);

		characterStream = new StringReader(xml);
		source = new InputSource(characterStream);
		String receivedOperatorName = (String) OPERATOR_XPATHEXPRESSION.evaluate(source, XPathConstants.STRING);
		String operatorName = receivedOperatorName.replaceAll("\\*", "");

		PaymentDetails actualPaymentDetails = paymentDetailsRepository.findOne(o2psmsPaymentDetails.getI());

		assertNotNull(actualPaymentDetails);

		assertEquals(false, actualPaymentDetails.isActivated());
		assertEquals("STOP sms", actualPaymentDetails.getDescriptionError());
		assertTrue(beforeUnsubscribeMillis <= actualPaymentDetails.getDisableTimestampMillis() && actualPaymentDetails.getDisableTimestampMillis() <= afterUnsubscribeMillis);

	}

}
