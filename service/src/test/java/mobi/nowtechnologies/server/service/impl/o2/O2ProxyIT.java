package mobi.nowtechnologies.server.service.impl.o2;

import javax.xml.transform.dom.DOMSource;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import org.junit.*;

import junit.framework.Assert;

public class O2ProxyIT {

    public final static String VALIDATE_PHONE_REQ = "/user/carrier/o2/authorise/";
    public final static String GET_USER_DETAILS_REQ = "/user/carrier/o2/details/";

    private static final String VALIDATE_URL = "http://localhost:8998/webserv" + VALIDATE_PHONE_REQ;
    private static final String VALIDATE_OTAC_URL = "http://localhost:8998/webserv" + GET_USER_DETAILS_REQ;

    @Test
    @Ignore
    public void testValidatePhoneNumber() {

        Assert.assertEquals("0223232233", validatePhoneNumber("0223232233"));

        Assert.assertEquals("o2|PAYM", validateOtac("000001"));
        Assert.assertEquals("o2|PAYG", validateOtac("000002"));
        Assert.assertEquals("non-o2|PAYG", validateOtac("000003"));
    }

    private String validatePhoneNumber(String phoneNumber) {
        MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
        request.add("phone_number", phoneNumber);

        RestTemplate restTemplate = new RestTemplate();
        DOMSource response = restTemplate.postForObject(VALIDATE_URL, request, DOMSource.class);

        String result = response.getNode().getFirstChild().getFirstChild().getFirstChild().getNodeValue();
        return result;
    }

    private String validateOtac(String otacAuthCode) {
        MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
        request.add("otac_auth_code", otacAuthCode);
        RestTemplate restTemplate = new RestTemplate();
        DOMSource response = restTemplate.postForObject(VALIDATE_OTAC_URL, request, DOMSource.class);

        String operator = response.getNode().getFirstChild().getFirstChild().getFirstChild().getNodeValue();
        String contract = response.getNode().getFirstChild().getFirstChild().getNextSibling().getFirstChild().getNodeValue();

        return operator + "|" + contract;
    }

}
