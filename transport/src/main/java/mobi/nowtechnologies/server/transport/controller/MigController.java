package mobi.nowtechnologies.server.transport.controller;

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

/**
 * MigController
 * 
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
@Controller
public class MigController {
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
	public void callback(@RequestParam (value = "MESSAGEID") String messageId,
			@RequestParam (value = "STATUSTYPE") String statusType,
			@RequestParam (value = "GUID") String guid,
			@RequestParam (value = "STATUS") String status,
			@RequestParam (value = "DESCRIPTION", required=false) String description,
			HttpServletResponse response,
			HttpServletRequest request) {
		LOGGER.info("[START] MIG query string is [{}]",request.getQueryString());
		LOGGER.info("DRListener command processing started. MESSAGEID=[{}], STATUSTYPE=[{}], GUID=[{}], STATUS=[{}]", new String[] {messageId, statusType, guid, status });
		try {
			if (messageId == null)
				throw new NullPointerException("The parameter messageId is null");
			
			String decodeDescription = "";
			if (StringUtils.hasText(description))
				decodeDescription = URLDecoder.decode(description, "UTF-8");
			
			migPaymentService.commitPayment(messageId, status, decodeDescription);
		} catch (Exception e) {
			LOGGER.error("error processing DRListener command", e);
		} finally {
			LOGGER.info("[DONE] invoking DRListener command");
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/MOListener")
	public void stopService(@RequestParam (value = "BODY") String action,
			@RequestParam (value = "OADC") String mobile,
			@RequestParam (value = "CONNECTION") String operatorMigName,
			HttpServletResponse response,
			HttpServletRequest request) {
		//Only 1 case when we get this request from MIG - stop service
		LOGGER.info("[START] MOLISTENER command processing started");
		try {
			if (STOP.equalsIgnoreCase(action))
				userService.unsubscribeUser(mobile, operatorMigName);
			else
				throw new IllegalStateException("action [" + action + "] not supported");
		} catch (Exception e) {
			LOGGER.error("error processing MOLISTENER command", e);
		} finally {
			LOGGER.info("[DONE] invoking MOListener command");
		}
	}
	
}
