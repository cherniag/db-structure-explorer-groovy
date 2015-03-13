package mobi.nowtechnologies.server.transport.referrals;

import mobi.nowtechnologies.server.shared.enums.ProviderType;

/**
 * Author: Gennadii Cherniaiev Date: 11/21/2014
 */

public class ReferralDto {

    private ProviderType source;
    private String id;

    public ProviderType getSource() {
        return source;
    }

    public String getId() {
        return id;
    }
}
