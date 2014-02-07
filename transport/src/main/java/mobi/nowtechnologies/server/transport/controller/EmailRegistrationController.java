package mobi.nowtechnologies.server.transport.controller;


import mobi.nowtechnologies.server.service.MailService;
import mobi.nowtechnologies.server.service.exception.ValidationException;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.util.EmailValidator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
public class EmailRegistrationController extends CommonController {

    private MailService mailService;
    private CommunityResourceBundleMessageSource messageSource;

    @RequestMapping(method = RequestMethod.POST,
            value = "**/{community:o2}/{apiVersion:[4-9]{1}\\.[0-9]{1,3}}/EMAIL_GENERATE")
    public ModelAndView sendConfirmationEmail(@RequestParam(value = "EMAIL") String email,
                                              @RequestParam(value = "USER_NAME") String userName,
                                              @RequestParam(value = "DEVICE_UID") String deviceUID,
                                              @PathVariable(value = "community") String community) {
        if (EmailValidator.isEmail(email)) {
            Map<String, String> params = new HashMap<String, String>();
            String from = messageSource.getMessage(community, "activation.email.from", null, null, null);
            String subject = messageSource.getMessage(community, "activation.email.subject", null, null, null);
            String body = messageSource.getMessage(community, "activation.email.body", null, null, null);
            mailService.sendMail(from, new String[]{email}, subject, body, params);
        } else {
            throw new ValidationException("Email " + email + " is not valid!");
        }
        return buildModelAndView(null);
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
