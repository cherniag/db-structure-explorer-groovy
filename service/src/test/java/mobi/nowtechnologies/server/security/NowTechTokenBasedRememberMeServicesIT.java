package mobi.nowtechnologies.server.security;

import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class NowTechTokenBasedRememberMeServicesIT {
    @Autowired NowTechTokenBasedRememberMeServices nowTechTokenBasedRememberMeServices;
    @Autowired CommunityResourceBundleMessageSource messageSource;

    @Test
    public void shouldGetRememberMeToken() {
        Period period = new Period().withDuration(2).withDurationUnit(DurationUnit.MONTHS);

        String message = messageSource.getMessage("o2", period.toMessageCode(), new String[]{String.valueOf(period.getDuration())}, null);

        //given
        String userName = "+447111111112";
        String password = "f701af8d07e5c95d3f5cf3bd9a62344d";

        //when
        String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken(userName, password);

        //then
        assertNotNull(rememberMeToken);
    }
}