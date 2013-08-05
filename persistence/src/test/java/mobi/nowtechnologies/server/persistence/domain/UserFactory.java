package mobi.nowtechnologies.server.persistence.domain;

import static mobi.nowtechnologies.server.persistence.domain.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;

import java.math.BigDecimal;
import java.util.*;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import mobi.nowtechnologies.server.shared.enums.UserType;


public class UserFactory
 {
	private UserFactory() {
	}

	public static Collection<User> getUserCollection() {
		Collection<User> users = new ArrayList<User>(1);
		
		users.add(createUser());
		return users;
	}
	
	public static List<User> getUserUnmodifableList() {
		Collection<User> users = getUserCollection();
		
		return Collections.unmodifiableList(new ArrayList<User>(users));
	}

	
	public static Collection<User> getUserUnmodifableCollection() {
		Collection<User> users = getUserCollection();
		
		users = Collections.unmodifiableCollection(users);
		return users;
	}


	public static User createUser() {
		
		UserStatus userStatus = new UserStatus();
		userStatus.setI((byte)10);
		userStatus.setName(UserStatusDao.SUBSCRIBED);
		
		PaymentStatus paymentStatus = new PaymentStatus();
		paymentStatus.setId(2);
		
		DeviceType deviceType = new DeviceType();
		deviceType.setName("IOS");
		deviceType.setI((byte)5);
		
		Community community = CommunityFactory.createCommunity();
		UserGroup userGroup = new UserGroup();
		userGroup.setI((byte)7);
		userGroup.setCommunity(community);
		
		User testUser= new User();
		testUser.setAddress1("678");
		testUser.setAddress2("");
		testUser.setCanContact(true);
		testUser.setCity("St.Albans");
		testUser.setCode("f72b0b018fed801932f97f3e3a26b23f");
		testUser.setCountry(1);
		testUser.setDevice("HTC HERO");
		testUser.setDeviceString("iPhone");
		testUser.setDeviceType(deviceType);
		testUser.setDisplayName("Nigel");
		testUser.setFirstName("Nigel");
		testUser.setIpAddress("217.35.32.182");
		testUser.setLastDeviceLogin(1306902146);
		testUser.setLastName("Rees");
		testUser.setLastPaymentTx(72);
		testUser.setLastWebLogin(1306873638);
		testUser.setMobile("+447770608575");
		testUser.setNextSubPayment(1307219588);
		testUser.setPostcode("412");
		testUser.setSessionID("attg0vs3e98dsddc2a4k9vdkc6");
		testUser.setStatus(userStatus);
		testUser.setSubBalance((byte) 5);
		testUser.setTempToken("NONE");
		testUser.setTitle("Mr");
		testUser.setToken("26b34b31237dfffb4caeb9518ad1ce02");
		testUser.setUserGroup(userGroup);
		testUser.setUserName("test_getListOfUsersForUpdate@rbt.com");
		testUser.setUserType(UserType.NORMAL);
		testUser.setPaymentType(UserRegInfo.PaymentType.UNKNOWN);
		testUser.setPin("pin");
		testUser.setPaymentStatus(paymentStatus.getId());
		testUser.setProvider("o2");
		testUser.setContract(PAYG);
		testUser.setSegment(CONSUMER);
		return testUser;
	}


	public static User createUserByDefaultConstructor() {
		return new User();
	}
	
	public static User createUser(PaymentDetails currentPaymentDetails, BigDecimal amountOfMoneyToUserNotification) {
		User user = createUser();
		user.setCurrentPaymentDetails(currentPaymentDetails);
		user.setAmountOfMoneyToUserNotification(amountOfMoneyToUserNotification);
		
		return user;
	}
	
	public static User createUser(PaymentDetails currentPaymentDetails, BigDecimal amountOfMoneyToUserNotification, UserGroup userGroup) {
		User user = createUser();
		user.setCurrentPaymentDetails(currentPaymentDetails);
		user.setAmountOfMoneyToUserNotification(amountOfMoneyToUserNotification);
		user.setUserGroup(userGroup);
		
		return user;
	}

     public static User createUserWithVideoPaymentDetails(Tariff subscribedUserTariff) {
         PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy(subscribedUserTariff);
         paymentPolicy.setContentCategory("");

         PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
         paymentDetails.setPaymentPolicy(paymentPolicy);

         User user = createUser();
         user.setCurrentPaymentDetails(paymentDetails);

         return user;
     }
}
