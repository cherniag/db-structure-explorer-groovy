package mobi.nowtechnologies.server.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.sentaca.spring.smpp.SMPPService;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 9/25/13
 * Time: 1:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class SMPPServiceImpl {

    public void sendSMS(String msg){

    }

    public void xtestMinimal() throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("minimal.xml");
        SMPPService service = context.getBean(SMPPServiceImpl.class);

        Thread.sleep(5000);
        service.send(new MTMessage("123", "123123", "Hello! There test3"));
        Thread.sleep(6000);
        context.close();
    }

    public void xtestMerging() throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("merging.xml");
        SMPPService service = context.getBean(SMPPServiceImpl.class);

        Thread.sleep(5000);
        service.send(new MTMessage("123", "123123", "Hello! There test3"));
        Thread.sleep(6000);
        context.close();
    }
}
