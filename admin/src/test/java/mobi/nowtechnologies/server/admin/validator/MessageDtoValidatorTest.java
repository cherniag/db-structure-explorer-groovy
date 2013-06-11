package mobi.nowtechnologies.server.admin.validator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageFrequence;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import mobi.nowtechnologies.server.shared.dto.admin.MessageDto;
import mobi.nowtechnologies.server.shared.dto.admin.MessageDtoFactory;
import mobi.nowtechnologies.server.shared.enums.MessageActionType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.ParametersSuppliedBy;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@RunWith(Theories.class)
public class MessageDtoValidatorTest {


    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDtoValidatorTest.class);
	private static final String MESSAGE_NOT_RICH_POPUP_FREQUENCE_IS_ONCE_AFTER1ST_TRACK_DOWNLOAD = "message.notRichPopup.frequence.isOnceAfter1stTrackDownload";
	private static final String RICH_POPUP_ACTION_BUTTON_TEXT_WRONG_SIZE = "richPopup.actionButtonText.wrongSize";
	private static final String RICH_POPUP_ACTION_BUTTON_TEXT_IS_NULL_EMPTY_OR_BLANK = "richPopup.actionButtonText.isNullEmptyOrBlank";
	private static final String RICH_POPUP_ACTION_NOT_URL = "richPopup.action.notUrl";
	private static final String RICH_POPUP_ACTION_WRONG_SIZE = "richPopup.action.wrongSize";
	private static final String RICH_POPUP_ACTION_IS_NULL_EMPTY_OR_BLANK = "richPopup.action.isNullEmptyOrBlank";
    private static final String MESSAGE_BODY_IS_BLANK = "message.body.isBlank";
    public static final String MESSAGE_BODY_WRONG_SIZE = "message.body.wrongSize";
    public static final String RICH_POPUP_BODY_WRONG_SIZE = "richPopup.body.wrongSize";

    public static class BodySupplier extends ParameterSupplier {
        @Override
        public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
            List<PotentialAssignment> list = new ArrayList<PotentialAssignment>();
            list.add(PotentialAssignment.forValue("body", null));
            list.add(PotentialAssignment.forValue("body", ""));
            list.add(PotentialAssignment.forValue("body", " "));
            list.add(PotentialAssignment.forValue("body", "a"));
            list.add(PotentialAssignment.forValue("body", new String(new char[255]).replace('\0', 'm')));
            list.add(PotentialAssignment.forValue("body", new String(new char[1001]).replace('\0', 'm')));
            return list;
        }
    }

    public static class ActionButtonTextSupplier extends ParameterSupplier {
		@Override
		public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
			List<PotentialAssignment> list = new ArrayList<PotentialAssignment>();
			list.add(PotentialAssignment.forValue("actionButtonText", null));
			list.add(PotentialAssignment.forValue("actionButtonText", ""));
			list.add(PotentialAssignment.forValue("actionButtonText", " "));
			list.add(PotentialAssignment.forValue("actionButtonText", "a"));
			list.add(PotentialAssignment.forValue("actionButtonText", new String(new char[255]).replace('\0', 'm')));
			list.add(PotentialAssignment.forValue("actionButtonText", new String(new char[256]).replace('\0', 'm')));
			return list;
		}
	}
	
	public static class ActionSupplier extends ParameterSupplier {
		@Override
		public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
			List<PotentialAssignment> list = new ArrayList<PotentialAssignment>();
			list.add(PotentialAssignment.forValue("action", null));
			list.add(PotentialAssignment.forValue("action", ""));
			list.add(PotentialAssignment.forValue("action", " "));
			list.add(PotentialAssignment.forValue("action", "http://i.ua"));
			list.add(PotentialAssignment.forValue("action", "https://i.ua"));
			list.add(PotentialAssignment.forValue("action", "https://i" + new String(new char[255]).replace('\0', 'i') + ".ua"));
			return list;
		}
	}

	static MessageDtoValidator messageDtoValidator;
	
	@DataPoints
	public static MessageActionType[] actionTypes = MessageActionType.values();

	@DataPoints
	public static MessageType[] messageTypes = MessageType.values();

	@DataPoints
	public static MessageFrequence[] messageFrequences = MessageFrequence.values();

	UrlValidator urlValidator = new UrlValidator();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @ParametersSuppliedBy(BodySupplier.class)
    @interface Body {};

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@ParametersSuppliedBy(ActionButtonTextSupplier.class)
	@interface ActionButtonText {};

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@ParametersSuppliedBy(ActionSupplier.class)
	@interface Action {};


	@BeforeClass
	public static void setUp() {
		messageDtoValidator = new MessageDtoValidator();
	}

	@Theory
	public void customValidate_richPopupExternalUrlIsValid_Success(MessageActionType actionType, @Action String action, @ActionButtonText String actionButtonText, MessageType messageType,
			MessageFrequence frequence, @Body String body) {

		Object[] data = prepareData(actionType, action, actionButtonText, messageType, frequence, body);

		MessageDto messageDto = (MessageDto) data[0];
		Errors errors = (Errors) data[1];

		Object[] expectedData = mockMethods(actionType, action, actionButtonText, messageType, messageDto, errors);
		
		LOGGER.info("Input data: {}, expected data: {}", Arrays.toString(data), Arrays.toString(expectedData));

		boolean actualHasErrors = messageDtoValidator.customValidate(messageDto, errors);

		verifyResult(errors, expectedData, actualHasErrors);

	}

	private Object[] mockMethods(MessageActionType actionType, String action, String actionButtonText, MessageType messageType, MessageDto messageDto, Errors errors) {
		doNothing().when(errors).rejectValue("action", RICH_POPUP_ACTION_IS_NULL_EMPTY_OR_BLANK, "The action field couldn't be null,  empty or blank for this action type");
		doNothing().when(errors).rejectValue("action", RICH_POPUP_ACTION_WRONG_SIZE, "The action field must consist of 1-255 characters");
		doNothing().when(errors).rejectValue("action", RICH_POPUP_ACTION_NOT_URL, "The action should contain URL for this action type");
		doNothing().when(errors).rejectValue("actionButtonText", RICH_POPUP_ACTION_BUTTON_TEXT_IS_NULL_EMPTY_OR_BLANK, "The action button text field couldn't be null, empty or blank");
		doNothing().when(errors).rejectValue("actionButtonText", RICH_POPUP_ACTION_BUTTON_TEXT_WRONG_SIZE, "The action button text field must consist of 1-255 characters");
		doNothing().when(errors).rejectValue("frequence", MESSAGE_NOT_RICH_POPUP_FREQUENCE_IS_ONCE_AFTER1ST_TRACK_DOWNLOAD,
				"The frequence field couldn't be such selected option for this message type");

        doNothing().when(errors).rejectValue("body", MESSAGE_BODY_IS_BLANK, "The body field couldn't be null, empty or blank");
        doNothing().when(errors).rejectValue("body", MESSAGE_BODY_WRONG_SIZE, new Object[]{1,255},
                "The body field must consist of {0}-{1} characters for this message type");
        doNothing().when(errors).rejectValue("body", MESSAGE_BODY_WRONG_SIZE, new Object[]{1,1000},
                "The body field must consist of {0}-{1} characters for this message type");

		Map<String, Integer> methodInvokationTimes = new HashMap<String, Integer>();

		int richPopupsActionisNullEmptyOrBlankTimes = 0;
		int richPopupsActionSizeTimes = 0;
		int richPopupsActionNotUrlTimes = 0;
		int richPopupsActionButtonTextIsNullEmptyOrBlankTimes = 0;
		int richPopupsActionButtonTextSizeTimes = 0;
		int notRichPopupsFrequenceIsOnceAfter1stTrackDownloadTimes = 0;
        int notRichPopupBodyIsBlank = 0;
        int notRichPopupBodyWrongSize = 0;
        int richPopupBodyWrongSize = 0;

        String body = messageDto.getBody();
        if (StringUtils.isBlank(body)){
            notRichPopupBodyIsBlank = 1;
        }

		if (messageType.equals(MessageType.RICH_POPUP)) {
            if (body != null && body.length() > 1000) {
                richPopupBodyWrongSize = 1;
            }

			if (StringUtils.isBlank(action)) {
				if (actionType.equals(MessageActionType.A_SPECIFIC_NEWS_STORY) || actionType.equals(MessageActionType.A_SPECIFIC_TRACK)
						|| actionType.equals(MessageActionType.EXTERNAL_URL) || actionType.equals(MessageActionType.MOBILE_WEB_PORTAL)) {
					richPopupsActionisNullEmptyOrBlankTimes = 1;
				}
			} else {
				if (action.length() > 255) {
					richPopupsActionSizeTimes = 1;
				}
				if (!urlValidator.isValid(action) && (actionType.equals(MessageActionType.EXTERNAL_URL) || actionType.equals(MessageActionType.MOBILE_WEB_PORTAL))) {
					richPopupsActionNotUrlTimes = 1;
				}
			}

			if (StringUtils.isBlank(actionButtonText)) {
				richPopupsActionButtonTextIsNullEmptyOrBlankTimes = 1;
			} else if (actionButtonText.length() > 255) {
                richPopupsActionButtonTextSizeTimes = 1;
			}
		} else {
            if (body != null && body.length() > 255) {
                notRichPopupBodyWrongSize =1;
            }

			if (MessageFrequence.ONCE_AFTER_1ST_TRACK_DOWNLOAD.equals(messageDto.getFrequence())) {
				notRichPopupsFrequenceIsOnceAfter1stTrackDownloadTimes = 1;
			}
		}

		methodInvokationTimes.put(RICH_POPUP_ACTION_IS_NULL_EMPTY_OR_BLANK, richPopupsActionisNullEmptyOrBlankTimes);
		methodInvokationTimes.put(RICH_POPUP_ACTION_WRONG_SIZE, richPopupsActionSizeTimes);
		methodInvokationTimes.put(RICH_POPUP_ACTION_NOT_URL, richPopupsActionNotUrlTimes);
		methodInvokationTimes.put(RICH_POPUP_ACTION_BUTTON_TEXT_IS_NULL_EMPTY_OR_BLANK, richPopupsActionButtonTextIsNullEmptyOrBlankTimes);
		methodInvokationTimes.put(RICH_POPUP_ACTION_BUTTON_TEXT_WRONG_SIZE, richPopupsActionButtonTextSizeTimes);
		methodInvokationTimes.put(MESSAGE_NOT_RICH_POPUP_FREQUENCE_IS_ONCE_AFTER1ST_TRACK_DOWNLOAD, notRichPopupsFrequenceIsOnceAfter1stTrackDownloadTimes);
        methodInvokationTimes.put(MESSAGE_BODY_IS_BLANK, notRichPopupBodyIsBlank);
        methodInvokationTimes.put(MESSAGE_BODY_WRONG_SIZE, notRichPopupBodyWrongSize);
        methodInvokationTimes.put(RICH_POPUP_BODY_WRONG_SIZE, richPopupBodyWrongSize);

		boolean expectedHasErrors = richPopupsActionisNullEmptyOrBlankTimes != 0 || richPopupsActionSizeTimes != 0 || richPopupsActionNotUrlTimes != 0
				|| richPopupsActionButtonTextIsNullEmptyOrBlankTimes != 0
				|| richPopupsActionButtonTextSizeTimes != 0 || notRichPopupsFrequenceIsOnceAfter1stTrackDownloadTimes != 0 || notRichPopupBodyWrongSize!=0 || richPopupBodyWrongSize!=0;

		when(errors.hasErrors()).thenReturn(expectedHasErrors);

		return new Object[] { expectedHasErrors, methodInvokationTimes };
	}

	private void verifyResult(Errors errors, Object[] expectedData, boolean actualHasErrors) {
		boolean expectedHasErrors = (Boolean) expectedData[0];
		@SuppressWarnings("unchecked")
		Map<String, Integer> methodInvokationTimes = (Map<String, Integer>) expectedData[1];

		assertEquals(expectedHasErrors, actualHasErrors);

		verify(errors, times(methodInvokationTimes.get(RICH_POPUP_ACTION_IS_NULL_EMPTY_OR_BLANK))).rejectValue("action", RICH_POPUP_ACTION_IS_NULL_EMPTY_OR_BLANK,
				"The action field couldn't be null, empty or blank for this action type");
		verify(errors, times(methodInvokationTimes.get(RICH_POPUP_ACTION_WRONG_SIZE))).rejectValue("action", RICH_POPUP_ACTION_WRONG_SIZE, "The action field must consist of 1-255 characters");
		verify(errors, times(methodInvokationTimes.get(RICH_POPUP_ACTION_NOT_URL))).rejectValue("action", RICH_POPUP_ACTION_NOT_URL, "The action should contain URL for this action type");
		verify(errors, times(methodInvokationTimes.get(RICH_POPUP_ACTION_BUTTON_TEXT_IS_NULL_EMPTY_OR_BLANK))).rejectValue("actionButtonText", RICH_POPUP_ACTION_BUTTON_TEXT_IS_NULL_EMPTY_OR_BLANK,
				"The action button text field couldn't be null, empty or blank");
		verify(errors, times(methodInvokationTimes.get(RICH_POPUP_ACTION_BUTTON_TEXT_WRONG_SIZE))).rejectValue("actionButtonText", RICH_POPUP_ACTION_BUTTON_TEXT_WRONG_SIZE,
				"The action button text field must consist of 1-255 characters");
		verify(errors, times(methodInvokationTimes.get(MESSAGE_NOT_RICH_POPUP_FREQUENCE_IS_ONCE_AFTER1ST_TRACK_DOWNLOAD))).rejectValue("frequence", MESSAGE_NOT_RICH_POPUP_FREQUENCE_IS_ONCE_AFTER1ST_TRACK_DOWNLOAD,
				"The frequence field couldn't be such selected option for this message type");
        verify(errors, times(methodInvokationTimes.get(MESSAGE_BODY_IS_BLANK))).rejectValue("body", MESSAGE_BODY_IS_BLANK,
                "The body field couldn't be null, empty or blank");
        verify(errors, times(methodInvokationTimes.get(MESSAGE_BODY_WRONG_SIZE))).rejectValue("body", MESSAGE_BODY_WRONG_SIZE, new Object[]{1,255},
                "The body field must consist of {0}-{1} characters for this message type");
        verify(errors, times(methodInvokationTimes.get(RICH_POPUP_BODY_WRONG_SIZE))).rejectValue("body", MESSAGE_BODY_WRONG_SIZE, new Object[]{1,1000},
                "The body field must consist of {0}-{1} characters for this message type");
	}

	private Object[] prepareData(MessageActionType actionType, String action, String actionButtonText, MessageType messageType, MessageFrequence frequence, String body) {
		MessageDto messageDto = MessageDtoFactory.createMessageDto();

		messageDto.setAction(action);
		messageDto.setActionButtonText(actionButtonText);
		messageDto.setActionType(actionType);
		messageDto.setMessageType(messageType);
		messageDto.setFrequence(frequence);
        messageDto.setBody(body);

		Errors errors = mock(Errors.class);

		return new Object[] { messageDto, errors };
	}

}
