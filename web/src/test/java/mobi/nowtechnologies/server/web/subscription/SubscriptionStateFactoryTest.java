package mobi.nowtechnologies.server.web.subscription;

import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;
import mobi.nowtechnologies.server.service.o2.impl.O2UserDetailsUpdater;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.Tariff;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Period;

import org.junit.*;

import junit.framework.Assert;

@SuppressWarnings("deprecation")
public class SubscriptionStateFactoryTest {

    private static final int DAYS = 10;

    private SubscriptionStateFactory factory = new SubscriptionStateFactory();

    @Test
    public void testPreview() {
        User user = new User();
        setNextSubpayment(user, getPastDate());
        setUserEligibleVideo(user, false);

        user.setFreeTrialExpired(getPastDate());
        user.setStatus(getUserStatus(UserStatus.LIMITED));
        SubscriptionState state = factory.getInstance(user);

        Assert.assertFalse(state.isPaySubscription());
        Assert.assertFalse(state.isFreeTrial());
        Assert.assertFalse(state.isUnlimitedFreeTrialFor4G());
        Assert.assertFalse(state.isFreeTrialAudioOnly());
        Assert.assertFalse(state.isFreeTrialOptedIn());
        Assert.assertFalse(state.isEligibleForVideo());
        Assert.assertFalse(state.isSubscribedToVideo());
        Assert.assertFalse(state.isUpgradingToVideo());
        Assert.assertFalse(state.isDowngradingToAudioOnly());
        Assert.assertFalse(state.isExpiringSubscription());
        Assert.assertNull(state.getNextBillingDate());
        Assert.assertNull(state.getDaysToNextBillingDate());
    }

    @Test
    public void testFreeTrialNonEligibleVideo() {

        User user = new User();
        Date futureDate = getFutureDate();
        user.setFreeTrialExpired(futureDate);
        setUserEligibleVideo(user, false);

        user.setStatus(getUserStatus(UserStatus.SUBSCRIBED));
        SubscriptionState state = factory.getInstance(user);

        Assert.assertFalse(state.isPaySubscription());
        Assert.assertTrue(state.isFreeTrial());
        Assert.assertFalse(state.isUnlimitedFreeTrialFor4G());
        Assert.assertTrue(state.isFreeTrialAudioOnly());
        Assert.assertFalse(state.isFreeTrialOptedIn());
        Assert.assertFalse(state.isEligibleForVideo());
        Assert.assertFalse(state.isSubscribedToVideo());
        Assert.assertFalse(state.isUpgradingToVideo());
        Assert.assertFalse(state.isDowngradingToAudioOnly());
        Assert.assertFalse(state.isExpiringSubscription());
        Assert.assertEquals(futureDate, state.getNextBillingDate());
        Assert.assertEquals(DAYS, state.getDaysToNextBillingDate().intValue());

    }

    @Test
    public void testFreeTrialEligibleVideoNotAcceptedTnC() {
        User user = new User();
        Date futureDate = getFutureDate();
        user.setFreeTrialExpired(futureDate);
        setUserEligibleVideo(user, true);

        user.setStatus(getUserStatus(UserStatus.SUBSCRIBED));
        SubscriptionState state = factory.getInstance(user);

        Assert.assertFalse(state.isPaySubscription());
        Assert.assertTrue(state.isFreeTrial());
        Assert.assertTrue(state.isUnlimitedFreeTrialFor4G());
        Assert.assertTrue(state.isFreeTrialAudioOnly());
        Assert.assertFalse(state.isFreeTrialOptedIn());
        Assert.assertTrue(state.isEligibleForVideo());
        Assert.assertFalse(state.isSubscribedToVideo());
        Assert.assertFalse(state.isUpgradingToVideo());
        Assert.assertFalse(state.isDowngradingToAudioOnly());
        Assert.assertFalse(state.isExpiringSubscription());
        Assert.assertEquals(futureDate, state.getNextBillingDate());
        Assert.assertEquals(DAYS, state.getDaysToNextBillingDate().intValue());

    }

    @Test
    public void testFreeTrialEligibleVideoAcceptedTnC() {
        User user = new User();

        user.setLastPromo(getPromo(MediaType.VIDEO_AND_AUDIO));
        Date futureDate = getFutureDate();
        user.setFreeTrialExpired(futureDate);
        setUserEligibleVideo(user, true);
        user.setVideoFreeTrialHasBeenActivated(true);
        user.setStatus(getUserStatus(UserStatus.SUBSCRIBED));
        SubscriptionState state = factory.getInstance(user);

        Assert.assertFalse(state.isPaySubscription());
        Assert.assertTrue(state.isFreeTrial());
        Assert.assertTrue(state.isUnlimitedFreeTrialFor4G());
        Assert.assertFalse(state.isFreeTrialAudioOnly());
        Assert.assertFalse(state.isFreeTrialOptedIn());
        Assert.assertTrue(state.isEligibleForVideo());
        Assert.assertFalse(state.isSubscribedToVideo());
        Assert.assertFalse(state.isUpgradingToVideo());
        Assert.assertFalse(state.isDowngradingToAudioOnly());
        Assert.assertFalse(state.isExpiringSubscription());
        Assert.assertEquals(futureDate, state.getNextBillingDate());
        Assert.assertEquals(DAYS, state.getDaysToNextBillingDate().intValue());
    }

    @Test
    public void testFreeTrialOptedIn() {
        User user = new User();

        user.setLastPromo(getPromo(MediaType.AUDIO));
        Date futureDate = getFutureDate();
        user.setFreeTrialExpired(futureDate);
        setNextSubpayment(user, futureDate);
        setUserEligibleVideo(user, false);
        user.setVideoFreeTrialHasBeenActivated(true);
        user.setStatus(getUserStatus(UserStatus.SUBSCRIBED));
        user.setCurrentPaymentDetails(createPaymentDetails(MediaType.AUDIO));

        SubscriptionState state = factory.getInstance(user);

        Assert.assertFalse(state.isPaySubscription());
        Assert.assertTrue(state.isFreeTrial());
        Assert.assertFalse(state.isUnlimitedFreeTrialFor4G());
        Assert.assertTrue(state.isFreeTrialAudioOnly());
        Assert.assertTrue(state.isFreeTrialOptedIn());
        Assert.assertFalse(state.isEligibleForVideo());
        Assert.assertFalse(state.isSubscribedToVideo());
        Assert.assertFalse(state.isUpgradingToVideo());
        Assert.assertFalse(state.isDowngradingToAudioOnly());
        Assert.assertFalse(state.isExpiringSubscription());
        Assert.assertEquals(futureDate, state.getNextBillingDate());
        Assert.assertEquals(DAYS, state.getDaysToNextBillingDate().intValue());
    }

    @Test
    public void testFreeTrialOptedInEligibleForVideo() {
        User user = new User();

        user.setLastPromo(getPromo(MediaType.VIDEO_AND_AUDIO));
        Date futureDate = getFutureDate();
        user.setFreeTrialExpired(futureDate);
        setNextSubpayment(user, futureDate);
        setUserEligibleVideo(user, true);
        user.setVideoFreeTrialHasBeenActivated(true);
        user.setStatus(getUserStatus(UserStatus.SUBSCRIBED));
        user.setCurrentPaymentDetails(createPaymentDetails(MediaType.VIDEO_AND_AUDIO));

        SubscriptionState state = factory.getInstance(user);

        Assert.assertFalse(state.isPaySubscription());
        Assert.assertTrue(state.isFreeTrial());
        Assert.assertTrue(state.isUnlimitedFreeTrialFor4G());
        Assert.assertFalse(state.isFreeTrialAudioOnly());
        Assert.assertTrue(state.isFreeTrialOptedIn());
        Assert.assertTrue(state.isEligibleForVideo());
        Assert.assertFalse(state.isSubscribedToVideo());
        Assert.assertFalse(state.isUpgradingToVideo());
        Assert.assertFalse(state.isDowngradingToAudioOnly());
        Assert.assertFalse(state.isExpiringSubscription());
        Assert.assertEquals(futureDate, state.getNextBillingDate());
        Assert.assertEquals(DAYS, state.getDaysToNextBillingDate().intValue());

    }

    @Test
    public void testSubscribed() {
        User user = new User();

        Date futureDate = getFutureDate();

        setNextSubpayment(user, futureDate);

        setUserEligibleVideo(user, false);
        user.setStatus(getUserStatus(UserStatus.SUBSCRIBED));
        user.setCurrentPaymentDetails(createPaymentDetails(MediaType.AUDIO));

        SubscriptionState state = factory.getInstance(user);

        Assert.assertTrue(state.isPaySubscription());
        Assert.assertFalse(state.isFreeTrial());
        Assert.assertFalse(state.isUnlimitedFreeTrialFor4G());
        Assert.assertFalse(state.isFreeTrialAudioOnly());
        Assert.assertFalse(state.isFreeTrialOptedIn());
        Assert.assertFalse(state.isEligibleForVideo());
        Assert.assertFalse(state.isSubscribedToVideo());
        Assert.assertFalse(state.isUpgradingToVideo());
        Assert.assertFalse(state.isDowngradingToAudioOnly());
        Assert.assertFalse(state.isExpiringSubscription());
        Assert.assertEquals(futureDate, state.getNextBillingDate());
        Assert.assertEquals(DAYS, state.getDaysToNextBillingDate().intValue());
    }

    @Test
    public void testSubscribedVideo() {
        User user = new User();

        Date futureDate = getFutureDate();

        setNextSubpayment(user, futureDate);

        setUserEligibleVideo(user, true);
        user.setStatus(getUserStatus(UserStatus.SUBSCRIBED));
        user.setCurrentPaymentDetails(createPaymentDetails(MediaType.VIDEO_AND_AUDIO));
        user.setLastSuccessfulPaymentDetails(user.getCurrentPaymentDetails());

        SubscriptionState state = factory.getInstance(user);

        Assert.assertTrue(state.isPaySubscription());
        Assert.assertFalse(state.isFreeTrial());
        Assert.assertFalse(state.isUnlimitedFreeTrialFor4G());
        Assert.assertFalse(state.isFreeTrialAudioOnly());
        Assert.assertFalse(state.isFreeTrialOptedIn());
        Assert.assertTrue(state.isEligibleForVideo());
        Assert.assertTrue(state.isSubscribedToVideo());
        Assert.assertFalse(state.isUpgradingToVideo());
        Assert.assertFalse(state.isDowngradingToAudioOnly());
        Assert.assertFalse(state.isExpiringSubscription());
        Assert.assertEquals(futureDate, state.getNextBillingDate());
        Assert.assertEquals(DAYS, state.getDaysToNextBillingDate().intValue());

    }

    @Test
    public void testSubscribedAudioOnly() {
        User user = new User();

        Date futureDate = getFutureDate();

        setNextSubpayment(user, futureDate);

        setUserEligibleVideo(user, true);
        user.setStatus(getUserStatus(UserStatus.SUBSCRIBED));
        user.setCurrentPaymentDetails(createPaymentDetails(MediaType.AUDIO));
        user.setLastSuccessfulPaymentDetails(user.getCurrentPaymentDetails());

        SubscriptionState state = factory.getInstance(user);

        Assert.assertTrue(state.isPaySubscription());
        Assert.assertFalse(state.isFreeTrial());
        Assert.assertFalse(state.isUnlimitedFreeTrialFor4G());
        Assert.assertFalse(state.isFreeTrialAudioOnly());
        Assert.assertFalse(state.isFreeTrialOptedIn());
        Assert.assertTrue(state.isEligibleForVideo());
        Assert.assertFalse(state.isSubscribedToVideo());
        Assert.assertFalse(state.isUpgradingToVideo());
        Assert.assertFalse(state.isDowngradingToAudioOnly());
        Assert.assertFalse(state.isExpiringSubscription());
        Assert.assertEquals(futureDate, state.getNextBillingDate());
        Assert.assertEquals(DAYS, state.getDaysToNextBillingDate().intValue());
    }

    @Test
    public void testSubscribedUpgrading() {
        User user = new User();

        Date futureDate = getFutureDate();

        setNextSubpayment(user, futureDate);

        setUserEligibleVideo(user, true);
        user.setStatus(getUserStatus(UserStatus.SUBSCRIBED));
        user.setCurrentPaymentDetails(createPaymentDetails(MediaType.VIDEO_AND_AUDIO));
        user.setLastSuccessfulPaymentDetails(createPaymentDetails(MediaType.AUDIO));

        SubscriptionState state = factory.getInstance(user);

        Assert.assertTrue(state.isPaySubscription());
        Assert.assertFalse(state.isFreeTrial());
        Assert.assertFalse(state.isUnlimitedFreeTrialFor4G());
        Assert.assertFalse(state.isFreeTrialAudioOnly());
        Assert.assertFalse(state.isFreeTrialOptedIn());
        Assert.assertTrue(state.isEligibleForVideo());
        Assert.assertFalse(state.isSubscribedToVideo());
        Assert.assertTrue(state.isUpgradingToVideo());
        Assert.assertFalse(state.isDowngradingToAudioOnly());
        Assert.assertFalse(state.isExpiringSubscription());
        Assert.assertEquals(futureDate, state.getNextBillingDate());
        Assert.assertEquals(DAYS, state.getDaysToNextBillingDate().intValue());

    }

    @Test
    public void testSubscribedDowngrading() {
        User user = new User();

        Date futureDate = getFutureDate();

        setNextSubpayment(user, futureDate);

        setUserEligibleVideo(user, true);
        user.setStatus(getUserStatus(UserStatus.SUBSCRIBED));
        user.setCurrentPaymentDetails(createPaymentDetails(MediaType.AUDIO));
        user.setLastSuccessfulPaymentDetails(createPaymentDetails(MediaType.VIDEO_AND_AUDIO));

        SubscriptionState state = factory.getInstance(user);

        Assert.assertTrue(state.isPaySubscription());
        Assert.assertFalse(state.isFreeTrial());
        Assert.assertFalse(state.isUnlimitedFreeTrialFor4G());
        Assert.assertFalse(state.isFreeTrialAudioOnly());
        Assert.assertFalse(state.isFreeTrialOptedIn());
        Assert.assertTrue(state.isEligibleForVideo());
        Assert.assertTrue(state.isSubscribedToVideo());
        Assert.assertFalse(state.isUpgradingToVideo());
        Assert.assertTrue(state.isDowngradingToAudioOnly());
        Assert.assertFalse(state.isExpiringSubscription());
        Assert.assertEquals(futureDate, state.getNextBillingDate());
        Assert.assertEquals(DAYS, state.getDaysToNextBillingDate().intValue());

    }

    @Test
    public void testSubscribedExpiring() {
        User user = new User();
        Date futureDate = getFutureDate();

        setNextSubpayment(user, futureDate);

        setUserEligibleVideo(user, false);
        user.setStatus(getUserStatus(UserStatus.SUBSCRIBED));
        user.setCurrentPaymentDetails(createPaymentDetails(MediaType.AUDIO));

        user.getCurrentPaymentDetails().setActivated(false);
        user.setLastSuccessfulPaymentDetails(createPaymentDetails(MediaType.AUDIO));

        SubscriptionState state = factory.getInstance(user);

        Assert.assertTrue(state.isPaySubscription());
        Assert.assertFalse(state.isFreeTrial());
        Assert.assertFalse(state.isUnlimitedFreeTrialFor4G());
        Assert.assertFalse(state.isFreeTrialAudioOnly());
        Assert.assertFalse(state.isFreeTrialOptedIn());
        Assert.assertFalse(state.isEligibleForVideo());
        Assert.assertFalse(state.isSubscribedToVideo());
        Assert.assertFalse(state.isUpgradingToVideo());
        Assert.assertFalse(state.isDowngradingToAudioOnly());
        Assert.assertTrue(state.isExpiringSubscription());
        Assert.assertEquals(futureDate, state.getNextBillingDate());
        Assert.assertEquals(DAYS, state.getDaysToNextBillingDate().intValue());
    }

    @Test
    public void testSubscribedExpiringInFreeTrial() {
        User user = new User();
        Date futureDate = getFutureDate();
        user.setFreeTrialExpired(futureDate);
        setNextSubpayment(user, futureDate);

        setUserEligibleVideo(user, false);
        user.setStatus(getUserStatus(UserStatus.SUBSCRIBED));
        user.setCurrentPaymentDetails(createPaymentDetails(MediaType.AUDIO));

        user.getCurrentPaymentDetails().setActivated(false);
        user.setLastSuccessfulPaymentDetails(createPaymentDetails(MediaType.AUDIO));

        SubscriptionState state = factory.getInstance(user);

        Assert.assertFalse(state.isPaySubscription());
        Assert.assertTrue(state.isFreeTrial());
        Assert.assertFalse(state.isUnlimitedFreeTrialFor4G());
        Assert.assertTrue(state.isFreeTrialAudioOnly());
        Assert.assertFalse(state.isFreeTrialOptedIn());
        Assert.assertFalse(state.isEligibleForVideo());
        Assert.assertFalse(state.isSubscribedToVideo());
        Assert.assertFalse(state.isUpgradingToVideo());
        Assert.assertFalse(state.isDowngradingToAudioOnly());
        Assert.assertFalse(state.isExpiringSubscription());
        Assert.assertEquals(futureDate, state.getNextBillingDate());
        Assert.assertEquals(DAYS, state.getDaysToNextBillingDate().intValue());
    }

    @Test
    public void testSubscribedVideoExpiring() {
        User user = new User();

        Date futureDate = getFutureDate();

        setNextSubpayment(user, futureDate);

        setUserEligibleVideo(user, true);
        user.setStatus(getUserStatus(UserStatus.SUBSCRIBED));
        user.setCurrentPaymentDetails(createPaymentDetails(MediaType.VIDEO_AND_AUDIO));

        user.getCurrentPaymentDetails().setActivated(false);
        user.setLastSuccessfulPaymentDetails(createPaymentDetails(MediaType.VIDEO_AND_AUDIO));

        SubscriptionState state = factory.getInstance(user);

        Assert.assertTrue(state.isPaySubscription());
        Assert.assertFalse(state.isFreeTrial());
        Assert.assertFalse(state.isUnlimitedFreeTrialFor4G());
        Assert.assertFalse(state.isFreeTrialAudioOnly());
        Assert.assertFalse(state.isFreeTrialOptedIn());
        Assert.assertTrue(state.isEligibleForVideo());
        Assert.assertTrue(state.isSubscribedToVideo());
        Assert.assertFalse(state.isUpgradingToVideo());
        Assert.assertFalse(state.isDowngradingToAudioOnly());
        Assert.assertTrue(state.isExpiringSubscription());
        Assert.assertEquals(futureDate, state.getNextBillingDate());
        Assert.assertEquals(DAYS, state.getDaysToNextBillingDate().intValue());

    }

    @Test
    public void testSubscribedAudioOnlyExpiring() {
        User user = new User();
        Date futureDate = getFutureDate();

        setNextSubpayment(user, futureDate);

        setUserEligibleVideo(user, true);
        user.setStatus(getUserStatus(UserStatus.SUBSCRIBED));
        user.setCurrentPaymentDetails(createPaymentDetails(MediaType.AUDIO));

        user.getCurrentPaymentDetails().setActivated(false);
        user.setLastSuccessfulPaymentDetails(createPaymentDetails(MediaType.AUDIO));

        SubscriptionState state = factory.getInstance(user);

        Assert.assertTrue(state.isPaySubscription());
        Assert.assertFalse(state.isFreeTrial());
        Assert.assertFalse(state.isUnlimitedFreeTrialFor4G());
        Assert.assertFalse(state.isFreeTrialAudioOnly());
        Assert.assertFalse(state.isFreeTrialOptedIn());
        Assert.assertTrue(state.isEligibleForVideo());
        Assert.assertFalse(state.isSubscribedToVideo());
        Assert.assertFalse(state.isUpgradingToVideo());
        Assert.assertFalse(state.isDowngradingToAudioOnly());
        Assert.assertTrue(state.isExpiringSubscription());
        Assert.assertEquals(futureDate, state.getNextBillingDate());
        Assert.assertEquals(DAYS, state.getDaysToNextBillingDate().intValue());
    }

    private UserStatus getUserStatus(String statusName) {
        UserStatus userStatus = new UserStatus();
        userStatus.setName(statusName);
        return userStatus;
    }

    private void setNextSubpayment(User user, Date someDate) {
        user.setNextSubPayment(new Long(someDate.getTime() / 1000).intValue());
    }

    private Date getFutureDate() {
        Date d = new DateTime().plus(Period.days(DAYS - 1)).plus(Period.hours(1)).toDate();
        return new Date((d.getTime() / 1000) * 1000);
    }

    private Date getPastDate() {
        return new DateTime().minus(Period.days(DAYS)).toDate();
    }

    private void setUserEligibleVideo(User user, boolean eligibleForVideo) {
        O2SubscriberData o2subscriberData = new O2SubscriberData();
        if (eligibleForVideo) {
            o2subscriberData.setProviderO2(true);
            o2subscriberData.setBusinessOrConsumerSegment(false);
            o2subscriberData.setContractPostPayOrPrePay(true);
            o2subscriberData.setTariff4G(true);
        }
        new O2UserDetailsUpdater().setUserFieldsFromSubscriberData(user, o2subscriberData);
    }

    private PaymentDetails createPaymentDetails(MediaType mediaType) {

        PaymentDetails pd = new O2PSMSPaymentDetails();
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setTariff(mediaType == MediaType.VIDEO_AND_AUDIO ?
                                Tariff._4G :
                                Tariff._3G);
        paymentPolicy.setMediaType(mediaType);

        pd.setPaymentPolicy(paymentPolicy);
        pd.setLastPaymentStatus(PaymentDetailsStatus.SUCCESSFUL);
        pd.setActivated(true);
        return pd;
    }

    private PromoCode getPromo(MediaType mediaType) {
        PromoCode lastPromo = new PromoCode();
        lastPromo.setMediaType(mediaType);
        return lastPromo;
    }

}
