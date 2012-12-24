package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/service-test.xml",
        "classpath:META-INF/dao-test.xml", "/META-INF/shared.xml", "classpath:transport-servlet-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@Ignore
public class ApplyInitPromoControllerTest {

    @Autowired
    ApplyInitPromoController controller;

    @Autowired
    UserService userService;

    @Test
    public void givenValidO2Token_whenAPPLY_PROMO_thenBigPromotionSet(){
        //given
        String userName = "test@test.com";
        User user = userService.findByName(userName);

        //then
        controller.applyO2Promotion("Now Music", userName, user.getToken(), "timestemp", "o2_token", "Now Music");

        //when
        user = userService.findByName(userName);
        Assert.assertEquals(13, days(user.getNextSubPayment()));
    }

    private int days(long nextSubPayment) {

        return Days.daysBetween(new DateTime(System.currentTimeMillis()), new DateTime(nextSubPayment * 1000)).getDays();

    }

}
