package mobi.nowtechnologies.server.admin.validator;

import java.util.Date;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import mobi.nowtechnologies.server.shared.enums.UserType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class UserDtoValidator extends BaseValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDtoValidator.class);

	@Override
	public boolean customValidate(Object target, Errors errors) {
		UserDto userDto = (UserDto) target;
		
		Integer id = userDto.getId();
		if(id==null){
			errors.rejectValue("id", "users.management.edit.page.idCannotBeNull.error", "The user id cannot be null");
		}
		
		UserType userType = userDto.getUserType();
		if(userType==null)
			errors.rejectValue("userType", "users.management.edit.page.userTypeCannotBeNull.error", "The user type cannot be null");

		mobi.nowtechnologies.server.shared.enums.UserStatus userStatus = userDto.getUserStatus();
		if (userStatus.equals(mobi.nowtechnologies.server.shared.enums.UserStatus.LOCKED))
			errors.rejectValue("userStatus", "users.management.edit.page.lockedUserStatus.error",
					"Locked user status is deprecated");

		int subBalance = userDto.getSubBalance();
		if (subBalance < 0)
			errors.rejectValue("subBalance", "users.management.edit.page.subBalanceLezzThan0.error", new Object[] { subBalance },
					"The user subBalance must be more than zero and less than 128. But it's {0}");

		String displayName = userDto.getDisplayName();
		if (displayName == null)
			errors.rejectValue("displayName", "users.management.edit.page.displayNameCannotBeNull.error", "The user display name cannot be null");

		Date nextSubPayment = userDto.getNextSubPayment();
		if (nextSubPayment == null){
			errors.rejectValue("nextSubPayment", "users.management.edit.page.nextSubPaymentCannotBeNull.error", "The user nextSubPayment cannot be null");
		}

		boolean hasErrors = errors.hasErrors();
		LOGGER.info("Output parameter errors=[{}]", errors);
		return hasErrors;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		final boolean supports = UserDto.class.isAssignableFrom(clazz);
		return supports;
	}

}
