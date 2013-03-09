package mobi.nowtechnologies.server.service;

import java.math.BigDecimal;

import mobi.nowtechnologies.server.dto.O2UserDetails;
import mobi.nowtechnologies.server.service.payment.response.O2Response;

public interface O2ClientService {
	
	String validatePhoneNumber(String phoneNumber);
	
	boolean isO2User(O2UserDetails userDetails);

	String getServerO2Url(String phoneNumber);

	String getRedeemServerO2Url(String phoneNumber);

	O2UserDetails getUserDetails(String token, String phoneNumber);
	
	boolean sendFreeSms(String phoneNumber, String message);

	O2Response makePremiumSMSRequest(int userId, String internalTxId, BigDecimal subCost, String o2PhoneNumber, String message);
}