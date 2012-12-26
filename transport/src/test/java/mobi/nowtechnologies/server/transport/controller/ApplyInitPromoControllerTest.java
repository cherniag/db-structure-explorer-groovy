package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/service-test.xml",
        "classpath:META-INF/dao-test.xml", "/META-INF/shared.xml", "classpath:transport-servlet-test.xml"})
//@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
//@Transactional
public class ApplyInitPromoControllerTest {

    @Autowired
    ApplyInitPromoController controller;

    @Autowired
    UserService userService;

    @Test
    public void givenValidO2Token_whenAPPLY_PROMO_thenBigPromotionSet(){
        //given
        String userName = "imei_351722057812748";
        User user = userService.findByName(userName);

        //then
        controller.applyO2Promotion("o2", userName, user.getToken(), "timestemp", "0000-4dfghg546456", "o2");

        //when
        user = userService.findByName(user.getMobile());
        Assert.assertEquals(13, days(user.getNextSubPayment()));
    }
    
    @Test
    public void givenValidO2Token_whenUserWithPhoneExistsAndREgistrationFromNewDevice_thenReturnOldUserWithNewDeviceAndRemoveSecondUser() {
    	//given
        String userName = "imei_351722057812749";
        User user = userService.findByName(userName);
        
        
        //then
        controller.applyO2Promotion("o2", userName, user.getToken(), "timestemp", "o2_token", "o2");

        //when
        User mobileUser = userService.findByName("+447111111111");
        
        Assert.assertEquals(user.getDevice(), mobileUser.getDevice());
        Assert.assertEquals(user.getDeviceUID(), mobileUser.getDeviceUID());
        Assert.assertEquals(user.getDeviceModel(), mobileUser.getDeviceModel());
        Assert.assertEquals(user.getDeviceString(), mobileUser.getDeviceString());
        
        user = userService.findByName(userName);
        Assert.assertNull(user);
    }

    private int days(long nextSubPayment) {

        return Days.daysBetween(new DateTime(System.currentTimeMillis()), new DateTime(nextSubPayment * 1000)).getDays();

    }

}
