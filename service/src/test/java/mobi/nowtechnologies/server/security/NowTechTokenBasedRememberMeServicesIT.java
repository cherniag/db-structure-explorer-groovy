package mobi.nowtechnologies.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class NowTechTokenBasedRememberMeServicesIT {

    @Autowired
    NowTechTokenBasedRememberMeServices nowTechTokenBasedRememberMeServices;

    @Test
    public void shouldGetRememberMeToken() {
        //given
        String userName = "+447111111112";
        String password = "f701af8d07e5c95d3f5cf3bd9a62344d";

        //when
        String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken(userName, password);

        //then
        assertNotNull(rememberMeToken);
    }
}