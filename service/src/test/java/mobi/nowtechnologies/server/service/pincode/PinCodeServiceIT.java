package mobi.nowtechnologies.server.service.pincode;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.persistence.domain.PinCode;
import mobi.nowtechnologies.server.persistence.repository.PinCodeRepository;
import mobi.nowtechnologies.server.service.exception.PinCodeException;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import javax.annotation.Resource;
import static org.junit.Assert.*;

/**
 * @author Anton Zemliankin
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/dao-test.xml", "/META-INF/service-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager")
public class PinCodeServiceIT {

    private static final Integer DEFAULT_USER_ID = 1;
    private static final int DEFAULT_CODE_LENGTH = 4;

    @Resource
    private PinCodeService pinCodeService;

    @Resource
    private PinCodeRepository pinCodeRepository;

    @Value("${pin.code.max.attempts}")
    private int maxAttempts;

    @Value("${pin.code.limit.count}")
    private int limitCount;

    @Value("${pin.code.expiration.seconds}")
    private int expirationSeconds;



    @Test(expected = PinCodeException.MaxPinCodesReached.class)
    public void testPinCodeServiceMaxPinCodesReached() throws Exception {
        for (int i = 0; i <= limitCount; i++) {
            pinCodeService.generatePinCode(DEFAULT_USER_ID, DEFAULT_CODE_LENGTH);
        }
    }

    @Test
    public void testPinCodeServiceGenerate() throws Exception {
        PinCode pinCode = pinCodeService.generatePinCode(DEFAULT_USER_ID, DEFAULT_CODE_LENGTH);

        assertEquals(DEFAULT_USER_ID, pinCode.getUserId());
        assertEquals(new Integer(0), pinCode.getAttempts());
        assertEquals(DEFAULT_CODE_LENGTH, pinCode.getCode().length());
        assertFalse(pinCode.isEntered());
        assertTrue(pinCode.getCreationTime() <= DateTimeUtils.getEpochSeconds() && pinCode.getCreationTime() > DateTimeUtils.getEpochSeconds() - 5);
    }

    @Test(expected = PinCodeException.NotFound.class)
    public void testPinCodeServiceCheckNotFound() throws Exception {
        PinCode pinCode = createPinCode(DEFAULT_USER_ID, DEFAULT_CODE_LENGTH, DateTimeUtils.getEpochSeconds(), false);
        pinCodeService.checkPinCode(DEFAULT_USER_ID + 1, pinCode.getCode());
    }

    @Test(expected = PinCodeException.NotFound.class)
    public void testPinCodeServiceCheckAlreadyEntered() throws Exception {
        PinCode pinCode = createPinCode(DEFAULT_USER_ID, DEFAULT_CODE_LENGTH, DateTimeUtils.getEpochSeconds(), true);
        pinCodeService.checkPinCode(DEFAULT_USER_ID, pinCode.getCode());
    }

    @Test(expected = PinCodeException.NotFound.class)
    public void testPinCodeServiceCheckExpired() throws Exception {
        PinCode pinCode = createPinCode(DEFAULT_USER_ID, DEFAULT_CODE_LENGTH, DateTimeUtils.getEpochSeconds() - (expirationSeconds + 1), false);
        pinCodeService.checkPinCode(DEFAULT_USER_ID, pinCode.getCode());
    }

    @Test(expected = PinCodeException.MaxAttemptsReached.class)
    public void testPinCodeServiceCheckMaxAttemptsReached() throws Exception {
        PinCode pinCode = createPinCode(DEFAULT_USER_ID, DEFAULT_CODE_LENGTH, DateTimeUtils.getEpochSeconds(), false);
        for (int i = 0; i <= maxAttempts; i++) {
            pinCodeService.checkPinCode(DEFAULT_USER_ID, pinCode.getCode() + "5");
        }
    }

    @Test
    public void testPinCodeServiceCheckTrue() throws Exception {
        PinCode pinCode = createPinCode(DEFAULT_USER_ID, DEFAULT_CODE_LENGTH, DateTimeUtils.getEpochSeconds(), false);

        boolean check = pinCodeService.checkPinCode(DEFAULT_USER_ID, pinCode.getCode());
        PinCode updatedPinCode = pinCodeRepository.findOne(pinCode.getId());

        assertTrue(check);
        assertFalse(pinCode.isEntered());
        assertTrue(updatedPinCode.isEntered());
        assertEquals(new Integer(0), pinCode.getAttempts());
        assertEquals(new Integer(1), updatedPinCode.getAttempts());
    }

    @Test
    public void testPinCodeServiceCheckFalse() throws Exception {
        PinCode pinCode = createPinCode(DEFAULT_USER_ID, DEFAULT_CODE_LENGTH, DateTimeUtils.getEpochSeconds(), false);

        boolean check = pinCodeService.checkPinCode(DEFAULT_USER_ID, pinCode.getCode()+"5");
        PinCode updatedPinCode = pinCodeRepository.findOne(pinCode.getId());

        assertFalse(check);
        assertFalse(pinCode.isEntered());
        assertFalse(updatedPinCode.isEntered());
        assertEquals(new Integer(0), pinCode.getAttempts());
        assertEquals(new Integer(1), updatedPinCode.getAttempts());
    }


    @After
    public void tearDown() throws Exception {
        pinCodeRepository.deleteAll();
    }


    private PinCode createPinCode(Integer userId, int codeLength, Integer creationTime, boolean isEntered){
        PinCode pinCode = new PinCode();
        pinCode.setUserId(userId);
        pinCode.setCode(RandomStringUtils.random(codeLength, false, true));
        pinCode.setAttempts(0);
        pinCode.setCreationTime(creationTime);
        pinCode.setEntered(isEntered);
        return pinCodeRepository.save(pinCode);
    }

}
