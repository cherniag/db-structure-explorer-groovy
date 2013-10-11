package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.VFOtacValidationService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertNull;
import static mobi.nowtechnologies.server.persistence.domain.enums.ProviderType.NON_VF;
import static mobi.nowtechnologies.server.persistence.domain.enums.ProviderType.VF;
import static mobi.nowtechnologies.server.service.VFOtacValidationService.*;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * User: Titov Mykhaylo (titov)
 * 30.09.13 17:41
 */
@RunWith(PowerMockRunner.class)
public class VFOtacValidationServiceImplTest {

    private VFOtacValidationServiceImpl vfOtacValidationServiceImplFixture;

    @Mock
    public UserService userServiceMock;

    @Before
    public void setUp(){
        vfOtacValidationServiceImplFixture = new VFOtacValidationServiceImpl();
        vfOtacValidationServiceImplFixture.setUserService(userServiceMock);
    }

    @Test
    public void shouldValidateAsPromotedNonVFDevice() throws Exception{
        //given
        String otac = TEST_OTAC_NON_VF;
        String phoneNumber = "phoneNumber";
        Community community = new Community().withRewriteUrl("vf_nz");

        doReturn(true).when(userServiceMock).isVFNZOtacValid(otac, phoneNumber, community);
        doReturn(true).when(userServiceMock).isPromotedDevice(phoneNumber, community);

        //when
        ProviderUserDetails providerUserDetails = vfOtacValidationServiceImplFixture.validate(otac, phoneNumber, community);

        //then
        assertThat(providerUserDetails.contract, is(PAYG.name()));
        assertThat(providerUserDetails.operator, is(NON_VF.toString()));

        verify(userServiceMock, times(0)).isVFNZOtacValid(otac, phoneNumber, community);
        verify(userServiceMock, times(1)).isPromotedDevice(phoneNumber, community);
    }

    @Test
    public void shouldValidateAsPromotedVFDevice() throws Exception{
        //given
        String otac = TEST_OTAC_VF;
        String phoneNumber = "phoneNumber";
        Community community = new Community().withRewriteUrl("vf_nz");

        doReturn(true).when(userServiceMock).isVFNZOtacValid(otac, phoneNumber, community);
        doReturn(true).when(userServiceMock).isPromotedDevice(phoneNumber, community);

        //when
        ProviderUserDetails providerUserDetails = vfOtacValidationServiceImplFixture.validate(otac, phoneNumber, community);

        //then
        assertThat(providerUserDetails.contract, is(PAYG.name()));
        assertThat(providerUserDetails.operator, is(VF.toString()));

        verify(userServiceMock, times(0)).isVFNZOtacValid(otac, phoneNumber, community);
        verify(userServiceMock, times(1)).isPromotedDevice(phoneNumber, community);
    }

    @Test
    public void shouldValidateAsPromotedVFDeviceWithNotTestedOtac() throws Exception{
        //given
        String otac = "";
        String phoneNumber = "phoneNumber";
        Community community = new Community().withRewriteUrl("vf_nz");

        doReturn(true).when(userServiceMock).isVFNZOtacValid(otac, phoneNumber, community);
        doReturn(true).when(userServiceMock).isPromotedDevice(phoneNumber, community);

        //when
        ProviderUserDetails providerUserDetails = vfOtacValidationServiceImplFixture.validate(otac, phoneNumber, community);

        //then
        assertThat(providerUserDetails.contract, is(PAYG.name()));
        assertNull(providerUserDetails.operator);

        verify(userServiceMock, times(1)).isVFNZOtacValid(otac, phoneNumber, community);
        verify(userServiceMock, times(1)).isPromotedDevice(phoneNumber, community);
    }

    @Test
    public void shouldValidateAsNotPromotedVFDevice() throws Exception{
        //given
        String otac = TEST_OTAC_NON_VF;
        String phoneNumber = "phoneNumber";
        Community community = new Community().withRewriteUrl("vf_nz");

        doReturn(true).when(userServiceMock).isVFNZOtacValid(otac, phoneNumber, community);
        doReturn(false).when(userServiceMock).isPromotedDevice(phoneNumber, community);

        //when
        ProviderUserDetails providerUserDetails = vfOtacValidationServiceImplFixture.validate(otac, phoneNumber, community);

        //then
        assertThat(providerUserDetails.contract, is(PAYG.name()));
        assertNull(providerUserDetails.operator);

        verify(userServiceMock, times(1)).isVFNZOtacValid(otac, phoneNumber, community);
        verify(userServiceMock, times(1)).isPromotedDevice(phoneNumber, community);
    }
}
