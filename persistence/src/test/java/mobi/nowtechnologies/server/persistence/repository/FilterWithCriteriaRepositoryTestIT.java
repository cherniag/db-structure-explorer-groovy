package mobi.nowtechnologies.server.persistence.repository;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.util.Assert;

/**
 * The class <code>FilterWithCriteriaRepositoryTest</code> contains tests for the class <code>{@link FilterWithCriteriaRepository}</code>.
 *
 * @generatedBy CodePro at 28.05.12 17:33
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(defaultRollback = true)
public class FilterWithCriteriaRepositoryTestIT {
	
	@Resource(name="filterWithCriteriaRepository")
	private FilterWithCriteriaRepository filterWithCriteriaRepository;
	
	@Test
	public void testname() throws Exception {
		List<String> names = Arrays.asList("LAST_TRIAL_DAY", "NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS");
		List<AbstractFilterWithCtiteria> abstractFilterWithCtiterias = filterWithCriteriaRepository.findByNames(names);
		Assert.notEmpty(abstractFilterWithCtiterias);
	}
	
}