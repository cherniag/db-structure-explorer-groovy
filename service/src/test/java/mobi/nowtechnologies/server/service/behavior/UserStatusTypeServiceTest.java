package mobi.nowtechnologies.server.service.behavior;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserStatusTypeServiceTest {

    User user = mock(User.class);
    PaymentDetails paymentDetails = mock(PaymentDetails.class);

    UserStatusTypeService userStatusTypeService = new UserStatusTypeService();

    @Test
    public void testUserStatusesToSinceMappingWhenUserIsInLimitedStatus() throws Exception {
        Date freeTrialExpiredDate = new Date();
        Date serverTime = DateUtils.addDays(freeTrialExpiredDate, -1);

        when(user.isSubscribedStatus()).thenReturn(true);
        when(user.getFreeTrialExpiredAsDate()).thenReturn(freeTrialExpiredDate);

        List<Pair<UserStatusType, Date>> userStatusTypeDatePairs = userStatusTypeService.userStatusesToSinceMapping(user, serverTime);

        assertEquals(UserStatusType.FREE_TRIAL, userStatusTypeDatePairs.get(0).getKey());
        assertEquals(serverTime, userStatusTypeDatePairs.get(0).getValue());

        assertEquals(UserStatusType.LIMITED, userStatusTypeDatePairs.get(1).getKey());
        assertEquals(freeTrialExpiredDate, userStatusTypeDatePairs.get(1).getValue());
    }

    @Test
    public void testUserStatusesToSinceMappingWhenUserIsOnFreeTrial() throws Exception {
        Date freeTrialExpiredDate = new Date();
        Date serverTime = DateUtils.addDays(freeTrialExpiredDate, -1);

        when(user.isSubscribedStatus()).thenReturn(true);
        when(user.getFreeTrialExpiredAsDate()).thenReturn(freeTrialExpiredDate);

        List<Pair<UserStatusType, Date>> userStatusTypeDatePairs = userStatusTypeService.userStatusesToSinceMapping(user, serverTime);

        assertEquals(UserStatusType.FREE_TRIAL, userStatusTypeDatePairs.get(0).getKey());
        assertEquals(serverTime, userStatusTypeDatePairs.get(0).getValue());

        assertEquals(UserStatusType.LIMITED, userStatusTypeDatePairs.get(1).getKey());
        assertEquals(freeTrialExpiredDate, userStatusTypeDatePairs.get(1).getValue());
    }

    @Test
    public void testUserStatusesToSinceMappingWhenUserIsFreeTrialAndJustSubscribed() throws Exception {
        Date freeTrialExpiredDate = new Date();
        Date serverTime = DateUtils.addDays(freeTrialExpiredDate, -1);

        when(user.isSubscribedStatus()).thenReturn(true);
        when(user.getFreeTrialExpiredAsDate()).thenReturn(freeTrialExpiredDate);
        when(user.getCurrentPaymentDetails()).thenReturn(paymentDetails);
        when(paymentDetails.isActivated()).thenReturn(true);

        List<Pair<UserStatusType, Date>> userStatusTypeDatePairs = userStatusTypeService.userStatusesToSinceMapping(user, serverTime);

        assertEquals(UserStatusType.SUBSCRIBED, userStatusTypeDatePairs.get(0).getKey());
        assertEquals(serverTime, userStatusTypeDatePairs.get(0).getValue());

        assertEquals(UserStatusType.LIMITED, userStatusTypeDatePairs.get(1).getKey());
        assertEquals(freeTrialExpiredDate, userStatusTypeDatePairs.get(1).getValue());
    }

    @Test
    public void testUserStatusesToSinceMappingWhenUserIsFreeTrialAndSubscribedAndPaid() throws Exception {
        Date freeTrialExpiredDate = new Date();
        Date serverTime = DateUtils.addDays(freeTrialExpiredDate, -1);
        Date nextPaymentDate = DateUtils.addDays(freeTrialExpiredDate, -1);

        when(user.getNextSubPaymentAsDate()).thenReturn(nextPaymentDate);
        when(user.isSubscribedStatus()).thenReturn(true);
        when(user.getFreeTrialExpiredAsDate()).thenReturn(freeTrialExpiredDate);
        when(user.getCurrentPaymentDetails()).thenReturn(paymentDetails);
        when(paymentDetails.isActivated()).thenReturn(true);

        List<Pair<UserStatusType, Date>> userStatusTypeDatePairs = userStatusTypeService.userStatusesToSinceMapping(user, serverTime);

        assertEquals(UserStatusType.SUBSCRIBED, userStatusTypeDatePairs.get(0).getKey());
        assertEquals(serverTime, userStatusTypeDatePairs.get(0).getValue());

        assertEquals(UserStatusType.LIMITED, userStatusTypeDatePairs.get(1).getKey());
        assertEquals(freeTrialExpiredDate, userStatusTypeDatePairs.get(1).getValue());
    }

    @Test
    public void testUserStatusesToSinceMappingWhenUserHasPaymentDetailsErrorAndCanRetry(){
        Date serverTime = new Date();
        Date freeTrialExpiredDate = DateUtils.addDays(serverTime, -1);

        when(user.getNextSubPaymentAsDate()).thenReturn(serverTime);
        when(user.isSubscribedStatus()).thenReturn(true);
        when(user.getFreeTrialExpiredAsDate()).thenReturn(freeTrialExpiredDate);
        when(user.getCurrentPaymentDetails()).thenReturn(paymentDetails);
        when(user.isPaymentInProgress()).thenReturn(true);

        List<Pair<UserStatusType, Date>> userStatusTypeDatePairs = userStatusTypeService.userStatusesToSinceMapping(user, serverTime);

        assertEquals(1, userStatusTypeDatePairs.size());
        assertEquals(serverTime, userStatusTypeDatePairs.get(0).getValue());
        assertEquals(UserStatusType.LIMITED, userStatusTypeDatePairs.get(0).getKey());
    }

}