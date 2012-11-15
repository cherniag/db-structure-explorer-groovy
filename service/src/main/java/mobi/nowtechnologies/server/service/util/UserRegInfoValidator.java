/**
 * 
 */
package mobi.nowtechnologies.server.service.util;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.UserRegInfoServer;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.UserService;
import org.springframework.validation.Errors;


/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class UserRegInfoValidator extends BaseValidator {
	
	private UserService userService;
	
	private PromotionService promoService;
	
	public UserRegInfoValidator(UserService userService, PromotionService promoService) {
		this.userService = userService;
		this.promoService = promoService;
	}
	
	// TODO: remove this method when appropriate validation will be implemented
	@Deprecated
	public static void validate(UserRegInfo aUserRegInfo) {
		if (aUserRegInfo == null)
			throw new NullPointerException("The parameter aUserRegInfo is null");

		final Class<UserRegInfoServer> userRegInfoClass = UserRegInfoServer.class;

		final String firstName = aUserRegInfo.getFirstName();
		MandatoryPropertyValidator.validateStringPropertyOnEmpty(
				userRegInfoClass, "firstName", firstName);

		final String lastName = aUserRegInfo.getLastName();
		MandatoryPropertyValidator.validateStringPropertyOnEmpty(
				userRegInfoClass, "lastName", lastName);

		final String deviceType = aUserRegInfo.getDeviceType();
		MandatoryPropertyValidator.validateOnNull(userRegInfoClass,
				"deviceType", deviceType);

		final String appVersion = aUserRegInfo.getAppVersion();
		MandatoryPropertyValidator.validateOnNull(userRegInfoClass,
				"appVersion", appVersion);

		final String countryFullName = aUserRegInfo.getCountryFullName();
		MandatoryPropertyValidator.validateStringPropertyOnEmpty(
				userRegInfoClass, "countryFullName", countryFullName);

		final String displayName = aUserRegInfo.getDisplayName();
		MandatoryPropertyValidator.validateStringPropertyOnEmpty(
				userRegInfoClass, "displayName", displayName);

		final String storedToken = aUserRegInfo.getStoredToken();
		MandatoryPropertyValidator.validateStringPropertyOnEmpty(
				userRegInfoClass, "storedToken", storedToken);

		final String email = aUserRegInfo.getEmail();
		MandatoryPropertyValidator.validateEmail(userRegInfoClass, "email",
				email);
	}
	
	
	
	
	// TODO: remove this method when appropriate validation will be implemented
	@Deprecated
	public static void validateWhitoutPersonalInfo(UserRegInfo aUserRegInfo) {
		if (aUserRegInfo == null)
			throw new NullPointerException("The parameter aUserRegInfo is null");

		final Class<UserRegInfoServer> userRegInfoClass = UserRegInfoServer.class;

		final String deviceType = aUserRegInfo.getDeviceType();
		/*
		MandatoryPropertyValidator.validateOnNull(userRegInfoClass,
				"deviceType", deviceType);
		*/
		
		final String appVersion = aUserRegInfo.getAppVersion();
		MandatoryPropertyValidator.validateOnNull(userRegInfoClass,
				"appVersion", appVersion);

		/*
		final String displayName = aUserRegInfo.getDisplayName();
		MandatoryPropertyValidator.validateStringPropertyOnEmpty(
				userRegInfoClass, "displayName", displayName);
		*/
		
		final String storedToken = aUserRegInfo.getStoredToken();
		MandatoryPropertyValidator.validateStringPropertyOnEmpty(
				userRegInfoClass, "storedToken", storedToken);

//		final String email = aUserRegInfo.getEmail();
//		MandatoryPropertyValidator.validateEmail(userRegInfoClass, "email",
//				email);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return UserRegInfo.class.isAssignableFrom(clazz);
	}
	
	// TODO: Implement appropriate validation of UserRegInfo
	@Override
	public boolean customValidate(Object target, Errors errors) {
		UserRegInfo userRegInfo = (UserRegInfo) target;
		
		if (!userRegInfo.getEula()) {
			errors.rejectValue("eula", "NotChecked.eula", "You can't finish registration without checking EULA");
		}
		
		if (!userRegInfo.getStoredToken().equals(userRegInfo.getConfirmStoredToken())) {
			errors.rejectValue("confirmStoredToken", "NoEquals.confirmStoredToken", "Confirmation field should equals to password field");
		}
		
		String email = userRegInfo.getEmail().toLowerCase();
		/*
		if (!email.equals(userRegInfo.getConfEmail().toLowerCase())) {
			errors.rejectValue("confEmail", "NoEquals.confEmail", "Confirmation field should equals to email field");
		}
		*/
		if (userService.userExists(email, userRegInfo.getCommunityName())) {
			errors.rejectValue("email", "AlreadyExists.email", "This email is already exists");
		}
		
		if (userRegInfo.getPromotionCode() != null && userRegInfo.getPromotionCode().length()>0) {
			if (null == promoService.getActivePromotion(userRegInfo.getPromotionCode(), userRegInfo.getCommunityName())) {
				errors.rejectValue("promotionCode", "NotExists.promotionCode", "There is no such promotion code for this community");
			}
		}
		return errors.hasErrors();
	}	
}