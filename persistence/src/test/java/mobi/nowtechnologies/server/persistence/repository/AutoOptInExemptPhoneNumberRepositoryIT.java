package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.AutoOptInExemptPhoneNumber;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertNotNull;


public class AutoOptInExemptPhoneNumberRepositoryIT extends AbstractRepositoryIT {
	@Resource
    private AutoOptInExemptPhoneNumberRepository autoOptInExemptPhoneNumberRepository;
	
	@Test
	public void testFindByUserName(){
        final String userNameInDb = "+447111111111";

        AutoOptInExemptPhoneNumber saved = autoOptInExemptPhoneNumberRepository.findOne(userNameInDb);

		assertNotNull(saved);
	}
	
}
