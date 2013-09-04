package mobi.nowtechnologies.server.service.impl;

import static mobi.nowtechnologies.server.persistence.domain.DeviceType.*;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSourceImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * 
 * Testing to see if we have messages for all users - the idea is to have all user types from app
 * and to see messages sent to migservice
 *
 */
@RunWith(PowerMockRunner.class)
public class UserNotificationServiceImplIT {

	private CommunityResourceBundleMessageSourceImpl messageSource;
	private UserNotificationServiceImpl userNotificationService;
	private List<User> audioOnlyUsers;
	private List<User> videoUsers;
	private MigHttpService migHttpService;
	
	@Before
	public void setUp() {
		messageSource = new CommunityResourceBundleMessageSourceImpl();
		messageSource.setBasenames(new String[] { "classpath:services_test" });
		messageSource.setDefaultEncoding("utf8");
		messageSource.setCacheSeconds(180);
		messageSource.setUseCodeAsDefaultMessage(true);
		
		userNotificationService = new UserNotificationServiceImpl();
		userNotificationService.setMessageSource(messageSource);
		userNotificationService.setAvailableCommunities(new String[]{"o2"});
		migHttpService = mock(MigHttpService.class);
		userNotificationService.setMigHttpService(migHttpService);
		MigResponse migResponse = mock(MigResponse.class);
		
		Mockito.when(migResponse.getHttpStatus()).thenReturn(200);
		Mockito.when(migResponse.getMessage()).thenReturn("000=[GEN] OK ");
		Mockito.when(migResponse.isSuccessful()).thenReturn(true);
		Mockito.when(migHttpService.makeFreeSMSRequest(anyString(), anyString(), anyString())).thenReturn(migResponse);
		
		audioOnlyUsers = new ArrayList<User>();
		addAudioUsers( audioOnlyUsers );
		
		videoUsers = new ArrayList<User>();
		addVideoUsers( videoUsers );
	}
	
	@Test
	public void testForNonVideo_sendSmsOnFreeTrialExpired() throws Exception {
		int times = 1;
		for ( User u : audioOnlyUsers ) {
			userNotificationService.sendSmsOnFreeTrialExpired(u);
			verify(migHttpService, times(times++)).makeFreeSMSRequest(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForNonVideo_sendUnsubscribeAfterSMS() throws Exception {
		int times = 1;
		for ( User u : audioOnlyUsers ) {
			userNotificationService.sendUnsubscribeAfterSMS(u);
			verify(migHttpService, times(times++)).makeFreeSMSRequest(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForNonVideo_sendUnsubscribePotentialSMS() throws Exception {
		int times = 1;
		for ( User u : audioOnlyUsers ) {
			userNotificationService.sendUnsubscribePotentialSMS(u);
			verify(migHttpService, times(times++)).makeFreeSMSRequest(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForNonVideo_sendLowBalanceWarning() throws Exception {
		int times = 1;
		for ( User u : audioOnlyUsers ) {
			if ( !u.isO2PAYGConsumer() ) {
				continue;
			}
			userNotificationService.sendLowBalanceWarning(u);
			verify(migHttpService, times(times++)).makeFreeSMSRequest(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForNonVideo_sendPaymentFailSMS() throws Exception {
		int times = 1;
		for ( User u : audioOnlyUsers ) {
			userNotificationService.sendPaymentFailSMS(createPendingPayment(u));
			verify(migHttpService, times(times++)).makeFreeSMSRequest(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForVideo_sendSmsOnFreeTrialExpired() throws Exception {
		int times = 1;
		for ( User u : videoUsers ) {
			userNotificationService.sendSmsOnFreeTrialExpired(u);
			verify(migHttpService, times(times++)).makeFreeSMSRequest(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForVideo_sendUnsubscribeAfterSMS() throws Exception {
		int times = 1;
		for ( User u : videoUsers ) {
			userNotificationService.sendUnsubscribeAfterSMS(u);
			verify(migHttpService, times(times++)).makeFreeSMSRequest(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForVideo_sendUnsubscribePotentialSMS() throws Exception {
		int times = 1;
		for ( User u : videoUsers ) {
			userNotificationService.sendUnsubscribePotentialSMS(u);
			verify(migHttpService, times(times++)).makeFreeSMSRequest(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForVideo_sendLowBalanceWarning() throws Exception {
		int times = 1;
		for ( User u : videoUsers ) {
			if ( !u.isO2PAYGConsumer() ) {
				continue;
			}
			userNotificationService.sendLowBalanceWarning(u);
			verify(migHttpService, times(times++)).makeFreeSMSRequest(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForVideo_sendPaymentFailSMS() throws Exception {
		int times = 1;
		for ( User u : videoUsers ) {
			userNotificationService.sendPaymentFailSMS(createPendingPayment(u));
			verify(migHttpService, times(times++)).makeFreeSMSRequest(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForVideo_send4GDowngradeSMS_freeTrial() throws Exception {
		int times = 1;
		for ( User u : videoUsers ) {
			userNotificationService.send4GDowngradeSMS(u,UserNotificationServiceImpl.DOWNGRADE_FROM_4G_FREETRIAL);
			verify(migHttpService, times(times++)).makeFreeSMSRequest(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForVideo_send4GDowngradeSMS_Subscribed() throws Exception {
		int times = 1;
		for ( User u : videoUsers ) {
			userNotificationService.send4GDowngradeSMS(u,UserNotificationServiceImpl.DOWNGRADE_FROM_4G_SUBSCRIBED);
			verify(migHttpService, times(times++)).makeFreeSMSRequest(anyString(), anyString(), anyString());
		}
	}
	
	private PendingPayment createPendingPayment(User u) {
		PendingPayment pp = new PendingPayment();
		pp.setUser(u);
		PaymentDetails paymentDetails = new PaymentDetails() {
			@Override
			public String getPaymentType() {
				return null;
			}
			@Override
			public int getMadeRetries() {
				return 0;
			}
			@Override
			public int getRetriesOnError() {
				return 0;
			}
		};
		pp.setPaymentDetails(paymentDetails);
		
		return pp;
	}
	
	private User createUser(ProviderType provider, SegmentType st,Contract c, String device, Tariff tariff) {
		User user = new User();

		user.setProvider( provider );
		user.setSegment( st );
		user.setContract( c );
		
		Community com = new Community();
		com.setRewriteUrlParameter("o2");
		com.setName("o2");
		
		UserGroup ug = new UserGroup();
		ug.setCommunity(com);
		user.setUserGroup(ug);
		
		DeviceType dt = new DeviceType();
		dt.setName(device);
		user.setDeviceType(dt);
		
		UserStatus us = new UserStatus();
		us.setName("LIMITED");
		user.setStatus(us);
		
		user.setCurrentPaymentDetails(new PaymentDetails() {
			@Override
			public String getPaymentType() {
				return "o2Psms";
			}
		});
		
		user.setTariff(tariff);
		
		return user;
	}
	
	private void addVideoUsers(List<User> ret) {
		//o2 business
		ret.add( createUser(O2, SegmentType.BUSINESS, Contract.PAYM, NONE, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.BUSINESS, Contract.PAYM, ANDROID, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.BUSINESS, Contract.PAYM, BLACKBERRY, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.BUSINESS, Contract.PAYM, IOS, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.BUSINESS, Contract.PAYM, J2ME, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.BUSINESS, Contract.PAYM, SYMBIAN, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.BUSINESS, Contract.PAYG, WINDOWS_PHONE, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.BUSINESS, Contract.PAYG, NONE, Tariff._4G) );
				
		//o2 PAYM
		ret.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYM, NONE, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYM, ANDROID, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYM, BLACKBERRY, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYM, IOS, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYM, J2ME, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYM, SYMBIAN, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYM, WINDOWS_PHONE, Tariff._4G) );
		
		//o2 PAYG
		ret.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYG, NONE, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYG, ANDROID, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYG, BLACKBERRY, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYG, IOS, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYG, J2ME, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYG, SYMBIAN, Tariff._4G) );
		ret.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYG, WINDOWS_PHONE, Tariff._4G) );
				
		// non-o2
		ret.add( createUser(NON_O2, SegmentType.CONSUMER, null, NONE, Tariff._4G) );
		ret.add( createUser(NON_O2, SegmentType.CONSUMER, Contract.PAYG, ANDROID, Tariff._4G) );
		ret.add( createUser(NON_O2, null, null, WINDOWS_PHONE, Tariff._4G) );
		ret.add( createUser(NON_O2, null, null, ANDROID, Tariff._4G) );
	}
	
	private void addAudioUsers(List<User> list) {
		//o2 business
		list.add( createUser(O2, SegmentType.BUSINESS, Contract.PAYM, NONE, null) );
		list.add( createUser(O2, SegmentType.BUSINESS, Contract.PAYM, ANDROID, null) );
		list.add( createUser(O2, SegmentType.BUSINESS, Contract.PAYM, BLACKBERRY, null) );
		list.add( createUser(O2, SegmentType.BUSINESS, Contract.PAYM, IOS, null) );
		list.add( createUser(O2, SegmentType.BUSINESS, Contract.PAYM, J2ME, null) );
		list.add( createUser(O2, SegmentType.BUSINESS, Contract.PAYM, SYMBIAN, null) );
		list.add( createUser(O2, SegmentType.BUSINESS, Contract.PAYM, WINDOWS_PHONE, null) );
		list.add( createUser(O2, SegmentType.BUSINESS, Contract.PAYG, NONE, null) );
		
		//o2 PAYM
		list.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYM, NONE, null) );
		list.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYM, ANDROID, null) );
		list.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYM, BLACKBERRY, null) );
		list.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYM, IOS, null) );
		list.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYM, J2ME, null) );
		list.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYM, SYMBIAN, null) );
		list.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYM, WINDOWS_PHONE, null) );
		
		//o2 PAYG
		list.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYG, NONE, null) );
		list.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYG, ANDROID, null) );
		list.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYG, BLACKBERRY, null) );
		list.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYG, IOS, null) );
		list.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYG, J2ME, null) );
		list.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYG, SYMBIAN, null) );
		list.add( createUser(O2, SegmentType.CONSUMER, Contract.PAYG, WINDOWS_PHONE, null) );
		
		// non-o2
		list.add( createUser(NON_O2, SegmentType.CONSUMER, null, NONE, null) );
		list.add( createUser(NON_O2, SegmentType.CONSUMER, Contract.PAYG, ANDROID, null) );
		list.add( createUser(NON_O2, null, null, WINDOWS_PHONE, null) );
		list.add( createUser(NON_O2, null, null, ANDROID, null) );
	}
}
