package mobi.nowtechnologies.server.service.sms;

import mobi.nowtechnologies.server.shared.Processor;

import org.jsmpp.bean.DeliverSm;

/**
 * User: Alexsandr_Kolpakov Date: 10/18/13 Time: 11:49 AM
 */
public interface SMSMessageProcessor<OUT> extends Processor<OUT> {

    boolean supports(DeliverSm deliverSm);

    void parserAndProcess(final Object data);
}
