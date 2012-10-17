package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "tb_useriPhoneDetails")
@NamedQueries( {
	@NamedQuery(name = UserIPhoneDetails.NQ_GET_USER_IPHONE_DETAILS_BY_USER_ID, query = "SELECT userIPhoneDetails FROM UserIPhoneDetails userIPhoneDetails WHERE userIPhoneDetails.userId=?"),
	@NamedQuery(name = UserIPhoneDetails.NQ_GET_USER_IPHONE_DETAILS_LIST_FOR_PUSH_NOTIFICATION, query = "SELECT userIPhoneDetails FROM UserIPhoneDetails userIPhoneDetails JOIN userIPhoneDetails.userGroup userGroup WHERE userIPhoneDetails.status=1 and userGroup.communityId=?"),
	@NamedQuery(name = UserIPhoneDetails.NQ_UPDATE_USER_IPHONE_DETAILS_LIST_FOR_PUSH_NOTIFICATION, query = "UPDATE UserIPhoneDetails userIPhoneDetails SET userIPhoneDetails.status=1 WHERE userIPhoneDetails.status=2 and userIPhoneDetails.userGroupId IN (select userGroup from UserGroup userGroup where userGroup.communityId=?)") 
})
public class UserIPhoneDetails extends UserDeviceDetails{

	public static final String NQ_GET_USER_IPHONE_DETAILS_BY_USER_ID = "NQ_GET_USER_IPHONE_DETAILS_BY_USER_ID";
	public static final String NQ_GET_USER_IPHONE_DETAILS_LIST_FOR_PUSH_NOTIFICATION = "NQ_GET_USER_IPHONE_DETAILS_LIST_FOR_PUSH_NOTIFICATION";
	public static final String NQ_UPDATE_USER_IPHONE_DETAILS_LIST_FOR_PUSH_NOTIFICATION = "NQ_UPDATE_USER_IPHONE_DETAILS_LIST_FOR_PUSH_NOTIFICATION";
	
	@Override
	public String toString() {
		return "UserIPhoneDetails [" + super.toString() + "]";
	}

}
