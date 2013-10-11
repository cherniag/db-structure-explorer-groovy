package mobi.nowtechnologies.server.service;

import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.*;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.*;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.jsmpp.util.TimeFormatter;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 10/1/13
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class SMPPSubmitRegisteredTestIT {
    private static TimeFormatter timeFormatter = new AbsoluteTimeFormatter();;

    public static void main(String[] args) {
        SMPPSession session = new SMPPSession();
        // Set listener to receive deliver_sm
        session.setMessageReceiverListener(new MessageReceiverListenerImplTest());

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

            String phoneNumber = "+642111111111";
//            String phoneNumber = "+64279000456";

            String messageId = session.submitShortMessage("CMT", TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN, "5804", TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN, phoneNumber, new ESMClass(),
                    (byte)0, (byte)0, null, null, new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT), (byte)0, ZERO, (byte)0, "It is another 123".getBytes());

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

    public static class MessageReceiverListenerImplTest implements MessageReceiverListener {
        public void onAcceptDeliverSm(DeliverSm deliverSm)
                throws ProcessRequestException {

            if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {
                // this message is delivery receipt
                try {
                    DeliveryReceipt delReceipt = deliverSm.getShortMessageAsDeliveryReceipt();

                    // lets cover the id to hex string format
                    long id = Long.parseLong(delReceipt.getId()) & 0xffffffff;
                    String messageId = Long.toString(id, 16).toUpperCase();

                /*
                 * you can update the status of your submitted message on the
                 * database based on messageId
                 */

                    System.out.println("Receiving delivery receipt for message '" + messageId + " ' from " + deliverSm.getSourceAddr() + " to " + deliverSm.getDestAddress() + " : " + delReceipt);
                } catch (InvalidDeliveryReceiptException e) {
                    System.err.println("Failed getting delivery receipt");
                    e.printStackTrace();
                }
            } else {
                // this message is regular short message

            /*
             * you can save the incoming message to database.
             */

                System.out.println("Receiving message : " + new String(deliverSm.getShortMessage()));
            }
        }

        public void onAcceptAlertNotification(AlertNotification alertNotification) {
        }

        public DataSmResult onAcceptDataSm(DataSm dataSm, Session source)
                throws ProcessRequestException {
            return null;
        }
    }

}

