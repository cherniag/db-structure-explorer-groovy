package mobi.nowtechnologies.server.persistence.domain;



/**
 * The class <code>UserGroupFactory</code> implements static methods that return instances of the class <code>{@link UserGroup}</code>.
 *
 * @generatedBy CodePro at 29.08.12 17:55
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
public class UserGroupFactory
 {
	/**
	 * Prevent creation of instances of this class.
	 *
	 * @generatedBy CodePro at 29.08.12 17:55
	 */
	private UserGroupFactory() {
	}


	/**
	 * Create an instance of the class <code>{@link UserGroup}</code>.
	 *
	 * @generatedBy CodePro at 29.08.12 17:55
	 */
	public static UserGroup createUserGroup() {
		return new UserGroup();
	}
	
	public static UserGroup createUserGroup(Community community) {
		UserGroup userGroup = new UserGroup();
		userGroup.setCommunity(community);
		return userGroup;
	}
}