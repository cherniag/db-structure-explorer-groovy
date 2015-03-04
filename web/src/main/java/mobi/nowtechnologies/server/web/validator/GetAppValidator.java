package mobi.nowtechnologies.server.web.validator;

import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.dto.web.GetPhoneDto;

import org.springframework.validation.Errors;

public class GetAppValidator extends BaseValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return GetPhoneDto.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean customValidate(Object target, Errors errors) {
        return errors.hasErrors();
    }

}
