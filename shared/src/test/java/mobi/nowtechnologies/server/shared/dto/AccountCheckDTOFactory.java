package mobi.nowtechnologies.server.shared.dto;

import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.SegmentType;

import static mobi.nowtechnologies.server.shared.enums.SegmentType.*;


/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class AccountCheckDTOFactory{

	public static AccountCheckDTO createAccountCheckDTO() {
		final AccountCheckDTO accountCheckDTO = new AccountCheckDTO();
		
		accountCheckDTO.activation = ActivationStatus.ACTIVATED;
		accountCheckDTO.appStoreProductId = "appStoreProductId";
		accountCheckDTO.chartItems = Byte.MIN_VALUE;
		accountCheckDTO.chartTimestamp = Integer.MIN_VALUE;
		accountCheckDTO.contract = null;
		accountCheckDTO.deviceType = "deviceType";
		accountCheckDTO.deviceUID = "deviceUID";
		accountCheckDTO.displayName = "displayName";
		accountCheckDTO.drmType = "drmType";
		accountCheckDTO.drmValue = Byte.MIN_VALUE;
		accountCheckDTO.isFreeTrial = true;
		accountCheckDTO.fullyRegistred = false;
		accountCheckDTO.graceCreditSeconds = Integer.MIN_VALUE;
		accountCheckDTO.hasOffers = false;
		accountCheckDTO.hasPotentialPromoCodePromotion = true;
		accountCheckDTO.lastPaymentStatus = PaymentDetailsStatus.ERROR;
		accountCheckDTO.lastSubscribedPaymentSystem = "lastSubscribedPaymentSystem";
		accountCheckDTO.newsItems = Byte.MIN_VALUE;
		accountCheckDTO.newsTimestamp = Integer.MIN_VALUE;
		accountCheckDTO.nextSubPaymentSeconds = Integer.MIN_VALUE;
		accountCheckDTO.oAuthProvider = OAuthProvider.FACEBOOK;
		accountCheckDTO.operator = Integer.MIN_VALUE;
		accountCheckDTO.paymentEnabled = false;
		accountCheckDTO.paymentStatus = "paymentStatus";
		accountCheckDTO.paymentType = "paymentType";
		accountCheckDTO.phoneNumber = "phoneNumber";
		accountCheckDTO.isPromotedDevice = false;
		accountCheckDTO.promotedWeeks = Integer.MIN_VALUE;
		accountCheckDTO.promotionLabel = "promotionLabel";
		accountCheckDTO.provider = "provider";
		accountCheckDTO.rememberMeToken = "rememberMeToken";
		accountCheckDTO.segment = null;
		accountCheckDTO.status = "status";
		accountCheckDTO.subBalance = Byte.MIN_VALUE;
		accountCheckDTO.timeOfMovingToLimitedStatusSeconds = Integer.MAX_VALUE;
		accountCheckDTO.userName = "userName";
		accountCheckDTO.userToken = "userToken";
		
		return accountCheckDTO;
	}
}