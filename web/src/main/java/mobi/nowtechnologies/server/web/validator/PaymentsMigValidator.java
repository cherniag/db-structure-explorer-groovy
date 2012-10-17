package mobi.nowtechnologies.server.web.validator;

import org.springframework.validation.Errors;

import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.dto.web.payment.PSmsDto;

public class PaymentsMigValidator extends BaseValidator {

	@Override
	public boolean supports(Class<?> clazz) {
		return PSmsDto.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean customValidate(Object target, Errors errors) {
		return errors.hasErrors();
	}

}
