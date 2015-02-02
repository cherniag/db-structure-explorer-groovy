package mobi.nowtechnologies.server.service.behavior;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

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
}