package mobi.nowtechnologies.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.dom.DOMSource;

import mobi.nowtechnologies.server.service.O2ClientService;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class O2ClientServiceImpl implements O2ClientService {

	public final static String VALIDATE_PHONE_REQ = "/user/carrier/o2/authorise/";

	private String serverO2Url;

	private RestTemplate restTemplate = new RestTemplate();

	public void init() {
		HttpMessageConverter<?> xmlConverter = new SourceHttpMessageConverter<DOMSource>();

		List<HttpMessageConverter<?>> list = new ArrayList<HttpMessageConverter<?>>();
		list.add(xmlConverter);

		restTemplate.setMessageConverters(list);
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
		} catch (Exception e) {
			throw new InvalidPhoneNumberException();
		}
	}
}
