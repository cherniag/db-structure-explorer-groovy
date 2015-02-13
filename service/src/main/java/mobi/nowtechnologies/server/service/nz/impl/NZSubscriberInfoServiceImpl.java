package mobi.nowtechnologies.server.service.nz.impl;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.ServiceNotAvailableException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import mobi.nowtechnologies.server.service.nz.NZSubscriberResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.WebServiceFaultException;

import javax.annotation.Resource;

/**
 * @author Anton Zemliankin
 */
public class NZSubscriberInfoServiceImpl implements NZSubscriberInfoService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    NZSubscriberInfoRepository subscriberInfoRepository;
    @Resource
    NZSubscriberInfoGateway subscriberInfoGateway;

    @Override
    public boolean belongs(String msisdn) throws ServiceNotAvailableException {
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

    private NZSubscriberInfo refreshSubscriberInfo(String msisdn) throws ServiceNotAvailableException {
        try {
            NZSubscriberResult subscriberResult = subscriberInfoGateway.getSubscriberResult(msisdn);

            NZSubscriberInfo nzSubscriberInfo = findOrCreate(msisdn, subscriberResult);

            return subscriberInfoRepository.save(nzSubscriberInfo);
        }catch(WebServiceFaultException e){
            log.debug("Failed to connect to NZ subscribers service: " + e.getWebServiceMessage().getFaultReason(), e);
            throw new ServiceNotAvailableException(e.getWebServiceMessage().getFaultReason(), e);
        }
    }

    private NZSubscriberInfo findOrCreate(String msisdn, NZSubscriberResult subscriberResult) {
        NZSubscriberInfo nzSubscriberInfo = subscriberInfoRepository.findSubscriberInfoByMsisdn(msisdn);

        if(nzSubscriberInfo == null){
            nzSubscriberInfo = new NZSubscriberInfo(msisdn);
        }

        nzSubscriberInfo.setPayIndicator(subscriberResult.getPayIndicator());
        nzSubscriberInfo.setProviderName(subscriberResult.getProviderName());
        nzSubscriberInfo.setBillingAccountName(subscriberResult.getBillingAccountName());
        nzSubscriberInfo.setBillingAccountNumber(subscriberResult.getBillingAccountNumber());
        return nzSubscriberInfo;
    }

}
