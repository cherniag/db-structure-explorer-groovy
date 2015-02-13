package mobi.nowtechnologies.server.web.model.mtvnz;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.web.model.PaymentModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class PaymentModelServiceImpl implements PaymentModelService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private PaymentPolicyService paymentPolicyService;

    @Override
    public Map<String, Object> getModel(User user) {
        Map<String, Object> model = new HashMap<>();
        List<PaymentPolicy> all = paymentPolicyService.findPaymentPolicies(user);

        Collection<PaymentPolicy> iTunes = Collections2.filter(all, new PaymentTypePredicate(PaymentDetails.ITUNES_SUBSCRIPTION));
        Collection<PaymentPolicy> vfPsms = Collections2.filter(all, new PaymentTypePredicate(PaymentDetails.VF_PSMS_TYPE));

        Preconditions.checkState(iTunes.size() == 1, "Found not one payment policy for iTunes for community " + user.getCommunity());
        Preconditions.checkState(!vfPsms.isEmpty(), "Found not one payment policy for VF SMS for community " + user.getCommunity());

        Set<PaymentPolicyDto> sorted = new TreeSet<>(PaymentPolicyDto.convert(vfPsms));
        Collection<PaymentPolicyDto> iTunesDto = PaymentPolicyDto.convert(iTunes);

        model.put("iTunesPaymentPolicy", Iterables.getLast(iTunesDto));
        model.put("smsPaymentPolicy", Iterables.getFirst(sorted, null));
        return model;
    }

    public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
        this.paymentPolicyService = paymentPolicyService;
    }
}
