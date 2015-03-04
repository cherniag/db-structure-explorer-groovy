package mobi.nowtechnologies.server.web.validator;

import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.dto.web.payment.PSmsDto;

import org.springframework.validation.Errors;

public class PaymentsMigValidator extends BaseValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PSmsDto.class.isAssignableFrom(clazz);
    }

    /**
     * 0044 234 234 234 != 0044234234234s 004441234211324 0044-324-2345-245 (0044)34523 23452 345
     */
    @Override
    public boolean customValidate(Object target, Errors errors) {
        return errors.hasErrors();
    }

}
