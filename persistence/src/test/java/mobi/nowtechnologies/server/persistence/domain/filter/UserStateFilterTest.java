package mobi.nowtechnologies.server.persistence.domain.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import mobi.nowtechnologies.server.persistence.dao.PersistenceException;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.NewsDetail;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * The class <code>UserStateFilterTest</code> contains tests for the class <code>{@link UserStateFilter}</code>.
 *
 * @generatedBy CodePro at 01.02.12 17:50
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(UserStatusDao.class)
public class UserStateFilterTest {
	
	private static UserStatus SUBSCRIBED_USER_STATUS;
	private static UserStatus EULA_USER_STATUS;
	private static UserStatus LIMITED_USER_STATUS;
	
	/**
	 * Run the UserStateFilter() constructor test.
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testUserStateFilter_Constructor()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		assertNotNull(userStateFilter);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsFREE_TRIALAndUserStatusIsSUBSCRIBEDAndCurrentPaymentDetailsIsNotNull_Failure()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		User user = new User();
		user.setStatus(SUBSCRIBED_USER_STATUS);
		MigPaymentDetails paymentDetails = new MigPaymentDetails();
			paymentDetails.setActivated(true);
		user.setCurrentPaymentDetails(paymentDetails);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.FREE_TRIAL);

		boolean result = userStateFilter.doFilter(user, newsDetail);

		assertEquals(false, result);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsFREE_TRIALAndUserStatusIsLIMITED_Failure()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		User user = new User();
		user.setStatus(LIMITED_USER_STATUS);
		user.addPaymentDetails(new MigPaymentDetails());
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.FREE_TRIAL);

		boolean result = userStateFilter.doFilter(user, newsDetail);
		
		assertEquals(false, result);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsFREE_TRIALAndUserStatusIsSUBSCRIBEDAndCurrentPaymentDetailsIsNull_Success()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		User user = new User();
		
		user.setStatus(SUBSCRIBED_USER_STATUS);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.FREE_TRIAL);

		boolean result = userStateFilter.doFilter(user, newsDetail);

		assertEquals(true, result);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsFREE_TRIALAndUserStatusIsEULAAndCurrentPaymentDetailsIsNull_Failure()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		User user = new User();
		user.setStatus(EULA_USER_STATUS);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.FREE_TRIAL);

		boolean result = userStateFilter.doFilter(user, newsDetail);

		assertEquals(false, result);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsFREE_TRIALAndUserStatusIsEULAAndCurrentPaymentDetailsIsNotNull_Failure()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		User user = new User();
		user.setStatus(EULA_USER_STATUS);
		user.addPaymentDetails(new MigPaymentDetails());
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.FREE_TRIAL);

		boolean result = userStateFilter.doFilter(user, newsDetail);

		assertEquals(false, result);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsLAST_TRIAL_DAYAndUserStatusIsEULAAndCurrentPaymentDetailsIsNotNull_Failure()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		User user = new User();
		user.setStatus(EULA_USER_STATUS);
		user.addPaymentDetails(new MigPaymentDetails());
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.LAST_TRIAL_DAY);

		boolean result = userStateFilter.doFilter(user, newsDetail);

		assertEquals(false, result);
	}
	
	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsLAST_TRIAL_DAYAndUserStatusIsSUBSCRIBEDAndCurrentPaymentDetailsIsNullAndNextSubPayment12Hours_Success()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		User user = new User();
		user.setStatus(SUBSCRIBED_USER_STATUS);
		user.setNextSubPayment(Utils.getEpochSeconds()+12*60*60);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.LAST_TRIAL_DAY);

		boolean result = userStateFilter.doFilter(user, newsDetail);

		assertEquals(true, result);
	}
	
	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsLIMITEDAndUserStatusIsLIMITEDAndCurrentPaymentDetailsIsNull_Success()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		User user = new User();
		user.setStatus(LIMITED_USER_STATUS);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.LIMITED);

		boolean result = userStateFilter.doFilter(user, newsDetail);

		assertEquals(true, result);
	}
	
	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsLIMITEDAndUserStatusIsSUBSCRIBEDAndCurrentPaymentDetailsIsNull_Failure()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		User user = new User();
		user.setStatus(SUBSCRIBED_USER_STATUS);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.LIMITED);

		boolean result = userStateFilter.doFilter(user, newsDetail);

		assertEquals(false, result);
	}
	
	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsLIMITEDAndUserStatusIsEULA_Failure()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		User user = new User();
		user.setStatus(EULA_USER_STATUS);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.LIMITED);

		boolean result = userStateFilter.doFilter(user, newsDetail);

		assertEquals(false, result);
	}
	
	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsLAST_TRIAL_DAYAndUserStatusIsSUBSCRIBEDAndCurrentPaymentDetailsIsNullAndNextSubPayment25Hours_Failure()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		User user = new User();
		user.setStatus(SUBSCRIBED_USER_STATUS);
		user.setNextSubPayment(Utils.getEpochSeconds()+25*60*60);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.LAST_TRIAL_DAY);

		boolean result = userStateFilter.doFilter(user, newsDetail);

		assertEquals(false, result);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsNOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILSAndUserStatusIsSUBSCRIBEDAndCurrentPaymentDetailsIsNullAndNextSubPayment25Hours_Success()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		User user = new User();
		user.setStatus(SUBSCRIBED_USER_STATUS);
		user.setNextSubPayment(Utils.getEpochSeconds()+25*60*60);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS);

		boolean result = userStateFilter.doFilter(user, newsDetail);
		
		assertEquals(true, result);
	}
	
	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsNOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILSAndUserStatusIsSUBSCRIBEDAndCurrentPaymentDetailsIsNotActivated_Success()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		MigPaymentDetails migPaymentDetails = new MigPaymentDetails();
		migPaymentDetails.setActivated(false);
		
		User user = new User();
		user.setStatus(SUBSCRIBED_USER_STATUS);
		user.addPaymentDetails(migPaymentDetails);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS);

		boolean result = userStateFilter.doFilter(user, newsDetail);
		
		assertEquals(true, result);
	}
	
	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsNOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILSAndUserStatusIsSUBSCRIBEDAndCurrentPaymentDetailsIsActivated_Failure()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		MigPaymentDetails migPaymentDetails = new MigPaymentDetails();
		migPaymentDetails.setActivated(true);
		
		User user = new User();
		user.setStatus(SUBSCRIBED_USER_STATUS);
		user.setCurrentPaymentDetails(migPaymentDetails);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS);

		boolean result = userStateFilter.doFilter(user, newsDetail);
		
		assertEquals(false, result);
	}
	
	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsNOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILSAndUserStatusIsLIMITEDAndCurrentPaymentDetailsIsActivated_Failure()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		MigPaymentDetails migPaymentDetails = new MigPaymentDetails();
		migPaymentDetails.setActivated(true);
		
		User user = new User();
		user.setStatus(LIMITED_USER_STATUS);
		user.setCurrentPaymentDetails(migPaymentDetails);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS);

		boolean result = userStateFilter.doFilter(user, newsDetail);
		
		assertEquals(false, result);
	}
	
	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsNOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILSAndUserStatusIsLIMITEDAndCurrentPaymentDetailsIsNotActivated_Success()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		MigPaymentDetails migPaymentDetails = new MigPaymentDetails();
		migPaymentDetails.setActivated(false);
		
		User user = new User();
		user.setStatus(LIMITED_USER_STATUS);
		user.addPaymentDetails(migPaymentDetails);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS);

		boolean result = userStateFilter.doFilter(user, newsDetail);
		
		assertEquals(true, result);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsPAYMENT_ERROR_DETAILSAndUserStatusIsLIMITEDAndCurrentPaymentDetailsIsNotActivatedAndLastPaymentStatusIsERROR_Success()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		MigPaymentDetails migPaymentDetails = new MigPaymentDetails();
		migPaymentDetails.setActivated(false);
		migPaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.ERROR);
		
		User user = new User();
		user.setStatus(LIMITED_USER_STATUS);
		user.setCurrentPaymentDetails(migPaymentDetails);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.PAYMENT_ERROR);

		boolean result = userStateFilter.doFilter(user, newsDetail);
		
		assertEquals(true, result);
	}
	
	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsLIMITED_AFTER_TRIALAndUserStatusIsLIMITEDAndCurrentPaymentDetailsIsNotNull_Failure()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		MigPaymentDetails migPaymentDetails = new MigPaymentDetails();
		migPaymentDetails.setActivated(false);
		migPaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.ERROR);
		
		User user = new User();
		user.setStatus(LIMITED_USER_STATUS);
		user.setCurrentPaymentDetails(migPaymentDetails);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.LIMITED_AFTER_TRIAL);

		boolean result = userStateFilter.doFilter(user, newsDetail);
		
		assertEquals(false, result);
	}
	
	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test
	public void testDoFilter_WhenUserStateIsLIMITED_AFTER_TRIALAndUserStatusIsLIMITEDAndCurrentPaymentDetailsIsNull_Success()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		User user = new User();
		user.setStatus(LIMITED_USER_STATUS);
		user.addPaymentDetails(null);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.LIMITED_AFTER_TRIAL);

		boolean result = userStateFilter.doFilter(user, newsDetail);
		
		assertEquals(true, result);
	}
	
	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.02.12 17:50
	 */
	@Test(expected=PersistenceException.class)
	public void testDoFilter_WhenUserStateIsONE_MONTH_PROMOAndUserStatusIsLIMITEDAndCurrentPaymentDetailsIsNotActivatedAndLastPaymentStatusIsERROR_Failure()
		throws Exception {
		UserStateFilter userStateFilter = new UserStateFilter();
		
		MigPaymentDetails migPaymentDetails = new MigPaymentDetails();
		migPaymentDetails.setActivated(false);
		migPaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.ERROR);
		
		User user = new User();
		user.setStatus(LIMITED_USER_STATUS);
		user.addPaymentDetails(migPaymentDetails);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserState(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState.ONE_MONTH_PROMO);

		userStateFilter.doFilter(user, newsDetail);
	}
	
	@Before
	public void setUp(){
		SUBSCRIBED_USER_STATUS = new UserStatus();
		EULA_USER_STATUS = new UserStatus();
		LIMITED_USER_STATUS = new UserStatus();
		
		SUBSCRIBED_USER_STATUS.setName(UserStatusDao.SUBSCRIBED);
		EULA_USER_STATUS.setName(UserStatusDao.EULA);
		LIMITED_USER_STATUS.setName(UserStatusDao.LIMITED);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(LIMITED_USER_STATUS);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(EULA_USER_STATUS);
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(SUBSCRIBED_USER_STATUS);		
	}
}