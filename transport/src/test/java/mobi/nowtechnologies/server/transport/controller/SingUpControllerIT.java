package mobi.nowtechnologies.server.transport.controller;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.DeviceUserDataService;
import mobi.nowtechnologies.server.service.UserService;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.server.MockMvc;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/service-test.xml",
		"classpath:META-INF/dao-test.xml", "/META-INF/shared.xml", "classpath:transport-servlet-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class SingUpControllerIT {

	@Resource(name = "transport.EntityController")
	EntityController entityController;

	@Resource(name = "service.UserService")
	UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
	DeviceUserDataService deviceUserDataService;
    
    MockMvc mockMvc;

    @Test
    public void givenO2ClientWhoHasSavedPhoneAndPin_whenACC_CHECK_thenActivationIs_ACTIVATED()throws Exception{
       
    }

    @BeforeClass
    public void setUp(){
    	
    	//mockMvc =  xmlConfigSetup(configLocations);
    }

}