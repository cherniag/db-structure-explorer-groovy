/**
 * 
 */
package mobi.nowtechnologies.server.persistence.domain;

import junit.framework.Assert;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserTest {

    User user;
    PaymentPolicy detachedPaymentPolicy;
	
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
			user.setFreeTrialExpiredMillis((Long)null);
		assertEquals(false, user.isOnFreeTrial());
	}
	
	@Test
	public void isO2PAYGConsumer_Success(){

		Community community = new Community();
		community.setRewriteUrlParameter("o2");
		
		UserGroup userGroup = new UserGroup();
		userGroup.setCommunity(community);
		
		User user = new User();
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(SegmentType.CONSUMER);
		user.setContract(Contract.PAYG);
		
		boolean isO2PAYGConsumer = user.isO2PAYGConsumer();
		
		assertTrue(isO2PAYGConsumer);
	}
	
	@Test
	public void isO2PAYGConsumer_non_o2_Success(){

		Community community = new Community();
		community.setRewriteUrlParameter("o2");
		
		UserGroup userGroup = new UserGroup();
		userGroup.setCommunity(community);
		
		User user = new User();
		user.setUserGroup(userGroup);
		user.setProvider("non_o2");
		user.setSegment(SegmentType.CONSUMER);
		user.setContract(Contract.PAYG);
		
		boolean isO2PAYGConsumer = user.isO2PAYGConsumer();
		
		assertFalse(isO2PAYGConsumer);
	}
	
	@Test
	public void isO2PAYGConsumer_emptySegment_Success(){

		Community community = new Community();
		community.setRewriteUrlParameter("o2");
		
		UserGroup userGroup = new UserGroup();
		userGroup.setCommunity(community);
		
		User user = new User();
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(null);
		user.setContract(Contract.PAYG);
		
		boolean isO2PAYGConsumer = user.isO2PAYGConsumer();
		
		assertFalse(isO2PAYGConsumer);
	}
	
	@Test
	public void isO2PAYGConsumer_chartsnow_Success(){

		Community community = new Community();
		community.setRewriteUrlParameter("chartsnow");
		
		UserGroup userGroup = new UserGroup();
		userGroup.setCommunity(community);
		
		User user = new User();
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(SegmentType.CONSUMER);
		user.setContract(Contract.PAYG);
		
		boolean isO2PAYGConsumer = user.isO2PAYGConsumer();
		
		assertFalse(isO2PAYGConsumer);
	}
	
	@Test
	public void isO2PAYGConsumer_PAYM_Success(){

		Community community = new Community();
		community.setRewriteUrlParameter("o2");
		
		UserGroup userGroup = new UserGroup();
		userGroup.setCommunity(community);
		
		User user = new User();
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(SegmentType.CONSUMER);
		user.setContract(Contract.PAYM);
		
		boolean isO2PAYGConsumer = user.isO2PAYGConsumer();
		
		assertFalse(isO2PAYGConsumer);
	}
	
	@Test()
	public void isO2PAYGConsumer_RewriteUrlParameterIsNull_Success(){

		Community community = new Community();
		community.setRewriteUrlParameter(null);
		
		UserGroup userGroup = new UserGroup();
		userGroup.setCommunity(community);
		
		User user = new User();
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(SegmentType.CONSUMER);
		user.setContract(Contract.PAYG);
		
		boolean isO2PAYGConsumer = user.isO2PAYGConsumer();
		
		assertFalse(isO2PAYGConsumer);
	}
	
	@Test(expected=NullPointerException.class)
	public void isO2PAYGConsumer_UserGroupIsNull_Failure(){
		
		User user = new User();
		user.setUserGroup(null);
		user.setProvider("o2");
		user.setSegment(SegmentType.CONSUMER);
		user.setContract(Contract.PAYG);
		
		user.isO2PAYGConsumer();
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
		user.setSegment(SegmentType.CONSUMER);
		
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
		user.setSegment(SegmentType.CONSUMER);
		
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
		user.setSegment(null);
		
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
		user.setSegment(SegmentType.CONSUMER);
		user.setContract(Contract.PAYG);
		
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
		user.setSegment(SegmentType.CONSUMER);
		
		boolean isO2Consumer = user.isO2Consumer();
		
		assertFalse(isO2Consumer);
	}
	
	@Test(expected=NullPointerException.class)
	public void isO2Consumer_UserGroupIsNull_Failure(){
		
		User user = new User();
		user.setUserGroup(null);
		user.setProvider("o2");
		user.setSegment(SegmentType.CONSUMER);
		
		user.isO2Consumer();
	}
	
	@Test
	public void testIsInvalidPaymentPolicy_NotSameProvider_Success(){
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		paymentPolicy.setProvider("non-o2");

		O2PSMSPaymentDetails o2psmsPaymentDetails = new O2PSMSPaymentDetails();
		o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);
		
		User user = UserFactory.createUser();
		user.setProvider("o2");
		user.setCurrentPaymentDetails(o2psmsPaymentDetails);
		user.setSegment(SegmentType.CONSUMER);
		
		boolean result = user.isInvalidPaymentPolicy();
		
		assertEquals(true, result);
	}
	
	@Test
	public void testIsInvalidPaymentPolicy_O2ProviderNotSameSegment_Success(){
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		paymentPolicy.setProvider("o2");
		paymentPolicy.setSegment(SegmentType.BUSINESS);
		
		O2PSMSPaymentDetails o2psmsPaymentDetails = new O2PSMSPaymentDetails();
		o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);
		
		User user = UserFactory.createUser();
		user.setProvider("o2");
		user.setCurrentPaymentDetails(o2psmsPaymentDetails);
		user.setSegment(SegmentType.CONSUMER);
		
		boolean result = user.isInvalidPaymentPolicy();
		
		assertEquals(true, result);
	}
	
	@Test
	public void testIsInvalidPaymentPolicy_NonO2ProviderNotSameSegment_Success(){
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		paymentPolicy.setProvider("non-o2");
		paymentPolicy.setSegment(SegmentType.BUSINESS);
		
		O2PSMSPaymentDetails o2psmsPaymentDetails = new O2PSMSPaymentDetails();
		o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);
		
		User user = UserFactory.createUser();
		user.setProvider("non-o2");
		user.setCurrentPaymentDetails(o2psmsPaymentDetails);
		user.setSegment(SegmentType.CONSUMER);
		
		boolean result = user.isInvalidPaymentPolicy();
		
		assertEquals(false, result);
	}
	
	@Test
	public void testIsInvalidPaymentPolicy_NullCurrentPaymentDetails_Success(){
			
		User user = UserFactory.createUser();
		user.setProvider("non-o2");
		user.setSegment(SegmentType.CONSUMER);
		
		boolean result = user.isInvalidPaymentPolicy();
		
		assertEquals(false, result);
	}

    public void testAreTariffsEqual_UserTariffIsNullAndDetachedPaymentPolicyTariffIs3G_Success(){
        createFreeTrialUserAndDetachedPaymentPolicy(Tariff._3G);

        boolean result = user.areTariffsEqual(detachedPaymentPolicy);

        Assert.assertTrue(result);
    }

    private void createFreeTrialUserAndDetachedPaymentPolicy(Tariff detachedPaymentPolicyTariff) {
        user = UserFactory.createUser();
        createDetachedPaymentPolicy(detachedPaymentPolicyTariff);
    }


    public void testAreTariffsEqual_UserTariffIs3GAndDetachedPaymentPolicyTariffIs3G_Success(){
        createSubscribedUserAndDetachedPaymentPolicy(Tariff._3G, Tariff._3G);

        boolean result = user.areTariffsEqual(detachedPaymentPolicy);

        Assert.assertTrue(result);
    }

    public void testAreTariffsEqual_UserTariffIs3GAndDetachedPaymentPolicyTariffIs4G_Success(){
        createSubscribedUserAndDetachedPaymentPolicy(Tariff._3G, Tariff._3G);

        boolean result = user.areTariffsEqual(detachedPaymentPolicy);

        Assert.assertFalse(result);
    }

    private void createSubscribedUserAndDetachedPaymentPolicy(Tariff subscribedUserTariff, Tariff detachedPaymentPolicyTariff) {
        user = UserFactory.createUserWithPaymentDetails(subscribedUserTariff);
        createDetachedPaymentPolicy(detachedPaymentPolicyTariff);
    }

    private void createDetachedPaymentPolicy(Tariff detachedPaymentPolicyTariff) {
        detachedPaymentPolicy = PaymentPolicyFactory.createPaymentPolicy(detachedPaymentPolicyTariff);
    }
}
