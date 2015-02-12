package mobi.nowtechnologies.server.web.model.mtvnz;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
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
        List<PaymentPolicy> all = paymentPolicyService.findPaymentPolicies(user, ProviderType.VF);

        Collection<PaymentPolicy> iTunes = Collections2.filter(all, new ITunesPredicate());
        Collection<PaymentPolicy> notITunes = Collections2.filter(all, Predicates.not(new ITunesPredicate()));

        Preconditions.checkState(iTunes.size() == 1, "Found not one payment policy for iTunes for community " + user.getCommunity());
        Preconditions.checkState(!notITunes.isEmpty(), "Found not one payment policy for iTunes for community " + user.getCommunity());

        Set<PaymentPolicyDto> sorted = new TreeSet<>(PaymentPolicyDto.convert(notITunes));

        model.put("iTunesPaymentPolicy", Iterables.getLast(iTunes));
        model.put("smsPaymentPolicy", Iterables.getFirst(sorted, null));
        return model;
    }

    public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
        this.paymentPolicyService = paymentPolicyService;
    }
}
