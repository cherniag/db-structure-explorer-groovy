package mobi.nowtechnologies.server.job.ft;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * User: gch
 * Date: 12/20/13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({
        @ContextConfiguration(locations = {
                "classpath:transport-root-test.xml",
                "classpath:ft-job-test.xml"}),
        @ContextConfiguration(locations = {
                "classpath:transport-servlet-test.xml"})})
@WebAppConfiguration
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@Ignore
public class CustomJobTestIT {

    @Resource
    private Scheduler scheduler;

    @Resource
    Trigger newTrigger;

    @Before
    public void setUp() throws Exception {
        int i=0;
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
