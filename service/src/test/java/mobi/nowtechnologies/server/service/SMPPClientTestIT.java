package mobi.nowtechnologies.server.service;

import java.io.File;
import java.io.IOException;

import org.smpp.test.SMPPTest;

/**
 * Created with IntelliJ IDEA. User: Alexsandr_Kolpakov Date: 9/25/13 Time: 4:50 PM To change this template use File | Settings | File Templates.
 */
public class SMPPClientTestIT {

    public static void main(String[] args) throws IOException {
        File is = new File("./smpptest.cfg");
        System.out.println(is.getAbsolutePath());
        SMPPTest.main(new String[0]);
    }
}
