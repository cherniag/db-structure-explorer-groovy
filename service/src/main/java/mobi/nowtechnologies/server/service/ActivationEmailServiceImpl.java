package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.ActivationEmail;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.ActivationEmailRepository;
import mobi.nowtechnologies.server.service.exception.ValidationException;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.util.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;



@Transactional
public class ActivationEmailServiceImpl implements ActivationEmailService {

    private ActivationEmailRepository activationEmailRepository;

    private UserService userService;

    private MailService mailService;

    private CommunityResourceBundleMessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivationEmailServiceImpl.class);

    @Override
    public void save(ActivationEmail activationEmail) {
        activationEmailRepository.save(activationEmail);
    }

    @Override
    public void activate(Long id, String email, String token) {
        LOGGER.info("Activating email with id: [{}], email: [{}], token: []", id, email, token);
        ActivationEmail activationEmail = activationEmailRepository.findOne(id);
        Assert.isTrue(!activationEmail.isActivated(), "ActivationEmail must not be activated");
        Assert.isTrue(activationEmail.getToken().equals(token), "Wrong token");
        Assert.isTrue(email.equals(activationEmail.getEmail()), "Wrong email");
        activationEmail.setActivated(true);

        save(activationEmail);
        LOGGER.info("Email activated");
    }

    public ActivationEmail sendEmail(String email, String userName, String deviceUID, String community) {
        LOGGER.info("Sending email to [{}]", email);
        ActivationEmail activationEmail;
        if (EmailValidator.isEmail(email)) {
            User user = userService.findByNameAndCommunity(userName, community);
            String token = ActivationEmail.generateToken(email, user);

            activationEmail = new ActivationEmail(email, deviceUID, token);
            save(activationEmail);

            Map<String, String> params = new HashMap<String, String>();
            params.put(ActivationEmail.ID, activationEmail.getId().toString());
            params.put(ActivationEmail.TOKEN, token);
            String from = messageSource.getMessage(community, "activation.email.from", null, null, null);
            String subject = messageSource.getMessage(community, "activation.email.subject", null, null, null);
            String body = messageSource.getMessage(community, "activation.email.body", null, null, null);
            mailService.sendMail(from, new String[]{email}, subject, body, params);
            LOGGER.info("Email sent");
        } else {
            throw new ValidationException("Email " + email + " is not valid!");
        }
        return activationEmail;
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
