package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import mobi.nowtechnologies.server.shared.dto.admin.MessageDto;
import mobi.nowtechnologies.server.shared.enums.MessageActionType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;

import java.util.*;

import static junit.framework.Assert.assertFalse;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.*;
import static mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageFrequence.*;
import static mobi.nowtechnologies.server.shared.enums.MessageActionType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@RunWith(PowerMockRunner.class)
public class MessageDtoValidatorTest {

    private MessageDto messageDto;
    private Errors errors;
    private boolean actualHasErrors;
    private MessageDtoValidator messageDtoValidator;

    @Before
    public void setUp(){
        messageDto = new MessageDto();
        errors = new MapBindingResult(Collections.emptyMap(), "");

        messageDtoValidator = new MessageDtoValidator();
    }

    @Test
	public void shouldValidateAsNoErrorsNews() {
        given().news().bodyLenghtIs(255);
        whenCustomValidate();
        then().validateAsNoError();
	}

    @Test
	public void shouldValidateAsWrongNewsBodySize() {
        given().news().and().bodyLenghtIs(256);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongNewsBodyLenghtError().and().theseErrorsAreOnly(1);
	}

    @Test
    public void shouldValidateAsWrongNewsBodyValue() {
        given().news().and().nullBody();
        whenCustomValidate();
        then().validateAsFalse().returnNullBodyError().and().theseErrorsAreOnly(1);
    }

    @Test
    public void shouldValidateAsWrongNewsFrequenceValue() {
        given().news().and().bodyLenghtIs(255).and().onceAfterFirstDownloadFrequence();
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongFrequenceError().and().theseErrorsAreOnly(1);
    }
    
    @Test
    public void shouldValidateAsWrongNewsFrequenceValueAndWrongBodySize() {
        given().news().and().bodyLenghtIs(256).and().onceAfterFirstDownloadFrequence();
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongFrequenceError().and().returnWrongNewsBodyLenghtError().and().theseErrorsAreOnly(2);
    }
    
    @Test
	public void shouldValidateAsNoErrorsRichPopup() {
        given().richPopup().bodyLenghtIs(1000).and().actionLenghtIs(255).and().actionTypeIs(OUR_PLAYLIST).and().actionButtonTextLenghtIs(255);
        whenCustomValidate();
        then().validateAsNoError();
	}
    
    @Test
	public void shouldValidateAsWrongRichPopupBodyLenght() {
        given().richPopup().bodyLenghtIs(1001).and().actionLenghtIs(255).and().actionTypeIs(OUR_PLAYLIST).and().actionButtonTextLenghtIs(255);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLenghtError().and().theseErrorsAreOnly(1);
	}
    
    @Test
	public void shouldValidateAsWrongRichPopupBodyAndActionLenghts() {
        given().richPopup().bodyLenghtIs(1001).and().actionLenghtIs(256).and().actionTypeIs(OUR_PLAYLIST).and().actionButtonTextLenghtIs(255);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLenghtError().and().returnWrongActionLenght().theseErrorsAreOnly(2);
	}
    
    @Test
	public void shouldValidateAsWrongRichPopupBodyAndActionButtonTextLenghts() {
        given().richPopup().bodyLenghtIs(1001).and().actionLenghtIs(255).and().actionTypeIs(OUR_PLAYLIST).and().actionButtonTextLenghtIs(256);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLenghtError().and().returnWrongActionButtonTextLenght().theseErrorsAreOnly(2);
	}
    
    @Test
	public void shouldValidateAsWrongRichPopupBodyLenghtAndActionButtonIsNull() {
        given().richPopup().bodyLenghtIs(1001).and().actionLenghtIs(255).and().actionTypeIs(OUR_PLAYLIST).and().actionButtonTextIsNull();
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLenghtError().and().returnWrongActionButtonTextValue().theseErrorsAreOnly(2);
	}
    
    public void shouldValidateAsNoErrorsMobileWebPortalRichPopup() {
        given().richPopup().bodyLenghtIs(1000).and().actionIs("http://i.ua").and().actionTypeIs(MOBILE_WEB_PORTAL).and().actionButtonTextLenghtIs(255);
        whenCustomValidate();
        then().validateAsNoError();
	}
    
    public void shouldValidateAsNoErrorsExternalUrlRichPopup() {
        given().richPopup().bodyLenghtIs(1000).and().actionIs("http://i.ua").and().actionTypeIs(EXTERNAL_URL).and().actionButtonTextLenghtIs(255);
        whenCustomValidate();
        then().validateAsNoError();
	}
    
    public void shouldValidateAsWrongMobileWebPortalRichPopupActionUri() {
        given().richPopup().bodyLenghtIs(1000).and().actionIs("notUri").and().actionTypeIs(MOBILE_WEB_PORTAL).and().actionButtonTextLenghtIs(255);
        whenCustomValidate();
        then().validateAsFalse().and().returnNotUriActionError().and().theseErrorsAreOnly(1);
	}
    
    public void shouldValidateAsWrongExternalActionRichPopupActionUri() {
        given().richPopup().bodyLenghtIs(1000).and().actionIs("notUri").and().actionTypeIs(EXTERNAL_URL).and().actionButtonTextLenghtIs(255);
        whenCustomValidate();
        then().validateAsFalse().and().returnNotUriActionError().and().theseErrorsAreOnly(1);
	}
    
    public void shouldValidateAsWrongExternalActionRichPopupActionUriWithWrongBodyAndActionButtonTextLenghts() {
        given().richPopup().bodyLenghtIs(1001).and().actionIs("notUri").and().actionTypeIs(EXTERNAL_URL).and().actionButtonTextLenghtIs(256);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLenghtError().and().returnNotUriActionError().and().returnWrongActionButtonTextLenght().and().theseErrorsAreOnly(3);
	}
    
    public void shouldValidateAsWrongExternalActionRichPopupActionValueWithWrongBodyAndActionButtonTextLenghts() {
        given().richPopup().bodyLenghtIs(1001).and().actionIs(null).and().actionTypeIs(EXTERNAL_URL).and().actionButtonTextLenghtIs(256);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLenghtError().and().returnNullActionError().and().returnWrongActionButtonTextLenght().and().theseErrorsAreOnly(3);
	}
    
    public void shouldValidateAsWrongMobileWebPortalRichPopupActionValueWithWrongBodyAndActionButtonTextLenghts() {
        given().richPopup().bodyLenghtIs(1001).and().actionIs(null).and().actionTypeIs(MOBILE_WEB_PORTAL).and().actionButtonTextLenghtIs(256);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLenghtError().and().returnNullActionError().and().returnWrongActionButtonTextLenght().and().theseErrorsAreOnly(3);
	}
    
    public void shouldValidateAsWrongSpecificNewsStoryRichPopupActionValueWithWrongBodyAndActionButtonTextLenghts() {
        given().richPopup().bodyLenghtIs(1001).and().actionIs(null).and().actionTypeIs(A_SPECIFIC_NEWS_STORY).and().actionButtonTextLenghtIs(256);
        whenCustomValidate();
        then().validateAsFalse().and().returnNullActionError().and().theseErrorsAreOnly(3);
	}
    
    public void shouldValidateAsWrongSpecificTrackRichPopupActionValueWithWrongBodyAndActionButtonTextLenghts() {
        given().richPopup().bodyLenghtIs(1001).and().actionIs(null).and().actionTypeIs(A_SPECIFIC_TRACK).and().actionButtonTextLenghtIs(256);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLenghtError().and().returnNullActionError().and().returnWrongActionButtonTextLenght().and().theseErrorsAreOnly(3);
	}
    
    public void shouldValidateAsWrongOfficialTop40PlaylistRichPopupActionValueWithWrongBodyAndActionButtonTextLenghts() {
        given().richPopup().bodyLenghtIs(1001).and().actionIs(null).and().actionTypeIs(OFFICIAL_TOP_40_PLAYLIST).and().actionButtonTextLenghtIs(256);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLenghtError().and().returnWrongActionButtonTextLenght().theseErrorsAreOnly(2);
	}

    private void whenCustomValidate() {
        actualHasErrors = messageDtoValidator.customValidate(messageDto, errors);
    }
    
    private MessageDtoValidatorTest actionButtonTextLenghtIs(int size) {
		messageDto.setActionButtonText(getChars(size));
		return this;
	}

    private MessageDtoValidatorTest news() {
        messageDto.setMessageType(MessageType.NEWS);
        return this;
    }
    
    private MessageDtoValidatorTest richPopup() {
        messageDto.setMessageType(MessageType.RICH_POPUP);
        return this;
    }
    
    private MessageDtoValidatorTest actionTypeIs(MessageActionType actionType) {
		messageDto.setActionType(actionType);
		return this;
	}
  
    private MessageDtoValidatorTest actionLenghtIs(int size) {
    	messageDto.setAction(getChars(size));	
    	return this;
    }
    
    private MessageDtoValidatorTest actionIs(String action) {
    	messageDto.setAction(action);	
    	return this;
    }

    private void nullBody(){
        messageDto.setBody(null);
    }

    private void theseErrorsAreOnly(int count) {
        assertEquals(count, errors.getErrorCount());
    }
    
    private void validateAsNoError() {
        assertFalse(actualHasErrors);
        assertEquals(false, errors.hasErrors());
    }

    private MessageDtoValidatorTest validateAsFalse() {
        assertTrue(actualHasErrors);
        assertEquals(true, errors.hasErrors());
        return this;
    }

    private MessageDtoValidatorTest bodyLenghtIs(int size) {
        messageDto.setBody(getChars(size));
        return this;
    }

    private MessageDtoValidatorTest onceAfterFirstDownloadFrequence() {
        messageDto.setFrequence(ONCE_AFTER_1ST_TRACK_DOWNLOAD);
        return this;
    }
    
    private MessageDtoValidatorTest actionButtonTextIsNull() {
    	messageDto.setActionButtonText(null);
		return this;
	}

    private MessageDtoValidatorTest given(){
        return this;
    }

    private MessageDtoValidatorTest and(){
        return this;
    }

    private MessageDtoValidatorTest then(){
        return this;
    }

    private MessageDtoValidatorTest returnWrongNewsBodyLenghtError() {
        FieldError fieldError = errors.getFieldError(BODY);

        assertEquals(MESSAGE_BODY_WRONG_SIZE, fieldError.getCode());
        assertEquals(THE_BODY_FIELD_MUST_CONSIST_OF_0_1_CHARACTERS_FOR_THIS_MESSAGE_TYPE, fieldError.getDefaultMessage());

        return this;
    }
    
    private MessageDtoValidatorTest returnWrongRichPopupBodyLenghtError() {
        FieldError fieldError = errors.getFieldError(BODY);

        assertEquals(RICH_POPUP_BODY_WRONG_SIZE, fieldError.getCode());
        assertEquals(THE_BODY_FIELD_MUST_CONSIST_OF_0_1_CHARACTERS_FOR_THIS_MESSAGE_TYPE, fieldError.getDefaultMessage());

        return this;
    }

    private MessageDtoValidatorTest returnNullBodyError() {
        FieldError fieldError = errors.getFieldError(BODY);

        assertEquals(MESSAGE_BODY_IS_BLANK, fieldError.getCode());
        assertEquals(THE_BODY_FIELD_COULDN_T_BE_NULL_EMPTY_OR_BLANK, fieldError.getDefaultMessage());

        return this;
    }
    
    private MessageDtoValidatorTest returnWrongFrequenceError() {
    	FieldError fieldError = errors.getFieldError(FREQUENCE);
    	
    	assertEquals(MESSAGE_NOT_RICH_POPUP_FREQUENCE_IS_ONCE_AFTER1ST_TRACK_DOWNLOAD, fieldError.getCode());
    	assertEquals(THE_FREQUENCE_FIELD_COULDN_T_BE_SUCH_SELECTED_OPTION_FOR_THIS_MESSAGE_TYPE, fieldError.getDefaultMessage());
    	
		return this;
    }
    
    private MessageDtoValidatorTest returnWrongActionLenght() {
    	FieldError fieldError = errors.getFieldError(ACTION);
    	
    	assertEquals(RICH_POPUP_ACTION_WRONG_SIZE, fieldError.getCode());
    	assertEquals(THE_ACTION_FIELD_MUST_CONSIST_OF_1_255_CHARACTERS, fieldError.getDefaultMessage());
    	
		return this;
    }
    
    private MessageDtoValidatorTest returnWrongActionButtonTextLenght() {
    	FieldError fieldError = errors.getFieldError(ACTION_BUTTON_TEXT);
    	
    	assertEquals(RICH_POPUP_ACTION_BUTTON_TEXT_WRONG_SIZE, fieldError.getCode());
    	assertEquals(THE_ACTION_BUTTON_TEXT_FIELD_MUST_CONSIST_OF_1_255_CHARACTERS, fieldError.getDefaultMessage());
    	
		return this;
    }
    
    private MessageDtoValidatorTest returnWrongActionButtonTextValue() {
    	FieldError fieldError = errors.getFieldError(ACTION_BUTTON_TEXT);
    	
    	assertEquals(RICH_POPUP_ACTION_BUTTON_TEXT_IS_NULL_EMPTY_OR_BLANK, fieldError.getCode());
    	assertEquals(THE_ACTION_BUTTON_TEXT_FIELD_COULDN_T_BE_NULL_EMPTY_OR_BLANK, fieldError.getDefaultMessage());
    	
		return this;
	}
    
    private MessageDtoValidatorTest returnNotUriActionError() {
    	FieldError fieldError = errors.getFieldError(ACTION);
    	
    	assertEquals(RICH_POPUP_ACTION_NOT_URL, fieldError.getCode());
    	assertEquals(THE_ACTION_SHOULD_CONTAIN_URL_FOR_THIS_ACTION_TYPE, fieldError.getDefaultMessage());
    	
		return this;
	}
    
    private MessageDtoValidatorTest returnNullActionError() {
    	FieldError fieldError = errors.getFieldError(ACTION);
    	
    	assertEquals(RICH_POPUP_ACTION_IS_NULL_EMPTY_OR_BLANK, fieldError.getCode());
    	assertEquals(THE_ACTION_FIELD_COULDN_T_BE_NULL_EMPTY_OR_BLANK_FOR_THIS_ACTION_TYPE, fieldError.getDefaultMessage());
    	
		return this;
	}
    
    private String getChars(int size) {
		return new String(new char[size]).replace('\0', 'm');
	}

}
