package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageFrequence;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import mobi.nowtechnologies.server.shared.dto.admin.MessageDto;
import mobi.nowtechnologies.server.shared.enums.MessageActionType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;

public class MessageDtoValidator extends BaseValidator {

	private final UrlValidator urlValidator = new UrlValidator();

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageDtoValidator.class);

	@Override
	public boolean customValidate(Object target, Errors errors) {
		LOGGER.debug("input parameters target, errors: [{}], [{}]", target, errors);

		MessageDto messageDto = (MessageDto) target;

		String action = messageDto.getAction();
		MessageActionType messageActionType = messageDto.getActionType();
		final String actionButtonText = messageDto.getActionButtonText();

        String body = messageDto.getBody();
        if (StringUtils.isBlank(body)){
            errors.rejectValue("body", "message.body.isBlank", "The body field couldn't be null, empty or blank");
        }

		if (messageDto.getMessageType().equals(MessageType.RICH_POPUP)) {

            if (body != null && body.length() > 1000) {
                errors.rejectValue("body", "message.body.wrongSize", new Object[]{1, 1000} , "The body field must consist of {0}-{1} characters for this message type");
            }

			if (StringUtils.isBlank(action)) {
				if (messageActionType.equals(MessageActionType.A_SPECIFIC_NEWS_STORY) || messageActionType.equals(MessageActionType.A_SPECIFIC_TRACK)
						|| messageActionType.equals(MessageActionType.EXTERNAL_URL) || messageActionType.equals(MessageActionType.MOBILE_WEB_PORTAL)) {
					errors.rejectValue("action", "richPopup.action.isNullEmptyOrBlank", "The action field couldn't be null, empty or blank for this action type");
				}
			} else {
				if (action.length() > 255) {
					errors.rejectValue("action", "richPopup.action.wrongSize", "The action field must consist of 1-255 characters");
				}
				if (!urlValidator.isValid(action) && (messageActionType.equals(MessageActionType.EXTERNAL_URL) || messageActionType.equals(MessageActionType.MOBILE_WEB_PORTAL))) {
					errors.rejectValue("action", "richPopup.action.notUrl", "The action should contain URL for this action type");
				}
			}
			
			if (StringUtils.isBlank(actionButtonText)) {
				errors.rejectValue("actionButtonText", "richPopup.actionButtonText.isNullEmptyOrBlank", "The action button text field couldn't be null, empty or blank");
			} else if (actionButtonText.length() > 255) {
				errors.rejectValue("actionButtonText", "richPopup.actionButtonText.wrongSize", "The action button text field must consist of 1-255 characters");
			}
		} else {
            if (body != null && body.length() > 255) {
                errors.rejectValue("body", "message.body.wrongSize", new Object[]{1, 255} , "The body field must consist of {0}-{1} characters for this message type");
            }

			if (MessageFrequence.ONCE_AFTER_1ST_TRACK_DOWNLOAD.equals(messageDto.getFrequence())) {
				errors.rejectValue("frequence", "message.notRichPopup.frequence.isOnceAfter1stTrackDownload", "The frequence field couldn't be such selected option for this message type");
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
