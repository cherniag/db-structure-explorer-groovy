package mobi.nowtechnologies.server.persistence.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.persistence.domain.Chart;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class <code>ChartRepositoryTest</code> contains tests for the class <code>{@link ChartRepository}</code>.
 * 
 * @generatedBy CodePro at 28.05.12 17:33
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
public class ChartRepositoryIT {

	@Resource(name = "chartRepository")
	private ChartRepository chartRepository;

	@Test
	public void testGetByCommuntityName() throws Exception {

		List<Chart> charts = chartRepository.getByCommunityName("CN Commercial Beta");

		assertNotNull(charts);
		assertEquals(2, charts.size());
	}
	
	@Test
	public void testGetByCommuntityUrl() throws Exception {

		List<Chart> charts = chartRepository.getByCommunityURL("ChartsNow");

		assertNotNull(charts);
		assertEquals(2, charts.size());
	}
}