package mobi.nowtechnologies.server.web.model.mtvnz;

import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import mobi.nowtechnologies.server.web.model.PaymentModelService;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

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
        Collection<PaymentPolicy> vfPsms = Collections2.filter(all, new PaymentTypePredicate(PaymentDetails.MTVNZ_PSMS_TYPE));

        Preconditions.checkState(vfPsms.size() == 2, "Found not one payment policy for vfPsms for community " + user.getCommunity());
        Preconditions.checkState(payPal.size() == 1, "Found not one payment policy for PayPal for community " + user.getCommunity());
        Preconditions.checkState(iTunes.size() == 1, "Found not one payment policy for iTunes for community " + user.getCommunity());
        Preconditions.checkState(!vfPsms.isEmpty(), "Found not one payment policy for VF SMS for community " + user.getCommunity());

        Set<PaymentPolicyDto> sorted = new TreeSet<>(PaymentPolicyDto.convert(vfPsms));
        Collection<PaymentPolicyDto> payPalDtos = PaymentPolicyDto.convert(payPal);
        Collection<PaymentPolicyDto> iTunesDtos = PaymentPolicyDto.convert(iTunes);

        model.put("anotherPaymentPolicy", anotherPaymentPolicy(user, sorted));
        model.put("payPalPaymentPolicy", Iterables.getLast(payPalDtos));
        model.put("iTunesPaymentPolicy", Iterables.getLast(iTunesDtos));
        model.put("smsPaymentPolicy", Iterables.getFirst(sorted, null));

        if(user.isPremium(timeService.now())) {
            boolean vfPaymentType = user.getCurrentPaymentDetails() != null && PaymentDetails.VF_PSMS_TYPE.equals(user.getCurrentPaymentDetails().getPaymentType());

            model.put("vf", vfPaymentType);

            logger.info("User id {} is premium user and has payment type: {}", user.getId(), vfPaymentType);
        }

        return model;
    }

    private Object anotherPaymentPolicy(User user, Set<PaymentPolicyDto> sorted) {
        if(user.getCurrentPaymentDetails() == null) {
            return null;
        }

        PaymentPolicyDto current = new PaymentPolicyDto(user.getCurrentPaymentDetails().getPaymentPolicy());
        List<PaymentPolicyDto> all = new ArrayList<>(sorted);
        all.remove(current);
        return Iterables.getLast(all);
    }
}
