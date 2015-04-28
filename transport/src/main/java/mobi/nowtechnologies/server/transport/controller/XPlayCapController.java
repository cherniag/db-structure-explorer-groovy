/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.ErrorMessage;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser;
import mobi.nowtechnologies.server.service.itunes.ITunesService;
import mobi.nowtechnologies.server.service.itunes.ITunesXPlayCapSubscriptionException;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class XPlayCapController extends CommonController {

    @Resource
    ITunesService iTunesService;

    @Resource
    PaymentPolicyRepository paymentPolicyRepository;

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community}/{apiVersion:6\\.11}/X_PLAY_CAP"})
    ModelAndView postPlayCap(@AuthenticatedUser User user, @RequestParam(value = "TRANSACTION_RECEIPT") String receipt) throws ITunesXPlayCapSubscriptionException {
        LOGGER.info("command processing started");
        try {
            userService.authorize(user, false, ActivationStatus.ACTIVATED);

            Map<String, ?> playCapValue = iTunesService.processXPlayCapSubscription(user, receipt);

            return new ModelAndView("default", playCapValue);
        } finally {
            LOGGER.info("command processing finished");
        }
    }

    @ExceptionHandler(ITunesXPlayCapSubscriptionException.class)
    public ModelAndView handleException(ITunesXPlayCapSubscriptionException exception, HttpServletResponse response) {
        ErrorMessage errorMessage = getErrorMessage(null, null, exception.getServerMessage().getErrorCode());
        return sendResponse(errorMessage, HttpStatus.CONFLICT, response);
    }
}
