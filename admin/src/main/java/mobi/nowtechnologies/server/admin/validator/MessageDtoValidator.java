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

    public static final String MESSAGE_NOT_RICH_POPUP_FREQUENCE_IS_ONCE_AFTER1ST_TRACK_DOWNLOAD = "message.notRichPopup.frequence.isOnceAfter1stTrackDownload";
    public static final String RICH_POPUP_ACTION_BUTTON_TEXT_WRONG_SIZE = "richPopup.actionButtonText.wrongSize";
    public static final String RICH_POPUP_ACTION_BUTTON_TEXT_IS_NULL_EMPTY_OR_BLANK = "richPopup.actionButtonText.isNullEmptyOrBlank";
    public static final String RICH_POPUP_ACTION_NOT_URL = "richPopup.action.notUrl";
    public static final String RICH_POPUP_ACTION_WRONG_SIZE = "richPopup.action.wrongSize";
    public static final String RICH_POPUP_ACTION_IS_NULL_EMPTY_OR_BLANK = "richPopup.action.isNullEmptyOrBlank";
    public static final String MESSAGE_BODY_IS_BLANK = "message.body.isBlank";
    public static final String MESSAGE_BODY_WRONG_SIZE = "message.body.wrongSize";
    public static final String RICH_POPUP_BODY_WRONG_SIZE = "richPopup.body.wrongSize";
    public static final String BODY = "body";
    public static final String ACTION = "action";
    public static final String ACTION_BUTTON_TEXT = "actionButtonText";
    public static final String FREQUENCE = "frequence";
    public static final String THE_BODY_FIELD_COULDN_T_BE_NULL_EMPTY_OR_BLANK = "The body field couldn't be null, empty or blank";
    public static final String THE_BODY_FIELD_MUST_CONSIST_OF_0_1_CHARACTERS_FOR_THIS_MESSAGE_TYPE = "The body field must consist of {0}-{1} characters for this message type";
    public static final String THE_ACTION_FIELD_COULDN_T_BE_NULL_EMPTY_OR_BLANK_FOR_THIS_ACTION_TYPE = "The action field couldn't be null, empty or blank for this action type";
    public static final String THE_ACTION_FIELD_MUST_CONSIST_OF_1_255_CHARACTERS = "The action field must consist of 1-255 characters";
    public static final String THE_ACTION_SHOULD_CONTAIN_URL_FOR_THIS_ACTION_TYPE = "The action should contain URL for this action type";
    public static final String THE_ACTION_BUTTON_TEXT_FIELD_COULDN_T_BE_NULL_EMPTY_OR_BLANK = "The action button text field couldn't be null, empty or blank";
    public static final String THE_ACTION_BUTTON_TEXT_FIELD_MUST_CONSIST_OF_1_255_CHARACTERS = "The action button text field must consist of 1-255 characters";
    public static final String THE_FREQUENCE_FIELD_COULDN_T_BE_SUCH_SELECTED_OPTION_FOR_THIS_MESSAGE_TYPE = "The frequence field couldn't be such selected option for this message type";

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
            errors.rejectValue(BODY, MESSAGE_BODY_IS_BLANK, THE_BODY_FIELD_COULDN_T_BE_NULL_EMPTY_OR_BLANK);
        }

		if (messageDto.getMessageType().equals(MessageType.RICH_POPUP)) {

            if (body != null && body.length() > 1000) {
                errors.rejectValue(BODY, RICH_POPUP_BODY_WRONG_SIZE, new Object[]{1, 1000} , THE_BODY_FIELD_MUST_CONSIST_OF_0_1_CHARACTERS_FOR_THIS_MESSAGE_TYPE);
            }

			if (StringUtils.isBlank(action)) {
				if (messageActionType.equals(MessageActionType.A_SPECIFIC_NEWS_STORY) || messageActionType.equals(MessageActionType.A_SPECIFIC_TRACK)
						|| messageActionType.equals(MessageActionType.EXTERNAL_URL) || messageActionType.equals(MessageActionType.MOBILE_WEB_PORTAL)) {
					errors.rejectValue(ACTION, RICH_POPUP_ACTION_IS_NULL_EMPTY_OR_BLANK, THE_ACTION_FIELD_COULDN_T_BE_NULL_EMPTY_OR_BLANK_FOR_THIS_ACTION_TYPE);
				}
			} else {
				if (action.length() > 255) {
					errors.rejectValue(ACTION, RICH_POPUP_ACTION_WRONG_SIZE, THE_ACTION_FIELD_MUST_CONSIST_OF_1_255_CHARACTERS);
				}
				if (!urlValidator.isValid(action) && (messageActionType.equals(MessageActionType.EXTERNAL_URL) || messageActionType.equals(MessageActionType.MOBILE_WEB_PORTAL))) {
					errors.rejectValue(ACTION, RICH_POPUP_ACTION_NOT_URL, THE_ACTION_SHOULD_CONTAIN_URL_FOR_THIS_ACTION_TYPE);
				}
			}
			
			if (StringUtils.isBlank(actionButtonText)) {
				errors.rejectValue(ACTION_BUTTON_TEXT, RICH_POPUP_ACTION_BUTTON_TEXT_IS_NULL_EMPTY_OR_BLANK, THE_ACTION_BUTTON_TEXT_FIELD_COULDN_T_BE_NULL_EMPTY_OR_BLANK);
			} else if (actionButtonText.length() > 255) {
				errors.rejectValue(ACTION_BUTTON_TEXT, RICH_POPUP_ACTION_BUTTON_TEXT_WRONG_SIZE, THE_ACTION_BUTTON_TEXT_FIELD_MUST_CONSIST_OF_1_255_CHARACTERS);
			}
		} else {
            if (body != null && body.length() > 255) {
                errors.rejectValue(BODY, MESSAGE_BODY_WRONG_SIZE, new Object[]{1, 255} , THE_BODY_FIELD_MUST_CONSIST_OF_0_1_CHARACTERS_FOR_THIS_MESSAGE_TYPE);
            }

			if (MessageFrequence.ONCE_AFTER_1ST_TRACK_DOWNLOAD.equals(messageDto.getFrequence())) {
				errors.rejectValue(FREQUENCE, MESSAGE_NOT_RICH_POPUP_FREQUENCE_IS_ONCE_AFTER1ST_TRACK_DOWNLOAD, THE_FREQUENCE_FIELD_COULDN_T_BE_SUCH_SELECTED_OPTION_FOR_THIS_MESSAGE_TYPE);
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
