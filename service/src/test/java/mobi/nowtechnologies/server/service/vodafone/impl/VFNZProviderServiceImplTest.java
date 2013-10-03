package mobi.nowtechnologies.server.service.vodafone.impl;

import junit.framework.Assert;
import mobi.nowtechnologies.server.persistence.domain.enums.ProviderType;
import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.validator.NZCellNumberValidator;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.times;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 10/2/13
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utils.class})
public class VFNZProviderServiceImplTest {
    private VFNZProviderServiceImpl fixture;

    @Mock
    private NZCellNumberValidator mockNzCellNumberValidator;

    @Before
    public void setUp() throws Exception {
          fixture = new VFNZProviderServiceImpl();
          fixture.setPhoneValidator(mockNzCellNumberValidator);
    }

    @Test
    public void testValidatePhoneNumber_Success() throws Exception {
        String phoneNumber = "2111111111";

        Mockito.when(mockNzCellNumberValidator.validate(Matchers.eq(phoneNumber))).thenReturn("+642111111111");
        PowerMockito.mockStatic(Utils.class);

        Mockito.when(Utils.generateRandomPIN()).thenReturn(1000);

        PhoneNumberValidationData result = fixture.validatePhoneNumber(phoneNumber);

        Assert.assertEquals("+642111111111", result.getPhoneNumber());
        Assert.assertEquals("1000", result.getPin());

        Mockito.verify(mockNzCellNumberValidator, Mockito.times(1)).validate(Matchers.eq(phoneNumber));
        PowerMockito.verifyStatic(times(1));
        Utils.generateRandomPIN();
    }

    @Test
    public void testGetSubscriberData_Success() throws Exception {
        String phoneNumber = "+642111111111";

        VFNZSubscriberData result = fixture.getSubscriberData(phoneNumber);

        Assert.assertEquals(ProviderType.ON_NET, result.getProvider());
    }
}
