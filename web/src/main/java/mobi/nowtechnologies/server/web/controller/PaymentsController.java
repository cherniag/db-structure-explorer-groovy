package mobi.nowtechnologies.server.web.controller;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.Cookie;

import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.PaymentDetailsByPaymentDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PaymentsController extends CommonController {
	private static final String PAYMENTS_NOTE_MSG_CODE = "pays.page.h1.options.note";

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentsController.class);

	public static final String SCOPE_PREFIX = "{scopePrefix:payments|payments_inapp}";

	public static final String VIEW_MANAGE_PAYMENTS = "payments";
	public static final String VIEW_MANAGE_PAYMENTS_INAPP = "payments_inapp";
	public static final String PAGE_MANAGE_PAYMENTS = PATH_DELIM + VIEW_MANAGE_PAYMENTS + PAGE_EXT;
	public static final String ACTIVATE_PAYMENT_DETAILS_BY_PAYMENT = SCOPE_PREFIX + PATH_DELIM + "paymentDetails" + PATH_DELIM + "{paymentDetailsId}";
	public static final String SUCCESS_ACTIVATE_PAYMENT_DETAILS_BY_PAYMENT = SCOPE_PREFIX + "/one_click_subscription_successful.html";
	public static final String PAGE_MANAGE_PAYMENTS_INAPP = PATH_DELIM + VIEW_MANAGE_PAYMENTS_INAPP + PAGE_EXT;

	private PaymentDetailsService paymentDetailsService;

	private UserService userService;

	protected ModelAndView getManagePaymentsPage(String viewName, String communityUrl) {
		LOGGER.debug("input parameters viewName, communityUrl: [{}], [{}]", viewName, communityUrl);
		ModelAndView modelAndView = new ModelAndView(viewName);

		final int userId = getSecurityContextDetails().getUserId();
		User user = userService.findById(userId);
		List<PaymentPolicyDto> paymentPolicies = Collections.emptyList();
		PaymentDetailsByPaymentDto paymentDetailsByPaymentDto = null;
		PaymentDetails paymentDetails = null;
		
		String paymentsNoteMsg;
		if (communityUrl.equals("o2") && "o2".equals(user.getProvider())) {
			paymentsNoteMsg = messageSource.getMessage(PAYMENTS_NOTE_MSG_CODE + "." + user.getProvider() + "." + user.getContract(), null, "", null);
			if (StringUtils.isEmpty(paymentsNoteMsg)) {
				paymentsNoteMsg = messageSource.getMessage(PAYMENTS_NOTE_MSG_CODE + "." + user.getProvider(), null, "", null);
			}
			if (StringUtils.isEmpty(paymentsNoteMsg)) {
				paymentsNoteMsg = messageSource.getMessage(PAYMENTS_NOTE_MSG_CODE, null, null);
			}
		} else {
			boolean nonO2User = userService.isNonO2User(user);
			if (isIOsNonO2ItunesSubscribedUser(user, nonO2User)) {
				paymentsNoteMsg = messageSource.getMessage("pays.page.h1.options.note.not.o2.inapp.subs", null, null);
				paymentPolicies = paymentDetailsService.getPaymentPolicyDetails(communityUrl, userId, PaymentDetails.ITNUNES_SUBSCRIPTION);
			}else if (!DeviceTypeDao.getIOSDeviceType().equals(user.getDeviceType())){
				paymentsNoteMsg = messageSource.getMessage(PAYMENTS_NOTE_MSG_CODE, null, null);
				paymentPolicies = paymentDetailsService.getPaymentPolicyDetailsWithouPaymentType(communityUrl, userId, PaymentDetails.ITNUNES_SUBSCRIPTION);
				paymentDetails = paymentDetailsService.getPaymentDetails(userId);
				paymentDetailsByPaymentDto = paymentDetailsService.getPaymentDetailsTypeByPayment(userId);
			}else{
				paymentsNoteMsg = messageSource.getMessage(PAYMENTS_NOTE_MSG_CODE, null, null);
				paymentPolicies = paymentDetailsService.getPaymentPolicyDetails(communityUrl, userId);
				paymentDetails = paymentDetailsService.getPaymentDetails(userId);
				paymentDetailsByPaymentDto = paymentDetailsService.getPaymentDetailsTypeByPayment(userId);
			}
		}
			
		modelAndView.addObject("paymentPolicies", paymentPolicies);
		modelAndView.addObject("paymentDetails", paymentDetails);
		modelAndView.addObject("paymentPoliciesNote", paymentsNoteMsg);
		modelAndView.addObject(PaymentDetailsByPaymentDto.NAME, paymentDetailsByPaymentDto);

		LOGGER.debug("Output parameter [{}]", modelAndView);
		return modelAndView;
	}

	private boolean isIOsNonO2ItunesSubscribedUser(User user, boolean nonO2User) {
		boolean isIOsNonO2ItunesSubscribedUser = false;
		
		final String lastSubscribedPaymentSystem = user.getLastSubscribedPaymentSystem();
		if (lastSubscribedPaymentSystem != null) {
			isIOsNonO2ItunesSubscribedUser = DeviceTypeDao.getIOSDeviceType().getName().equals(user.getDeviceType().getName()) && nonO2User && (lastSubscribedPaymentSystem.equals(PaymentDetails.ITNUNES_SUBSCRIPTION))
					&& user.getStatus().getI() == UserStatusDao.getSubscribedUserStatus().getI();
		}
		return isIOsNonO2ItunesSubscribedUser;
	}

	@RequestMapping(value = { ACTIVATE_PAYMENT_DETAILS_BY_PAYMENT }, method = RequestMethod.POST)
	public ModelAndView activatePaymentDetailsByPayment(@PathVariable("scopePrefix") String scopePrefix, @PathVariable("paymentDetailsId") Long paymentDetailsId) {
		LOGGER.debug("input parameters paymentDetailsId: [{}]", paymentDetailsId);

		paymentDetailsService.activatePaymentDetailsByPayment(paymentDetailsId);

		ModelAndView modelAndView = new ModelAndView("redirect:/" + scopePrefix + "/one_click_subscription_successful.html");
		LOGGER.debug("Output parameter [{}]", modelAndView);
		return modelAndView;
	}

	@RequestMapping(value = { SUCCESS_ACTIVATE_PAYMENT_DETAILS_BY_PAYMENT }, method = RequestMethod.GET)
	public ModelAndView getOneClickSubscriptionSuccessfulPage(@PathVariable("scopePrefix") String scopePrefix) {

		final int userId = getSecurityContextDetails().getUserId();
		PaymentDetailsByPaymentDto paymentDetailsByPaymentDto = paymentDetailsService.getPaymentDetailsTypeByPayment(userId);

		final ModelAndView modelAndView;
		if (paymentDetailsByPaymentDto == null || !paymentDetailsByPaymentDto.isActivated())
		{
			modelAndView = new ModelAndView("redirect:account.html");
		} else {
			modelAndView = new ModelAndView(scopePrefix + "/one_click_subscription_successful");
			modelAndView.addObject(PaymentDetailsByPaymentDto.NAME, paymentDetailsByPaymentDto);
		}

		LOGGER.debug("Output parameter [{}]", modelAndView);
		return modelAndView;
	}

	@RequestMapping(value = { PAGE_MANAGE_PAYMENTS }, method = RequestMethod.GET)
	public ModelAndView getManagePaymentsPage(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityUrl) {
		return getManagePaymentsPage(VIEW_MANAGE_PAYMENTS, communityUrl);
	}

	@RequestMapping(value = { PAGE_MANAGE_PAYMENTS_INAPP }, method = RequestMethod.GET)
	public ModelAndView getManagePaymentsPageInApp(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityUrl) {
		return getManagePaymentsPage(VIEW_MANAGE_PAYMENTS_INAPP, communityUrl);
	}

	public void setPaymentDetailsService(PaymentDetailsService paymentDetailsService) {
		this.paymentDetailsService = paymentDetailsService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}