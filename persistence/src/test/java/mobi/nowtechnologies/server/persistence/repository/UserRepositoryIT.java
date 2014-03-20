package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.dao.UserGroupDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

import static mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory.paymentPolicyWithDefaultNotNullFields;
import static mobi.nowtechnologies.server.persistence.domain.enums.UserLogStatus.SUCCESS;
import static mobi.nowtechnologies.server.persistence.domain.enums.UserLogType.UPDATE_O2_USER;
import static mobi.nowtechnologies.server.persistence.domain.enums.UserLogType.VALIDATE_PHONE_NUMBER;
import static mobi.nowtechnologies.server.shared.Utils.*;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.ERROR;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.NONE;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class UserRepositoryIT {
	
	private static final int HOUR_SECONDS = 60 * 60;
	private static final int DAY_SECONDS = 24 * HOUR_SECONDS;
	private static final int TWO_DAY_SECONDS = 2 * DAY_SECONDS;
	private static final int o2CommunityId = 7;

	@Resource(name = "userRepository")
	private UserRepository userRepository;

	@Resource(name = "userLogRepository")
	private UserLogRepository userLogRepository;
	
	@Resource(name = "paymentDetailsRepository")
	private PaymentDetailsRepository paymentDetailsRepository;

	@Resource(name = "paymentPolicyRepository")
	private PaymentPolicyRepository paymentPolicyRepository;

    @Test
    public void testFindByMobile(){
        String phoneNumber = "+64279000456";

        List<User> list = userRepository.findByMobile(phoneNumber);

        assertEquals(1, list.size());
        assertEquals(phoneNumber, list.get(0).getMobile());
    }

	@Test
	@Rollback
	public void testFindBefore48hExpireUsers() throws Exception {
		final int epochSeconds = getEpochSeconds();

		User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
		testUser.setLastBefore48SmsMillis(0);
		testUser.setNextSubPayment(epochSeconds + TWO_DAY_SECONDS);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());

		testUser = userRepository.save(testUser);

		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);

		Pageable pageable = new PageRequest(0, 1);
		List<User> users = userRepository.findBefore48hExpireUsers(epochSeconds, pageable);

		assertNotNull(users);
		assertEquals(1, users.size());
		assertEquals(users.get(0).getId(), testUser.getId());
	}
	
	@Test
	@Rollback
	public void testFindBefore48hExpireUsers_InActivePaymentDetails() throws Exception {
		final int epochSeconds = getEpochSeconds();

		User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
		testUser.setLastBefore48SmsMillis(0);
		testUser.setNextSubPayment(epochSeconds + TWO_DAY_SECONDS);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());

		testUser = userRepository.save(testUser);

		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(false);
		currentO2PaymentDetails.setOwner(testUser);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);

		Pageable pageable = new PageRequest(0, 1);
		List<User> users = userRepository.findBefore48hExpireUsers(epochSeconds, pageable);

		assertNotNull(users);
		assertEquals(0, users.size());
	}
	
	@Test
	@Rollback
	public void testFindBefore48hExpireUsers_LastBefore48SmsMillisAfter48() throws Exception {
		final int epochSeconds = getEpochSeconds();
		final int nextSubPaymentSeconds = epochSeconds + DAY_SECONDS;

		User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
		testUser.setLastBefore48SmsMillis((nextSubPaymentSeconds-10)*1000L);
		testUser.setNextSubPayment(nextSubPaymentSeconds);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());

		testUser = userRepository.save(testUser);

		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);

		Pageable pageable = new PageRequest(0, 1);
		List<User> users = userRepository.findBefore48hExpireUsers(epochSeconds, pageable);

		assertNotNull(users);
		assertEquals(0, users.size());
	}
	
	@Test
	@Rollback
	public void testFindBefore48hExpireUsers_NextSubPaymentAtThreeDays() throws Exception {
		final int epochSeconds = getEpochSeconds();

		User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
		testUser.setLastBefore48SmsMillis(0);
		testUser.setNextSubPayment(epochSeconds + 3*DAY_SECONDS);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());

		testUser = userRepository.save(testUser);

		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);

		Pageable pageable = new PageRequest(0, 1);
		List<User> users = userRepository.findBefore48hExpireUsers(epochSeconds, pageable);

		assertNotNull(users);
		assertEquals(0, users.size());
	}
	
	@Test
	@Rollback
	public void testFindBefore48hExpireUsers_NextSubPaymentAtDay() throws Exception {
		final int epochSeconds = getEpochSeconds();

		User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
		testUser.setLastBefore48SmsMillis(0);
		testUser.setNextSubPayment(epochSeconds + DAY_SECONDS);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());

		testUser = userRepository.save(testUser);

		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);

		Pageable pageable = new PageRequest(0, 1);
		List<User> users = userRepository.findBefore48hExpireUsers(epochSeconds, pageable);

		assertNotNull(users);
		assertEquals(1, users.size());
		assertEquals(users.get(0).getId(), testUser.getId());
	}
	
	@Test
	@Rollback
	public void testFindBefore48hExpireUsers_NextSubPaymentNow() throws Exception {
		final int epochSeconds = getEpochSeconds();

		User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
		testUser.setLastBefore48SmsMillis(0);
		testUser.setNextSubPayment(epochSeconds);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());

		testUser = userRepository.save(testUser);

		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);

		Pageable pageable = new PageRequest(0, 1);
		List<User> users = userRepository.findBefore48hExpireUsers(epochSeconds, pageable);

		assertNotNull(users);
		assertEquals(0, users.size());
	}
	
	@Test
	public void testUpdateLastBefore48SmsMillis_Success() throws Exception {
		long newLastBefore48SmsMillis = 10L;

		User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
		testUser.setLastBefore48SmsMillis(Long.MIN_VALUE);
		
		testUser = userRepository.save(testUser);
		
		int updatedCount = userRepository.updateLastBefore48SmsMillis(newLastBefore48SmsMillis , testUser.getId());
		assertEquals(1, updatedCount);
	}
	
	@Test
	public void testGetUsersForRetryPayment_MadeRetriesNotEqRetriesOnError_Success() throws Exception {
		
		int epochSeconds = getEpochSeconds();

        UserGroup o2UserGroup = UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(o2CommunityId);
		
		User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
		testUser.setNextSubPayment(epochSeconds + DAY_SECONDS);
		testUser.setLastDeviceLogin(epochSeconds);
        testUser.setSubBalance(0);
        testUser.setUserGroup(o2UserGroup);

		testUser = userRepository.save(testUser);

        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFields().withAdvancedPaymentSeconds(DAY_SECONDS).withCommunity(o2UserGroup.getCommunity()));
		
		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);
		currentO2PaymentDetails.resetMadeAttempts();
		currentO2PaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.ERROR);
		currentO2PaymentDetails.setRetriesOnError(3);
        currentO2PaymentDetails.setPaymentPolicy(paymentPolicy);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);
		
		List<User> actualUsers = userRepository.getUsersForRetryPayment(epochSeconds);
		
		assertNotNull(actualUsers);

		assertEquals(1, actualUsers.size());
		assertEquals(testUser.getId(), actualUsers.get(0).getId());

	}
	
	@Test
	public void testGetUsersForRetryPayment_O2CommunityUserWithActivatePaymentDetailsAndNextSubPaymentInTheFutureAndMadeRetriesEqRetriesOnError_Success() throws Exception {
		
		int epochSeconds = getEpochSeconds();
		
		UserGroup o2UserGroup = UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(o2CommunityId);
		
		User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
		testUser.setNextSubPayment(epochSeconds + DAY_SECONDS);
		testUser.setLastDeviceLogin(epochSeconds);
		testUser.setUserGroup(o2UserGroup);

		testUser = userRepository.save(testUser);
		
		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);
		currentO2PaymentDetails.withMadeRetries(3);
		currentO2PaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.ERROR);
		currentO2PaymentDetails.setRetriesOnError(3);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);
		
		List<User> actualUsers = userRepository.getUsersForRetryPayment(epochSeconds);
		
		assertNotNull(actualUsers);
		assertEquals(0, actualUsers.size());
	}
	
	@Test
	public void testGetUsersForRetryPayment_O2CommunityUserWithActivatePaymentDetailsAndNextSubPaymentInThePastAndMadeRetriesEqRetriesOnErrorAndMadeAttemptsIs1_Success() throws Exception {
		
		int epochSeconds = getEpochSeconds()-1;
		
		UserGroup o2UserGroup = UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(o2CommunityId);
		
		User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
		testUser.setNextSubPayment(epochSeconds);
		testUser.setLastDeviceLogin(epochSeconds);
		testUser.setUserGroup(o2UserGroup);

		testUser = userRepository.save(testUser);

        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(PaymentPolicyFactory.paymentPolicyWithDefaultNotNullFields().withAfterNextSubPaymentSeconds(100)
                .withCommunity(o2UserGroup.getCommunity())
                .withAdvancedPaymentSeconds(1));

		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

        currentO2PaymentDetails.setPaymentPolicy(paymentPolicy);
		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);
		currentO2PaymentDetails.withMadeRetries(0);
		currentO2PaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.ERROR);
		currentO2PaymentDetails.setRetriesOnError(3);
        currentO2PaymentDetails.withMadeAttempts(1);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);
		
		List<User> actualUsers = userRepository.getUsersForRetryPayment(epochSeconds);
		
		assertNotNull(actualUsers);
		assertEquals(1, actualUsers.size());
		assertEquals(testUser.getId(), actualUsers.get(0).getId());

	}

	@Test
	public void testFindUsersForUpdate_WithTwoMoreDayAndLessDay_Success() throws Exception {
		long epochMillis = getEpochMillis() - DAY_MILLISECONDS;
		
		UserGroup o2UserGroup = UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(o2CommunityId);
		
		User testUser = userRepository.save(UserFactory.createUser(ActivationStatus.ACTIVATED).withUserName("1").withActivationStatus(ACTIVATED).withUserGroup(o2UserGroup).withDeviceUID("attg0vs3e98dsddc2a4k9vdkc61"));
        userLogRepository.save(new UserLog().withLogTimeMillis(epochMillis-DAY_MILLISECONDS).withUser(testUser).withUserLogStatus(SUCCESS).withUserLogType(UPDATE_O2_USER).withDescription("dfdf"));
        
        User testUser1 = userRepository.save(UserFactory.createUser(ActivationStatus.ACTIVATED).withUserName("2").withActivationStatus(ACTIVATED).withUserGroup(o2UserGroup).withDeviceUID("attg0vs3e98dsddc2a4k9vdkc62"));
        userLogRepository.save(new UserLog().withLogTimeMillis(epochMillis+DAY_MILLISECONDS).withUser(testUser1).withUserLogStatus(SUCCESS).withUserLogType(UPDATE_O2_USER).withDescription("dfdf"));
        userLogRepository.save(new UserLog().withLogTimeMillis(epochMillis-DAY_MILLISECONDS).withUser(testUser1).withUserLogStatus(SUCCESS).withUserLogType(VALIDATE_PHONE_NUMBER).withDescription("dfdf"));
		
        User testUser2 = userRepository.save(UserFactory.createUser(ActivationStatus.ACTIVATED).withUserName("3").withActivationStatus(ACTIVATED).withUserGroup(o2UserGroup).withDeviceUID("attg0vs3e98dsddc2a4k9vdkc63"));
        userLogRepository.save(new UserLog().withLogTimeMillis(0L).withUser(testUser2).withUserLogStatus(SUCCESS).withUserLogType(UPDATE_O2_USER).withDescription("dfdf"));
        
		List<Integer> actualUsers = userRepository.getUsersForUpdate(epochMillis, o2UserGroup.getId());
		
		assertNotNull(actualUsers);
		assertEquals(3, actualUsers.size());
	}
	
	@Test
	public void testGetUsersForPendingPayment_O2_O2_CONSUMER_PSMS_Success() throws Exception {
        //given
        int epochSeconds = getEpochSeconds()-1;

		UserGroup o2UserGroup = UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(o2CommunityId);
		
		User testUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
		testUser.setNextSubPayment(epochSeconds);
		testUser.setLastDeviceLogin(epochSeconds);
		testUser.setUserGroup(o2UserGroup);
        testUser.setSubBalance(0);
		
		testUser = userRepository.save(testUser);
		
		PaymentPolicy paymentPolicy = paymentPolicyRepository.findOne(228);
		
		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		
		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);
		currentO2PaymentDetails.withMadeRetries(0);
		currentO2PaymentDetails.setPaymentPolicy(paymentPolicy);
		currentO2PaymentDetails.setLastPaymentStatus(NONE);
		currentO2PaymentDetails.setRetriesOnError(3);
		
		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);
		
		testUser = userRepository.save(testUser);

        //when
		List<User> actualUsers = userRepository.getUsersForPendingPayment(epochSeconds);

        //then
		assertNotNull(actualUsers);
		assertThat(actualUsers.size(), is(2));
        assertThat(actualUsers.get(1).getId(), is(testUser.getId()));
	}

    @Test
    public void shouldFindOneRecordByPinMobileAndCommunity() throws Exception {
        //given
        UserGroup o2UserGroup = UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(o2CommunityId);

        User user = userRepository.save(UserFactory.createUser(ActivationStatus.ACTIVATED).withMobile("mobile").withPin("pin").withUserGroup(o2UserGroup));

        //when
        long count = userRepository.findByOtacMobileAndCommunity(user.getPin(), user.getMobile(), o2UserGroup.getCommunity());

        //then
        assertThat(count, is(1L));
    }

    @Test
    public void shouldNotFindOneRecordByPinMobileAndCommunity() throws Exception {
        //given
        UserGroup o2UserGroup = UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(o2CommunityId);

        User user = userRepository.save(UserFactory.createUser(ActivationStatus.ACTIVATED).withMobile("mobile").withPin("pin").withUserGroup(o2UserGroup));

        //when
        long count = userRepository.findByOtacMobileAndCommunity("unknownPin", user.getMobile(), o2UserGroup.getCommunity());

        //then
        assertThat(count, is(0L));
    }

    @Test
    public void shouldFindUserTree(){
        //given
        User user = userRepository.save(UserFactory.createUser(ActivationStatus.ACTIVATED).withUserName("1").withMobile("2").withUserGroup(UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(o2CommunityId)));

        //when
        User actualUser = userRepository.findUserTree(user.getId());

        //then
        assertNotNull(actualUser);
        assertThat(actualUser.getId(), is(user.getId()));
    }

    @Test
    public void shouldFindByUserNameAndCommunityAndOtherThanPassedId(){
        //given
        User user = userRepository.save(UserFactory.createUser(ActivationStatus.ACTIVATED).withUserName("145645").withMobile("+447766666667").withUserGroup(UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(o2CommunityId)).withDeviceUID("attg0vs3e98dsddc2a4k9vdkc63"));
        User user2 = userRepository.save(UserFactory.createUser(ActivationStatus.ACTIVATED).withUserName("+447766666667").withMobile("222").withUserGroup(UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(o2CommunityId)).withDeviceUID("attg0vs3e98dsddc2a4k9vdkc62"));

        //when
        User actualUser = userRepository.findByUserNameAndCommunityAndOtherThanPassedId(user.getMobile(), user.getUserGroup().getCommunity(), user.getId());

        //then
        assertNotNull(actualUser);
        assertThat(actualUser.getId(), is(user2.getId()));
    }

}