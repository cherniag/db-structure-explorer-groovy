package mobi.nowtechnologies.server.trackrepo.controller;

import mobi.nowtechnologies.server.trackrepo.TrackRepoEnvironmentInitializer;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

// Created by oar on 2/5/14.
@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({
        @ContextConfiguration(value = "classpath:META-INF/application-test.xml", initializers = TrackRepoEnvironmentInitializer.class),
        @ContextConfiguration("classpath:META-INF/trackrepo-servlet-test.xml")
}
)
@WebAppConfiguration
@TransactionConfiguration(transactionManager = "trackRepo.TransactionManager", defaultRollback = true)
@Transactional
public abstract class AbstractTrackRepoIT {

    @Autowired WebApplicationContext applicationContext;

    protected MockMvc mockMvc;

    @Before
    public void setupMVC() {
        mockMvc = webAppContextSetup(applicationContext).build();
    }

}