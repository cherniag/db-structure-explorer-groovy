package mobi.nowtechnologies.server.service.sms;

import mobi.nowtechnologies.server.shared.BasicProcessor;
import org.jsmpp.bean.DeliverSm;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/18/13
 * Time: 11:49 AM
 */
public abstract class BasicSMSMessageProcessor<OUT> extends BasicProcessor<OUT> implements SMSMessageProcessor<OUT> {
    public abstract boolean supports(DeliverSm deliverSm);
}
