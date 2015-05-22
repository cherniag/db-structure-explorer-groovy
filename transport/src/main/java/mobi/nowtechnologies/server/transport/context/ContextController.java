package mobi.nowtechnologies.server.transport.context;

import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser;
import mobi.nowtechnologies.server.service.behavior.PaymentTimeService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.transport.context.dto.ContextDto;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by zam on 11/21/2014.
 */
@Controller
public class ContextController extends CommonController {

    @Resource
    ContextDtoAsm contextDtoAsm;
    @Resource
    TimeService timeService;
    @Resource
    PaymentTimeService paymentTimeService;

    @RequestMapping(method = GET,
                    value = {"**/{community}/{apiVersion:6\\.11}/CONTEXT", "**/{community}/{apiVersion:6\\.10}/CONTEXT", "**/{community}/{apiVersion:6\\.9}/CONTEXT",
                        "**/{community}/{apiVersion:6\\.8}/CONTEXT"})
    public ModelAndView getContext(@AuthenticatedUser User user, HttpServletResponse response) throws Exception {
        return getContext(user, true, response);
    }

    @RequestMapping(method = GET,
                    value = {"**/{community}/{apiVersion:6\\.7}/CONTEXT"})
    public ModelAndView getContextNoFreemiumSupport(@AuthenticatedUser User user, HttpServletResponse response) throws Exception {
        return getContext(user, false, response);
    }

    public ModelAndView getContext(User user, boolean needToLookAtActivationDate, HttpServletResponse response) throws Exception {
        userService.authorize(user, false, ActivationStatus.ACTIVATED);

        final Date serverTime = timeService.now();

        ContextDto contextDto = contextDtoAsm.assemble(user, needToLookAtActivationDate, serverTime);

        handleExpires(user, response, serverTime);

        return createModelAndView(contextDto);
    }

    private void handleExpires(User user, HttpServletResponse response, Date serverTime) {
        if (user.isPaymentInProgress()) {
            Date nextRetryTime = paymentTimeService.getNextRetryTime(user, serverTime);
            if (nextRetryTime != null) {
                response.setDateHeader("Expires", nextRetryTime.getTime());
            }
        }
    }

    private ModelAndView createModelAndView(ContextDto contextDto) {
        return new ModelAndView("default", "context", contextDto);
    }
}
