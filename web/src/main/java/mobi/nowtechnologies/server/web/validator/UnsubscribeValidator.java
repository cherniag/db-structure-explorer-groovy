package mobi.nowtechnologies.server.web.validator;

import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.dto.web.payment.UnsubscribeDto;

import org.springframework.validation.Errors;

public class UnsubscribeValidator extends BaseValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(UnsubscribeDto.class);
    }

    @Override
    public boolean customValidate(Object target, Errors errors) {
        return errors.hasErrors();
    }

}
