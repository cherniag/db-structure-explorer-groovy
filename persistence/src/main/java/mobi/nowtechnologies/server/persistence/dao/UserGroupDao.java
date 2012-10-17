package mobi.nowtechnologies.server.persistence.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.nowtechnologies.server.persistence.domain.UserGroup;

public class UserGroupDao {
	private static Map<Byte, UserGroup> USER_GROUP_MAP_COMMUNITY_ID_AS_KEY;

	private static void setEntityDao(EntityDao entityDao) {
		List<UserGroup> userGroupList = entityDao.findAll(UserGroup.class);
		Map<Byte, UserGroup> userGroupCommunityIdMap = new HashMap<Byte, UserGroup>();
		for (UserGroup userGroup : userGroupList) {
			userGroupCommunityIdMap.put(userGroup.getCommunityId(), userGroup);
		}
		USER_GROUP_MAP_COMMUNITY_ID_AS_KEY = Collections.unmodifiableMap(userGroupCommunityIdMap);
	}

	public static Map<Byte, UserGroup> getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY() {
		return USER_GROUP_MAP_COMMUNITY_ID_AS_KEY;
	}
}