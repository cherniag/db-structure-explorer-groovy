package mobi.nowtechnologies.server.service.nz.impl;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.exception.SubscriberServiceException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import mobi.nowtechnologies.server.service.nz.NZSubscriberResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.ws.client.WebServiceFaultException;

/**
 * @author Anton Zemliankin
 */
public class NZSubscriberInfoServiceImpl implements NZSubscriberInfoService, InitializingBean {
    public static final String NOT_FOUND_TOKEN = "MSISDN NOT FOUND IN INFRANET";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    NZSubscriberInfoRepository subscriberInfoRepository;
    NZSubscriberInfoGateway subscriberInfoGateway;

    @Override
    public boolean belongs(String msisdn) throws SubscriberServiceException.ServiceNotAvailable, SubscriberServiceException.MSISDNNotFound {
        log.info("Checking if {} is Vodafone msisdn.", msisdn);
        NZSubscriberInfo nzSubscriberInfo = refreshSubscriberInfo(msisdn);
        boolean isVodafone = "Vodafone".equals(nzSubscriberInfo.getProviderName());
        log.info("{} is{} Vodafone msisdn.", msisdn, isVodafone ? "" : " not");
        return isVodafone;
    }

    @Override
    @Transactional
    public NZSubscriberInfo confirm(int userId, String msisdn) {
        log.info("confirm msisdn {} for {}", msisdn, userId);
        NZSubscriberInfo nzSubscriberInfo = subscriberInfoRepository.findSubscriberInfoByMsisdn(msisdn);
        nzSubscriberInfo.setUserId(userId);
        return nzSubscriberInfo;
    }

    private NZSubscriberInfo refreshSubscriberInfo(String msisdn) throws SubscriberServiceException.MSISDNNotFound, SubscriberServiceException.ServiceNotAvailable {
        NZSubscriberInfo nzSubscriberInfo = null;
        try {
            NZSubscriberResult subscriberResult = subscriberInfoGateway.getSubscriberResult(msisdn);

            nzSubscriberInfo = findOrCreate(msisdn, subscriberResult);

            return subscriberInfoRepository.save(nzSubscriberInfo);
        } catch (WebServiceFaultException e) {
            if (NOT_FOUND_TOKEN.equals(e.getMessage())) {
                log.info("Msisdn not found {}", msisdn);
                throw new SubscriberServiceException.MSISDNNotFound(e.getMessage(), e);
            } else {
                log.info("Failed to connect to NZ subscribers service: " + e.getMessage(), e);
                throw new SubscriberServiceException.ServiceNotAvailable(e.getMessage(), e);
            }
        } catch(DataIntegrityViolationException e){
            log.info("Unable insert subscriber info for msisdn " + msisdn, e);
            return nzSubscriberInfo;
        }
    }

    private NZSubscriberInfo findOrCreate(String msisdn, NZSubscriberResult subscriberResult) {
        NZSubscriberInfo nzSubscriberInfo = getSubscriberInfoByMsisdn(msisdn);

        if (nzSubscriberInfo == null) {
            nzSubscriberInfo = new NZSubscriberInfo(msisdn);
        }

        nzSubscriberInfo.setPayIndicator(subscriberResult.getPayIndicator());
        nzSubscriberInfo.setProviderName(subscriberResult.getProviderName());
        nzSubscriberInfo.setBillingAccountName(subscriberResult.getBillingAccountName());
        nzSubscriberInfo.setBillingAccountNumber(subscriberResult.getBillingAccountNumber());
        return nzSubscriberInfo;
    }

    NZSubscriberInfo getSubscriberInfoByMsisdn(String msisdn) {
        return subscriberInfoRepository.findSubscriberInfoByMsisdn(msisdn);
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
