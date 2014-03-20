package mobi.nowtechnologies.server.web.controller;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.PayPalDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PaymentsPayPalController extends CommonController {

	public static final String VIEW_PAYMENTS_PAYPAL = "/paypal";

	public static final String PAGE_PAYMENTS_PAYPAL = PaymentsController.SCOPE_PREFIX + VIEW_PAYMENTS_PAYPAL + PAGE_EXT;
	public static final String PAGE_PAYMENTS_PAYPAL_INAPP = PaymentsController.SCOPE_PREFIX + VIEW_PAYMENTS_PAYPAL + PAGE_EXT;

	public static final String PAYPAL_BILLING_AGREEMENT_DESCRIPTION = "pay.paypal.billing.agreement.description";

	public static final String REQUEST_PARAM_PAYPAL = "result";
	private static final String REQUEST_PARAM_PAYPAL_TOKEN = "token";
	private static final String REQUEST_PARAM_PAYPAL_PAYMENT_POLICY = "paymentPolicyId";
	
	public static final String SUCCESSFUL_RESULT = "successful";
	public static final String FAIL_RESULT = "fail";

	private PaymentDetailsService paymentDetailsService;
    private PaymentPolicyService paymentPolicyService;

	@RequestMapping(value = PAGE_PAYMENTS_PAYPAL, method = RequestMethod.GET)
	public ModelAndView getPayPalPage(@PathVariable("scopePrefix") String scopePrefix, @RequestParam(value = REQUEST_PARAM_PAYPAL, required = false) String result,
			@RequestParam(value = REQUEST_PARAM_PAYPAL_TOKEN, required = false) String token,
			@RequestParam(value = REQUEST_PARAM_PAYPAL_PAYMENT_POLICY, required = true) Integer paymentPolicyId,
			@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) Cookie communityUrl, Locale locale) {
		ModelAndView modelAndModel = new ModelAndView(scopePrefix + VIEW_PAYMENTS_PAYPAL);

		if (StringUtils.hasText(result)) {
			if (StringUtils.hasText(token)) {
				paymentDetailsService.commitPayPalPaymentDetails(token, paymentPolicyId, communityUrl.getValue(), getSecurityContextDetails().getUserId());
			}
			modelAndModel.addObject(REQUEST_PARAM_PAYPAL, result);
		}else{
			PaymentPolicyDto paymentPolicy = paymentPolicyService.getPaymentPolicyDto(paymentPolicyId);
			modelAndModel.addObject(PaymentPolicyDto.PAYMENT_POLICY_DTO, paymentPolicy);
		}

		return modelAndModel;
	}

	@RequestMapping(value = PAGE_PAYMENTS_PAYPAL, method = RequestMethod.POST)
	public ModelAndView createPaymentDetails(@PathVariable("scopePrefix") String scopePrefix, HttpServletRequest request, 
			@ModelAttribute(PayPalDto.NAME) PayPalDto dto,
			@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) Cookie communityUrl, Locale locale) {
		PaymentPolicyDto paymentPolicy = paymentPolicyService.getPaymentPolicyDto(dto.getPaymentPolicyId());
		dto.setBillingAgreementDescription(messageSource.getMessage(PAYPAL_BILLING_AGREEMENT_DESCRIPTION, new Object[]{paymentPolicy.getSubweeks(), paymentPolicy.getSubcost()}, locale));
		StringBuilder callbackUrl = new StringBuilder(RequestUtils.getServerURL()).append(PATH_DELIM).append(scopePrefix).append(VIEW_PAYMENTS_PAYPAL).append(PAGE_EXT)
				.append(START_PARAM_DELIM)
				.append(REQUEST_PARAM_PAYPAL_PAYMENT_POLICY).append("=").append(dto.getPaymentPolicyId()).append("&")
				.append(REQUEST_PARAM_PAYPAL).append("=");
		dto.setFailUrl(callbackUrl+FAIL_RESULT);
		dto.setSuccessUrl(callbackUrl+SUCCESSFUL_RESULT);
		PayPalPaymentDetails payPalPamentDetails = paymentDetailsService.createPayPalPaymentDetails(dto, communityUrl.getValue(), getSecurityContextDetails().getUserId());
		return new ModelAndView(REDIRECT + payPalPamentDetails.getBillingAgreementTxId());
	}

	@ExceptionHandler(value = ServiceException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleExceptions(HttpServletRequest request, ServiceException exception, Locale locale) {
		String scopeView = request.getRequestURI().indexOf(PaymentsController.VIEW_MANAGE_PAYMENTS_INAPP) >= 0 ? PaymentsController.VIEW_MANAGE_PAYMENTS_INAPP
				: PaymentsController.VIEW_MANAGE_PAYMENTS;

		ModelAndView modelAndView = new ModelAndView(scopeView + VIEW_PAYMENTS_PAYPAL);

		final String message = messageSource.getMessage(exception.getErrorCodeForMessageLocalization(), null, locale);
		if (exception instanceof ExternalServiceException)
			modelAndView.addObject("external_error", message);
		else
			modelAndView.addObject("internal_error", message);
		modelAndView.addObject(REQUEST_PARAM_PAYPAL, FAIL);

		return modelAndView;
	}

	public void setPaymentDetailsService(PaymentDetailsService paymentDetailsService) {
		this.paymentDetailsService = paymentDetailsService;
	}

    public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
        this.paymentPolicyService = paymentPolicyService;
    }
}