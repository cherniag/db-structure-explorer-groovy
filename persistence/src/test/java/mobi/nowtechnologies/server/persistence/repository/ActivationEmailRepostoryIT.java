package mobi.nowtechnologies.server.persistence.repository;


import mobi.nowtechnologies.server.persistence.domain.ActivationEmail;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class ActivationEmailRepostoryIT {

    @Autowired
    private ActivationEmailRepository activationEmailRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFind() {
        User user = userRepository.findByMobile("+64279000456").get(0);

        ActivationEmail activationEmail = new ActivationEmail();
        activationEmail.setUser(user);
        activationEmail.setToken(Utils.getRandomString(10));
    }
}