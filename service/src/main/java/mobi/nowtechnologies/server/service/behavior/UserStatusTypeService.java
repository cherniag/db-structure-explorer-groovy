package mobi.nowtechnologies.server.service.behavior;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserStatusTypeService {
    public Map<UserStatusType, Date> userStatusesToSinceMapping(User user, Date time) {
        Map<UserStatusType, Date> orderOfStatuses = new LinkedHashMap<UserStatusType, Date>(2);

        UserStatusType currentStatus = of(user, time);
        orderOfStatuses.put(currentStatus, time);

        if(currentStatus != UserStatusType.LIMITED) {
            orderOfStatuses.put(UserStatusType.LIMITED, possibleLimitedSince(user, time));
        }

        return orderOfStatuses;
    }

    UserStatusType of(User user, Date time) {
        if(user.isSubscribedStatus() && user.getFreeTrialExpiredMillis() > time.getTime()) {
            return UserStatusType.FREE_TRIAL;
        }

        if(user.isSubscribedStatus()) {
            return UserStatusType.SUBSCRIBED;
        }

        return UserStatusType.LIMITED;
    }

    Date possibleLimitedSince(User user, Date time) {
        UserStatusType current = of(user, time);

        if(current == UserStatusType.SUBSCRIBED) {
            return user.getNextSubPaymentAsDate();
        }

        if(current == UserStatusType.FREE_TRIAL) {
            return new Date(user.getFreeTrialExpiredMillis());
        }

        return time;
    }
}
