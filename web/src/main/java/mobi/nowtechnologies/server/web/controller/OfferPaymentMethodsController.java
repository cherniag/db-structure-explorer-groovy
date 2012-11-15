package mobi.nowtechnologies.server.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.shared.dto.web.OfferPaymentPolicyDto;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@Controller
public class OfferPaymentMethodsController extends CommonController {

	private PaymentPolicyService paymentPolicyService;

	public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
		this.paymentPolicyService = paymentPolicyService;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(OfferPaymentMethodsController.class);

	@RequestMapping(value = "/offers/{offerId}/payments.html", method = RequestMethod.GET)
	public ModelAndView getOfferPaymentMethodsPage(HttpServletRequest request, @PathVariable("offerId") Integer offerId) {
		LOGGER.debug("input parameters request, offerId: [{}], [{}]", request, offerId);

		String communityURL = RequestUtils.getCommunityURL();

		List<OfferPaymentPolicyDto> offerPaymentPolicyDtos = paymentPolicyService.getOfferPaymentPolicyDto(communityURL);

		ModelAndView modelAndView = new ModelAndView("offer/payments");
		modelAndView.addObject(OfferPaymentPolicyDto.OFFER_PAYMENT_POLICY_DTO_LIST, offerPaymentPolicyDtos);
		modelAndView.addObject("offerId", offerId);

		LOGGER.debug("Output parameter [{}]", modelAndView);
		return modelAndView;
	}

}
