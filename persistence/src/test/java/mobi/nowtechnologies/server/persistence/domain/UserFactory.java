package mobi.nowtechnologies.server.persistence.domain;

import java.math.BigDecimal;
import java.util.*;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.shared.enums.UserType;


/**
 * The class <code>UserFactory</code> implements static methods that return instances of the class <code>{@link User}</code>.
 *
 * @generatedBy CodePro at 21.08.12 10:58
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
public class UserFactory
 {
	/**
	 * Prevent creation of instances of this class.
	 *
	 * @generatedBy CodePro at 21.08.12 10:58
	 */
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


	/**
	 * Create an instance of the class <code>{@link User}</code>.
	 *
	 * @generatedBy CodePro at 21.08.12 10:58
	 */
	public static User createUser() {
		
//		DeviceType deviceType = new DeviceType();
//		deviceType.setI((byte)5);
//		
//		UserStatus userStatus = new UserStatus();
//		userStatus.setI((byte)10);
//		userStatus.setName(UserStatusDao.SUBSCRIBED);
//
//		Community community = CommunityFactory.createCommunity();
//		UserGroup userGroup = new UserGroup();
//		userGroup.setI((byte)7);
//		userGroup.setCommunity(community);
//		
//		User user = new User();
//		
//		user.setAddress1("678");
//		user.setAddress2("");
//		user.setCanContact(true);
//		user.setCity("St.Albans");
//		user.setCode("f72b0b018fed801932f97f3e3a26b23f");
//		user.setConformStoredToken("conformStoredToken");
//		user.setCountry(1);
//		user.setCountryIdString("25");
//		user.setCurrentPaymentDetails(null);
//		user.setDevice("HTC HERO");
//		user.setDeviceModel("deviceModel");
//		user.setDeviceString("Android");
//		user.setDeviceType(deviceType);
//		user.setDeviceUID("deviceUID");
//		user.setDisplayName("Nigel");
//		user.setDrms(null);
//		user.setFacebookId("facebookId");
//		user.setFirstDeviceLoginMillis(654564L);
//		user.setFirstName("Nigel");
//		user.setFirstUserLoginMillis(65545646L);
//		user.setId(56);
//		user.setIpAddress("217.35.32.182");
//		user.setLastDeviceLogin(1306902146);
//		user.setLastName("Rees");
//		user.setLastPaymentTx(72);
//		user.setLastSuccessfulPaymentTimeMillis(646574L);
//		user.setLastWebLogin(1306873638);
//		user.setMobile("+447770608575");
//		user.setNewStoredToken("newStoredToken");
//		user.setNextSubPayment(1307219588);
//		user.setNumPsmsRetries(2);
//		user.setOperator(2);
//		user.setPaymentDetailsList(null);
//		user.setPaymentEnabled(true);
//		user.setPaymentStatus(12);
//		user.setPaymentType("UNKNOWN");
//		user.setPin("pin");
//		user.setPostcode("412");
//		user.setPotentialPromoCodePromotion(null);
//		user.setPotentialPromotion(null);
//		user.setSessionID("attg0vs3e98dsddc2a4k9vdkc6");
//		user.setStatus(userStatus);
//		user.setSubBalance((byte) 5);
//		user.setTempToken("NONE");
//		user.setTitle("Mr");
//		user.setToken("26b34b31237dfffb4caeb9518ad1ce02");
//		user.setUserGroup(userGroup);
//		user.setUserName("test_getListOfUsersForUpdate@rbt.com");
//		user.setUserType(UserType.NORMAL);
		UserStatus userStatus = new UserStatus();
		userStatus.setI((byte)10);
		userStatus.setName(UserStatusDao.SUBSCRIBED);
		
		PaymentStatus paymentStatus = new PaymentStatus();
		paymentStatus.setId(2);
		
		DeviceType deviceType = new DeviceType();
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
		testUser.setPaymentEnabled(true);
		
		testUser.setProvider("o2");
		testUser.setContract("PAYG");
		testUser.setSegment("consumer");
		return testUser;
	}


	/**
	 * Create an instance of the class <code>{@link User}</code>.
	 *
	 * @generatedBy CodePro at 21.08.12 10:58
	 */
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
}