/**
 * 
 */
package mobi.nowtechnologies.server.persistence.domain;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import mobi.nowtechnologies.server.shared.Utils;

import org.junit.Test;

/**
 * Creational test for entity User
 * @author Titov Mykhaylo (titov)
 * @author Mayboroda Dmytro
 *
 */
public class UserTest {
	
	/**
	 * user.isOnFreeTrial() returns true
	 * only if freeTrialExpiredMillis > System.currentMillis  
	 */
	@Test
	public void isOnFreeTrial_true_when_freeTrialExpiredMillis_Gt_currentMillis() {
		User user = new User();
			user.setFreeTrialExpiredMillis(System.currentTimeMillis()+200000L);
		assertEquals(true, user.isOnFreeTrial());
	}
	
	/**
	 * user.isOnFreeTrial() returns false
	 * only if freeTrialExpiredMillis < System.currentMillis  
	 */
	@Test
	public void isOnFreeTrial_false_when_freeTrialExpiredMillis_Lt_currentMillis() {
		User user = new User();
			user.setFreeTrialExpiredMillis(System.currentTimeMillis());
		assertEquals(false, user.isOnFreeTrial());
	}
	
	/**
	 * user.isOnFreeTrial() returns false
	 * only if freeTrialExpiredMillis == null  
	 */
	@Test
	public void isOnFreeTrial_false_when_freeTrialExpiredMillis_Eq_Null() {
		User user = new User();
			user.setFreeTrialExpiredMillis(null);
		assertEquals(false, user.isOnFreeTrial());
	}
	
	@Test
	public void isO2Consumer_Success(){

		Community community = new Community();
		community.setRewriteUrlParameter("o2");
		
		UserGroup userGroup = new UserGroup();
		userGroup.setCommunity(community);
		
		User user = new User();
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment("consumer");
		user.setContract("PAYG");
		
		boolean isO2Consumer = user.isO2Consumer();
		
		assertTrue(isO2Consumer);
	}
	
	@Test
	public void isO2Consumer_non_o2_Success(){

		Community community = new Community();
		community.setRewriteUrlParameter("o2");
		
		UserGroup userGroup = new UserGroup();
		userGroup.setCommunity(community);
		
		User user = new User();
		user.setUserGroup(userGroup);
		user.setProvider("non_o2");
		user.setSegment("consumer");
		user.setContract("PAYG");
		
		boolean isO2Consumer = user.isO2Consumer();
		
		assertFalse(isO2Consumer);
	}
	
	@Test
	public void isO2Consumer_emptySegment_Success(){

		Community community = new Community();
		community.setRewriteUrlParameter("o2");
		
		UserGroup userGroup = new UserGroup();
		userGroup.setCommunity(community);
		
		User user = new User();
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment("");
		user.setContract("PAYG");
		
		boolean isO2Consumer = user.isO2Consumer();
		
		assertFalse(isO2Consumer);
	}
	
	@Test
	public void isO2Consumer_chartsnow_Success(){

		Community community = new Community();
		community.setRewriteUrlParameter("chartsnow");
		
		UserGroup userGroup = new UserGroup();
		userGroup.setCommunity(community);
		
		User user = new User();
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment("consumer");
		user.setContract("PAYG");
		
		boolean isO2Consumer = user.isO2Consumer();
		
		assertFalse(isO2Consumer);
	}
	
	@Test
	public void isO2Consumer_PAYM_Success(){

		Community community = new Community();
		community.setRewriteUrlParameter("o2");
		
		UserGroup userGroup = new UserGroup();
		userGroup.setCommunity(community);
		
		User user = new User();
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment("consumer");
		user.setContract("PAYM");
		
		boolean isO2Consumer = user.isO2Consumer();
		
		assertFalse(isO2Consumer);
	}
	
	@Test()
	public void isO2Consumer_RewriteUrlParameterIsNull_Success(){

		Community community = new Community();
		community.setRewriteUrlParameter(null);
		
		UserGroup userGroup = new UserGroup();
		userGroup.setCommunity(community);
		
		User user = new User();
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment("consumer");
		user.setContract("PAYM");
		
		boolean isO2Consumer = user.isO2Consumer();
		
		assertFalse(isO2Consumer);
	}
	
	@Test(expected=NullPointerException.class)
	public void isO2Consumer_UserGroupIsNull_Failure(){
		
		User user = new User();
		user.setUserGroup(null);
		user.setProvider("o2");
		user.setSegment("consumer");
		user.setContract("PAYM");
		
		user.isO2Consumer();
	}
}