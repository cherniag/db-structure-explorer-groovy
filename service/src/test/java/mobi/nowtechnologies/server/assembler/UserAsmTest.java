package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.VFPSMSPaymentDetails;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.UserType;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.ITUNES_SUBSCRIPTION;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.VF_PSMS_TYPE;

import java.util.Date;

import org.junit.*;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;

public class UserAsmTest {
    User user = new User();

    @Test
    public void shouldReturnPAY_PALPaymentTypeWhenCurrentPaymentDetailsIsPayPal() {
        //given
        PaymentDetails paymentDetails = new PayPalPaymentDetails();
        String lastSubscribedPaymentSystem = ITUNES_SUBSCRIPTION;
        user.setLastSubscribedPaymentSystem(lastSubscribedPaymentSystem);
        user.setCurrentPaymentDetails(paymentDetails);

        //when
        String paymentType = UserAsm.getPaymentType(user);

        //then
        assertThat(paymentType, is("PAY_PAL"));
    }

    @Test
    public void shouldReturnITUNES_SUBSCRIPTIONPaymentTypeWhenCurrentPaymentDetailsIsITunes() {
        //given
        PaymentDetails paymentDetails = new ITunesPaymentDetails();
        user.setCurrentPaymentDetails(paymentDetails);

        //when
        String paymentType = UserAsm.getPaymentType(user);

        //then
        assertThat(paymentType, is("ITUNES_SUBSCRIPTION"));
    }

    @Test
    public void shouldReturnITUNES_SUBSCRIPTIONPaymentTypeWhenLastSubscribedPaymentSystemIsITunes() {
        //given
        PaymentDetails paymentDetails = null;
        String lastSubscribedPaymentSystem = ITUNES_SUBSCRIPTION;
        user.setLastSubscribedPaymentSystem(lastSubscribedPaymentSystem);
        user.setCurrentPaymentDetails(paymentDetails);

        //when
        String paymentType = UserAsm.getPaymentType(user);

        //then
        assertThat(paymentType, is("ITUNES_SUBSCRIPTION"));
    }

    @Test
    public void shouldReturnUNKNOWNPaymentTypeWhenLastSubscribedPaymentSystemIsNull() {
        //given
        PaymentDetails paymentDetails = null;
        String lastSubscribedPaymentSystem = null;
        user.setLastSubscribedPaymentSystem(lastSubscribedPaymentSystem);
        user.setCurrentPaymentDetails(paymentDetails);

        //when
        String paymentType = UserAsm.getPaymentType(user);

        //then
        assertThat(paymentType, is("UNKNOWN"));
    }

    @Test
    public void shouldReturnVF_PSMS_TYPEPaymentTypeWhenCurrentPaymentDetailsIsVF_PSMS_TYPE() {
        //given
        String lastSubscribedPaymentSystem = ITUNES_SUBSCRIPTION;
        PaymentDetails paymentDetails = new VFPSMSPaymentDetails();
        user.setLastSubscribedPaymentSystem(lastSubscribedPaymentSystem);
        user.setCurrentPaymentDetails(paymentDetails);

        //when
        String paymentType = UserAsm.getPaymentType(user);

        //then
        assertEquals(VF_PSMS_TYPE, paymentType);
    }

    @Test
    public void shouldReturnVF_PSMS_TYPEPaymentTypeWhenCurrentPaymentDetailsIsMIG_SMS_TYPE() {
        //given
        String lastSubscribedPaymentSystem = ITUNES_SUBSCRIPTION;
        PaymentDetails paymentDetails = new MigPaymentDetails();
        user.setLastSubscribedPaymentSystem(lastSubscribedPaymentSystem);
        user.setCurrentPaymentDetails(paymentDetails);

        //when
        String paymentType = UserAsm.getPaymentType(user);

        //then
        assertEquals("PSMS", paymentType);
    }

    @Test
    public void shouldReturnCreditCardPaymentTypeWhenCurrentPaymentDetailsIsSAGEPAY_CREDITCARD_TYPE() {
        //given
        String lastSubscribedPaymentSystem = ITUNES_SUBSCRIPTION;
        PaymentDetails paymentDetails = new SagePayCreditCardPaymentDetails();
        user.setLastSubscribedPaymentSystem(lastSubscribedPaymentSystem);
        user.setCurrentPaymentDetails(paymentDetails);

        //when
        String paymentType = UserAsm.getPaymentType(user);

        //then
        assertEquals("creditCard", paymentType);
    }

    @Test
    public void shouldReturnPAY_PALPaymentTypeWhenCurrentPaymentDetailsIsSPAY_PAL() {
        //given
        String lastSubscribedPaymentSystem = null;
        PaymentDetails paymentDetails = new PayPalPaymentDetails();
        user.setLastSubscribedPaymentSystem(lastSubscribedPaymentSystem);
        user.setCurrentPaymentDetails(paymentDetails);

        //when
        String paymentType = UserAsm.getPaymentType(user);

        //then
        assertEquals("PAY_PAL", paymentType);
    }

    @Test
    public void testFromUserDto_IsOnTrial_Success() throws Exception {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setFreeTrialExpiredMillis(new Date().getTime() + 1000000);
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
        assertEquals((int) (userDto.getNextSubPayment().getTime() / 1000), result.getNextSubPayment());
        assertEquals(result.getNextSubPayment() * 1000L, result.getFreeTrialExpiredMillis().longValue());
        assertEquals(userDto.getUserType(), result.getUserType());
    }

    @Test
    public void testFromUserDto_IsNotOnTrial_Success() throws Exception {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setFreeTrialExpiredMillis(new Date().getTime() + 1000000);
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
        assertEquals((int) (userDto.getNextSubPayment().getTime() / 1000), result.getNextSubPayment());
        assertEquals(user.getFreeTrialExpiredMillis().longValue(), result.getFreeTrialExpiredMillis().longValue());
        assertEquals(userDto.getUserType(), result.getUserType());
    }
}