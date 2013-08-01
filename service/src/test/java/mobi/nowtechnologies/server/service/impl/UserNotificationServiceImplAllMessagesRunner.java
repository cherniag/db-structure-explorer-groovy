package mobi.nowtechnologies.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSourceImpl;
import mobi.nowtechnologies.server.shared.service.PostService;

/**
 *
 * This is just a runner class to see what messages are sent to different user types
 * To have a cleaner output, disable logging
 *
 */
public class UserNotificationServiceImplAllMessagesRunner {

	public static void main(String[] args) throws Exception {
		System.out.println();
		
		UserNotificationServiceImpl userNotificationServiceImpl = new UserNotificationServiceImpl();
		CommunityResourceBundleMessageSourceImpl ms = new CommunityResourceBundleMessageSourceImpl();
		ms.setBasename("classpath:services_test");
		userNotificationServiceImpl.setMessageSource( ms );
		userNotificationServiceImpl.setMigHttpService( null );
		userNotificationServiceImpl.setAvailableCommunities(new String[]{"o2"});
		userNotificationServiceImpl.setMigHttpService(new MigHttpService(){
			@Override
			public MigResponse makeFreeSMSRequest(String numbers,
					String message, String title) {
				System.out.println(title + " - " + message);
				PostService.Response res = new PostService.Response();
				res.setMessage("000=[GEN] OK ");
				res.setStatusCode(200);
				return new MigResponse(res);
			}
		});
		
		for ( User u : getAllUserTypes() ) {
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("xxxxx    " + u.getProvider() + " | " + u.getSegment() + " | " + u.getContract() + " | " + u.getDeviceTypeIdString() + "     xxxxxxxxxxxxxxx");
			
			System.out.println("sendLowBalanceWarning:");
			userNotificationServiceImpl.sendLowBalanceWarning(u);
			System.out.println();

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
			System.out.println("sendPaymentFailSMS:");
			userNotificationServiceImpl.sendPaymentFailSMS(pp);
			System.out.println();
			
			System.out.println("sendSmsOnFreeTrialExpired:");
			userNotificationServiceImpl.sendSmsOnFreeTrialExpired(u);
			System.out.println();
			
			System.out.println("sendUnsubscribeAfterSMS");
			userNotificationServiceImpl.sendUnsubscribeAfterSMS(u);
			System.out.println();
			
			System.out.println("sendUnsubscribePotentialSMS");
			userNotificationServiceImpl.sendUnsubscribePotentialSMS(u);
			
//			userNotificationServiceImpl.send4GDowngradeSMS(u, UserNotificationServiceImpl.DOWNGRADE_FROM_4G_FREETRIAL);
//			userNotificationServiceImpl.send4GDowngradeSMS(u, UserNotificationServiceImpl.DOWNGRADE_FROM_4G_SUBSCRIBED);
		}
		
	}
	
	private static List<User> getAllUserTypes() {
		List<User> ret = new ArrayList<User>();
		
		//*
		//o2 business
		ret.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.NONE, null) );
		ret.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.ANDROID, null) );
		ret.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.BLACKBERRY, null) );
		ret.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.IOS, null) );
		ret.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.J2ME, null) );
		ret.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.SYMBIAN, null) );
		ret.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYM, DeviceType.WINDOWS_PHONE, null) );
		ret.add( createUser("o2", SegmentType.BUSINESS, Contract.PAYG, DeviceType.NONE, null) );
		
		//o2 PAYM
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.NONE, null) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.ANDROID, null) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.BLACKBERRY, null) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.IOS, null) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.J2ME, null) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.SYMBIAN, null) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYM, DeviceType.WINDOWS_PHONE, null) );
		
		//o2 PAYG
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.NONE, null) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.ANDROID, null) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.BLACKBERRY, null) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.IOS, null) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.J2ME, null) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.SYMBIAN, null) );
		ret.add( createUser("o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.WINDOWS_PHONE, null) );
		
		// non-o2
		ret.add( createUser("non-o2", SegmentType.CONSUMER, null, DeviceType.NONE, null) );
		ret.add( createUser("non-o2", SegmentType.CONSUMER, Contract.PAYG, DeviceType.ANDROID, null) );
		ret.add( createUser("non-o2", null, null, DeviceType.WINDOWS_PHONE, null) );
		ret.add( createUser("non-o2", null, null, DeviceType.ANDROID, null) );
		//*/
		
		/*
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
		*/
		
		return ret;
	}
	
	private static User createUser(String provider, SegmentType st,Contract c, String device, Tariff tariff) {
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
}
