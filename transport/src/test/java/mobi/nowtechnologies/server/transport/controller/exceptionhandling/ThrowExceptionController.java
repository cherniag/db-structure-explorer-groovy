package mobi.nowtechnologies.server.transport.controller.exceptionhandling;

import mobi.nowtechnologies.server.service.exception.ActivationStatusException;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;
import mobi.nowtechnologies.server.service.exception.LimitPhoneNumberValidationException;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ThrowExceptionController extends CommonController {

    public static final String ACTIVATION_STATUS_ERROR_URL = "/testActivationStatus";
    public static final String INVALID_PHONE_NUMBER_ERROR_URL = "/testInvalidPhoneNumber";
    public static final String LIMIT_PHONE_NUMBER_ERROR_URL = "/testLimitPhoneNumberErrorUrl";

    @RequestMapping(ACTIVATION_STATUS_ERROR_URL)
    public void raiseActivationStatusException() {
        throw new ActivationStatusException(ActivationStatus.REGISTERED, ActivationStatus.PENDING_ACTIVATION);
    }

    @RequestMapping(INVALID_PHONE_NUMBER_ERROR_URL)
    public void raiseInvalidPhoneNumberException() {
        throw new InvalidPhoneNumberException("777");
    }

    @RequestMapping(LIMIT_PHONE_NUMBER_ERROR_URL)
    public void raiseLimitPhoneNumberErrorUrlException() {
        throw new LimitPhoneNumberValidationException("777", "http");
    }

}
