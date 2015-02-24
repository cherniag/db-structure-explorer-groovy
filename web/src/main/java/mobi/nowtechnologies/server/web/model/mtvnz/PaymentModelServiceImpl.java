package mobi.nowtechnologies.server.web.model.mtvnz;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.domain.NZProviderType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import mobi.nowtechnologies.server.web.model.PaymentModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

class PaymentModelServiceImpl implements PaymentModelService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    PaymentPolicyService paymentPolicyService;
    @Resource
    TimeService timeService;
    @Resource
    NZSubscriberInfoRepository subscriberInfoRepository;
    @Resource
    NZSubscriberInfoService nzSubscriberInfoService;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getModel(User user) {
        Map<String, Object> model = new HashMap<>();
        List<PaymentPolicy> all = paymentPolicyService.findPaymentPolicies(user);

        logger.info("Found for user id {} policies: {}", user.getId(), PaymentPolicyDto.convert(all));

        Collection<PaymentPolicy> payPal = Collections2.filter(all, new PaymentTypePredicate("PAY_PAL"));
        Collection<PaymentPolicy> iTunes = Collections2.filter(all, new PaymentTypePredicate(PaymentDetails.ITUNES_SUBSCRIPTION));
        Collection<PaymentPolicy> vfPsms = Collections2.filter(all, new PaymentTypePredicate(PaymentDetails.VF_PSMS_TYPE));

        Preconditions.checkState(payPal.size() == 1, "Found not one payment policy for PayPal for community " + user.getCommunity());
        Preconditions.checkState(iTunes.size() == 1, "Found not one payment policy for iTunes for community " + user.getCommunity());
        Preconditions.checkState(!vfPsms.isEmpty(), "Found not one payment policy for VF SMS for community " + user.getCommunity());

        Set<PaymentPolicyDto> sorted = new TreeSet<>(PaymentPolicyDto.convert(vfPsms));
        Collection<PaymentPolicyDto> payPalDtos = PaymentPolicyDto.convert(payPal);
        Collection<PaymentPolicyDto> iTunesDtos = PaymentPolicyDto.convert(iTunes);

        model.put("payPalPaymentPolicy", Iterables.getLast(payPalDtos));
        model.put("iTunesPaymentPolicy", Iterables.getLast(iTunesDtos));
        model.put("smsPaymentPolicy", Iterables.getFirst(sorted, null));

        if(user.isPremium(timeService.now())) {
            logger.info("User id {} is premium user", user.getId());

            NZSubscriberInfo info = subscriberInfoRepository.findSubscriberInfoByUserId(user.getId());
            logger.info("User id {} has info {}", user.getId(), info);

            boolean vf = info.getProviderType() == NZProviderType.VODAFONE;

            model.put("vf", vf);
        }

        return model;
    }
}
