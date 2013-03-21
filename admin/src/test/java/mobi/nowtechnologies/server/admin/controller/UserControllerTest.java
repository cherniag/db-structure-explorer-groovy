package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;

import static org.junit.Assert.assertEquals;

public class UserControllerTest {

    @Test
    public void givenUserWithChangedNextSubPaymentField_whenUpdateFreeTrialExpiredTime_willChangeFreeTrialExpiredFieldToTheSameTime() {
        Long currentFreeTrial = System.currentTimeMillis() + 60*1000;
        User user = new User().withNextSubPayment(1000).withFreeTrialExpiredMillis(currentFreeTrial);
        UserDto userDto = new UserDto().withNextSubPayment(9999*1000);
        //when
        UserDto result = new UserController().updateFreeTrialExpiredTime(userDto, user);
        //then
        assertEquals(9999, result.getFreeTrialExpiredMillis());
    }

    @Test
    public void givenUserWithNextSubPaymentThatDoesNotChanged_whenUpdateFreeTrialExpiredTime_willDoesNotChangeFreeTrialExpiredFieldToTheSameTime() {
        User user = new User().withNextSubPayment(1000);
        UserDto userDto = new UserDto().withNextSubPayment(1000*1000);
        //when
        UserDto result = new UserController().updateFreeTrialExpiredTime(userDto, user);
        //then
        assertEquals(0, result.getFreeTrialExpiredMillis());
    }
}
