package mobi.nowtechnologies.server.service.nz.impl;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.domain.NZProviderType;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.exception.SubscriberServiceException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import mobi.nowtechnologies.server.service.nz.NZSubscriberResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataIntegrityViolationException;
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
        boolean isVodafone = nzSubscriberInfo.getProviderType() == NZProviderType.VODAFONE;
        log.info("{} is{} Vodafone msisdn.", msisdn, isVodafone ? "" : " not");
        return isVodafone;
    }

    private NZSubscriberInfo refreshSubscriberInfo(String msisdn) throws SubscriberServiceException.MSISDNNotFound, SubscriberServiceException.ServiceNotAvailable {
        NZSubscriberInfo nzSubscriberInfo = null;
        try {
            long wsCallTime = DateTimeUtils.getEpochMillis();
            NZSubscriberResult subscriberResult = subscriberInfoGateway.getSubscriberResult(msisdn);
            wsCallTime = DateTimeUtils.getEpochMillis() - wsCallTime;

            nzSubscriberInfo = findOrCreate(msisdn, subscriberResult);
            nzSubscriberInfo.setWsCallMillis(wsCallTime);

            if(wsCallTime > 500){
                log.warn("NZ subscriber web service call took {} milliseconds", wsCallTime);
            } else {
                log.info("NZ subscriber web service call took {} milliseconds", wsCallTime);
            }

            return subscriberInfoRepository.save(nzSubscriberInfo);
        } catch (WebServiceFaultException e) {
            if (NOT_FOUND_TOKEN.equals(e.getMessage())) {
                log.info("Msisdn not found {}", msisdn);
                throw new SubscriberServiceException.MSISDNNotFound(e.getMessage(), e);
            } else {
                nzSubscriberInfo = getSubscriberInfoByMsisdn(msisdn);
                if(nzSubscriberInfo != null) {
                    return nzSubscriberInfo;
                }

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
