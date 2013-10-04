package mobi.nowtechnologies.server.service.o2.impl;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.service.MobileProviderService;
import mobi.nowtechnologies.server.service.payment.response.O2Response;

import java.math.BigDecimal;

public interface O2ProviderService extends MobileProviderService {
	
	boolean isO2User(ProviderUserDetails userDetails);

	String getServerO2Url(String phoneNumber);

	String getRedeemServerO2Url(String phoneNumber);

	ProviderUserDetails getUserDetails(String token, String phoneNumber);

	boolean sendFreeSms(String phoneNumber, String message);

	O2Response makePremiumSMSRequest(int userId, String internalTxId, BigDecimal subCost, String o2PhoneNumber, String message, String contentCategory, String contentType, String contentDescription, String subMerchantId, boolean smsNotify);
}