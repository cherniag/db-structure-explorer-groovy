package mobi.nowtechnologies.server.user.criteria;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/9/2014
 */
public class IsEligibleForDirectPaymentUserMatcher implements Matcher<User> {

    private PaymentPolicyRepository paymentPolicyRepository;
    private List<String> directPaymentTypes;

    public IsEligibleForDirectPaymentUserMatcher(PaymentPolicyRepository paymentPolicyRepository, List<String> directPaymentTypes) {
        this.paymentPolicyRepository = paymentPolicyRepository;
        this.directPaymentTypes = directPaymentTypes;
    }

    @Override
    public boolean match(User user){
        List<PaymentPolicy> paymentPolicies = paymentPolicyRepository.getPaymentPoliciesWithPaymentType(
                    user.getUserGroup().getCommunity(),
                    user.getProvider(),
                    user.getSegment(),
                    user.getContract(),
                    user.getTariff(),
                    Lists.newArrayList(MediaType.values()),
                    directPaymentTypes
        );
        return paymentPolicies != null && paymentPolicies.size()>0;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("directPaymentTypes", directPaymentTypes)
                .toString();
    }
}
