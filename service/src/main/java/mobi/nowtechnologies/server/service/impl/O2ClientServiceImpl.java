package mobi.nowtechnologies.server.service.impl;

import javax.xml.transform.dom.DOMSource;

import mobi.nowtechnologies.server.dto.O2UserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.DeviceService;
import mobi.nowtechnologies.server.service.O2ClientService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class O2ClientServiceImpl implements O2ClientService {
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	public final static String VALIDATE_PHONE_REQ = "/user/carrier/o2/authorise/";
	public final static String GET_USER_DETAILS_REQ = "/user/carrier/o2/details/";

	private String serverO2Url;

	private String promotedServerO2Url;

	private RestTemplate restTemplate;
	
	private String redeemServerO2Url;

	private String redeemPromotedServerO2Url;
	
	private CommunityService communityService;
	
	private DeviceService deviceService;
	
	private Community o2Community;

	public void init() {
		restTemplate = new RestTemplate();
		o2Community = communityService.getCommunityByName("o2");
	}

	public void setServerO2Url(String serverO2Url) {
		this.serverO2Url = serverO2Url;
	}
	
	public void setPromotedServerO2Url(String promotedServerO2Url) {
		this.promotedServerO2Url = promotedServerO2Url;
	}

	@Override
	public String getServerO2Url(String phoneNumber) {
		String serverO2Url = deviceService.isPromotedDevicePhone(o2Community, phoneNumber)
				? this.promotedServerO2Url
				: this.serverO2Url;
		
		return serverO2Url;
	}

	@Override
	public String getRedeemServerO2Url(String phoneNumber) {
		String redeemServerO2Url = deviceService.isPromotedDevicePhone(o2Community, phoneNumber)
				? this.redeemPromotedServerO2Url
				: this.redeemServerO2Url;
		
		return redeemServerO2Url;
	}

	public void setRedeemServerO2Url(String redeemServerO2Url) {
		this.redeemServerO2Url = redeemServerO2Url;
	}

	public void setCommunityService(CommunityService communityService) {
		this.communityService = communityService;
	}

	public void setDeviceService(DeviceService deviceService) {
		this.deviceService = deviceService;
	}

	public void setRedeemPromotedServerO2Url(String redeemPromotedServerO2Url) {
		this.redeemPromotedServerO2Url = redeemPromotedServerO2Url;
	}

	@Override
	public String validatePhoneNumber(String phoneNumber) {
		String serverO2Url = getServerO2Url(phoneNumber);
		
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

	@Override
	public O2UserDetails getUserDetails(String token, String phoneNumber) {
		String serverO2Url = getServerO2Url(phoneNumber);
		
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
			request.add("otac_auth_code", token);
		try {
			DOMSource response = restTemplate.postForObject(serverO2Url + GET_USER_DETAILS_REQ, request, DOMSource.class);
			return new O2UserDetails(response.getNode().getFirstChild().getFirstChild().getFirstChild().getNodeValue(), response.getNode().getFirstChild().getFirstChild().getNextSibling().getFirstChild().getNodeValue());
		}catch (Exception e) {
			LOGGER.error("Error of the number validation",e);
			throw new ExternalServiceException("602", "O2 server cannot be reached");
		}
	}
	
	@Override
	public boolean isO2User(O2UserDetails userDetails) {
		if (userDetails != null && "o2".equals(userDetails.getOperator())) {
			return true;
		}
		return false;
	}
}
