package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType.O2_PSMS;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;

/**
 * User: Titov Mykhaylo (titov)
 * 05.09.13 14:15
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PaymentDetailsService.class)
public class PaymentDetailsServiceTest {

    @Mock
    public PaymentPolicyService paymentPolicyServiceMock;

    private PaymentDetailsService paymentDetailsServiceSpy;
    private PaymentPolicy defaultPaymentPolicy;
    private O2PSMSPaymentDetails expectedO2PSMSPaymentDetails;
    private User user;
    private ArgumentMatcher<PaymentDetailsDto> o2PsmsPaymentDetailsDtoMatcher;

    @Before
    public void setUp() {
        paymentDetailsServiceSpy = PowerMockito.spy(new PaymentDetailsService());
        paymentDetailsServiceSpy.setPaymentPolicyService(paymentPolicyServiceMock);

        o2PsmsPaymentDetailsDtoMatcher = new ArgumentMatcher<PaymentDetailsDto>() {
            @Override
            public boolean matches(Object argument) {
                PaymentDetailsDto paymentDetailsDto = (PaymentDetailsDto) argument;

                assertEquals(O2_PSMS, paymentDetailsDto.getPaymentType());
                assertEquals(defaultPaymentPolicy.getId(),paymentDetailsDto.getPaymentPolicyId());

                return true;
            }
        };
    }

    @Test
    public void shouldCreateDefaultO2PsmsPaymentDetails() throws ServiceException {
        //given
        user = new User().withUserGroup(new UserGroup().withCommunity(new Community()));
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
        user = new User().withUserGroup(new UserGroup().withCommunity(new Community()));
        defaultPaymentPolicy = null;
        expectedO2PSMSPaymentDetails = new O2PSMSPaymentDetails();

        doReturn(defaultPaymentPolicy).when(paymentPolicyServiceMock).findDefaultO2PsmsPaymentPolicy(user);
        doReturn(expectedO2PSMSPaymentDetails).when(paymentDetailsServiceSpy).createPaymentDetails(argThat(o2PsmsPaymentDetailsDtoMatcher), eq(user), eq(user.getUserGroup().getCommunity()));

        //when
        paymentDetailsServiceSpy.createDefaultO2PsmsPaymentDetails(user);
    }

    @Test
    public void shouldFindFailurePaymentPaymentDetailsWithNoNotification(){
        //given
        String communityUrl ="";
        Pageable pageable = new PageRequest(0,1);

        List<PaymentDetails> expectedPaymentDetailsList = Collections.<PaymentDetails>singletonList(new O2PSMSPaymentDetails());

        doReturn(expectedPaymentDetailsList).when(paymentDetailsRepositoryMock).findFailedPaymentWithNoNotificationPaymentDetails(communityUrl, pageable);;

        //when
        List<PaymentDetails> paymentDetailsList = paymentDetailsServiceFixture.findFailedPaymentWithNoNotificationPaymentDetails(communityUrl, pageable);

        //then
        assertThat(paymentDetailsList, is(expectedPaymentDetailsList));
    }
}
