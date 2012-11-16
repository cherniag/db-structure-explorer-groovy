package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.dto.admin.MessageDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;

public class MessageDtoValidator extends BaseValidator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageDtoValidator.class);

	@Override
	public boolean customValidate(Object target, Errors errors) {
		LOGGER.debug("input parameters target, errors: [{}], [{}]", target, errors);
		
//		MessageDto messageDto = (MessageDto) target;
//		
//		if (messageDto.getFile().isEmpty())
//			errors.rejectValue("file", "message.file.isEmpty", "No file has been selected or file has no content");
		
		boolean hasErrors = errors.hasErrors();
		
		LOGGER.debug("Output parameter hasErrors=[{}]", hasErrors);
		return hasErrors;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		LOGGER.debug("input parameters clazz: [{}]", clazz);
		final boolean supports = MessageDto.class.isAssignableFrom(clazz);
		LOGGER.debug("Output parameter clazz=[{}]", clazz);
		return supports;
	}

}
