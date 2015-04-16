package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.UserGroup;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserGroupDao {

    private static Map<Integer, UserGroup> USER_GROUP_MAP_COMMUNITY_ID_AS_KEY;

    private static void setEntityDao(EntityDao entityDao) {
        List<UserGroup> userGroupList = entityDao.findAll(UserGroup.class);
        Map<Integer, UserGroup> userGroupCommunityIdMap = new HashMap<Integer, UserGroup>();
        for (UserGroup userGroup : userGroupList) {
            userGroupCommunityIdMap.put(userGroup.getCommunityId(), userGroup);
        }
        USER_GROUP_MAP_COMMUNITY_ID_AS_KEY = Collections.unmodifiableMap(userGroupCommunityIdMap);
    }

}