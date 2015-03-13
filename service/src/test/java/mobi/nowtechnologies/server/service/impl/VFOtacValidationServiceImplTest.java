package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.UserService;
import static mobi.nowtechnologies.server.service.VFOtacValidationService.TEST_OTAC_NON_VF;
import static mobi.nowtechnologies.server.service.VFOtacValidationService.TEST_OTAC_VF;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_VF;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.VF;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import static org.hamcrest.CoreMatchers.is;

import org.powermock.modules.junit4.PowerMockRunner;

/**
 * User: Titov Mykhaylo (titov) 30.09.13 17:41
 */
@RunWith(PowerMockRunner.class)
public class VFOtacValidationServiceImplTest {

    @Mock
    public UserService userServiceMock;
    private VFOtacValidationServiceImpl vfOtacValidationServiceImplFixture;

    @Before
    public void setUp() {
        vfOtacValidationServiceImplFixture = new VFOtacValidationServiceImpl();
        vfOtacValidationServiceImplFixture.setUserService(userServiceMock);
    }

    @Test
    public void shouldValidateAsPromotedNonVFDevice() throws Exception {
        //given
        String otac = TEST_OTAC_NON_VF;
        String phoneNumber = "phoneNumber";
        Community community = new Community().withRewriteUrl("vf_nz");

        doReturn(true).when(userServiceMock).isVFNZOtacValid(otac, phoneNumber, community);
        doReturn(true).when(userServiceMock).isPromotedDevice(phoneNumber, community);

        //when
        ProviderUserDetails providerUserDetails = vfOtacValidationServiceImplFixture.validate(otac, phoneNumber, community);

        //then
        assertThat(providerUserDetails.operator, is(NON_VF.getKey()));

        verify(userServiceMock, times(0)).isVFNZOtacValid(otac, phoneNumber, community);
        verify(userServiceMock, times(1)).isPromotedDevice(phoneNumber, community);
    }

    @Test
    public void shouldValidateAsPromotedVFDevice() throws Exception {
        //given
        String otac = TEST_OTAC_VF;
        String phoneNumber = "phoneNumber";
        Community community = new Community().withRewriteUrl("vf_nz");

        doReturn(true).when(userServiceMock).isVFNZOtacValid(otac, phoneNumber, community);
        doReturn(true).when(userServiceMock).isPromotedDevice(phoneNumber, community);

        //when
        ProviderUserDetails providerUserDetails = vfOtacValidationServiceImplFixture.validate(otac, phoneNumber, community);

        //then
        assertThat(providerUserDetails.operator, is(VF.getKey()));

        verify(userServiceMock, times(0)).isVFNZOtacValid(otac, phoneNumber, community);
        verify(userServiceMock, times(1)).isPromotedDevice(phoneNumber, community);
    }

    @Test
    public void shouldValidateAsPromotedVFDeviceWithNotTestedOtac() throws Exception {
        //given
        String otac = "";
        String phoneNumber = "phoneNumber";
        Community community = new Community().withRewriteUrl("vf_nz");

        doReturn(true).when(userServiceMock).isVFNZOtacValid(otac, phoneNumber, community);
        doReturn(true).when(userServiceMock).isPromotedDevice(phoneNumber, community);

        //when
        ProviderUserDetails providerUserDetails = vfOtacValidationServiceImplFixture.validate(otac, phoneNumber, community);

        //then
        assertNull(providerUserDetails.operator);

        verify(userServiceMock, times(1)).isVFNZOtacValid(otac, phoneNumber, community);
        verify(userServiceMock, times(1)).isPromotedDevice(phoneNumber, community);
    }

    @Test
    public void shouldValidateAsNotPromotedVFDevice() throws Exception {
        //given
        String otac = TEST_OTAC_NON_VF;
        String phoneNumber = "phoneNumber";
        Community community = new Community().withRewriteUrl("vf_nz");

        doReturn(true).when(userServiceMock).isVFNZOtacValid(otac, phoneNumber, community);
        doReturn(false).when(userServiceMock).isPromotedDevice(phoneNumber, community);

        //when
        ProviderUserDetails providerUserDetails = vfOtacValidationServiceImplFixture.validate(otac, phoneNumber, community);

        //then
        assertNull(providerUserDetails.operator);

        verify(userServiceMock, times(1)).isVFNZOtacValid(otac, phoneNumber, community);
        verify(userServiceMock, times(1)).isPromotedDevice(phoneNumber, community);
    }
}
