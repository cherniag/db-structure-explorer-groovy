package mobi.nowtechnologies.applicationtests.features.referrals

import mobi.nowtechnologies.server.persistence.domain.User
import mobi.nowtechnologies.server.persistence.domain.referral.Referral
import mobi.nowtechnologies.server.persistence.domain.referral.ReferralState
import mobi.nowtechnologies.server.shared.enums.ProviderType

import java.util.regex.Matcher
import java.util.regex.Pattern


class ReferralNotation {
    static Pattern pattern = Pattern.compile("(.+)\\((.+):(.+)\\)");

    String key
    ProviderType providerType
    ReferralState state
    String contact

    Referral toReferral(User user) {
        new Referral(
            userId: user.id,
            communityId: user.userGroup.community.id,
            contact: contact,
            providerType: providerType
        )
    }

    public static String generate(ProviderType providerType) {
        def id = System.nanoTime();

        if(providerType == ProviderType.EMAIL) {
            return "e${id}@mail.com".toString();
        }

        if(providerType == ProviderType.FACEBOOK) {
            return "f.${id}".toString();
        }

        if(providerType == ProviderType.GOOGLE_PLUS) {
            return "g.${id}".toString();
        }

        return null;
    }

    public static ReferralNotation create(String value) {
        Matcher hrefMatcher = pattern.matcher(value);
        assert hrefMatcher.find();
        ReferralNotation n = new ReferralNotation()
        n.key = hrefMatcher.group(1)
        n.providerType = ProviderType.valueOf(hrefMatcher.group(2))
        n.state = ReferralState.valueOf(hrefMatcher.group(3))
        n.contact = generate(n.providerType);
        return n
    }
}