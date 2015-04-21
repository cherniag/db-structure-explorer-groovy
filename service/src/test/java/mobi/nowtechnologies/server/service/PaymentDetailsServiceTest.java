package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.service.exception.CanNotDeactivatePaymentDetailsException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import static mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType.O2_PSMS;

import java.util.Date;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * User: Titov Mykhaylo (titov) 05.09.13 14:15
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PaymentDetailsService.class)
public class PaymentDetailsServiceTest {

    @Mock
    private PaymentPolicyService paymentPolicyServiceMock;
    @Mock
    private UserService userService;
    @Mock
    private PaymentDetailsRepository paymentDetailsRepositoryMock;

    private PaymentDetailsService paymentDetailsServiceSpy;
    private PaymentPolicy defaultPaymentPolicy;
    private O2PSMSPaymentDetails expectedO2PSMSPaymentDetails;
    private User user;
    private ArgumentMatcher<PaymentDetailsDto> o2PsmsPaymentDetailsDtoMatcher;

    @Before
    public void setUp() {
        user = new User().withUserGroup(new UserGroup().withCommunity(new Community()));

        paymentDetailsServiceSpy = PowerMockito.spy(new PaymentDetailsService());
        paymentDetailsServiceSpy.paymentDetailsRepository = paymentDetailsRepositoryMock;
        paymentDetailsServiceSpy.setUserService(userService);
        paymentDetailsServiceSpy.setPaymentPolicyService(paymentPolicyServiceMock);

        o2PsmsPaymentDetailsDtoMatcher = new ArgumentMatcher<PaymentDetailsDto>() {
            @Override
            public boolean matches(Object argument) {
                PaymentDetailsDto paymentDetailsDto = (PaymentDetailsDto) argument;

                assertEquals(O2_PSMS, paymentDetailsDto.getPaymentType());
                assertEquals(defaultPaymentPolicy.getId(), paymentDetailsDto.getPaymentPolicyId());

                return true;
            }
        };
    }

    @Test
    public void shouldNotTryToDeactivateCurrentPaymentDetailsIfOneExistWhenDetailsIsNull() throws Exception {
        String reason = "reason";

        when(userService.setToZeroSmsAccordingToLawAttributes(user)).thenReturn(user);


        paymentDetailsServiceSpy.deactivateCurrentPaymentDetailsIfOneExist(user, reason);

        verify(paymentDetailsRepositoryMock, times(0)).save(any(PaymentDetails.class));
        verify(userService, times(0)).updateUser(user);
        verify(userService).setToZeroSmsAccordingToLawAttributes(user);
    }

    @Test
    public void shouldTryToDeactivateCurrentPaymentDetailsIfOneExistWhenDetailsStatusIsNotAwaiting() throws Exception {
        String reason = "reason";

        PaymentDetails paymentDetails = mock(PaymentDetails.class);
        when(paymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.NONE);

        user.setCurrentPaymentDetails(paymentDetails);


        when(userService.setToZeroSmsAccordingToLawAttributes(user)).thenReturn(user);
        when(userService.updateUser(user)).thenReturn(user);

        paymentDetailsServiceSpy.deactivateCurrentPaymentDetailsIfOneExist(user, reason);

        verify(paymentDetails).disable(eq(reason), any(Date.class));
        verify(paymentDetailsRepositoryMock, times(1)).save(paymentDetails);
        verify(userService).updateUser(user);
        verify(userService).setToZeroSmsAccordingToLawAttributes(user);
    }

    @Test(expected = CanNotDeactivatePaymentDetailsException.class)
    public void shouldNotTryToDeactivateCurrentPaymentDetailsIfOneExistWhenDetailsStatusIsAwaiting() throws Exception {
        String reason = "reason";

        PaymentDetails paymentDetails = mock(PaymentDetails.class);
        when(paymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.AWAITING);

        user.setCurrentPaymentDetails(paymentDetails);

        doReturn(paymentDetails).when(paymentDetailsRepositoryMock).save(paymentDetails);
        when(userService.setToZeroSmsAccordingToLawAttributes(user)).thenReturn(user);

        paymentDetailsServiceSpy.deactivateCurrentPaymentDetailsIfOneExist(user, reason);

        verify(paymentDetailsRepositoryMock, times(0)).save(paymentDetails);
        verify(userService, times(0)).updateUser(user);
        verify(userService).setToZeroSmsAccordingToLawAttributes(user);
    }

    @Test
    public void shouldCreateDefaultO2PsmsPaymentDetails() throws ServiceException {
        //given
        defaultPaymentPolicy = new PaymentPolicy().withId(Integer.MAX_VALUE);
        expectedO2PSMSPaymentDetails = new O2PSMSPaymentDetails();

        doReturn(defaultPaymentPolicy).when(paymentPolicyServiceMock).findDefaultO2PsmsPaymentPolicy(user);
        doReturn(expectedO2PSMSPaymentDetails).when(paymentDetailsServiceSpy).createPaymentDetails(argThat(o2PsmsPaymentDetailsDtoMatcher), eq(user), eq(user.getUserGroup().getCommunity()));

        //when
        O2PSMSPaymentDetails o2PSMSPaymentDetails = paymentDetailsServiceSpy.createDefaultO2PsmsPaymentDetails(user);

        //then
        assertNotNull(o2PSMSPaymentDetails);
        assertEquals(expectedO2PSMSPaymentDetails, o2PSMSPaymentDetails);

        verify(paymentPolicyServiceMock, times(1)).findDefaultO2PsmsPaymentPolicy(user);
        verify(paymentDetailsServiceSpy, times(1)).createPaymentDetails(argThat(o2PsmsPaymentDetailsDtoMatcher), eq(user), eq(user.getUserGroup().getCommunity()));
    }

    @Test(expected = ServiceException.class)
    public void shouldDoNotCreateDefaultO2PsmsPaymentDetails() throws ServiceException {
        //given
        defaultPaymentPolicy = null;
        expectedO2PSMSPaymentDetails = new O2PSMSPaymentDetails();

        doReturn(defaultPaymentPolicy).when(paymentPolicyServiceMock).findDefaultO2PsmsPaymentPolicy(user);
        doReturn(expectedO2PSMSPaymentDetails).when(paymentDetailsServiceSpy).createPaymentDetails(argThat(o2PsmsPaymentDetailsDtoMatcher), eq(user), eq(user.getUserGroup().getCommunity()));

        //when
        paymentDetailsServiceSpy.createDefaultO2PsmsPaymentDetails(user);
    }
}
