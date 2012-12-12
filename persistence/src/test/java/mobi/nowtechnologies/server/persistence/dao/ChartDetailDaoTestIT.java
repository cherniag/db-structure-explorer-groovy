package mobi.nowtechnologies.server.persistence.dao;

import junit.framework.Assert;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * The class <code>ChartDetailDaoTest</code> contains tests for the class <code>{@link ChartDetailDao}</code>.
 *
 * @generatedBy CodePro at 19.12.11 15:54
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class ChartDetailDaoTestIT {
	
	@Resource(name="persistence.ChartDetailDao")
	private ChartDetailDao chartDetailDao;
	
	@Test
	public void testFindChartDetailTreeForDrmUpdate() {
		
		int userId=1;
		byte chartId=3;
		List<ChartDetail> chartDetails = chartDetailDao.findChartDetailTreeForDrmUpdate(userId, chartId);
		Assert.assertNotNull(chartDetails);
		Assert.assertEquals(21, chartDetails.size());
	}
}