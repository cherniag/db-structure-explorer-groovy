package mobi.nowtechnologies.server.persistence.domain.referral;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Gennadii Cherniaiev
 * Date: 11/21/2014
 */
public enum ReferralState {
    PENDING, // ->
    ACTIVATED, DUPLICATED;

    private static Map<ReferralState, List<ReferralState>> transitions = new HashMap<ReferralState, List<ReferralState>>();

    static {
        transitions.put(PENDING, Lists.newArrayList(ACTIVATED, DUPLICATED));
    }

    public boolean hasNext(ReferralState state) {
        return transitions.get(this) != null && transitions.get(this).contains(state);
    }

    public boolean isPending() {
        return PENDING == this;
    }
}
