package mobi.nowtechnologies.server.web.subscription;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import junit.framework.Assert;

import mobi.nowtechnologies.server.web.subscription.SubscriptionState;
import mobi.nowtechnologies.server.web.subscription.SubscriptionTexts;
import mobi.nowtechnologies.server.web.subscription.SubscriptionTextsGenerator;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

// TODO should be marked as integration test
public class SubscriptionTextsGeneratorTest {

	private static Locale communityLocale = new Locale("o2");
	private static ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
	private static SubscriptionTextsGenerator generator = new SubscriptionTextsGenerator(messageSource, communityLocale);
	private static final Date NEXT_DATE = new DateTime(2013, 8, 21, 15, 59, 40, 900).toDate();
	private static final int DAYS = 10;

	@BeforeClass
	public static void beforeClass() {
        File file = new File(".");
        if (file.getAbsolutePath().endsWith("/web/.")){
            messageSource.setBasename("file:src/main/webapp/i18n/messages");
        }else{
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
	public void testFreeTrialNonEligibleVideo() {

		SubscriptionState s = new SubscriptionState();
		s.setFreeTrial(true);
		s.setNextBillingDate(NEXT_DATE);
		s.setDaysToNextBillingDate(DAYS);
		SubscriptionTexts r = generator.generate(s);
		Assert.assertEquals("Free Trial", r.getStatusText());
		Assert.assertEquals("You have " + DAYS + " days left on your free trial", r.getNextBillingText());
		Assert.assertNull(r.getFutureText());

	}

	@Test
	public void testFreeTrialEligibleVideoNotAcceptedTnC() {

		SubscriptionState s = new SubscriptionState();
		s.setFreeTrial(true);
		s.setEligibleForVideo(true);
		s.setUnlimitedFreeTrialFor4G(true);
		s.setFreeTrialAudioOnly(true);

		SubscriptionTexts r = generator.generate(s);
		Assert.assertEquals("Free Trial", r.getStatusText());
		Assert.assertEquals("You will be notified towards the end of your trial", r.getNextBillingText());
		Assert.assertNull(r.getFutureText());
	}

	@Test
	public void testFreeTrialEligibleVideoAcceptedTnC() {

		SubscriptionState s = new SubscriptionState();
		s.setFreeTrial(true);
		s.setEligibleForVideo(true);
		s.setUnlimitedFreeTrialFor4G(true);

		SubscriptionTexts r = generator.generate(s);
		Assert.assertEquals("Free Trial", r.getStatusText());
		Assert.assertEquals("You will be notified towards the end of your trial", r.getNextBillingText());
		Assert.assertNull(r.getFutureText());
	}

	@Test
	public void testFreeTrialOptedIn() {

		SubscriptionState s = new SubscriptionState();
		s.setFreeTrial(true);
		s.setFreeTrialOptedIn(true);
		s.setDaysToNextBillingDate(DAYS);

		SubscriptionTexts r = generator.generate(s);
		Assert.assertEquals("Subscribed", r.getStatusText());
		Assert.assertEquals("Due to subscribe after free trial (" + DAYS + " days left)", r.getNextBillingText());
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
		Assert.assertEquals("You will be notified towards the end of your trial", r.getNextBillingText());
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
		Assert.assertEquals("Next billing cycle: 21 August 2013", r.getNextBillingText());
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
		Assert.assertEquals("Next billing cycle: 21 August 2013", r.getNextBillingText());
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
		Assert.assertEquals("Next billing cycle: 21 August 2013", r.getNextBillingText());
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
		Assert.assertEquals("Due get access to Video on 21 August 2013", r.getNextBillingText());
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
		Assert.assertEquals("Video access will expire on 21 August 2013", r.getNextBillingText());
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
		Assert.assertEquals("Ending on: 21 August 2013", r.getNextBillingText());
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
		Assert.assertEquals("Ending on: 21 August 2013", r.getNextBillingText());
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
		Assert.assertEquals("Ending on: 21 August 2013", r.getNextBillingText());
		Assert.assertEquals("", r.getFutureText());
	}

	@Test
	public void testPreviewMode() {

		SubscriptionState s = new SubscriptionState();
		s.setEligibleForVideo(false);

		SubscriptionTexts r = generator.generate(s);
		Assert.assertEquals("Unsubscribed", r.getStatusText());
		Assert.assertEquals("Consider subscribing to gain full access.", r.getNextBillingText());
		Assert.assertNull(r.getFutureText());
	}

	@Test
	public void testPreviewModeEligibleVideo() {

		SubscriptionState s = new SubscriptionState();
		s.setEligibleForVideo(true);

		SubscriptionTexts r = generator.generate(s);
		Assert.assertEquals("Unsubscribed", r.getStatusText());
		Assert.assertEquals("Consider subscribing to gain full access.", r.getNextBillingText());
		Assert.assertNull(r.getFutureText());
	}
}
