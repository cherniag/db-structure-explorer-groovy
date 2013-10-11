package mobi.nowtechnologies.server.service;

import com.sentaca.spring.smpp.SMPPService;
import com.sentaca.spring.smpp.mt.MTMessage;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * User: Alexsandr_Kolpakov
 * Date: 9/25/13
 * Time: 1:06 PM
 */
public class SMPPServiceImpl {

    public void sendSMS(String msg){

    }

    public void xtestMinimal() throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("minimal.xml");
        SMPPService service = context.getBean(SMPPService.class);

        Thread.sleep(5000);
        service.send(new MTMessage("123", "123123", "Hello! There test3"));
        Thread.sleep(6000);
        context.close();
    }

    public void xtestMerging() throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("merging.xml");
        SMPPService service = context.getBean(SMPPService.class);

        Thread.sleep(5000);
        service.send(new MTMessage("123", "123123", "Hello! There test3"));
        Thread.sleep(6000);
        context.close();
    }
}
