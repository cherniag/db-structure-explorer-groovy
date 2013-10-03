package mobi.nowtechnologies.server.service;

import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.*;
import org.jsmpp.examples.MessageReceiverListenerImpl;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 10/1/13
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubmitRegisteredTestIT {
    private static TimeFormatter timeFormatter = new AbsoluteTimeFormatter();;

    public static void main(String[] args) {
        SMPPSession session = new SMPPSession();
        // Set listener to receive deliver_sm
        session.setMessageReceiverListener(new MessageReceiverListenerImpl());

        DataCoding ZERO = new DataCoding() {
            @Override
            public byte value() {
                return 0;
            }
        };


        try {
            session.connectAndBind("localhost", 5000, new BindParameter(BindType.BIND_TRX, "MQ", "ZnFeSn77", "SMPP", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
        } catch (IOException e) {
            System.err.println("Failed connect and bind to host");
            e.printStackTrace();
        }

        try {
            String messageId = session.submitShortMessage("CMT", TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN, "4003", TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN, "64279000456", new ESMClass(),
                    (byte)0, (byte)0, null, null, new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE), (byte)0, ZERO, (byte)0, "It is another 123".getBytes());

            /*
             * you can save the submitted message to database.
             */

            System.out.println("Message submitted, message_id is " + messageId);
            Thread.sleep(10000);

//            QuerySmResult response = session.queryShortMessage(messageId, TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN, "4003");
//            System.out.println("Message queried, message_id is " + messageId + ", errorCode is " + response.getErrorCode() +  ", msgState is " + response.getMessageState() +  ", finalDate is " + response.getFinalDate());

            //Thread.sleep(2000);
        } catch (PDUException e) {
            // Invalid PDU parameter
            System.err.println("Invalid PDU parameter");
            e.printStackTrace();
        } catch (ResponseTimeoutException e) {
            // Response timeout
            System.err.println("Response timeout");
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            // Invalid response
            System.err.println("Receive invalid respose");
            e.printStackTrace();
        } catch (NegativeResponseException e) {
            // Receiving negative response (non-zero command_status)
            System.err.println("Receive negative response");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO error occur");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted");
            e.printStackTrace();
        }

        session.unbindAndClose();
    }


}

