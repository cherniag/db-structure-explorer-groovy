package mobi.nowtechnologies.server.web.model.mtvnz;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.web.model.ModelService;

import java.util.Collections;
import java.util.Map;

public class PinModelService implements ModelService {
    private PaymentPolicyService paymentPolicyService;

    @Override
    public Map<String, Object> getModel(User user) {
        Object paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);
        return Collections.singletonMap("paymentPolicyDtos", paymentPolicyDtos);
    }

    public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
        this.paymentPolicyService = paymentPolicyService;
    }
}
