package mobi.nowtechnologies.server.service.pincode;

import mobi.nowtechnologies.server.persistence.domain.PinCode;
import mobi.nowtechnologies.server.persistence.repository.AbstractRepositoryIT;
import mobi.nowtechnologies.server.persistence.repository.PinCodeRepository;
import org.junit.After;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Anton Zemliankin
 */
public class PinCodeRepositoryIT extends AbstractRepositoryIT {

    @Resource
    private PinCodeRepository pinCodeRepository;

    @Test
    public void findPinCodeByUserAndCreationTimeTest() {
        savePinCode(1, "444", 4, false); //4 minutes ago

        savePinCode(1, "555", 5, false); //5 minutes ago

        savePinCode(1, "666", 6, false); //6 minutes ago

        savePinCode(2, "777", 3, false); //3 minutes ago and another user

        savePinCode(1, "888", 2, true);  //2 minutes ago and already entered

        savePinCode(1, "999", 8, true);  //8 minutes ago

        List<PinCode> pinCodes = pinCodeRepository.findPinCodesByUserAndCreationTime(1, new Date(System.currentTimeMillis() - 7 * 60 * 1000));

        assertEquals(3, pinCodes.size());

        PinCode pinCode = pinCodes.get(0);

        assertEquals(new Integer(1), pinCode.getUserId());
        assertEquals("444", pinCode.getCode());
        assertFalse(pinCode.isEntered());
    }

    @Test
    public void countUserPinCodesTest() {
        savePinCode(1, "555", 1, false); //1 minute ago

        savePinCode(1, "666", 2, false); //2 minutes ago

        savePinCode(1, "777", 3, true); //2 minutes ago and already entered

        savePinCode(2, "888", 1, false); //1 minute ago and another user

        savePinCode(1, "999", 5, false); //5 minutes ago

        int count = pinCodeRepository.countUserPinCodes(1, new Date(System.currentTimeMillis() - 4 * 60 * 1000));

        assertEquals(3, count);
    }

    @After
    public void tearDown() throws Exception {
        pinCodeRepository.deleteAll();
    }

    private void savePinCode(Integer userId, String code, int minutesAgo, boolean isEntered) {
        PinCode pinCode = new PinCode(userId, code);
        Date creationTime = new Date(System.currentTimeMillis() - minutesAgo * 60 * 1000);
        ReflectionTestUtils.setField(pinCode, "creationTime", creationTime);
        pinCode.setEntered(isEntered);
        pinCodeRepository.save(pinCode);
    }

}
