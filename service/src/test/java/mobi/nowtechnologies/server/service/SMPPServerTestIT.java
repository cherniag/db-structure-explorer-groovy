package mobi.nowtechnologies.server.service;

import java.io.File;
import java.io.IOException;

import org.smpp.smscsim.Simulator;

/**
 * User: Alexsandr_Kolpakov Date: 9/25/13 Time: 4:50 PM
 */
public class SMPPServerTestIT {

    public static void main(String[] args) throws IOException {
        File is = new File("etc/users.txt");
        System.out.println(is.getAbsolutePath());
        Simulator.main(new String[0]);
    }
}
