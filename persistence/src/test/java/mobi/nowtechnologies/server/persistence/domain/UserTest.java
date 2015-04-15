/**
 *
 */

package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import static mobi.nowtechnologies.server.persistence.domain.Community.O2_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.persistence.domain.Community.VF_NZ_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.EMAIL;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.FACEBOOK;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_O2;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_VF;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.VF;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.BUSINESS;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static mobi.nowtechnologies.server.shared.enums.Tariff._4G;

import org.junit.*;
import org.junit.runner.*;
import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.is;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Utils.class)
public class UserTest {

    User user = new User().withTariff(_4G).withSegment(CONSUMER);
    private PaymentPolicy paymentPolicy;
    private Tariff paymentPolicyTariff;
    private MediaType mediaType;
    private int nextSubPayment;
    private int epochSeconds;

    @Test
    public void isOnFreeTrial_true_when_freeTrialExpiredMillis_Gt_currentMillis() {
        User user = new User();
        user.setFreeTrialExpiredMillis(System.currentTimeMillis() + 200000L);
        assertEquals(true, user.isOnFreeTrial());
    }

    @Test
    public void isOnFreeTrial_false_when_freeTrialExpiredMillis_Lt_currentMillis() {
        User user = new User();
        user.setFreeTrialExpiredMillis(System.currentTimeMillis());
        assertEquals(false, user.isOnFreeTrial());
    }

    @Test
    public void isOnFreeTrial_false_when_freeTrialExpiredMillis_Eq_Null() {
        User user = new User();
        user.setFreeTrialExpiredMillis((Long) null);
        assertEquals(false, user.isOnFreeTrial());
    }

    @Test
    public void isO2PAYGConsumer_Success() {

        Community community = new Community();
        community.setRewriteUrlParameter("o2");

        UserGroup userGroup = new UserGroup().withId(1);
        userGroup.setCommunity(community);

        User user = new User();
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSegment(CONSUMER);
        user.setContract(PAYG);

        boolean isO2PAYGConsumer = user.isO2PAYGConsumer();

        assertTrue(isO2PAYGConsumer);
    }

    @Test
    public void isO2PAYGConsumer_non_o2_Success() {

        Community community = new Community();
        community.setRewriteUrlParameter("o2");

        UserGroup userGroup = new UserGroup().withId(1);
        userGroup.setCommunity(community);

        User user = new User();
        user.setUserGroup(userGroup);
        user.setProvider(NON_O2);
        user.setSegment(CONSUMER);
        user.setContract(PAYG);

        boolean isO2PAYGConsumer = user.isO2PAYGConsumer();

        assertFalse(isO2PAYGConsumer);
    }

    @Test
    public void isO2PAYGConsumer_emptySegment_Success() {

        Community community = new Community();
        community.setRewriteUrlParameter("o2");

        UserGroup userGroup = new UserGroup().withId(1);
        userGroup.setCommunity(community);

        User user = new User();
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSegment(null);
        user.setContract(PAYG);

        boolean isO2PAYGConsumer = user.isO2PAYGConsumer();

        assertFalse(isO2PAYGConsumer);
    }

    @Test
    public void isO2PAYGConsumer_chartsnow_Success() {

        Community community = new Community();
        community.setRewriteUrlParameter("chartsnow");

        UserGroup userGroup = new UserGroup().withId(1);
        userGroup.setCommunity(community);

        User user = new User();
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSegment(CONSUMER);
        user.setContract(PAYG);

        boolean isO2PAYGConsumer = user.isO2PAYGConsumer();

        assertFalse(isO2PAYGConsumer);
    }

    @Test
    public void isO2PAYGConsumer_PAYM_Success() {

        Community community = new Community();
        community.setRewriteUrlParameter("o2");

        UserGroup userGroup = new UserGroup().withId(1);
        userGroup.setCommunity(community);

        User user = new User();
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSegment(CONSUMER);
        user.setContract(PAYM);

        boolean isO2PAYGConsumer = user.isO2PAYGConsumer();

        assertFalse(isO2PAYGConsumer);
    }

    @Test()
    public void isO2PAYGConsumer_RewriteUrlParameterIsNull_Success() {

        Community community = new Community();
        community.setRewriteUrlParameter(null);

        UserGroup userGroup = new UserGroup().withId(1);
        userGroup.setCommunity(community);

        User user = new User();
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSegment(CONSUMER);
        user.setContract(PAYG);

        boolean isO2PAYGConsumer = user.isO2PAYGConsumer();

        assertFalse(isO2PAYGConsumer);
    }

    @Test(expected = NullPointerException.class)
    public void isO2PAYGConsumer_UserGroupIsNull_Failure() {

        User user = new User();
        user.setUserGroup(null);
        user.setProvider(O2);
        user.setSegment(CONSUMER);
        user.setContract(PAYG);

        user.isO2PAYGConsumer();
    }

    @Test
    public void isO2Consumer_Success() {

        Community community = new Community();
        community.setRewriteUrlParameter("o2");

        UserGroup userGroup = new UserGroup().withId(1);
        userGroup.setCommunity(community);

        User user = new User();
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSegment(CONSUMER);

        boolean isO2Consumer = user.isO2Consumer();

        assertTrue(isO2Consumer);
    }

    @Test
    public void isO2Consumer_non_o2_Success() {

        Community community = new Community();
        community.setRewriteUrlParameter("o2");

        UserGroup userGroup = new UserGroup().withId(1);
        userGroup.setCommunity(community);

        User user = new User();
        user.setUserGroup(userGroup);
        user.setProvider(NON_O2);
        user.setSegment(CONSUMER);

        boolean isO2Consumer = user.isO2Consumer();

        assertFalse(isO2Consumer);
    }

    @Test
    public void isO2Consumer_emptySegment_Success() {

        Community community = new Community();
        community.setRewriteUrlParameter("o2");

        UserGroup userGroup = new UserGroup().withId(1);
        userGroup.setCommunity(community);

        User user = new User();
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSegment(null);

        boolean isO2Consumer = user.isO2Consumer();

        assertFalse(isO2Consumer);
    }

    @Test
    public void isO2Consumer_chartsnow_Success() {

        Community community = new Community();
        community.setRewriteUrlParameter("chartsnow");

        UserGroup userGroup = new UserGroup().withId(1);
        userGroup.setCommunity(community);

        User user = new User();
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSegment(CONSUMER);
        user.setContract(PAYG);

        boolean isO2Consumer = user.isO2Consumer();

        assertFalse(isO2Consumer);
    }

    @Test()
    public void isO2Consumer_RewriteUrlParameterIsNull_Success() {

        Community community = new Community();
        community.setRewriteUrlParameter(null);

        UserGroup userGroup = new UserGroup().withId(1);
        userGroup.setCommunity(community);

        User user = new User();
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSegment(CONSUMER);

        boolean isO2Consumer = user.isO2Consumer();

        assertFalse(isO2Consumer);
    }

    @Test(expected = NullPointerException.class)
    public void isO2Consumer_UserGroupIsNull_Failure() {

        User user = new User();
        user.setUserGroup(null);
        user.setProvider(O2);
        user.setSegment(CONSUMER);

        user.isO2Consumer();
    }

    @Test
    public void testIsInvalidPaymentPolicy_NotSameProvider_Success() {
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setProvider(NON_O2);

        O2PSMSPaymentDetails o2psmsPaymentDetails = new O2PSMSPaymentDetails();
        o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setProvider(O2);
        user.setCurrentPaymentDetails(o2psmsPaymentDetails);
        user.setSegment(CONSUMER);

        boolean result = user.isInvalidPaymentPolicy();

        assertEquals(true, result);
    }

    @Test
    public void testIsInvalidPaymentPolicy_O2ProviderNotSameSegment_Success() {
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setProvider(NON_O2);
        paymentPolicy.setSegment(BUSINESS);

        O2PSMSPaymentDetails o2psmsPaymentDetails = new O2PSMSPaymentDetails();
        o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setProvider(O2);
        user.setCurrentPaymentDetails(o2psmsPaymentDetails);
        user.setSegment(CONSUMER);

        boolean result = user.isInvalidPaymentPolicy();

        assertEquals(true, result);
    }

    @Test
    public void testIsInvalidPaymentPolicy_NonO2ProviderNotSameSegment_Success() {
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setProvider(NON_O2);
        paymentPolicy.setSegment(BUSINESS);

        O2PSMSPaymentDetails o2psmsPaymentDetails = new O2PSMSPaymentDetails();
        o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setProvider(NON_O2);
        user.setCurrentPaymentDetails(o2psmsPaymentDetails);
        user.setSegment(CONSUMER);

        boolean result = user.isInvalidPaymentPolicy();

        assertEquals(false, result);
    }

    @Test
    public void testIsTariffChanged_tariffsAreTheSame_Success() {

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setProvider(NON_O2);
        user.setSegment(CONSUMER);

        boolean result = user.isInvalidPaymentPolicy();

        assertEquals(true, result);
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
    public void shouldReturnCanPlayVideoTrueForUserOn4GVideoAudioFreeTrial() {
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
        user = new User().withTariff(_4G)
                         .withNextSubPayment(Integer.MAX_VALUE)
                         .withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withTariff(_4G).withMediaType(VIDEO_AND_AUDIO)));

        //when
        boolean canPlayVideo = user.canPlayVideo();

        //then
        org.junit.Assert.assertEquals(true, canPlayVideo);
    }

    @Test
    public void shouldReturnCanPlayVideoFalseForUserWith4GVideoAudioFreeTrialExpiredAndNotOnBoughtVideoAudioPeriod() {
        //given
        user = new User().withTariff(_4G)
                         .withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO))
                         .withFreeTrialExpiredMillis(0L)
                         .withNextSubPayment(0)
                         .withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withTariff(_4G).withMediaType(VIDEO_AND_AUDIO)));

        //when
        boolean canPlayVideo = user.canPlayVideo();

        //then
        org.junit.Assert.assertEquals(false, canPlayVideo);
    }

    @Test
    public void shouldReturnCanPlayVideoFalseForUserOn3GAudioFreeTrial() {
        //given
        user = new User().withTariff(_3G).withLastPromo(new PromoCode().withMediaType(AUDIO)).withFreeTrialExpiredMillis(Long.MAX_VALUE);

        //when
        boolean canPlayVideo = user.canPlayVideo();

        //then
        org.junit.Assert.assertEquals(false, canPlayVideo);
    }

    @Test
    public void shouldReturnCanPlayVideoFalseForUserOn4GAudioFreeTrial() {
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
        user = new User().withTariff(_4G)
                         .withNextSubPayment(Integer.MAX_VALUE)
                         .withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withTariff(_3G).withMediaType(AUDIO)));

        //when
        boolean canPlayVideo = user.canPlayVideo();

        //then
        org.junit.Assert.assertEquals(false, canPlayVideo);
    }

    @Test
    public void shouldReturnCanPlayVideoFalseForUserO4GAudioBoughtPeriod() {
        //given
        user = new User().withTariff(_4G)
                         .withNextSubPayment(Integer.MAX_VALUE)
                         .withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withTariff(_4G).withMediaType(AUDIO)));

        //when
        boolean canPlayVideo = user.canPlayVideo();

        //then
        org.junit.Assert.assertEquals(false, canPlayVideo);
    }

    @Test
    public void shouldReturnCanPlayVideoFalseForUserOn4GVideoAudioSubscriptionWithNextSubPaymentInThePast() {
        //given
        user = new User().withTariff(_4G)
                         .withNextSubPayment(0)
                         .withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withTariff(_4G).withMediaType(VIDEO_AND_AUDIO)));

        //when
        boolean canPlayVideo = user.canPlayVideo();

        //then
        org.junit.Assert.assertEquals(false, canPlayVideo);
    }

    @Test
    public void testIsNonO2User_nonO2User_Success() throws Exception {
        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(NON_O2);

        assertTrue(user.isNonO2User());
    }

    @Test
    public void testIsNonO2User_O2User_Success() throws Exception {
        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(O2);

        assertFalse(user.isNonO2User());
    }

    @Test
    public void testINonO2User_UserFromNotO2Community_Success() throws Exception {
        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        community.setRewriteUrlParameter("r");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(null);

        assertTrue(user.isNonO2User());
    }

    @Test(expected = NullPointerException.class)
    public void testIsNonO2User_CommunityIsNull_Failure() throws Exception {
        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = null;

        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);

        user.isNonO2User();
    }

    @Test
    public void shouldBeOnWhiteListedVideoAudioFreeTrial() {
        //given
        user = new User().withFreeTrialExpiredMillis(Long.MAX_VALUE).withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO).withPromotion(new Promotion().withIsWhiteListed(true)));

        //when
        boolean isOnWhiteListedVideoAudioFreeTrial = user.isOnWhiteListedVideoAudioFreeTrial();

        //then
        Assert.assertTrue(isOnWhiteListedVideoAudioFreeTrial);
    }

    @Test
    public void shouldNotBeOnWhiteListedVideoAudioFreeTrialWhenItIsOnAudio() {
        //given
        user = new User().withFreeTrialExpiredMillis(Long.MAX_VALUE).withLastPromo(new PromoCode().withMediaType(AUDIO).withPromotion(new Promotion().withIsWhiteListed(true)));

        //when
        boolean isOnWhiteListedVideoAudioFreeTrial = user.isOnWhiteListedVideoAudioFreeTrial();

        //then
        Assert.assertFalse(isOnWhiteListedVideoAudioFreeTrial);
    }

    @Test
    public void shouldNotBeOnWhiteListedVideoAudioFreeTrialWhenItIslExpired() {
        //given
        user = new User().withFreeTrialExpiredMillis(Long.MIN_VALUE).withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO).withPromotion(new Promotion().withIsWhiteListed(true)));

        //when
        boolean isOnWhiteListedVideoAudioFreeTrial = user.isOnWhiteListedVideoAudioFreeTrial();

        //then
        Assert.assertFalse(isOnWhiteListedVideoAudioFreeTrial);
    }

    @Test
    public void shouldNotBeOnWhiteListedVideoAudioFreeTrial() {
        //given
        user = new User().withFreeTrialExpiredMillis(Long.MAX_VALUE).withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO).withPromotion(new Promotion().withIsWhiteListed(false)));

        //when
        boolean isOnWhiteListedVideoAudioFreeTrial = user.isOnWhiteListedVideoAudioFreeTrial();

        //then
        Assert.assertFalse(isOnWhiteListedVideoAudioFreeTrial);
    }

    @Test
    public void shouldBeEligibleForVideoO24GConsumer() {
        //given
        user = new User().withUserGroup(new UserGroup().withId(1).withCommunity(new Community().withRewriteUrl("o2"))).withTariff(_4G).withSegment(CONSUMER).withProvider(O2);

        //when
        boolean isEligibleForVideo = user.isEligibleForVideo();

        //then
        assertEquals(true, isEligibleForVideo);
    }

    @Test
    public void shouldNotBeEligibleForVideoO24GConsumerFromWrongCommunity() {
        //given
        user = new User().withUserGroup(new UserGroup().withId(1).withCommunity(new Community().withRewriteUrl("non-o2"))).withTariff(_4G).withSegment(CONSUMER).withProvider(O2);

        //when
        boolean isEligibleForVideo = user.isEligibleForVideo();

        //then
        assertEquals(false, isEligibleForVideo);
    }

    @Test
    public void shouldNotBeEligibleForVideoNonO24GConsumer() {
        //given
        user = new User().withUserGroup(new UserGroup().withId(1).withCommunity(new Community().withRewriteUrl("o2"))).withTariff(_4G).withSegment(CONSUMER).withProvider(NON_O2);

        //when
        boolean isEligibleForVideo = user.isEligibleForVideo();

        //then
        assertEquals(false, isEligibleForVideo);
    }

    @Test
    public void shouldNotBeEligibleForVideoO23GConsumer() {
        //given
        user = new User().withUserGroup(new UserGroup().withId(1).withCommunity(new Community().withRewriteUrl("o2"))).withTariff(_3G).withSegment(CONSUMER).withProvider(O2);

        //when
        boolean isEligibleForVideo = user.isEligibleForVideo();

        //then
        assertEquals(false, isEligibleForVideo);
    }

    @Test
    public void shouldNotBeEligibleForVideoO24GBusiness() {
        //given
        user = new User().withUserGroup(new UserGroup().withId(1).withCommunity(new Community().withRewriteUrl("o2"))).withTariff(_4G).withSegment(BUSINESS).withProvider(O2);

        //when
        boolean isEligibleForVideo = user.isEligibleForVideo();

        //then
        assertEquals(false, isEligibleForVideo);
    }

    @Test
    public void shouldBeEligibleForVideoUserOnWhiteListedVideoAudioFreeTrial() {
        //given
        user = new User().withUserGroup(new UserGroup().withId(1).withCommunity(new Community().withRewriteUrl("o2")))
                         .withFreeTrialExpiredMillis(Long.MAX_VALUE)
                         .withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO).withPromotion(new Promotion().withIsWhiteListed(true)));

        //when
        boolean isEligibleForVideo = user.isEligibleForVideo();

        //then
        assertEquals(true, isEligibleForVideo);
    }

    @Test
    public void shouldNotBeEligibleForVideoUserOnExpiredWhiteListedVideoAudioFreeTrial() {
        //given
        user = new User().withUserGroup(new UserGroup().withId(1).withCommunity(new Community().withRewriteUrl("o2")))
                         .withFreeTrialExpiredMillis(Long.MIN_VALUE)
                         .withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO).withPromotion(new Promotion().withIsWhiteListed(true)));

        //when
        boolean isEligibleForVideo = user.isEligibleForVideo();

        //then
        assertEquals(false, isEligibleForVideo);
    }

    @Test
    public void shouldNotBeEligibleForVideoUserOnNotWhiteListedVideoAudioFreeTrial() {
        //given
        user = new User().withUserGroup(new UserGroup().withId(1).withCommunity(new Community().withRewriteUrl("o2")))
                         .withFreeTrialExpiredMillis(Long.MAX_VALUE)
                         .withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO).withPromotion(new Promotion().withIsWhiteListed(false)));

        //when
        boolean isEligibleForVideo = user.isEligibleForVideo();

        //then
        assertEquals(false, isEligibleForVideo);
    }

    @Test
    public void shouldNotBeEligibleForVideoUserOnNotWhiteListedFreeTrial() {
        //given
        user = new User().withUserGroup(new UserGroup().withId(1).withCommunity(new Community().withRewriteUrl("o2"))).withFreeTrialExpiredMillis(Long.MAX_VALUE);

        //when
        boolean isEligibleForVideo = user.isEligibleForVideo();

        //then
        assertEquals(false, isEligibleForVideo);
    }

    @Test
    public void shouldNotBeEligibleForVideoUserOnWhiteListedAudioFreeTrial() {
        //given
        user = new User().withUserGroup(new UserGroup().withId(1).withCommunity(new Community().withRewriteUrl("o2")))
                         .withFreeTrialExpiredMillis(Long.MAX_VALUE)
                         .withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO).withPromotion(new Promotion().withIsWhiteListed(false)));

        //when
        boolean isEligibleForVideo = user.isEligibleForVideo();

        //then
        assertEquals(false, isEligibleForVideo);
    }

    @Test
    public void shouldBeFNZCommunityUser() {
        //given
        User user = new User().withUserGroup(new UserGroup().withId(1).withCommunity(new Community().withRewriteUrl(VF_NZ_COMMUNITY_REWRITE_URL)));

        //when
        boolean isVFNZCommunityUser = user.isVFNZCommunityUser();

        //then
        assertThat(isVFNZCommunityUser, is(true));
    }

    @Test
    public void shouldBeO2CommunityUser() {
        //given
        User user = new User().withUserGroup(new UserGroup().withId(1).withCommunity(new Community().withRewriteUrl(O2_COMMUNITY_REWRITE_URL)));

        //when
        boolean isVFNZCommunityUser = user.isO2CommunityUser();

        //then
        assertThat(isVFNZCommunityUser, is(true));
    }

    @Test
    public void shouldNotBeFNZCommunityUser() {
        //given
        User user = new User().withUserGroup(new UserGroup().withId(1).withCommunity(new Community().withRewriteUrl(O2_COMMUNITY_REWRITE_URL)));

        //when
        boolean isVFNZCommunityUser = user.isVFNZCommunityUser();

        //then
        assertThat(isVFNZCommunityUser, is(false));
    }

    @Test
    public void shouldBeVFNZUser() {
        //given
        User user = new User().withUserGroup(new UserGroup().withId(1).withCommunity(new Community().withRewriteUrl(VF_NZ_COMMUNITY_REWRITE_URL))).withProvider(VF);

        //when
        boolean isVFNZUser = user.isVFNZUser();

        //then
        assertThat(isVFNZUser, is(true));
    }

    @Test
    public void shouldBeNotVFNZUser() {
        //given
        User user = new User().withUserGroup(new UserGroup().withId(1).withCommunity(new Community().withRewriteUrl(VF_NZ_COMMUNITY_REWRITE_URL))).withProvider(NON_VF);

        //when
        boolean isVFNZUser = user.isVFNZUser();

        //then
        assertThat(isVFNZUser, is(false));
    }

    @Test
    public void shouldBeNotVFNZUserOnO2Community() {
        //given
        User user = new User().withUserGroup(new UserGroup().withId(1).withCommunity(new Community().withRewriteUrl(O2_COMMUNITY_REWRITE_URL))).withProvider(VF);

        //when
        boolean isVFNZUser = user.isVFNZUser();

        //then
        assertThat(isVFNZUser, is(false));
    }

    @Test
    public void shouldBeSubjectToAutoOptInO23GConsumerWithoutPromo() {
        //given
        User user = new User().withAutoOptInEnabled(true)
                              .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(O2_COMMUNITY_REWRITE_URL)))
                              .withTariff(_3G)
                              .withProvider(O2)
                              .withSegment(CONSUMER);

        //when
        boolean s = user.isSubjectToAutoOptIn();

        //then
        assertThat(s, is(true));
    }

    @Test
    public void shouldBeSubjectToAutoOptInO23GConsumerWithVideoAudioLastPromo() {
        //given
        User user = new User().withAutoOptInEnabled(true)
                              .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(O2_COMMUNITY_REWRITE_URL)))
                              .withTariff(_3G)
                              .withProvider(O2)
                              .withSegment(CONSUMER)
                              .withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO));

        //when
        boolean s = user.isSubjectToAutoOptIn();

        //then
        assertThat(s, is(true));
    }

    @Test
    public void shouldNotBeSubjectToAutoOptInNotO2Community() {
        //given
        User user = new User().withAutoOptInEnabled(true)
                              .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("")))
                              .withTariff(_3G)
                              .withProvider(O2)
                              .withSegment(BUSINESS)
                              .withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO));

        //when
        boolean s = user.isSubjectToAutoOptIn();

        //then
        assertThat(s, is(false));
    }

    @Test
    public void shouldNotBeSubjectToAutoOptInBusinessSegmentUser() {
        //given
        User user = new User().withAutoOptInEnabled(true)
                              .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(O2_COMMUNITY_REWRITE_URL)))
                              .withTariff(_3G)
                              .withProvider(O2)
                              .withSegment(BUSINESS)
                              .withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO));

        //when
        boolean s = user.isSubjectToAutoOptIn();

        //then
        assertThat(s, is(false));
    }

    @Test
    public void shouldNotBeSubjectToAutoOptInNonO2User() {
        //given
        User user = new User().withAutoOptInEnabled(true)
                              .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(O2_COMMUNITY_REWRITE_URL)))
                              .withTariff(_3G)
                              .withProvider(NON_O2)
                              .withSegment(CONSUMER)
                              .withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO));

        //when
        boolean s = user.isSubjectToAutoOptIn();

        //then
        assertThat(s, is(false));
    }

    @Test
    public void shouldNotBeSubjectToAutoOptInO23GConsumerWithAudioLastPromo() {
        //given
        User user = new User().withAutoOptInEnabled(true)
                              .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(O2_COMMUNITY_REWRITE_URL)))
                              .withTariff(_3G)
                              .withProvider(O2)
                              .withSegment(CONSUMER)
                              .withLastPromo(new PromoCode().withMediaType(AUDIO));

        //when
        boolean s = user.isSubjectToAutoOptIn();

        //then
        assertThat(s, is(false));
    }

    @Test
    public void shouldBeSubjectToAutoOptInO24GConsumerWithoutPromo() {
        //given
        User user = new User().withAutoOptInEnabled(true)
                              .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(O2_COMMUNITY_REWRITE_URL)))
                              .withTariff(_4G)
                              .withProvider(O2)
                              .withSegment(CONSUMER);

        //when
        boolean s = user.isSubjectToAutoOptIn();

        //then
        assertThat(s, is(true));
    }

    @Test
    public void shouldNotBeSubjectToAutoOptInNotO2Community4G() {
        //given
        User user = new User().withAutoOptInEnabled(true)
                              .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("")))
                              .withTariff(_4G)
                              .withProvider(O2)
                              .withSegment(BUSINESS)
                              .withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO));

        //when
        boolean s = user.isSubjectToAutoOptIn();

        //then
        assertThat(s, is(false));
    }

    @Test
    public void shouldNotBeSubjectToAutoOptInBusinessSegment4GUser() {
        //given
        User user = new User().withAutoOptInEnabled(true)
                              .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(O2_COMMUNITY_REWRITE_URL)))
                              .withTariff(_4G)
                              .withProvider(O2)
                              .withSegment(BUSINESS)
                              .withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO));

        //when
        boolean s = user.isSubjectToAutoOptIn();

        //then
        assertThat(s, is(false));
    }

    @Test
    public void shouldNotBeSubjectToAutoOptInNonO24GUser() {
        //given
        User user = new User().withAutoOptInEnabled(true)
                              .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(O2_COMMUNITY_REWRITE_URL)))
                              .withTariff(_4G)
                              .withProvider(NON_O2)
                              .withSegment(CONSUMER)
                              .withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO));

        //when
        boolean s = user.isSubjectToAutoOptIn();

        //then
        assertThat(s, is(false));
    }

    @Test
    public void shouldNotBeSubjectToAutoOptInUserWithOldUser() {
        //given
        User user = new User().withAutoOptInEnabled(true)
                              .withOldUser(new User())
                              .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(O2_COMMUNITY_REWRITE_URL)))
                              .withTariff(_3G)
                              .withProvider(O2)
                              .withSegment(CONSUMER)
                              .withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO));

        //when
        boolean s = user.isSubjectToAutoOptIn();

        //then
        assertThat(s, is(false));
    }


    private void checkUser(User user, boolean isActive) {
        assertThat(user.isActivatedUserName(), is(isActive));
    }

    @Test
    public void isActivatedUser() {
        checkUser(new User().withProvider(FACEBOOK).withUserName("aa@ukr.net"), true);
        checkUser(new User().withProvider(FACEBOOK), false);
        checkUser(new User().withProvider(EMAIL).withUserName("aa@ukr.net"), true);
        checkUser(new User().withProvider(O2).withUserName("aa@ukr.net"), false);
        checkUser(new User().withProvider(O2).withUserName("1").withMobile("1"), true);
        checkUser(new User().withProvider(VF).withUserName("aa@ukr.net"), false);
        checkUser(new User().withProvider(VF).withUserName("1").withMobile("1"), true);
    }


    @Test
    public void shouldNotBeSubjectToAutoOptInWhenDisabledAutoOptIn() {
        //given
        User user = new User().withAutoOptInEnabled(false)
                              .withOldUser(new User())
                              .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(O2_COMMUNITY_REWRITE_URL)))
                              .withTariff(_3G)
                              .withProvider(O2)
                              .withSegment(CONSUMER)
                              .withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO));

        //when
        boolean s = user.isSubjectToAutoOptIn();

        //then
        assertThat(s, is(false));
    }

    private void prepareDataToIsOn4GVideoAudioBoughtPeriod() {
        mockStatic(Utils.class);
        when(Utils.getEpochSeconds()).thenReturn(epochSeconds);

        paymentPolicy = PaymentPolicyFactory.createPaymentPolicy(paymentPolicyTariff);
        paymentPolicy.setMediaType(mediaType);

        O2PSMSPaymentDetails o2PSMSPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
        o2PSMSPaymentDetails.setPaymentPolicy(paymentPolicy);

        user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setLastSuccessfulPaymentDetails(o2PSMSPaymentDetails);
        user.setNextSubPayment(nextSubPayment);
    }
}
