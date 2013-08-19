/**
 * 
 */
package mobi.nowtechnologies.server.persistence.domain;

import junit.framework.Assert;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static mobi.nowtechnologies.server.persistence.domain.enums.SegmentType.*;
import static mobi.nowtechnologies.server.shared.enums.Contract.*;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.INDIRECT;
import static mobi.nowtechnologies.server.shared.enums.MediaType.*;
import static mobi.nowtechnologies.server.shared.enums.Tariff.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Utils.class)
public class UserTest {

    User user = new User().withTariff(_4G).withSegment(CONSUMER);
    PaymentPolicy detachedPaymentPolicy;
    private PaymentPolicy paymentPolicy;
    private Tariff paymentPolicyTariff;
    private MediaType mediaType;
    private int nextSubPayment;
    private int epochSeconds;

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
		user.setSegment(CONSUMER);
		user.setContract(PAYG);
		
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
		user.setSegment(CONSUMER);
		user.setContract(PAYG);
		
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
		user.setContract(PAYG);
		
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
		user.setSegment(CONSUMER);
		user.setContract(PAYG);
		
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
		user.setSegment(CONSUMER);
		user.setContract(PAYM);
		
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
		user.setSegment(CONSUMER);
		user.setContract(PAYG);
		
		boolean isO2PAYGConsumer = user.isO2PAYGConsumer();
		
		assertFalse(isO2PAYGConsumer);
	}
	
	@Test(expected=NullPointerException.class)
	public void isO2PAYGConsumer_UserGroupIsNull_Failure(){
		
		User user = new User();
		user.setUserGroup(null);
		user.setProvider("o2");
		user.setSegment(CONSUMER);
		user.setContract(PAYG);
		
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
		user.setSegment(CONSUMER);
		
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
		user.setSegment(CONSUMER);
		
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
		user.setSegment(CONSUMER);
		user.setContract(PAYG);
		
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
		user.setSegment(CONSUMER);
		
		boolean isO2Consumer = user.isO2Consumer();
		
		assertFalse(isO2Consumer);
	}
	
	@Test(expected=NullPointerException.class)
	public void isO2Consumer_UserGroupIsNull_Failure(){
		
		User user = new User();
		user.setUserGroup(null);
		user.setProvider("o2");
		user.setSegment(CONSUMER);
		
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
		user.setSegment(CONSUMER);
		
		boolean result = user.isInvalidPaymentPolicy();
		
		assertEquals(true, result);
	}
	
	@Test
	public void testIsInvalidPaymentPolicy_O2ProviderNotSameSegment_Success(){
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		paymentPolicy.setProvider("o2");
		paymentPolicy.setSegment(BUSINESS);
		
		O2PSMSPaymentDetails o2psmsPaymentDetails = new O2PSMSPaymentDetails();
		o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);
		
		User user = UserFactory.createUser();
		user.setProvider("o2");
		user.setCurrentPaymentDetails(o2psmsPaymentDetails);
		user.setSegment(CONSUMER);
		
		boolean result = user.isInvalidPaymentPolicy();
		
		assertEquals(true, result);
	}
	
	@Test
	public void testIsInvalidPaymentPolicy_NonO2ProviderNotSameSegment_Success(){
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		paymentPolicy.setProvider("non-o2");
		paymentPolicy.setSegment(BUSINESS);
		
		O2PSMSPaymentDetails o2psmsPaymentDetails = new O2PSMSPaymentDetails();
		o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);
		
		User user = UserFactory.createUser();
		user.setProvider("non-o2");
		user.setCurrentPaymentDetails(o2psmsPaymentDetails);
		user.setSegment(CONSUMER);
		
		boolean result = user.isInvalidPaymentPolicy();
		
		assertEquals(false, result);
	}
	
	@Test
	public void testIsTariffChanged_tariffsAreTheSame_Success(){
			
		User user = UserFactory.createUser();
		user.setProvider("non-o2");
		user.setSegment(CONSUMER);
		
		boolean result = user.isInvalidPaymentPolicy();
		
		assertEquals(false, result);
	}

    private void createSubscribedUserWithTariffMigration(Tariff subscribedUserTariff, Tariff newUserTariff) {
        user = UserFactory.createUserWithVideoPaymentDetails(subscribedUserTariff);
        user.setTariff(newUserTariff);
    }

    @Test
     public void testIsOn4GVideoAudioBoughtPeriod_Success() throws Exception {
        paymentPolicyTariff = _4G;
        mediaType = VIDEO_AND_AUDIO;
        epochSeconds = 0;
        nextSubPayment = epochSeconds + 10;

        prepareDataToIsOn4GVideoAudioBoughtPeriod();

        boolean isOn4GVideoAudioBoughtPeriod = user.isOn4GVideoAudioBoughtPeriod();

        Assert.assertTrue(isOn4GVideoAudioBoughtPeriod);
    }

    @Test
    public void testIsOn4GVideoAudioBoughtPeriod_NextSubPaymentInThePast_Success() throws Exception {
        paymentPolicyTariff = _4G;
        mediaType = VIDEO_AND_AUDIO;
        epochSeconds = Integer.MAX_VALUE;
        nextSubPayment = epochSeconds - 10;

        prepareDataToIsOn4GVideoAudioBoughtPeriod();

        boolean isOn4GVideoAudioBoughtPeriod = user.isOn4GVideoAudioBoughtPeriod();

        Assert.assertFalse(isOn4GVideoAudioBoughtPeriod);
    }

    @Test
    public void testIsOn4GVideoAudioBoughtPeriod_WrongTariff_Success() throws Exception {
        paymentPolicyTariff = _3G;
        mediaType = VIDEO_AND_AUDIO;
        epochSeconds = 0;
        nextSubPayment = epochSeconds + 10;
        prepareDataToIsOn4GVideoAudioBoughtPeriod();

        boolean isOn4GVideoAudioBoughtPeriod = user.isOn4GVideoAudioBoughtPeriod();

        Assert.assertFalse(isOn4GVideoAudioBoughtPeriod);
    }

    @Test
    public void testIsOn4GVideoAudioBoughtPeriod_WrongContentCategory_Success() throws Exception {

        paymentPolicyTariff = _4G;
        mediaType = AUDIO;
        epochSeconds = 0;
        nextSubPayment = epochSeconds + 10;
        prepareDataToIsOn4GVideoAudioBoughtPeriod();

        boolean isOn4GVideoAudioBoughtPeriod = user.isOn4GVideoAudioBoughtPeriod();

        Assert.assertFalse(isOn4GVideoAudioBoughtPeriod);
    }

    @Test
    public void shouldNotShowFreeTrialFor4GO2PaymConsumerOnVideoAudioFreeTrial(){
        //given
        UserGroup o2 = new UserGroup().withCommunity(new Community().withRewriteUrl("o2"));
        PromoCode videoPromo = new PromoCode().withMediaType(VIDEO_AND_AUDIO);

        user = new User().withTariff(_4G).withSegment(CONSUMER).withContract(PAYM).withProvider("o2")
                .withUserGroup(o2)
                .withLastPromo(videoPromo).withFreeTrialExpiredMillis(Long.MAX_VALUE);

        //when
        boolean isShowPromotion = user.isShowFreeTrial();

        //then
        assertEquals(false, isShowPromotion);
    }

    @Test
    public void shouldNotShowFreeTrialFor4GO2PaymConsumerOnAudioFreeTrial(){
        //given
        user = new User().withTariff(_4G).withSegment(CONSUMER).withContract(PAYM).withProvider("o2").withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2"))).withLastPromo(new PromoCode().withMediaType(AUDIO)).withFreeTrialExpiredMillis(Long.MAX_VALUE);

        //when
        boolean isShowPromotion = user.isShowFreeTrial();

        //then
        assertEquals(true, isShowPromotion);
    }

    @Test
    public void shouldNotShowFreeTrialFor4GO2PaygConsumerOnVideoAudioFreeTrial(){
        //given
        user = new User().withTariff(_4G).withSegment(CONSUMER).withContract(PAYG).withProvider("o2").withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2"))).withLastPromo(new PromoCode().withMediaType(AUDIO)).withFreeTrialExpiredMillis(Long.MAX_VALUE);

        //when
        boolean isShowPromotion = user.isShowFreeTrial();

        //then
        assertEquals(true, isShowPromotion);
    }

    @Test
    public void shouldNotShowFreeTrialFor4GO2PaymBusinessOnVideoAudioFreeTrial(){
        //given
        user = new User().withTariff(_4G).withSegment(BUSINESS).withContract(PAYM).withProvider("o2").withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2"))).withLastPromo(new PromoCode().withMediaType(AUDIO)).withFreeTrialExpiredMillis(Long.MAX_VALUE);

        //when
        boolean isShowPromotion = user.isShowFreeTrial();

        //then
        assertEquals(true, isShowPromotion);
    }

    @Test
    public void shouldReturnCanPlayVideoTrueForUserOn4GVideoAudioFreeTrial(){
        //given
        user = new User().withTariff(_4G).withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO)).withFreeTrialExpiredMillis(Long.MAX_VALUE);

        //when
        boolean canPlayVideo = user.canPlayVideo();

        //then
        org.junit.Assert.assertEquals(true, canPlayVideo);
    }

    @Test
    public void shouldReturnCanPlayVideoTrueForUserOn4GVideoAudioBoughtPeriod() {
        //given
        user = new User().withTariff(_4G).withNextSubPayment(Integer.MAX_VALUE).withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withTariff(_4G).withMediaType(VIDEO_AND_AUDIO)));

        //when
        boolean canPlayVideo = user.canPlayVideo();

        //then
        org.junit.Assert.assertEquals(true, canPlayVideo);
    }

    @Test
    public void shouldReturnCanPlayVideoFalseForUserWith4GVideoAudioFreeTrialExpiredAndNotOnBoughtVideoAudioPeriod(){
        //given
        user = new User().withTariff(_4G).withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO)).withFreeTrialExpiredMillis(0L).withNextSubPayment(0).withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withTariff(_4G).withMediaType(VIDEO_AND_AUDIO)));

        //when
        boolean canPlayVideo = user.canPlayVideo();

        //then
        org.junit.Assert.assertEquals(false, canPlayVideo);
    }

    @Test
    public void shouldReturnCanPlayVideoFalseForUserOn3GAudioFreeTrial(){
        //given
        user = new User().withTariff(_3G).withLastPromo(new PromoCode().withMediaType(AUDIO)).withFreeTrialExpiredMillis(Long.MAX_VALUE);

        //when
        boolean canPlayVideo = user.canPlayVideo();

        //then
        org.junit.Assert.assertEquals(false, canPlayVideo);
    }

    @Test
    public void shouldReturnCanPlayVideoFalseForUserOn4GAudioFreeTrial(){
        //given
        user = new User().withTariff(_4G).withLastPromo(new PromoCode().withMediaType(AUDIO)).withFreeTrialExpiredMillis(Long.MAX_VALUE);

        //when
        boolean canPlayVideo = user.canPlayVideo();

        //then
        org.junit.Assert.assertEquals(false, canPlayVideo);
    }

    @Test
    public void shouldReturnCanPlayVideoFalseForUserO3GAudioBoughtPeriod() {
        //given
        user = new User().withTariff(_4G).withNextSubPayment(Integer.MAX_VALUE).withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withTariff(_3G).withMediaType(AUDIO)));

        //when
        boolean canPlayVideo = user.canPlayVideo();

        //then
        org.junit.Assert.assertEquals(false, canPlayVideo);
    }

    @Test
    public void shouldReturnCanPlayVideoFalseForUserO4GAudioBoughtPeriod() {
        //given
        user = new User().withTariff(_4G).withNextSubPayment(Integer.MAX_VALUE).withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withTariff(_4G).withMediaType(AUDIO)));

        //when
        boolean canPlayVideo = user.canPlayVideo();

        //then
        org.junit.Assert.assertEquals(false, canPlayVideo);
    }

    @Test
    public void shouldReturnCanPlayVideoFalseForUserOn4GVideoAudioSubscriptionWithNextSubPaymentInThePast() {
        //given
        user = new User().withTariff(_4G).withNextSubPayment(0).withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withTariff(_4G).withMediaType(VIDEO_AND_AUDIO)));

        //when
        boolean canPlayVideo = user.canPlayVideo();

        //then
        org.junit.Assert.assertEquals(false, canPlayVideo);
    }

    private void prepareDataToIsOn4GVideoAudioBoughtPeriod() {

        mockStatic(Utils.class);
        when(Utils.getEpochSeconds()).thenReturn(epochSeconds);

        paymentPolicy = PaymentPolicyFactory.createPaymentPolicy(paymentPolicyTariff);
        paymentPolicy.setMediaType(mediaType);

        O2PSMSPaymentDetails o2PSMSPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
        o2PSMSPaymentDetails.setPaymentPolicy(paymentPolicy);

        user = UserFactory.createUser();
        user.setLastSuccessfulPaymentDetails(o2PSMSPaymentDetails);
        user.setNextSubPayment(nextSubPayment);
    }
}
