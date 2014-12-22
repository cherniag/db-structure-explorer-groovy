package mobi.nowtechnologies.applicationtests.features.referrals

import mobi.nowtechnologies.server.shared.enums.ProviderType

import java.util.regex.Matcher
import java.util.regex.Pattern

class UserNotation {
    static Pattern pattern = Pattern.compile("(.+)\\((.+)\\)");

    String key
    ProviderType providerType
    String emailRef
    String idRef

    public static UserNotation create(String value) {
        Matcher hrefMatcher = pattern.matcher(value);
        assert hrefMatcher.find();
        String pairs = hrefMatcher.group(2)

        UserNotation n = new UserNotation()
        n.key = hrefMatcher.group(1)

        pairs.split(";").each {
            def pair = it.split(":");

            if("EMAIL".equals(pair[0])) {
                n.emailRef = pair[1]
            } else {
                n.idRef = pair[1]
            }
        }

        return n
    }
}