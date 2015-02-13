package mobi.nowtechnologies.server.web.model.mtvnz;

import com.google.common.collect.Collections2;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.web.model.PinModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class PinPinModelServiceImpl implements PinModelService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private PaymentPolicyService paymentPolicyService;

    @Override
    public Map<String, Object> getModel(User user) {
        List<PaymentPolicy> all = paymentPolicyService.findPaymentPolicies(user);
        List<PaymentPolicy> filtered = filterWithOneDurationLength(all);
        Collection<PaymentPolicy> vfPsms = Collections2.filter(filtered, new PaymentTypePredicate(PaymentDetails.VF_PSMS_TYPE));
        Collection<PaymentPolicyDto> converted = PaymentPolicyDto.convert(vfPsms);
        Object policies = new TreeSet<>(converted);
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

    public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
        this.paymentPolicyService = paymentPolicyService;
    }
}
