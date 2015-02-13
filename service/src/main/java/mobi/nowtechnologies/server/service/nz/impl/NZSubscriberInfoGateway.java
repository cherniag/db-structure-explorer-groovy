package mobi.nowtechnologies.server.service.nz.impl;

import com.google.common.base.Preconditions;
import mobi.nowtechnologies.server.service.nz.NZSubscriberResult;
import nz.co.vodafone.ws.customer.com.service.onlineaccountservice._1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBElement;

/**
 * @author Anton Zemliankin
 */

public class NZSubscriberInfoGateway extends WebServiceGatewaySupport {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ObjectFactory objectFactory = new ObjectFactory();

    private String nzUserId;

    public NZSubscriberResult getSubscriberResult(String msisdn) {
        log.debug("Getting nz user info for {}", msisdn);

        TConnectionResponse connectionDetails = createConnectionDetails(Preconditions.checkNotNull(msisdn));

        TConnectionResponseInfo ci = connectionDetails.getConnectionResponseInfo();

        log.debug("Provider name for {} is {}", msisdn, ci.getProviderName());

        return new NZSubscriberResult(ci.getPayIndicator(), ci.getProviderName(), ci.getBillingAccountNumber(), ci.getBillingAccountName());
    }

    private TConnectionResponse createConnectionDetails(String msisdn) {
        TChannel channel = new TChannel();
        channel.setChannelType("WEB");

        TConnectionRequest request = new TConnectionRequest();
        request.setUserId(nzUserId);
        request.setMSISDN(msisdn);
        request.setChannel(channel);

        JAXBElement<TConnectionRequest> connectionRequest = objectFactory.createConnectionRequest(request);
        JAXBElement<TConnectionResponse> response = (JAXBElement<TConnectionResponse>) getWebServiceTemplate().marshalSendAndReceive(connectionRequest);
        return response.getValue();
    }

    @PostConstruct
    public void checkConfiguration() {
        Assert.notNull(nzUserId, "nzUserId should not be null");
    }

    public void setNzUserId(String nzUserId) {
        this.nzUserId = nzUserId;
    }
}
