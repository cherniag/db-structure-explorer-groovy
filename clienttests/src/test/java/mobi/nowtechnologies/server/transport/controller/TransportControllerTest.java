package mobi.nowtechnologies.server.transport.controller;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import junit.extensions.TestSetup;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * author:lach
 */

public class TransportControllerTest {

    public static final String SALT = "8z54YKmns9Qz";
    private String deviceUid;
    private String timestamp;
    private String communityName;
    private String deviceType;
    private WebClient webClient;
    private String userName;
    private String userToken;

    @Before
    public void before() {
        deviceUid = "+447788995599";
        timestamp = new Date().toString();
        communityName = "o2";
        deviceType = "ANDROID";

        userName = "+447788995599";
        userToken = "ab90da624e970e5865887c5f6d0dc044";
        webClient = new WebClient();
    }


    @Test
    public void testSignUpDeviceXmlAndJson() throws Exception {

        WebRequest requestSettings = getDefaultWebRequest("o2/4.0/SIGN_UP_DEVICE", communityName, timestamp);

        requestSettings.getRequestParameters().add(new NameValuePair("DEVICE_UID", deviceUid));
        requestSettings.getRequestParameters().add(new NameValuePair("DEVICE_TYPE", deviceType));

        assertPageContainsXml(requestSettings);

        requestSettings.setAdditionalHeader("Accept", "application/json");

        assertPageContainsJson(requestSettings);

        requestSettings = getDefaultWebRequest("o2/4.0/SIGN_UP_DEVICE.json", communityName, timestamp);
        requestSettings.getRequestParameters().add(new NameValuePair("DEVICE_UID", deviceUid));
        requestSettings.getRequestParameters().add(new NameValuePair("DEVICE_TYPE", "ANDROID"));

        assertPageContainsJson(requestSettings);

    }

    @Test
    public void testPhoneNumberXmlAndJson() throws Exception {

        WebRequest requestSettings = getUserWebRequest("o2/4.0/PHONE_NUMBER", communityName, timestamp, userName, userToken);

        requestSettings.getRequestParameters().add(new NameValuePair("PHONE", userName));

        assertPageContainsXml(requestSettings);

        requestSettings.setAdditionalHeader("Accept", "application/json");

        assertPageContainsJson(requestSettings);

        requestSettings = getUserWebRequest("o2/4.0/PHONE_NUMBER.json", communityName, timestamp, userName, userToken);
        requestSettings.getRequestParameters().add(new NameValuePair("PHONE", userName));

        assertPageContainsJson(requestSettings);

    }

    @Test
    public void testAccountCheck() throws Exception {

        WebRequest requestSettings = getUserWebRequest("/o2/4.0/ACC_CHECK", communityName, timestamp, userName, userToken);

        assertPageContainsXml(requestSettings);

        requestSettings.setAdditionalHeader("Accept", "application/json");

        assertPageContainsJson(requestSettings);

        requestSettings = getUserWebRequest("/o2/4.0/ACC_CHECK.json", communityName, timestamp, userName, userToken);

        assertPageContainsJson(requestSettings);

    }

    @Test
    public void testApplyInitPromoXmlAndJson() throws Exception {

        WebRequest requestSettings = getUserWebRequest("/o2/4.0/APPLY_INIT_PROMO", communityName, timestamp, userName, userToken);

        requestSettings.getRequestParameters().add(new NameValuePair("OTAC_TOKEN", "ANDROID"));

        assertPageContainsXml(requestSettings);

        requestSettings.setAdditionalHeader("Accept", "application/json");

        assertPageContainsJson(requestSettings);

        requestSettings = getUserWebRequest("o2/4.0/APPLY_INIT_PROMO.json", communityName, timestamp, userName, userToken);
        requestSettings.getRequestParameters().add(new NameValuePair("OTAC_TOKEN", "ANDROID"));

        assertPageContainsJson(requestSettings);

    }

    @Test
    public void testGetChartXmlAndJson() throws Exception {

        WebRequest requestSettings = getUserWebRequest("o2/3.9/GET_CHART", communityName, timestamp, userName, userToken);
        requestSettings.getRequestParameters().add(new NameValuePair("DEVICE_UID", userName));

        assertPageContainsXml(requestSettings);

        requestSettings.setAdditionalHeader("Accept", "application/json");

        assertPageContainsJson(requestSettings);

        requestSettings = getUserWebRequest("o2/4.0/GET_CHART.json", communityName, timestamp, userName, userToken);
        requestSettings.getRequestParameters().add(new NameValuePair("DEVICE_UID", userName));

        assertPageContainsJson(requestSettings);

    }

    @Test
    public void testGetNewsXmlAndJson() throws Exception {

        WebRequest requestSettings = getUserWebRequest("o2/3.9/GET_NEWS", communityName, timestamp, userName, userToken);

        assertPageContainsXml(requestSettings);

        requestSettings.setAdditionalHeader("Accept", "application/json");

        assertPageContainsJson(requestSettings);

        requestSettings = getUserWebRequest("o2/4.0/GET_NEWS.json", communityName, timestamp, userName, userToken);

        assertPageContainsJson(requestSettings);
    }


    private void assertPageContainsJson(WebRequest requestSettings) throws IOException {
        Page page = getPage(requestSettings);

        assertNotNull(page);
        assertTrue(page.getWebResponse().getContentAsString().indexOf("{") == 0);
    }

    private void assertPageContainsXml(WebRequest requestSettings) throws IOException {
        Page page = getPage(requestSettings);

        assertNotNull(page);
        assertTrue(page.getWebResponse().getContentAsString().indexOf("<?xml") == 0);
    }

    private Page getPage(WebRequest requestSettings) throws IOException {
        Page page = webClient.getPage(requestSettings);
        System.out.println(page.getWebResponse().getContentAsString());
        return page;
    }

    private WebRequest getUserWebRequest(String commandString, String communityName, String timestamp, String userName, String userToken) throws MalformedURLException {
        WebRequest request = getDefaultWebRequest(commandString, communityName, timestamp);

        request.getRequestParameters().add(new NameValuePair("USER_NAME", userName));
        request.getRequestParameters().add(new NameValuePair("USER_TOKEN", createTimestampToken(userToken, timestamp)));

        return request;

    }

    private WebRequest getDefaultWebRequest(String commandString, String communityName, String timestamp) throws MalformedURLException {
        String url = "http://localhost:8080/transport/service/mqid/" + commandString;
        WebRequest request = new WebRequest(new URL(url), HttpMethod.POST);
        request.setRequestParameters(new ArrayList());
        request.getRequestParameters().add(new NameValuePair("COMMUNITY_NAME", communityName));
        request.getRequestParameters().add(new NameValuePair("TIMESTAMP", timestamp));
        request.getRequestParameters().add(new NameValuePair("APP_VERSION", "ANDROID"));
        request.getRequestParameters().add(new NameValuePair("API_VERSION", "4.0"));

        return request;
    }

    public static String createTimestampToken(String token, String timestamp) {
        return md5(SALT + token + SALT + timestamp + SALT);
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);

            while (md5.length() < 32) {
                md5 = "0" + md5;
            }
            return md5;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
