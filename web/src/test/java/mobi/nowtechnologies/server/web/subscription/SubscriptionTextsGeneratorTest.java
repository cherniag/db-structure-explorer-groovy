package mobi.nowtechnologies.server.web.subscription;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import org.junit.*;

// TODO should be marked as integration test
public class SubscriptionTextsGeneratorTest {

    private static final Date NEXT_DATE = new DateTime(2013, 8, 21, 15, 59, 40, 900).toDate();
    private static final int DAYS = 10;
    private static Locale communityLocale = new Locale("o2");
    private static ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    private static SubscriptionTextsGenerator generator = new SubscriptionTextsGenerator(messageSource, communityLocale);

    @BeforeClass
    public static void beforeClass() {
        File file = new File(".");
        if (file.getAbsolutePath().endsWith("/web/.") || file.getAbsolutePath().endsWith("\\web\\.")) {
            messageSource.setBasename("file:src/main/webapp/i18n/messages");
        }
        else {
            messageSource.setBasename("file:web/src/main/webapp/i18n/messages");
        }
        messageSource.setDefaultEncoding("UTF-8");
    }

    @Test
    public void testMessageSource() {
        String msg = messageSource.getMessage("subscription.text.subscribed", new Object[] {}, communityLocale);
        Assert.assertEquals(msg, "Subscribed");
    }

    @Test
    public void testPendingPayment() {
        SubscriptionState s = new SubscriptionState();
        s.setPendingPayment(true);
        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Pending", r.getStatusText());
        Assert.assertEquals("", r.getNextBillingText());
        Assert.assertNull(r.getFutureText());
    }

    @Test
    public void testFreeTrialNonEligibleVideo() {

        SubscriptionState s = new SubscriptionState();
        s.setFreeTrial(true);
        s.setNextBillingDate(NEXT_DATE);
        s.setDaysToNextBillingDate(DAYS);
        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Free Trial", r.getStatusText());
        Assert.assertEquals("<br />You have " + DAYS + " days left on your free trial", r.getNextBillingText());
        Assert.assertNull(r.getFutureText());

    }

    @Test
    public void testFreeTrialEligibleVideoNotAcceptedTnC() {

        SubscriptionState s = new SubscriptionState();
        s.setFreeTrial(true);
        s.setEligibleForVideo(true);
        s.setUnlimitedFreeTrialFor4G(true);
        s.setFreeTrialAudioOnly(true);
        s.setDaysToNextBillingDate(DAYS);

        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Free Trial", r.getStatusText());
        Assert.assertEquals("<br />You have " + DAYS + " days left on your free trial", r.getNextBillingText());
        Assert.assertNull(r.getFutureText());
    }

    @Test
    public void testFreeTrialEligibleVideoAcceptedTnC() {

        SubscriptionState s = new SubscriptionState();
        s.setFreeTrial(true);
        s.setEligibleForVideo(true);
        s.setUnlimitedFreeTrialFor4G(true);
        s.setDaysToNextBillingDate(DAYS);

        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Free Trial", r.getStatusText());
        Assert.assertEquals("<br />You have " + DAYS + " days left on your free trial", r.getNextBillingText());
        Assert.assertNull(r.getFutureText());
    }

    @Test
    public void testFreeTrialLastDay() {
        SubscriptionState s = new SubscriptionState();
        s.setFreeTrial(true);
        s.setDaysToNextBillingDate(1);

        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Free Trial", r.getStatusText());
        Assert.assertEquals("<br />Today is the last day of your free trial", r.getNextBillingText());
    }

    @Test
    public void testFreeTrialOptedIn() {

        SubscriptionState s = new SubscriptionState();
        s.setFreeTrial(true);
        s.setFreeTrialOptedIn(true);
        s.setDaysToNextBillingDate(DAYS);

        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Subscribed", r.getStatusText());
        Assert.assertEquals("<br />You&#39;ve got " + DAYS + " days of free music left and then it&#39;s only &pound;1 a week.", r.getNextBillingText());
        Assert.assertEquals("Subscribed", r.getFutureText());
    }

    @Test
    public void testFreeTrialOptedInEligibleForVideo() {

        SubscriptionState s = new SubscriptionState();
        s.setFreeTrial(true);
        s.setFreeTrialOptedIn(true);
        s.setEligibleForVideo(true);
        s.setUnlimitedFreeTrialFor4G(true);
        s.setDaysToNextBillingDate(DAYS);

        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Subscribed", r.getStatusText());
        Assert.assertEquals("<br />You&#39;ve got " + DAYS + " days of free music left and then it&#39;s only &pound;1 a week.", r.getNextBillingText());
        Assert.assertEquals("Subscribed", r.getFutureText());
    }

    @Test
    public void testSubscribed() {

        SubscriptionState s = new SubscriptionState();
        s.setPaySubscription(true);
        s.setEligibleForVideo(false);
        s.setNextBillingDate(NEXT_DATE);

        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Subscribed", r.getStatusText());
        Assert.assertEquals("My next payment:", r.getNextBillingText());
        Assert.assertEquals("", r.getFutureText());
    }

    @Test
    public void testSubscribedVideo() {

        SubscriptionState s = new SubscriptionState();
        s.setPaySubscription(true);
        s.setSubscribedToVideo(true);
        s.setEligibleForVideo(true);
        s.setNextBillingDate(NEXT_DATE);

        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Subscribed", r.getStatusText());
        Assert.assertEquals("My next payment:", r.getNextBillingText());
        Assert.assertEquals("", r.getFutureText());
    }

    @Test
    public void testSubscribedAudioOnly() {

        SubscriptionState s = new SubscriptionState();
        s.setPaySubscription(true);
        s.setSubscribedToVideo(false);
        s.setEligibleForVideo(true);
        s.setNextBillingDate(NEXT_DATE);

        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Subscribed", r.getStatusText());
        Assert.assertEquals("My next payment:", r.getNextBillingText());
        Assert.assertEquals("", r.getFutureText());
    }

    @Test
    public void testSubscribedUpgrading() {

        SubscriptionState s = new SubscriptionState();
        s.setPaySubscription(true);
        s.setSubscribedToVideo(false);
        s.setEligibleForVideo(true);
        s.setNextBillingDate(NEXT_DATE);
        s.setUpgradingToVideo(true);

        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Subscribed", r.getStatusText());
        Assert.assertEquals("Your video access begins on", r.getNextBillingText());
        Assert.assertEquals("", r.getFutureText());
    }

    @Test
    public void testSubscribedDowngrading() {

        SubscriptionState s = new SubscriptionState();
        s.setPaySubscription(true);
        s.setSubscribedToVideo(true);
        s.setEligibleForVideo(true);
        s.setNextBillingDate(NEXT_DATE);
        s.setDowngradingToAudioOnly(true);

        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Subscribed", r.getStatusText());
        Assert.assertEquals("Your video access ends on", r.getNextBillingText());
        Assert.assertEquals("", r.getFutureText());
    }

    @Test
    public void testSubscribedExpiring() {

        SubscriptionState s = new SubscriptionState();
        s.setPaySubscription(true);
        s.setEligibleForVideo(false);
        s.setExpiringSubscription(true);
        s.setNextBillingDate(NEXT_DATE);

        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Subscribed", r.getStatusText());
        Assert.assertEquals("Ending on:", r.getNextBillingText());
        Assert.assertEquals("", r.getFutureText());
    }

    @Test
    public void testSubscribedVideoExpiring() {

        SubscriptionState s = new SubscriptionState();
        s.setPaySubscription(true);
        s.setSubscribedToVideo(true);
        s.setEligibleForVideo(true);
        s.setNextBillingDate(NEXT_DATE);
        s.setExpiringSubscription(true);

        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Subscribed", r.getStatusText());
        Assert.assertEquals("Ending on:", r.getNextBillingText());
        Assert.assertEquals("", r.getFutureText());
    }

    @Test
    public void testSubscribedAudioOnlyExpiring() {

        SubscriptionState s = new SubscriptionState();
        s.setPaySubscription(true);
        s.setSubscribedToVideo(false);
        s.setEligibleForVideo(true);
        s.setNextBillingDate(NEXT_DATE);
        s.setExpiringSubscription(true);

        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Subscribed", r.getStatusText());
        Assert.assertEquals("Ending on:", r.getNextBillingText());
        Assert.assertEquals("", r.getFutureText());
    }

    @Test
    public void testPreviewMode() {

        SubscriptionState s = new SubscriptionState();
        s.setEligibleForVideo(false);

        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Unsubscribed", r.getStatusText());
        Assert.assertEquals("<br />It's only &pound;1 a week to keep the music coming. Choose an option below. Don't miss out.", r.getNextBillingText());
        Assert.assertNull(r.getFutureText());
    }

    @Test
    public void testPreviewModeEligibleVideo() {

        SubscriptionState s = new SubscriptionState();
        s.setEligibleForVideo(true);

        SubscriptionTexts r = generator.generate(s);
        Assert.assertEquals("Unsubscribed", r.getStatusText());
        Assert.assertEquals("<br />It's only &pound;1 a week to keep the music coming. Choose an option below. Don't miss out.", r.getNextBillingText());
        Assert.assertNull(r.getFutureText());
    }
}
