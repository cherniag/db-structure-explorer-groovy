package mobi.nowtechnologies.server.service.nz.impl;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import mobi.nowtechnologies.server.service.nz.NZSubscriberResult;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.WebServiceFaultException;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.annotation.Resource;

/**
 * @author Anton Zemliankin
 */
public class NZSubscriberInfoServiceImpl implements NZSubscriberInfoService {

    private final static String VODAFONE_PROVIDER_NAME = "Vodafone";

    @Resource
    private NZSubscriberInfoRepository subscriberInfoRepository;

    @Resource
    private NZSubscriberInfoGateway subscriberInfoGateway;


    @Override
    public boolean checkVodafone(int userId, String msisdn) {
        NZSubscriberInfo nzSubscriberInfo = refreshSubscriberInfo(userId, msisdn);
        return VODAFONE_PROVIDER_NAME.equals(nzSubscriberInfo.getProviderName());
    }

    @Override
    public NZSubscriberInfo confirmSubscriber(int userId, String msisdn) {
        NZSubscriberInfo nzSubscriberInfo = subscriberInfoRepository.findTopByUserIdAndMsisdn(userId, msisdn);
        nzSubscriberInfo.setActive(true);
        return subscriberInfoRepository.save(nzSubscriberInfo);
    }

    private NZSubscriberInfo refreshSubscriberInfo(int userId, String msisdn) {
        try {
            NZSubscriberResult subscriberResult = subscriberInfoGateway.getSubscriberResult(msisdn);
            NZSubscriberInfo nzSubscriberInfo = subscriberInfoRepository.findTopByUserIdAndMsisdn(userId, msisdn);

            if(nzSubscriberInfo == null){
                nzSubscriberInfo = new NZSubscriberInfo();
                nzSubscriberInfo.setUserId(userId);
                nzSubscriberInfo.setMsisdn(msisdn);
                nzSubscriberInfo.setActive(false);
            }

            nzSubscriberInfo.setPayIndicator(subscriberResult.getPayIndicator());
            nzSubscriberInfo.setProviderName(subscriberResult.getProviderName());
            nzSubscriberInfo.setBillingAccountName(subscriberResult.getBillingAccountName());
            nzSubscriberInfo.setBillingAccountNumber(subscriberResult.getBillingAccountNumber());
            return subscriberInfoRepository.save(nzSubscriberInfo);
        }catch(WebServiceFaultException e){ //Thrown when the response message has a fault.
            throw new ExternalServiceException(e.getWebServiceMessage().getFaultReason(), e);
        }
    }

}
