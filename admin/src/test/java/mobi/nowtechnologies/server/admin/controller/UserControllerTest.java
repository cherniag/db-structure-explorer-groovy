package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserControllerTest {

    @Test
    public void givenUserWithChangedNextSubPaymentField_whenUpdateFreeTrialExpiredTime_willChangeFreeTrialExpiredFieldToTheSameTime() {
        Long time = System.currentTimeMillis() + 60*1000;
        Date date = new Date(time);
        Date futureDate = new Date(time + 1000);

        User user = new User().withNextSubPayment(date).withFreeTrialExpiredMillis(date);
        UserDto userDto = new UserDto().withNextSubPayment(futureDate);
        //when
        UserDto result = new UserController().updateFreeTrialExpiredTime(userDto, user);
        //then
        assertTrue(Utils.datesNotEquals(futureDate, result.getFreeTrialExpiredAsDate()));
    }

    @Test
    public void givenUserWithNextSubPaymentThatDoesNotChanged_whenUpdateFreeTrialExpiredTime_willDoesNotChangeFreeTrialExpiredFieldToTheSameTime() {
        Long time = System.currentTimeMillis() + 60*1000;
        Date date = new Date(time);

        User user = new User().withNextSubPayment(date);
        UserDto userDto = new UserDto().withNextSubPayment(date);
        //when
        UserDto result = new UserController().updateFreeTrialExpiredTime(userDto, user);
        //then
        assertEquals(0, result.getFreeTrialExpiredMillis());
    }
}
