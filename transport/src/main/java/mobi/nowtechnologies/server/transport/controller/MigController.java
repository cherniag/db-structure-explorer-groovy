package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.MigPaymentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URLDecoder;
import java.util.List;

/**
 * MigController
 * 
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
@Controller
public class MigController extends ProfileController {
	private static final Logger LOGGER = LoggerFactory.getLogger(MigController.class);

	private static final String STOP = "Stop";

	private UserService userService;
	private MigPaymentService migPaymentService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setMigPaymentService(MigPaymentService migPaymentService) {
		this.migPaymentService = migPaymentService;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/DRListener")
	public void callback(@RequestParam(value = "MESSAGEID") String messageId,
			@RequestParam(value = "STATUSTYPE") String statusType,
			@RequestParam(value = "GUID") String guid,
			@RequestParam(value = "STATUS") String status,
			@RequestParam(value = "DESCRIPTION", required = false) String description,
			HttpServletResponse response,
			HttpServletRequest request) {
		LOGGER.info("[START] MIG query string is [{}]", request.getQueryString());
		LOGGER.info("DRListener command processing started. MESSAGEID=[{}], STATUSTYPE=[{}], GUID=[{}], STATUS=[{}]", new String[] { messageId, statusType, guid, status });
		User user = null;
		boolean isFailed = false;
		try {
			if (messageId == null)
				throw new NullPointerException("The parameter messageId is null");

			String decodeDescription = "";
			if (StringUtils.hasText(description))
				decodeDescription = URLDecoder.decode(description, "UTF-8");

			SubmittedPayment submittedPayment = migPaymentService.commitPayment(messageId, status, decodeDescription);
			if (submittedPayment != null) {
				user = submittedPayment.getUser();
			}
		} catch (Exception e) {
			isFailed = true;
			logProfileData(null, null, null, null, user, e);
			LOGGER.error("error processing DRListener command", e);
		} finally {
			if (!isFailed) {
				logProfileData(null, null, null, null, user, null);
			}
			LOGGER.info("[DONE] invoking DRListener command");
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/MOListener")
	public void stopService(@RequestParam(value = "BODY") String action,
			@RequestParam(value = "OADC") String mobile,
			@RequestParam(value = "CONNECTION") String operatorMigName,
			HttpServletResponse response,
			HttpServletRequest request) {
		// Only 1 case when we get this request from MIG - stop service
		LOGGER.info("[START] MOLISTENER command processing started");
		User user = null;
		boolean isFailed = false;
		try {
			if (STOP.equalsIgnoreCase(action)) {
				List<PaymentDetails> paymentDetails = userService.unsubscribeUser(mobile, operatorMigName);
				if (paymentDetails != null && !paymentDetails.isEmpty() && paymentDetails.get(0) != null) {
					user = paymentDetails.get(0).getOwner();
				}
			} else {
				throw new IllegalStateException("action [" + action + "] not supported");
			}
		} catch (Exception e) {
			isFailed = true;
			logProfileData(null, null, null, null, user, e);
			LOGGER.error("error processing MOLISTENER command", e);
		} finally {
			if (!isFailed) {
				logProfileData(null, null, null, null, user, null);
			}
			LOGGER.info("[DONE] invoking MOListener command");
		}
	}

}
