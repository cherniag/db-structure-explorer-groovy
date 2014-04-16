package mobi.nowtechnologies.server.user.criteria;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class NotMatcher<T> implements Matcher<T> {

    private Matcher<T> matcher;

    public NotMatcher(Matcher<T> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean match(T value) {
        return !matcher.match(value);
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append(matcher)
                .toString();
    }
}
