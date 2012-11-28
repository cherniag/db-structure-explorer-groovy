package mobi.nowtechnologies.server.service;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * The class <code>ChartDetailServiceTest</code> contains tests for the class <code>{@link ChartDetailService}</code>.
 *
 * @generatedBy CodePro at 23.12.11 10:50
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml", "/META-INF/service-test.xml","/META-INF/shared.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@Ignore
public class ChartDetailServiceTestIT {
	
	@Resource(name="service.ChartDetailService")
	private ChartDetailService chartDetailService;

	
}