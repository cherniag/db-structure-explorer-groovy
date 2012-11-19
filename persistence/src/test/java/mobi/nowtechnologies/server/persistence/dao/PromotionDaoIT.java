package mobi.nowtechnologies.server.persistence.dao;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * The class <code>PromotionDaoTest</code> contains tests for the class <code>{@link PromotionDao}</code>.
 *
 * @generatedBy CodePro at 04.10.11 11:55
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class PromotionDaoIT {
	
	@Resource(name="persistence.PromotionDao")
	private PromotionDao promotionDao;

}