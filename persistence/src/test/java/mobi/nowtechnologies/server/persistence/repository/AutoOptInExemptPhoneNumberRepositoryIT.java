package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.AutoOptInExemptPhoneNumber;

import javax.annotation.Resource;

import org.junit.*;
import static org.junit.Assert.*;


public class AutoOptInExemptPhoneNumberRepositoryIT extends AbstractRepositoryIT {

    @Resource
    private AutoOptInExemptPhoneNumberRepository autoOptInExemptPhoneNumberRepository;

    @Test
    public void testFindByUserName() {
        final String userNameInDb = "+447111111111";

        AutoOptInExemptPhoneNumber saved = autoOptInExemptPhoneNumberRepository.findOne(userNameInDb);

        assertNotNull(saved);
    }

}
