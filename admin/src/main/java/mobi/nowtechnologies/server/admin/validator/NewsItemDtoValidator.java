package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.dto.admin.NewsItemDto;

import org.springframework.validation.Errors;

/**
 * @author Mayboroda Dmytro
 * @author Titov Mykhaylo (titov)
 *
 */
public class NewsItemDtoValidator extends BaseValidator {

	@Override
	public boolean customValidate(Object target, Errors errors) {
		return errors.hasErrors();
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return NewsItemDto.class.isAssignableFrom(clazz);
	}

}
