package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.StringReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Titov Mykhaylo (titov)
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

    @RequestMapping(method = RequestMethod.POST,
                    value = {"/{community:.+}/{apiVersion:6\\.12}/stop_subscription", "/{community:.+}/{apiVersion:6\\.11}/stop_subscription", "/{community:.+}/{apiVersion:6\\.10}/stop_subscription",
                             "/{community:.+}/{apiVersion:6\\.9}/stop_subscription", "/{community:.+}/{apiVersion:6\\.8}/stop_subscription",
                        "/{community:.+}/{apiVersion:6\\.7}/stop_subscription", "/{community:.+}/{apiVersion:6\\.6}/stop_subscription", "/{community:.+}/{apiVersion:6\\.5}/stop_subscription",
                        "/{community:.+}/{apiVersion:6\\.4}/stop_subscription", "/{community:.+}/{apiVersion:6\\.3}/stop_subscription", "/{community:.+}/{apiVersion:6\\.2}/stop_subscription",
                        "/{community:.+}/{apiVersion:6\\.1}/stop_subscription", "/{community:.+}/{apiVersion:6\\.0}/stop_subscription", "/{community:.+}/{apiVersion:5\\.0}/stop_subscription",
                        "/{community:.+}/{apiVersion:4\\.2}/stop_subscription", "/{community:.+}/{apiVersion:4\\.1}/stop_subscription", "/{community:.+}/{apiVersion:4\\.0}/stop_subscription",
                        "/{community:.+}/{apiVersion:[1-9][0-9]\\.[1-9][0-9]\\" + ".[1-9][0-9]{0,2}}/stop_subscription",
                        "/{community:.+}/{apiVersion:[3-9]\\.[1-9][0-9]\\.[1-9][0-9]{0,2}}/stop_subscription",
                        "/{community:.+}/{apiVersion:[3-9]\\.[8-9]\\.[1-9][0-9]{0,2}}/stop_subscription", "/{community:.+}/{apiVersion:[1-9][0-9]\\.[1-9][0-9]}/stop_subscription",
                        "/{community:" + ".+}/{apiVersion:[1-9][0-9]\\.[0-9]}/stop_subscription", "/{community:.+}/{apiVersion:[3-9]\\.[1-9][0-9]}/stop_subscription",
                        "/{community:.+}/{apiVersion:[3-9]\\" + ".[8-9]}/stop_subscription"})
    public
    @ResponseBody
    String unsubscribe(@RequestBody String body, @PathVariable("community") String community) throws Exception {
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
            message = messageSource.getMessage(community, "unsubscribe.mrs.message.payment.details.not.found", null, null);
        } else {
            message = messageSource.getMessage(community, "unsubscribe.mrs.message", null, null);
        }

        LOGGER.info("Output parameter message=[{}]", message);
        return message;
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
