package mobi.nowtechnologies.server.web.controller;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.Cookie;

import junit.framework.TestCase;
import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.shared.web.security.userdetails.UserDetailsImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.ResultActions;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

/**
 * The class <code>ChartControllerTest</code> contains tests for the class <code>{@link ChartController}</code>.
 * 
 * @author Alexander Kolpakov (akolpakov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:security.xml",
		"classpath:web-test.xml",
		"classpath:META-INF/service-test.xml",
		"classpath:META-INF/dao-test.xml",
		"classpath:META-INF/shared.xml" }, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "web.PaymentsController")
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class PaymentsControllerIT extends TestCase {

	@Autowired
	private ApplicationContext wac;

	private MockMvc mockMvc;

	protected static final String URL_DATE_TIME_FORMAT = "yyyy-MM-dd_HH:mm:ss";

	protected DateFormat dateTimeFormat = new SimpleDateFormat(URL_DATE_TIME_FORMAT);

	/**
	 * Run the ModelAndView getManagePaymentsPage(Cookie communityUrl) method test with success expected result.
	 * 
	 */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void testGetManagePaymentsPage_NonO2User_Successful()
			throws Exception {
		String communityUrl = "o2";
		
		SecurityContextHolder.setContext(createSecurityContext(1));

		ResultActions resultActions = mockMvc.perform(
				get("/payments.html")
					.cookie(new Cookie[]{new Cookie(CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, communityUrl)}))	
				.andExpect(status().isOk());

		ModelAndView modelAndView = resultActions.andReturn().getModelAndView();
		ModelMap modelMap = resultActions.andReturn().getModelAndView().getModelMap();

		String viewName = modelAndView.getViewName();
		String paymentPoliciesNote = (String) modelMap.get("paymentPoliciesNote");
		List<PaymentPolicyDto> paymentPolicies = (List<PaymentPolicyDto>) modelMap.get("paymentPolicies");

		assertEquals("payments", viewName);
		assertNotNull(paymentPolicies);
		assertEquals(3, paymentPolicies.size());
		assertEquals("Get ongoing unlimited access to all the hits all the time for just &pound;1 a week! (You will not lose your remaining free trial)", paymentPoliciesNote);
	}

	/**
	 * Run the ModelAndView getManagePaymentsPage(Cookie communityUrl) method test with success expected result.
	 * 
	 */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void testGetManagePaymentsPage_O2UserDTB_Successful()
			throws Exception {
		String communityUrl = "o2";
		
		SecurityContextHolder.setContext(createSecurityContext(101));

		ResultActions resultActions = mockMvc.perform(
				get("/payments.html")
					.cookie(new Cookie[]{new Cookie(CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, communityUrl)}))	
				.andExpect(status().isOk());

		ModelAndView modelAndView = resultActions.andReturn().getModelAndView();
		ModelMap modelMap = resultActions.andReturn().getModelAndView().getModelMap();

		String viewName = modelAndView.getViewName();
		String paymentPoliciesNote = (String) modelMap.get("paymentPoliciesNote");
		List<PaymentPolicyDto> paymentPolicies = (List<PaymentPolicyDto>) modelMap.get("paymentPolicies");

		assertEquals("payments", viewName);
		assertNotNull(paymentPolicies);
		assertEquals(0, paymentPolicies.size());
		assertEquals("Please come back after your trial!", paymentPoliciesNote);
	}
	
	/**
	 * Run the ModelAndView getManagePaymentsPage(Cookie communityUrl) method test with success expected result.
	 * 
	 */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void testGetManagePaymentsPage_O2UserNonDTB_Successful()
			throws Exception {
		String communityUrl = "o2";
		
		SecurityContextHolder.setContext(createSecurityContext(102));

		ResultActions resultActions = mockMvc.perform(
				get("/payments.html")
					.cookie(new Cookie[]{new Cookie(CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, communityUrl)}))	
				.andExpect(status().isOk());

		ModelAndView modelAndView = resultActions.andReturn().getModelAndView();
		ModelMap modelMap = resultActions.andReturn().getModelAndView().getModelMap();

		String viewName = modelAndView.getViewName();
		String paymentPoliciesNote = (String) modelMap.get("paymentPoliciesNote");
		List<PaymentPolicyDto> paymentPolicies = (List<PaymentPolicyDto>) modelMap.get("paymentPolicies");

		assertEquals("payments", viewName);
		assertNotNull(paymentPolicies);
		assertEquals(0, paymentPolicies.size());
		assertEquals("Please come back after your trial", paymentPoliciesNote);
	}

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 * 
	 * @see TestCase#setUp()
	 * 
	 */
	@Before
	public void setUp()
			throws Exception {
		super.setUp();

		mockMvc = MockMvcBuilders.webApplicationContextSetup((WebApplicationContext) this.wac).build();
	}

	@After
	public void tearDown()
			throws Exception {
		super.setUp();
	}

	/**
	 * generate test data
	 */
	private SecurityContext createSecurityContext(int userId) {
		User user = new User();
		user.setId(userId);
		Authentication authentication = new RememberMeAuthenticationToken("test", new UserDetailsImpl(user, true), null);
		SecurityContext securityContext = new SecurityContextImpl();
		securityContext.setAuthentication(authentication);
		 
		return securityContext;
	}
}