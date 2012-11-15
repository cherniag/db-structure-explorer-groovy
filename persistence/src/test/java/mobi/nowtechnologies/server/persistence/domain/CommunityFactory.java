package mobi.nowtechnologies.server.persistence.domain;



/**
 * The class <code>CommunityFactory</code> implements static methods that return instances of the class <code>{@link Community}</code>.
 *
 * @generatedBy CodePro at 29.08.12 11:44
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
public class CommunityFactory
 {
	/**
	 * Prevent creation of instances of this class.
	 *
	 * @generatedBy CodePro at 29.08.12 11:44
	 */
	private CommunityFactory() {
	}


	/**
	 * Create an instance of the class <code>{@link Community}</code>.
	 *
	 * @generatedBy CodePro at 29.08.12 11:44
	 */
	public static Community createCommunity() {
		Community community = new Community();
		community.setName("name");
		community.setDisplayName("displayName");
		community.setRewriteUrlParameter("nowtop40");
		return community;
	}
}