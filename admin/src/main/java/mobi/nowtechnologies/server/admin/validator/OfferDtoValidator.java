package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.dto.admin.OfferDto;

import org.springframework.validation.Errors;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
public class OfferDtoValidator extends BaseValidator {

    @Override
    public boolean customValidate(Object target, Errors errors) {
        OfferDto offerDto = (OfferDto) target;

        if (offerDto.getFile().isEmpty() && offerDto.getId() == null) {
            errors.rejectValue("file", "offer.file.isEmpty", "No file has been selected or file has no content");
        }

        return errors.hasErrors();
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return OfferDto.class.isAssignableFrom(clazz);
    }
}