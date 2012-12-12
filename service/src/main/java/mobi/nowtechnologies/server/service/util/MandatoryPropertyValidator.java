/**
 * 
 */
package mobi.nowtechnologies.server.service.util;

import mobi.nowtechnologies.common.util.ServerMessage;
import mobi.nowtechnologies.server.persistence.domain.Operator;
import mobi.nowtechnologies.server.service.exception.ValidationException;
import mobi.nowtechnologies.server.shared.util.CreditCardNumberValidator;
import mobi.nowtechnologies.server.shared.util.EmailValidator;
import mobi.nowtechnologies.server.shared.util.PhoneNumberValidator;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class MandatoryPropertyValidator {
	public static final byte CV2_MAX_LENGTH = 4;
	public static final byte CV2_MIN_LENGTH = 3;
	public static final byte POST_CODE_MAX_LENGTH = 6;
	public static final byte POST_CODE_MIN_LENGTH = 6;

	public static void validateOnNull(Class<?> aClass, String aPropertyName,
			Object aPropertyValue) {
		if (aPropertyName == null)
			throw new NullPointerException(
					"The parameter aPropertyName is null");
		if (aClass == null)
			throw new NullPointerException("The parameter aClass is null");
		if (aPropertyValue == null)
			throw new ValidationException(ServerMessage.getMessageOnNullValue(
					aClass, aPropertyName));
	}

	public static void validateStringPropertyOnEmpty(Class<?> aClass,
			String aPropertyName, String aPropertyValue) {
		if (aPropertyName == null)
			throw new NullPointerException(
					"The parameter aPropertyName is null");
		if (aClass == null)
			throw new NullPointerException("The parameter aClass is null");
		if (aPropertyValue == null)
			throw new ValidationException(ServerMessage.getMessageOnNullValue(
					aClass, aPropertyName));
		if (aPropertyValue.isEmpty())
			throw new ValidationException(ServerMessage.getMessageOnEmptyValue(
					aClass, aPropertyName));
	}

	public static void validateEmail(Class<?> aClass, String aPropertyName,
			String aEmail) {
		if (aPropertyName == null)
			throw new NullPointerException(
					"The parameter aPropertyName is null");
		if (aClass == null)
			throw new NullPointerException("The parameter aClass is null");
		validateStringPropertyOnEmpty(aClass, aPropertyName, aEmail);
		if (!EmailValidator.validate(aEmail))
			throw new ValidationException(ServerMessage
					.getMessageOnInvalidValue(aClass, aPropertyName, aEmail));
	}

	public static void validateCV2(Class<?> aClass, String aPropertyName,
			String aCv2) {
		if (aPropertyName == null)
			throw new NullPointerException(
					"The parameter aPropertyName is null");
		if (aClass == null)
			throw new NullPointerException("The parameter aClass is null");
		if (aCv2 == null)
			throw new NullPointerException("The parameter aCv2 is null");
		String cv2StringValue = String.valueOf(aCv2);
		if (CV2_MAX_LENGTH < cv2StringValue.length())
			throw new ValidationException(ServerMessage
					.getMessageOnBigValueLenthValue(aClass, aPropertyName,
							aCv2, CV2_MAX_LENGTH));
		if (CV2_MIN_LENGTH > cv2StringValue.length())
			throw new ValidationException(ServerMessage
					.getMessageOnBigValueLenthValue(aClass, aPropertyName,
							aCv2, CV2_MIN_LENGTH));
	}

	public static void validateCreditCard(Class<?> aClass,
			String aPropertyName, String aCreditCardNumber) {
		if (aClass == null)
			throw new NullPointerException("The parameter aClass is null");
		if (aPropertyName == null)
			throw new NullPointerException(
					"The parameter aPropertyName is null");
		if (aCreditCardNumber == null)
			throw new NullPointerException(
					"The parameter aCreditCardNumber is null");
		if (!CreditCardNumberValidator.validate(aCreditCardNumber))
			throw new ValidationException(ServerMessage
					.getMessageOnInvalidCardNumber(aClass, aPropertyName,
							aCreditCardNumber));
	}

	public static void validateMonth(Class<?> aClass, String aPropertyName,
			Integer aMotnth) {
		if (aMotnth > 12 || aMotnth < 1)
			throw new ValidationException(ServerMessage
					.getMessageOnInvalidMonthNumberValue(aClass, aPropertyName,
							aMotnth));
	}

	public static void validateYear(Class<?> aClass,
			String aPropertyName, int aExpirationYear) {
		if (aExpirationYear < 1)
			throw new ValidationException(ServerMessage
					.getMessageOnInvalidYearNumberValue(aClass, aPropertyName,
							aExpirationYear));
	}

	public static void validatePostCode(final Class<?> aClass,
			final String aPropertyName, final String aPostCode) {
		if (aClass == null)
			throw new NullPointerException("The parameter aClass is null");
		if (aPropertyName == null)
			throw new NullPointerException(
					"The parameter aPropertyName is null");
		validateStringPropertyOnEmpty(aClass, aPropertyName, aPostCode);
	}

	public static void validatePhoneNumber(Class<?> aClass,
			String aPropertyName, String phoneNumber) {
		if (aClass == null)
			throw new NullPointerException("The parameter aClass is null");
		if (aPropertyName == null)
			throw new NullPointerException(
					"The parameter aPropertyName is null");
		if (phoneNumber == null)
			throw new NullPointerException("The parameter phoneNumber is null");
		boolean isValid = PhoneNumberValidator.validate(phoneNumber);
		if (!isValid)
			throw new ValidationException(ServerMessage
					.getMessageOnInvalidPhoneNumber(aClass, aPropertyName,
							phoneNumber));
	}

	public static void validateCardIssueNumber(Class<?> clazz,
			String propertyName, String cardIssueNumberValue) {
		if (clazz == null)
			throw new NullPointerException("The parameter clazz is null");
		if (propertyName == null)
			throw new NullPointerException("The parameter propertyName is null");
		if (cardIssueNumberValue == null)
			throw new NullPointerException(
					"The parameter cardIssueNumberValue is null");
		int cardIssueNumberValueLength = cardIssueNumberValue.length();
		if (cardIssueNumberValueLength > 2 || cardIssueNumberValueLength == 0)
			throw new ValidationException(ServerMessage
					.getMessageOnInvalidIssueNumber(clazz, propertyName,
							cardIssueNumberValue));

	}

	public static void validateMobileOperatorId(
			Class<?> clazz, String propertyName,
			int operatorId) {
		if (clazz == null)
			throw new NullPointerException("The parameter clazz is null");
		if (propertyName == null)
			throw new NullPointerException("The parameter propertyName is null");
		if (!Operator.getMapAsIds().containsKey(operatorId))
			throw new ValidationException(ServerMessage.getMessageOnInvalidMobileOperatorId(
					clazz, propertyName,
					operatorId));
		
	}
}
