package mobi.nowtechnologies.server.user.criteria;

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
}
