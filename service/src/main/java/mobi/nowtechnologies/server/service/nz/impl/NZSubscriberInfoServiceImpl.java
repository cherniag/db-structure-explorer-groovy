package mobi.nowtechnologies.server.service.nz.impl;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.exception.SubscriberServiceException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import mobi.nowtechnologies.server.service.nz.NZSubscriberResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.ws.client.WebServiceFaultException;

/**
 * @author Anton Zemliankin
 */
public class NZSubscriberInfoServiceImpl implements NZSubscriberInfoService, InitializingBean {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    NZSubscriberInfoRepository subscriberInfoRepository;
    NZSubscriberInfoGateway subscriberInfoGateway;

    @Override
    public boolean belongs(String msisdn) throws SubscriberServiceException.ServiceNotAvailable, SubscriberServiceException.MSISDNNotFound {
        log.debug("Checking if {} is Vodafone msisdn.", msisdn);
        NZSubscriberInfo nzSubscriberInfo = refreshSubscriberInfo(msisdn);
        boolean isVodafone = "Vodafone".equals(nzSubscriberInfo.getProviderName());
        log.debug("{} is{} Vodafone msisdn.", msisdn, isVodafone ? "" : " not");
        return isVodafone;
    }

    @Override
    public NZSubscriberInfo confirm(int userId, String msisdn) {
        NZSubscriberInfo nzSubscriberInfo = subscriberInfoRepository.findSubscriberInfoByMsisdn(msisdn);
        nzSubscriberInfo.setUserId(userId);
        return subscriberInfoRepository.save(nzSubscriberInfo);
    }

    private NZSubscriberInfo refreshSubscriberInfo(String msisdn) throws SubscriberServiceException.MSISDNNotFound, SubscriberServiceException.ServiceNotAvailable {
        try {
            NZSubscriberResult subscriberResult = subscriberInfoGateway.getSubscriberResult(msisdn);

            NZSubscriberInfo nzSubscriberInfo = findOrCreate(msisdn, subscriberResult);

            return subscriberInfoRepository.save(nzSubscriberInfo);
        } catch (WebServiceFaultException e) {
            if ("MSISDN NOT FOUND IN INFRANET".equals(e.getMessage())) {
                log.debug("Msisdn not found", e);
                throw new SubscriberServiceException.MSISDNNotFound(e.getMessage(), e);
            } else {
                log.debug("Failed to connect to NZ subscribers service: " + e.getMessage(), e);
                throw new SubscriberServiceException.ServiceNotAvailable(e.getMessage(), e);
            }
        }
    }

    private NZSubscriberInfo findOrCreate(String msisdn, NZSubscriberResult subscriberResult) {
        NZSubscriberInfo nzSubscriberInfo = subscriberInfoRepository.findSubscriberInfoByMsisdn(msisdn);

        if (nzSubscriberInfo == null) {
            nzSubscriberInfo = new NZSubscriberInfo(msisdn);
        }

        nzSubscriberInfo.setPayIndicator(subscriberResult.getPayIndicator());
        nzSubscriberInfo.setProviderName(subscriberResult.getProviderName());
        nzSubscriberInfo.setBillingAccountName(subscriberResult.getBillingAccountName());
        nzSubscriberInfo.setBillingAccountNumber(subscriberResult.getBillingAccountNumber());
        return nzSubscriberInfo;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(subscriberInfoRepository, "subscriberInfoRepository should not be null.");
        Assert.notNull(subscriberInfoGateway, "subscriberInfoGateway should not be null.");
    }

    public void setSubscriberInfoRepository(NZSubscriberInfoRepository subscriberInfoRepository) {
        this.subscriberInfoRepository = subscriberInfoRepository;
    }

    public void setSubscriberInfoGateway(NZSubscriberInfoGateway subscriberInfoGateway) {
        this.subscriberInfoGateway = subscriberInfoGateway;
    }
}
