package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.MigPaymentService;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.net.URLDecoder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Maksym Chernolevskyi (maksym)
 */
@Controller
public class MigController extends CommonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MigController.class);

    private static final String STOP = "Stop";

    @Resource(name = "service.UserService")
    private UserService userService;

    @Resource
    private MigPaymentService migPaymentService;


    @RequestMapping(method = RequestMethod.GET, value = "/DRListener")
    public
    @ResponseBody
    String callback(@RequestParam(value = "MESSAGEID") String messageId, @RequestParam(value = "STATUSTYPE") String statusType, @RequestParam(value = "GUID") String guid,
                    @RequestParam(value = "STATUS") String status, @RequestParam(value = "DESCRIPTION", required = false) String description, HttpServletRequest request) {
        LOGGER.info("[START] MIG query string is [{}]", request.getQueryString());
        LOGGER.info("DRListener command processing started. MESSAGEID=[{}], STATUSTYPE=[{}], GUID=[{}], STATUS=[{}]", new String[] {messageId, statusType, guid, status});
        User user = null;
        Exception ex = null;
        try {
            if (messageId == null) {
                throw new NullPointerException("The parameter messageId is null");
            }

            String decodeDescription = "";
            if (StringUtils.hasText(description)) {
                decodeDescription = URLDecoder.decode(description, "UTF-8");
            }

            SubmittedPayment submittedPayment = migPaymentService.commitPayment(messageId, status, decodeDescription);
            if (submittedPayment != null) {
                user = submittedPayment.getUser();
            }
            return "000";
        } catch (Exception e) {
            ex = e;
            LOGGER.error("error processing DRListener command", e);
        } finally {
            logProfileData(null, null, null, null, user, ex);
            LOGGER.info("[DONE] invoking DRListener command");
        }
        return "";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/MOListener")
    public
    @ResponseBody
    void stopService(@RequestParam(value = "BODY") String action, @RequestParam(value = "OADC") String mobile, @RequestParam(value = "CONNECTION") String operatorMigName) {
        LOGGER.info("[START] MOLISTENER command processing started");
        User user = null;
        Exception ex = null;
        boolean hasNoSuchActivatedPaymentDetails = false;
        try {
            if (STOP.equalsIgnoreCase(action)) {
                List<PaymentDetails> paymentDetails = userService.unsubscribeUser(mobile, operatorMigName);
                hasNoSuchActivatedPaymentDetails = paymentDetails.isEmpty();
                if (paymentDetails != null && !hasNoSuchActivatedPaymentDetails && paymentDetails.get(0) != null) {
                    user = paymentDetails.get(0).getOwner();
                }
            } else {
                throw new IllegalStateException("action [" + action + "] not supported");
            }
        } catch (Exception e) {
            ex = e;
            LOGGER.error("error processing MOLISTENER command", e);
        } finally {
            if (hasNoSuchActivatedPaymentDetails) {
                ex = new Exception("No such activated payment details");
            }
            logProfileData(null, null, null, null, user, ex);
            LOGGER.info("[DONE] invoking MOListener command");
        }
    }

}
