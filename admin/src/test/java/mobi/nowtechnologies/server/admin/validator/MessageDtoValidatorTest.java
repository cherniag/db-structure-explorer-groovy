package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.shared.dto.admin.MessageDto;
import mobi.nowtechnologies.server.shared.enums.MessageActionType;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.ACTION;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.ACTION_BUTTON_TEXT;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.BODY;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.FREQUENCE;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.MESSAGE_BODY_IS_BLANK;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.MESSAGE_BODY_WRONG_SIZE;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.MESSAGE_NOT_RICH_POPUP_FREQUENCE_IS_ONCE_AFTER1ST_TRACK_DOWNLOAD;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.RICH_POPUP_ACTION_BUTTON_TEXT_IS_NULL_EMPTY_OR_BLANK;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.RICH_POPUP_ACTION_BUTTON_TEXT_WRONG_SIZE;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.RICH_POPUP_ACTION_IS_NULL_EMPTY_OR_BLANK;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.RICH_POPUP_ACTION_NOT_URL;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.RICH_POPUP_ACTION_SHOULD_BE_NULL;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.RICH_POPUP_ACTION_WRONG_SIZE;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.RICH_POPUP_BODY_WRONG_SIZE;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.THE_ACTION_BUTTON_TEXT_FIELD_COULDN_T_BE_NULL_EMPTY_OR_BLANK;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.THE_ACTION_BUTTON_TEXT_FIELD_MUST_CONSIST_OF_1_255_CHARACTERS;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.THE_ACTION_FIELD_COULDN_T_BE_NULL_EMPTY_OR_BLANK_FOR_THIS_ACTION_TYPE;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.THE_ACTION_FIELD_MUST_CONSIST_OF_1_255_CHARACTERS;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.THE_ACTION_FIELD_SHOULD_BE_NULL_FOR_THIS_ACTION_TYPE;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.THE_ACTION_SHOULD_CONTAIN_URL_FOR_THIS_ACTION_TYPE;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.THE_BODY_FIELD_COULDN_T_BE_NULL_EMPTY_OR_BLANK;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.THE_BODY_FIELD_MUST_CONSIST_OF_0_1_CHARACTERS_FOR_THIS_MESSAGE_TYPE;
import static mobi.nowtechnologies.server.admin.validator.MessageDtoValidator.THE_FREQUENCE_FIELD_COULDN_T_BE_SUCH_SELECTED_OPTION_FOR_THIS_MESSAGE_TYPE;
import static mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageFrequence.ONCE_AFTER_1ST_TRACK_DOWNLOAD;
import static mobi.nowtechnologies.server.shared.enums.MessageActionType.A_SPECIFIC_NEWS_STORY;
import static mobi.nowtechnologies.server.shared.enums.MessageActionType.A_SPECIFIC_TRACK;
import static mobi.nowtechnologies.server.shared.enums.MessageActionType.EXTERNAL_URL;
import static mobi.nowtechnologies.server.shared.enums.MessageActionType.MOBILE_WEB_PORTAL;
import static mobi.nowtechnologies.server.shared.enums.MessageActionType.OFFICIAL_TOP_40_PLAYLIST;
import static mobi.nowtechnologies.server.shared.enums.MessageActionType.OUR_PLAYLIST;
import static mobi.nowtechnologies.server.shared.enums.MessageType.NEWS;
import static mobi.nowtechnologies.server.shared.enums.MessageType.RICH_POPUP;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;

import org.junit.*;
import org.junit.runner.*;
import static org.junit.Assert.*;

import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertFalse;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(PowerMockRunner.class)
public class MessageDtoValidatorTest {

    private MessageDto messageDto = new MessageDto();

    private Errors errors = new MapBindingResult(Collections.emptyMap(), "");
    private MessageDtoValidator messageDtoValidator = new MessageDtoValidator();
    private boolean actualHasErrors;


    @Test
    public void shouldValidateAsNoErrorsNews() {
        given().news().bodyLengthIs(255);
        whenCustomValidate();
        then().validateAsNoError();
    }

    @Test
    public void shouldValidateAsWrongNewsBodySize() {
        given().news().and().bodyLengthIs(256);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongNewsBodyLengthError().and().theseErrorsAreOnly(1);
    }

    @Test
    public void shouldValidateAsWrongNewsBodyValue() {
        given().news().and().nullBody();
        whenCustomValidate();
        then().validateAsFalse().returnNullBodyError().and().theseErrorsAreOnly(1);
    }

    @Test
    public void shouldValidateAsWrongNewsFrequenceValue() {
        given().news().and().bodyLengthIs(255).and().onceAfterFirstDownloadFrequence();
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongFrequenceError().and().theseErrorsAreOnly(1);
    }

    @Test
    public void shouldValidateAsWrongNewsFrequenceValueAndWrongBodySize() {
        given().news().and().bodyLengthIs(256).and().onceAfterFirstDownloadFrequence();
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongFrequenceError().and().returnWrongNewsBodyLengthError().and().theseErrorsAreOnly(2);
    }

    @Test
    public void shouldValidateAsNoErrorsRichPopup() {
        given().richPopup().bodyLengthIs(1000).and().actionLengthIs(255).and().actionTypeIs(A_SPECIFIC_TRACK).and().actionButtonTextLengthIs(255);
        whenCustomValidate();
        then().validateAsNoError();
    }

    @Test
    public void shouldValidateAsWrongRichPopupBodyLength() {
        given().richPopup().bodyLengthIs(1001).and().actionLengthIs(255).and().actionTypeIs(A_SPECIFIC_TRACK).and().actionButtonTextLengthIs(255);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLengthError().and().theseErrorsAreOnly(1);
    }

    @Test
    public void shouldValidateAsWrongRichPopupBodyAndActionLengths() {
        given().richPopup().bodyLengthIs(1001).and().actionLengthIs(256).and().actionTypeIs(A_SPECIFIC_TRACK).and().actionButtonTextLengthIs(255);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLengthError().and().returnWrongActionLength().theseErrorsAreOnly(2);
    }

    @Test
    public void shouldValidateAsWrongRichPopupBodyAndActionButtonTextLengths() {
        given().richPopup().bodyLengthIs(1001).and().actionLengthIs(255).and().actionTypeIs(A_SPECIFIC_TRACK).and().actionButtonTextLengthIs(256);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLengthError().and().returnWrongActionButtonTextLength().theseErrorsAreOnly(2);
    }

    @Test
    public void shouldValidateAsWrongRichPopupBodyLengthAndActionButtonIsNull() {
        given().richPopup().bodyLengthIs(1001).and().actionLengthIs(255).and().actionTypeIs(A_SPECIFIC_TRACK).and().actionButtonTextIsNull();
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLengthError().and().returnWrongActionButtonTextValue().theseErrorsAreOnly(2);
    }

    @Test
    public void shouldValidateAsNoErrorsMobileWebPortalRichPopup() {
        given().richPopup().bodyLengthIs(1000).and().actionIs("http://i.ua").and().actionTypeIs(MOBILE_WEB_PORTAL).and().actionButtonTextLengthIs(255);
        whenCustomValidate();
        then().validateAsNoError();
    }

    @Test
    public void shouldValidateAsNoErrorsExternalUrlRichPopup() {
        given().richPopup().bodyLengthIs(1000).and().actionIs("http://i.ua").and().actionTypeIs(EXTERNAL_URL).and().actionButtonTextLengthIs(255);
        whenCustomValidate();
        then().validateAsNoError();
    }

    @Test
    public void shouldValidateAsNotNullActionRichPopup() {
        given().richPopup().bodyLengthIs(1000).and().actionIs("http://i.ua").and().actionTypeIs(OUR_PLAYLIST).and().actionButtonTextLengthIs(255);
        whenCustomValidate();
        then().validateAsFalse().and().returnNotNullActionError().and().theseErrorsAreOnly(1);
    }

    @Test
    public void shouldValidateAsNoErrorRichPopup() {
        given().richPopup().bodyLengthIs(1000).and().actionIs(null).and().actionTypeIs(OUR_PLAYLIST).and().actionButtonTextLengthIs(255);
        whenCustomValidate();
        then().validateAsNoError();
    }

    @Test
    public void shouldValidateAsWrongMobileWebPortalRichPopupActionUri() {
        given().richPopup().bodyLengthIs(1000).and().actionIs("notUri").and().actionTypeIs(MOBILE_WEB_PORTAL).and().actionButtonTextLengthIs(255);
        whenCustomValidate();
        then().validateAsFalse().and().returnNotUriActionError().and().theseErrorsAreOnly(1);
    }

    @Test
    public void shouldValidateAsWrongExternalActionRichPopupActionUri() {
        given().richPopup().bodyLengthIs(1000).and().actionIs("notUri").and().actionTypeIs(EXTERNAL_URL).and().actionButtonTextLengthIs(255);
        whenCustomValidate();
        then().validateAsFalse().and().returnNotUriActionError().and().theseErrorsAreOnly(1);
    }

    @Test
    public void shouldValidateAsWrongExternalActionRichPopupActionUriWithWrongBodyAndActionButtonTextLengths() {
        given().richPopup().bodyLengthIs(1001).and().actionIs("notUri").and().actionTypeIs(EXTERNAL_URL).and().actionButtonTextLengthIs(256);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLengthError().and().returnNotUriActionError().and().returnWrongActionButtonTextLength().and().theseErrorsAreOnly(3);
    }

    @Test
    public void shouldValidateAsWrongExternalActionRichPopupActionValueWithWrongBodyAndActionButtonTextLengths() {
        given().richPopup().bodyLengthIs(1001).and().actionIs(null).and().actionTypeIs(EXTERNAL_URL).and().actionButtonTextLengthIs(256);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLengthError().and().returnNullActionError().and().returnWrongActionButtonTextLength().and().theseErrorsAreOnly(3);
    }

    @Test
    public void shouldValidateAsWrongMobileWebPortalRichPopupActionValueWithWrongBodyAndActionButtonTextLengths() {
        given().richPopup().bodyLengthIs(1001).and().actionIs(null).and().actionTypeIs(MOBILE_WEB_PORTAL).and().actionButtonTextLengthIs(256);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLengthError().and().returnNullActionError().and().returnWrongActionButtonTextLength().and().theseErrorsAreOnly(3);
    }

    @Test
    public void shouldValidateAsWrongSpecificNewsStoryRichPopupActionValueWithWrongBodyAndActionButtonTextLengths() {
        given().richPopup().bodyLengthIs(1001).and().actionIs(null).and().actionTypeIs(A_SPECIFIC_NEWS_STORY).and().actionButtonTextLengthIs(256);
        whenCustomValidate();
        then().validateAsFalse().and().returnNullActionError().and().theseErrorsAreOnly(3);
    }

    @Test
    public void shouldValidateAsWrongSpecificTrackRichPopupActionValueWithWrongBodyAndActionButtonTextLengths() {
        given().richPopup().bodyLengthIs(1001).and().actionIs(null).and().actionTypeIs(A_SPECIFIC_TRACK).and().actionButtonTextLengthIs(256);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLengthError().and().returnNullActionError().and().returnWrongActionButtonTextLength().and().theseErrorsAreOnly(3);
    }

    @Test
    public void shouldValidateAsWrongOfficialTop40PlaylistRichPopupActionValueWithWrongBodyAndActionButtonTextLengths() {
        given().richPopup().bodyLengthIs(1001).and().actionIs(null).and().actionTypeIs(OFFICIAL_TOP_40_PLAYLIST).and().actionButtonTextLengthIs(256);
        whenCustomValidate();
        then().validateAsFalse().and().returnWrongRichPopupBodyLengthError().and().returnWrongActionButtonTextLength().theseErrorsAreOnly(2);
    }

    private void whenCustomValidate() {
        actualHasErrors = messageDtoValidator.customValidate(messageDto, errors);
    }

    private MessageDtoValidatorTest actionButtonTextLengthIs(int size) {
        messageDto.setActionButtonText(getChars(size));
        return this;
    }

    private MessageDtoValidatorTest news() {
        messageDto.setMessageType(NEWS);
        messageDto.setHeadline("the head line");
        return this;
    }

    private MessageDtoValidatorTest richPopup() {
        messageDto.setMessageType(RICH_POPUP);
        messageDto.setHeadline("the head line");
        return this;
    }

    private MessageDtoValidatorTest actionTypeIs(MessageActionType actionType) {
        messageDto.setActionType(actionType);
        return this;
    }

    private MessageDtoValidatorTest actionLengthIs(int size) {
        messageDto.setAction(getChars(size));
        return this;
    }

    private MessageDtoValidatorTest actionIs(String action) {
        messageDto.setAction(action);
        return this;
    }

    private void nullBody() {
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

    private MessageDtoValidatorTest bodyLengthIs(int size) {
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

    private MessageDtoValidatorTest given() {
        return this;
    }

    private MessageDtoValidatorTest and() {
        return this;
    }

    private MessageDtoValidatorTest then() {
        return this;
    }

    private MessageDtoValidatorTest returnWrongNewsBodyLengthError() {
        FieldError fieldError = errors.getFieldError(BODY);

        assertEquals(MESSAGE_BODY_WRONG_SIZE, fieldError.getCode());
        assertEquals(THE_BODY_FIELD_MUST_CONSIST_OF_0_1_CHARACTERS_FOR_THIS_MESSAGE_TYPE, fieldError.getDefaultMessage());

        return this;
    }

    private MessageDtoValidatorTest returnWrongRichPopupBodyLengthError() {
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

    private MessageDtoValidatorTest returnWrongActionLength() {
        FieldError fieldError = errors.getFieldError(ACTION);

        assertEquals(RICH_POPUP_ACTION_WRONG_SIZE, fieldError.getCode());
        assertEquals(THE_ACTION_FIELD_MUST_CONSIST_OF_1_255_CHARACTERS, fieldError.getDefaultMessage());

        return this;
    }

    private MessageDtoValidatorTest returnWrongActionButtonTextLength() {
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

    private MessageDtoValidatorTest returnNotNullActionError() {
        FieldError fieldError = errors.getFieldError(ACTION);

        assertEquals(RICH_POPUP_ACTION_SHOULD_BE_NULL, fieldError.getCode());
        assertEquals(THE_ACTION_FIELD_SHOULD_BE_NULL_FOR_THIS_ACTION_TYPE, fieldError.getDefaultMessage());

        return this;
    }

    private String getChars(int size) {
        return StringUtils.repeat('m', size);
    }

}
