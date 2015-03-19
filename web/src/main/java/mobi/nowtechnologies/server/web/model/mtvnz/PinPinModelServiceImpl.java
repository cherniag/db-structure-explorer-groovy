package mobi.nowtechnologies.server.web.model.mtvnz;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.web.model.PinModelService;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

public class PinPinModelServiceImpl implements PinModelService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    PaymentPolicyService paymentPolicyService;
    @Resource
    NZSubscriberInfoRepository subscriberInfoRepository;

    @Override
    @Transactional
    public Map<String, Object> getModel(User user, String phone) {
        List<PaymentPolicy> filtered = filterWithOneDurationLength(paymentPolicyService.findPaymentPolicies(user));

        logger.info("Found for community {} payment policies: {}", user.getCommunity().getRewriteUrlParameter(), PaymentPolicyDto.convert(filtered));

        Collection<PaymentPolicy> vfPsms = Collections2.filter(filtered, new PaymentTypePredicate(PaymentDetails.MTVNZ_PSMS_TYPE));

        Collection<PaymentPolicyDto> converted = PaymentPolicyDto.convert(vfPsms);

        logger.info("Found for community {} and payment type: {} payment policies: {}", user.getCommunity().getRewriteUrlParameter(), PaymentDetails.MTVNZ_PSMS_TYPE, converted);

        Preconditions.checkState(converted.size() == 2, "Found not one payment policy for vfPsms for community " + user.getCommunity().getRewriteUrlParameter());

        Object policies = new ArrayList<>(new TreeSet<>(converted));

        confirm(user.getId(), phone);

        return Collections.singletonMap("paymentPolicyDtos", policies);
    }

    List<PaymentPolicy> filterWithOneDurationLength(List<PaymentPolicy> paymentPolicies) {
        List<PaymentPolicy> dtos = new ArrayList<>(paymentPolicies);

        for (PaymentPolicy paymentPolicy : paymentPolicies) {
            if(paymentPolicy.getPeriod().getDuration() != 1) {
                logger.warn("Payment Policy {} contains duration not equal to one for provider type {}", paymentPolicy.getId(), ProviderType.VF);
            } else {
                dtos.add(paymentPolicy);
            }
        }

        return dtos;
    }

    private void confirm(int userId, String msisdn) {
        NZSubscriberInfo existing = subscriberInfoRepository.findSubscriberInfoByUserId(userId);
        if(existing != null) {
            existing.unassignUser();
        }

        logger.info("confirm msisdn {} for {}", msisdn, userId);
        NZSubscriberInfo nzSubscriberInfo = subscriberInfoRepository.findSubscriberInfoByMsisdn(msisdn);
        nzSubscriberInfo.setUserId(userId);
    }

}
