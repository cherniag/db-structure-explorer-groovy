package mobi.nowtechnologies.server.transport.context;

import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.referral.UserReferralsSnapshot;
import mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser;
import mobi.nowtechnologies.server.service.behavior.BehaviorService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.transport.context.dto.ContextDto;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Date;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by zam on 11/21/2014.
 */
@Controller
public class ContextController extends CommonController {
    @Resource
    BehaviorService behaviorService;
    @Resource
    ContextDtoAsm contextDtoAsm;
    @Resource
    TimeService timeService;

    @RequestMapping(method = GET,
            value = {
                    "**/{community}/{apiVersion:6\\.8}/CONTEXT",
                    "**/{community}/{apiVersion:6\\.7}/CONTEXT"
            })
    public ModelAndView getContext(@AuthenticatedUser User user) throws Exception {
        LOGGER.info("command processing started");
        Exception ex = null;
        try {
            userService.authorize(user, false, ActivationStatus.ACTIVATED);

            Date now = timeService.now();

            ContextDto contextDto = decideContextDto(user, now);

            return createModelAndView(contextDto);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(null, getCurrentCommunityUri(), null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    private ContextDto decideContextDto(User user, Date now) {
        if (behaviorService.isFreemiumActivated(user, now)) {
            UserReferralsSnapshot snapshot = behaviorService.getSnapshot(user);
            return contextDtoAsm.assemble(user, snapshot, now);
        } else {
            return contextDtoAsm.assemble();
        }
    }

    private ModelAndView createModelAndView(ContextDto contextDto) {
        return new ModelAndView("default", "context", contextDto);
    }
}
