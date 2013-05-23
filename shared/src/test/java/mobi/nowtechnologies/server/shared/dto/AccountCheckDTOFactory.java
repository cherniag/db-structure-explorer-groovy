package mobi.nowtechnologies.server.shared.dto;

import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;



/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class AccountCheckDTOFactory
 {
	private AccountCheckDTOFactory() {
	}


	public static AccountCheckDTO createAccountCheckDTO() {
		final AccountCheckDTO accountCheckDTO = new AccountCheckDTO();
		
		accountCheckDTO.setActivation(ActivationStatus.ACTIVATED);
		accountCheckDTO.setAppStoreProductId("appStoreProductId");
		accountCheckDTO.setChartItems(Byte.MIN_VALUE);
		accountCheckDTO.setChartTimestamp(Integer.MIN_VALUE);
		accountCheckDTO.setContract("contract");
		accountCheckDTO.setDeviceType("deviceType");
		accountCheckDTO.setDeviceUID("deviceUID");
		accountCheckDTO.setDisplayName("displayName");
		accountCheckDTO.setDrmType("drmType");
		accountCheckDTO.setDrmValue(Byte.MIN_VALUE);
		accountCheckDTO.setFreeTrial(true);
		accountCheckDTO.setFullyRegistred(false);
		accountCheckDTO.setGraceCreditSeconds(Integer.MIN_VALUE);
		accountCheckDTO.setHasOffers(false);
		accountCheckDTO.setHasPotentialPromoCodePromotion(true);
		accountCheckDTO.setLastPaymentStatus(PaymentDetailsStatus.ERROR);
		accountCheckDTO.setLastSubscribedPaymentSystem("lastSubscribedPaymentSystem");
		accountCheckDTO.setNewsItems(Byte.MIN_VALUE);
		accountCheckDTO.setNewsTimestamp(Integer.MIN_VALUE);
		accountCheckDTO.setNextSubPaymentSeconds(Integer.MIN_VALUE);
		accountCheckDTO.setoAuthProvider(OAuthProvider.FACEBOOK);
		accountCheckDTO.setOperator(Integer.MIN_VALUE);
		accountCheckDTO.setPaymentEnabled(false);
		accountCheckDTO.setPaymentStatus("paymentStatus");
		accountCheckDTO.setPaymentType("paymentType");
		accountCheckDTO.setPhoneNumber("phoneNumber");
		accountCheckDTO.setPromotedDevice(false);
		accountCheckDTO.setPromotedWeeks(Integer.MIN_VALUE);
		accountCheckDTO.setPromotionLabel("promotionLabel");
		accountCheckDTO.setProvider("provider");
		accountCheckDTO.setRememberMeToken("rememberMeToken");
		accountCheckDTO.setSegment("segment");
		accountCheckDTO.setStatus("status");
		accountCheckDTO.setSubBalance(Byte.MIN_VALUE);
		accountCheckDTO.setTimeOfMovingToLimitedStatusSeconds(Integer.MAX_VALUE);
		accountCheckDTO.setUserName("userName");
		accountCheckDTO.setUserToken("userToken");
		
		return accountCheckDTO;
	}
}