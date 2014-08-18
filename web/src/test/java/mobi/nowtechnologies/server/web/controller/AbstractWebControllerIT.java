package mobi.nowtechnologies.server.web.controller;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

/**
 * Created by Oleg Artomov on 8/18/2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({
        @ContextConfiguration("classpath:web-root-test.xml"),
        @ContextConfiguration("classpath:web-test.xml")
})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public abstract class AbstractWebControllerIT {
    @Resource
    private WebApplicationContext wac;

    protected MockMvc mockMvc;


    @Before
    public void setUp()
            throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
}
