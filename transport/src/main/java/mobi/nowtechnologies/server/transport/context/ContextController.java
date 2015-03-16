package mobi.nowtechnologies.server.transport.context;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.transport.context.dto.ContextDto;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;

import javax.annotation.Resource;

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

    @RequestMapping(method = GET,
                    value = {"**/{community}/{apiVersion:6\\.10}/CONTEXT", "**/{community}/{apiVersion:6\\.9}/CONTEXT", "**/{community}/{apiVersion:6\\.8}/CONTEXT"})
    public ModelAndView getContext(@AuthenticatedUser User user) throws Exception {
        return getContext(user, true);
    }

    @RequestMapping(method = GET,
                    value = {"**/{community}/{apiVersion:6\\.7}/CONTEXT"})
    public ModelAndView getContextNoFreemiumSupport(@AuthenticatedUser User user) throws Exception {
        return getContext(user, false);
    }

    public ModelAndView getContext(@AuthenticatedUser User user, boolean needToLookAtActivationDate) throws Exception {
        LOGGER.info("command processing started");
        Exception ex = null;
        try {
            userService.authorize(user, false, ActivationStatus.ACTIVATED);

            ContextDto contextDto = contextDtoAsm.assemble(user, needToLookAtActivationDate);

            return createModelAndView(contextDto);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(null, getCurrentCommunityUri(), null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    private ModelAndView createModelAndView(ContextDto contextDto) {
        return new ModelAndView("default", "context", contextDto);
    }
}
