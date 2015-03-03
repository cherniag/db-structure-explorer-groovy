package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.StringReader;

import org.xml.sax.InputSource;

import org.springframework.http.HttpHeaders;

import org.junit.*;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Titov Mykhaylo (titov)
 */
public class UnsubscribeControllerIT extends AbstractControllerTestIT {

    public static final String[] xml = new String[] {
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<methodCall>" + "<methodName>XMLRPCHandler.getContent</methodName>" + "<params>" + "<param>" + "<value>" + "<struct>" + "<member>" +
        "<name>SHORTCODE</name>" + "<value>8320100</value>" + "</member>" + "<member>" + "<name>NETWORK</name>" + "<value>O2</value>" + "</member>" + "<member>" + "<name>MSISDN</name>" +
        "<value>00447585927651</value>" + "</member>" + "<member>" + "<name>msg</name>" + "<value>Rpctest</value>" + "</member>" + "</struct>" + "</value>" + "</param>" + "</params>" +
        "</methodCall>",
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<methodCall>" + "<methodName>XMLRPCHandler.getContent</methodName>" + "<params>" + "<param>" + "<value>" + "<struct>" + "<member>" +
        "<name>*SHORTCODE*</name>" + "<value>*8320100*</value>" + "</member>" + "<member>" + "<name>*NETWORK*</name>" + "<value>*O2*</value>" + "</member>" + "<member>" + "<name>*MSISDN*</name>" +
        "<value>*00447585927651*</value>" + "</member>" + "<member>" + "<name>*msg*</name>" + "<value>*Rpctest*</value>" + "</member>" + "</struct>" + "</value>" + "</param>" + "</params>" +
        "</methodCall>",
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<methodCall>" + "<methodName>XMLRPCHandler.getContent</methodName>" + "<params>" + "<param>" + "<value>" + "<struct>" + "<member>" +
        "<key>SHORTCODE</key>" + "<value>8320100</value>" + "</member>" + "<member>" + "<key>NETWORK</key>" + "<value>O2</value>" + "</member>" + "<member>" + "<key>MSISDN</key>" +
        "<value>00447585927651</value>" + "</member>" + "<member>" + "<key>msg</key>" + "<value>Rpctest</value>" + "</member>" + "</struct>" + "</value>" + "</param>" + "</params>" + "</methodCall>",
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<methodCall>" + "<methodName>XMLRPCHandler.getContent</methodName>" + "<params>" + "<param>" + "<value>" + "<struct>" + "<member>" +
        "<key>*SHORTCODE*</key>" + "<value>*8320100*</value>" + "</member>" + "<member>" + "<key>*NETWORK*</key>" + "<value>*O2*</value>" + "</member>" + "<member>" + "<key>*MSISDN*</key>" +
        "<value>*00447585927651*</value>" + "</member>" + "<member>" + "<key>*msg*</key>" + "<value>*Rpctest*</value>" + "</member>" + "</struct>" + "</value>" + "</param>" + "</params>" +
        "</methodCall>"};
    private static final XPathExpression PHONE_NUMBER_XPATHEXPRESSION;
    private static final XPathExpression OPERATOR_XPATHEXPRESSION;
    private static O2PSMSPaymentDetails o2psmsPaymentDetails;

    static {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();
        try {
            PHONE_NUMBER_XPATHEXPRESSION = xPath.compile("//member[contains(name,'MSISDN') or contains(name,'*MSISDN*') or contains(key,'MSISDN') or contains(key,'*MSISDN*')]/value");
            OPERATOR_XPATHEXPRESSION = xPath.compile("//member[contains(name,'NETWORK') or contains(name,'*NETWORK*') or contains(key,'NETWORK') or contains(key,'*NETWORK*')]/value");
        }
        catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    @Resource(name = "serviceMessageSource")
    protected CommunityResourceBundleMessageSource messageSource;
    @Resource
    private PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    private UserRepository userRepository;

    @Before
    public void setUpContext() throws Exception {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        user = userRepository.save(user);

        o2psmsPaymentDetails = new O2PSMSPaymentDetails();
        o2psmsPaymentDetails.setActivated(true);
        o2psmsPaymentDetails.setCreationTimestampMillis(0L);
        o2psmsPaymentDetails.setDisableTimestampMillis(0L);
        o2psmsPaymentDetails.resetMadeAttempts();
        o2psmsPaymentDetails.setRetriesOnError(0);
        o2psmsPaymentDetails.setOwner(user);

        o2psmsPaymentDetails = paymentDetailsRepository.save(o2psmsPaymentDetails);

        user.setCurrentPaymentDetails(o2psmsPaymentDetails);

        userRepository.save(user);
    }

    @Test
    public void test_unsubscribe_success() throws Exception {
        for (String currentXML : xml) {
            test_unsubscribe_success1(currentXML);
        }
    }

    public void test_unsubscribe_success1(String xml) throws Exception {


        String community = "o2";
        String requestURI = "/" + community + "/3.8/stop_subscription";

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true); // never forget this!
        StringReader characterStream = new StringReader(xml);

        InputSource source = new InputSource(characterStream);

        String receivedPhoneNumber = (String) PHONE_NUMBER_XPATHEXPRESSION.evaluate(source, XPathConstants.STRING);
        final String o2PsmsPhoneNumber = receivedPhoneNumber.replaceAll("\\*", "");

        o2psmsPaymentDetails.setPhoneNumber(o2PsmsPhoneNumber);

        o2psmsPaymentDetails = paymentDetailsRepository.save(o2psmsPaymentDetails);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Length", "409");
        headers.add("Content-Type", "text/xml");
        headers.add("User-Agent", "Java1.3.1_06");
        headers.add("Host", "goat.london.02.net:8080");
        headers.add("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");


        long beforeUnsubscribeMillis = Utils.getEpochMillis();

        MvcResult result = mockMvc.perform(post(requestURI).content(xml.getBytes()).headers(headers)).
            andExpect(status().isOk()).andReturn();

        long afterUnsubscribeMillis = Utils.getEpochMillis();

        String responseBody = result.getResponse().getContentAsString();

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

