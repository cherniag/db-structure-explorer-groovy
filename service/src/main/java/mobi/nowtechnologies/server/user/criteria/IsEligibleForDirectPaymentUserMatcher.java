package mobi.nowtechnologies.server.user.criteria;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.shared.enums.MediaType;

import java.util.List;
import java.util.Set;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/9/2014
 */
public class IsEligibleForDirectPaymentUserMatcher implements Matcher<User> {

    private PaymentPolicyRepository paymentPolicyRepository;
    private Set<String> directPaymentTypes;

    public IsEligibleForDirectPaymentUserMatcher(PaymentPolicyRepository paymentPolicyRepository, Set<String> directPaymentTypes) {
        this.paymentPolicyRepository = paymentPolicyRepository;
        this.directPaymentTypes = directPaymentTypes;
    }

    @Override
    public boolean match(User user){
        if(user == null || user.getUserGroup() == null || user.getUserGroup().getCommunity() == null){
            return false;
        }
        List<PaymentPolicy> paymentPolicies = null;
        paymentPolicies = paymentPolicyRepository.getPaymentPolicies(
                    user.getUserGroup().getCommunity(),
                    user.getProvider(),
                    user.getSegment(),
                    user.getContract(),
                    user.getTariff(),
                    Lists.newArrayList(MediaType.values())
        );
        if(paymentPolicies == null){
            return false;
        }
        for (PaymentPolicy paymentPolicy : paymentPolicies) {
            if (directPaymentTypes.contains(paymentPolicy.getPaymentType())){
                return true;
            }
        }
        return false;
    }
}
