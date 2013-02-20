package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.dto.O2UserDetails;

public interface O2ClientService {
	
	String validatePhoneNumber(String phoneNumber);
	
	boolean isO2User(O2UserDetails userDetails);

	String getServerO2Url(String phoneNumber);

	String getRedeemServerO2Url(String phoneNumber);

	O2UserDetails getUserDetails(String token, String phoneNumber);
}