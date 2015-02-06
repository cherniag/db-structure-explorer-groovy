package mobi.nowtechnologies.server.service.nz.impl;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import nz.co.vodafone.ws.customer.com.service.onlineaccountservice._1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

/**
 * @author Anton Zemliankin
 */

public class ContentProviderAccountServiceGateway extends WebServiceGatewaySupport implements NZSubscriberInfoService {

    private static final String CHANNEL_TYPE = "WEB";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ObjectFactory objectFactory = new ObjectFactory();

    private String nzUserId;

    public NZSubscriberInfo getSubscriberInfo(int userId, String msisdn) {
        TConnectionResponse connectionDetails = getConnectionDetails(msisdn);
        TConnectionResponseInfo connectionResponseInfo = connectionDetails.getConnectionResponseInfo();

        NZSubscriberInfo nzSubscriberInfo = new NZSubscriberInfo();
        nzSubscriberInfo.setUserId(userId);
        nzSubscriberInfo.setMsisdn(msisdn);
        nzSubscriberInfo.setPayIndicator(connectionResponseInfo.getPayIndicator());
        nzSubscriberInfo.setProviderName(connectionResponseInfo.getProviderName());
        nzSubscriberInfo.setBillingAccountName(connectionResponseInfo.getBillingAccountName());
        nzSubscriberInfo.setBillingAccountNumber(connectionResponseInfo.getBillingAccountNumber());
        nzSubscriberInfo.setActive(false); // set to TRUE after pin verification

        return nzSubscriberInfo;
    }

    TConnectionResponse getConnectionDetails(String msisdn) {
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
