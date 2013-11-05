package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.VFPSMSPaymentDetails;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import mobi.nowtechnologies.server.shared.enums.UserType;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserAsmTest {

    @Test
    public void testGetPaymentType_UnknowPayment_Success() {
        UserStatus userStatus = null;
        String lastSubscribedPaymentSystem = null;
        PaymentDetails paymentDetails = new VFPSMSPaymentDetails();

        String paymentType = UserAsm.getPaymentType(paymentDetails, lastSubscribedPaymentSystem, userStatus);

        assertEquals(PaymentDetails.VF_PSMS_TYPE, paymentType);
    }
	
	@Test
	public void testFromUserDto_IsOnTrial_Success()
		throws Exception {
		User user = UserFactory.createUser();
		user.setFreeTrialExpiredMillis(new Date().getTime()+1000000);
		UserDto userDto = new UserDto();
		userDto.setDisplayName("Display Name");
		userDto.setSubBalance(0);
		userDto.setNextSubPayment(new Date());
		userDto.withFreeTrialExpiredMillis(userDto.getNextSubPayment());
		userDto.setUserType(UserType.NORMAL);
		userDto.setPaymentEnabled(true);

		User result = UserAsm.fromUserDto(userDto, user);

		assertNotNull(result);
		assertEquals(userDto.getDisplayName(), result.getDisplayName());
		assertEquals(userDto.getSubBalance(), result.getSubBalance());
		assertEquals((int)(userDto.getNextSubPayment().getTime()/1000), result.getNextSubPayment());
		assertEquals(result.getNextSubPayment()*1000L, result.getFreeTrialExpiredMillis().longValue());
		assertEquals(userDto.getUserType(), result.getUserType());
	}
	
	@Test
	public void testFromUserDto_IsNotOnTrial_Success()
		throws Exception {
		User user = UserFactory.createUser();
		user.setFreeTrialExpiredMillis(new Date().getTime()+1000000);
		UserDto userDto = new UserDto();
		userDto.setDisplayName("Display Name");
		userDto.setSubBalance(0);
		userDto.setNextSubPayment(new Date());
		userDto.setUserType(UserType.NORMAL);
		userDto.setPaymentEnabled(true);

		User result = UserAsm.fromUserDto(userDto, user);

		assertNotNull(result);
		assertEquals(userDto.getDisplayName(), result.getDisplayName());
		assertEquals(userDto.getSubBalance(), result.getSubBalance());
		assertEquals((int)(userDto.getNextSubPayment().getTime()/1000), result.getNextSubPayment());
		assertEquals(user.getFreeTrialExpiredMillis().longValue(), result.getFreeTrialExpiredMillis().longValue());
		assertEquals(userDto.getUserType(), result.getUserType());
	}
}