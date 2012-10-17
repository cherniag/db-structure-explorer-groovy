package mobi.nowtechnologies.server.admin.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.server.assembler.AccountLogAsm;
import mobi.nowtechnologies.server.assembler.PendingPaymentAsm;
import mobi.nowtechnologies.server.assembler.SubmittedPaymentAsm;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.AccountLogService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.PendingPaymentService;
import mobi.nowtechnologies.server.service.payment.SubmitedPaymentService;
import mobi.nowtechnologies.server.shared.dto.admin.AccountLogDto;
import mobi.nowtechnologies.server.shared.dto.admin.PendingPaymentDto;
import mobi.nowtechnologies.server.shared.dto.admin.SubmittedPaymentDto;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

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
public class AccountLogController extends AbstractCommonController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountLogController.class);

	private AccountLogService accountLogService;
	private PendingPaymentService pendingPaymentService;
	private SubmitedPaymentService submitedPaymentService;
	private UserService userService;

	public void setAccountLogService(AccountLogService accountLogService) {
		this.accountLogService = accountLogService;
	}

	public void setPendingPaymentService(PendingPaymentService pendingPaymentService) {
		this.pendingPaymentService = pendingPaymentService;
	}

	public void setSubmitedPaymentService(SubmitedPaymentService submitedPaymentService) {
		this.submitedPaymentService = submitedPaymentService;
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping(value = "/accountLogs/{userId}", method = RequestMethod.GET)
	public ModelAndView getTransactionHistory(HttpServletRequest request, @PathVariable(value = "userId") Integer userId) {
		LOGGER.debug("input parameters request, userId: [{}], [{}]", request, userId);

		User user = userService.findById(userId);
		List<AccountLog> accountLogs = accountLogService.findByUserId(userId);
		List<PendingPayment> pendingPayments = pendingPaymentService.getPendingPayments(userId);
		List<SubmittedPayment> submittedPayments = submitedPaymentService.findByUserIdAndPaymentStatus(Arrays.asList(userId), Arrays.asList(
				PaymentDetailsStatus.ERROR, PaymentDetailsStatus.EXTERNAL_ERROR));

		List<AccountLogDto> accountLogDtos = AccountLogAsm.toAccountLogDtos(accountLogs);
		List<PendingPaymentDto> pendingPaymentDtos = PendingPaymentAsm.toPendingPaymentDtos(pendingPayments);
		List<SubmittedPaymentDto> submittedPaymentDtos = SubmittedPaymentAsm.toSubmittedPaymentDtos(submittedPayments);

		final ModelAndView modelAndView = new ModelAndView("transactionHistory/transactionHistory");
		modelAndView.addObject(AccountLogDto.ACCOUNT_LOG_DTO_LIST, accountLogDtos);
		modelAndView.addObject(PendingPaymentDto.PENDING_PAYMENT_DTO_LIST, pendingPaymentDtos);
		modelAndView.addObject(SubmittedPaymentDto.SUBMITTED_PAYMENT_DTO_LIST, submittedPaymentDtos);
		modelAndView.addObject("userName", user.getUserName());

		LOGGER.info("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

}
