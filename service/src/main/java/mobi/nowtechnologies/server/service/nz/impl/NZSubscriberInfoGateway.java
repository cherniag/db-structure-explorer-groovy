package mobi.nowtechnologies.server.service.nz.impl;

import mobi.nowtechnologies.server.service.nz.MsisdnNotFoundException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoProvider;
import mobi.nowtechnologies.server.service.nz.NZSubscriberResult;
import mobi.nowtechnologies.server.service.nz.ProviderConnectionException;
import mobi.nowtechnologies.server.service.nz.ProviderNotAvailableException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBElement;

import com.google.common.base.Preconditions;
import nz.co.vodafone.ws.customer.com.service.onlineaccountservice._1.ObjectFactory;
import nz.co.vodafone.ws.customer.com.service.onlineaccountservice._1.TChannel;
import nz.co.vodafone.ws.customer.com.service.onlineaccountservice._1.TConnectionRequest;
import nz.co.vodafone.ws.customer.com.service.onlineaccountservice._1.TConnectionResponse;
import nz.co.vodafone.ws.customer.com.service.onlineaccountservice._1.TConnectionResponseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.WebServiceFaultException;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

/**
 * @author Anton Zemliankin
 */

public class NZSubscriberInfoGateway extends WebServiceGatewaySupport implements NZSubscriberInfoProvider {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private ObjectFactory objectFactory = new ObjectFactory();

    private String nzUserId;

    @Override
    public NZSubscriberResult getSubscriberResult(String msisdn) throws MsisdnNotFoundException, ProviderNotAvailableException {
        try {
            log.info("Getting nz user info for {}", msisdn);

            TConnectionResponse connectionDetails = createConnectionDetails(Preconditions.checkNotNull(msisdn));
            TConnectionResponseInfo ci = connectionDetails.getConnectionResponseInfo();

            log.info("Provider name for {} is {}", msisdn, ci.getProviderName());

            return new NZSubscriberResult(ci.getPayIndicator(), ci.getProviderName(), ci.getBillingAccountNumber(), ci.getBillingAccountName());
        } catch (WebServiceIOException e) {
            log.info("Failed to connect to NZ subscribers service: " + e.getMessage(), e);
            throw new ProviderConnectionException(e.getMessage(), e);
        } catch (WebServiceException e) {
            if ("MSISDN NOT FOUND IN INFRANET".equals(e.getMessage())) {
                log.info("Msisdn not found {}", msisdn);
                throw new MsisdnNotFoundException(e.getMessage(), e);
            } else {
                log.info("Provider not available : " + e.getMessage(), e);
                throw new ProviderNotAvailableException(e.getMessage(), e);
            }
        }
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
