package mobi.nowtechnologies.server.service.aop;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Locale;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.service.MigService;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import antlr.Utils;

@RunWith( PowerMockRunner.class )
@PrepareForTest(Utils.class)
@SuppressWarnings("deprecation")
public class SMSNotificationTest {
	
	@Mock
	private MigService mockMigService;
	
	@Mock
	private CommunityResourceBundleMessageSource mockMessageSource;

	private SMSNotification fixture;

	@Test
	public void testSendLimitedStatusSMS_NotLimitedStatus_Failure()
		throws Exception {
		User user = UserFactory.createUser();
		
		doReturn(null).when(mockMigService).sendFreeSms(anyString(), anyInt(), anyString(), anyString());

		fixture.sendLimitedStatusSMS(user);

		verify(mockMigService, times(0)).sendFreeSms(anyString(), anyInt(), anyString(), anyString());
	}
	
	@Test
	public void testSendLimitedStatusSMS_NotAvailableCommunity_Failure()
		throws Exception {
		User user = UserFactory.createUser();
		user.getStatus().setName(UserStatus.LIMITED.name());
		fixture.setAvailableCommunities("community3, community2, community1");
		
		doReturn(null).when(mockMigService).sendFreeSms(anyString(), anyInt(), anyString(), anyString());

		fixture.sendLimitedStatusSMS(user);

		verify(mockMigService, times(0)).sendFreeSms(anyString(), anyInt(), anyString(), anyString());
	}
	
	@Test
	public void testSendLimitedStatusSMS_Success()
		throws Exception {
		String msg = "message1";
		String paymentsUrl = "http://musicqubed.com/web/payments.html";
		String tinyUrlService = "http://tinyurl.com/api-create.php?url={url}";
		User user = UserFactory.createUser();
		user.getStatus().setName(UserStatus.LIMITED.name());
		Community community = user.getUserGroup().getCommunity();
		fixture.setAvailableCommunities("community3, community2, community1, "+community.getRewriteUrlParameter());
		fixture.setPaymentsUrl(paymentsUrl);
		fixture.setTinyUrlService(tinyUrlService);
		
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.limited.status.text"), any(Object[].class), eq((Locale)null))).thenReturn(msg);
		doReturn(null).when(mockMigService).sendFreeSms(anyString(), anyInt(), anyString(), eq(msg));

		fixture.sendLimitedStatusSMS(user);

		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.limited.status.text"), any(Object[].class), eq((Locale)null));
		verify(mockMigService, times(1)).sendFreeSms(anyString(), anyInt(), anyString(), eq(msg));
	}
	
	@Test
	public void testSendLimitedStatusSMS_WithBadTinyServiceUrl_Success()
		throws Exception {
		String msg = "message1";
		String paymentsUrl = "http://musicqubed.com/web/payments.html";
		String tinyUrlService = "bad url";
		User user = UserFactory.createUser();
		user.getStatus().setName(UserStatus.LIMITED.name());
		Community community = user.getUserGroup().getCommunity();
		fixture.setAvailableCommunities("community3, community2, community1, "+community.getRewriteUrlParameter());
		fixture.setPaymentsUrl(paymentsUrl);
		fixture.setTinyUrlService(tinyUrlService);
		
		String url =  paymentsUrl + "?rememberMeToken=" + user.getToken()+"&community="+community.getRewriteUrlParameter();
		String[] args = {url};
		
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.limited.status.text"), eq(args), eq((Locale)null))).thenReturn(msg);
		doReturn(null).when(mockMigService).sendFreeSms(anyString(), anyInt(), anyString(), eq(msg));

		fixture.sendLimitedStatusSMS(user);

		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.limited.status.text"), eq(args), eq((Locale)null));
		verify(mockMigService, times(1)).sendFreeSms(anyString(), anyInt(), anyString(), eq(msg));
	}
	
	@Test
	public void testSendUnsubscribePotentialSMS_NotCurrentPaymentDetails_Failure()
		throws Exception {
		User user = UserFactory.createUser(null, new BigDecimal(0));
		
		doReturn(null).when(mockMigService).sendFreeSms(anyString(), anyInt(), anyString(), anyString());

		fixture.sendUnsubscribePotentialSMS(user);

		verify(mockMigService, times(0)).sendFreeSms(anyString(), anyInt(), anyString(), anyString());
	}
	
	@Test
	public void testSendUnsubscribePotentialSMS_NotAvailableCommunity_Failure()
		throws Exception {
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), new BigDecimal(0));
		fixture.setAvailableCommunities("community3, community2, community1");
		
		doReturn(null).when(mockMigService).sendFreeSms(anyString(), anyInt(), anyString(), anyString());

		fixture.sendUnsubscribePotentialSMS(user);

		verify(mockMigService, times(0)).sendFreeSms(anyString(), anyInt(), anyString(), anyString());
	}
	
	@Test
	public void testSendUnsubscribePotentialSMS_Success()
		throws Exception {
		String msg = "message1";
		String unsubscribeUrl = "http://musicqubed.com/web/payments/unsubscribe.html";
		String tinyUrlService = "http://tinyurl.com/api-create.php?url={url}";
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), new BigDecimal(0));
		Community community = user.getUserGroup().getCommunity();
		fixture.setAvailableCommunities("community3, community2, community1, "+community.getRewriteUrlParameter());
		fixture.setUnsubscribeUrl(unsubscribeUrl);
		fixture.setTinyUrlService(tinyUrlService);
		
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.unsubscribe.potential.text"), any(Object[].class), eq((Locale)null))).thenReturn(msg);
		doReturn(null).when(mockMigService).sendFreeSms(anyString(), anyInt(), anyString(), eq(msg));

		fixture.sendUnsubscribePotentialSMS(user);

		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.unsubscribe.potential.text"), any(Object[].class), eq((Locale)null));
		verify(mockMigService, times(1)).sendFreeSms(anyString(), anyInt(), anyString(), eq(msg));
	}
	
	@Test
	public void testSendUnsubscribePotentialSMS_WithBadTinyServiceUrl_Success()
		throws Exception {
		String msg = "message1";
		String unsubscribeUrl = "http://musicqubed.com/web/payments/unsubscribe.html";
		String tinyUrlService = "bad url";
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), new BigDecimal(0));
		Community community = user.getUserGroup().getCommunity();
		fixture.setAvailableCommunities("community3, community2, community1, "+community.getRewriteUrlParameter());
		fixture.setUnsubscribeUrl(unsubscribeUrl);
		fixture.setTinyUrlService(tinyUrlService);
		
		String url =  unsubscribeUrl + "?rememberMeToken=" + user.getToken()+"&community="+community.getRewriteUrlParameter();
		String[] args = {url};
		
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.unsubscribe.potential.text"), eq(args), eq((Locale)null))).thenReturn(msg);
		doReturn(null).when(mockMigService).sendFreeSms(anyString(), anyInt(), anyString(), eq(msg));

		fixture.sendUnsubscribePotentialSMS(user);

		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.unsubscribe.potential.text"), eq(args), eq((Locale)null));
		verify(mockMigService, times(1)).sendFreeSms(anyString(), anyInt(), anyString(), eq(msg));
	}

	@Before
	public void setUp()
		throws Exception {
		
		fixture = new SMSNotification();
		fixture.setMigService(mockMigService);
		fixture.setMessageSource(mockMessageSource);
	}
}