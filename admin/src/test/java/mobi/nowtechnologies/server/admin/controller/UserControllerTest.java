package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;

import java.util.Date;

import org.junit.*;
import static org.junit.Assert.*;

public class UserControllerTest {

    @Test
    public void givenUserWithChangedNextSubPaymentField_whenUpdateFreeTrialExpiredTime_willChangeFreeTrialExpiredFieldToTheSameTime() {
        Long time = System.currentTimeMillis() + 60 * 1000;
        Date date = new Date(time);
        Date futureDate = new Date(time + 1000);

        User user = new User().withNextSubPayment(date).withFreeTrialExpiredMillis(date);
        UserDto userDto = new UserDto().withNextSubPayment(futureDate);
        //when
        UserDto result = new UserController().updateFreeTrialExpiredTime(userDto, user);
        //then
        assertFalse(futureDate.equals(result.getFreeTrialExpiredAsDate()));
    }

    @Test
    public void givenUserWithNextSubPaymentThatDoesNotChanged_whenUpdateFreeTrialExpiredTime_willDoesNotChangeFreeTrialExpiredFieldToTheSameTime() {
        Long time = System.currentTimeMillis() + 60 * 1000;
        Date date = new Date(time);

        User user = new User().withNextSubPayment(date);
        UserDto userDto = new UserDto().withNextSubPayment(date);
        //when
        UserDto result = new UserController().updateFreeTrialExpiredTime(userDto, user);
        //then
        assertEquals(0, result.getFreeTrialExpiredMillis());
    }

    @Test
    public void givenUserWithNotNullLastPaymentSystem_whenUpdateFreeTrialExpiredTime_willDoesNotChangeFreeTrialExpiredFieldToTheSameTime() {
        Long time = System.currentTimeMillis() + 60 * 1000;
        Date date = new Date(time);
        Date futureDate = new Date(time + 1000);

        User user = new User().withNextSubPayment(date);
        user.setLastSubscribedPaymentSystem("paypal");
        UserDto userDto = new UserDto().withNextSubPayment(futureDate);
        //when
        UserDto result = new UserController().updateFreeTrialExpiredTime(userDto, user);
        //then
        assertEquals(0, result.getFreeTrialExpiredMillis());
    }

    @Test
    public void givenUserWithNotNullCurrentPaymentDetails_whenUpdateFreeTrialExpiredTime_willDoesNotChangeFreeTrialExpiredFieldToTheSameTime() {
        Long time = System.currentTimeMillis() + 60 * 1000;
        Date date = new Date(time);
        Date futureDate = new Date(time + 1000);

        User user = new User().withNextSubPayment(date);
        user.setCurrentPaymentDetails(new PayPalPaymentDetails());
        UserDto userDto = new UserDto().withNextSubPayment(futureDate);
        //when
        UserDto result = new UserController().updateFreeTrialExpiredTime(userDto, user);
        //then
        assertEquals(0, result.getFreeTrialExpiredMillis());
    }
}
