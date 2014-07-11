package mobi.nowtechnologies.server.transport.controller.exceptionhandling;

import mobi.nowtechnologies.server.log4j.InMemoryEventAppender;
import mobi.nowtechnologies.server.service.exception.ActivationStatusException;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;
import mobi.nowtechnologies.server.service.exception.LimitPhoneNumberValidationException;
import mobi.nowtechnologies.server.transport.controller.AbstractControllerTestIT;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by Oleg Artomov on 6/20/2014.
 */
public class ExceptionHandlingControllerIT extends AbstractControllerTestIT {
    private InMemoryEventAppender inMemoryEventAppender = new InMemoryEventAppender();

    @After
    public void onComplete() {
        Logger.getRootLogger().removeAppender(inMemoryEventAppender);
    }

    @Before
    public void onStart() throws Exception {
        Logger.getRootLogger().addAppender(inMemoryEventAppender);
    }

    @Test
    public void testInvalidPhoneNumberException() throws Exception {
        mockMvc.perform(post(ThrowExceptionController.INVALID_PHONE_NUMBER_ERROR_URL));
        validateLoggingForClass(ThrowExceptionController.class, InvalidPhoneNumberException.class, 0, 1, 1);
    }

    @Test
    public void testActivationStatusException() throws Exception {
        mockMvc.perform(post(ThrowExceptionController.ACTIVATION_STATUS_ERROR_URL));
        validateLoggingForClass(ThrowExceptionController.class, ActivationStatusException.class, 0, 1, 1);
    }

    @Test
    public void testLimitPhoneNumberErrorUrlException() throws Exception {
        mockMvc.perform(post(ThrowExceptionController.LIMIT_PHONE_NUMBER_ERROR_URL));
        validateLoggingForClass(ThrowExceptionController.class, LimitPhoneNumberValidationException.class, 0, 1, 1);
    }


    private void validateLoggingForClass(Class loggerClass, Class throwableClass, int expectedForCritical, int expectedForWarn, int totalCountWithStackTrace) {
        assertEquals(expectedForCritical, inMemoryEventAppender.countOfErrorsWithStackTraceForLogger(loggerClass));
        assertEquals(expectedForWarn, inMemoryEventAppender.countOfWarnWithStackTraceForLogger(loggerClass));
        assertEquals(totalCountWithStackTrace, inMemoryEventAppender.totalCountOfMessagesWithStackTraceForException(throwableClass));
    }



}
