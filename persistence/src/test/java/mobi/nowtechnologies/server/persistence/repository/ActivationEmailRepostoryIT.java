package mobi.nowtechnologies.server.persistence.repository;


import mobi.nowtechnologies.server.persistence.domain.ActivationEmail;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class ActivationEmailRepostoryIT extends AbstractRepositoryIT {

    @Autowired
    private ActivationEmailRepository activationEmailRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFind() {
        User user = userRepository.findByMobile("+64279000456").get(0);

        ActivationEmail activationEmail = new ActivationEmail();
        activationEmail.setToken(Utils.getRandomString(10));
    }
}
