package mobi.nowtechnologies.server.service.util;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.UserRegInfoServer;
import mobi.nowtechnologies.server.service.exception.ValidationException;

public class PaymentDetailsValidator {
	public static void validate(UserRegInfo aUserRegInfo) {
		if (aUserRegInfo == null)
			throw new NullPointerException("The parameter aUserRegInfo is null");
		
		final Class<UserRegInfoServer> userRegInfoClass = UserRegInfoServer.class;
		
		String paymentType = aUserRegInfo.getPaymentType();
		MandatoryPropertyValidator.validateOnNull(userRegInfoClass,
				"paymentType", paymentType);

		if (paymentType.equals(UserRegInfoServer.PaymentType.CREDIT_CARD)) {
			final String cardType = aUserRegInfo.getCardType();
			MandatoryPropertyValidator.validateOnNull(userRegInfoClass, "cardType",
					cardType);

			final String cardBillingAddress = aUserRegInfo.getCardBillingAddress();
			MandatoryPropertyValidator.validateOnNull(userRegInfoClass,
					"cardBillingAddress", cardBillingAddress);

			final String cardIssueNumber = aUserRegInfo.getCardIssueNumber();
			if (cardIssueNumber!=null && !cardIssueNumber.isEmpty())
				MandatoryPropertyValidator.validateCardIssueNumber(userRegInfoClass,
						"cardIssueNumber", cardIssueNumber);

			Integer cardSartMonth = aUserRegInfo.getCardStartMonth();
			if (null != cardSartMonth)
				MandatoryPropertyValidator.validateMonth(userRegInfoClass,
						"cardSartMonth", cardSartMonth);

			Integer cardStartYear = aUserRegInfo.getCardStartYear();
			if (null != cardStartYear)
				MandatoryPropertyValidator.validateYear(userRegInfoClass,
						"cardStartYear", cardStartYear);
			
			String cardHolderFirstName = aUserRegInfo.getCardHolderFirstName();
			MandatoryPropertyValidator.validateStringPropertyOnEmpty(
					userRegInfoClass, "cardHolderFirstName",
					cardHolderFirstName);

			String cardHolderLastName = aUserRegInfo.getCardHolderLastName();
			MandatoryPropertyValidator.validateStringPropertyOnEmpty(
					userRegInfoClass, "cardHolderLastName", cardHolderLastName);

			String cardCv2 = aUserRegInfo.getCardCv2();
			MandatoryPropertyValidator.validateCV2(userRegInfoClass, "cardCv2",
					cardCv2);

			String cardNumber = aUserRegInfo.getCardNumber();
			MandatoryPropertyValidator.validateCreditCard(userRegInfoClass,
					"cardNumber", cardNumber);

			Integer expirationMonth = aUserRegInfo.getCardExpirationMonth();
			MandatoryPropertyValidator.validateMonth(userRegInfoClass,
					"expirationMonth", expirationMonth);

			Integer expirationYear = aUserRegInfo.getCardExpirationYear();
			MandatoryPropertyValidator.validateYear(userRegInfoClass,
					"expirationYear", expirationYear);

			String cardBillingCity = aUserRegInfo.getCardBillingCity();
			MandatoryPropertyValidator.validateStringPropertyOnEmpty(
					userRegInfoClass, "cardBillingCity", cardBillingCity);

			final String cardBillingCountry = aUserRegInfo
					.getCardBillingCountry();
			MandatoryPropertyValidator.validateStringPropertyOnEmpty(
					userRegInfoClass, "cardBillingCountry", cardBillingCountry);

			final String cardBillingPostCode = aUserRegInfo
					.getCardBillingPostCode();
			MandatoryPropertyValidator.validatePostCode(userRegInfoClass,
					"cardBillingPostCode", cardBillingPostCode);

		} else if (paymentType
				.equals(UserRegInfoServer.PaymentType.PREMIUM_USER)) {
			String phoneNumber = aUserRegInfo.getPhoneNumber();
			MandatoryPropertyValidator.validatePhoneNumber(userRegInfoClass,
					"phoneNumber", phoneNumber);
			MandatoryPropertyValidator.validateMobileOperatorId(
					userRegInfoClass, "operator", aUserRegInfo.getOperator());
		}  else if (paymentType
				.equals(UserRegInfoServer.PaymentType.FREEMIUM)) {
			//no checks
		}  else if (paymentType
			.equals(UserRegInfoServer.PaymentType.PAY_PAL)) {
			//no checks
		} else if (paymentType
				.equals(UserRegInfoServer.PaymentType.UNKNOWN)) {
			//if (aUserRegInfo.getPromotionCode() == null || aUserRegInfo.getPromotionCode().equals(""))
				/*throw new ValidationException(
						"The property propertyType of UserRegInfo class object has UNKNOWN paymentType value and empty (or null) promotionCode value");*/
		} else
			throw new ValidationException(
					"The property paymentType of UserRegInfo class object has unknown payment type value: "
							+ aUserRegInfo.getPaymentType());
	}
}
