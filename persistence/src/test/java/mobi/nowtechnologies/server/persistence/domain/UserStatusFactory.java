package mobi.nowtechnologies.server.persistence.domain;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class UserStatusFactory {
	
	
	public static UserStatus createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus choosedUserStatus){
		UserStatus userStatus = new UserStatus();
		userStatus.setName(choosedUserStatus.name());
		userStatus.setI(choosedUserStatus.getCode());
		
		return userStatus;
		
	}

}
