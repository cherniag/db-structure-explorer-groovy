package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class FilterWithCriteriaRepositoryTestIT {
	
	@Resource(name="filterWithCriteriaRepository")
	private FilterWithCriteriaRepository filterWithCriteriaRepository;
	
	@Test
	public void testFindByNames() throws Exception {
		List<String> names = Arrays.asList("LAST_TRIAL_DAY", "NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS");
		List<AbstractFilterWithCtiteria> abstractFilterWithCtiterias = filterWithCriteriaRepository.findByNames(names);
		Assert.notEmpty(abstractFilterWithCtiterias);
	}
	
}