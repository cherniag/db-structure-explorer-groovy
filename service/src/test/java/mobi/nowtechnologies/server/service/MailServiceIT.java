package mobi.nowtechnologies.server.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class MailServiceIT {

    MailService mailService;

    String templateString = "Dear %username%,\n Welcome to %product_name%. Thank you for registration";

    @Before
    public void startup() {
        mailService = new MailService();
    }

    @Test
    public void processMailText_Successful() {
        String username = "User1";
        String productName = "Charts Now";
        String finalString = "Dear " + username + ",\n Welcome to " + productName + ". Thank you for registration";

        Map<String, String> model = new HashMap<String, String>();
        model.put("username", username);
        model.put("product_name", productName);
        String processedString = MailTemplateProcessor.processTemplateString(templateString, model);

        Assert.assertEquals(finalString, processedString);
    }

    @Test
    public void noModelVariableInTemplate() {
        String username = "User1";
        String finalString = "Dear " + username + ",\n Welcome to %product_name%. Thank you for registration";

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
        String finalString = "Dear %" + username + "%,\n Welcome to %" + productName + "%. Thank you %" + username + "%% for registration in %%%%" + productName + "%";

        Map<String, String> model = new HashMap<String, String>();
        model.put("username", username);
        model.put("product_name", productName);
        String processedString = MailTemplateProcessor.processTemplateString(templateString, model);

        Assert.assertEquals(finalString, processedString);
    }
}