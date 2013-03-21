package mobi.nowtechnologies.server.service.aop;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Locale;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.security.NowTechTokenBasedRememberMeServices;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.access.hierarchicalroles.UserDetailsServiceWrapper;

import antlr.Utils;

@RunWith( PowerMockRunner.class )
@PrepareForTest(Utils.class)
public class SMSNotificationTest {
	
	@Mock
	private MigHttpService mockMigService;
	
	@Mock
	private NowTechTokenBasedRememberMeServices mockRememberMeServices;
	
	@Mock
	private CommunityResourceBundleMessageSource mockMessageSource;
	
	private NowTechTokenBasedRememberMeServices rememberMeServices;

	private SMSNotification fixture;

	@Test
	public void testSendLimitedStatusSMS_NotLimitedStatus_Failure()
		throws Exception {
		User user = UserFactory.createUser();
		
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());

		fixture.sendLimitedStatusSMS(user);

		verify(mockMigService, times(0)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testSendLimitedStatusSMS_NotAvailableCommunity_Failure()
		throws Exception {
		User user = UserFactory.createUser();
		user.getStatus().setName(UserStatus.LIMITED.name());
		fixture.setAvailableCommunities("community3, community2, community1");
		
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());

		fixture.sendLimitedStatusSMS(user);

		verify(mockMigService, times(0)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testSendLimitedStatusSMS_Success()
		throws Exception {
		String msg = "message1";
		String title = "O2";
		String paymentsUrl = "https://chartsnow.mobi/web/payments.html";
		String tinyUrlService = "http://tinyurl.com/api-create.php";
		User user = UserFactory.createUser();
		user.getStatus().setName(UserStatus.LIMITED.name());
		user.setUserName("+447989326753");
		user.setToken("8875de694fdc1e69f582672f0ab97a6c");
		Community community = user.getUserGroup().getCommunity();
		community.setRewriteUrlParameter("o2");
		fixture.setAvailableCommunities("community3, community2, community1, "+community.getRewriteUrlParameter());
		fixture.setPaymentsUrl(paymentsUrl);
		fixture.setTinyUrlService(tinyUrlService);
		
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.limited.status.text.for.o2.PAYG"), any(Object[].class), eq(""), eq((Locale)null))).thenReturn(msg);
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null))).thenReturn(title);
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
		
		fixture.sendLimitedStatusSMS(user);

		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.limited.status.text.for."+user.getProvider()+"."+user.getContract()), any(Object[].class), eq(""), eq((Locale)null));
		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null));
		verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
	}
	
	@Test
	public void testSendLimitedStatusSMS_EmptyMsg_Failure()
			throws Exception {
		String msg = "";
		String title = "O2";
		String paymentsUrl = "https://chartsnow.mobi/web/payments.html";
		String tinyUrlService = "http://tinyurl.com/api-create.php";
		User user = UserFactory.createUser();
		user.getStatus().setName(UserStatus.LIMITED.name());
		user.setUserName("+447989326753");
		user.setToken("8875de694fdc1e69f582672f0ab97a6c");
		Community community = user.getUserGroup().getCommunity();
		community.setRewriteUrlParameter("o2");
		fixture.setAvailableCommunities("community3, community2, community1, "+community.getRewriteUrlParameter());
		fixture.setPaymentsUrl(paymentsUrl);
		fixture.setTinyUrlService(tinyUrlService);
		
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.limited.status.text.for.o2.PAYG"), any(Object[].class), eq(""), eq((Locale)null))).thenReturn(msg);
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null))).thenReturn(title);
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
		
		fixture.sendLimitedStatusSMS(user);
		
		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.limited.status.text.for."+user.getProvider()+"."+user.getContract()), any(Object[].class), eq(""), eq((Locale)null));
		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null));
		verify(mockMigService, times(0)).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
	}
	
	@Test
	public void testSendLimitedStatusSMS_WithBadTinyServiceUrl_Success()
		throws Exception {
		String msg = "message1";
		String title = "O2";
		String paymentsUrl = "http://musicqubed.com/web/payments.html";
		String tinyUrlService = "bad url";
		User user = UserFactory.createUser();
		String rememberMeToken = "rememberMeToken";
		user.getStatus().setName(UserStatus.LIMITED.name());
		Community community = user.getUserGroup().getCommunity();
		fixture.setAvailableCommunities("community3, community2, community1, "+community.getRewriteUrlParameter());
		fixture.setPaymentsUrl(paymentsUrl);
		fixture.setTinyUrlService(tinyUrlService);
		
		String url =  paymentsUrl + "?_REMEMBER_ME=" + rememberMeToken+"&community="+community.getRewriteUrlParameter();
		String[] args = {url};
		
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.limited.status.text.for.o2.PAYG"), any(String[].class), eq(""), eq((Locale)null))).thenReturn(msg);
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null))).thenReturn(title);
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), eq(msg), eq(title));

		fixture.sendLimitedStatusSMS(user);

		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.limited.status.text.for."+user.getProvider()+"."+user.getContract()), any(String[].class), eq(""), eq((Locale)null));
		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null));
		verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
	}
	
	@Test
	public void testSendUnsubscribePotentialSMS_NotCurrentPaymentDetails_Failure()
		throws Exception {
		User user = UserFactory.createUser(null, new BigDecimal(0));
		
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());

		fixture.sendUnsubscribePotentialSMS(user);

		verify(mockMigService, times(0)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testSendUnsubscribePotentialSMS_NotAvailableCommunity_Failure()
		throws Exception {
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), new BigDecimal(0));
		fixture.setAvailableCommunities("community3, community2, community1");
		
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());

		fixture.sendUnsubscribePotentialSMS(user);

		verify(mockMigService, times(0)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testSendUnsubscribePotentialSMS_Success()
		throws Exception {
		String msg = "message1";
		String title = "O2";
		String unsubscribeUrl = "http://musicqubed.com/web/payments/unsubscribe.html";
		String tinyUrlService = "http://tinyurl.com/api-create.php?url={url}";
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), new BigDecimal(0));
		Community community = user.getUserGroup().getCommunity();
		fixture.setAvailableCommunities("community3, community2, community1, "+community.getRewriteUrlParameter());
		fixture.setUnsubscribeUrl(unsubscribeUrl);
		fixture.setTinyUrlService(tinyUrlService);
		
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.unsubscribe.potential.text.for.o2.PAYG"), any(Object[].class), eq(""), eq((Locale)null))).thenReturn(msg);
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null))).thenReturn(title);
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
		
		fixture.sendUnsubscribePotentialSMS(user);

		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.unsubscribe.potential.text.for."+user.getProvider()+"."+user.getContract()), any(Object[].class), eq(""), eq((Locale)null));
		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null));
		verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
	}
	
	@Test
	public void testSendUnsubscribePotentialSMS_WithBadTinyServiceUrl_Success()
		throws Exception {
		String msg = "message1";
		String title = "O2";
		String unsubscribeUrl = "http://musicqubed.com/web/payments/unsubscribe.html";
		String tinyUrlService = "bad url";
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), new BigDecimal(0));
		Community community = user.getUserGroup().getCommunity();
		fixture.setAvailableCommunities("community3, community2, community1, "+community.getRewriteUrlParameter());
		fixture.setUnsubscribeUrl(unsubscribeUrl);
		fixture.setTinyUrlService(tinyUrlService);
		String rememberMeToken = rememberMeServices.getRememberMeToken(user.getUserName(), user.getToken());
		
		String url =  unsubscribeUrl + "?community="+community.getRewriteUrlParameter()+"&_REMEMBER_ME=" + rememberMeToken;
		String[] args = {url};
		
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.unsubscribe.potential.text.for.o2.PAYG"), any(String[].class), eq(""), eq((Locale)null))).thenReturn(msg);
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null))).thenReturn(title);
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), eq(msg), eq(title));

		fixture.sendUnsubscribePotentialSMS(user);

		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.unsubscribe.potential.text.for."+user.getProvider()+"."+user.getContract()), any(String[].class), eq(""), eq((Locale)null));
		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null));
		verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
	}
	
	@Test
	public void testSendUnsubscribePotentialSMS_EmptyMsg_Failure()
			throws Exception {
		String msg = "";
		String title = "O2";
		String unsubscribeUrl = "http://musicqubed.com/web/payments/unsubscribe.html";
		String tinyUrlService = "bad url";
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), new BigDecimal(0));
		Community community = user.getUserGroup().getCommunity();
		fixture.setAvailableCommunities("community3, community2, community1, "+community.getRewriteUrlParameter());
		fixture.setUnsubscribeUrl(unsubscribeUrl);
		fixture.setTinyUrlService(tinyUrlService);
		String rememberMeToken = rememberMeServices.getRememberMeToken(user.getUserName(), user.getToken());
		
		String url =  unsubscribeUrl + "?community="+community.getRewriteUrlParameter()+"&_REMEMBER_ME=" + rememberMeToken;
		String[] args = {url};
		
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.unsubscribe.potential.text.for.o2.PAYG"), any(String[].class), eq(""), eq((Locale)null))).thenReturn(msg);
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null))).thenReturn(title);
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
		
		fixture.sendUnsubscribePotentialSMS(user);
		
		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.unsubscribe.potential.text.for."+user.getProvider()+"."+user.getContract()), any(String[].class), eq(""), eq((Locale)null));
		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null));
		verify(mockMigService, times(0)).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
	}
	
	@Test
	public void testSendUnsubscribeAfterSMS_NotCurrentPaymentDetails_Failure()
		throws Exception {
		User user = UserFactory.createUser(null, new BigDecimal(0));
		
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());

		fixture.sendUnsubscribeAfterSMS(user);

		verify(mockMigService, times(0)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testSendUnsubscribeAfterSMS_NotAvailableCommunity_Failure()
		throws Exception {
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), new BigDecimal(0));
		fixture.setAvailableCommunities("community3, community2, community1");
		
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());

		fixture.sendUnsubscribeAfterSMS(user);

		verify(mockMigService, times(0)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testSendUnsubscribeAfterSMS_Success()
		throws Exception {
		String msg = "message1";
		String title = "O2";
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), new BigDecimal(0));
		user.setNextSubPayment((int)(System.currentTimeMillis()/1000+4*24*60*60));
		Community community = user.getUserGroup().getCommunity();
		fixture.setAvailableCommunities("community3, community2, community1, "+community.getRewriteUrlParameter());
		
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.unsubscribe.after.text.for."+user.getProvider()+"."+user.getContract()), any(Object[].class), eq(""), eq((Locale)null))).thenReturn(msg);
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null))).thenReturn(title);
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
		
		fixture.sendUnsubscribeAfterSMS(user);

		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.unsubscribe.after.text.for."+user.getProvider()+"."+user.getContract()), any(Object[].class), eq(""), eq((Locale)null));
		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null));
		verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
	}
	
	@Test
	public void testSendUnsubscribeAfterSMS_EmptyMsg_Failure()
			throws Exception {
		String msg = "";
		String title = "O2";
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), new BigDecimal(0));
		user.setNextSubPayment((int)(System.currentTimeMillis()/1000+4*24*60*60));
		Community community = user.getUserGroup().getCommunity();
		fixture.setAvailableCommunities("community3, community2, community1, "+community.getRewriteUrlParameter());
		
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.unsubscribe.after.text.for."+user.getProvider()+"."+user.getContract()), any(Object[].class), eq(""), eq((Locale)null))).thenReturn(msg);
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null))).thenReturn(title);
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
		
		fixture.sendUnsubscribeAfterSMS(user);
		
		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.unsubscribe.after.text.for."+user.getProvider()+"."+user.getContract()), any(Object[].class), eq(""), eq((Locale)null));
		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null));
		verify(mockMigService, times(0)).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
	}
	
	@Test
	public void testSendLowBalanceWarning_NotCurrentPaymentDetails_Failure()
			throws Exception {
		User user = UserFactory.createUser(null, new BigDecimal(0));
		
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
		
		fixture.sendLowBalanceWarning(user);
		
		verify(mockMigService, times(0)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testSendLowBalanceWarning_NotAvailableCommunity_Failure()
			throws Exception {
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), new BigDecimal(0));
		fixture.setAvailableCommunities("community3, community2, community1");
		
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
		
		fixture.sendLowBalanceWarning(user);
		
		verify(mockMigService, times(0)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testSendLowBalanceWarning_NotPAYGContract_Failure()
			throws Exception {
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), new BigDecimal(0));
		Community community = user.getUserGroup().getCommunity();
		user.setContract(Contract.PAYM);
		fixture.setAvailableCommunities("community3, community2, community1, "+community.getRewriteUrlParameter());
		
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
		
		fixture.sendLowBalanceWarning(user);
		
		verify(mockMigService, times(0)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testSendLowBalanceWarning_NotConsumerContract_Failure()
			throws Exception {
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), new BigDecimal(0));
		Community community = user.getUserGroup().getCommunity();
		user.setSegment(SegmentType.BUSINESS);
		fixture.setAvailableCommunities("community3, community2, community1, "+community.getRewriteUrlParameter());
		
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
		
		fixture.sendLowBalanceWarning(user);
		
		verify(mockMigService, times(0)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testSendLowBalanceWarning_EmptyMsg_Failure()
			throws Exception {
		String msg = "";
		String title = "O2";
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), new BigDecimal(0));
		Community community = user.getUserGroup().getCommunity();
		fixture.setAvailableCommunities("community3, community2, community1, "+community.getRewriteUrlParameter());
		
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.lowBalance.text.for."+user.getProvider()+"."+user.getSegment()+"."+user.getContract()), any(Object[].class), eq(""), eq((Locale)null))).thenReturn(msg);
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null))).thenReturn(title);
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
		
		fixture.sendLowBalanceWarning(user);
		
		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.lowBalance.text.for."+user.getProvider()+"."+user.getSegment()+"."+user.getContract()), any(Object[].class), eq(""), eq((Locale)null));
		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null));
		verify(mockMigService, times(0)).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
	}
	
	@Test
	public void testSendLowBalanceWarning_Success()
			throws Exception {
		String msg = "message1";
		String title = "O2";
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), new BigDecimal(0));
		Community community = user.getUserGroup().getCommunity();
		fixture.setAvailableCommunities("community3, community2, community1, "+community.getRewriteUrlParameter());
		
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.lowBalance.text.for."+user.getProvider()+"."+user.getSegment()+"."+user.getContract()), any(Object[].class), eq(""), eq((Locale)null))).thenReturn(msg);
		when(mockMessageSource.getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null))).thenReturn(title);
		doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
		
		fixture.sendLowBalanceWarning(user);
		
		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.lowBalance.text.for."+user.getProvider()+"."+user.getSegment()+"."+user.getContract()), any(Object[].class), eq(""), eq((Locale)null));
		verify(mockMessageSource, times(1)).getMessage(eq(community.getRewriteUrlParameter()), eq("sms.title"), any(Object[].class), eq((Locale)null));
		verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), eq(msg), eq(title));
	}

	@SuppressWarnings("deprecation")
	@Before
	public void setUp()
		throws Exception {
		rememberMeServices = new NowTechTokenBasedRememberMeServices("web", new UserDetailsServiceWrapper());
		
		fixture = new SMSNotification();
		fixture.setMigService(mockMigService);
		fixture.setMessageSource(mockMessageSource);
		fixture.setRememberMeTokenCookieName("_REMEMBER_ME");
		fixture.setRememberMeServices(rememberMeServices);
        when(mockMessageSource.getMessage(anyString(), anyString(), (Object[])anyObject(), anyString(), any(Locale.class))).thenReturn("");
	}
}