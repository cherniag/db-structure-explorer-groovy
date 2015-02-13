package mobi.nowtechnologies.server.service.nz.impl;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import mobi.nowtechnologies.server.service.nz.NZSubscriberResult;
import org.springframework.ws.client.WebServiceFaultException;

import javax.annotation.Resource;

/**
 * @author Anton Zemliankin
 */
public class NZSubscriberInfoServiceImpl implements NZSubscriberInfoService {
    @Resource
    NZSubscriberInfoRepository subscriberInfoRepository;
    @Resource
    NZSubscriberInfoGateway subscriberInfoGateway;

    @Override
    public boolean belongs(String msisdn) {
        NZSubscriberInfo nzSubscriberInfo = refreshSubscriberInfo(msisdn);
        return "Vodafone".equals(nzSubscriberInfo.getProviderName());
    }

    @Override
    public NZSubscriberInfo confirm(int userId, String msisdn) {
        NZSubscriberInfo nzSubscriberInfo = subscriberInfoRepository.findTopByUserIdAndMsisdn(msisdn);
        nzSubscriberInfo.activate();
        return subscriberInfoRepository.save(nzSubscriberInfo);
    }

    private NZSubscriberInfo refreshSubscriberInfo(String msisdn) {
        try {
            NZSubscriberResult subscriberResult = subscriberInfoGateway.getSubscriberResult(msisdn);

            NZSubscriberInfo nzSubscriberInfo = findOrCreate(msisdn, subscriberResult);

            return subscriberInfoRepository.save(nzSubscriberInfo);
        }catch(WebServiceFaultException e){ //Thrown when the response message has a fault.
            throw new ExternalServiceException(e.getWebServiceMessage().getFaultReason(), e);
        }
    }

    private NZSubscriberInfo findOrCreate(String msisdn, NZSubscriberResult subscriberResult) {
        NZSubscriberInfo nzSubscriberInfo = subscriberInfoRepository.findTopByUserIdAndMsisdn(msisdn);

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
