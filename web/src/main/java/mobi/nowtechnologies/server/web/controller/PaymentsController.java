package mobi.nowtechnologies.server.web.controller;

import java.util.List;

import javax.servlet.http.Cookie;

import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.PaymentDetailsByPaymentDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PaymentsController extends CommonController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentsController.class);

	public static final String SCOPE_PREFIX = "{scopePrefix:payments|payments_inapp}";

	public static final String VIEW_MANAGE_PAYMENTS = "payments";
	public static final String VIEW_MANAGE_PAYMENTS_INAPP = "payments_inapp";
	public static final String PAGE_MANAGE_PAYMENTS = PATH_DELIM + VIEW_MANAGE_PAYMENTS + PAGE_EXT;
	public static final String ACTIVATE_PAYMENT_DETAILS_BY_PAYMENT = SCOPE_PREFIX + PATH_DELIM + "paymentDetails" + PATH_DELIM + "{paymentDetailsId}";
	public static final String SUCCESS_ACTIVATE_PAYMENT_DETAILS_BY_PAYMENT = SCOPE_PREFIX + "/one_click_subscription_successful.html";
	public static final String PAGE_MANAGE_PAYMENTS_INAPP = PATH_DELIM + VIEW_MANAGE_PAYMENTS_INAPP + PAGE_EXT;

	private PaymentDetailsService paymentDetailsService;

	protected ModelAndView getManagePaymentsPage(String viewName, Cookie communityUrl) {
		LOGGER.debug("input parameters viewName, communityUrl: [{}], [{}]", viewName, communityUrl);
		ModelAndView modelAndView = new ModelAndView(viewName);

		final int userId = getSecurityContextDetails().getUserId();
		List<PaymentPolicyDto> paymentPolicies = paymentDetailsService.getPaymentPolicyDetails(communityUrl.getValue(), userId);
		PaymentDetailsByPaymentDto paymentDetailsByPaymentDto = paymentDetailsService.getPaymentDetailsTypeByPayment(userId);
		modelAndView.addObject("paymentPolicies", paymentPolicies);
		modelAndView.addObject("paymentDetails", paymentDetailsService.getPaymentDetails(userId));
		modelAndView.addObject(PaymentDetailsByPaymentDto.NAME, paymentDetailsByPaymentDto);

		LOGGER.debug("Output parameter [{}]", modelAndView);
		return modelAndView;
	}
	
	@RequestMapping(value = { ACTIVATE_PAYMENT_DETAILS_BY_PAYMENT }, method = RequestMethod.POST)
	public ModelAndView activatePaymentDetailsByPayment(@PathVariable("scopePrefix") String scopePrefix, @PathVariable("paymentDetailsId") Long paymentDetailsId) {
		LOGGER.debug("input parameters paymentDetailsId: [{}]", paymentDetailsId);
		
		paymentDetailsService.activatePaymentDetailsByPayment(paymentDetailsId);
		
		ModelAndView modelAndView = new ModelAndView("redirect:/"+scopePrefix+"/one_click_subscription_successful.html");
		LOGGER.debug("Output parameter [{}]", modelAndView);
		return modelAndView;
	}
	
	@RequestMapping(value = { SUCCESS_ACTIVATE_PAYMENT_DETAILS_BY_PAYMENT }, method = RequestMethod.GET)
	public ModelAndView getOneClickSubscriptionSuccessfulPage(@PathVariable("scopePrefix") String scopePrefix) {
		
		final int userId = getSecurityContextDetails().getUserId();
		PaymentDetailsByPaymentDto paymentDetailsByPaymentDto = paymentDetailsService.getPaymentDetailsTypeByPayment(userId);
		
		final ModelAndView modelAndView;
		if(paymentDetailsByPaymentDto==null || !paymentDetailsByPaymentDto.isActivated())
		{
			modelAndView= new ModelAndView("redirect:account.html");
		}else{
			modelAndView= new ModelAndView(scopePrefix+"/one_click_subscription_successful");
			modelAndView.addObject(PaymentDetailsByPaymentDto.NAME, paymentDetailsByPaymentDto);
		}
			
		LOGGER.debug("Output parameter [{}]", modelAndView);
		return modelAndView;
	}

	@RequestMapping(value = { PAGE_MANAGE_PAYMENTS }, method = RequestMethod.GET)
	public ModelAndView getManagePaymentsPage(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) Cookie communityUrl) {
		return getManagePaymentsPage(VIEW_MANAGE_PAYMENTS, communityUrl);
	}

	@RequestMapping(value = { PAGE_MANAGE_PAYMENTS_INAPP }, method = RequestMethod.GET)
	public ModelAndView getManagePaymentsPageInApp(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) Cookie communityUrl) {
		return getManagePaymentsPage(VIEW_MANAGE_PAYMENTS_INAPP, communityUrl);
	}

	public void setPaymentDetailsService(PaymentDetailsService paymentDetailsService) {
		this.paymentDetailsService = paymentDetailsService;
	}	
}