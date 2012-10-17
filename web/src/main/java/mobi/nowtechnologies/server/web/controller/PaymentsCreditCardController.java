package mobi.nowtechnologies.server.web.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import mobi.nowtechnologies.server.persistence.domain.Country;
import mobi.nowtechnologies.server.service.CountryService;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.admin.OfferDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.CreditCardDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.CreditCardDto.Action;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.web.validator.PaymentsCreditCardValidator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PaymentsCreditCardController extends CommonController {

	public static final String VIEW_PAYMENTS_CREDITCARD = "/creditcard";
	public static final String VIEW_PAYMENTS_CREDITCARD_PREVIEW = "/creditcard_preview";
	public static final String VIEW_PAYMENTS_CREDITCARD_DETAILS = "/creditcard_details";
	public static final String VIEW_CREATE_PAYMENT_DETAIL_SUCCESSFUL = "/creditcard_details_successful";
	public static final String VIEW_CREATE_PAYMENT_DETAIL_FAIL = "/creditcard_details_fail";

	public static final String PAGE_PAYMENTS_CREDITCARD = PaymentsController.SCOPE_PREFIX + VIEW_PAYMENTS_CREDITCARD + PAGE_EXT;
	public static final String PAGE_CREATE_PAYMENT_DETAILS = PaymentsController.SCOPE_PREFIX + VIEW_PAYMENTS_CREDITCARD_DETAILS + PAGE_EXT;

	private CountryService countryService;
	private PaymentDetailsService paymentDetailsService;

	@InitBinder(CreditCardDto.NAME)
	public void initBinder(HttpServletRequest request, WebDataBinder binder) {
		binder.setValidator(new PaymentsCreditCardValidator());
	}

	@RequestMapping(value = PAGE_PAYMENTS_CREDITCARD, method = RequestMethod.GET)
	public ModelAndView getCreditCardPaymentsPage(@PathVariable("scopePrefix") String scopePrefix, @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) Cookie communityUrl) {
		PaymentPolicyDto paymentPolicy = paymentDetailsService.getPayPalPaymentPolicy(communityUrl.getValue());

		ModelAndView modelAndView = new ModelAndView(scopePrefix + VIEW_PAYMENTS_CREDITCARD);
		modelAndView.addObject(CreditCardDto.NAME, new CreditCardDto());
		modelAndView.addAllObjects(CreditCardDto.staticData);
		List<Country> countries = countryService.getAllCountries();
		modelAndView.addObject("countries", countries);
		modelAndView.addObject(PaymentPolicyDto.PAYMENT_POLICY_DTO, paymentPolicy);

		return modelAndView;
	}

	@RequestMapping(value = PAGE_PAYMENTS_CREDITCARD, method = RequestMethod.POST)
	public ModelAndView postCreditCardPaymentsPreview(@PathVariable("scopePrefix") String scopePrefix, @Valid @ModelAttribute(CreditCardDto.NAME) CreditCardDto creditCardDto, BindingResult result,
			@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) Cookie communityUrl) {
		PaymentPolicyDto paymentPolicy = paymentDetailsService.getPayPalPaymentPolicy(communityUrl.getValue());
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addAllObjects(CreditCardDto.staticData);
		List<Country> countries = countryService.getAllCountries();
		modelAndView.addObject("countries", countries);
		modelAndView.addObject(PaymentPolicyDto.PAYMENT_POLICY_DTO, paymentPolicy);
		
		if (result.hasErrors() || creditCardDto.getAction() == Action.EDIT) {
			creditCardDto.setAction(Action.PREVIEW);
			modelAndView.setViewName(scopePrefix + VIEW_PAYMENTS_CREDITCARD);
		} else {
			creditCardDto.setAction(Action.EDIT);
			modelAndView.setViewName(scopePrefix + VIEW_PAYMENTS_CREDITCARD_PREVIEW);
		}

		return modelAndView;
	}

	@RequestMapping(value = PAGE_CREATE_PAYMENT_DETAILS, method = RequestMethod.POST)
	public ModelAndView createCreditCardPaymentDetails(@PathVariable("scopePrefix") String scopePrefix, HttpServletResponse response,
			@Valid @ModelAttribute(CreditCardDto.NAME) CreditCardDto creditCardDto,
			BindingResult result, @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) Cookie communityUrl) {
		ModelAndView modelAndView = new ModelAndView();

		if (result.hasErrors()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			modelAndView.setViewName(scopePrefix + VIEW_CREATE_PAYMENT_DETAIL_FAIL);
		} else {
			paymentDetailsService.createCreditCardPamentDetails(creditCardDto, communityUrl.getValue(), getSecurityContextDetails().getUserId());
			modelAndView.setViewName(scopePrefix + VIEW_CREATE_PAYMENT_DETAIL_SUCCESSFUL);
		}

		return modelAndView;
	}

	@ExceptionHandler(value = ServiceException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handlerException(ServiceException serviceException, HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String scopeView = request.getRequestURI().indexOf(PaymentsController.VIEW_MANAGE_PAYMENTS_INAPP) >= 0 ? PaymentsController.VIEW_MANAGE_PAYMENTS_INAPP
				: PaymentsController.VIEW_MANAGE_PAYMENTS;

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(scopeView + VIEW_CREATE_PAYMENT_DETAIL_FAIL);

		final String message = messageSource.getMessage(serviceException.getErrorCode(), null, locale);
		if (serviceException instanceof ExternalServiceException)
			modelAndView.addObject("external_error", message);
		else
			modelAndView.addObject("internal_error", message);

		return modelAndView;
	}

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}

	public void setPaymentDetailsService(PaymentDetailsService paymentDetailsService) {
		this.paymentDetailsService = paymentDetailsService;
	}
}