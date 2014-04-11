package mobi.nowtechnologies.server.user.criteria;

import com.google.common.collect.Lists;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
public class AndMatcher<T> implements Matcher<T> {

    private Iterable<Matcher<T>> matchers;

    public AndMatcher(Iterable<Matcher<T>> matchers) {
        this.matchers = matchers;
    }

    public AndMatcher(Matcher<T> ... matchers) {
        this.matchers = Lists.newArrayList(matchers);
    }

    @Override
    public boolean match(T value){
        for (Matcher<T> matcher : matchers) {
            if(!matcher.match(value)){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("matchers", matchers)
                .toString();
    }
}
