package mobi.nowtechnologies.server.service.pincode;

import mobi.nowtechnologies.server.persistence.domain.PinCode;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.PinCodeRepository;
import mobi.nowtechnologies.server.service.exception.PinCodeException;
import mobi.nowtechnologies.server.service.pincode.impl.PinCodeServiceImpl;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Anton Zemliankin
 */

@RunWith(MockitoJUnitRunner.class)
public class PinCodeServiceImplTest {

    private static final User DEFAULT_USER = new User().withId(1);
    private static final int DEFAULT_CODE_LENGTH = 4;

    private int limitCount = 5;
    private int limitSeconds = 100;
    private int maxAttempts = 5;
    private int expirationSeconds = 20;

    @Mock
    private PinCodeRepository pinCodeRepository;

    @InjectMocks
    private PinCodeServiceImpl pinCodeService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Captor
    ArgumentCaptor<PinCode> pinCodeArgumentCaptor;


    @Before
    public void setUp(){
        pinCodeService.setMaxAttempts(maxAttempts);
        pinCodeService.setExpirationSeconds(expirationSeconds);
        pinCodeService.setLimitCount(limitCount);
        pinCodeService.setLimitSeconds(limitSeconds);
    }


    @Test
    public void testPinCodeServiceMaxPinCodesReached() throws Exception {
        when(pinCodeRepository.countUserPinCodes(eq(DEFAULT_USER.getId()), any(Date.class))).thenReturn(limitCount);
        thrown.expect(PinCodeException.MaxGenerationReached.class);
        pinCodeService.generate(DEFAULT_USER, DEFAULT_CODE_LENGTH);
    }


    @Test
    public void testPinCodeServiceGenerate() throws Exception {
        when(pinCodeRepository.countUserPinCodes(eq(DEFAULT_USER.getId()), any(Date.class))).thenReturn(0);
        pinCodeService.generate(DEFAULT_USER, DEFAULT_CODE_LENGTH);

        verify(pinCodeRepository).save(pinCodeArgumentCaptor.capture());

        PinCode pin = pinCodeArgumentCaptor.getValue();
        assertEquals(DEFAULT_USER.getId(), pin.getUserId().intValue());
        assertEquals(0, pin.getAttempts());
        assertEquals(DEFAULT_CODE_LENGTH, pin.getCode().length());
        assertFalse(pin.isEntered());
    }


    @Test
    public void testPinCodeServiceCheckNotFound() throws Exception {
        final PinCode pinCode = createPinCode(DEFAULT_USER, DEFAULT_CODE_LENGTH, new Date(), false);

        when(pinCodeRepository.findPinCodesByUserAndCreationTime(eq(DEFAULT_USER.getId()), any(Date.class))).thenReturn(asList(pinCode));

        User user = new User();
        user.setId(DEFAULT_USER.getId() + 1);

        thrown.expect(PinCodeException.NotValid.class);
        pinCodeService.attempt(user, pinCode.getCode());
    }


    @Test
    public void testPinCodeServiceCheckMaxAttemptsReached() throws Exception {
        final PinCode pinCode = createPinCode(DEFAULT_USER, DEFAULT_CODE_LENGTH, new Date(), false);
        ReflectionTestUtils.setField(pinCode, "attempts", maxAttempts);

        when(pinCodeRepository.findPinCodesByUserAndCreationTime(eq(DEFAULT_USER.getId()), any(Date.class))).thenReturn(asList(pinCode));

        thrown.expect(PinCodeException.MaxAttemptsReached.class);
        pinCodeService.attempt(DEFAULT_USER, pinCode.getCode());
    }


    @Test
    public void testPinCodeServiceCheckTrue() throws Exception {
        final PinCode pinCode = createPinCode(DEFAULT_USER, DEFAULT_CODE_LENGTH, new Date(), false);
        when(pinCodeRepository.findPinCodesByUserAndCreationTime(eq(DEFAULT_USER.getId()), any(Date.class))).thenReturn(asList(pinCode));

        boolean result = pinCodeService.attempt(DEFAULT_USER, pinCode.getCode());
        assertTrue(result);
    }


    @Test
    public void testPinCodeServiceCheckFalse() throws Exception {
        final PinCode pinCode = createPinCode(DEFAULT_USER, DEFAULT_CODE_LENGTH, new Date(), false);
        when(pinCodeRepository.findPinCodesByUserAndCreationTime(eq(DEFAULT_USER.getId()), any(Date.class))).thenReturn(asList(pinCode));

        boolean result = pinCodeService.attempt(DEFAULT_USER, "WRONG_CODE");
        assertFalse(result);
    }


    private PinCode createPinCode(User user, int codeLength, Date creationTime, boolean isEntered) {
        PinCode pinCode = new PinCode(user.getId(), RandomStringUtils.random(codeLength, false, true));
        ReflectionTestUtils.setField(pinCode, "creationTime", creationTime);
        pinCode.setEntered(isEntered);
        return pinCode;
    }
}














































































