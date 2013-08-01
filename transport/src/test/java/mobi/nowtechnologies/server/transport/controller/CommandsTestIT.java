package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.service.FileService.FileType;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.service.PostService;
import mobi.nowtechnologies.server.shared.service.PostService.Response;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Random;

/**
 * CommandsTestIT
 * 
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
@Ignore
public class CommandsTestIT {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandsTestIT.class);

	private static final String URL = "http://localhost:8080/transport/service/";
	
	final String USER_NAME = "q" + new Random().nextInt(1000000) + "@q.com";
	final String PASSWORD = "12345678";
	final String STORED_TOKEN = Utils.createStoredToken(USER_NAME, PASSWORD);
	
	String APP_VERSION = "CNBETA";
	String API_VERSION = "V1.1";
	String COMMUNTIY_NAME = "Metal Hammer";

	@Test
	public void testRegisterUser() throws Exception {
		String xml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
			+ "<userRegInfo>"
			+ "<email>" + USER_NAME + "</email>"
			+ "<storedToken>" + STORED_TOKEN + "</storedToken>"
			+ "<appVersion>" + APP_VERSION + "</appVersion>"
			+ "<apiVersion>" + API_VERSION + "</apiVersion>"
			+ "<communityName>" + COMMUNTIY_NAME + "</communityName>"
			+ "<title>Mr</title>"
			+ "<firstName>33</firstName>"
			+ "<lastName>33</lastName>"
			+ "<displayName>33</displayName>"
			+ "<deviceType>IOS</deviceType>"
			+ "<deviceString>Device 1</deviceString>"
			+ "<countryFullName>Great Britain</countryFullName>"
			+ "<address>33333</address>"
			+ "<city>33</city>"
			+ "<postCode>null</postCode>"
			+ "<paymentType>PSMS</paymentType>"
			+ "<newsByEmail>false</newsByEmail>"
			+ "<phoneNumber>00447580381128</phoneNumber>"
			+ "<operator>1</operator>"
//			+ "<cardBillingAddress>88</cardBillingAddress>"
//			+ "<cardBillingCity>London</cardBillingCity>"
//			+ "<cardBillingCountry>GB</cardBillingCountry>"
//			+ "<cardCv2>123</cardCv2>"
//			+ "<cardHolderFirstName>John</cardHolderFirstName>"
//			+ "<cardHolderLastName>Smith</cardHolderLastName>"
//			+ "<cardBillingPostCode>412</cardBillingPostCode>"
//			+ "<cardStartMonth>1</cardStartMonth>"
//			+ "<cardStartYear>2011</cardStartYear>"
//			+ "<cardExpirationMonth>1</cardExpirationMonth>"
//			+ "<cardExpirationYear>2012</cardExpirationYear>"
//			+ "<cardNumber>4929000000006</cardNumber>"
//			+ "<cardType>VISA</cardType>" 
			+ "</userRegInfo>";
		System.out.println(USER_NAME);
		//System.out.println(xml);
		PostService postService = new PostService();
		Response response = postService.sendHttpPost(URL + "REGISTER_USER",
				null, xml);

		System.out.println(response.getMessage());
	}
	
	@Test
	public void testCheckPin() throws Exception {
		String command = "CHECK_PIN";

		NameValuePair[] nameValuePairs = new NameValuePair[7];
		nameValuePairs[0] = new BasicNameValuePair("USER_NAME", "q373482@q.com");
		nameValuePairs[1] = new BasicNameValuePair(
				"USER_TOKEN", Utils.createTimestampToken(Utils.createStoredToken("q373482@q.com", PASSWORD), "1"));
		nameValuePairs[2] = new BasicNameValuePair("TIMESTAMP", "1");
		nameValuePairs[3] = new BasicNameValuePair("APP_VERSION", APP_VERSION);
		nameValuePairs[4] = new BasicNameValuePair("API_VERSION", API_VERSION);
		nameValuePairs[5] = new BasicNameValuePair("COMMUNITY_NAME", COMMUNTIY_NAME);
		nameValuePairs[6] = new BasicNameValuePair("PIN", "14391690");

		PostService postService = new PostService();
		Response response = postService.sendHttpPost(URL + command,
				Arrays.asList(nameValuePairs), null);

		System.out.println(response.getMessage());
	}
	
	@Test
	public void testCheckAccount() throws Exception {
		String command = "ACC_CHECK";

		NameValuePair[] nameValuePairs = new NameValuePair[6];
		nameValuePairs[0] = new BasicNameValuePair("USER_NAME", USER_NAME);
		nameValuePairs[1] = new BasicNameValuePair(
				"USER_TOKEN", Utils.createTimestampToken(STORED_TOKEN, "1"));
		nameValuePairs[2] = new BasicNameValuePair("TIMESTAMP", "1");
		nameValuePairs[3] = new BasicNameValuePair("APP_VERSION", APP_VERSION);
		nameValuePairs[4] = new BasicNameValuePair("API_VERSION", API_VERSION);
		nameValuePairs[5] = new BasicNameValuePair("COMMUNITY_NAME", COMMUNTIY_NAME);

		PostService postService = new PostService();
		Response response = postService.sendHttpPost(URL + command,
				Arrays.asList(nameValuePairs), null);

		System.out.println(response.getMessage());
	}
	
	@Test
	public void testGetFile() throws Exception {
		String command = "GET_FILE";

		NameValuePair[] nameValuePairs = new NameValuePair[9];
		nameValuePairs[0] = new BasicNameValuePair("USER_NAME", USER_NAME);
		nameValuePairs[1] = new BasicNameValuePair(
				"USER_TOKEN", Utils.createTimestampToken(
						Utils.createStoredToken(USER_NAME, PASSWORD), "1"));
		nameValuePairs[2] = new BasicNameValuePair("TIMESTAMP", "1");
		nameValuePairs[3] = new BasicNameValuePair("APP_VERSION", APP_VERSION);
		nameValuePairs[4] = new BasicNameValuePair("API_VERSION", API_VERSION);
		nameValuePairs[5] = new BasicNameValuePair("TYPE", FileType.AUDIO.toString());
		nameValuePairs[6] = new BasicNameValuePair("ID", "USJAY1100032");
		nameValuePairs[7] = new BasicNameValuePair("RESOLUTION", "1");
		nameValuePairs[8] = new BasicNameValuePair("COMMUNITY_NAME", COMMUNTIY_NAME);

		PostService postService = new PostService();
		Response response = postService.sendHttpPost(URL + command,
				Arrays.asList(nameValuePairs), null);

		System.out.println(response.getMessage());
	}
	
//	@Test
//	public void testGetPaymentPolicy() {	
//		String command = "GET_PAYMENT_POLICY";
//
//		NameValuePair[] nameValuePairs = new NameValuePair[9];
//		nameValuePairs[3] = new NameValuePair("APP_VERSION", APP_VERSION);
//		nameValuePairs[4] = new NameValuePair("API_VERSION", API_VERSION);
//		nameValuePairs[8] = new NameValuePair("COMMUNITY_NAME", COMMUNTIY_NAME);
//
//		PostService postService = new PostService();
//		String response = postService.sendHttpPost(URL + command,
//				nameValuePairs, null);
//
//		LOGGER.info(response);
//	}	
	
}
