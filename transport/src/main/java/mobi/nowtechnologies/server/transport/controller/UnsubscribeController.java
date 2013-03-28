package mobi.nowtechnologies.server.transport.controller;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import mobi.nowtechnologies.server.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@Controller
public class UnsubscribeController extends CommonController{
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UnsubscribeController.class);

	private static final XPathExpression PHONE_NUMBER_XPATHEXPRESSION;
	private static final XPathExpression OPERATOR_XPATHEXPRESSION;
	
	static{
		XPathFactory xPathFactory = XPathFactory.newInstance();
	    XPath xPath = xPathFactory.newXPath();
	    try {
			PHONE_NUMBER_XPATHEXPRESSION = xPath.compile("//member[contains(name,'MSISDN') or contains(name,'*MSISDN*') or contains(key,'MSISDN') or contains(key,'*MSISDN*')]/value");
			OPERATOR_XPATHEXPRESSION = xPath.compile("//member[contains(name,'NETWORK') or contains(name,'*NETWORK*') or contains(key,'NETWORK') or contains(key,'*NETWORK*')]/value");
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
			"/{community:.+}/{apiVersion:[1-9][0-9]\\.[1-9][0-9]\\.[1-9][0-9]{0,2}}/stop_subscription"})
	public @ResponseBody String unsubscribe(@RequestBody String body, @PathVariable("community") String community) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		LOGGER.debug("input parameters body, community: [{}], [{}]", body, community);
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	    domFactory.setNamespaceAware(true); // never forget this!
	    StringReader characterStream = new StringReader(body);
		
	    InputSource source = new InputSource(characterStream);
	   
	    String receivedPhoneNumber = (String) PHONE_NUMBER_XPATHEXPRESSION.evaluate(source, XPathConstants.STRING);
	    String phoneNumber = receivedPhoneNumber.replaceAll("\\*", "");
	    
	    characterStream = new StringReader(body);
	    source = new InputSource(characterStream);
	    String receivedOperatorName = (String) OPERATOR_XPATHEXPRESSION.evaluate(source, XPathConstants.STRING);
	    String operatorName = receivedOperatorName.replaceAll("\\*", "");
	    
	    userService.unsubscribeUser(phoneNumber, operatorName);
	    
	    String message = messageSource.getMessage(community, "unsubscribe.mrs.message", null, null);
	    
	    LOGGER.debug("Output parameter message=[{}]", message);
	    return message;
	}

}
