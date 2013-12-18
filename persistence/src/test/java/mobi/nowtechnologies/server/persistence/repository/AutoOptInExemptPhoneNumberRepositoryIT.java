package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.AutoOptInExemptPhoneNumber;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager")
@Transactional
public class AutoOptInExemptPhoneNumberRepositoryIT {
	@Resource
    private AutoOptInExemptPhoneNumberRepository autoOptInExemptPhoneNumberRepository;
	
	@Test
	public void testFindByUserName(){
        final String userNameInDb = "+447111111111";

        AutoOptInExemptPhoneNumber saved = autoOptInExemptPhoneNumberRepository.findByUserName(userNameInDb);

		assertNotNull(saved);
	}
	
}
