package mobi.nowtechnologies.server.web.validator;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.security.SecurityContextDetails;
import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.web.controller.AccountDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
/**
 * @author Titov Mykhaylo (titov)
 */
public class AccountDtoValidator extends BaseValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDtoValidator.class);

    private UserRepository userRepository;

    public AccountDtoValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean customValidate(Object target, Errors errors) {
        LOGGER.debug("input parameters target, errors: [{}], [{}]", target, errors);
        AccountDto accountDto = (AccountDto) target;

        SecurityContextDetails securityContextDetails = (SecurityContextDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = securityContextDetails.getUserId();
        User user = userRepository.findOne(userId);

        String currentStoredToken = Utils.createStoredToken(accountDto.getEmail(), accountDto.getCurrentPassword());
        if (null == errors.getFieldError("currentPassword")) {
            if (!currentStoredToken.equals(user.getToken())) {
                errors.rejectValue("currentPassword", "change_account.page.changeAccountForm.notEquals.storedToken", "Current password field should equals to current user password");
            }
        }

        // Checking whether passwords are identical
        if (!accountDto.getNewPassword().equals(accountDto.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "change_account.page.changeAccountForm.notEquals.confirmPassword", "Confirmation field should equals to password field");
        }

        final boolean hasErrors = errors.hasErrors();

        LOGGER.debug("Output parameter hasErrors=[{}]", hasErrors);

        return hasErrors;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        LOGGER.debug("input parameters clazz: [{}]", clazz);
        final boolean supports = AccountDto.class.isAssignableFrom(clazz);
        LOGGER.debug("Output parameter clazz=[{}]", clazz);
        return supports;
    }

}
