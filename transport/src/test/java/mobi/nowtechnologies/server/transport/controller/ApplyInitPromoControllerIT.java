package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

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
public class ApplyInitPromoControllerIT {

    @Autowired
    ApplyInitPromoController controller;

    @Autowired
    UserService userService;
    
    @Autowired
    UserRepository userRepository;

    @Test
    public void givenValidO2Token_whenAPPLY_PROMO_thenBigPromotionSet(){
        //given
        String userName = "imei_351722057812748";
        User user = userService.findByName(userName);
        user.setActivationStatus(ActivationStatus.ENTERED_NUMBER);
        userRepository.save(user);
        
        //then
        controller.applyO2Promotion("o2", userName, user.getToken(), "timestemp", "00000000-c768-4fe7-bb56-a5e0c722cd44", "o2");

        //when
        user = userService.findByName(user.getMobile());
        Assert.assertEquals(13, days(user.getNextSubPayment()));
        Assert.assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
        Assert.assertEquals("o2", user.getProvider());
        Assert.assertEquals("PAYG", user.getContract());
    }
    
    @Test
    public void givenValidO2Token_whenUserWithPhoneExistsAndREgistrationFromNewDevice_thenReturnOldUserWithNewDeviceAndRemoveSecondUser() {
    	//given
        String userName = "imei_351722057812749";
        User user = userService.findByName(userName);
        
        
        //then
        controller.applyO2Promotion("o2", userName, user.getToken(), "timestemp", "11111111-c768-4fe7-bb56-a5e0c722cd44", "o2");

        //when
        User mobileUser = userService.findByName("+447111111111");
        
        Assert.assertEquals(user.getDevice(), mobileUser.getDevice());
        Assert.assertEquals(user.getDeviceUID(), mobileUser.getDeviceUID());
        Assert.assertEquals(user.getDeviceModel(), mobileUser.getDeviceModel());
        Assert.assertEquals(user.getDeviceString(), mobileUser.getDeviceString());
        
        user = userService.findByName(userName);
        Assert.assertNull(user);
    }
    
    @Test
    public void givenValidO2Token_whenUserReInstallAppWithOldPhoneNumber_then_ReturnAUserWithOldPhoneNumberAndAppliedPromo() {
    	//given
        String userName = "+447111111111";
        String oldUserName = "+447888888888";
        User user = userService.findByName(userName);
        user.setUserName(oldUserName);
        user.setActivationStatus(ActivationStatus.ENTERED_NUMBER);
        user.setNextSubPayment(0);
        userRepository.save(user);
        
        //then
        controller.applyO2Promotion("o2", oldUserName, user.getToken(), "timestemp", "00000000-c768-4fe7-bb56-a5e0c722cd44", "o2");

        //when
        User mobileUser = userService.findByName(userName);
        
        Assert.assertEquals(user.getDevice(), mobileUser.getDevice());
        Assert.assertEquals(user.getDeviceUID(), mobileUser.getDeviceUID());
        Assert.assertEquals(user.getDeviceModel(), mobileUser.getDeviceModel());
        Assert.assertEquals(user.getDeviceString(), mobileUser.getDeviceString());
        Assert.assertEquals(ActivationStatus.ACTIVATED, mobileUser.getActivationStatus());
        Assert.assertEquals(13, days(mobileUser.getNextSubPayment()));
    }
    
    @Test
    public void applyInitPromo_whenUserCallMethodTwice_then_ReturnAUser() {
    	//given
        String userName = "+447733333333";
        User user = userService.findByName(userName);
        
        //then
        controller.applyO2Promotion("o2", userName, user.getToken(), "timestemp", "00000000-c768-4fe7-bb56-a5e0c722cd44", "o2");

        //when
        user = userService.findByName(userName);
        Assert.assertEquals(user.getUserName(), "+447733333333");
        Assert.assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
    }
    
    @Test
    public void applyInitPromo_whenUserReInstallAppWithNewPhoneNumber_then_ReturnAUserWithNewPhoneNumber() {
    	//given
        String userName = "+447766666666";
        User user = userService.findByName(userName);
        
        //then
        controller.applyO2Promotion("o2", userName, user.getToken(), "timestemp", "00000000-c768-4fe7-bb56-a5e0c722cd44", "o2");
        
        user = userService.findByName(user.getMobile());
        //when
        Assert.assertNotNull(user);
        Assert.assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
    }
    
    @Test(expected=UserCredentialsException.class)
    public void applyInitPromo_whenUserUserNameIsWrong_then_Fail() {
    	//given
        
        
        //then
        controller.applyO2Promotion("o2", "+447700000000", "hello token", "timestemp", "00000000-c768-4fe7-bb56-a5e0c722cd44", "o2");
        
        //when
    }
    
    private int days(long nextSubPayment) {

        return Days.daysBetween(new DateTime(System.currentTimeMillis()), new DateTime(nextSubPayment * 1000)).getDays();

    }

}
