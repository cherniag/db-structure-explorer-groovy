package mobi.nowtechnologies.applicationtests.features.streamzine.transform;

import cucumber.api.Transformer;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;

public class AccessPolicyTransformer extends Transformer<AccessPolicy> {
    @Override
    public AccessPolicy transform(String value) {
        if("no".equalsIgnoreCase(value.trim())) {
            return null;
        }

        if("vip".equalsIgnoreCase(value.trim())) {
            return AccessPolicy.enabledForVipOnly();
        }

        if("HiddenForSubscribed".equalsIgnoreCase(value.trim())) {
            return AccessPolicy.hiddenForSubscribed();
        }

        throw new IllegalArgumentException("Can not transform " + value + " to " + AccessPolicy.class);
    }
}