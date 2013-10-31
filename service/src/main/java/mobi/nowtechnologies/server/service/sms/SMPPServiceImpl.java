package mobi.nowtechnologies.server.service.sms;

import com.sentaca.spring.smpp.BindConfiguration;
import com.sentaca.spring.smpp.SMPPService;
import com.sentaca.spring.smpp.SMSCGatewayConfiguration;
import com.sentaca.spring.smpp.mo.MessageReceiver;
import com.sentaca.spring.smpp.monitoring.LoggingSMPPMonitoringAgent;
import com.sentaca.spring.smpp.monitoring.SMPPMonitoringAgent;
import com.sentaca.spring.smpp.mt.OutboundMessageCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.Service;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/14/13
 * Time: 7:20 PM
 */
public class SMPPServiceImpl extends SMPPService {
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private Set<SMSCGatewayConfiguration> gatewaysConfigurations = new HashSet<SMSCGatewayConfiguration>();
    private SMPPMonitoringAgent smppMonitoringAgent = new LoggingSMPPMonitoringAgent();
    private OutboundMessageCreator outboundMessageCreator = new SMPPOutboundMessageCreator();

    public SMPPServiceImpl(){
        super.setAutoStart(false);
        super.setOutboundMessageCreator(outboundMessageCreator);
        super.setSmppMonitoringAgent(smppMonitoringAgent);
    }

    @Override
    public void afterPropertiesSet() {
        try {
                Assert.notEmpty(gatewaysConfigurations, "Gateways must not be empty.");
                Assert.notNull(smppMonitoringAgent, "smppMonitoringAgent must not be null, use default LoggingSMPPMonitoringAgent or NoopSMPPMonitoringAgent.");
                Assert.notNull(outboundMessageCreator, "outboundMessageCreator must not be null, use DefaultOutboundMessageCreator or your own implementatin.");

                // add gateways
                for (SMSCGatewayConfiguration configuration : gatewaysConfigurations) {
                    Service.getInstance().addGateway(createGateway(configuration.getSmscConfig(), configuration.getMessageReceiver(), configuration.isUseUdhiInSubmitSm()));
                }

                // and fire-it-up
                Service.getInstance().startService();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void setGatewaysConfiguration(java.util.Set<com.sentaca.spring.smpp.SMSCGatewayConfiguration> gatewaysConfigurations) {
        super.setGatewaysConfiguration(gatewaysConfigurations);

        this.gatewaysConfigurations = gatewaysConfigurations;
    }

    protected SMPPGateway createGateway(BindConfiguration cfg, MessageReceiver receiver, boolean isUseUdhiInSubmitSm){
        return new SMPPGateway(cfg, receiver, smppMonitoringAgent, isUseUdhiInSubmitSm);
    }
}
