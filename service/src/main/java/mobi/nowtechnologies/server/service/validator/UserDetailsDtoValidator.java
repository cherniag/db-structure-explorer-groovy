package mobi.nowtechnologies.server.service.validator;

import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.dto.UserDetailsDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class UserDetailsDtoValidator extends BaseValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsDtoValidator.class);

	private final UserService userService;

	public UserDetailsDtoValidator(UserService userService) {
		this.userService = userService;
	}

	@Override
	public boolean customValidate(Object target, Errors errors) {
		LOGGER.debug("input parameters target, errors: [{}], [{}]", target, errors);
		UserDetailsDto userDetailsDto = (UserDetailsDto) target;
		
		String userName = userDetailsDto.getDeviceId();
		String newPassword = userDetailsDto.getNewPassword();
		String newConfirmPassword = userDetailsDto.getNewConfirmPassword();
		String storedToken = userDetailsDto.getStoredToken();
		
		if(!newPassword.equals(newConfirmPassword))
			errors.rejectValue("confirmPassword", "NoEquals.confirmPassword", "Confirmation field should equals to password field");
		
		final boolean hasErrors = errors.hasErrors();

		LOGGER.debug("Output parameter hasErrors=[{}]", hasErrors);

		return hasErrors;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		LOGGER.debug("input parameters clazz: [{}]", clazz);
		final boolean supports = UserDetailsDto.class.isAssignableFrom(clazz);
		LOGGER.debug("Output parameter clazz=[{}]", clazz);
		return supports;
	}

}
