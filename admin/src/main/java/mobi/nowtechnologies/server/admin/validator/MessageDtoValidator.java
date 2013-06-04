package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import mobi.nowtechnologies.server.shared.dto.admin.MessageDto;
import mobi.nowtechnologies.server.shared.enums.MessageActionType;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;

public class MessageDtoValidator extends BaseValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageDtoValidator.class);

	@Override
	public boolean customValidate(Object target, Errors errors) {
		LOGGER.debug("input parameters target, errors: [{}], [{}]", target, errors);

		MessageDto messageDto = (MessageDto) target;

		if (messageDto.getMessageType().equals(MessageType.RICH_POPUP)) {

			String action = messageDto.getAction();
			MessageActionType messageActionType = messageDto.getActionType();
			if (StringUtils.isBlank(action)) {
				if (messageActionType.equals(MessageActionType.A_SPECIFIC_NEWS_STORY) || messageActionType.equals(MessageActionType.A_SPECIFIC_TRACK)
						|| messageActionType.equals(MessageActionType.EXTERNAL_URL) || messageActionType.equals(MessageActionType.MOBILE_WEB_PORTAL)) {
					errors.rejectValue("action", "richPopups.action.isEmptyOrBlank", "The action field couldn't be empty or blank for this action type");
				}
			} else if (action.length() > 255) {
				errors.rejectValue("action", "richPopups.action.size", "This is field must consist of 1-255 characters");
			}

			final String actionButtonText = messageDto.getActionButtonText();
			if (StringUtils.isBlank(actionButtonText)) {
				errors.rejectValue("actionButtonText", "richPopups.actionButtonText.isEmptyOrBlank", "The action button text field couldn't be empty or blank");
			}else if (actionButtonText.length() > 255) {
				errors.rejectValue("action", "richPopups.actionButtonText.size", "This is field must consist of 1-255 characters");
			}
		}

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
