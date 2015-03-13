package mobi.nowtechnologies.server.web.validator;

import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.dto.web.payment.CreditCardDto;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import org.springframework.validation.Errors;

public class PaymentsCreditCardValidator extends BaseValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CreditCardDto.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean customValidate(Object target, Errors errors) {
        CreditCardDto dto = (CreditCardDto) target;
        // Checking dates
        DateTime startDateTime = DateTimeFormat.forPattern("MM/yyyy").parseDateTime(dto.getStartDateMonth() + "/" + dto.getStartDateYear());
        DateTime expireDateTime = DateTimeFormat.forPattern("MM/yyyy").parseDateTime(dto.getExpireDateMonth() + "/" + dto.getExpireDateYear());
        DateTime nowDateTime = new DateTime();
        if (nowDateTime.compareTo(startDateTime) <= 0 || expireDateTime.compareTo(nowDateTime) <= 0 || (startDateTime.compareTo(expireDateTime) > 0)) {
            errors.rejectValue("startDateMonth", "pay.cc.error.form.wrong.dates");
        }
        return errors.hasErrors();
    }

}