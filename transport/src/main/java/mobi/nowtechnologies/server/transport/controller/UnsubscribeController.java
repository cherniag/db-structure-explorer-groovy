package mobi.nowtechnologies.server.transport.controller;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import mobi.nowtechnologies.common.util.ServerMessage;
import mobi.nowtechnologies.server.persistence.domain.ErrorMessage;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@Controller
public class UnsubscribeController extends CommonController {

	private static final String UNSUBSCRIBE_MRS_UNPARSABLEXML_OPERATOR = "unsubscribe.mrs.unparsablexml.operator";

	private static final String UNSUBSCRIBE_MRS_UNPARSABLEXML_PHONE = "unsubscribe.mrs.unparsablexml.phone";

	private static final Logger LOGGER = LoggerFactory.getLogger(UnsubscribeController.class);

	private static final XPathExpression PHONE_NUMBER_XPATHEXPRESSION;
	private static final XPathExpression OPERATOR_XPATHEXPRESSION;
	private static final String NO_PAYMENT_DETAILS_FOUND_MESSAGE_CODE = "unsubscribe.mrs.nopaymentdetailsfound";

	static {
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xPath = xPathFactory.newXPath();
		try {
			PHONE_NUMBER_XPATHEXPRESSION = xPath.compile("//member[name='MSISDN' or name='*MSISDN*' or key='MSISDN' or key='*MSISDN*']/value");
			OPERATOR_XPATHEXPRESSION = xPath.compile("//member[name='NETWORK' or name='*NETWORK*' or key='NETWORK' or key='*NETWORK*']/value");
		} catch (XPathExpressionException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	private UserService userService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping(method = RequestMethod.POST, value = {
			"/{community:.+}/{apiVersion:[3-9]\\.[8-9]}/stop_subscription",
			"/{community:.+}/{apiVersion:[3-9]\\.[1-9][0-9]}/stop_subscription",
			"/{community:.+}/{apiVersion:[1-9][0-9]\\.[0-9]}/stop_subscription",
			"/{community:.+}/{apiVersion:[1-9][0-9]\\.[1-9][0-9]}/stop_subscription",
			"/{community:.+}/{apiVersion:[3-9]\\.[8-9]\\.[1-9][0-9]{0,2}}/stop_subscription",
			"/{community:.+}/{apiVersion:[3-9]\\.[1-9][0-9]\\.[1-9][0-9]{0,2}}/stop_subscription",
			"/{community:.+}/{apiVersion:[1-9][0-9]\\.[1-9][0-9]\\.[1-9][0-9]{0,2}}/stop_subscription",
            "/{community:.+}/{apiVersion:4\\.0}/stop_subscription",
            "/{community:.+}/{apiVersion:4\\.1}/stop_subscription",
            "/{community:.+}/{apiVersion:4\\.2}/stop_subscription"
    })
	public @ResponseBody
	String unsubscribe(@RequestBody String body, @PathVariable("community") String community) throws Exception {
		User user = null;
		Exception ex = null;
		boolean hasNoSuchActivatedPaymentDetails = false;
		try {
			LOGGER.info("command processing started");
			LOGGER.info("input parameters body, community: [{}], [{}]", body, community);

			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true); // never forget this!
			StringReader characterStream = new StringReader(body);

			InputSource source = new InputSource(characterStream);

			String receivedPhoneNumber = (String) PHONE_NUMBER_XPATHEXPRESSION.evaluate(source, XPathConstants.STRING);
			String phoneNumber = receivedPhoneNumber.replaceAll("\\*", "");
			if (phoneNumber.isEmpty()) {
				throw new ServiceException(UNSUBSCRIBE_MRS_UNPARSABLEXML_PHONE, "Couldn't parse phone number (MSISDN)");
			}

			characterStream = new StringReader(body);
			source = new InputSource(characterStream);
			String receivedOperatorName = (String) OPERATOR_XPATHEXPRESSION.evaluate(source, XPathConstants.STRING);
			String operatorName = receivedOperatorName.replaceAll("\\*", "");
			if (operatorName.isEmpty()) {
				throw new ServiceException(UNSUBSCRIBE_MRS_UNPARSABLEXML_OPERATOR, "Couldn't parse operator name (NETWORK)");
			}

			List<PaymentDetails> paymentDetailsList = userService.unsubscribeUser(phoneNumber, operatorName);

			String message;
			if (paymentDetailsList.isEmpty()) {
				hasNoSuchActivatedPaymentDetails = true;
				message = messageSource.getMessage(community, "unsubscribe.mrs.message.payment.details.not.found", null, null);
			} else {
				final PaymentDetails paymentDetails = paymentDetailsList.get(0);
				if (paymentDetails != null) {
					user = paymentDetails.getOwner();
				}
				message = messageSource.getMessage(community, "unsubscribe.mrs.message", null, null);
			}

			LOGGER.info("Output parameter message=[{}]", message);
			return message;
		} catch (Exception e) {
			ex = e;
			throw e;
		} finally {
			if (hasNoSuchActivatedPaymentDetails){
				ex = new Exception("No such activated payment details");
			}
			logProfileData(null, community, null, null, user, ex);
			LOGGER.info("command processing finished");
		}
	}

	@ExceptionHandler(ServiceException.class)
	public ModelAndView handleException(ServiceException serviceException, HttpServletRequest httpServletRequest, HttpServletResponse response) {
		ModelAndView modelAndView = super.handleException(serviceException, httpServletRequest, response);

		final String errorCodeForMessageLocalization = serviceException.getErrorCodeForMessageLocalization();
		if (NO_PAYMENT_DETAILS_FOUND_MESSAGE_CODE.equals(errorCodeForMessageLocalization)) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		} else if (UNSUBSCRIBE_MRS_UNPARSABLEXML_PHONE.equals(errorCodeForMessageLocalization) || UNSUBSCRIBE_MRS_UNPARSABLEXML_OPERATOR.equals(errorCodeForMessageLocalization)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
		}

		return modelAndView;
	}

}
