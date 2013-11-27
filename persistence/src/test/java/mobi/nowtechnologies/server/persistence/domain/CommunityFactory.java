package mobi.nowtechnologies.server.persistence.domain;

/**
 * @generatedBy CodePro at 29.08.12 11:44
 * @author Titov Mykhaylo (titov)
 */
public class CommunityFactory{

	public static Community createCommunity() {
		Community community = new Community();
		community.setId(1);
		community.setName("name");
		community.setDisplayName("displayName");
		community.setRewriteUrlParameter("nowtop40");
		return community;
	}
}