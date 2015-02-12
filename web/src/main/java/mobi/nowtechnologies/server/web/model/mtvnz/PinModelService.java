package mobi.nowtechnologies.server.web.model.mtvnz;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.web.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PinModelService implements ModelService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private PaymentPolicyService paymentPolicyService;

    @Override
    public Map<String, Object> getModel(User user) {
        List<PaymentPolicy> paymentPolicies = paymentPolicyService.findPaymentPolicies(user, ProviderType.VF);
        Object filtered = filter(paymentPolicies);
        return Collections.singletonMap("paymentPolicyDtos", filtered);
    }

    private Set<PaymentPolicyDto> filter(List<PaymentPolicy> paymentPolicies) {
        Set<PaymentPolicyDto> filtered = new TreeSet<>();

        for (PaymentPolicy paymentPolicy : paymentPolicies) {
            if(paymentPolicy.getPeriod().getDuration() != 1) {
                logger.warn("Payment Policy {} contains duration not equal to one", paymentPolicy.getId());
            } else {
                filtered.add(new PaymentPolicyDto(paymentPolicy));
            }
        }

        return filtered;
    }

    public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
        this.paymentPolicyService = paymentPolicyService;
    }
}
