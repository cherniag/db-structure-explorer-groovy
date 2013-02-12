package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.dto.O2UserDetails;

public interface O2ClientService {
	
	String validatePhoneNumber(String phoneNumber);
	
	O2UserDetails getUserDetails(String token);
	
	boolean isO2User(O2UserDetails userDetails);

	String getRedeemPromotedServerO2Url();

	String getRedeemServerO2Url();
}