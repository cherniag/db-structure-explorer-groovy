package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import org.junit.Assert;
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
        controller.applyO2Promotion("Now Music", userName, user.getToken(), "timestemp", "o2_token");

        //when
        user = userService.findByName(userName);
        Assert.assertEquals(8, days(user.getNextSubPayment()));
    }

    private int days(long nextSubPayment) {
        long currentTime = System.currentTimeMillis();
        long diff = nextSubPayment - currentTime;
        long day = 1000*60*60*24;
        return Math.round(diff/day);

    }

}
