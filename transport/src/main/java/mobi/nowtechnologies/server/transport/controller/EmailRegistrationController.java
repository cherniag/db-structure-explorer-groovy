package mobi.nowtechnologies.server.transport.controller;


import mobi.nowtechnologies.server.persistence.domain.ActivationEmail;
import mobi.nowtechnologies.server.service.ActivationEmailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EmailRegistrationController extends CommonController {

    private ActivationEmailService activationEmailService;

    @RequestMapping(method = RequestMethod.POST,
            value = "**/{community}/{apiVersion:[4-9]{1}\\.[0-9]{1,3}}/EMAIL_GENERATE")
    public ModelAndView sendConfirmationEmail(@RequestParam(value = "EMAIL") String email,
                                              @RequestParam(value = "USER_NAME") String userName,
                                              @RequestParam(value = "DEVICE_UID") String deviceUID,
                                              @PathVariable(value = "community") String community) {
        LOGGER.info("EMAIL_GENERATE started for userName: [{}], email: [{}], community: [{}]", userName, email, community);
        ActivationEmail activationEmail = activationEmailService.sendEmail(email, userName, deviceUID, community);
        LOGGER.info("EMAIL_GENERATE finished");
        return buildModelAndView(activationEmail.getId());
    }

    public void setActivationEmailService(ActivationEmailService activationEmailService) {
        this.activationEmailService = activationEmailService;
    }
}
