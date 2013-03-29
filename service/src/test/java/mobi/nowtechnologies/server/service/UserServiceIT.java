package mobi.nowtechnologies.server.service;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.EntityDao;
import mobi.nowtechnologies.server.persistence.dao.UserGroupDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@RunWith(Theories.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class UserServiceIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceIT.class);

	private static final int HOUR_SECONDS = 60 * 60;
	private static final int DAY_SECONDS = 24 * HOUR_SECONDS;
	private static final int TWO_DAY_SECONDS = 2 * DAY_SECONDS;
	private static final int EPOCH_SECONDS = Utils.getEpochSeconds();
	private static final byte chartsNowCommunityId = 3;
	private static final byte o2CommunityId = 7;

	public enum PaymentDetailsType {
		sagePayCreditCard, o2Psms
	}

	public enum Provider {
		o2, non_o2
	}

	public static int count;

	@DataPoints
	public static final byte[] communityIds = new byte[] { chartsNowCommunityId, o2CommunityId };

	@DataPoints
	public static final int[] nextSubPayments = new int[] { EPOCH_SECONDS - DAY_SECONDS, EPOCH_SECONDS + HOUR_SECONDS, EPOCH_SECONDS + DAY_SECONDS, EPOCH_SECONDS + TWO_DAY_SECONDS };

	@DataPoints
	public static final Provider[] providers = Arrays.copyOf(Provider.values(), Provider.values().length + 1);

	@DataPoints
	public static final Contract[] contracts = Arrays.copyOf(Contract.values(), Contract.values().length + 1);

	@DataPoints
	public static final SegmentType[] segments = SegmentType.values();

	@DataPoints
	public static final long[] lastPaymentTryMilliss = new long[] { EPOCH_SECONDS * 1000L };

	@DataPoints
	public static final PaymentDetailsStatus[] lastPaymentStatuss = PaymentDetailsStatus.values();

	@DataPoints
	public static final PaymentDetailsType[] paymentDetailsTypes = PaymentDetailsType.values();
	
	@DataPoints
	public static mobi.nowtechnologies.server.shared.enums.UserStatus[] userStatuses = new mobi.nowtechnologies.server.shared.enums.UserStatus[]{mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED, mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED};

	@DataPoints
	public static User[] users;

	private TestContextManager testContextManager;

	@Resource(name = "service.UserService")
	private UserService userService;

	@Resource(name = "persistence.EntityDao")
	private EntityDao entityDao;
	private PaymentPolicy paymentPolicy;

	@BeforeClass
	public static void generateDataPoints() throws Exception {
		User o2ConsumerUser = new User();
		o2ConsumerUser.setProvider(Provider.o2.name());
		o2ConsumerUser.setSegment(SegmentType.CONSUMER);
		o2ConsumerUser.setContract(Contract.PAYG);

		User o2BussinessUser = new User();
		o2BussinessUser.setProvider(Provider.o2.name());
		o2BussinessUser.setSegment(SegmentType.CONSUMER);
		o2BussinessUser.setContract(Contract.PAYG);

		User o2ConsumerPaymUser = new User();
		o2ConsumerPaymUser.setProvider(Provider.o2.name());
		o2ConsumerPaymUser.setSegment(SegmentType.CONSUMER);
		o2ConsumerPaymUser.setContract(Contract.PAYM);

		User o2BussinessPaygUser = new User();
		o2BussinessPaygUser.setProvider(Provider.o2.name());
		o2BussinessPaygUser.setSegment(SegmentType.CONSUMER);
		o2BussinessPaygUser.setContract(Contract.PAYG);

		User notO2User = new User();
		notO2User.setProvider(Provider.non_o2.name());
		notO2User.setSegment(SegmentType.CONSUMER);
		notO2User.setContract(Contract.PAYG);

		User chartsNowUser = new User();

		users = new User[] { o2ConsumerUser, o2BussinessUser, o2ConsumerPaymUser, o2BussinessPaygUser, notO2User, chartsNowUser };
	}

	@Before
	public void setUpContext() throws Exception {
		this.testContextManager = new TestContextManager(getClass());
		this.testContextManager.prepareTestInstance(this);

		Community community = CommunityDao.getCommunity("CN Commercial Beta");

		paymentPolicy = new PaymentPolicy();
		paymentPolicy.setAvailableInStore(true);
		paymentPolicy.setCommunity(community);
		paymentPolicy.setCurrencyISO("GBP");
		paymentPolicy.setPaymentType(UserRegInfo.PaymentType.CREDIT_CARD);
		paymentPolicy.setSubcost(BigDecimal.TEN);
		paymentPolicy.setSubweeks((byte) 0);
		entityDao.saveEntity(paymentPolicy);

		UserGroup o2UserGroup = UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(o2CommunityId);
		UserGroup chartsNowUserGroup = UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(chartsNowCommunityId);

		if (count == 0) {
			for (int i = 0; i < users.length; i++) {
				User user = users[i];
				if (i < users.length - 1) {
					user.setUserGroup(o2UserGroup);
				} else {
					user.setUserGroup(chartsNowUserGroup);
				}
				user.setDeviceType(DeviceTypeDao.getAndroidDeviceType());
				user.setUserName(UUID.randomUUID().toString());
				user.setLastDeviceLogin(55);
				user.setStatus(UserStatusDao.getLimitedUserStatus());
				entityDao.saveEntity(user);
			}
		}
		count++;
	}

	@Theory
	public void test_getUsersForPendingPayment_Successful(User user, int nextSubPayment, long lastPaymentTryMillis,
			PaymentDetailsStatus lastPaymentStatus, PaymentDetailsType paymentDetailsType,  mobi.nowtechnologies.server.shared.enums.UserStatus userStatus) {

		User expectedUser = prepareTestData(nextSubPayment, user, lastPaymentTryMillis, lastPaymentStatus, paymentDetailsType, userStatus);

		boolean isExpectedUser = isExpectedUser(expectedUser);

		List<User> users = userService.getUsersForPendingPayment();

		assertNotNull(users);
		if (isExpectedUser) {
			LOGGER.info("Expected that users list [{}] contains [{}]", users, expectedUser);
			boolean contains = contains(expectedUser, users);

			assertTrue(contains);
		} else {
			LOGGER.info("Expected that users list [{}] doesn't contain [{}]", users, expectedUser);
			boolean contains = contains(expectedUser, users);

			assertFalse(contains);
		}

	}

	private boolean contains(User user, List<User> users) {
		boolean contains = false;
		for (User userFromList : users) {
			if (userFromList.getId() == user.getId()) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	private boolean isExpectedUser(User user) {
		final boolean isExpectedUser;
		if (user.getUserGroup().getCommunityId() == o2CommunityId) {
			isExpectedUser = isExpectedO2CommunityUser(user);
		} else {
			isExpectedUser = isExpectedNotO2CommunityUser(user);
		}
		return isExpectedUser;
	}

	private boolean isExpectedNotO2CommunityUser(User user) {
		final int currentTimeSeconds = EPOCH_SECONDS;
		final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
		boolean isExpectedNotO2CommunityUser = user.getSubBalance() == 0 && user.getLastDeviceLogin() != 0 && currentPaymentDetails != null && currentPaymentDetails.isActivated() && user.getNextSubPayment()<=currentTimeSeconds
				&& (PaymentDetailsStatus.NONE.equals(currentPaymentDetails.getLastPaymentStatus()) || PaymentDetailsStatus.SUCCESSFUL.equals(currentPaymentDetails.getLastPaymentStatus()));
		return isExpectedNotO2CommunityUser;
	}

	private boolean isExpectedO2CommunityUser(User user) {
		boolean isExpectedO2CommunityUser;

		if ("o2".equals(user.getProvider())) {
			isExpectedO2CommunityUser = isExpectedO2User(user);
		} else {
			isExpectedO2CommunityUser = isExpectedNotO2User(user);
		}
		return isExpectedO2CommunityUser;
	}

	private boolean isExpectedNotO2User(User user) {
		final int nextSubPayment = user.getNextSubPayment();
		final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
		final int currentTimeSeconds = EPOCH_SECONDS;

		boolean isExpectedNotO2User = !"o2".equals(user.getProvider()) && nextSubPayment <= currentTimeSeconds + DAY_SECONDS && currentPaymentDetails.isActivated() && user.getLastDeviceLogin() != 0
				&& (PaymentDetailsStatus.NONE.equals(currentPaymentDetails.getLastPaymentStatus()) || PaymentDetailsStatus.SUCCESSFUL.equals(currentPaymentDetails.getLastPaymentStatus()));

		return isExpectedNotO2User;
	}
	
	
	private boolean isExpectedO2User(User user) {	
		boolean isExpectedO2User;
		if (user.getSegment().equals(SegmentType.CONSUMER)) {
			isExpectedO2User = isExpectedO2ConsumerUser(user);
		} else if (user.getSegment().equals(SegmentType.BUSINESS)){
			isExpectedO2User = isExpectedO2BusinessUser(user);
		}else{
			isExpectedO2User = false;
		}
		
		
		return isExpectedO2User;
	}
	
	private boolean isExpectedO2BusinessUser(User user) {
		final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
		final int nextSubPayment = user.getNextSubPayment();

		final int currentTimeSeconds = EPOCH_SECONDS;
		
		boolean isExpectedO2BussinessUser = SegmentType.BUSINESS.equals(user.getSegment())
				&& (PaymentDetailsStatus.NONE.equals(currentPaymentDetails.getLastPaymentStatus()) || PaymentDetailsStatus.SUCCESSFUL.equals(currentPaymentDetails.getLastPaymentStatus()))
				&& currentPaymentDetails.isActivated()
				&& user.getLastDeviceLogin() != 0
				&& nextSubPayment <= currentTimeSeconds + DAY_SECONDS;
		
		return isExpectedO2BussinessUser;
	}

	private boolean isExpectedO2ConsumerUser(User user) {
		final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
		final int nextSubPayment = user.getNextSubPayment();

		final int currentTimeSeconds = EPOCH_SECONDS;

		boolean isExpectedO2User = SegmentType.CONSUMER.equals(user.getSegment())
				&& (PaymentDetailsStatus.NONE.equals(currentPaymentDetails.getLastPaymentStatus()) || PaymentDetailsStatus.SUCCESSFUL.equals(currentPaymentDetails.getLastPaymentStatus()))
				//&& Contract.PAYG.equals(user.getContract())
				&& currentPaymentDetails.isActivated()
				&& user.getLastDeviceLogin() != 0
				&& currentPaymentDetails.getPaymentType().equals(PaymentDetails.O2_PSMS_TYPE)
				&& nextSubPayment <= currentTimeSeconds + DAY_SECONDS;
		return isExpectedO2User;
	}

	private User prepareTestData(int nextSubPayment, User user, long lastPaymentTryMillis, PaymentDetailsStatus lastPaymentStatus,
			PaymentDetailsType paymentDetailsType, mobi.nowtechnologies.server.shared.enums.UserStatus userStatus) {
		user.setNextSubPayment(nextSubPayment);
		user.setLastPaymentTryInCycleMillis(lastPaymentTryMillis);
		user.setStatus(UserStatusDao.getUserStatusMapUserStatusAsKey().get(userStatus));
		entityDao.updateEntity(user);

		if (lastPaymentStatus != null) {
			final PaymentDetails currentPaymentDetails;
			if (paymentDetailsType == null || paymentDetailsType.equals(PaymentDetailsType.sagePayCreditCard)) {
				currentPaymentDetails = new SagePayCreditCardPaymentDetails();
				currentPaymentDetails.setPaymentPolicy(paymentPolicy);
				currentPaymentDetails.setLastPaymentStatus(lastPaymentStatus);
				((SagePayCreditCardPaymentDetails) currentPaymentDetails).setReleased(false);
				currentPaymentDetails.setActivated(true);
				currentPaymentDetails.setOwner(user);
			} else if (paymentDetailsType.equals(PaymentDetailsType.o2Psms)) {
				currentPaymentDetails = new O2PSMSPaymentDetails();
				currentPaymentDetails.setPaymentPolicy(paymentPolicy);
				currentPaymentDetails.setLastPaymentStatus(lastPaymentStatus);
				currentPaymentDetails.setActivated(true);
				currentPaymentDetails.setOwner(user);
			} else {
				throw new RuntimeException("Unknown paymentDetailsType: [" + paymentDetailsType + "]");
			}
			entityDao.saveEntity(currentPaymentDetails);

			user.setCurrentPaymentDetails(currentPaymentDetails);
			user = entityDao.updateEntity(user);
		}

		return user;
	}

}
