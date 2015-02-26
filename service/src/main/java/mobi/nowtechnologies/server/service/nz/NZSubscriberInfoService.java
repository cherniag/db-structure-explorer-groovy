package mobi.nowtechnologies.server.service.nz;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.persistence.domain.NZProviderType;
import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.util.Assert;

/**
 * @author Anton Zemliankin
 */
public class NZSubscriberInfoService implements InitializingBean {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    NZSubscriberInfoRepository subscriberInfoRepository;
    NZSubscriberInfoProvider subscriberInfoProvider;

    public boolean belongs(String msisdn) throws ProviderNotAvailableException, MsisdnNotFoundException {
        log.info("Checking if {} is Vodafone msisdn.", msisdn);
        NZSubscriberInfo nzSubscriberInfo = refreshSubscriberInfo(msisdn);
        boolean isVodafone = nzSubscriberInfo.getProviderType() == NZProviderType.VODAFONE;
        log.info("{} is {} msisdn.", msisdn, nzSubscriberInfo.getProviderName());
        return isVodafone;
    }

    private NZSubscriberInfo refreshSubscriberInfo(String msisdn) throws ProviderNotAvailableException, MsisdnNotFoundException {
        NZSubscriberInfo nzSubscriberInfo = null;
        try {
            long callTime = DateTimeUtils.getEpochMillis();
            NZSubscriberResult subscriberResult = subscriberInfoProvider.getSubscriberResult(msisdn);

            if(DateTimeUtils.getEpochMillis() - callTime > 500){
                log.warn("NZ subscriber web service call took {} milliseconds", callTime);
            } else {
                log.info("NZ subscriber web service call took {} milliseconds", callTime);
            }

            nzSubscriberInfo = findOrCreate(msisdn, subscriberResult);

            return subscriberInfoRepository.save(nzSubscriberInfo);
        } catch (ProviderNotAvailableException e) {
            log.warn("Failed to connect to NZ subscribers service: " + e.getMessage(), e);

            nzSubscriberInfo = getSubscriberInfoByMsisdn(msisdn);
            if(nzSubscriberInfo != null) {
                return nzSubscriberInfo;
            }

            throw e;
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

        nzSubscriberInfo.incCallCount();
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
        Assert.notNull(subscriberInfoProvider, "subscriberInfoProvider should not be null.");
    }

    public void setSubscriberInfoRepository(NZSubscriberInfoRepository subscriberInfoRepository) {
        this.subscriberInfoRepository = subscriberInfoRepository;
    }

    public void setSubscriberInfoProvider(NZSubscriberInfoProvider subscriberInfoProvider) {
        this.subscriberInfoProvider = subscriberInfoProvider;
    }
}
