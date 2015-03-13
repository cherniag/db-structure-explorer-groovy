package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.UserStatus;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Titov Mykhaylo (titov)
 */
@Deprecated
public class UserStatusDao {

    public static final String SUBSCRIBED = "SUBSCRIBED";
    public static final String EULA = "EULA";
    public static final String LIMITED = "LIMITED";
    private static Map<Byte, UserStatus> USER_STATUS_MAP_ID_AS_KEY;
    private static Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY;
    private static UserStatus subscribedUserStatus;
    private static UserStatus eulaUserStatus;
    private static UserStatus limitedUserStatus;

    private static void setEntityDao(EntityDao entityDao) {
        List<UserStatus> userStatusList = entityDao.findAll(UserStatus.class);
        Map<Byte, UserStatus> userStatusMapIdAsKey = new LinkedHashMap<Byte, UserStatus>();
        Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> userStatusMapUserStatusAsKey = new LinkedHashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();

        for (UserStatus userStatus : userStatusList) {
            if (userStatus.getName().equals(SUBSCRIBED)) {
                subscribedUserStatus = userStatus;
                userStatusMapUserStatusAsKey.put(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED, userStatus);
            } else if (userStatus.getName().equals(EULA)) {
                eulaUserStatus = userStatus;
                userStatusMapUserStatusAsKey.put(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA, userStatus);
            } else if (userStatus.getName().equals(LIMITED)) {
                limitedUserStatus = userStatus;
                userStatusMapUserStatusAsKey.put(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED, userStatus);
            }

            userStatusMapIdAsKey.put(userStatus.getI(), userStatus);
        }

        if (subscribedUserStatus == null) {
            throw new PersistenceException("The parameter subscribedUserStatus is null");
        }
        if (eulaUserStatus == null) {
            throw new PersistenceException("The parameter eulaUserStatus is null");
        }
        if (limitedUserStatus == null) {
            throw new PersistenceException("The parameter limitedUserStatus is null");
        }

        USER_STATUS_MAP_ID_AS_KEY = Collections.unmodifiableMap(userStatusMapIdAsKey);
        USER_STATUS_MAP_USER_STATUS_AS_KEY = Collections.unmodifiableMap(userStatusMapUserStatusAsKey);
    }

    public static UserStatus getSubscribedUserStatus() {
        return subscribedUserStatus;
    }

    public static UserStatus getEulaUserStatus() {
        return eulaUserStatus;
    }

    public static UserStatus getLimitedUserStatus() {
        return limitedUserStatus;
    }

    public static Map<Byte, UserStatus> getUserStatusMapIdAsKey() {
        return USER_STATUS_MAP_ID_AS_KEY;
    }

    public static Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> getUserStatusMapUserStatusAsKey() {
        return USER_STATUS_MAP_USER_STATUS_AS_KEY;
    }

}
