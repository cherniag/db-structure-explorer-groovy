package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.dto.transport.ReferralContextDto;
import mobi.nowtechnologies.server.dto.transport.ReferralContextDtoFactory;
import mobi.nowtechnologies.server.dto.transport.UserContextDto;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

// Created by zam on 11/21/2014.
@Controller
public class UserContextController extends CommonController {

    private ReferralContextDtoFactory referralContextDtoFactory;

    @Resource
    public void setReferralContextDtoFactory(ReferralContextDtoFactory referralContextDtoFactory) {
        this.referralContextDtoFactory = referralContextDtoFactory;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"**/{community}/{apiVersion:6.7}/CONTEXT"})
    public ModelAndView getContext(@AuthenticatedUser User user) throws Exception {
        Exception ex = null;
        try {
            userService.authorize(user, false, ActivationStatus.ACTIVATED);

            ReferralContextDto referralContextDto = referralContextDtoFactory.getReferralContextDto(user);

            UserContextDto userContextDto = new UserContextDto();
            userContextDto.setReferralContextDto(referralContextDto);

            // and model based on it
            return new ModelAndView("default", "context", userContextDto);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(null, getCurrentCommunityUri(), null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }
}
