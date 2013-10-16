package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.service.DeviceService;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 
 * Testing to see if we have messages for all users - the idea is to have all user types from app
 * and to see messages sent to migservice
 *
 */
@RunWith(PowerMockRunner.class)
public class UserNotificationServiceImplIT {

	private CommunityResourceBundleMessageSourceImpl messageSource;
	private DeviceService deviceService;
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

        deviceService = new DeviceService();
        deviceService.setMessageSource(messageSource);

		userNotificationService = spy(new UserNotificationServiceImpl());
		userNotificationService.setMessageSource(messageSource);
        userNotificationService.setDeviceService(deviceService);
		userNotificationService.setAvailableCommunities(new String[]{"o2","vf_nz"});
		migHttpService = mock(MigHttpService.class);
		MigResponse migResponse = mock(MigResponse.class);
		
		Mockito.when(migResponse.getHttpStatus()).thenReturn(200);
		Mockito.when(migResponse.getMessage()).thenReturn("000=[GEN] OK ");
		Mockito.when(migResponse.isSuccessful()).thenReturn(true);
        doReturn(migHttpService).when(userNotificationService).getSMSProvider(anyString());
		Mockito.when(migHttpService.send(anyString(), anyString(), anyString())).thenReturn(migResponse);
		
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
			verify(migHttpService, times(times++)).send(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForNonVideo_sendUnsubscribeAfterSMS() throws Exception {
		int times = 1;
		for ( User u : audioOnlyUsers ) {
			userNotificationService.sendUnsubscribeAfterSMS(u);
			verify(migHttpService, times(times++)).send(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForNonVideo_sendUnsubscribePotentialSMS() throws Exception {
		int times = 1;
		for ( User u : audioOnlyUsers ) {
			userNotificationService.sendUnsubscribePotentialSMS(u);
			verify(migHttpService, times(times++)).send(anyString(), anyString(), anyString());
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
			verify(migHttpService, times(times++)).send(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForNonVideo_sendPaymentFailSMS() throws Exception {
		int times = 1;
		for ( User u : audioOnlyUsers ) {
			userNotificationService.sendPaymentFailSMS(createPendingPayment(u));
			verify(migHttpService, times(times++)).send(anyString(), anyString(), anyString());
		}
	}

    @Test
    public void testForNonVideo_sendActivationPinSMS() throws Exception {
        int times = 0;
        for ( User u : audioOnlyUsers ) {
            userNotificationService.sendActivationPinSMS(u);

            if("vf_nz".equals(u.getUserGroup().getCommunity().getRewriteUrlParameter()))
                times++;

            verify(migHttpService, times(times)).send(anyString(), anyString(), anyString());
        }
    }
	
	@Test
	public void testForVideo_sendSmsOnFreeTrialExpired() throws Exception {
		int times = 1;
		for ( User u : videoUsers ) {
			userNotificationService.sendSmsOnFreeTrialExpired(u);
			verify(migHttpService, times(times++)).send(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForVideo_sendUnsubscribeAfterSMS() throws Exception {
		int times = 1;
		for ( User u : videoUsers ) {
			userNotificationService.sendUnsubscribeAfterSMS(u);
			verify(migHttpService, times(times++)).send(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForVideo_sendUnsubscribePotentialSMS() throws Exception {
		int times = 1;
		for ( User u : videoUsers ) {
			userNotificationService.sendUnsubscribePotentialSMS(u);
			verify(migHttpService, times(times++)).send(anyString(), anyString(), anyString());
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
			verify(migHttpService, times(times++)).send(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForVideo_sendPaymentFailSMS() throws Exception {
		int times = 1;
		for ( User u : videoUsers ) {
			userNotificationService.sendPaymentFailSMS(createPendingPayment(u));
			verify(migHttpService, times(times++)).send(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForVideo_send4GDowngradeSMS_freeTrial() throws Exception {
		int times = 1;
		for ( User u : videoUsers ) {
			userNotificationService.send4GDowngradeSMS(u,UserNotificationServiceImpl.DOWNGRADE_FROM_4G_FREETRIAL);
			verify(migHttpService, times(times++)).send(anyString(), anyString(), anyString());
		}
	}
	
	@Test
	public void testForVideo_send4GDowngradeSMS_Subscribed() throws Exception {
		int times = 1;
		for ( User u : videoUsers ) {
			userNotificationService.send4GDowngradeSMS(u,UserNotificationServiceImpl.DOWNGRADE_FROM_4G_SUBSCRIBED);
			verify(migHttpService, times(times++)).send(anyString(), anyString(), anyString());
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
	
	private User createUser(String community, String provider, SegmentType st,Contract c, String device, Tariff tariff,final String paymentType) {
		User user = new User();

		user.setProvider( provider );
		user.setSegment( st );
		user.setContract( c );
        user.setMobile("+642111111111");
		
		Community com = new Community();
		com.setRewriteUrlParameter(community);
		com.setName(community);
		
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
				return paymentType;
			}
		});
		
		user.setTariff(tariff);
		
		return user;
	}

    private User createUser(String provider, SegmentType st,Contract c, String device, Tariff tariff) {

        return createUser("o2", provider, st, c, device, tariff, "o2Psms");
    }
	
	private void addVideoUsers(List<User> ret) {
		//o2 business
		ret.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.NONE, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.ANDROID, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.BLACKBERRY, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.IOS, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.J2ME, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.SYMBIAN, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYG, DeviceType.WINDOWS_PHONE, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYG, DeviceType.NONE, Tariff._4G) );
				
		//o2 PAYM
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.NONE, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.ANDROID, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.BLACKBERRY, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.IOS, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.J2ME, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.SYMBIAN, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.WINDOWS_PHONE, Tariff._4G) );
		
		//o2 PAYG
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.NONE, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.ANDROID, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.BLACKBERRY, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.IOS, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.J2ME, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.SYMBIAN, Tariff._4G) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.WINDOWS_PHONE, Tariff._4G) );
				
		// non-o2
		ret.add( createUser("non-o2", SegmentType.CONSUMER, null, DeviceType.NONE, Tariff._4G) );
		ret.add( createUser("non-o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.ANDROID, Tariff._4G) );
		ret.add( createUser("non-o2", null, null, DeviceType.WINDOWS_PHONE, Tariff._4G) );
		ret.add( createUser("non-o2", null, null, DeviceType.ANDROID, Tariff._4G) );
	}
	
	private void addAudioUsers(List<User> list) {
		//o2 business
		list.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.NONE, null) );
		list.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.ANDROID, null) );
		list.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.BLACKBERRY, null) );
		list.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.IOS, null) );
		list.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.J2ME, null) );
		list.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.SYMBIAN, null) );
		list.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.WINDOWS_PHONE, null) );
		list.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYG, DeviceType.NONE, null) );
		
		//o2 PAYM
		list.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.NONE, null) );
		list.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.ANDROID, null) );
		list.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.BLACKBERRY, null) );
		list.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.IOS, null) );
		list.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.J2ME, null) );
		list.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.SYMBIAN, null) );
		list.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.WINDOWS_PHONE, null) );
		
		//o2 PAYG
		list.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.NONE, null) );
		list.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.ANDROID, null) );
		list.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.BLACKBERRY, null) );
		list.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.IOS, null) );
		list.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.J2ME, null) );
		list.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.SYMBIAN, null) );
		list.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.WINDOWS_PHONE, null) );
		
		// non-o2
		list.add( createUser("non-o2", SegmentType.CONSUMER, null, DeviceType.NONE, null) );
		list.add( createUser("non-o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.ANDROID, null) );
		list.add( createUser("non-o2", null, null, DeviceType.WINDOWS_PHONE, null) );
		list.add( createUser("non-o2", null, null, DeviceType.ANDROID, null) );

		// vf_nz on-net
        list.add( createUser("vf_nz", "on-net", null, null, DeviceType.NONE, null, "vfPSms") );
        list.add( createUser("vf_nz", "on-net", null, null, DeviceType.ANDROID, null, "vfPSms") );
        list.add( createUser("vf_nz", "on-net", null, null, DeviceType.BLACKBERRY, null, "vfPSms") );
        list.add( createUser("vf_nz", "on-net", null, null, DeviceType.IOS, null, "vfPSms") );
        list.add( createUser("vf_nz", "on-net", null, null, DeviceType.J2ME, null, "vfPSms") );
        list.add( createUser("vf_nz", "on-net", null, null, DeviceType.SYMBIAN, null, "vfPSms") );
        list.add( createUser("vf_nz", "on-net", null, null, DeviceType.WINDOWS_PHONE, null, "vfPSms") );

        // vf_nz off-net
        list.add( createUser("vf_nz", "off-net", null, null, DeviceType.NONE, null, "vfPSms") );
        list.add( createUser("vf_nz", "off-net", null, null, DeviceType.ANDROID, null, "vfPSms") );
        list.add( createUser("vf_nz", "off-net", null, null, DeviceType.BLACKBERRY, null, "vfPSms") );
        list.add( createUser("vf_nz", "off-net", null, null, DeviceType.IOS, null, "vfPSms") );
        list.add( createUser("vf_nz", "off-net", null, null, DeviceType.J2ME, null, "vfPSms") );
        list.add( createUser("vf_nz", "off-net", null, null, DeviceType.SYMBIAN, null, "vfPSms") );
        list.add( createUser("vf_nz", "off-net", null, null, DeviceType.WINDOWS_PHONE, null, "vfPSms") );
	}
}
