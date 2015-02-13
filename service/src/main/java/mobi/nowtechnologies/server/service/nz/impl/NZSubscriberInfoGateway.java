package mobi.nowtechnologies.server.service.nz.impl;

import mobi.nowtechnologies.server.service.nz.NZSubscriberResult;
import nz.co.vodafone.ws.customer.com.service.onlineaccountservice._1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

/**
 * @author Anton Zemliankin
 */

public class NZSubscriberInfoGateway extends WebServiceGatewaySupport {

    private static final String CHANNEL_TYPE = "WEB";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ObjectFactory objectFactory = new ObjectFactory();

    private String nzUserId;

    public NZSubscriberResult getSubscriberResult(String msisdn) {
        TConnectionResponse connectionDetails = getConnectionDetails(msisdn);
        TConnectionResponseInfo ci = connectionDetails.getConnectionResponseInfo();
        return new NZSubscriberResult(ci.getPayIndicator(), ci.getProviderName(), ci.getBillingAccountNumber(), ci.getBillingAccountName());
    }

    private TConnectionResponse getConnectionDetails(String msisdn) {
        log.debug(String.format("Getting nz user info for %s", msisdn));

        TChannel channel = new TChannel();
        channel.setChannelType(CHANNEL_TYPE);

        TConnectionRequest request = new TConnectionRequest();
        request.setUserId(nzUserId);
        request.setMSISDN(msisdn);
        request.setChannel(channel);

        TConnectionResponse response = (TConnectionResponse) getWebServiceTemplate().marshalSendAndReceive(objectFactory.createConnectionRequest(request));

        log.debug(String.format("Provider name for %s is %s", msisdn, response.getConnectionResponseInfo().getProviderName()));
        return response;
    }

    public void setNzUserId(String nzUserId) {
        this.nzUserId = nzUserId;
    }
}
