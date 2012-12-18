package mobi.nowtechnologies.server.service.aop;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.service.*;
import mobi.nowtechnologies.server.shared.dto.web.payment.CreditCardDto;
import mobi.nowtechnologies.server.shared.enums.UserStatus;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/shared.xml", "/META-INF/dao-test.xml", "/META-INF/service-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@SuppressWarnings("deprecation")
public class SMSNotificationIT {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@Autowired
	private SMSNotification fixture;
	
	@Autowired
	@Qualifier("service.WeeklyUpdateService")
	private WeeklyUpdateService weeklyUpdateService;
	
	@Autowired
	@Qualifier("service.SpyPaymentDetailsService")
	private PaymentDetailsService paymentDetailsService;
	
	private MigService mockMigService;
	
	private UserService mockUserService;
	
	@Test
	public void testSendLimitedStatusSMS_Success()
			throws Exception {		
		User user = UserFactory.createUser();
		user.getStatus().setName(UserStatus.LIMITED.name());
		user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");
		
		Mockito.doNothing().when(weeklyUpdateService).saveWeeklyPayment(any(User.class));
		Mockito.doReturn(null).when(mockMigService).sendFreeSms(anyString(), anyInt(), anyString(), anyString());
		
		weeklyUpdateService.saveWeeklyPayment(user);
		
		verify(mockMigService, times(1)).sendFreeSms(anyString(), anyInt(), anyString(), anyString());
	}
	
	@Test
	public void testSendUnsubscribePotentialSMS_afterCreatedCreditCardPaymentDetails_Success()
			throws Exception {		
		CreditCardDto creditCardDto = new CreditCardDto();
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), null);
		user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");
		
		Mockito.doReturn(null).when(paymentDetailsService).createCreditCardPamentDetails(any(CreditCardDto.class), anyString(), anyInt());
		Mockito.doReturn(null).when(mockMigService).sendFreeSms(anyString(), anyInt(), anyString(), anyString());
		Mockito.doReturn(user).when(mockUserService).findById(anyInt());
		
		paymentDetailsService.createCreditCardPamentDetails(creditCardDto, "O2", user.getId());
		
		verify(mockMigService, times(1)).sendFreeSms(anyString(), anyInt(), anyString(), anyString());
	}

	@Test
	public void testSendUnsubscribePotentialSMS_afterCreatedPayPalPaymentDetails_Success()
			throws Exception {		
		User user = UserFactory.createUser(new PayPalPaymentDetails(), null);
		user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");
		
		Mockito.doReturn(null).when(paymentDetailsService).commitPayPalPaymentDetails(anyString(), anyString(), anyInt());
		Mockito.doReturn(null).when(mockMigService).sendFreeSms(anyString(), anyInt(), anyString(), anyString());
		Mockito.doReturn(user).when(mockUserService).findById(anyInt());
		
		paymentDetailsService.commitPayPalPaymentDetails("xxxxxxxxxxxxxxxxx", "O2", user.getId());
		
		verify(mockMigService, times(1)).sendFreeSms(anyString(), anyInt(), anyString(), anyString());
	}
	
	@Test
	public void testSendUnsubscribePotentialSMS_afterCreatedMigPaymentDetails_Success()
			throws Exception {		
		User user = UserFactory.createUser(new MigPaymentDetails(), null);
		user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");
		
		Mockito.doReturn(null).when(paymentDetailsService).commitMigPaymentDetails(anyString(), anyInt());
		Mockito.doReturn(null).when(mockMigService).sendFreeSms(anyString(), anyInt(), anyString(), anyString());
		Mockito.doReturn(user).when(mockUserService).findById(anyInt());
		
		paymentDetailsService.commitMigPaymentDetails("xxxxxxxxxxxxxxxxx", user.getId());
		
		verify(mockMigService, times(1)).sendFreeSms(anyString(), anyInt(), anyString(), anyString());
	}

	@Before
	public void setUp()
			throws Exception {
		mockMigService = mock(MigService.class);
		mockUserService = mock(UserService.class);
		
		fixture.setMigService(mockMigService);
		fixture.setUserService(mockUserService);
	}
}