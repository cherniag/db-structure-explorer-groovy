package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
/**
 * The class <code>PromotionServiceTest</code> contains tests for the class <code>{@link PromotionService}</code>.
 *
 * @generatedBy CodePro at 04.10.11 15:37
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
public class PromotionServiceTest {
	
	private PromotionService promotionService;
	
	@Before
	public void before() {
		promotionService = new PromotionService();
			EntityService entityService = mock(EntityService.class);
			when(entityService.updateEntity(any(Object.class))).thenAnswer(new Answer<Object>() {
				@Override public Object answer(InvocationOnMock invocation) throws Throwable {
					return invocation.getArguments()[0];
				}
			});
		promotionService.setEntityService(entityService);
	}
	
	@Test
	public void applyPromotion() {
		User user = new User();
			Promotion potentialPromotion = new Promotion();
			user.setPotentialPromotion(potentialPromotion);
			PaymentDetails currentPaymentDetails = new SagePayCreditCardPaymentDetails();
				PromotionPaymentPolicy promotionPaymentPolicy = new PromotionPaymentPolicy();
				currentPaymentDetails.setPromotionPaymentPolicy(promotionPaymentPolicy);
			user.setCurrentPaymentDetails(currentPaymentDetails);
		User userAfterPromotion = promotionService.applyPromotion(user);
		
		assertNotNull(userAfterPromotion);
		assertNull(userAfterPromotion.getPotentialPromotion());
		assertNull(user.getCurrentPaymentDetails().getPromotionPaymentPolicy());
	}
	
	/**
	 * Run the Promotion getActivePromotion(String,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 04.10.11 15:37
	 */
	@Test
	@Ignore
	public void testGetActivePromotion_Success()
		throws Exception {
		String promotionCode = "promo";
		String communityName = "Now Music";

		Promotion result = promotionService.getActivePromotion(promotionCode, communityName);

		assertNotNull(result);
	}

	/**
	 * Run the Promotion getActivePromotion(String,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 04.10.11 15:37
	 */
	@Test(expected = mobi.nowtechnologies.server.service.exception.ServiceException.class)
	@Ignore
	public void testGetActivePromotion_WhenPromotionCodeIsNull()
		throws Exception {
		String promotionCode = null;
		String communityName = "";

		Promotion result = promotionService.getActivePromotion(promotionCode, communityName);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the Promotion getActivePromotion(String,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 04.10.11 15:37
	 */
	@Test(expected = mobi.nowtechnologies.server.service.exception.ServiceException.class)
	@Ignore
	public void testGetActivePromotion_WhenCommunityNameIsNull()
		throws Exception {
		String promotionCode = "";
		String communityName = null;

		Promotion result = promotionService.getActivePromotion(promotionCode, communityName);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the boolean isPromoCodeActivePromotionExsist(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 04.10.11 15:37
	 */
	@Test
	@Ignore
	public void testIsPromoCodeActivePromotionExsist_WhenPromotionExsist()
		throws Exception {
		String communityName = "Now Music";

		boolean result = promotionService.isPromoCodeActivePromotionExsist(communityName);
		assertTrue(result);
	}

	/**
	 * Run the boolean isPromoCodeActivePromotionExsist(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 04.10.11 15:37
	 */
	@Test(expected = NullPointerException.class)
	@Ignore
	public void testIsPromoCodeActivePromotionExsist_WhenCommunityNameIsEmpty()
		throws Exception {
		String communityName = "";

		boolean result = promotionService.isPromoCodeActivePromotionExsist(communityName);

		assertFalse(result);
	}

	/**
	 * Run the boolean isPromoCodeActivePromotionExsist(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 04.10.11 15:37
	 */
	@Test(expected = mobi.nowtechnologies.server.service.exception.ServiceException.class)
	@Ignore
	public void testIsPromoCodeActivePromotionExsist_WhenCommunityNameIsNull()
		throws Exception {
		String communityName = null;

		boolean result = promotionService.isPromoCodeActivePromotionExsist(communityName);

		// add additional test code here
		assertTrue(result);
	}
}