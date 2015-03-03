package mobi.nowtechnologies.server.service;

import java.io.IOException;

import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.MessageType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.Session;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.jsmpp.util.TimeFormatter;

/*
 * User: Alexsandr_Kolpakov
 * Date: 10/1/13
 * Time: 12:39 PM
 */
public class SMPPSubmitRegisteredTestIT {

    private static TimeFormatter timeFormatter = new AbsoluteTimeFormatter();
    ;

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
            session.connectAndBind("localhost", 5000, new BindParameter(BindType.BIND_TRX, "MQPRD", "u8VrD9ka", "SMPP", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
            //            session.connectAndBind("localhost", 5000, new BindParameter(BindType.BIND_TRX, "MQ", "ZnFeSn77", "SMPP", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
        }
        catch (IOException e) {
            System.err.println("Failed connect and bind to host");
            e.printStackTrace();
        }

        try {

            //            String phoneNumber = "+6425321321";
            //            String phoneNumber = "+64279000456";
            //            String phoneNumber = "+642108398674";
            String phoneNumber = "+642108398674";

            String messageId = session
                .submitShortMessage("CMT", TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN, "3313", TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN, phoneNumber, new ESMClass(),
                                    (byte) 0, (byte) 0, null, null, new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE), (byte) 0, ZERO, (byte) 0, "It is another 123".getBytes());

            /*
             * you can save the submitted message to database.
             */

            System.out.println("Message submitted, message_id is " + messageId);
            Thread.sleep(10000);

            //            QuerySmResult response = session.queryShortMessage(messageId, TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN, "4003");
            //            System.out.println("Message queried, message_id is " + messageId + ", errorCode is " + response.getErrorCode() +  ", msgState is " + response.getMessageState() +  ",
            // finalDate is " + response.getFinalDate());

            //Thread.sleep(2000);
        }
        catch (PDUException e) {
            // Invalid PDU parameter
            System.err.println("Invalid PDU parameter");
            e.printStackTrace();
        }
        catch (ResponseTimeoutException e) {
            // Response timeout
            System.err.println("Response timeout");
            e.printStackTrace();
        }
        catch (InvalidResponseException e) {
            // Invalid response
            System.err.println("Receive invalid respose");
            e.printStackTrace();
        }
        catch (NegativeResponseException e) {
            // Receiving negative response (non-zero command_status)
            System.err.println("Receive negative response");
            e.printStackTrace();
        }
        catch (IOException e) {
            System.err.println("IO error occur");
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            System.err.println("Thread interrupted");
            e.printStackTrace();
        }

        session.unbindAndClose();
    }

    public static class MessageReceiverListenerImplTest implements MessageReceiverListener {

        public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {

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
                }
                catch (InvalidDeliveryReceiptException e) {
                    System.err.println("Failed getting delivery receipt");
                    e.printStackTrace();
                }
            }
            else {
                // this message is regular short message

            /*
             * you can save the incoming message to database.
             */

                System.out.println("Receiving message : " + new String(deliverSm.getShortMessage()));
            }
        }

        public void onAcceptAlertNotification(AlertNotification alertNotification) {
        }

        public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) throws ProcessRequestException {
            return null;
        }
    }

}

