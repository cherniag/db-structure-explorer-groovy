package mobi.nowtechnologies.server.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml",
		"/META-INF/service-test.xml", "/META-INF/shared.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class MailServiceIT {
	
	private MailService mailService;
	
	@Autowired
	@Qualifier("serviceMessageSource")
	private MessageSource messageSource;
	
	private String templateString = "Dear %username%,\n Welcome to %product_name%. Thank you for registration";
	
	@Before
	public void startup() {
		mailService = new MailService();
	}
	
	@Test
	public void processMailText_Successful() {
		String username = "User1";
		String productName = "Charts Now";
		String finalString = "Dear "+username+",\n Welcome to "+productName+". Thank you for registration";
		
		Map<String, String> model = new HashMap<String, String>();
			model.put("username", username);
			model.put("product_name", productName);
		String processedString = MailTemplateProcessor.processTemplateString(templateString, model);
		
		Assert.assertEquals(finalString, processedString);
	}
	
	@Test
	public void noModelVariableInTemplate() {
		String username = "User1";
		String finalString = "Dear "+username+",\n Welcome to %product_name%. Thank you for registration";
		
		Map<String, String> model = new HashMap<String, String>();
			model.put("username", username);
		String processedString = MailTemplateProcessor.processTemplateString(templateString, model);
		
		Assert.assertEquals(finalString, processedString);
	}
	
	@Test
	public void modePrecentSignsInTemplate() {
		String templateString = "Dear %%username%%,\n Welcome to %%product_name%%. Thank you %%username%%% for registration in %%%%%product_name%%";
		
		String username = "User1";
		String productName = "Charts Now";
		String finalString = "Dear %"+username+"%,\n Welcome to %"+productName+"%. Thank you %"+username+"%% for registration in %%%%"+productName+"%";
		
		Map<String, String> model = new HashMap<String, String>();
			model.put("username", username);
			model.put("product_name", productName);
		String processedString = MailTemplateProcessor.processTemplateString(templateString, model);
		
		Assert.assertEquals(finalString, processedString);
	}
}