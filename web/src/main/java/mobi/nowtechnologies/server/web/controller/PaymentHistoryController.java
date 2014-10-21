package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.service.PaymentService;
import mobi.nowtechnologies.server.shared.dto.web.PaymentHistoryItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@Controller
public class PaymentHistoryController extends CommonController{
	private static final String PAYMENT_HISTORY_ITEM_DTOS = "paymentHistoryItemDtos";

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentHistoryController.class);
	
	private PaymentService paymentService;
	
	public void setPaymentService(PaymentService paymentService) {
		this.paymentService = paymentService;
	}
	
	@RequestMapping(value = "/payment_history.html", method = RequestMethod.GET)
	public ModelAndView getUserPaymentHistory(HttpServletRequest request, @RequestParam(value="maxResults", required=false) Integer maxResults, Locale locale) {
		LOGGER.debug("input parameters request, maxResults: [{}], [{}]", request, maxResults);
		
		int userId = getUserId();
		
		List<PaymentHistoryItemDto> paymentHistoryItemDtos = paymentService.findByUserIdOrderedByLogTimestampDesc(userId, maxResults);
		
		for (PaymentHistoryItemDto paymentHistoryItemDto : paymentHistoryItemDtos) {
			String localizedDescription=messageSource.getMessage("transaction_history.historyTable.paymentType."+paymentHistoryItemDto.getDescription(), new Object[]{paymentHistoryItemDto.getPeriod(), paymentHistoryItemDto.getPeriodUnit()}, locale);
			paymentHistoryItemDto.setDescription(localizedDescription);
			String paymentMethod=messageSource.getMessage("transaction_history.historyTable.paymentMethod."+paymentHistoryItemDto.getPaymentMethod(), null, locale);
			paymentHistoryItemDto.setPaymentMethod(paymentMethod);
		}
		
		ModelAndView modelAndView = new ModelAndView("payment_history");
		modelAndView.getModel().put(PAYMENT_HISTORY_ITEM_DTOS, paymentHistoryItemDtos);
		modelAndView.addObject("dateFormat", messageSource.getMessage("transaction_history.formater", null, locale));
		
		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

}
