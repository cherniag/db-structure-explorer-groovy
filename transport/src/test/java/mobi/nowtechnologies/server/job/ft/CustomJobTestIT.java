package mobi.nowtechnologies.server.job.ft;

import javax.annotation.Resource;

import org.quartz.Scheduler;
import org.quartz.Trigger;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * User: gch Date: 12/20/13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({@ContextConfiguration(locations = {"classpath:transport-root-test.xml", "classpath:ft-job-test.xml"}), @ContextConfiguration(locations = {"classpath:transport-servlet-test.xml"})})
@WebAppConfiguration
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@Ignore
public class CustomJobTestIT {

    @Resource
    Trigger newTrigger;
    @Resource
    private Scheduler scheduler;

    @Before
    public void setUp() throws Exception {
        int i = 0;
    }

    @Test
    public void testName() throws Exception {
        System.out.println("######################");
        Thread.sleep(2000);
        System.out.println("######################");
        scheduler.rescheduleJob("triggerName", "groupName", newTrigger);
        System.out.println("######################");
        Thread.sleep(2000);
        System.out.println("######################");

    }
}
