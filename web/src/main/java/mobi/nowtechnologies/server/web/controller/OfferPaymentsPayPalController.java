package mobi.nowtechnologies.server.web.controller;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.server.persistence.domain.PayPalPaymentDetails;
import mobi.nowtechnologies.server.service.OfferService;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.admin.OfferDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.PayPalDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
@Controller
public class OfferPaymentsPayPalController extends CommonController {

	public static final String VIEW_PAYMENTS_PAYPAL = "offer/payments/paypal";

	public static final String PAGE_PAYMENTS_PAYPAL = "offers/{offerId}/payments/paypal.html";
	public static final String PAGE_PAYMENTS_PAYPAL_CALLBACK = "offers/{offerId}/payments/paypal_callback.html";

	public static final String PAYPAL_BILLING_AGREEMENT_DESCRIPTION = "offer.pay.paypal.billing.agreement.description";

	public static final String REQUEST_PARAM_PAYPAL = "result";
	private static final String REQUEST_PARAM_PAYPAL_TOKEN = "token";

	public static final String SUCCESSFUL_RESULT = "successful";
	public static final String FAIL_RESULT = "fail";

	private PaymentDetailsService paymentDetailsService;
	private OfferService offerService;

	@RequestMapping(value = PAGE_PAYMENTS_PAYPAL, method = RequestMethod.GET)
	public ModelAndView getPayPalPage(@PathVariable(OfferDto.OFFER_ID) Integer offerId, HttpServletRequest request, @ModelAttribute(PayPalDto.NAME) PayPalDto dto,
			@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) Cookie communityUrl, Locale locale) {
		OfferDto offer = offerService.getOfferDto(offerId);
		StringBuilder callbackUrl = new StringBuilder(RequestUtils.getServerURL()).append(PATH_DELIM).append(PAGE_PAYMENTS_PAYPAL_CALLBACK.replaceFirst("\\{offerId\\}", offerId.toString()));
		callbackUrl.append(START_PARAM_DELIM).append(REQUEST_PARAM_PAYPAL).append("=");
		
		dto.setBillingAgreementDescription(messageSource.getMessage(PAYPAL_BILLING_AGREEMENT_DESCRIPTION, new Object[]{offer.getTitle(), offer.getPrice()}, locale));
		dto.setFailUrl(callbackUrl+FAIL_RESULT);
		dto.setSuccessUrl(callbackUrl+SUCCESSFUL_RESULT);
		
		PayPalPaymentDetails payPalPamentDetails = paymentDetailsService.createPayPalPamentDetails(dto, communityUrl.getValue(), getSecurityContextDetails().getUserId());
		
		return new ModelAndView(REDIRECT + payPalPamentDetails.getBillingAgreementTxId());
	}

	@RequestMapping(value = PAGE_PAYMENTS_PAYPAL_CALLBACK, method = RequestMethod.GET)
	public ModelAndView buyByPaymentDetails(
			HttpServletRequest request,
			@PathVariable(OfferDto.OFFER_ID) Integer offerId,
			@RequestParam(value = REQUEST_PARAM_PAYPAL) String result,
			@RequestParam(value = REQUEST_PARAM_PAYPAL_TOKEN) String token,
			@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) Cookie communityUrl) {
		ModelAndView modelAndModel = new ModelAndView(VIEW_PAYMENTS_PAYPAL);

		if(result.equals(SUCCESSFUL_RESULT))
			paymentDetailsService.buyByPayPalPaymentDetails(token, communityUrl.getValue(), getSecurityContextDetails().getUserId(), offerId);

		modelAndModel.addObject(REQUEST_PARAM_PAYPAL, result);
		modelAndModel.addObject(OfferDto.OFFER_ID, offerId);

		return modelAndModel;
	}

	@ExceptionHandler(value = ServiceException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleExceptions(HttpServletRequest request, ServiceException exception, Locale locale) {
		ModelAndView modelAndView = new ModelAndView(VIEW_PAYMENTS_PAYPAL);

		final String message = messageSource.getMessage(exception.getErrorCode(), null, locale);
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

	public void setOfferService(OfferService offerService) {
		this.offerService = offerService;
	}
}