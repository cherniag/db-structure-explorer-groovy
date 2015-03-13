package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.Country;
import mobi.nowtechnologies.server.service.CountryService;
import mobi.nowtechnologies.server.service.OfferService;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.admin.OfferDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.CreditCardDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.CreditCardDto.Action;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.web.validator.PaymentsCreditCardValidator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.util.List;
import java.util.Locale;

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
public class OfferPaymentsCreditCardController extends CommonController {

    public static final String VIEW_PAYMENTS_CREDITCARD = "offer/payments/creditcard";
    public static final String VIEW_PAYMENTS_CREDITCARD_PREVIEW = "offer/payments/creditcard_preview";
    public static final String VIEW_PAYMENTS_CREDITCARD_DETAILS = "offer/payments/creditcard_details";
    public static final String VIEW_CREATE_PAYMENT_DETAIL_SUCCESSFUL = "offer/payments/creditcard_details_successful";
    public static final String VIEW_CREATE_PAYMENT_DETAIL_FAIL = "offer/payments/creditcard_details_fail";

    public static final String PAGE_PAYMENTS_CREDITCARD = "offers/{offerId}/payments/creditcard.html";
    public static final String PAGE_CREATE_PAYMENT_DETAILS = "offers/{offerId}/payments/creditcard_details.html";

    private CountryService countryService;
    private PaymentDetailsService paymentDetailsService;
    private OfferService offerService;

    @InitBinder(CreditCardDto.NAME)
    public void initBinder(HttpServletRequest request, WebDataBinder binder) {
        binder.setValidator(new PaymentsCreditCardValidator());
    }

    @RequestMapping(value = PAGE_PAYMENTS_CREDITCARD, method = RequestMethod.GET)
    public ModelAndView getCreditCardPaymentsPage(@PathVariable(OfferDto.OFFER_ID) Integer offerId) {
        ModelAndView modelAndView = new ModelAndView(VIEW_PAYMENTS_CREDITCARD);

        OfferDto offer = offerService.getOfferDto(offerId);

        modelAndView.addObject(CreditCardDto.NAME, new CreditCardDto());
        modelAndView.addAllObjects(CreditCardDto.staticData);
        List<Country> countries = countryService.getAllCountries();
        modelAndView.addObject("countries", countries);
        modelAndView.addObject(OfferDto.OFFER_DTO, offer);

        return modelAndView;
    }

    @RequestMapping(value = PAGE_PAYMENTS_CREDITCARD, method = RequestMethod.POST)
    public ModelAndView postCreditCardPaymentsPreview(@PathVariable(OfferDto.OFFER_ID) Integer offerId, @Valid @ModelAttribute(CreditCardDto.NAME) CreditCardDto creditCardDto, BindingResult result) {
        ModelAndView modelAndView = new ModelAndView();

        OfferDto offer = offerService.getOfferDto(offerId);

        modelAndView.addAllObjects(CreditCardDto.staticData);
        List<Country> countries = countryService.getAllCountries();
        modelAndView.addObject("countries", countries);
        modelAndView.addObject(OfferDto.OFFER_DTO, offer);

        if (result.hasErrors() || creditCardDto.getAction() == Action.EDIT) {
            creditCardDto.setAction(Action.PREVIEW);
            modelAndView.setViewName(VIEW_PAYMENTS_CREDITCARD);
        } else {
            creditCardDto.setAction(Action.EDIT);
            modelAndView.setViewName(VIEW_PAYMENTS_CREDITCARD_PREVIEW);
        }

        return modelAndView;
    }

    @RequestMapping(value = PAGE_CREATE_PAYMENT_DETAILS, method = RequestMethod.POST)
    public ModelAndView buyByCreditCardPaymentDetails(@PathVariable(OfferDto.OFFER_ID) Integer offerId, HttpServletResponse response,
                                                      @Valid @ModelAttribute(CreditCardDto.NAME) CreditCardDto creditCardDto, BindingResult result,
                                                      @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) Cookie communityUrl) {
        ModelAndView modelAndView = new ModelAndView();

        if (result.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            modelAndView.setViewName(VIEW_CREATE_PAYMENT_DETAIL_FAIL);
        } else {
            paymentDetailsService.buyByCreditCardPaymentDetails(creditCardDto, communityUrl.getValue(), getSecurityContextDetails().getUserId(), offerId);
            modelAndView.setViewName(VIEW_CREATE_PAYMENT_DETAIL_SUCCESSFUL);
        }

        return modelAndView;
    }

    @ExceptionHandler(value = ServiceException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handlerException(ServiceException serviceException, HttpServletRequest request, HttpServletResponse response, Locale locale) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(VIEW_CREATE_PAYMENT_DETAIL_FAIL);

        final String message = messageSource.getMessage(serviceException.getErrorCodeForMessageLocalization(), null, locale);
        if (serviceException instanceof ExternalServiceException) {
            modelAndView.addObject("external_error", message);
        } else {
            modelAndView.addObject("internal_error", message);
        }

        return modelAndView;
    }

    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    public void setPaymentDetailsService(PaymentDetailsService paymentDetailsService) {
        this.paymentDetailsService = paymentDetailsService;
    }

    public void setOfferService(OfferService offerService) {
        this.offerService = offerService;
    }
}