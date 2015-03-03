package mobi.nowtechnologies.server.persistence.domain;

/**
 * The class <code>UserGroupFactory</code> implements static methods that return instances of the class <code>{@link UserGroup}</code>.
 *
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
public class UserGroupFactory {

    /**
     * Prevent creation of instances of this class.
     */
    private UserGroupFactory() {
    }

    /**
     * Create an instance of the class <code>{@link UserGroup}</code>.
     */
    public static UserGroup createUserGroup() {
        UserGroup userGroup = new UserGroup();
        userGroup.setName("Some name");

        return userGroup;
    }

    public static UserGroup createUserGroup(Community community) {
        UserGroup userGroup = new UserGroup();
        userGroup.setCommunity(community);
        return userGroup;
    }
}