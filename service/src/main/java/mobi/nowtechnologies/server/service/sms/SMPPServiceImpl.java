package mobi.nowtechnologies.server.service.sms;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.sentaca.spring.smpp.BindConfiguration;
import com.sentaca.spring.smpp.SMPPService;
import com.sentaca.spring.smpp.SMSCGatewayConfiguration;
import com.sentaca.spring.smpp.mo.MessageReceiver;
import com.sentaca.spring.smpp.monitoring.LoggingSMPPMonitoringAgent;
import com.sentaca.spring.smpp.monitoring.SMPPMonitoringAgent;
import com.sentaca.spring.smpp.mt.MTMessage;
import com.sentaca.spring.smpp.mt.OutboundMessageCreator;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.GatewayException;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.TimeoutException;

import org.springframework.util.Assert;

/**
 * User: Alexsandr_Kolpakov Date: 10/14/13 Time: 7:20 PM
 */
public class SMPPServiceImpl extends SMPPService {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private Set<SMSCGatewayConfiguration> gatewaysConfigurations = new HashSet<SMSCGatewayConfiguration>();
    private SMPPMonitoringAgent smppMonitoringAgent = new LoggingSMPPMonitoringAgent();
    private OutboundMessageCreator outboundMessageCreator = new SMPPOutboundMessageCreator();
    private boolean useQueueManager;

    public SMPPServiceImpl() {
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
                SMPPBindConfiguration bindConfiguration = (SMPPBindConfiguration) configuration.getSmscConfig();
                for (int i = 0; i < bindConfiguration.getConnectionPoolSize(); i++) {
                    Service.getInstance().addGateway(createGateway(configuration.getSmscConfig(), configuration.getMessageReceiver(), configuration.isUseUdhiInSubmitSm()));
                }
            }

            // and fire-it-up
            Service.getInstance().startService();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public SMSResponse sendMessage(MTMessage message) throws GatewayException, IOException, InterruptedException, TimeoutException {
        final OutboundMessage outboundMessage = outboundMessageCreator.toOutboundMessage(message);
        outboundMessage.setGatewayId("*");
        smppMonitoringAgent.onMessageSend(message, outboundMessage);
        Service instance = Service.getInstance();
        boolean result = useQueueManager ?
                         instance.queueMessage(outboundMessage) :
                         instance.sendMessage(outboundMessage);
        SMSServiceResponse smsResponse = new SMSServiceResponse();
        smsResponse.isSuccessful = result;

        if (!result) {
            if (instance.getServiceStatus() != Service.ServiceStatus.STARTED) {
                LOGGER.error("Can't send message cause SMPP Service is not STARTED");
            }

            LOGGER.error("SMS Message was sent with fails: " + outboundMessage.getFailureCause());
            smsResponse.descriptionError = "Service status:" + instance.getServiceStatus() + ", Failure cause:" + outboundMessage.getFailureCause();
        }

        return smsResponse;
    }

    @Override
    public void setGatewaysConfiguration(java.util.Set<com.sentaca.spring.smpp.SMSCGatewayConfiguration> gatewaysConfigurations) {
        super.setGatewaysConfiguration(gatewaysConfigurations);

        this.gatewaysConfigurations = gatewaysConfigurations;
    }

    protected SMPPGateway createGateway(BindConfiguration cfg, MessageReceiver receiver, boolean isUseUdhiInSubmitSm) {
        return new SMPPGateway(cfg, receiver, smppMonitoringAgent, isUseUdhiInSubmitSm);
    }

    public void setUseQueueManager(boolean useQueueManager) {
        this.useQueueManager = useQueueManager;
    }

    private static class SMSServiceResponse implements SMSResponse{
        private boolean isSuccessful;
        private String descriptionError;

        @Override
        public boolean isSuccessful() {
            return isSuccessful;
        }

        @Override
        public String getDescriptionError() {
            return descriptionError;
        }


        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("isSuccessful", isSuccessful)
                    .append("descriptionError", descriptionError)
                    .toString();
        }
    }
}
