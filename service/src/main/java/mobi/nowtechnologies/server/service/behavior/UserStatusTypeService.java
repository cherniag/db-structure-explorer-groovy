package mobi.nowtechnologies.server.service.behavior;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class UserStatusTypeService {

    public List<Pair<UserStatusType, Date>> userStatusesToSinceMapping(User user, Date time) {
        final UserStatusType currentStatus = of(user, time);

        PaymentDetails paymentDetails = user.getCurrentPaymentDetails();
        if(paymentDetails!=null && (paymentDetails.isAwaiting() || paymentDetails.isErrorAndCanRetry())){
            Pair<UserStatusType, Date> pair = new ImmutablePair<>(UserStatusType.LIMITED, time);
            return Collections.singletonList(pair);
        }

        if (currentStatus == UserStatusType.LIMITED) {
            Pair<UserStatusType, Date> pair = new ImmutablePair<>(UserStatusType.LIMITED, possibleLimitedSince(user, time));
            return Collections.singletonList(pair);
        }

        if (currentStatus == UserStatusType.FREE_TRIAL) {
            if (userHasActivePaymentDetailsAndIsStillOnFreeTrial(user, time)) {
                List<Pair<UserStatusType, Date>> orderOfStatuses = new ArrayList<>(2);
                orderOfStatuses.add(new ImmutablePair<>(UserStatusType.SUBSCRIBED, time));
                orderOfStatuses.add(new ImmutablePair<>(UserStatusType.LIMITED, user.getFreeTrialExpiredAsDate()));
                return orderOfStatuses;
            }
        }

        List<Pair<UserStatusType, Date>> orderOfStatuses = new ArrayList<>(2);
        orderOfStatuses.add(new ImmutablePair<>(currentStatus, time));
        orderOfStatuses.add(new ImmutablePair<>(UserStatusType.LIMITED, possibleLimitedSince(user, time)));

        return orderOfStatuses;
    }

    private UserStatusType of(User user, Date serverTime) {
        if (user.isSubscribedStatus() && user.getFreeTrialExpiredAsDate().after(serverTime)) {
            return UserStatusType.FREE_TRIAL;
        }

        if (user.isSubscribedStatus() && user.getNextSubPaymentAsDate().after(user.getFreeTrialExpiredAsDate())) {
            return UserStatusType.SUBSCRIBED;
        }

        return UserStatusType.LIMITED;
    }

    private boolean userHasActivePaymentDetailsAndIsStillOnFreeTrial(User user, Date serverTime) {
        return user.isSubscribedStatus() &&
               user.getCurrentPaymentDetails() != null &&
               user.getCurrentPaymentDetails().isActivated() &&
               user.getFreeTrialExpiredAsDate().after(serverTime) &&
               user.getNextSubPayment() <= user.getFreeTrialExpiredMillis();
    }

    private Date possibleLimitedSince(User user, Date time) {
        UserStatusType current = of(user, time);

        if (current == UserStatusType.SUBSCRIBED) {
            return user.getNextSubPaymentAsDate();
        }

        if (current == UserStatusType.FREE_TRIAL) {
            return user.getFreeTrialExpiredAsDate();
        }

        return time;
    }
}
