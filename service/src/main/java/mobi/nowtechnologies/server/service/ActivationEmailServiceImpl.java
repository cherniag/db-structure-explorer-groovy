package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.ActivationEmail;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.ActivationEmailRepository;
import mobi.nowtechnologies.server.service.exception.ValidationException;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.util.EmailValidator;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;



@Transactional
public class ActivationEmailServiceImpl implements ActivationEmailService {

    private ActivationEmailRepository activationEmailRepository;

    private UserService userService;

    private MailService mailService;

    private CommunityResourceBundleMessageSource messageSource;

    @Override
    public void save(ActivationEmail activationEmail) {
        activationEmailRepository.save(activationEmail);
    }

    public void sendEmail(String email, String userName, String deviceUID, String community) {
        if (EmailValidator.isEmail(email)) {
            User user = userService.findByNameAndCommunity(userName, community);
            String token = ActivationEmail.generateToken(email, user);

            Map<String, String> params = new HashMap<String, String>();
            String from = messageSource.getMessage(community, "activation.email.from", null, null, null);
            String subject = messageSource.getMessage(community, "activation.email.subject", null, null, null);
            String body = messageSource.getMessage(community, "activation.email.body", null, null, null);
            mailService.sendMail(from, new String[]{email}, subject, body, params);

            save(new ActivationEmail(user, email, deviceUID, token));
        } else {
            throw new ValidationException("Email " + email + " is not valid!");
        }
    }

    public void setActivationEmailRepository(ActivationEmailRepository activationEmailRepository) {
        this.activationEmailRepository = activationEmailRepository;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
