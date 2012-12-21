package mobi.nowtechnologies.server.service.impl;

import javax.xml.transform.dom.DOMSource;

import mobi.nowtechnologies.server.service.O2ClientService;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class O2ClientServiceImpl implements O2ClientService {
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	public final static String VALIDATE_PHONE_REQ = "/user/carrier/o2/authorise/";

	private String serverO2Url;

	private RestTemplate restTemplate;

	public void init() {
		restTemplate = new RestTemplate();
	}

	public void setServerO2Url(String serverO2Url) {
		this.serverO2Url = serverO2Url;
	}

	@Override
	public String validatePhoneNumber(String phoneNumber) {
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.add("phone_number", phoneNumber);

		try {
			DOMSource response = restTemplate.postForObject(serverO2Url + VALIDATE_PHONE_REQ, request, DOMSource.class);
			return response.getNode().getFirstChild().getFirstChild().getFirstChild().getNodeValue();
		}catch (Exception e) {
			LOGGER.error("Error of the number validation",e);
			throw new InvalidPhoneNumberException();
		}
	}
}
